package org.apache.syncope.core.spring.security.util;

import org.apache.syncope.common.lib.policy.AccountRuleConf;
import org.apache.syncope.common.lib.policy.PasswordRuleConf;
import org.apache.syncope.common.lib.policy.PullCorrelationRuleConf;
import org.apache.syncope.common.lib.policy.PushCorrelationRuleConf;
import org.apache.syncope.common.lib.report.ReportConf;
import org.apache.syncope.core.provisioning.api.ImplementationLookup;
import org.apache.syncope.core.provisioning.api.job.report.ReportJobDelegate;
import org.apache.syncope.core.provisioning.api.rules.AccountRule;
import org.apache.syncope.core.provisioning.api.rules.PasswordRule;
import org.apache.syncope.core.provisioning.api.rules.PullCorrelationRule;
import org.apache.syncope.core.provisioning.api.rules.PushCorrelationRule;

import java.util.Set;

public class ImplementationLookupImpl implements ImplementationLookup {
    @Override
    public Set<String> getClassNames(String type) {
        return Set.of();
    }

    @Override
    public Set<Class<?>> getJWTSSOProviderClasses() {
        return Set.of();
    }

    @Override
    public Class<? extends ReportJobDelegate> getReportClass(Class<? extends ReportConf> reportConfClass) {
        return null;
    }

    @Override
    public Class<? extends AccountRule> getAccountRuleClass(Class<? extends AccountRuleConf> accountRuleConfClass) {
        return null;
    }

    @Override
    public Class<? extends PasswordRule> getPasswordRuleClass(Class<? extends PasswordRuleConf> passwordRuleConfClass) {
            return PasswordRuleImpl.class;
    }

    @Override
    public Class<? extends PullCorrelationRule> getPullCorrelationRuleClass(Class<? extends PullCorrelationRuleConf> pullCorrelationRuleConfClass) {
        return null;
    }

    @Override
    public Class<? extends PushCorrelationRule> getPushCorrelationRuleClass(Class<? extends PushCorrelationRuleConf> pushCorrelationRuleConfClass) {
        return null;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
