package com.enonic.wem.core.content.schema.relationship;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.schema.relationship.DeleteRelationshipType;
import com.enonic.wem.api.command.content.schema.relationship.DeleteRelationshipTypeResult;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.api.exception.RelationshipTypeNotFoundException;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.schema.relationship.dao.RelationshipTypeDao;

@Component
public final class DeleteRelationshipTypeHandler
    extends CommandHandler<DeleteRelationshipType>
{
    private RelationshipTypeDao relationshipTypeDao;

    public DeleteRelationshipTypeHandler()
    {
        super( DeleteRelationshipType.class );
    }

    @Override
    public void handle( final CommandContext context, final DeleteRelationshipType command )
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
