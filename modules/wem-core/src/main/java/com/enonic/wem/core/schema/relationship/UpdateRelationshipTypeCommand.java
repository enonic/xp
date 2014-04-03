package com.enonic.wem.core.schema.relationship;

import com.enonic.wem.api.command.schema.relationship.GetRelationshipTypeParams;
import com.enonic.wem.api.command.schema.relationship.UpdateRelationshipTypeParams;
import com.enonic.wem.api.command.schema.relationship.UpdateRelationshipTypeResult;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeAlreadyExistException;
import com.enonic.wem.api.schema.relationship.editor.RelationshipTypeEditor;
import com.enonic.wem.core.schema.relationship.dao.RelationshipTypeDao;


final class UpdateRelationshipTypeCommand
{
    private RelationshipTypeDao relationshipTypeDao;

    private UpdateRelationshipTypeParams params;

    UpdateRelationshipTypeResult execute()
    {
        params.validate();

        return doExecute();
    }

    private UpdateRelationshipTypeResult doExecute()
    {
        final RelationshipTypeEditor editor = params.getEditor();

        final RelationshipType original = new GetRelationshipTypeCommand().
            params( new GetRelationshipTypeParams().name( params.getName() ) ).
            relationshipTypeDao( this.relationshipTypeDao ).
            execute();

        final RelationshipType changed = editor.edit( original );
        if ( ( changed != null ) && ( changed != original ) )
        {
            original.checkIllegalEdit( changed );

            if ( !original.getName().equals( changed.getName() ) )
            {
                // renamed
                final RelationshipType existing = new GetRelationshipTypeCommand().
                    params( new GetRelationshipTypeParams().name( changed.getName() ) ).
                    relationshipTypeDao( this.relationshipTypeDao ).
                    execute();
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
            return new UpdateRelationshipTypeResult( changed );
        }
        else
        {
            return new UpdateRelationshipTypeResult( original );
        }
    }

    UpdateRelationshipTypeCommand relationshipTypeDao( final RelationshipTypeDao relationshipTypeDao )
    {
        this.relationshipTypeDao = relationshipTypeDao;
        return this;
    }

    UpdateRelationshipTypeCommand params( final UpdateRelationshipTypeParams params )
    {
        this.params = params;
        return this;
    }
}
