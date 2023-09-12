package org.apache.syncope.core.provisioning.api.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class RealmUtilsGetEffectiveTest {

    /** List of allowed realms */
    private Set<String> allowedRealms;

    /** Requested realm */
    private String requestedRealm;

    /** Expected filter and Normalized realms */
    private Set<String> expectedRealms;


    private enum ConstantChecker {VALID_EFFECTIVE, NULL_EFFECTIVE};
    private ConstantChecker testType;


    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {

        return Arrays.asList(new Object[][]{
                {new HashSet<>(Arrays.asList("/a/b/c@key1")), "/e/f/g@key2", new HashSet<>(Arrays.asList("/a/b/c@key1")), ConstantChecker.VALID_EFFECTIVE},
                {null, "/e/f/g@key2", new HashSet<>(Arrays.asList()), ConstantChecker.VALID_EFFECTIVE},
                {new HashSet<>(Arrays.asList("/a/b/c@key1")), null, null, ConstantChecker.NULL_EFFECTIVE},
                {new HashSet<>(Arrays.asList()), "/a/b/c@key1", new HashSet<>(Arrays.asList()), ConstantChecker.VALID_EFFECTIVE},
                {new HashSet<>(Arrays.asList("/a/b/c@key1")), "", new HashSet<>(Arrays.asList("/a/b/c@key1")), ConstantChecker.VALID_EFFECTIVE},
                {new HashSet<>(Arrays.asList("a-b-c@key1")), "/e/f/g@key2", new HashSet<>(Arrays.asList("a-b-c@key1")), ConstantChecker.VALID_EFFECTIVE},
                {new HashSet<>(Arrays.asList("/a/b/c@key1")), "e-f-g@key2", new HashSet<>(Arrays.asList("/a/b/c@key1")), ConstantChecker.VALID_EFFECTIVE},
        });
    }


    public RealmUtilsGetEffectiveTest(Set<String> alloweRealms, String requestedRealm, Set<String> expectedRealms, ConstantChecker testType) {
        this.allowedRealms = alloweRealms;
        this.requestedRealm = requestedRealm;
        this.expectedRealms = expectedRealms;
        this.testType = testType;
    }


    @Test
    public void getEffectiveTest() {
        Set<String> outputNormalize;

        try {
            outputNormalize = RealmUtils.getEffective(allowedRealms, requestedRealm);
            System.out.println(outputNormalize);

            assertEquals(expectedRealms, outputNormalize);
        }catch(NullPointerException e){
            if(testType == ConstantChecker.NULL_EFFECTIVE){assertTrue(true);}
        }

    }





}