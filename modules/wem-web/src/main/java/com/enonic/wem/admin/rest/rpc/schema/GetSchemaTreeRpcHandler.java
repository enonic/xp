package com.enonic.wem.admin.rest.rpc.schema;

import java.util.EnumSet;
import java.util.Set;

import com.enonic.wem.admin.json.JsonErrorResult;
import com.enonic.wem.admin.jsonrpc.JsonRpcContext;
import com.enonic.wem.admin.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.api.command.schema.GetSchemaTree;
import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.schema.SchemaKind;
import com.enonic.wem.api.support.tree.Tree;

import static com.enonic.wem.api.command.Commands.schema;


public final class GetSchemaTreeRpcHandler
    extends AbstractDataRpcHandler
{
    public GetSchemaTreeRpcHandler()
    {
        super( "schema_tree" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final Set<SchemaKind> typesToInclude;
        try
        {
            typesToInclude = getTypesToInclude( context );
        }
        catch ( IllegalArgumentException e )
        {
            context.setResult( new JsonErrorResult( "Invalid parameter 'types': [{0}]", context.param( "types" ).asString() ) );
            return;
        }
        final GetSchemaTree command = schema().getTree();
        if ( !typesToInclude.isEmpty() )
        {
            command.includeKind( typesToInclude );
        }

        final Tree<Schema> schemaTree = client.execute( command );

        context.setResult( new GetSchemaTreeJsonResult( schemaTree ) );
    }

    private Set<SchemaKind> getTypesToInclude( final JsonRpcContext context )
    {
        final String[] includeTypeParams = context.param( "types" ).asStringArray();
        final EnumSet<SchemaKind> types = EnumSet.noneOf( SchemaKind.class );
        for ( String includeTypeParam : includeTypeParams )
        {
            types.add( SchemaKind.valueOf( includeTypeParam.trim().toUpperCase() ) );
        }
        return types;
    }
}
