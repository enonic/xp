package com.enonic.wem.admin.json.schema.relationship;

import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.api.schema.relationship.RelationshipType;

public class RelationshipTypeJson
    extends AbstractRelationshipTypeJson
    implements ItemJson
{
    private final RelationshipTypeResultJson relationshipType;

    private final boolean editable;

    private final boolean deletable;

    public RelationshipTypeJson( final RelationshipType type )
    {
        this.relationshipType = new RelationshipTypeResultJson( type );

        this.editable = true;
        this.deletable = true;
    }

    public RelationshipTypeResultJson getRelationshipType()
    {
        return this.relationshipType;
    }

    @Override
    public boolean getEditable()
    {
        return editable;
    }

    @Override
    public boolean getDeletable()
    {
        return deletable;
    }
}
