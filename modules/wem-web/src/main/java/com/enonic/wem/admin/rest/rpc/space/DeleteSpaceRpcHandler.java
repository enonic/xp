package com.enonic.wem.admin.rest.rpc.space;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import com.enonic.wem.admin.json.rpc.JsonRpcContext;
import com.enonic.wem.admin.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.api.command.space.DeleteSpace;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.api.space.SpaceNames;

import static com.enonic.wem.api.command.Commands.space;


public final class DeleteSpaceRpcHandler
    extends AbstractDataRpcHandler
{
    public DeleteSpaceRpcHandler()
    {
        super( "space_delete" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String[] spaceNameValues = context.param( "spaceName" ).notBlank().asStringArray();
        final SpaceNames spaceNames = SpaceNames.from( spaceNameValues );
        final List<SpaceName> notDeleted = Lists.newArrayList();
        boolean success = true;
        for ( SpaceName spaceName : spaceNames )
        {
            final DeleteSpace deleteSpace = space().delete().name( spaceName );
            boolean deleted = client.execute( deleteSpace );
            if ( !deleted )
            {
                notDeleted.add( spaceName );
                success = false;
            }
        }

        if ( success )
        {
            context.setResult( DeleteSpaceJsonResult.success() );
        }
        else
        {
            final String spacesNotDeleted = Joiner.on( ", " ).join( spaceNames );
            context.setResult( DeleteSpaceJsonResult.failure( String.format( "Space [%s] not found", spacesNotDeleted ) ) );
        }
    }
}
