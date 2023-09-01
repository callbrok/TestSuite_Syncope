package org.apache.syncope.core.provisioning.api.utils;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class RealmUtilsNormalizeTest {

    /** List of realm */
    private Set<String> realms;

    /** Expected output realm list */
    private Pair<Set<String>, Set<String>> expectedRealms;



    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        Pair<Set<String>, Set<String>> expectedValid = Pair.of(
                new HashSet<>(),
                new HashSet<>(Arrays.asList("/a/b@k3y"))
        );

        Pair<Set<String>, Set<String>> expectedInvalid = Pair.of(
                new HashSet<>(Arrays.asList("/c/d")),
                new HashSet<>()
        );

        Pair<Set<String>, Set<String>> expectedEmptyInvalid = Pair.of(
                new HashSet<>(),
                new HashSet<>()
        );

        return Arrays.asList(new Object[][]{
                {null, expectedEmptyInvalid},
                {new HashSet<>(), expectedEmptyInvalid},
                {new HashSet<>(Arrays.asList("/a/b@k3y")), expectedValid},
                {new HashSet<>(Arrays.asList("/c/d")), expectedInvalid}
        });
    }


    public RealmUtilsNormalizeTest(Set<String> realms, Pair<Set<String>, Set<String>> expectedRealms) {
        this.realms = realms;
        this.expectedRealms = expectedRealms;
    }


    @Test
    public void test() {


        Pair<Set<String>, Set<String>> outputNormalize = RealmUtils.normalize(realms);
        System.out.println("USCITO: " + outputNormalize);

        assertEquals(outputNormalize, expectedRealms);

    }





}