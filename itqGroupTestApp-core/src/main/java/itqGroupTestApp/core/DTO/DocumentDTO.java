package itqGroupTestApp.core.DTO;

import itqGroupTestApp.core.entity.Status;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class DocumentDTO {
    private Long id;
    private UUID uniqueNumber;
    private UserDTO author;
    private String title;
    private Status status;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}