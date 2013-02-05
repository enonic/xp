package com.enonic.wem.core.content.relationshiptype;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.relationship.UpdateRelationshipTypes;
import com.enonic.wem.api.command.content.relationship.editor.RelationshipTypeEditor;
import com.enonic.wem.api.content.relationshiptype.RelationshipType;
import com.enonic.wem.api.content.relationshiptype.RelationshipTypes;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.relationshiptype.dao.RelationshipTypeDao;

@Component
public final class UpdateRelationshipTypesHandler
    extends CommandHandler<UpdateRelationshipTypes>
{
    private RelationshipTypeDao relationshipTypeDao;

    public UpdateRelationshipTypesHandler()
    {
        super( UpdateRelationshipTypes.class );
    }

    @Override
    public void handle( final CommandContext context, final UpdateRelationshipTypes command )
        throws Exception
    {
        final Session session = context.getJcrSession();

        final RelationshipTypeEditor editor = command.getEditor();

        final RelationshipTypes relationshipTypes = relationshipTypeDao.select( command.getSelectors(), session );
        int relationshipTypesUpdated = 0;
        for ( RelationshipType relationshipType : relationshipTypes )
        {
            final RelationshipType modifiedRelationshipType = editor.edit( relationshipType );
            if ( modifiedRelationshipType != null )
            {
                relationshipTypeDao.update( relationshipType, session );
                relationshipTypesUpdated++;
            }
        }

        session.save();
        command.setResult( relationshipTypesUpdated );
    }

    @Autowired
    public void setRelationshipTypeDao( final RelationshipTypeDao relationshipTypeDao )
    {
        this.relationshipTypeDao = relationshipTypeDao;
    }
}
