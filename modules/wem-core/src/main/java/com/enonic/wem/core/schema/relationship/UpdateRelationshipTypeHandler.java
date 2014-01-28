package com.enonic.wem.core.schema.relationship;

import javax.inject.Inject;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.relationship.UpdateRelationshipType;
import com.enonic.wem.api.command.schema.relationship.UpdateRelationshipTypeResult;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeAlreadyExistException;
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
        final RelationshipTypeEditor editor = command.getEditor();

        final RelationshipType original = context.getClient().execute( Commands.relationshipType().get().byName( command.getName() ) );

        final RelationshipType changed = editor.edit( original );
        if ( ( changed != null ) && ( changed != original ) )
        {
            original.checkIllegalEdit( changed );

            if ( !original.getName().equals( changed.getName() ) )
            {
                // renamed
                final RelationshipType existing =
                    context.getClient().execute( Commands.relationshipType().get().byName( changed.getName() ) );
                if ( existing != null )
                {
                    throw new RelationshipTypeAlreadyExistException( changed.getName() );
                }

                relationshipTypeDao.updateRelationshipType( changed );
                relationshipTypeDao.deleteRelationshipType( original.getName() );
            }
            else
            {
                relationshipTypeDao.updateRelationshipType( changed );
            }
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
