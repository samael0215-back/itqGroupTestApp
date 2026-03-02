package itqGroupTestApp.core.DTO;

import itqGroupTestApp.core.entity.Action;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HistoryDTO {
    private Long id;
    private DocumentDTO document;
    private Action action;
    private UserDTO initiator;
    private LocalDateTime actionDate;
    private String comment;
}