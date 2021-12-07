package rhpay.monolith.repository.spring;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rhpay.monolith.entity.TokenEntity;
import rhpay.monolith.entity.TokenKey;

public interface TokenEntityRepository extends JpaRepository<TokenEntity, TokenKey>, TokenEntityRepositoryCustom {

    @Modifying
    @Query("UPDATE TokenEntity t SET t.status = :newStatus WHERE t.id.ownerId = :shopperId AND t.id.tokenId = :tokenId AND t.status = :currentStatus")
    int updateStatus(@Param("shopperId") Integer shopperId, @Param("tokenId") String tokenId, @Param("currentStatus") int currentStatus, @Param("newStatus") int newStatus);
}
