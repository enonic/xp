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

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final SuPasswordVerifier suPasswordVerifier;

    private volatile PHCEncoder phcEncoder;

    public PasswordSecurityService()
    {
        this( new SuPasswordVerifier() );
    }

    public PasswordSecurityService( final SuPasswordVerifier suPasswordVerifier )
    {
        this.suPasswordVerifier = suPasswordVerifier;
    }

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
            return suPasswordVerifier.verify( plainPassword, null );
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

    private static void addRandomDelay()
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
