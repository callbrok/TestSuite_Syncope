package org.apache.syncope.core.spring.security;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.syncope.core.provisioning.api.utils.RealmUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.times;

@RunWith(Parameterized.class)
public class AuthDataAccessorIT {

    /** entForRealms Map */
    private Map<String, Set<String>> entForRealms;


    private AuthDataAccessor authDataAccessor;

    private enum ConstantChecker {VALID_BUILD, INVALID_BUILD, INVALID_EMPTY_BUILD, INVALID_MAP};
    private ConstantChecker testType;



    @Before
    public void setUp(){
        authDataAccessor = new AuthDataAccessor(null, null, null, null, null, null, null, null, null, null, null, null, null);
    }


    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        Map<String, Set<String>> validEntForRealms = new HashMap<>();
        validEntForRealms.put("DOMAIN_READ", Set.of("/a/b/c","/e/f/g"));

        Map<String, Set<String>> invalid1EntForRealms = new HashMap<>();
        invalid1EntForRealms.put("777_fake_ENT_ACCESS", Set.of("/a/b/c"));

        Map<String, Set<String>> invalid2EntForRealms = new HashMap<>();
        invalid2EntForRealms.put("DOMAIN_READ", Set.of("fake-path-realm"));


        return Arrays.asList(new Object[][]{
                {null, ConstantChecker.INVALID_BUILD},
                {Collections.emptyMap(), ConstantChecker.INVALID_EMPTY_BUILD},
                {validEntForRealms, ConstantChecker.VALID_BUILD},
                {invalid1EntForRealms, ConstantChecker.VALID_BUILD},
                {invalid2EntForRealms, ConstantChecker.VALID_BUILD}
        });
    }


    public AuthDataAccessorIT(Map<String, Set<String>> entForRealms, ConstantChecker testType) {
        this.entForRealms = entForRealms;
        this.testType = testType;
    }


    // Check number of normalize() invocation inside buildAuthorities() method
    @Test
    public void normalizeCallTest() {
        boolean checkCall = true;

        Pair<Set<String>, Set<String>> expectedValid = Pair.of(
                new HashSet<>(),
                new HashSet<>(Arrays.asList("/a/b@k3y"))
        );

        try (MockedStatic<RealmUtils> realmMockedStatic = Mockito.mockStatic(RealmUtils.class)) {
            realmMockedStatic.when(() -> RealmUtils.normalize(anyCollection()))
                    .thenReturn(expectedValid);

            // when
            Set<SyncopeGrantedAuthority> syncopeGrantedAuthorities = authDataAccessor.buildAuthorities(entForRealms);

            //then
            realmMockedStatic.verify(
                    () -> RealmUtils.normalize(anyCollection()),
                    times(1)
            );
        }catch (Throwable e){
            checkCall = false;
        }

        // If passed null or empty collection as parameter, normalize() method will not be invoked
        if(testType != ConstantChecker.VALID_BUILD){checkCall=true;}

        assertTrue(checkCall);
    }


    // Make local normalize() and compare with normalized realms of authority
    @Test
    public void normalizeWorkingTest() {
        boolean checkOutput = false;
        Set<String> realmToNormalize = null;

        try {
            // retrieve realms to normalize from test parameters
            for (Map.Entry<String, Set<String>> passedMap : entForRealms.entrySet()) {realmToNormalize = passedMap.getValue();}
            // normalize realms set from map
            Pair<Set<String>, Set<String>> expectedOutputNormalize = RealmUtils.normalize(realmToNormalize);
            // Transform normalized realms from Pair object to Set<String> for comparing
            Set<String> normalizedRealms = new HashSet<>(expectedOutputNormalize.getLeft());
            normalizedRealms.addAll(expectedOutputNormalize.getRight());

            Set<SyncopeGrantedAuthority> syncopeGrantedAuthorities = authDataAccessor.buildAuthorities(entForRealms);

            for (SyncopeGrantedAuthority auth: syncopeGrantedAuthorities) {
                System.out.println("AUTH_ENTITLEMENT: " + auth.getAuthority() + " | AUTH_REALMS: " + auth.getRealms());

                checkOutput = normalizedRealms.equals(auth.getRealms());
            }

            if(testType == ConstantChecker.INVALID_EMPTY_BUILD && syncopeGrantedAuthorities.isEmpty()){checkOutput=true;}

            assertTrue(checkOutput);

        }catch (NullPointerException e){
            if(testType == ConstantChecker.INVALID_BUILD){assertTrue(true);}
        }

    }

}