import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import itqGroupTestApp.core.DTO.DocumentDTO;
import itqGroupTestApp.core.entity.*;
import itqGroupTestApp.core.mapper.DocumentMapper;
import itqGroupTestApp.core.ropositories.DocumentRepository;
import itqGroupTestApp.core.servises.DocumentService;
import itqGroupTestApp.core.servises.HistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

class DocumentServiceTest {

    @InjectMocks
    private DocumentService documentService;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private HistoryService historyService;

    @Mock
    private DocumentMapper documentMapper;

    private User author;
    private DocumentDTO documentDTO;
    private Document document;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        author = new User();
        author.setId(1L);
        author.setUsername("Тестовый юзер");

        documentDTO = new DocumentDTO();
        documentDTO.setId(1L);
        documentDTO.setTitle("Тестовый документ");

        document = new Document();
        document.setId(1L);
        document.setTitle("Тестовый документ");
        document.setStatus(Status.DRAFT);
    }

    @Test
    void shouldCreateDocumentSuccessfully() {
        when(documentRepository.save(any(Document.class))).thenAnswer(invocation -> {
            Document doc = invocation.getArgument(0);
            doc.setId(1L);
            return doc;
        });
        when(documentMapper.toDto(any(Document.class))).thenReturn(documentDTO);

        DocumentDTO result = documentService.createDocument("Тестовый документ", author);

        assertNotNull(result);
        assertEquals(documentDTO.getId(), result.getId());
        assertEquals(documentDTO.getTitle(), result.getTitle());

        verify(documentRepository, times(1)).save(any(Document.class));
        verify(documentMapper, times(1)).toDto(any(Document.class));
    }

    @Test
    void shouldSubmitDocumentsSuccessfully() {
        List<Long> ids = Collections.singletonList(1L);
        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
        when(documentRepository.save(any(Document.class))).thenReturn(document);

        Map<Long, ActionResult> result = documentService.submitDocuments(ids, author);

        assertEquals(1, result.size());
        assertEquals(ActionResult.SUCCESS, result.get(1L));
        assertEquals(Status.SUBMITTED, document.getStatus());

        verify(documentRepository, times(1)).findById(1L);
        verify(documentRepository, times(1)).save(document);
        verify(historyService, times(1)).createHistory(document, author, Action.SUBMIT);
    }
}