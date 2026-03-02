package itqGroupTestApp.core.mapper;

import org.mapstruct.Mapper;

import itqGroupTestApp.core.DTO.HistoryDTO;
import itqGroupTestApp.core.entity.History;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { UserMapper.class, DocumentMapper.class })
public interface HistoryMapper {
    @Mapping(source = "document", target = "document")
    @Mapping(source = "initiator", target = "initiator")
    HistoryDTO toDto(History entity);

    @Mapping(source = "document", target = "document")
    @Mapping(source = "initiator", target = "initiator")
    History toEntity(HistoryDTO dto);
}
