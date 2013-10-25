package com.enonic.wem.api.item;

import com.enonic.wem.api.support.AbstractId;

public class ItemId
    extends AbstractId
    implements ItemSelector
{

    public ItemId( final String id )
    {
        super( id );
    }

    public static ItemId from( String id )
    {
        return new ItemId( id );
    }
}
