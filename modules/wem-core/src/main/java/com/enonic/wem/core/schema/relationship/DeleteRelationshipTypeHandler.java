package com.enonic.wem.core.schema.relationship;

import javax.inject.Inject;

import com.enonic.wem.api.command.schema.relationship.DeleteRelationshipType;
import com.enonic.wem.api.command.schema.relationship.DeleteRelationshipTypeResult;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.schema.relationship.dao.RelationshipTypeDao;


public final class DeleteRelationshipTypeHandler
    extends CommandHandler<DeleteRelationshipType>
{
    private RelationshipTypeDao relationshipTypeDao;

    @Override
    public void handle()
        throws Exception
    {
        final RelationshipTypeName relationshipTypeName = command.getName();

        final RelationshipType.Builder deletedRelationshipType = relationshipTypeDao.getRelationshipType( command.getName() );
        relationshipTypeDao.deleteRelationshipType( relationshipTypeName );
        command.setResult( new DeleteRelationshipTypeResult( deletedRelationshipType != null ? deletedRelationshipType.build() : null ) );
    }

    @Inject
    public void setRelationshipTypeDao( final RelationshipTypeDao relationshipTypeDao )
    {
        this.relationshipTypeDao = relationshipTypeDao;
    }
}
