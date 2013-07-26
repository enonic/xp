package com.enonic.wem.admin.rest.resource.relationship.model;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.relationship.Relationship;
import com.enonic.wem.api.relationship.Relationships;

public class RelationshipListJson
{
    private final ImmutableList<RelationshipJson> list;

    public RelationshipListJson( final Relationships relationships )
    {
        final ImmutableList.Builder<RelationshipJson> builder = ImmutableList.builder();
        for ( final Relationship model : relationships )
        {
            builder.add( new RelationshipJson( model ) );
        }

        this.list = builder.build();
    }

    public int getTotal()
    {
        return this.list.size();
    }

    public List<RelationshipJson> getRelationships()
    {
        return this.list;
    }
}
