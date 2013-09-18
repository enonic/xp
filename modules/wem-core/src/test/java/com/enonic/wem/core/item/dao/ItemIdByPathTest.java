package com.enonic.wem.core.item.dao;


import java.util.LinkedHashMap;

import org.junit.Test;

import com.enonic.wem.api.item.ItemId;
import com.enonic.wem.api.item.ItemPath;

import static junit.framework.Assert.assertNotNull;

public class ItemIdByPathTest
{
    private ItemIdByPath itemIdByPath = new ItemIdByPath( new LinkedHashMap<ItemPath, ItemId>() );

    @Test
    public void when_get_given_a_path_with_associated_ItemId_then_notNull_is_returned()
    {
        itemIdByPath.put( new ItemPath( "/path" ), new ItemId() );
        assertNotNull( itemIdByPath.get( new ItemPath( "/path" ) ) );
    }
}
