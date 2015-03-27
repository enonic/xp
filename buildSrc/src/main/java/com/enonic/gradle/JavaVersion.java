package com.enonic.gradle;

public final class JavaVersion
{
    private final String value;

    public JavaVersion()
    {
        this.value = System.getProperty( "java.version" );
    }

    public boolean isJava8()
    {
        return this.value.startsWith( "1.8" );
    }

    public int getUpdate()
    {
        final int index = this.value.indexOf( '_' );
        if ( index <= 0 )
        {
            return 0;
        }

        try
        {
            return Integer.parseInt( this.value.substring( index + 1 ) );
        }
        catch ( final Exception e )
        {
            return 0;
        }
    }

    @Override
    public String toString()
    {
        return this.value;
    }
}
