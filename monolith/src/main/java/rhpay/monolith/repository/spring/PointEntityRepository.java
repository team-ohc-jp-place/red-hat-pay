package rhpay.monolith.repository.spring;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rhpay.monolith.entity.PointEntity;

public interface PointEntityRepository extends JpaRepository<PointEntity, Integer> {

    @Modifying
    @Query("UPDATE PointEntity p SET p.amount = :amount WHERE p.ownerId = :shopperId")
    int updatePoint(@Param("shopperId") Integer shopperId, @Param("amount") int amount);
}
