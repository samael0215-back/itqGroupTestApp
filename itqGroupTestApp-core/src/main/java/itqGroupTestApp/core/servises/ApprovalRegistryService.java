package itqGroupTestApp.core.servises;


import itqGroupTestApp.core.entity.*;
import itqGroupTestApp.core.utilityClasses.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import itqGroupTestApp.core.ropositories.ApprovalRegistryRepository;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@Slf4j
public class ApprovalRegistryService {

    private final DocumentService documentService;
    private final ApprovalRegistryRepository approvalRegistryRepository;

    @Autowired
    public ApprovalRegistryService(DocumentService documentService, ApprovalRegistryRepository approvalRegistryRepository) {
        this.documentService = documentService;
        this.approvalRegistryRepository = approvalRegistryRepository;
    }

    @Transactional
    public Map<Long, ActionResult> approveDocuments(List<Long> ids, User initiator) {
        if (ids == null || ids.isEmpty()) {
            throw new ServiceException(ErrorCode.VALIDATION_ERROR);
        }
        int totalDocuments = ids.size();
        log.info("Starting approve documents for {} documents", totalDocuments);
        Map<Long, ActionResult> result = new HashMap<>();
        long overallStartTime = System.currentTimeMillis();

        for (int index = 0; index < totalDocuments; index++) {
            Long id = ids.get(index);
            log.info("Progress: {} of {} documents approved", index + 1, totalDocuments);
            ActionResult res = documentService.approveOne(id, initiator);
            result.put(id, res);
        }
        long overallEndTime = System.currentTimeMillis();
        log.info("Approve documents ended, duration: {} ms", overallEndTime - overallStartTime);
        return result;

    }

    public void createRecord(Document doc, User initiator) {
        ApprovalRecord record = new ApprovalRecord();
        record.setDocument(doc);
        record.setApprover(initiator);
        approvalRegistryRepository.save(record);
    }

    public Map<String, Object> executeConcurrentApprovals(Long documentId, int threads, int attempts) {
        Map<String, Object> summary = new HashMap<>();
        // идея пиздит - трай с ресурсами падает при компиляции нужно самому закрыть
        ExecutorService executorService = Executors.newFixedThreadPool(threads);

        try {

            List<Future<ActionResult>> futures = new ArrayList<>();

            for (int i = 0; i < threads; i++) {
                futures.add(executorService.submit(() -> {
                    ActionResult result = null;
                    for (int attempt = 0; attempt < attempts; attempt++) {
                        result = documentService.approveOne(documentId, getMockUser());
                        if (result == ActionResult.SUCCESS) {
                            break;
                        }
                    }
                    return result;
                }));
            }

            int successCount = 0;
            int conflictCount = 0;
            int errorCount = 0;

            for (Future<ActionResult> future : futures) {
                try {
                    ActionResult result = future.get();
                    switch (result) {
                        case SUCCESS:
                            successCount++;
                            break;
                        case CONFLICT:
                            conflictCount++;
                            break;
                        case REGISTRATION_FAILED:
                            errorCount++;
                            break;
                    }
                } catch (ExecutionException | InterruptedException e) {
                    log.error("Error with multi-approving document with ID: {}: {}", documentId, e.getMessage());
                }
            }

            Document document = documentService.getDocumentById(documentId);
            summary.put("finalStatus", document.getStatus());
            summary.put("successCount", successCount);
            summary.put("conflictCount", conflictCount);
            summary.put("errorCount", errorCount);
        } finally {
            executorService.shutdown();
        }
        //лучше возвращать ДТО, но дальнейшая судьба этих данных туманна и неоднозначна... )
        return summary;
    }

    // или добавить в контроллер инициатора конкурентной проверки
    private User getMockUser() {
        User user = new User();
        user.setId(1L);
        return user;
    }
}
