package rhpay.monolith.repository.spring;

import rhpay.monolith.entity.WalletEntity;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

public class WalletEntityRepositoryImpl implements WalletEntityRepositoryCustom{

    EntityManager entityManager;

    public WalletEntityRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public WalletEntity findByIdWithLock(Integer id) {
        return entityManager.find(WalletEntity.class, id, LockModeType.PESSIMISTIC_WRITE);
    }
}
