package com.enonic.xp.util;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public final class Version
    implements Comparable<Version>
{
    public static final Version emptyVersion = new Version( 0 );

    private static final String SEPARATOR = ".";

    private final int major;

    private final int minor;

    private final int micro;

    private final String qualifier;

    private transient String versionString /* default to null */;

    private transient int hash /* default to 0 */;

    public Version( int major )
    {
        this( major, 0, 0, null );
    }

    public Version( int major, int minor )
    {
        this( major, minor, 0, null );
    }

    public Version( int major, int minor, int micro )
    {
        this( major, minor, micro, null );
    }

    public Version( int major, int minor, int micro, String qualifier )
    {
        if ( qualifier == null )
        {
            qualifier = "";
        }

        this.major = major;
        this.minor = minor;
        this.micro = micro;
        this.qualifier = qualifier;
        validate();
    }

    public Version( String version )
    {
        int maj = 0;
        int min = 0;
        int mic = 0;
        String qual = "";

        try
        {
            StringTokenizer st = new StringTokenizer( version, SEPARATOR, true );
            maj = parseInt( st.nextToken(), version );

            if ( st.hasMoreTokens() )
            { // minor
                st.nextToken(); // consume delimiter
                min = parseInt( st.nextToken(), version );

                if ( st.hasMoreTokens() )
                { // micro
                    st.nextToken(); // consume delimiter
                    mic = parseInt( st.nextToken(), version );

                    if ( st.hasMoreTokens() )
                    { // qualifier separator
                        st.nextToken(); // consume delimiter
                        qual = st.nextToken( "" ); // remaining string

                        if ( st.hasMoreTokens() )
                        { // fail safe
                            throw new IllegalArgumentException( "invalid version \"" + version + "\": invalid format" );
                        }
                    }
                }
            }
        }
        catch ( NoSuchElementException e )
        {
            IllegalArgumentException iae = new IllegalArgumentException( "invalid version \"" + version + "\": invalid format", e );
            throw iae;
        }

        major = maj;
        minor = min;
        micro = mic;
        qualifier = qual;
        validate();
    }

    private static int parseInt( String value, String version )
    {
        try
        {
            return Integer.parseInt( value );
        }
        catch ( NumberFormatException e )
        {
            IllegalArgumentException iae =
                new IllegalArgumentException( "invalid version \"" + version + "\": non-numeric \"" + value + "\"", e );
            throw iae;
        }
    }

    public static Version parseVersion( String version )
    {
        if ( version == null )
        {
            return emptyVersion;
        }

        return valueOf( version );
    }

    public static Version valueOf( String version )
    {
        version = version.trim();
        if ( version.length() == 0 )
        {
            return emptyVersion;
        }

        return new Version( version );
    }

    private void validate()
    {
        if ( major < 0 )
        {
            throw new IllegalArgumentException( "invalid version \"" + toString0() + "\": negative number \"" + major + "\"" );
        }
        if ( minor < 0 )
        {
            throw new IllegalArgumentException( "invalid version \"" + toString0() + "\": negative number \"" + minor + "\"" );
        }
        if ( micro < 0 )
        {
            throw new IllegalArgumentException( "invalid version \"" + toString0() + "\": negative number \"" + micro + "\"" );
        }
        for ( int i = 0; i < qualifier.length(); i++ )
        {
            final char ch = qualifier.charAt(i);
            if ( ( 'A' <= ch ) && ( ch <= 'Z' ) )
            {
                continue;
            }
            if ( ( 'a' <= ch ) && ( ch <= 'z' ) )
            {
                continue;
            }
            if ( ( '0' <= ch ) && ( ch <= '9' ) )
            {
                continue;
            }
            if ( ( ch == '_' ) || ( ch == '-' ) )
            {
                continue;
            }
            throw new IllegalArgumentException( "invalid version \"" + toString0() + "\": invalid qualifier \"" + qualifier + "\"" );
        }
    }

    public int getMajor()
    {
        return major;
    }

    public int getMinor()
    {
        return minor;
    }

    public int getMicro()
    {
        return micro;
    }

    public String getQualifier()
    {
        return qualifier;
    }


    public boolean lessThan( final Version version )
    {
        return this.compareTo( version ) < 0;
    }

    @Override
    public String toString()
    {
        return toString0();
    }

    String toString0()
    {
        String s = versionString;
        if ( s != null )
        {
            return s;
        }
        int q = qualifier.length();
        StringBuilder result = new StringBuilder( 20 + q );
        result.append( major );
        result.append( SEPARATOR );
        result.append( minor );
        result.append( SEPARATOR );
        result.append( micro );
        if ( q > 0 )
        {
            result.append( SEPARATOR );
            result.append( qualifier );
        }
        return versionString = result.toString();
    }

    public String toShortestString()
    {
        int q = qualifier.length();
        StringBuilder result = new StringBuilder();
        result.append( major );
        if ( minor > 0 || micro > 0 || q > 0 )
        {
            result.append( SEPARATOR );
            result.append( minor );
            if ( micro > 0 || q > 0 )
            {
                result.append( SEPARATOR );
                result.append( micro );
                if ( q > 0 )
                {
                    result.append( SEPARATOR );
                    result.append( qualifier );
                }
            }
        }
        return result.toString();
    }

    @Override
    public int hashCode()
    {
        int h = hash;
        if ( h != 0 )
        {
            return h;
        }
        h = 31 * 17;
        h = 31 * h + major;
        h = 31 * h + minor;
        h = 31 * h + micro;
        h = 31 * h + qualifier.hashCode();
        return hash = h;
    }

    @Override
    public boolean equals( Object object )
    {
        if ( object == this )
        { // quicktest
            return true;
        }

        if ( !( object instanceof Version ) )
        {
            return false;
        }

        Version other = (Version) object;
        return ( major == other.major ) && ( minor == other.minor ) && ( micro == other.micro ) && qualifier.equals( other.qualifier );
    }

    @Override
    public int compareTo( Version other )
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

        return qualifier.compareTo( other.qualifier );
    }
}
