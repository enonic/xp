package com.enonic.wem.api.schema.mixin;


import com.enonic.wem.api.item.Node;

import static com.enonic.wem.api.schema.mixin.Mixin.newMixin;

public class MixinFactory
{
    public static Mixin fromItem( final Node node )
    {
        return newMixin().
            name( node.name() ).
            displayName( node.property( "displayName" ).getString() ).
            createdTime( node.getCreatedTime() ).
            creator( node.getCreator() ).
            modifiedTime( node.getModifiedTime() ).
            modifier( node.getModifier() ).
            icon( node.icon() ).
            build();
    }
}
