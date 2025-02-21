package sn.zahra.service.mapper;

import org.mapstruct.*;
    import sn.zahra.domain.FileEntity;
import sn.zahra.service.dto.FileRequestDTO;

/**
 * Mapper for the entity {@link FileEntity} and its DTO {@link FileRequestDTO}.
 */
@Mapper(componentModel = "spring")
public interface FileRequestMapper extends EntityMapper<FileRequestDTO, FileEntity> {}
