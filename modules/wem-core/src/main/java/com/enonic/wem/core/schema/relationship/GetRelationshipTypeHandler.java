package com.enonic.wem.core.schema.relationship;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.relationship.GetRelationshipType;
import com.enonic.wem.api.entity.NoEntityFoundException;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNotFoundException;
import com.enonic.wem.core.command.CommandHandler;


public final class GetRelationshipTypeHandler
    extends CommandHandler<GetRelationshipType>
{
    @Override
    public void handle()
        throws Exception
    {
        try
        {
            final RelationshipType relationshipType = context.getClient().execute( Commands.relationshipType().get().byName( command.getName() ) );
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
}
