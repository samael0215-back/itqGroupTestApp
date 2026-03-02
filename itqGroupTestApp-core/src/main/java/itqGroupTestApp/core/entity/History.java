package itqGroupTestApp.core.entity;

import jakarta.persistence.*;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "document_unique_number", referencedColumnName = "unique_number")
    private Document document;

    @Enumerated(EnumType.STRING)
    private Action action;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @Column
    @NotNull
    private LocalDateTime actionDate;

    @Column
    private String comment;

    @PrePersist
    private void prePersist() {
        this.actionDate = LocalDateTime.now();
    }

}
