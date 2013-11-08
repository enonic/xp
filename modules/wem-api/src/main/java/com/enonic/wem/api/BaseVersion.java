package com.enonic.wem.api;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BaseVersion
{
    private final static Pattern VERSION_PATTERN = Pattern.compile( "^(\\d+)\\.(\\d+)\\.(\\d+)$" );

    private final int major;

    private final int minor;

    private final int revision;

    private final String refString;

    protected BaseVersion( final int major, final int minor, final int revision )
    {
        this.major = major;
        this.minor = minor;
        this.revision = revision;
        this.refString = major + "." + minor + "." + revision;
    }

    protected BaseVersion( final String version )
    {
        final Matcher matcher = VERSION_PATTERN.matcher( version );
        if ( !matcher.find() )
        {
            throw new IllegalArgumentException( "Invalid version: [" + version + "]" );
        }

        final int major = Integer.parseInt( matcher.group( 1 ).toUpperCase() );
        final int minor = Integer.parseInt( matcher.group( 2 ).toUpperCase() );
        final int revision = Integer.parseInt( matcher.group( 3 ).toUpperCase() );

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

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final BaseVersion that = (BaseVersion) o;
        return refString.equals( that.refString );
    }

    @Override
    public int hashCode()
    {
        return this.refString.hashCode();
    }

    @Override
    public String toString()
    {
        return refString;
    }

    protected int compareTo( final BaseVersion other )
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

}
