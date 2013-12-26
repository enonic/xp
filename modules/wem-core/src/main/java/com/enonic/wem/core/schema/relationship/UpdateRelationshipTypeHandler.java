package com.enonic.wem.core.schema.relationship;

import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.command.schema.relationship.UpdateRelationshipType;
import com.enonic.wem.api.command.schema.relationship.UpdateRelationshipTypeResult;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.editor.RelationshipTypeEditor;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.schema.relationship.dao.RelationshipTypeDao;


public final class UpdateRelationshipTypeHandler
    extends CommandHandler<UpdateRelationshipType>
{
    private RelationshipTypeDao relationshipTypeDao;

    @Override
    public void handle()
        throws Exception
    {
        final Session session = context.getJcrSession();

        final RelationshipTypeEditor editor = command.getEditor();

        final RelationshipType original = relationshipTypeDao.select( command.getName(), session );

        final RelationshipType changed = editor.edit( original );
        if ( changed != null )
        {
            original.checkIllegalEdit( changed );
            relationshipTypeDao.update( changed, session );
            session.save();
            command.setResult( new UpdateRelationshipTypeResult( changed ) );
        }
        else
        {
            command.setResult( new UpdateRelationshipTypeResult( original ) );
        }

    }

    @Inject
    public void setRelationshipTypeDao( final RelationshipTypeDao relationshipTypeDao )
    {
        this.relationshipTypeDao = relationshipTypeDao;
    }
}
