package itqGroupTestApp.core.ropositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import itqGroupTestApp.core.entity.History;
import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long>, JpaSpecificationExecutor<History> {

    List<History> findAllByDocument_Id(Long id);
}
