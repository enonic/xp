package com.enonic.wem.api.relationship.editor;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import com.enonic.wem.api.relationship.Relationship;

import static com.enonic.wem.api.relationship.Relationship.newRelationship;

final class SetRelationshipPropertiesEditor
    implements RelationshipEditor
{
    private final Map<String, String> source;

    SetRelationshipPropertiesEditor( final ImmutableMap<String, String> source )
    {
        this.source = source;
    }

    @Override
    public Relationship edit( final Relationship relationship )
        throws Exception
    {
        final Relationship.Builder builder = newRelationship( relationship );
        builder.properties( source );
        return builder.build();
    }

}
