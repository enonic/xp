package com.enonic.wem.api.content.binary;

import com.enonic.wem.api.support.AbstractId;

public final class BinaryId
    extends AbstractId
{
    public BinaryId( final String id )
    {
        super( id );
    }

    public static BinaryId from( String s )
    {
        return new BinaryId( s );
    }
}
