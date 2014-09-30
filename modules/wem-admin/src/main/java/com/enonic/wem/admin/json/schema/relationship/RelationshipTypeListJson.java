package com.enonic.wem.admin.json.schema.relationship;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.admin.rest.resource.schema.relationship.RelationshipTypeIconUrlResolver;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;

public class RelationshipTypeListJson
{
    private final ImmutableList<RelationshipTypeJson> list;

    public RelationshipTypeListJson( final RelationshipTypes relationshipTypes, final RelationshipTypeIconUrlResolver iconUrlResolver )
    {
        final ImmutableList.Builder<RelationshipTypeJson> builder = ImmutableList.builder();
        for ( final RelationshipType type : relationshipTypes.getList() )
        {
            builder.add( new RelationshipTypeJson( type, iconUrlResolver ) );
        }

        this.list = builder.build();
    }

    public int getTotal()
    {
        return this.list.size();
    }

    public List<RelationshipTypeJson> getRelationshipTypes()
    {
        return this.list;
    }
}
