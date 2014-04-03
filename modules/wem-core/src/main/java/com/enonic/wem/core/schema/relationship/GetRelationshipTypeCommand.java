package com.enonic.wem.core.schema.relationship;

import com.enonic.wem.api.schema.relationship.GetRelationshipTypeParams;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNotFoundException;
import com.enonic.wem.core.schema.relationship.dao.RelationshipTypeDao;


final class GetRelationshipTypeCommand
{
    private RelationshipTypeDao relationshipTypeDao;

    private GetRelationshipTypeParams params;

    RelationshipType execute()
    {
        params.validate();

        return doExecute();
    }

    private RelationshipType doExecute()
    {
        final RelationshipTypeName selector = params.getName();
        final RelationshipType.Builder relationshipType = relationshipTypeDao.getRelationshipType( selector );
        if ( relationshipType == null )
        {
            if ( params.isNotFoundAsException() )
            {
                throw new RelationshipTypeNotFoundException( params.getName() );
            }
            else
            {
                return null;
            }
        }
        else
        {
            return relationshipType.build();
        }
    }

    GetRelationshipTypeCommand relationshipTypeDao( final RelationshipTypeDao relationshipTypeDao )
    {
        this.relationshipTypeDao = relationshipTypeDao;
        return this;
    }

    GetRelationshipTypeCommand params( final GetRelationshipTypeParams params )
    {
        this.params = params;
        return this;
    }
}
