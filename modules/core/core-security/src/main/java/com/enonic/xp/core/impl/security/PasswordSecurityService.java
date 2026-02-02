package com.enonic.xp.core.impl.security;

import java.security.SecureRandom;

import org.jspecify.annotations.NonNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

@Component(configurationPid = "com.enonic.xp.security", service = PasswordSecurityService.class)
public final class PasswordSecurityService
{
    private static final PBKDF2Encoder LEGACY_VERIFIER = new PBKDF2Encoder();

    static final SuPasswordVerifier SU_PASSWORD_VERIFIER = new SuPasswordVerifier();

    private volatile PHCEncoder phcEncoder;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Activate
    @Modified
    public void activate( final SecurityConfig config )
    {
        this.phcEncoder = new PHCEncoder( config.password_policy(), SECURE_RANDOM );
    }

    public PasswordEncoder defaultEncoder()
    {
        return phcEncoder;
    }

    public PasswordValidator suPasswordValidator()
    {
        return plainPassword -> {
            addRandomDelay();
            return SU_PASSWORD_VERIFIER.verify( plainPassword, null );
        };
    }

    public PasswordValidator validatorFor( final @NonNull String authHash )
    {
        if ( authHash.isEmpty() || authHash.startsWith( "$" ) )
        {
            return plainPassword -> phcEncoder.verify( plainPassword, authHash );
        }
        else
        {
            return plainPassword -> {
                addRandomDelay();
                return LEGACY_VERIFIER.verify( plainPassword, authHash );
            };
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
            Thread.currentThread().interrupt();
        }
    }
}
