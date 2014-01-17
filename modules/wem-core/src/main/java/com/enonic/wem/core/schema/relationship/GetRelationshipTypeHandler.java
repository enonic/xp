package com.enonic.wem.core.schema.relationship;

import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.command.schema.relationship.GetRelationshipType;
import com.enonic.wem.api.entity.NoEntityFoundException;
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
        try
        {
            final Session session = context.getJcrSession();
            final RelationshipTypeName selector = command.getName();
            final RelationshipType relationshipType = relationshipTypeDao.select( selector, session );

            command.setResult( relationshipType );
        }
        catch ( NoEntityFoundException e )
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
    }

    @Inject
    public void setRelationshipTypeDao( final RelationshipTypeDao relationshipTypeDao )
    {
        this.relationshipTypeDao = relationshipTypeDao;
    }
}
