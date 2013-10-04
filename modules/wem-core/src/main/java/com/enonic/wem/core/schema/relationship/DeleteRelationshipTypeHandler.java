package com.enonic.wem.core.schema.relationship;

import javax.inject.Inject;

import com.enonic.wem.api.command.schema.relationship.DeleteRelationshipType;
import com.enonic.wem.api.command.schema.relationship.DeleteRelationshipTypeResult;
import com.enonic.wem.api.exception.RelationshipTypeNotFoundException;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.schema.relationship.dao.RelationshipTypeDao;


public final class DeleteRelationshipTypeHandler
    extends CommandHandler<DeleteRelationshipType>
{
    private RelationshipTypeDao relationshipTypeDao;

    @Override
    public void handle( final DeleteRelationshipType command )
        throws Exception
    {
        final QualifiedRelationshipTypeName relationshipTypeName = command.getQualifiedName();
        try
        {
            relationshipTypeDao.delete( relationshipTypeName, context.getJcrSession() );
            context.getJcrSession().save();
            command.setResult( DeleteRelationshipTypeResult.SUCCESS );
        }
        catch ( RelationshipTypeNotFoundException e )
        {
            command.setResult( DeleteRelationshipTypeResult.NOT_FOUND );
        }
    }

    @Inject
    public void setRelationshipTypeDao( final RelationshipTypeDao relationshipTypeDao )
    {
        this.relationshipTypeDao = relationshipTypeDao;
    }
}
