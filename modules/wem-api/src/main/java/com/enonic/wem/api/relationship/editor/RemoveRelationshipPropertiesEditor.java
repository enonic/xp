package com.enonic.wem.api.relationship.editor;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.enonic.wem.api.relationship.Relationship;

final class RemoveRelationshipPropertiesEditor
    implements RelationshipEditor
{
    private final Set<String> source;

    RemoveRelationshipPropertiesEditor( final List<String> source )
    {
        this.source = Sets.newHashSet( source );
    }

    @Override
    public Relationship edit( final Relationship relationship )
        throws Exception
    {
        final Map<String, String> properties = Maps.newLinkedHashMap();

        for ( Map.Entry<String, String> property : relationship.getProperties().entrySet() )
        {
            if ( !source.contains( property.getKey() ) )
            {
                properties.put( property.getKey(), property.getValue() );
            }
        }

        final Relationship.Builder builder = Relationship.newRelationship( relationship );
        builder.properties( properties );
        return builder.build();
    }
}
