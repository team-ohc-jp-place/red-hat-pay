package rhpay.monolith.repository;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import rhpay.monolith.entity.TokenEntity;
import rhpay.monolith.entity.TokenKey;
import rhpay.monolith.repository.spring.TokenEntityRepository;
import rhpay.payment.domain.ShopperId;
import rhpay.payment.domain.Token;
import rhpay.payment.domain.TokenId;
import rhpay.payment.domain.TokenStatus;
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
    public Token processing(Token token) {
        int rowNum = tokenSpringRepository.updateStatus(token.getShopperId().value, token.getTokenId().value, token.getStatus().value, TokenStatus.PROCESSING.value);
        if (rowNum == 0) {
            throw new RuntimeException(String.format("This token is not exist on Data Store: %s", token));
        } else if (rowNum == 1) {
            Token newToken = new Token(token.getShopperId(), token.getTokenId(), rhpay.payment.domain.TokenStatus.PROCESSING);
            return newToken;
        }
        throw new RuntimeException(String.format("Many token were changed : %s", token));
    }

    @Override
    public Token used(Token token) {
        int rowNum = tokenSpringRepository.updateStatus(token.getShopperId().value, token.getTokenId().value, token.getStatus().value, TokenStatus.USED.value);
        if (rowNum == 0) {
            throw new RuntimeException(String.format("This token is not exist on Data Store: %s", token));
        } else if (rowNum == 1) {
            Token newToken = new Token(token.getShopperId(), token.getTokenId(), rhpay.payment.domain.TokenStatus.USED);
            return newToken;
        }
        throw new RuntimeException(String.format("Many token were changed : %s", token));
    }

    @Override
    public Token failed(Token token) {
        int rowNum = tokenSpringRepository.updateStatus(token.getShopperId().value, token.getTokenId().value, token.getStatus().value, TokenStatus.FAILED.value);
        if (rowNum == 0) {
            throw new RuntimeException(String.format("This token is not exist on Data Store: %s", token));
        } else if (rowNum == 1) {
            Token newToken = new Token(token.getShopperId(), token.getTokenId(), rhpay.payment.domain.TokenStatus.FAILED);
            return newToken;
        }
        throw new RuntimeException(String.format("Many token were changed : %s", token));
    }
}
