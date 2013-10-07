package com.enonic.wem.api.module;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Version
    implements Comparable<Version>
{
    private final static Pattern VERSION_PATTERN = Pattern.compile( "^(\\d+)\\.(\\d+)\\.(\\d+)$" );

    private final int major;

    private final int minor;

    private final int revision;

    private final String refString;

    private Version( final int major, final int minor, final int revision )
    {
        this.major = major;
        this.minor = minor;
        this.revision = revision;
        this.refString = major + "." + minor + "." + revision;
    }

    public int getMajor()
    {
        return major;
    }

    public int getMinor()
    {
        return minor;
    }

    public int getRevision()
    {
        return revision;
    }

    public boolean equals( final Object o )
    {
        return ( o instanceof Version ) && ( (Version) o ).refString.equals( this.refString );
    }

    public int hashCode()
    {
        return this.refString.hashCode();
    }

    @Override
    public String toString()
    {
        return refString;
    }

    @Override
    public int compareTo( final Version other )
    {
        if ( other == this )
        {
            return 0;
        }

        int result = major - other.major;
        if ( result != 0 )
        {
            return result;
        }
        result = minor - other.minor;
        if ( result != 0 )
        {
            return result;
        }
        result = revision - other.revision;
        if ( result != 0 )
        {
            return result;
        }
        return 0;
    }

    public static Version from( final String version )
    {
        final Matcher matcher = VERSION_PATTERN.matcher( version );
        if ( !matcher.find() )
        {
            throw new IllegalArgumentException( "Invalid version: [" + version + "]" );
        }

        final int major = Integer.parseInt( matcher.group( 1 ).toUpperCase() );
        final int minor = Integer.parseInt( matcher.group( 2 ).toUpperCase() );
        final int rev = Integer.parseInt( matcher.group( 3 ).toUpperCase() );
        return new Version( major, minor, rev );
    }

    public static Version from( final int major, final int minor, final int revision )
    {
        return new Version( major, minor, revision );
    }
}
