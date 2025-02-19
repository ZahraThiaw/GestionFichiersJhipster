package sn.zahra.service.mapper;

import org.mapstruct.*;
import sn.zahra.domain.UserEntity;
import sn.zahra.service.dto.UserEntityDTO;

/**
 * Mapper for the entity {@link UserEntity} and its DTO {@link UserEntityDTO}.
 */
@Mapper(componentModel = "spring")
public interface UserEntityMapper extends EntityMapper<UserEntityDTO, UserEntity> {}
