package sn.zahra.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class UserEntityCriteriaTest {

    @Test
    void newUserEntityCriteriaHasAllFiltersNullTest() {
        var userEntityCriteria = new UserEntityCriteria();
        assertThat(userEntityCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void userEntityCriteriaFluentMethodsCreatesFiltersTest() {
        var userEntityCriteria = new UserEntityCriteria();

        setAllFilters(userEntityCriteria);

        assertThat(userEntityCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void userEntityCriteriaCopyCreatesNullFilterTest() {
        var userEntityCriteria = new UserEntityCriteria();
        var copy = userEntityCriteria.copy();

        assertThat(userEntityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(userEntityCriteria)
        );
    }

    @Test
    void userEntityCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var userEntityCriteria = new UserEntityCriteria();
        setAllFilters(userEntityCriteria);

        var copy = userEntityCriteria.copy();

        assertThat(userEntityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(userEntityCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var userEntityCriteria = new UserEntityCriteria();

        assertThat(userEntityCriteria).hasToString("UserEntityCriteria{}");
    }

    private static void setAllFilters(UserEntityCriteria userEntityCriteria) {
        userEntityCriteria.id();
        userEntityCriteria.createdAt();
        userEntityCriteria.updatedAt();
        userEntityCriteria.deleted();
        userEntityCriteria.deletedAt();
        userEntityCriteria.nom();
        userEntityCriteria.prenom();
        userEntityCriteria.email();
        userEntityCriteria.password();
        userEntityCriteria.role();
        userEntityCriteria.distinct();
    }

    private static Condition<UserEntityCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getDeleted()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getNom()) &&
                condition.apply(criteria.getPrenom()) &&
                condition.apply(criteria.getEmail()) &&
                condition.apply(criteria.getPassword()) &&
                condition.apply(criteria.getRole()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<UserEntityCriteria> copyFiltersAre(UserEntityCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getDeleted(), copy.getDeleted()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getNom(), copy.getNom()) &&
                condition.apply(criteria.getPrenom(), copy.getPrenom()) &&
                condition.apply(criteria.getEmail(), copy.getEmail()) &&
                condition.apply(criteria.getPassword(), copy.getPassword()) &&
                condition.apply(criteria.getRole(), copy.getRole()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
