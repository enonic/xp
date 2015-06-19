package com.enonic.xp.relationship.editor;

import java.util.Map;

import com.enonic.xp.relationship.Relationship;

final class AddRelationshipPropertiesEditor
    implements RelationshipEditor
{
    private final Map<String, String> source;

    AddRelationshipPropertiesEditor( final Map<String, String> source )
    {
        this.source = source;
    }

    @Override
    public Relationship edit( final Relationship relationship )
    {
        final Relationship.Builder builder = Relationship.create( relationship );
        for ( Map.Entry<String, String> property : source.entrySet() )
        {
            builder.property( property.getKey(), property.getValue() );
        }
        return builder.build();
    }
}
