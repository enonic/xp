package com.enonic.wem.core.content.relationshiptype;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.relationshiptype.UpdateRelationshipType;
import com.enonic.wem.api.content.relationshiptype.RelationshipType;
import com.enonic.wem.api.content.relationshiptype.editor.RelationshipTypeEditor;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.relationshiptype.dao.RelationshipTypeDao;

@Component
public final class UpdateRelationshipTypesHandler
    extends CommandHandler<UpdateRelationshipType>
{
    private RelationshipTypeDao relationshipTypeDao;

    public UpdateRelationshipTypesHandler()
    {
        super( UpdateRelationshipType.class );
    }

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
            existing.checkIllegalChange( changed );
            relationshipTypeDao.update( changed, session );
            session.save();
            command.setResult( Boolean.TRUE );
        }
        else
        {
            command.setResult( Boolean.FALSE );
        }

    }

    @Autowired
    public void setRelationshipTypeDao( final RelationshipTypeDao relationshipTypeDao )
    {
        this.relationshipTypeDao = relationshipTypeDao;
    }
}
