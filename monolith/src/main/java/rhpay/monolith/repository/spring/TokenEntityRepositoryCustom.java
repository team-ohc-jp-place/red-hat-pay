package rhpay.monolith.repository.spring;

import rhpay.monolith.entity.TokenEntity;
import rhpay.monolith.entity.TokenKey;

public interface TokenEntityRepositoryCustom {
    TokenEntity findByIdWithLock(TokenKey id);
}
