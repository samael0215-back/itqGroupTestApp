package itqGroupTestApp.core.servises;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import itqGroupTestApp.core.entity.Status;
import itqGroupTestApp.core.entity.User;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackgroundService {

    private final DocumentService documentService;
    private final UserService userService;
    private final ApprovalRegistryService approvalRegistryService;

    @Value("${app.batchSize:50}")
    private int batchSize;

    @Async("submitExecutor")
    public void startSubmitWorker() {
        log.info("Starting SUBMIT-worker");
        User initiator = userService.getUserById(1L);
        while (!Thread.currentThread().isInterrupted()) {
            try {
                List<Long> batchIds = getIdsList(Status.DRAFT, batchSize);
                if (!batchIds.isEmpty()) {
                    documentService.submitDocuments(batchIds, initiator);
                }
            } catch (Exception ex) {
                log.error("Error in SUBMIT-worker: {}", ex.getMessage());
            }
            sleepQuietly(120000);
        }
    }

    @Async("approveExecutor")
    public void startApproveWorker() {
        log.info("Starting APPROVE-worker");
        User initiator = userService.getUserById(1L);
        sleepQuietly(5000);
        while (!Thread.currentThread().isInterrupted()) {
            try {
                List<Long> batchIds = getIdsList(Status.SUBMITTED, batchSize);
                if (!batchIds.isEmpty()) {
                    approvalRegistryService.approveDocuments(batchIds, initiator);
                }
            } catch (Exception ex) {
                log.error("Error in APPROVE-worker: {}", ex.getMessage());
            }
            sleepQuietly(240000);
        }
    }

    private List<Long> getIdsList(Status status, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return documentService.findIdsByStatus(status, pageable);
    }

    private void sleepQuietly(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}