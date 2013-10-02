package com.enonic.wem.core.item.dao;


import org.joda.time.DateTime;

import com.enonic.wem.api.item.Item;

public class ItemModifiedSinceRead
    extends RuntimeException
{
    public ItemModifiedSinceRead( final DateTime yourModifiedTime, final Item persistedItem )
    {
        super( "Item has been modified since it was read by you. Persisted Item's modified time is [" +
                   persistedItem.getModifiedTime().toString() + "] while yours is [" + yourModifiedTime.toString() + "]" );
    }
}
