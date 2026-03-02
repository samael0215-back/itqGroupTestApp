package itqGroupTestApp.core.mapper;

import org.mapstruct.Mapper;

import itqGroupTestApp.core.DTO.UserDTO;
import itqGroupTestApp.core.entity.User;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "position", target = "position")
    UserDTO toDto(User entity);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "position", target = "position")
    User toEntity(UserDTO dto);
}
