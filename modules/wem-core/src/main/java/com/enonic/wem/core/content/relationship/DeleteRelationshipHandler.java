package com.enonic.wem.core.content.relationship;

import javax.inject.Inject;
import javax.jcr.Session;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.relationship.DeleteRelationships;
import com.enonic.wem.api.command.content.relationship.DeleteRelationshipsResult;
import com.enonic.wem.api.content.relationship.RelationshipKey;
import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.relationship.dao.RelationshipDao;

@Component
public final class DeleteRelationshipHandler
    extends CommandHandler<DeleteRelationships>
{
    private RelationshipDao relationshipDao;

    public DeleteRelationshipHandler()
    {
        super( DeleteRelationships.class );
    }

    @Override
    public void handle( final CommandContext context, final DeleteRelationships command )
        throws Exception
    {
        final DeleteRelationshipsResult result = new DeleteRelationshipsResult();
        final Session session = context.getJcrSession();

        for ( RelationshipKey relationshipKey : command.getRelationshipKeys() )
        {
            try
            {
                relationshipDao.delete( relationshipKey, session );
                session.save();
                result.success( relationshipKey );
            }
            catch ( SystemException e )
            {
                result.failure( relationshipKey, e );
            }
        }

        command.setResult( result );
    }

    @Inject
    public void setRelationshipDao( final RelationshipDao relationshipDao )
    {
        this.relationshipDao = relationshipDao;
    }
}
