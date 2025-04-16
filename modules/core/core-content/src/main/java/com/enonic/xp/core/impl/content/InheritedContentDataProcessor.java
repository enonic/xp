package com.enonic.xp.core.impl.content;

import java.util.EnumSet;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.NodeDataProcessor;
import com.enonic.xp.node.NodePath;

public abstract class InheritedContentDataProcessor
    implements NodeDataProcessor
{
    @Override
    public PropertyTree process( final PropertyTree originalData, final NodePath path )
    {
        final PropertyTree data = originalData.copy();

        if ( data.hasProperty( ContentPropertyNames.INHERIT ) )
        {
            boolean changed = false;
            final ImmutableSet.Builder<String> inheritSet = ImmutableSet.builder();

            for ( final String value : data.getStrings( ContentPropertyNames.INHERIT ) )
            {
                if ( !getTypesToProceed().contains( ContentInheritType.valueOf( value ) ) )
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
                data.removeProperties( ContentPropertyNames.INHERIT );
                data.addStrings( ContentPropertyNames.INHERIT, inheritSet.build() );
            }
        }

        return data;
    }

    protected abstract EnumSet<ContentInheritType> getTypesToProceed();
}
