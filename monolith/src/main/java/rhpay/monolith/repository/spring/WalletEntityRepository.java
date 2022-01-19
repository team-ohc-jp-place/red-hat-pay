package rhpay.monolith.repository.spring;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rhpay.monolith.entity.WalletEntity;

public interface WalletEntityRepository extends JpaRepository<WalletEntity, Integer>, WalletEntityRepositoryCustom {

    @Modifying
    @Query("UPDATE WalletEntity w SET w.chargedMoney = :chargedMoney WHERE w.ownerId = :shopperId")
    int updateChargedMoney(@Param("shopperId") Integer shopperId, @Param("chargedMoney") int chargedMoney);
}
