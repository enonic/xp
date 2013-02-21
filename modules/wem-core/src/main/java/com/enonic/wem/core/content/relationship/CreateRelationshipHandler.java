package com.enonic.wem.core.content.relationship;

import javax.jcr.Session;

import org.joda.time.DateTime;
import javax.inject.Inject;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.content.relationship.CreateRelationship;
import com.enonic.wem.api.content.relationship.Relationship;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.relationship.dao.RelationshipDao;

import static com.enonic.wem.api.content.relationship.Relationship.newRelationship;

@Component
public final class CreateRelationshipHandler
    extends CommandHandler<CreateRelationship>
{
    private RelationshipDao relationshipDao;

    public CreateRelationshipHandler()
    {
        super( CreateRelationship.class );
    }

    @Override
    public void handle( final CommandContext context, final CreateRelationship command )
        throws Exception
    {
        final Relationship.Builder builder = newRelationship();
        builder.creator( AccountKey.anonymous() );
        builder.createdTime( DateTime.now() );
        builder.type( command.getType() );
        builder.fromContent( command.getFromContent() );
        builder.toContent( command.getToContent() );
        builder.properties( command.getProperties() );
        if ( command.isManaged() )
        {
            builder.managed( command.getManagingData() );
        }
        final Relationship relationship = builder.build();

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
