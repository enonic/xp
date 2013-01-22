package com.enonic.wem.core.content.relationship;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.relation.UpdateRelationshipTypes;
import com.enonic.wem.api.command.content.relation.editor.RelationshipTypeEditor;
import com.enonic.wem.api.content.relation.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.relation.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.content.relation.RelationshipType;
import com.enonic.wem.api.content.relation.RelationshipTypes;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.relationship.dao.RelationshipTypeDao;

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
        final QualifiedRelationshipTypeNames relationshipTypeNames = command.getQualifiedNames();
        final RelationshipTypeEditor editor = command.getEditor();
        final Session session = context.getJcrSession();
        int relationshipTypesUpdated = 0;
        for ( QualifiedRelationshipTypeName relationshipTypeName : relationshipTypeNames )
        {
            final RelationshipType relationshipType = retrieveRelationshipType( session, relationshipTypeName );
            if ( relationshipType != null )
            {
                final RelationshipType modifiedRelationshipType = editor.edit( relationshipType );
                if ( modifiedRelationshipType != null )
                {
                    updateRelationshipType( session, modifiedRelationshipType );
                    relationshipTypesUpdated++;
                }
            }
        }

        session.save();
        command.setResult( relationshipTypesUpdated );
    }

    private void updateRelationshipType( final Session session, final RelationshipType relationshipType )
    {
        relationshipTypeDao.updateRelationshipType( relationshipType, session );
    }

    private RelationshipType retrieveRelationshipType( final Session session, final QualifiedRelationshipTypeName relationshipTypeName )
    {
        final RelationshipTypes relationshipTypes =
            relationshipTypeDao.retrieveRelationshipTypes( QualifiedRelationshipTypeNames.from( relationshipTypeName ), session );
        return relationshipTypes.isEmpty() ? null : relationshipTypes.first();
    }

    @Autowired
    public void setRelationshipTypeDao( final RelationshipTypeDao relationshipTypeDao )
    {
        this.relationshipTypeDao = relationshipTypeDao;
    }
}
