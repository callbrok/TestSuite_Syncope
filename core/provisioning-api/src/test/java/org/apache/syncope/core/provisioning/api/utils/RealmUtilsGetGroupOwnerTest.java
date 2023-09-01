package org.apache.syncope.core.provisioning.api.utils;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class RealmUtilsGetGroupOwnerTest {

    /** Path of realm */
    String realmPath;

    /** Key ID of Owner Group */
    String groupKey;


    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(new Object[][]{
                {null, ""},
                {"", "k3y"},
                {"/", null},
                {"pr0v@", "~~~"}
        });
    }


    public RealmUtilsGetGroupOwnerTest(String realmPath, String groupKey) {
        this.realmPath = realmPath;
        this.groupKey = groupKey;
    }


    @Test
    public void test() {
        String expectedResult = String.format("%s@%s",this.realmPath,this.groupKey);
        String actualValue = RealmUtils.getGroupOwnerRealm(this.realmPath, this.groupKey);
        System.out.println("RISULTATO: " + actualValue);
        assertEquals(expectedResult, actualValue);
    }





}