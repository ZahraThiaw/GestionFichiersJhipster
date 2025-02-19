package sn.zahra.service.mapper;

import static sn.zahra.domain.FileEntityAsserts.*;
import static sn.zahra.domain.FileEntityTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FileEntityMapperTest {

    private FileEntityMapper fileEntityMapper;

    @BeforeEach
    void setUp() {
        fileEntityMapper = new FileEntityMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getFileEntitySample1();
        var actual = fileEntityMapper.toEntity(fileEntityMapper.toDto(expected));
        assertFileEntityAllPropertiesEquals(expected, actual);
    }
}
