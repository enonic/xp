package com.enonic.xp.core.impl.content;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.SetNodeChildOrderProcessor;

public class SetContentChildOrderProcessor
    implements SetNodeChildOrderProcessor
{
    @Override
    public Node process( final Node originalNode )
    {

        final PropertyTree originalData = originalNode.data().copy();

        if ( originalData.hasProperty( ContentPropertyNames.INHERIT ) )
        {
            boolean changed = false;
            final ImmutableSet.Builder<String> inheritSet = ImmutableSet.builder();

            for ( final String value : originalData.getStrings( ContentPropertyNames.INHERIT ) )
            {
                if ( !ContentInheritType.SORT.name().equals( value ) )
                {
                    inheritSet.add( value );
                }
                else
                {
                    changed = true;
                }
            }

            if ( changed )
            {
                originalData.removeProperties( ContentPropertyNames.INHERIT );
                originalData.addStrings( ContentPropertyNames.INHERIT, inheritSet.build() );
            }
        }

        return Node.
            create( originalNode ).
            data( originalData ).
            build();
    }
}
