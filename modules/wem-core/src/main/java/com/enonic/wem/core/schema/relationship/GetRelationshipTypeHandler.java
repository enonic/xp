package com.enonic.wem.core.schema.relationship;

import javax.inject.Inject;

import com.enonic.wem.api.command.schema.relationship.GetRelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNotFoundException;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.schema.relationship.dao.RelationshipTypeDao;


public final class GetRelationshipTypeHandler
    extends CommandHandler<GetRelationshipType>
{
    private RelationshipTypeDao relationshipTypeDao;

    @Override
    public void handle()
        throws Exception
    {
        final RelationshipTypeName selector = command.getName();
        final RelationshipType relationshipType = relationshipTypeDao.select( selector, context.getJcrSession() );
        if ( relationshipType == null )
        {
            if ( command.isNotFoundAsException() )
            {
                throw new RelationshipTypeNotFoundException( command.getName() );
            }
            else
            {
                command.setResult( null );
            }

        }

        command.setResult( relationshipType );
    }

    @Inject
    public void setRelationshipTypeDao( final RelationshipTypeDao relationshipTypeDao )
    {
        this.relationshipTypeDao = relationshipTypeDao;
    }
}
