package com.enonic.wem.core.content.schema.relationshiptype;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.schema.relationshiptype.DeleteRelationshipTypes;
import com.enonic.wem.api.command.content.schema.relationshiptype.RelationshipTypeDeletionResult;
import com.enonic.wem.api.content.schema.relationshiptype.QualifiedRelationshipTypeName;
import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.schema.relationshiptype.dao.RelationshipTypeDao;

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

        for ( QualifiedRelationshipTypeName relationshipTypeName : command.getQualifiedNames() )
        {
            try
            {
                relationshipTypeDao.delete( relationshipTypeName, context.getJcrSession() );
                context.getJcrSession().save();
                relationshipTypeDeletionResult.success( relationshipTypeName );
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
