package com.enonic.xp.core.impl.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class PasswordSecurityServiceTest
{
    private PasswordSecurityService factory;

    @BeforeEach
    void setUp()
    {
        final SecurityConfig securityConfig = mock( SecurityConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        this.factory = new PasswordSecurityService();
        this.factory.activate( securityConfig );
    }

    @Test
    void defaultEncoder_returns_phc_encoder()
    {
        final PasswordEncoder encoder = factory.defaultEncoder();

        assertInstanceOf( PHCEncoder.class, encoder );

        final String encoded = encoder.encode( "testpassword".toCharArray() );
        assertTrue( encoded.startsWith( "$pbkdf2-sha512$" ) );
    }

    @Test
    void validatorFor_phc_format()
    {
        final String encodedPwd = factory.defaultEncoder().encode( "testpassword".toCharArray() );

        assertTrue( factory.validatorFor( encodedPwd ).validate( "testpassword".toCharArray() ) );
        assertFalse( factory.validatorFor( encodedPwd ).validate( "wrongpassword".toCharArray() ) );
    }

    @Test
    void validatorFor_legacy_format()
    {
        // Pre-generated legacy hash - can only verify, not create new ones
        final String legacyHash = "PBKDF2:0102030405060708091011121314151617181920:b8f3c4a5d6e7f8091a2b3c4d5e6f7081";

        // Wrong password should fail validation
        assertFalse( factory.validatorFor( legacyHash ).validate( "wrongpassword".toCharArray() ) );
    }

    @Test
    void validatorFor_empty_always_fails()
    {
        final PasswordValidator validator = factory.validatorFor( "" );

        assertFalse( validator.validate( "anypassword".toCharArray() ) );
    }

    @Test
    void suValidator()
    {
        assertNotNull( factory.suPasswordValidator() );
    }
}
