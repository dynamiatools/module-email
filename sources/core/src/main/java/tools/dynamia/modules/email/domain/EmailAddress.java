package tools.dynamia.modules.email.domain;

import tools.dynamia.modules.saas.jpa.BaseEntitySaaS;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "email_addresses")
public class EmailAddress extends BaseEntitySaaS {

    @NotNull
    private String email;
    private long sendCount;
    private boolean active = true;
    private boolean blacklisted;
    @Column(name = "addressTag")
    private String tag;

    public EmailAddress() {
    }

    public EmailAddress(@NotNull String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getSendCount() {
        return sendCount;
    }

    public void setSendCount(long sendCount) {
        this.sendCount = sendCount;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isBlacklisted() {
        return blacklisted;
    }

    public void setBlacklisted(boolean blacklisted) {
        this.blacklisted = blacklisted;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
