package com.enonic.xp.schema.xdata;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.schema.BaseSchemaName;

@PublicApi
public final class XDataName
    extends BaseSchemaName
    implements Comparable<XDataName>
{
    private XDataName( final String name )
    {
        super( name );
    }

    private XDataName( final ApplicationKey applicationKey, final String localName )
    {
        super( applicationKey, localName );
    }

    public static XDataName from( final ApplicationKey applicationKey, final String localName )
    {
        return new XDataName( applicationKey, localName );
    }

    public static XDataName from( final String xdataName )
    {
        return new XDataName( xdataName );
    }

    public String getApplicationPrefix()
    {
        return this.getApplicationKey() == null ? "" : this.getApplicationKey().toString().replace( '.', '-' );
    }

    @Override
    public int compareTo( final XDataName that )
    {
        return this.toString().compareTo( that.toString() );
    }
}
