package com.enonic.wem.core.schema.relationship;

import org.joda.time.DateTime;

import com.enonic.wem.api.command.schema.relationship.CreateRelationshipTypeParams;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.core.schema.relationship.dao.RelationshipTypeDao;

import static com.enonic.wem.api.schema.relationship.RelationshipType.newRelationshipType;


final class CreateRelationshipTypeCommand
{
    private RelationshipTypeDao relationshipTypeDao;

    private CreateRelationshipTypeParams params;

    RelationshipTypeName execute()
    {
        params.validate();

        return doExecute();
    }

    private RelationshipTypeName doExecute()
    {
        final DateTime currentTime = DateTime.now();

        final RelationshipType.Builder builder = newRelationshipType();
        builder.name( params.getName() );
        builder.displayName( params.getDisplayName() );
        builder.fromSemantic( params.getFromSemantic() );
        builder.toSemantic( params.getToSemantic() );
        if ( params.getAllowedFromTypes() != null )
        {
            builder.addAllowedFromTypes( params.getAllowedFromTypes() );
        }
        if ( params.getAllowedToTypes() != null )
        {
            builder.addAllowedToTypes( params.getAllowedToTypes() );
        }
        builder.createdTime( currentTime );
        builder.modifiedTime( currentTime );
        builder.icon( params.getSchemaIcon() );
        final RelationshipType relationshipType = builder.build();

        relationshipTypeDao.createRelationshipType( relationshipType );
        return relationshipType.getName();
    }

    CreateRelationshipTypeCommand relationshipTypeDao( final RelationshipTypeDao relationshipTypeDao )
    {
        this.relationshipTypeDao = relationshipTypeDao;
        return this;
    }

    CreateRelationshipTypeCommand params( final CreateRelationshipTypeParams params )
    {
        this.params = params;
        return this;
    }
}
