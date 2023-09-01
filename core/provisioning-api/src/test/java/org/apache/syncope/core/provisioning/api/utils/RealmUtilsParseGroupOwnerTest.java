package org.apache.syncope.core.provisioning.api.utils;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class RealmUtilsParseGroupOwnerTest {

    /** String to parse */
    String stringToParse;

    private enum ConstantChecker {VALID_PARSE, INVALID_PARSE, NULL_PARSE};
    private ConstantChecker testType;



    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(new Object[][]{
                {null, ConstantChecker.INVALID_PARSE},
                {"", ConstantChecker.INVALID_PARSE},
                {"path@key", ConstantChecker.VALID_PARSE},
                {"path-key", ConstantChecker.INVALID_PARSE}
        });
    }


    public RealmUtilsParseGroupOwnerTest(String stringToParse, ConstantChecker testType) {
        this.stringToParse = stringToParse;
        this.testType = testType;
    }


    @Test
    public void test() {
        Optional<Pair<String,String>> expectedValidValue = Optional.of(Pair.of("path","key"));
        Optional<Pair<String, String>> actualValue;

        try{
            actualValue = RealmUtils.parseGroupOwnerRealm(this.stringToParse);

            if(testType == ConstantChecker.INVALID_PARSE){
                System.out.println("RISULTATO: " + actualValue);
                assertEquals(Optional.empty(), actualValue);
            }
            if(testType == ConstantChecker.VALID_PARSE){
                System.out.println("RISULTATO: " + actualValue);
                assertEquals(expectedValidValue, actualValue);
            }

        }catch (NullPointerException e){
            if(testType == ConstantChecker.NULL_PARSE){assertTrue(true);}
        }
    }





}