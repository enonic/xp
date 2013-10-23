package com.enonic.wem.admin.rpc.space;


import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

import com.enonic.wem.admin.json.JsonErrorResult;
import com.enonic.wem.admin.jsonrpc.JsonRpcContext;
import com.enonic.wem.admin.rpc.AbstractDataRpcHandler;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.api.space.SpaceNames;
import com.enonic.wem.api.space.Spaces;


public final class GetSpaceRpcHandler
    extends AbstractDataRpcHandler
{

    public GetSpaceRpcHandler()
    {
        super( "space_get" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String[] namesParam = context.param( "spaceNames" ).notBlank().asStringArray();
        final SpaceNames requestedSpaces = SpaceNames.from( namesParam );
        final Spaces spaces = client.execute( Commands.space().get().names( requestedSpaces ) );
        if ( requestedSpaces.getSize() == spaces.getSize() )
        {
            context.setResult( new GetSpacesJsonResult( spaces ) );
        }
        else
        {
            final ImmutableSet<SpaceName> spacesFound = spaces.getNames();

            final String[] notFoundSpaces = FluentIterable.
                from( requestedSpaces ).
                filter( new Predicate<SpaceName>()
                {
                    public boolean apply( final SpaceName requestedSpace )
                    {
                        return !spacesFound.contains( requestedSpace );
                    }
                } ).
                transform( new Function<SpaceName, String>()
                {
                    public String apply( final SpaceName space )
                    {
                        return space.name();
                    }
                } ).
                toArray( String.class );

            final String missingSpaces = Joiner.on( "," ).join( notFoundSpaces );
            context.setResult( new JsonErrorResult( "Spaces [{0}] not found", missingSpaces ) );
        }
    }
}
