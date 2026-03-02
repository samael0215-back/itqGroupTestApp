package itqGroupTestApp.core.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;


import itqGroupTestApp.core.entity.ActionResult;
import itqGroupTestApp.core.servises.ApprovalRegistryService;
import itqGroupTestApp.core.servises.UserService;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/approve")
public class ApprovalRegistryController {

    private final UserService userService;
    private final ApprovalRegistryService approvalRegistryService;

    @Autowired
    public ApprovalRegistryController(UserService userService,
                                      ApprovalRegistryService approvalRegistryService) {
        this.userService = userService;
        this.approvalRegistryService = approvalRegistryService;
    }

    @PostMapping()
    @Operation(description = "Утвердить документы")
    public ResponseEntity<Map<Long, ActionResult>> approveDocuments(
            @Parameter(description = "Список ID документов") @RequestParam List<Long> ids,
            @Parameter(description = "ID инициатора утверждения", example = "1") @RequestParam Long iniciatorId
    ) {
        log.info("Approving documents with IDs: {}", ids);
        return ResponseEntity.ok(approvalRegistryService.approveDocuments(ids, userService.getUserById(iniciatorId)));
    }

    @PostMapping("/concurrent-approve")
    @Operation(description = "Проверка конкурентного утверждения")
    public ResponseEntity<Map<String, Object>> concurrentApprove(
            @Parameter(description = "ID документа") @RequestParam Long documentId,
            @Parameter(description = "Количество потоков") @RequestParam int threads,
            @Parameter(description = "Количество попыток на поток") @RequestParam int attempts) {

        log.info("Starting concurrent approval for document ID: {} with threads: {} and attempts: {}", documentId, threads, attempts);
        return ResponseEntity.ok(approvalRegistryService.executeConcurrentApprovals(documentId, threads, attempts));
    }
}