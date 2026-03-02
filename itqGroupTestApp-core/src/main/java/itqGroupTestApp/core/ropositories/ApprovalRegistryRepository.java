package itqGroupTestApp.core.ropositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import itqGroupTestApp.core.entity.ApprovalRecord;

@Repository
public interface ApprovalRegistryRepository extends JpaRepository<ApprovalRecord, Long>, JpaSpecificationExecutor<ApprovalRecord>{
}
