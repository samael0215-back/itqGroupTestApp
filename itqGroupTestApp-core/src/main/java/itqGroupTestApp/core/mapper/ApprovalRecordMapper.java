package itqGroupTestApp.core.mapper;

import org.mapstruct.Mapper;

import itqGroupTestApp.core.DTO.ApprovalRecordDTO;
import itqGroupTestApp.core.entity.ApprovalRecord;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { UserMapper.class, DocumentMapper.class })
public interface ApprovalRecordMapper {

    @Mapping(source = "document", target = "document")
    @Mapping(source = "approver", target = "approver")
    ApprovalRecordDTO toDto(ApprovalRecord entity);

    @Mapping(source = "document", target = "document")
    @Mapping(source = "approver", target = "approver")
    ApprovalRecord toEntity(ApprovalRecordDTO dto);
}
