package rhpay.monolith.repository;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import rhpay.monolith.entity.TokenEntity;
import rhpay.monolith.entity.TokenKey;
import rhpay.monolith.repository.spring.TokenEntityRepository;
import rhpay.payment.domain.*;
import rhpay.payment.repository.TokenRepository;

@RequestScope
@Component
public class RdbmsTokenRepository implements TokenRepository {

    TokenEntityRepository tokenSpringRepository;

    public RdbmsTokenRepository(TokenEntityRepository tokenSpringRepository) {
        this.tokenSpringRepository = tokenSpringRepository;
    }

    @Override
    public void create(Token token) {
        TokenEntity entity = new TokenEntity(new TokenKey(token.getShopperId().value, token.getTokenId().value), token.getStatus().value);
        tokenSpringRepository.save(entity);
    }

    @Override
    public Token load(ShopperId shopperId, TokenId tokenId) {
        TokenKey key = new TokenKey(shopperId.value, tokenId.value);
        TokenEntity entity = tokenSpringRepository.findByIdWithLock(key);
        return new Token(shopperId, tokenId, TokenStatus.getInstance(entity.getStatus()));
    }

    @Override
    public void store(Token token) throws TokenException {
        int rowNum = tokenSpringRepository.updateStatus(token.getShopperId().value, token.getTokenId().value, token.getStatus().value);
        if (rowNum == 0) {
            throw new RuntimeException(String.format("This token is not exist on Data Store: %s", token));
        } else if (rowNum == 1) {
            return;
        }
        throw new RuntimeException(String.format("Many token were changed : %s", token));
    }
}
