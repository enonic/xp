package com.enonic.wem.web.rest.rpc.content;


import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.schema.GetBaseTypes;
import com.enonic.wem.api.content.BaseTypeKind;
import com.enonic.wem.api.content.schema.BaseType;
import com.enonic.wem.api.content.schema.BaseTypes;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

@Component
public final class ListBaseTypesRpcHandler
    extends AbstractDataRpcHandler
{
    public ListBaseTypesRpcHandler()
    {
        super( "baseType_list" );
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
        final String searchFilter = context.param( "search" ).asString( "" ).trim();
        final Set<String> moduleNamesFilter = Sets.newHashSet( context.param( "modules" ).asStringArray() );

        final GetBaseTypes command = Commands.baseType().get();
        if ( !typesToInclude.isEmpty() )
        {
            command.includeTypes( typesToInclude );
        }

        BaseTypes baseTypes = client.execute( command );
        if ( !moduleNamesFilter.isEmpty() || !searchFilter.isEmpty() )
        {
            baseTypes = filter( baseTypes, moduleNamesFilter, searchFilter );
        }

        context.setResult( new ListBaseTypesRpcJsonResult( baseTypes ) );
    }

    private BaseTypes filter( final BaseTypes baseTypes, final Set<String> moduleNamesFilter, final String searchString )
    {
        final List<BaseType> filteredList = Lists.newArrayList();
        for ( BaseType baseType : baseTypes )
        {
            if ( matchesSearchFilter( baseType, searchString ) && matchesModuleFilter( baseType, moduleNamesFilter ) )
            {
                filteredList.add( baseType );
            }
        }
        return filteredList.size() == baseTypes.getSize() ? baseTypes : BaseTypes.from( filteredList );
    }

    private boolean matchesSearchFilter( final BaseType baseType, final String searchString )
    {
        final String baseTypeName = baseType.getQualifiedName().toString().toLowerCase();
        final String displayName = Strings.nullToEmpty( baseType.getDisplayName() ).toLowerCase();
        final String searchText = searchString.toLowerCase();
        return searchText.isEmpty() || baseTypeName.contains( searchText ) || displayName.contains( searchText );
    }

    private boolean matchesModuleFilter( final BaseType baseType, final Set<String> moduleNamesFilter )
    {
        return moduleNamesFilter.isEmpty() || moduleNamesFilter.contains( baseType.getModuleName().toString() );
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
