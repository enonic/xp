package com.enonic.wem.admin.rpc.schema;


import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.wem.admin.json.JsonErrorResult;
import com.enonic.wem.admin.jsonrpc.JsonRpcContext;
import com.enonic.wem.admin.rpc.AbstractDataRpcHandler;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.SchemaTypes;
import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.schema.SchemaKind;
import com.enonic.wem.api.schema.Schemas;


public final class ListSchemasRpcHandler
    extends AbstractDataRpcHandler
{
    public ListSchemasRpcHandler()
    {
        super( "schema_list" );
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
        final String searchFilter = context.param( "search" ).asString( "" ).trim();
        final Set<String> moduleNamesFilter = Sets.newHashSet( context.param( "modules" ).asStringArray() );

        final SchemaTypes command = Commands.schema().get();
        if ( !typesToInclude.isEmpty() )
        {
            command.includeTypes( typesToInclude );
        }

        Schemas schemas = client.execute( command );
        if ( !moduleNamesFilter.isEmpty() || !searchFilter.isEmpty() )
        {
            schemas = filter( schemas, moduleNamesFilter, searchFilter );
        }

        context.setResult( new ListSchemasRpcJsonResult( schemas ) );
    }

    private Schemas filter( final Schemas schemas, final Set<String> moduleNamesFilter, final String searchString )
    {
        final List<Schema> filteredList = Lists.newArrayList();
        for ( Schema schema : schemas )
        {
            if ( matchesSearchFilter( schema, searchString ) && matchesModuleFilter( schema, moduleNamesFilter ) )
            {
                filteredList.add( schema );
            }
        }
        return filteredList.size() == schemas.getSize() ? schemas : Schemas.from( filteredList );
    }

    private boolean matchesSearchFilter( final Schema schema, final String searchString )
    {
        final String schemaName = schema.getQualifiedName().toString().toLowerCase();
        final String displayName = Strings.nullToEmpty( schema.getDisplayName() ).toLowerCase();
        final String searchText = searchString.toLowerCase();
        return searchText.isEmpty() || schemaName.contains( searchText ) || displayName.contains( searchText );
    }

    private boolean matchesModuleFilter( final Schema schema, final Set<String> moduleNamesFilter )
    {
        return moduleNamesFilter.isEmpty() || moduleNamesFilter.contains( schema.getModuleName().toString() );
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
