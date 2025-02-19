package sn.zahra.service.mapper;

import org.mapstruct.*;
import sn.zahra.domain.FileEntity;
import sn.zahra.service.dto.FileEntityDTO;

/**
 * Mapper for the entity {@link FileEntity} and its DTO {@link FileEntityDTO}.
 */
@Mapper(componentModel = "spring")
public interface FileEntityMapper extends EntityMapper<FileEntityDTO, FileEntity> {}
