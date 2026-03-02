package itqGroupTestApp.core.servises;

import itqGroupTestApp.core.DTO.DocumentDTO;
import itqGroupTestApp.core.entity.*;
import itqGroupTestApp.core.mapper.DocumentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import itqGroupTestApp.core.DTO.DocumentHistoryDTO;
import itqGroupTestApp.core.mapper.HistoryMapper;
import itqGroupTestApp.core.ropositories.DocumentRepository;
import itqGroupTestApp.core.utilityClasses.ServiceException;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Slf4j
@Service
public class DocumentService implements Serializable {

    private final DocumentRepository documentRepository;
    private final HistoryService historyService;
    private final ApprovalRegistryService approvalRegistryService;
    private final DocumentMapper documentMapper;
    private final HistoryMapper historyMapper;

    @Autowired
    public DocumentService(DocumentRepository documentRepository,
                           HistoryService historyService,
                           @Lazy ApprovalRegistryService approvalRegistryService,
                           DocumentMapper documentMapper, HistoryMapper historyMapper) {
        this.documentRepository = documentRepository;
        this.historyService = historyService;
        this.approvalRegistryService = approvalRegistryService;
        this.documentMapper = documentMapper;
        this.historyMapper = historyMapper;
    }

    @Transactional
    public DocumentDTO createDocument(String title, User author) {
        if (title == null || title.trim().isEmpty()) {
            throw new ServiceException(ErrorCode.VALIDATION_ERROR);
        }
        try {
            long startTime = System.currentTimeMillis();
            Document doc = new Document();
            doc.setTitle(title);
            doc.setAuthor(author);
            doc.setStatus(Status.DRAFT);
            doc = documentRepository.save(doc);
            long endTime = System.currentTimeMillis();
            log.info("Document save successful, duration: {} ms", (endTime - startTime));
            return documentMapper.toDto(doc);
        } catch (Exception ex) {
            log.error("Error saving document: {}", ex.getMessage(), ex);
            throw new ServiceException(ErrorCode.INVALID_OPERATION, ex);
        }
    }

    @Transactional
    public DocumentHistoryDTO getDocumentWithHistory(Long id) {
        if (id == null) {
            throw new ServiceException(ErrorCode.VALIDATION_ERROR);
        }
        Document document = documentRepository.findById(id).orElseThrow(() -> {
            log.error("Document not found for id: {}", id);
            return new ServiceException(ErrorCode.DOCUMENT_NOT_FOUND);
        });
        return new DocumentHistoryDTO(documentMapper.toDto(document),
                historyService.getHistoryByDocumentId(id).stream().map(historyMapper::toDto).toList());
    }

    public Page<DocumentDTO> getDocumentsByIds(List<Long> ids, Pageable pageable) {
        if (ids == null || ids.isEmpty()) {
            throw new ServiceException(ErrorCode.VALIDATION_ERROR);
        }
        return documentRepository.findDocumentsByIds(ids, pageable).map(documentMapper::toDto);
    }

    public Page<DocumentDTO> getAllDocuments(Pageable pageable) {
        return documentRepository.findAll(pageable).map(documentMapper::toDto);
    }

    @Transactional
    public Map<Long, ActionResult> submitDocuments(List<Long> ids, User initiator) {
        if (ids == null || ids.isEmpty()) {
            throw new ServiceException(ErrorCode.VALIDATION_ERROR);
        }
        int totalDocuments = ids.size();
        log.info("Starting submit documents for {} documents", totalDocuments);
        Map<Long, ActionResult> result = new HashMap<>();
        long overallStartTime = System.currentTimeMillis();

        for (int index = 0; index < totalDocuments; index++) {
            Long id = ids.get(index);
            try {
                log.info("Progress: {} of {} documents submitted", index + 1, totalDocuments);
                Document document = documentRepository.findById(id).orElseThrow(() -> {
                    log.error("Document not found for id: {}", id);
                    return new ServiceException(ErrorCode.DOCUMENT_NOT_FOUND);
                });

                if (document.getStatus() == Status.DRAFT) {
                    document.setStatus(Status.SUBMITTED);
                    documentRepository.save(document);
                    historyService.createHistory(document, initiator, Action.SUBMIT);
                    result.put(id, ActionResult.SUCCESS);
                } else {
                    result.put(id, ActionResult.CONFLICT);
                    throw new ServiceException(ErrorCode.INVALID_OPERATION);
                }
            } catch (ServiceException e) {
                result.put(id, ActionResult.NOT_FOUND);
            } catch (Exception ex) {
                log.error("Error submitting document with ID: {}: {}", id, ex.getMessage());
            }
        }
        long overallEndTime = System.currentTimeMillis();
        log.info("Submit documents ended, duration: {} ms", overallEndTime - overallStartTime);
        return result;
    }

    @Transactional
    public Document getDocumentById(Long id) {
        if (id == null) {
            throw new ServiceException(ErrorCode.VALIDATION_ERROR);
        }
        return documentRepository.findByIdForUpdate(id).orElseThrow(() -> {
            log.error("Document not found for id: {}", id);
            return new ServiceException(ErrorCode.DOCUMENT_NOT_FOUND);
        });
    }

    public List<DocumentDTO> searchDocuments(Status status, Long authorId, LocalDate startDate, LocalDate endDate) {
        if (status == null) {
            throw new ServiceException(ErrorCode.VALIDATION_ERROR);
        }
        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (endDate != null) ? endDate.plusDays(1).atStartOfDay() : null;
        return documentRepository.findAllByFilters(status, authorId, startDateTime, endDateTime).stream()
                .map(documentMapper::toDto).toList();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ActionResult approveOne(Long documentId, User initiator) {
        if (documentId == null) {
            throw new ServiceException(ErrorCode.VALIDATION_ERROR);
        }
        try {
            Optional<Document> opt = documentRepository.findByIdForUpdate(documentId);
            if (opt.isEmpty()) {
                return ActionResult.NOT_FOUND;
            }
            Document doc = opt.get();

            if (doc.getStatus() == Status.SUBMITTED) {
                doc.setStatus(Status.APPROVED);
                documentRepository.save(doc);

                historyService.createHistory(doc, initiator, Action.APPROVE);
                approvalRegistryService.createRecord(doc, initiator);
                return ActionResult.SUCCESS;
            } else {
                return ActionResult.CONFLICT;
            }
        } catch (ServiceException e) {
            return ActionResult.REGISTRATION_FAILED;
        } catch (Exception ex) {
            log.error("Error approving document with ID: {}: {}", documentId, ex.getMessage());
        }
        return ActionResult.SUCCESS;
    }

    public List<Long> findIdsByStatus(Status status, Pageable pageable) {
        if (status == null) {
            throw new ServiceException(ErrorCode.VALIDATION_ERROR);
        }
            return documentRepository.findIdsByStatus(status, pageable);
    }
}
