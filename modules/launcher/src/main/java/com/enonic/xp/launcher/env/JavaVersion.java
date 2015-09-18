package com.enonic.xp.launcher.env;

final class JavaVersion
{
    private final String value;

    public JavaVersion( final SystemProperties props )
    {
        this.value = props.get( "java.version" );
    }

    public boolean isJava8()
    {
        return this.value.startsWith( "1.8" );
    }

    public int getUpdate()
    {
        final String version = getVersion();
        final int index = version.indexOf( '_' );
        if ( index <= 0 )
        {
            return 0;
        }

        try
        {
            return Integer.parseInt( version.substring( index + 1 ) );
        }
        catch ( final Exception e )
        {
            return 0;
        }
    }

    private String getVersion()
    {
        final int index = this.value.indexOf( '-' );
        if ( index <= 0 )
        {
            return this.value;
        }
        else
        {
            return this.value.substring( 0, index );
        }
    }

    @Override
    public String toString()
    {
        return this.value;
    }
}
