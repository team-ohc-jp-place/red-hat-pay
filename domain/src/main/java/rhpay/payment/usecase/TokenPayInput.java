package rhpay.payment.usecase;

import rhpay.payment.domain.Money;
import rhpay.payment.domain.ShopperId;
import rhpay.payment.domain.StoreId;
import rhpay.payment.domain.TokenId;

public class TokenPayInput {

    public TokenPayInput(ShopperId shopperId, Money amount, TokenId tokenId, StoreId storeId) {
        this.shopperId = shopperId;
        this.amount = amount;
        this.tokenId = tokenId;
        this.storeId = storeId;
    }

    public final ShopperId shopperId;
    public final Money amount;
    public final TokenId tokenId;
    public final StoreId storeId;
}
