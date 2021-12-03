package rhpay.monolith.repository.spring;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import rhpay.monolith.entity.TokenEntity;
import rhpay.monolith.entity.TokenKey;
import rhpay.monolith.entity.TokenStatus;

public interface TokenSpringRepository extends CrudRepository<TokenEntity, TokenKey> {

    @Modifying
    @Query("UPDATE TOKEN SET status = :newStatus WHERE shopper_id = :shopperId AND token_id = :tokenId AND status = :currentStatus")
    int updateStatus(@Param("shopperId") Integer shopperId, @Param("tokenId") String tokenId, @Param("currentStatus") TokenStatus currentStatus, @Param("newStatus") TokenStatus newStatus);
}
