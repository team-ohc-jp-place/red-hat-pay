package rhpay.monolith.repository;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import rhpay.monolith.entity.TokenEntity;
import rhpay.monolith.entity.TokenKey;
import rhpay.monolith.entity.TokenStatus;
import rhpay.monolith.repository.spring.TokenSpringRepository;
import rhpay.payment.domain.*;
import rhpay.payment.repository.TokenRepository;

@RequestScope
@Component
public class RdbmsTokenRepository implements TokenRepository {

    TokenSpringRepository tokenSpringRepository;

    public RdbmsTokenRepository(TokenSpringRepository tokenSpringRepository) {
        this.tokenSpringRepository = tokenSpringRepository;
    }

    @Override
    public void create(Token token) {
        TokenEntity entity = new TokenEntity(token.getShopperId().value, token.getTokenId().value, TokenStatus.fromDomain(token.getStatus()));
        tokenSpringRepository.save(entity);
    }

    @Override
    public Token load(ShopperId shopperId, TokenId tokenId) {
        TokenKey key = new TokenKey(shopperId.value, tokenId.value);
        TokenEntity entity = tokenSpringRepository.findById(key).get();
        return new Token(shopperId, tokenId, entity.getStatus().toDomain());
    }

    @Override
    public Token processing(Token token) {
        int rowNum = tokenSpringRepository.updateStatus(token.getShopperId().value, token.getTokenId().value, TokenStatus.fromDomain(token.getStatus()), TokenStatus.PROCESSING);
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
        int rowNum = tokenSpringRepository.updateStatus(token.getShopperId().value, token.getTokenId().value, TokenStatus.fromDomain(token.getStatus()), TokenStatus.USED);
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
        int rowNum = tokenSpringRepository.updateStatus(token.getShopperId().value, token.getTokenId().value, TokenStatus.fromDomain(token.getStatus()), TokenStatus.FAILED);
        if (rowNum == 0) {
            throw new RuntimeException(String.format("This token is not exist on Data Store: %s", token));
        } else if (rowNum == 1) {
            Token newToken = new Token(token.getShopperId(), token.getTokenId(), rhpay.payment.domain.TokenStatus.FAILED);
            return newToken;
        }
        throw new RuntimeException(String.format("Many token were changed : %s", token));
    }
}
