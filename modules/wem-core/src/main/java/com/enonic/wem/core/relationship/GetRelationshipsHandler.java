package com.enonic.wem.core.relationship;

import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.command.relationship.GetRelationships;
import com.enonic.wem.api.relationship.RelationshipNotFoundException;
import com.enonic.wem.api.relationship.Relationships;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.relationship.dao.RelationshipDao;


public final class GetRelationshipsHandler
    extends CommandHandler<GetRelationships>
{
    private RelationshipDao relationshipDao;

    @Override
    public void handle()
        throws Exception
    {
        final Session session = context.getJcrSession();
        final Relationships relationships = relationshipDao.selectFromContent( command.getFromContent(), session );

        if ( relationships == null )
        {
            throw new RelationshipNotFoundException( relationships );
        }

        command.setResult( relationships );
    }

    @Inject
    public void setRelationshipDao( final RelationshipDao relationshipDao )
    {
        this.relationshipDao = relationshipDao;
    }
}
