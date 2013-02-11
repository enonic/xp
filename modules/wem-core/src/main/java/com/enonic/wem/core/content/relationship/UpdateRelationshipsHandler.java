package com.enonic.wem.core.content.relationship;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.relationship.UpdateRelationships;
import com.enonic.wem.api.command.content.relationship.UpdateRelationshipsResult;
import com.enonic.wem.api.content.relationship.Relationship;
import com.enonic.wem.api.content.relationship.Relationships;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.relationship.dao.RelationshipDao;

@Component
public final class UpdateRelationshipsHandler
    extends CommandHandler<UpdateRelationships>
{
    private RelationshipDao relationshipDao;

    public UpdateRelationshipsHandler()
    {
        super( UpdateRelationships.class );
    }

    @Override
    public void handle( final CommandContext context, final UpdateRelationships command )
        throws Exception
    {
        final Session session = context.getJcrSession();

        final UpdateRelationshipsResult result = new UpdateRelationshipsResult();
        final Relationships relationships = relationshipDao.select( command.getRelationshipIds(), session );

        for ( Relationship existing : relationships )
        {
            try
            {
                final Relationship changed = command.getEditor().edit( existing );
                relationshipDao.update( changed, session );
                session.save();
                result.success( existing.getId() );
            }
            catch ( Exception e )
            {
                result.failure( existing.getId(), e );
            }
        }
        command.setResult( result );
    }

    @Autowired
    public void setRelationshipDao( final RelationshipDao relationshipDao )
    {
        this.relationshipDao = relationshipDao;
    }
}
