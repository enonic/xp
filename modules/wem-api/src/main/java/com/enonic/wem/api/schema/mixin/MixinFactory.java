package com.enonic.wem.api.schema.mixin;


import com.enonic.wem.api.item.Item;

import static com.enonic.wem.api.schema.mixin.Mixin.newMixin;

public class MixinFactory
{
    public static Mixin fromItem( final Item item )
    {
        return newMixin().
            name( item.name() ).
            displayName( item.property( "displayName" ).getString() ).
            createdTime( item.getCreatedTime() ).
            creator( item.getCreator() ).
            modifiedTime( item.getModifiedTime() ).
            modifier( item.getModifier() ).
            icon( item.icon() ).
            build();
    }
}
