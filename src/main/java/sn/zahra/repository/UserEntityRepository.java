package sn.zahra.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.zahra.domain.UserEntity;

/**
 * Spring Data JPA repository for the UserEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {}
