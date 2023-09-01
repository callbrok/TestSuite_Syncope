package org.apache.syncope.core.spring.security.util;

import org.apache.syncope.common.lib.policy.DefaultPasswordRuleConf;
import org.apache.syncope.common.lib.policy.PasswordRuleConf;
import org.apache.syncope.core.persistence.api.entity.user.LinkedAccount;
import org.apache.syncope.core.persistence.api.entity.user.User;
import org.apache.syncope.core.provisioning.api.rules.PasswordRule;
import org.apache.syncope.core.provisioning.api.rules.PasswordRuleConfClass;


@PasswordRuleConfClass(DefaultPasswordRuleConf.class)
public class PasswordRuleImpl implements PasswordRule {

    private DefaultPasswordRuleConf conf;

    @Override
    public PasswordRuleConf getConf() {
        return conf;
    }

    @Override
    public void setConf(final PasswordRuleConf conf) {
        if (conf instanceof DefaultPasswordRuleConf) {
            this.conf = (DefaultPasswordRuleConf) conf;
        } else {
            throw new IllegalArgumentException(
                    DefaultPasswordRuleConf.class.getName() + " expected, got " + conf.getClass().getName());
        }
    }

    @Override
    public void enforce(String username, String clearPassword) {}

    @Override
    public void enforce(User user, String clearPassword) {}

    @Override
    public void enforce(LinkedAccount account) {}
}
