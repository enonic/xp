package com.enonic.xp.core.impl.security;

import java.security.SecureRandom;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

import com.google.common.base.Strings;

@Component(configurationPid = "com.enonic.xp.security", service = PasswordSecurityService.class)
public final class PasswordSecurityService
{
    private static final PBKDF2Encoder LEGACY_VERIFIER = new PBKDF2Encoder();

    private static final SuPasswordVerifier SU_PASSWORD_VERIFIER = new SuPasswordVerifier();

    private volatile PHCEncoder phcEncoder;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Activate
    @Modified
    public void activate( final SecurityConfig config )
    {
        this.phcEncoder = new PHCEncoder( config.password_policy() );
    }

    public PasswordEncoder defaultEncoder()
    {
        return phcEncoder;
    }

    public PasswordValidator suPasswordValidator() {
            addRandomDelay();
            return plainPassword -> SU_PASSWORD_VERIFIER.verify( plainPassword, null );
    }

    public PasswordValidator validatorFor( final String authenticationHash )
    {
        String authHash = Strings.nullToEmpty( authenticationHash );
        if ( authHash.isEmpty() || authHash.startsWith( "$" ) )
        {
            return plainPassword -> phcEncoder.verify( plainPassword, authHash );
        }
        else
        {
            addRandomDelay();
            return plainPassword -> LEGACY_VERIFIER.verify( plainPassword, authHash );
        }
    }

    static void addRandomDelay()
    {
        // legacy validators use random delay before verifying password
        try
        {
            Thread.sleep( SECURE_RANDOM.nextInt( 130 ) + 20 );
        }
        catch ( InterruptedException e )
        {
            // Thread interrupted during sleep, nothing to do
        }
    }
}
