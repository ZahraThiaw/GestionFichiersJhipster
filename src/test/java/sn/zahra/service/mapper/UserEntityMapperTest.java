package sn.zahra.service.mapper;

import static sn.zahra.domain.UserEntityAsserts.*;
import static sn.zahra.domain.UserEntityTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserEntityMapperTest {

    private UserEntityMapper userEntityMapper;

    @BeforeEach
    void setUp() {
        userEntityMapper = new UserEntityMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getUserEntitySample1();
        var actual = userEntityMapper.toEntity(userEntityMapper.toDto(expected));
        assertUserEntityAllPropertiesEquals(expected, actual);
    }
}
