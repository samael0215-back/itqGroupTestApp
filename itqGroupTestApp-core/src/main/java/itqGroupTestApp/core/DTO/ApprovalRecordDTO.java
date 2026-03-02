package itqGroupTestApp.core.DTO;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ApprovalRecordDTO {
    private Long id;
    private DocumentDTO document;
    private UserDTO approver;
    private LocalDateTime approvalDate;
}
