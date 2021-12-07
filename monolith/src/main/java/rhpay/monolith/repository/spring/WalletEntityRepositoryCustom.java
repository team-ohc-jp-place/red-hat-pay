package rhpay.monolith.repository.spring;

import rhpay.monolith.entity.WalletEntity;

public interface WalletEntityRepositoryCustom {
    WalletEntity findByIdWithLock(Integer id);
}
