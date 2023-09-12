package org.apache.syncope.core.spring.security;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class AuthDataAccessorBuildAuthoritiesTest {

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
        invalid1EntForRealms.put("fake_ENT_ACCESS", Set.of("/a/b/c"));

        Map<String, Set<String>> invalid2EntForRealms = new HashMap<>();
        invalid2EntForRealms.put("DOMAIN_READ", Set.of("fake-path-realm"));

        Map<String, Set<String>> mapNotValidMock = Mockito.mock(Map.class);
        // Configura il mock in modo che quando si chiama il metodo getValue() su un oggetto Entry, generi un'eccezione
        Mockito.when(mapNotValidMock.entrySet())
                .thenAnswer(invocation -> {
                    Map.Entry<String, Set<String>> entry = Mockito.mock(Map.Entry.class);
                    Mockito.when(entry.getValue()).thenThrow(new RuntimeException("Errore generato"));
                    return Set.of(entry);
                });


        return Arrays.asList(new Object[][]{
                {null, ConstantChecker.INVALID_BUILD},
                {Collections.emptyMap(), ConstantChecker.INVALID_EMPTY_BUILD},
                {validEntForRealms, ConstantChecker.VALID_BUILD},
                {invalid1EntForRealms, ConstantChecker.VALID_BUILD},
                {invalid2EntForRealms, ConstantChecker.VALID_BUILD},
                {mapNotValidMock, ConstantChecker.INVALID_EMPTY_BUILD},
        });
    }


    public AuthDataAccessorBuildAuthoritiesTest(Map<String, Set<String>> entForRealms, ConstantChecker testType) {
        this.entForRealms = entForRealms;
        this.testType = testType;
    }

    @Test
    public void buildAuthoritiesTest() {
        boolean checkOutput = false;


        try {
            Set<SyncopeGrantedAuthority> syncopeGrantedAuthorities = authDataAccessor.buildAuthorities(entForRealms);

            // Controlla se per una determinata chiave entitlement restitutisce il medesimo gruppo di realms
            for (SyncopeGrantedAuthority auth: syncopeGrantedAuthorities) {
                checkOutput = entForRealms.get(auth.getAuthority()).equals(auth.getRealms());
            }

            if(testType == ConstantChecker.INVALID_EMPTY_BUILD && syncopeGrantedAuthorities.isEmpty()){checkOutput=true;}

            assertTrue(checkOutput);

        }catch (NullPointerException e){
            if(testType == ConstantChecker.INVALID_BUILD){assertTrue(true);}
        }

    }

}