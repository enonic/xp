package com.enonic.wem.admin.rest.resource.schema;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import com.enonic.wem.admin.json.schema.SchemaJson;
import com.enonic.wem.admin.rest.resource.schema.exception.InvalidSchemaTypeException;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.GetChildSchemas;
import com.enonic.wem.api.command.schema.SchemaTypes;
import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.schema.SchemaKey;
import com.enonic.wem.api.schema.SchemaKind;
import com.enonic.wem.api.schema.Schemas;

import static com.enonic.wem.api.command.Commands.schema;

@Path("schema")
@Produces(MediaType.APPLICATION_JSON)
public class SchemaResource
{
    private Client client;

    @GET
    @Path("list")
    public List<SchemaJson> list( @QueryParam("parentKey") final String parentName )
    {
        Schemas schemas;
        if ( StringUtils.isEmpty( parentName ) )
        {
            schemas = client.execute( Commands.schema().getRoots() );
        }
        else
        {
            final GetChildSchemas getSchemas = Commands.schema().getChildren().parentKey( SchemaKey.from( parentName ) );
            schemas = client.execute( getSchemas );
        }

        final List<SchemaJson> schemaJsonResult = new ArrayList<>();
        for ( Schema schema : schemas )
        {
            schemaJsonResult.add( SchemaJson.from( schema ) );
        }
        return schemaJsonResult;
    }

    @GET
    @Path("find")
    public List<SchemaJson> find( @QueryParam("search") String searchFilter, @QueryParam("modules") Set<String> moduleNamesFilter,
                                  @QueryParam("types") List<String> types )
    {
        final Set<SchemaKind> typesToInclude;
        try
        {
            typesToInclude = getTypesToInclude( types );
        }
        catch ( IllegalArgumentException e )
        {
            throw new InvalidSchemaTypeException( "Invalid parameter 'types': " + types );
        }

        final SchemaTypes command = schema().get();
        if ( !typesToInclude.isEmpty() )
        {
            command.includeTypes( typesToInclude );
        }

        Schemas schemas = client.execute( command );
        if ( !moduleNamesFilter.isEmpty() || !searchFilter.isEmpty() )
        {
            schemas = filter( schemas, moduleNamesFilter, searchFilter );
        }

        final List<SchemaJson> schemaJsonResult = new ArrayList<>();
        for ( Schema schema : schemas )
        {
            schemaJsonResult.add( SchemaJson.from( schema ) );
        }
        return schemaJsonResult;
    }

    private Set<SchemaKind> getTypesToInclude( List<String> includeTypeParams )
    {
        final EnumSet<SchemaKind> types = EnumSet.noneOf( SchemaKind.class );
        for ( String includeTypeParam : includeTypeParams )
        {
            types.add( SchemaKind.valueOf( includeTypeParam.trim().toUpperCase() ) );
        }
        return types;
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
        return true; // moduleNamesFilter.isEmpty() || moduleNamesFilter.contains( schema.getModuleName().toString() );
    }

    @Inject
    public void setClient( Client client )
    {
        this.client = client;
    }
}
