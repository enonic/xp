package com.enonic.wem.core.content.relationship;

import javax.inject.Inject;
import javax.jcr.Session;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.relationship.UpdateRelationships;
import com.enonic.wem.api.command.content.relationship.UpdateRelationshipsResult;
import com.enonic.wem.api.content.relationship.Relationship;
import com.enonic.wem.api.content.relationship.RelationshipKey;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.relationship.dao.RelationshipDao;

import static com.enonic.wem.api.command.content.relationship.UpdateRelationshipsResult.newUpdateRelationshipsResult;

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

        final UpdateRelationshipsResult.Builder result = newUpdateRelationshipsResult();

        for ( RelationshipKey relationshipKey : command.getRelationshipKeys() )
        {
            Relationship existing = relationshipDao.select( relationshipKey, session );
            try
            {
                final Relationship changed = command.getEditor().edit( existing );
                existing.checkIllegalChange( changed );
                relationshipDao.update( changed, session );
                session.save();
                result.success( existing.getKey() );
            }
            catch ( Exception e )
            {
                result.failure( existing.getKey(), e );
            }
        }

        command.setResult( result.build() );
    }

    @Inject
    public void setRelationshipDao( final RelationshipDao relationshipDao )
    {
        this.relationshipDao = relationshipDao;
    }
}
