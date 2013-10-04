package com.enonic.wem.core.relationship;

import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.command.relationship.GetRelationships;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.relationship.dao.RelationshipDao;


public final class GetRelationshipsHandler
    extends CommandHandler<GetRelationships>
{
    private RelationshipDao relationshipDao;

    @Override
    public void handle( final CommandContext context, final GetRelationships command )
        throws Exception
    {
        final Session session = context.getJcrSession();

        command.setResult( relationshipDao.selectFromContent( command.getFromContent(), session ) );
    }

    @Inject
    public void setRelationshipDao( final RelationshipDao relationshipDao )
    {
        this.relationshipDao = relationshipDao;
    }
}
