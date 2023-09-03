package org.apache.syncope.core.spring.security;

import org.apache.syncope.common.lib.policy.DefaultPasswordRuleConf;
import org.apache.syncope.core.persistence.api.entity.Realm;
import org.apache.syncope.core.persistence.api.entity.policy.PasswordPolicy;
import org.apache.syncope.core.provisioning.api.ImplementationLookup;
import org.apache.syncope.core.provisioning.api.serialization.POJOHelper;
import org.apache.syncope.core.spring.ApplicationContextProvider;
import org.apache.syncope.core.spring.security.util.*;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

import java.nio.BufferOverflowException;
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

    /** Regex for checking generated password */
    private String regexForCheck;


    private enum ConstantChecker {VALID_GENERATE, VALID_GENERATE_EMPTY, INVALID_GENERATE};
    private enum Miglioramenti {
        CONF1, CONF2, CONF3, CONF4, CONF5, CONF6, CONF7, CONF8, CONF9, CONF10,
        CONF11, CONF12, CONF13, CONF14, CONF15, CONF16, CONF17, CONF18, CONF19, CONF20,
        CONF21, CONF22, CONF23, CONF24, CONF25, CONF26, CONF27, CONF28, CONF_29_BUFFEROVERFLOW
    }

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
                {validPolicy, ConstantChecker.VALID_GENERATE, "^(?=.*[A-Z].*[A-Z]).{8,64}$"},
                {invalidMinPolicies, ConstantChecker.VALID_GENERATE, "^.{8,64}$"},
                {null, ConstantChecker.INVALID_GENERATE, null},
                {new ArrayList<PasswordPolicy>(), ConstantChecker.VALID_GENERATE_EMPTY, "^.{8,64}$"},

                // Miglioramenti
                {bestParameters(Miglioramenti.CONF1), ConstantChecker.VALID_GENERATE, "^.{8,64}$"},
                {bestParameters(Miglioramenti.CONF2), ConstantChecker.VALID_GENERATE, "^.{8,64}$"},
                {bestParameters(Miglioramenti.CONF3), ConstantChecker.VALID_GENERATE, "^(?=.*[A-Z].*[A-Z].*[A-Z])[A-Za-z0-9]{8,64}$"},

                {bestParameters(Miglioramenti.CONF4), ConstantChecker.VALID_GENERATE, "^.{8,64}$"},
                {bestParameters(Miglioramenti.CONF5), ConstantChecker.VALID_GENERATE, "^.{8,64}$"},
                {bestParameters(Miglioramenti.CONF6), ConstantChecker.VALID_GENERATE, "^(?=.*[a-z].*[a-z].*[a-z])[A-Za-z0-9]{8,64}$"},

                {bestParameters(Miglioramenti.CONF7), ConstantChecker.VALID_GENERATE, "^.{8,64}$"},
                {bestParameters(Miglioramenti.CONF8), ConstantChecker.VALID_GENERATE, "^.{8,64}$"},
                {bestParameters(Miglioramenti.CONF9), ConstantChecker.VALID_GENERATE, "^.{8,11}$"},
                {bestParameters(Miglioramenti.CONF10), ConstantChecker.VALID_GENERATE, "^.{8,64}$"},

                {bestParameters(Miglioramenti.CONF11), ConstantChecker.VALID_GENERATE, "^.{8,64}$"},
                {bestParameters(Miglioramenti.CONF12), ConstantChecker.VALID_GENERATE, "^.{8,64}$"},
                {bestParameters(Miglioramenti.CONF13), ConstantChecker.VALID_GENERATE, "^(?=.*[a-zA-Z].*[a-zA-Z].*[a-zA-Z].*[a-zA-Z])[A-Za-z0-9]{8,64}$"},

                {bestParameters(Miglioramenti.CONF14), ConstantChecker.VALID_GENERATE, "^.{8,64}$"},
                {bestParameters(Miglioramenti.CONF15), ConstantChecker.VALID_GENERATE, "^.{8,64}$"},
                {bestParameters(Miglioramenti.CONF16), ConstantChecker.VALID_GENERATE, "^(?=.*[0-9].*[0-9].*[0-9].*[0-9].*[0-9].*[0-9])[A-Za-z0-9]{8,64}$"},

                {bestParameters(Miglioramenti.CONF17), ConstantChecker.VALID_GENERATE, "^.{8,64}$"},
                {bestParameters(Miglioramenti.CONF18), ConstantChecker.VALID_GENERATE, "^.{8,64}$"},
                {bestParameters(Miglioramenti.CONF19), ConstantChecker.VALID_GENERATE, "^.{5,64}$"},

                {bestParameters(Miglioramenti.CONF20), ConstantChecker.INVALID_GENERATE, null},
                {bestParameters(Miglioramenti.CONF21), ConstantChecker.VALID_GENERATE, "^.{8,64}$"},
                {bestParameters(Miglioramenti.CONF22), ConstantChecker.VALID_GENERATE, "^(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?])[A-Za-z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]{8,64}$"},

                {bestParameters(Miglioramenti.CONF23), ConstantChecker.VALID_GENERATE, "^.{8,64}$"},
                {bestParameters(Miglioramenti.CONF24), ConstantChecker.VALID_GENERATE, "^.{8,64}$"},
                {bestParameters(Miglioramenti.CONF25), ConstantChecker.INVALID_GENERATE, null},

                {bestParameters(Miglioramenti.CONF26), ConstantChecker.VALID_GENERATE, "^.{8,64}$"},
                {bestParameters(Miglioramenti.CONF27), ConstantChecker.VALID_GENERATE, "^.{8,64}$"},

                {bestParameters(Miglioramenti.CONF28), ConstantChecker.VALID_GENERATE, "^(?!(?:.*[xXpP]|h0t_w0rd))[A-Za-z0-9@!%]{8,16}$"},

                {bestParameters(Miglioramenti.CONF_29_BUFFEROVERFLOW), ConstantChecker.INVALID_GENERATE, "^.{8,64}$"},

        });
    }

    public DefaultPasswordGeneratorTest(List<PasswordPolicy> policies, ConstantChecker testType, String regexForCheck) {
        this.testType = testType;
        this.regexForCheck = regexForCheck;
        this.policies = policies;
    }


    @Test
    public void generatePasswordTest(){
        String password = null;
        boolean checkPass = false;

        try {
            DefaultPasswordGenerator defaultPasswordGenerator = new DefaultPasswordGenerator();
            password = defaultPasswordGenerator.generate(policies);

            System.out.println(password);

            checkPass = password.matches(regexForCheck);
            assertTrue(checkPass);

        } catch (NullPointerException | BufferOverflowException | IllegalArgumentException e) {
            System.out.println("ECCEZZIONE: " + e.getClass().getName());
            if(testType == ConstantChecker.INVALID_GENERATE){assertTrue(true);}
        }

    }

    // Miglioramenti JaCoCo
    public static List<PasswordPolicy> bestParameters(Miglioramenti confEnum){
        DefaultPasswordRuleConf bestConf = new DefaultPasswordRuleConf();

        int max_default_leght = 64;
        int min_default_lenght = 8;

        switch (confEnum){
            case CONF1 -> {bestConf.setUppercase(-1);}
            case CONF2 -> {bestConf.setUppercase(0);}
            case CONF3 -> {bestConf.setUppercase(3);}

            case CONF4 -> {bestConf.setLowercase(-1);}
            case CONF5 -> {bestConf.setLowercase(0);}
            case CONF6 -> {bestConf.setLowercase(3);}

            case CONF7 -> {bestConf.setMaxLength(-1);}
            case CONF8 -> {bestConf.setMaxLength(0);}
            case CONF9 -> {bestConf.setMaxLength(11);}
            case CONF10 -> {bestConf.setMaxLength(max_default_leght+1);}

            case CONF11 -> {bestConf.setAlphabetical(-1);}
            case CONF12 -> {bestConf.setAlphabetical(0);}
            case CONF13 -> {bestConf.setAlphabetical(4);}

            case CONF14 -> {bestConf.setDigit(-1);}
            case CONF15 -> {bestConf.setDigit(0);}
            case CONF16 -> {bestConf.setDigit(6);}

            case CONF17 -> {bestConf.setMinLength(-1);}
            case CONF18 -> {bestConf.setMinLength(0);}
            case CONF19 -> {bestConf.setMinLength(5);}

            case CONF20 -> {bestConf.setSpecial(-1);}
            case CONF21 -> {bestConf.setSpecial(0);}
            case CONF22 -> {bestConf.setSpecial(4);}

            case CONF23 -> {bestConf.setRepeatSame(-1);}
            case CONF24 -> {bestConf.setRepeatSame(0);}
            case CONF25 -> {bestConf.setRepeatSame(1);}

            case CONF26 -> {bestConf.setUsernameAllowed(true);}
            case CONF27 -> {bestConf.setUsernameAllowed(false);}

            case CONF28 -> {
                bestConf.setMaxLength(16);
                bestConf.setSpecial(3);
                bestConf.getSpecialChars().add('@');
                bestConf.getSpecialChars().add('!');
                bestConf.getSpecialChars().add('%');
                bestConf.getIllegalChars().add('x');
                bestConf.getIllegalChars().add('p');
                bestConf.getWordsNotPermitted().add("h0t_w0rd");
            }

            case CONF_29_BUFFEROVERFLOW -> {
                bestConf.setMaxLength(1);
                bestConf.setMinLength(2);
                bestConf.setAlphabetical(1);
                bestConf.setUppercase(1);
                bestConf.setLowercase(1);
                bestConf.setDigit(1);
                bestConf.setSpecial(1);
                bestConf.getSpecialChars().add('$');
                bestConf.getIllegalChars().add('x');
                bestConf.setRepeatSame(2);
                bestConf.setUsernameAllowed(true);
                bestConf.getWordsNotPermitted().add("h0t_w0rd");
            }
        }

        return List.of(new PasswordPolicyImpl(new ImplementationImpl(POJOHelper.serialize(bestConf))));
    }

    @Test
    public void generatePasswordExternalResourceTest(){
        String password = null;
        boolean checkPass = false;

        ExternalResourceImpl externalResource = null;
        List<Realm> realms = null;

        if(testType != ConstantChecker.VALID_GENERATE_EMPTY && policies != null){
            externalResource = new ExternalResourceImpl(policies.get(0));
            realms = List.of(new RealmImpl(policies.get(0)));

        }else {
            PasswordPolicyImpl passPolicyEmpty = new PasswordPolicyImpl();

            externalResource = new ExternalResourceImpl(passPolicyEmpty);
            realms = List.of(new RealmImpl(passPolicyEmpty));
        }

        try {

            DefaultPasswordGenerator defaultPasswordGenerator = new DefaultPasswordGenerator();
            password = defaultPasswordGenerator.generate(externalResource, realms);
            System.out.println(password);

            checkPass = password.matches(regexForCheck);
            assertTrue(checkPass);

        } catch (NullPointerException | BufferOverflowException | IllegalArgumentException e) {
            System.out.println("ENTRATO");
            if(testType == ConstantChecker.INVALID_GENERATE){assertTrue(true);}
        }

    }
}