package com.enonic.wem.core.content.relationship;

import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.command.content.relationship.DeleteRelationship;
import com.enonic.wem.api.content.relationship.DeleteRelationshipResult;
import com.enonic.wem.api.content.relationship.RelationshipNotFoundException;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.relationship.dao.RelationshipDao;


public final class DeleteRelationshipHandler
    extends CommandHandler<DeleteRelationship>
{
    private RelationshipDao relationshipDao;

    public DeleteRelationshipHandler()
    {
        super( DeleteRelationship.class );
    }

    @Override
    public void handle( final CommandContext context, final DeleteRelationship command )
        throws Exception
    {
        final Session session = context.getJcrSession();

        try
        {
            relationshipDao.delete( command.getRelationshipKey(), session );
            session.save();
            command.setResult( DeleteRelationshipResult.SUCCESS );
        }
        catch ( RelationshipNotFoundException e )
        {
            command.setResult( DeleteRelationshipResult.from( e ) );
        }
    }

    @Inject
    public void setRelationshipDao( final RelationshipDao relationshipDao )
    {
        this.relationshipDao = relationshipDao;
    }
}
