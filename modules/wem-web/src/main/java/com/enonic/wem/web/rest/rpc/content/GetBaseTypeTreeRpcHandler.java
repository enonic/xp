package com.enonic.wem.web.rest.rpc.content;

import java.util.EnumSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.GetBaseTypeTree;
import com.enonic.wem.api.content.BaseType;
import com.enonic.wem.api.content.BaseTypeKind;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

import static com.enonic.wem.api.command.Commands.baseType;

@Component
public final class GetBaseTypeTreeRpcHandler
    extends AbstractDataRpcHandler
{
    public GetBaseTypeTreeRpcHandler()
    {
        super( "baseType_tree" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final Set<BaseTypeKind> typesToInclude;
        try
        {
            typesToInclude = getTypesToInclude( context );
        }
        catch ( IllegalArgumentException e )
        {
            context.setResult( new JsonErrorResult( "Invalid parameter 'types': [{0}]", context.param( "types" ).asString() ) );
            return;
        }
        final GetBaseTypeTree command = baseType().getTree();
        if ( !typesToInclude.isEmpty() )
        {
            command.includeTypes( typesToInclude );
        }

        final Tree<BaseType> baseTypeTree = client.execute( command );

        context.setResult( new GetBaseTypeTreeJsonResult( baseTypeTree ) );
    }

    private Set<BaseTypeKind> getTypesToInclude( final JsonRpcContext context )
    {
        final String[] includeTypeParams = context.param( "types" ).asStringArray();
        final EnumSet<BaseTypeKind> types = EnumSet.noneOf( BaseTypeKind.class );
        for ( String includeTypeParam : includeTypeParams )
        {
            types.add( BaseTypeKind.valueOf( includeTypeParam.trim().toUpperCase() ) );
        }
        return types;
    }
}
