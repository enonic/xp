package com.enonic.wem.core.relationship;

import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.command.relationship.DeleteRelationship;
import com.enonic.wem.api.relationship.DeleteRelationshipResult;
import com.enonic.wem.api.relationship.RelationshipNotFoundException;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.relationship.dao.RelationshipDao;


public final class DeleteRelationshipHandler
    extends CommandHandler<DeleteRelationship>
{
    private RelationshipDao relationshipDao;

    @Override
    public void handle( final DeleteRelationship command )
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
