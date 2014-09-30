package com.enonic.wem.core.schema.relationship;

import com.enonic.wem.api.schema.relationship.GetRelationshipTypeParams;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeService;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;
import com.enonic.wem.core.schema.relationship.dao.RelationshipTypeDao;

public class RelationshipTypeServiceImpl
    implements RelationshipTypeService
{
    private RelationshipTypeDao relationshipTypeDao;

    @Override
    public RelationshipTypes getAll()
    {
        return relationshipTypeDao.getAllRelationshipTypes();
    }

    @Override
    public RelationshipType getByName( final GetRelationshipTypeParams params )
    {
        return new GetRelationshipTypeCommand().params( params ).relationshipTypeDao( relationshipTypeDao ).execute();
    }

    public void setRelationshipTypeDao( final RelationshipTypeDao relationshipTypeDao )
    {
        this.relationshipTypeDao = relationshipTypeDao;
    }
}
