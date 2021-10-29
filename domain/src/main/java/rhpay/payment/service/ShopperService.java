package rhpay.payment.service;

import rhpay.payment.domain.Shopper;
import rhpay.payment.domain.ShopperId;
import rhpay.payment.repository.ShopperRepository;

public class ShopperService {

    private final ShopperRepository userRepository;

    public ShopperService(ShopperRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Shopper load(final ShopperId id) {
        return userRepository.load(id);
    }
}