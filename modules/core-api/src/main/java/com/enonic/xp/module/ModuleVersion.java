package com.enonic.xp.module;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

public final class ModuleVersion
    implements Comparable<ModuleVersion>
{
    private final static Pattern VERSION_PATTERN = Pattern.compile( "^(\\d+)\\.(\\d+)\\.(\\d+)(\\.([^\\s]+))?$" );

    private final int major;

    private final int minor;

    private final int micro;

    private final String qualifier;

    private final String refString;

    private ModuleVersion( final String version )
    {
        Preconditions.checkNotNull( version, "version not given" );
        final Matcher matcher = VERSION_PATTERN.matcher( version );
        if ( !matcher.find() )
        {
            throw new IllegalArgumentException( "Invalid version: [" + version + "]" );
        }

        this.major = Integer.parseInt( matcher.group( 1 ) );
        this.minor = Integer.parseInt( matcher.group( 2 ) );
        this.micro = Integer.parseInt( matcher.group( 3 ) );
        this.qualifier = matcher.group( 5 );

        if ( this.qualifier == null )
        {
            this.refString = this.major + "." + this.minor + "." + this.micro;
        }
        else
        {
            this.refString = this.major + "." + this.minor + "." + this.micro + "." + this.qualifier;
        }
    }

    public int getMajor()
    {
        return this.major;
    }

    public int getMinor()
    {
        return this.minor;
    }

    public int getMicro()
    {
        return this.micro;
    }

    public String getQualifier()
    {
        return this.qualifier;
    }

    @Override
    public int compareTo( final ModuleVersion other )
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
        result = micro - other.micro;
        if ( result != 0 )
        {
            return result;
        }
        return 0;
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

        final ModuleVersion that = (ModuleVersion) o;
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

    public static ModuleVersion from( final String version )
    {
        return new ModuleVersion( version );
    }
}
