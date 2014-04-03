package com.enonic.wem.core.schema.relationship;

import com.enonic.wem.api.command.schema.relationship.DeleteRelationshipTypeResult;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.core.schema.relationship.dao.RelationshipTypeDao;


final class DeleteRelationshipTypeCommand
{
    private RelationshipTypeDao relationshipTypeDao;

    private RelationshipTypeName relationshipTypeName;

    DeleteRelationshipTypeResult execute()
    {
        final RelationshipType.Builder deletedRelationshipType = relationshipTypeDao.getRelationshipType( relationshipTypeName );
        relationshipTypeDao.deleteRelationshipType( relationshipTypeName );

        return new DeleteRelationshipTypeResult( deletedRelationshipType != null ? deletedRelationshipType.build() : null );
    }

    DeleteRelationshipTypeCommand relationshipTypeDao( final RelationshipTypeDao relationshipTypeDao )
    {
        this.relationshipTypeDao = relationshipTypeDao;
        return this;
    }

    DeleteRelationshipTypeCommand name( final RelationshipTypeName name )
    {
        this.relationshipTypeName = name;
        return this;
    }
}
