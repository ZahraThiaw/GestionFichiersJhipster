package sn.zahra.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class FileEntityCriteriaTest {

    @Test
    void newFileEntityCriteriaHasAllFiltersNullTest() {
        var fileEntityCriteria = new FileEntityCriteria();
        assertThat(fileEntityCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void fileEntityCriteriaFluentMethodsCreatesFiltersTest() {
        var fileEntityCriteria = new FileEntityCriteria();

        setAllFilters(fileEntityCriteria);

        assertThat(fileEntityCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void fileEntityCriteriaCopyCreatesNullFilterTest() {
        var fileEntityCriteria = new FileEntityCriteria();
        var copy = fileEntityCriteria.copy();

        assertThat(fileEntityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(fileEntityCriteria)
        );
    }

    @Test
    void fileEntityCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var fileEntityCriteria = new FileEntityCriteria();
        setAllFilters(fileEntityCriteria);

        var copy = fileEntityCriteria.copy();

        assertThat(fileEntityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(fileEntityCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var fileEntityCriteria = new FileEntityCriteria();

        assertThat(fileEntityCriteria).hasToString("FileEntityCriteria{}");
    }

    private static void setAllFilters(FileEntityCriteria fileEntityCriteria) {
        fileEntityCriteria.id();
        fileEntityCriteria.fileName();
        fileEntityCriteria.originalFileName();
        fileEntityCriteria.contentType();
        fileEntityCriteria.fileSize();
        fileEntityCriteria.filePath();
        fileEntityCriteria.storageType();
        fileEntityCriteria.createdAt();
        fileEntityCriteria.updatedAt();
        fileEntityCriteria.deleted();
        fileEntityCriteria.deletedAt();
        fileEntityCriteria.distinct();
    }

    private static Condition<FileEntityCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getFileName()) &&
                condition.apply(criteria.getOriginalFileName()) &&
                condition.apply(criteria.getContentType()) &&
                condition.apply(criteria.getFileSize()) &&
                condition.apply(criteria.getFilePath()) &&
                condition.apply(criteria.getStorageType()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getDeleted()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<FileEntityCriteria> copyFiltersAre(FileEntityCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getFileName(), copy.getFileName()) &&
                condition.apply(criteria.getOriginalFileName(), copy.getOriginalFileName()) &&
                condition.apply(criteria.getContentType(), copy.getContentType()) &&
                condition.apply(criteria.getFileSize(), copy.getFileSize()) &&
                condition.apply(criteria.getFilePath(), copy.getFilePath()) &&
                condition.apply(criteria.getStorageType(), copy.getStorageType()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getDeleted(), copy.getDeleted()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
