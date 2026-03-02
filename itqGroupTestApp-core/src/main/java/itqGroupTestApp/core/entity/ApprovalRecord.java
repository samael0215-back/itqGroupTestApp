package itqGroupTestApp.core.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Data
@Getter
@Setter
@Entity
public class ApprovalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "document_unique_number_ar", referencedColumnName = "unique_number")
    private Document document;

    @ManyToOne
    @JoinColumn(name = "author_id_ar")
    private User approver;

    @Column
    @NotNull
    private LocalDateTime approvalDate;

    @PrePersist
    private void prePersist() {
        this.approvalDate = LocalDateTime.now();
    }
}