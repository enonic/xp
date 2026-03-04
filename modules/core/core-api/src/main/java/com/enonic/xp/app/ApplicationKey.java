package com.enonic.xp.app;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.core.internal.NameValidator;


@NullMarked
public final class ApplicationKey
    implements Serializable
{
    @Serial
    private static final long serialVersionUID = 0;

    /**
     * ApplicationKey AKA Application Name validator.
     * Must be a valid OSGi bundle name.
     * Must not contain dashes (-), because dots (.) converted to dashes when the application name is persisted as a field (for instance, in SiteConfig).
     * Length is limited to 63 to align with other system limits: Database identifiers, DNS labels, Kubernetes labels.
     * Other general limitations applied. See {@link NameValidator}
     */
    private static final NameValidator APPLICATION_KEY_VALIDATOR =
        NameValidator.builder( ApplicationKey.class ).maxLength( 63 ).regex( Pattern.compile( "^\\w+(?:\\.\\w+)*$" ) ).build();

    public static final ApplicationKey SYSTEM = new ApplicationKey( "system" );

    public static final ApplicationKey MEDIA_MOD = new ApplicationKey( "media" );

    public static final ApplicationKey PORTAL = new ApplicationKey( "portal" );

    public static final ApplicationKey BASE = new ApplicationKey( "base" );

    public static final ApplicationKeys SYSTEM_RESERVED_APPLICATION_KEYS = ApplicationKeys.from( SYSTEM, MEDIA_MOD, PORTAL, BASE );

    private final String name;

    private ApplicationKey( final String name )
    {
        this.name = Objects.requireNonNull( name );
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return name;
    }

    @Override
    public boolean equals( final Object o )
    {
        return this == o || o instanceof ApplicationKey && this.name.equals( ( (ApplicationKey) o ).name );
    }

    @Override
    public int hashCode()
    {
        return this.name.hashCode();
    }

    public static ApplicationKey from( final String name )
    {
        return switch ( Objects.requireNonNull( name, "ApplicationKey cannot be null" ) )
        {
            case "system" -> SYSTEM;
            case "media" -> MEDIA_MOD;
            case "portal" -> PORTAL;
            case "base" -> BASE;
            default -> new ApplicationKey( APPLICATION_KEY_VALIDATOR.validate( name ) );
        };
    }
}
