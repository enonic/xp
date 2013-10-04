package com.enonic.wem.core.schema.relationship;

import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.command.schema.relationship.UpdateRelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.editor.RelationshipTypeEditor;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.schema.relationship.dao.RelationshipTypeDao;


public final class UpdateRelationshipTypesHandler
    extends CommandHandler<UpdateRelationshipType>
{
    private RelationshipTypeDao relationshipTypeDao;

    @Override
    public void handle( final CommandContext context, final UpdateRelationshipType command )
        throws Exception
    {
        final Session session = context.getJcrSession();

        final RelationshipTypeEditor editor = command.getEditor();

        final RelationshipType existing = relationshipTypeDao.select( command.getQualifiedName(), session );

        final RelationshipType changed = editor.edit( existing );
        if ( changed != null )
        {
            existing.checkIllegalEdit( changed );
            relationshipTypeDao.update( changed, session );
            session.save();
            command.setResult( Boolean.TRUE );
        }
        else
        {
            command.setResult( Boolean.FALSE );
        }

    }

    @Inject
    public void setRelationshipTypeDao( final RelationshipTypeDao relationshipTypeDao )
    {
        this.relationshipTypeDao = relationshipTypeDao;
    }
}
