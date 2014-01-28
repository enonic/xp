package com.enonic.wem.core.relationship;

import javax.inject.Inject;
import javax.jcr.Session;

import org.joda.time.DateTime;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.relationship.CreateRelationship;
import com.enonic.wem.api.relationship.Relationship;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.relationship.dao.RelationshipDao;

import static com.enonic.wem.api.relationship.Relationship.newRelationship;


public final class CreateRelationshipHandler
    extends CommandHandler<CreateRelationship>
{
    private RelationshipDao relationshipDao;

    @Override
    public void handle()
        throws Exception
    {
        final Relationship relationship = newRelationship().
            creator( AccountKey.anonymous() ).
            createdTime( DateTime.now() ).
            type( command.getType() ).
            fromContent( command.getFromContent() ).
            toContent( command.getToContent() ).
            properties( command.getProperties() ).
            createdTime( DateTime.now() ).
            build();

        final Session session = context.getJcrSession();
        relationshipDao.create( relationship, session );
        session.save();
        command.setResult( relationship.getId() );
    }

    @Inject
    public void setRelationshipDao( final RelationshipDao relationshipDao )
    {
        this.relationshipDao = relationshipDao;
    }
}
