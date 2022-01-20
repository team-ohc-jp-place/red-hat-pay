package rhpay.monolith.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Token")
@Cacheable(value = false)
public class TokenEntity implements Serializable {

    @EmbeddedId
    private TokenKey id;

    private int status;

    public TokenEntity() {
    }

    public TokenEntity(TokenKey id, int status) {
        this.id = id;
        this.status = status;
    }

    public void setId(TokenKey id) {
        this.id = id;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public TokenKey getId() {
        return id;
    }

    public int getStatus() {
        return status;
    }
}
