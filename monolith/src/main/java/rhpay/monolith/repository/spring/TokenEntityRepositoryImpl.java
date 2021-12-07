package rhpay.monolith.repository.spring;

import rhpay.monolith.entity.TokenEntity;
import rhpay.monolith.entity.TokenKey;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

public class TokenEntityRepositoryImpl implements TokenEntityRepositoryCustom {

    EntityManager entityManager;

    public TokenEntityRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public TokenEntity findByIdWithLock(TokenKey id) {
        return entityManager.find(TokenEntity.class, id, LockModeType.PESSIMISTIC_WRITE);
    }
}
