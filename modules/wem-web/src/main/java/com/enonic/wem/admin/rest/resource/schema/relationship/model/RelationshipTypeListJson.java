package com.enonic.wem.admin.rest.resource.schema.relationship.model;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;

public class RelationshipTypeListJson
{
    private final ImmutableList<RelationshipTypeResultJson> list;

    public RelationshipTypeListJson( final RelationshipTypes relationshipTypes )
    {
        final ImmutableList.Builder<RelationshipTypeResultJson> builder = ImmutableList.builder();
        for ( final RelationshipType type : relationshipTypes.getList() )
        {
            builder.add( new RelationshipTypeResultJson( type ) );
        }

        this.list = builder.build();
    }

    public int getTotal()
    {
        return this.list.size();
    }

    public List<RelationshipTypeResultJson> getRelationshipTypes()
    {
        return this.list;
    }
}
