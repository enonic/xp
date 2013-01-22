package com.enonic.wem.core.content.relationship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.relationship.DeleteRelationshipTypes;
import com.enonic.wem.api.command.content.relationship.RelationshipTypeDeletionResult;
import com.enonic.wem.api.content.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.relationship.dao.RelationshipTypeDao;

@Component
public final class DeleteRelationshipTypesHandler
    extends CommandHandler<DeleteRelationshipTypes>
{
    private RelationshipTypeDao relationshipTypeDao;

    public DeleteRelationshipTypesHandler()
    {
        super( DeleteRelationshipTypes.class );
    }

    @Override
    public void handle( final CommandContext context, final DeleteRelationshipTypes command )
        throws Exception
    {
        final RelationshipTypeDeletionResult relationshipTypeDeletionResult = new RelationshipTypeDeletionResult();

        for ( QualifiedRelationshipTypeName relationshipTypeName : command.getNames() )
        {
            try
            {
                relationshipTypeDao.deleteRelationshipType( relationshipTypeName, context.getJcrSession() );
                relationshipTypeDeletionResult.success( relationshipTypeName );
                context.getJcrSession().save();
            }
            catch ( SystemException e )
            {
                relationshipTypeDeletionResult.failure( relationshipTypeName, e );
            }
        }

        command.setResult( relationshipTypeDeletionResult );
    }

    @Autowired
    public void setRelationshipTypeDao( final RelationshipTypeDao relationshipTypeDao )
    {
        this.relationshipTypeDao = relationshipTypeDao;
    }
}
