package com.enonic.wem.core.content.relationship;

import javax.inject.Inject;
import javax.jcr.Session;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.relationship.UpdateRelationship;
import com.enonic.wem.api.content.relationship.Relationship;
import com.enonic.wem.api.content.relationship.UpdateRelationshipFailureException;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.relationship.dao.RelationshipDao;

import static com.enonic.wem.api.content.relationship.UpdateRelationshipFailureException.newUpdateRelationshipsResult;

@Component
public final class UpdateRelationshipHandler
    extends CommandHandler<UpdateRelationship>
{
    private RelationshipDao relationshipDao;

    public UpdateRelationshipHandler()
    {
        super( UpdateRelationship.class );
    }

    @Override
    public void handle( final CommandContext context, final UpdateRelationship command )
        throws Exception
    {
        final Session session = context.getJcrSession();

        final Relationship existing = relationshipDao.select( command.getRelationshipKey(), session );

        try
        {
            final Relationship changed = command.getEditor().edit( existing );
            existing.checkIllegalChange( changed );
            relationshipDao.update( changed, session );
            session.save();
        }
        catch ( Exception e )
        {
            final UpdateRelationshipFailureException.Builder result = newUpdateRelationshipsResult();
            result.relationshipKey( existing.getKey() );
            result.failure( e );
            throw result.build();
        }
    }

    @Inject
    public void setRelationshipDao( final RelationshipDao relationshipDao )
    {
        this.relationshipDao = relationshipDao;
    }
}
