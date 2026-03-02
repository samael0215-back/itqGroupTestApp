package itqGroupTestApp.core.controllers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;


import itqGroupTestApp.core.DTO.DocumentDTO;
import itqGroupTestApp.core.DTO.DocumentHistoryDTO;
import itqGroupTestApp.core.entity.ActionResult;

import itqGroupTestApp.core.entity.Status;
import itqGroupTestApp.core.servises.DocumentService;
import itqGroupTestApp.core.servises.UserService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final UserService userService;

    @Autowired
    public DocumentController(DocumentService documentService, UserService userService) {
        this.documentService = documentService;
        this.userService = userService;
    }

    @PostMapping("/new")
    @Operation(description = "Создание нового документа")
    public ResponseEntity<DocumentDTO> createDocument(
            @Parameter(description = "Заголовок документа", example = "Sample doc title") @RequestParam(required = false) String title,
//            @Parameter(description = "Файл документа (например, .doc или .pdf)") @RequestParam(required = true) MultipartFile documentFile,
            @Parameter(description = "Айди автора документа", example = "1") @RequestParam Long author
    ) {
        log.info("Starting document creation");
        return ResponseEntity.ok(documentService.createDocument(title, userService.getUserById(author)));
    }

    @GetMapping("/find")
    @Operation(description = "Получение одного документа по ID вместе с историей")
    public ResponseEntity<DocumentHistoryDTO> getDocumentWithHistory(
            @Parameter(description = "ID документа", example = "1") @RequestParam Long id
    ) {
        log.info("Fetching document with ID: {}", id);
        return ResponseEntity.ok(documentService.getDocumentWithHistory(id));
    }

    @GetMapping()
    @Operation(description = "Получение списка документов по списку ID")
    public ResponseEntity<Page<DocumentDTO>> getDocumentsByIds(
            @Parameter(description = "Список ID документов") @RequestParam (required = false) List<Long> ids,
            @Parameter(description = "Параметры пагинации и сортировки") @PageableDefault Pageable pageable
    ) {
        log.info("Fetching documents with IDs: {} with pagination: {}", ids, pageable);
        Page<DocumentDTO> documents;
        if (ids != null && !ids.isEmpty()) {
            documents = documentService.getDocumentsByIds(ids, pageable);
        } else {
            documents = documentService.getAllDocuments(pageable);
        }
        return ResponseEntity.ok(documents);
    }

    @PostMapping("/submit")
    @Operation(description = "Отправить документы на согласование")
    public ResponseEntity<Map<Long, ActionResult>> submitDocuments(
            @Parameter(description = "Список ID документов") @RequestParam List<Long> ids,
            @Parameter(description = "Айди инициатора согласования пакета документов", example = "1") @RequestParam Long iniciatorId
    ) {
        log.info("Submitting documents with IDs: {}", ids);
        return ResponseEntity.ok(documentService.submitDocuments(ids, userService.getUserById(iniciatorId)));
    }

    @GetMapping("/search")
    @Operation(description = "Поиск документов")
    public ResponseEntity<List<DocumentDTO>> searchDocuments(
            @Parameter(description = "Статус документа") @RequestParam(required = false) Status status,
            @Parameter(description = "ID автора") @RequestParam(required = false) Long authorId,
            @Parameter(description = "Дата начала") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "Дата окончания") @RequestParam(required = false) LocalDate endDate) {

        log.info("Searching documents with status: {}, authorId: {}, date range: {} to {}",
                status, authorId, startDate, endDate);
        //todo создать объект критерия и использовать стандартный метод репы
        return ResponseEntity.ok(documentService.searchDocuments(status, authorId, startDate, endDate));
    }
}

