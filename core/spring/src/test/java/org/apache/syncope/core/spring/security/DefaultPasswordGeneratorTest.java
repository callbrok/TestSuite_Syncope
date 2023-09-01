package org.apache.syncope.core.spring.security;

import org.apache.syncope.common.lib.policy.DefaultPasswordRuleConf;
import org.apache.syncope.core.persistence.api.entity.policy.PasswordPolicy;
import org.apache.syncope.core.provisioning.api.ImplementationLookup;
import org.apache.syncope.core.provisioning.api.serialization.POJOHelper;
import org.apache.syncope.core.spring.ApplicationContextProvider;
import org.apache.syncope.core.spring.security.util.ImplementationImpl;
import org.apache.syncope.core.spring.security.util.ImplementationLookupImpl;
import org.apache.syncope.core.spring.security.util.PasswordPolicyImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class DefaultPasswordGeneratorTest {

    /** List of policy */
    private List<PasswordPolicy> policies;

    /** DefaultPasswordGenerator */
    private DefaultPasswordGenerator defaultPasswordGenerator = new DefaultPasswordGenerator();

    private enum ConstantChecker {VALID_GENERATE, INVALID_GENERATE};
    private ConstantChecker testType;


    @Mock
    ConfigurableApplicationContext applicationContext;

    @Before
    public void setUp() {
        applicationContext = Mockito.mock(ConfigurableApplicationContext.class);

        SecurityProperties securityProperties = new SecurityProperties();
        when(applicationContext.getBean(SecurityProperties.class)).thenReturn(securityProperties);

        ImplementationLookup implementationLookup = new ImplementationLookupImpl();
        when(applicationContext.getBean(ImplementationLookup.class)).thenReturn(implementationLookup);

        ApplicationContextProvider.setBeanFactory(new DefaultListableBeanFactory());
        ApplicationContextProvider.setApplicationContext(applicationContext);
    }


    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        DefaultPasswordRuleConf validConf = new DefaultPasswordRuleConf();
        validConf.setUppercase(2);
        List<PasswordPolicy> validPolicy = List.of(new PasswordPolicyImpl(new ImplementationImpl(POJOHelper.serialize(validConf))));

        DefaultPasswordRuleConf invalidMinConf = new DefaultPasswordRuleConf();
        invalidMinConf.setMinLength(-1);
        List<PasswordPolicy> invalidMinPolicies = List.of(new PasswordPolicyImpl(new ImplementationImpl(POJOHelper.serialize(invalidMinConf))));


        return Arrays.asList(new Object[][]{
                {validPolicy, ConstantChecker.VALID_GENERATE},
                {invalidMinPolicies, ConstantChecker.VALID_GENERATE},
                {null, ConstantChecker.INVALID_GENERATE},
                {new ArrayList<PasswordPolicy>(), ConstantChecker.VALID_GENERATE}
        });
    }

    public DefaultPasswordGeneratorTest(List<PasswordPolicy> policies, ConstantChecker testType) {
        this.testType = testType;
        this.policies = policies;
    }


    @Test
    public void generatePasswordTest(){
        String password = null;

        try {
            password = defaultPasswordGenerator.generate(policies);
            System.out.println(password);
            Assert.assertNotNull(password);

        } catch (NullPointerException e) {
            if(testType == ConstantChecker.INVALID_GENERATE){assertTrue(true);}
        }

    }
}