package rhpay.monolith.rest;

import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import rhpay.monolith.entity.PointEntity;
import rhpay.monolith.entity.ShopperEntity;
import rhpay.monolith.entity.StoreEntity;
import rhpay.monolith.entity.WalletEntity;
import rhpay.monolith.repository.spring.*;

@RestController
public class InitializeResource {

    StoreEntityRepository storeSpringRepository;
    TokenEntityRepository tokenSpringRepository;
    PaymentEntityRepository paymentSpringRepository;
    WalletEntityRepository walletSpringRepository;
    PointEntityRepository pointSpringRepository;
    ShopperEntityRepository shopperSpringRepository;

    public InitializeResource(StoreEntityRepository storeSpringRepository, TokenEntityRepository tokenSpringRepository, PaymentEntityRepository paymentSpringRepository, WalletEntityRepository walletSpringRepository, PointEntityRepository pointSpringRepository, ShopperEntityRepository shopperSpringRepository) {
        this.storeSpringRepository = storeSpringRepository;
        this.tokenSpringRepository = tokenSpringRepository;
        this.paymentSpringRepository = paymentSpringRepository;
        this.walletSpringRepository = walletSpringRepository;
        this.pointSpringRepository = pointSpringRepository;
        this.shopperSpringRepository = shopperSpringRepository;
    }

    @GetMapping(value = "/init/{userNum}/{amount}/{autoCharge}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public String initialize(@PathVariable("userNum") final int userNum, @PathVariable("amount") final int amount, @PathVariable("autoCharge") final int autoCharge) {
        storeSpringRepository.deleteAll();

        paymentSpringRepository.deleteAll();
        tokenSpringRepository.deleteAll();

        walletSpringRepository.deleteAll();
        pointSpringRepository.deleteAll();
        shopperSpringRepository.deleteAll();

        for (int i = 0; i < userNum; i++) {
            shopperSpringRepository.save(new ShopperEntity(i, "User" + i));
            walletSpringRepository.save(new WalletEntity(i, amount, autoCharge));
            pointSpringRepository.save(new PointEntity(i, 20));
        }
        for (int i = 0; i < 10; i++) {
            storeSpringRepository.save(new StoreEntity(i, "Shop" + i));
        }
        return "初期化完了";
    }
}
