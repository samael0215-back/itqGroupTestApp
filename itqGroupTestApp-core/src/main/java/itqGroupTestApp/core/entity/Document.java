package itqGroupTestApp.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unique_number", unique = true)
    private UUID uniqueNumber;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @Column
    @NotBlank
    private String title;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column
    @NotNull
    private LocalDateTime createdDate;

    @Column
    @NotNull
    private LocalDateTime updatedDate;

    @PrePersist
    private void prePersist() {
        this.uniqueNumber = UUID.randomUUID();
        this.createdDate = LocalDateTime.now();
        this.updatedDate = this.createdDate;
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}
