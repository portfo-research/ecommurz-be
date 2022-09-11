package com.github.portforesearch.ecommurzbe.module.user.mapper;

import com.github.portforesearch.ecommurzbe.module.user.dto.UserRequestDto;
import com.github.portforesearch.ecommurzbe.module.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "createdDate")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "updatedDate")
    @Mapping(ignore = true, target = "recordStatusId")
    @Mapping(ignore = true, target = "roles")
    @Mapping(ignore = true, target = "sellerId")
    @Mapping(ignore = true, target = "customerId")
    @Mapping(ignore = true, target = "customer")
    User loginRequestDtoToUser(UserRequestDto userRequestDto);


}
