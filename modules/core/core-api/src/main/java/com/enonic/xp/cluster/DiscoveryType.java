package com.enonic.xp.cluster;

public enum DiscoveryType
{
    STATIC_IP( "staticIp" );

    private String value;

    DiscoveryType( final String value )
    {
        this.value = value;
    }

    public static DiscoveryType fromString( final String value )
    {
        for ( DiscoveryType dt : DiscoveryType.values() )
        {
            if ( dt.value.equalsIgnoreCase( value ) )
            {
                return dt;
            }
        }
        throw new IllegalArgumentException( "No DiscoveryType with string value '" + value + "' found" );
    }

    @Override
    public String toString()
    {
        return value;
    }
}
