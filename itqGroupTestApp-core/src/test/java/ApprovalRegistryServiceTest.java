import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import itqGroupTestApp.core.entity.*;
import itqGroupTestApp.core.ropositories.DocumentRepository;
import itqGroupTestApp.core.servises.ApprovalRegistryService;
import itqGroupTestApp.core.servises.DocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ApprovalRegistryServiceTest {

    @InjectMocks
    private ApprovalRegistryService approvalRegistryService;

    @Mock
    private DocumentService documentService;

    @Mock
    private DocumentRepository documentRepository;

    private User author;
    private Document document;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setId(1L);
        author.setUsername("testUser");

        document = new Document();
        document.setId(1L);
        document.setTitle("Test Document");
        document.setStatus(Status.DRAFT);
    }

    @Test
    void shouldApproveDocumentsSuccessfullyWithPartialResults() {
        List<Long> ids = List.of(1L, 2L);

        lenient().when(documentRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(document));
        lenient().when(documentRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(document));

        when(documentService.approveOne(1L, author)).thenReturn(ActionResult.SUCCESS);
        when(documentService.approveOne(2L, author)).thenReturn(ActionResult.SUCCESS);

        Map<Long, ActionResult> result = approvalRegistryService.approveDocuments(ids, author);

        assertEquals(2, result.size());
        assertEquals(ActionResult.SUCCESS, result.get(1L));
        assertEquals(ActionResult.SUCCESS, result.get(2L));


        verify(documentService, times(2)).approveOne(anyLong(), any(User.class));
    }
}