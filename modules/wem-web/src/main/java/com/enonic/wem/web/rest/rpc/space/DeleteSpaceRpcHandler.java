package com.enonic.wem.web.rest.rpc.space;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import com.enonic.wem.api.command.space.DeleteSpace;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.api.space.SpaceNames;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

import static com.enonic.wem.api.command.Commands.space;

@Component
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
