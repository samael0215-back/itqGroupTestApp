package itqGroupTestApp.core.ropositories;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import itqGroupTestApp.core.entity.Document;
import itqGroupTestApp.core.entity.Status;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {

    @Query("SELECT d FROM Document d WHERE d.id IN :ids")
    Page<Document> findDocumentsByIds(@Param("ids") List<Long> ids, Pageable pageable);

    @Query("SELECT d FROM Document d WHERE (:status IS NULL OR d.status = :status) " +
           "AND (:authorId IS NULL OR d.author.id = :authorId) " +
           "AND (cast (:startDate as date ) IS NULL OR d.updatedDate >= :startDate) " +
           "AND (cast(:endDate as date ) IS NULL OR d.updatedDate <= :endDate)")
    List<Document> findAllByFilters(@Param("status") Status status,
                                     @Param("authorId") Long authorId,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Document d WHERE d.id = :id")
    Optional<Document> findByIdForUpdate(@Param("id") Long id);

    @Query("SELECT d.id FROM Document d WHERE d.status = :status")
    List<Long> findIdsByStatus(@Param("status") Status status, Pageable pageable);
}
