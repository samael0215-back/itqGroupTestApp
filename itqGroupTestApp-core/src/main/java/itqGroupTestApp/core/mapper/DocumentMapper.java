package itqGroupTestApp.core.mapper;

import org.mapstruct.Mapper;

import itqGroupTestApp.core.DTO.DocumentDTO;
import itqGroupTestApp.core.entity.Document;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface DocumentMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "uniqueNumber", target = "uniqueNumber")
    @Mapping(source = "author", target = "author")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "updatedDate", target = "updatedDate")
    DocumentDTO toDto(Document entity);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "uniqueNumber", target = "uniqueNumber")
    @Mapping(source = "author", target = "author")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "updatedDate", target = "updatedDate")
    Document toEntity(DocumentDTO dto);
}
