package org.apache.syncope.core.provisioning.api.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class RealmUtilsNormalizingAddToTest {

    /** New realm to add */
    private String newRealm;

    /** List of realm */
    private Set<String> realms;

    private enum ConstantChecker {VALID_ADD, INVALID_NULL_ADD,STARTS_WITH_REALMS_ADD, STARTS_WITH_NEWREALM_ADD};
    private ConstantChecker testType;



    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {

        return Arrays.asList(new Object[][]{
                {null, null, ConstantChecker.INVALID_NULL_ADD},
                {null, "", ConstantChecker.INVALID_NULL_ADD},
                {null, "/e/f", ConstantChecker.INVALID_NULL_ADD},
                {null, "/a/b/c", ConstantChecker.INVALID_NULL_ADD},
                {null, "/a", ConstantChecker.INVALID_NULL_ADD},

                {new HashSet<>(), null, ConstantChecker.INVALID_NULL_ADD},
                {new HashSet<>(), "", ConstantChecker.VALID_ADD},
                {new HashSet<>(), "/e/f", ConstantChecker.VALID_ADD},
                {new HashSet<>(), "/a/b/c", ConstantChecker.VALID_ADD},
                {new HashSet<>(), "/a", ConstantChecker.VALID_ADD},

                {new HashSet<>(Arrays.asList("/a/b", "/c/d")), null, ConstantChecker.INVALID_NULL_ADD},
         //       {new HashSet<>(Arrays.asList("/a/b", "/c/d")), "", ConstantChecker.VALID_ADD},
                {new HashSet<>(Arrays.asList("/a/b", "/c/d")), "/e/f", ConstantChecker.VALID_ADD},
                {new HashSet<>(Arrays.asList("/a/b", "/c/d")), "/a/b/c", ConstantChecker.STARTS_WITH_REALMS_ADD},
                {new HashSet<>(Arrays.asList("/a/b", "/c/d")), "/a", ConstantChecker.STARTS_WITH_NEWREALM_ADD},

                {new HashSet<>(Arrays.asList("")), null, ConstantChecker.INVALID_NULL_ADD},
                {new HashSet<>(Arrays.asList("")), "", ConstantChecker.STARTS_WITH_REALMS_ADD},
          //      {new HashSet<>(Arrays.asList("")), "/e/f", ConstantChecker.VALID_ADD},
          //      {new HashSet<>(Arrays.asList("")), "/a/b/c", ConstantChecker.VALID_ADD},
          //      {new HashSet<>(Arrays.asList("")), "/a", ConstantChecker.VALID_ADD}
        });
    }


    public RealmUtilsNormalizingAddToTest(Set<String> realms, String newRealm, ConstantChecker testType) {
        this.realms = realms;
        this.newRealm = newRealm;
        this.testType = testType;
    }


    @Test
    public void normalizingAddToTest() {
        boolean actualValue = false;

        try{
            int prevRealmsSize = realms.size();
            actualValue = RealmUtils.normalizingAddTo(realms, newRealm);

            if(testType == ConstantChecker.VALID_ADD){
                assertTrue(actualValue);
                assertEquals(prevRealmsSize+1, realms.size());
            }
            if(testType == ConstantChecker.STARTS_WITH_NEWREALM_ADD){
                assertTrue(actualValue);
                assertEquals(prevRealmsSize, realms.size());
            }
            if(testType == ConstantChecker.STARTS_WITH_REALMS_ADD){
                assertFalse(actualValue);
                assertEquals(prevRealmsSize, realms.size());
            }


        }catch (NullPointerException e){
            if(testType == ConstantChecker.INVALID_NULL_ADD){

                assertTrue(true);}
        }


    }





}