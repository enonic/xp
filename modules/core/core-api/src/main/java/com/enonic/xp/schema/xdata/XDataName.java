package com.enonic.xp.schema.xdata;

import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.schema.BaseSchemaName;

@Beta
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

    @Override
    public int compareTo( final XDataName that )
    {
        return this.toString().compareTo( that.toString() );
    }
}
