package com.enonic.xp.impl.server.rest;

import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.MediaType;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.CreateNamespaceParams;
import com.enonic.xp.app.Namespace;
import com.enonic.xp.app.NamespaceNotFoundException;
import com.enonic.xp.app.UpdateNamespaceParams;
import com.enonic.xp.core.internal.json.ObjectMapperHelper;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.exception.DuplicateElementException;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.exception.NotFoundException;
import com.enonic.xp.impl.server.rest.model.AppResourceJson;
import com.enonic.xp.impl.server.rest.model.ComponentResourceJson;
import com.enonic.xp.impl.server.rest.model.ErrorJson;
import com.enonic.xp.impl.server.rest.model.MacroResourceJson;
import com.enonic.xp.impl.server.rest.model.NamespaceJson;
import com.enonic.xp.impl.server.rest.model.NamespaceParamsJson;
import com.enonic.xp.impl.server.rest.model.PhrasesResourceJson;
import com.enonic.xp.impl.server.rest.model.SchemaResourceJson;
import com.enonic.xp.impl.server.rest.model.SchemaResourceParamsJson;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.portal.handler.WebHandlerHelper;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.resource.CreateDynamicCmsParams;
import com.enonic.xp.resource.CreateDynamicComponentParams;
import com.enonic.xp.resource.CreateDynamicContentSchemaParams;
import com.enonic.xp.resource.CreateDynamicMacroParams;
import com.enonic.xp.resource.CreateDynamicPhrasesParams;
import com.enonic.xp.resource.CreateDynamicStylesParams;
import com.enonic.xp.resource.DeleteDynamicComponentParams;
import com.enonic.xp.resource.DeleteDynamicContentSchemaParams;
import com.enonic.xp.resource.DeleteDynamicMacroParams;
import com.enonic.xp.resource.DeleteDynamicPhrasesParams;
import com.enonic.xp.resource.DynamicComponentType;
import com.enonic.xp.resource.DynamicContentSchemaType;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.GetDynamicComponentParams;
import com.enonic.xp.resource.GetDynamicContentSchemaParams;
import com.enonic.xp.resource.GetDynamicMacroParams;
import com.enonic.xp.resource.GetDynamicPhrasesParams;
import com.enonic.xp.resource.ListDynamicComponentsParams;
import com.enonic.xp.resource.ListDynamicContentSchemasParams;
import com.enonic.xp.resource.ListDynamicMacrosParams;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.SchemaService;
import com.enonic.xp.resource.UpdateDynamicCmsParams;
import com.enonic.xp.resource.UpdateDynamicComponentParams;
import com.enonic.xp.resource.UpdateDynamicContentSchemaParams;
import com.enonic.xp.resource.UpdateDynamicMacroParams;
import com.enonic.xp.resource.UpdateDynamicPhrasesParams;
import com.enonic.xp.resource.UpdateDynamicStylesParams;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.schema.BaseSchemaName;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.style.StyleDescriptor;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

@Component(property = {"key=server:schema", "title=Schemas API", "mount=management", "allowedPrincipals=role:system.admin",
    "allowedPrincipals=role:system.schema.admin", "allowedPrincipals=role:cms.admin"})
public class SchemaApiHandler
    implements UniversalApiHandler
{
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperHelper.create();

    private static final String SCHEMA_API = "server:schema";

    private final SchemaService schemaService;

    @Activate
    public SchemaApiHandler( @Reference final SchemaService schemaService )
    {
        this.schemaService = schemaService;
    }

    @Override
    public WebResponse handle( final WebRequest request )
    {
        final String apiPath = WebHandlerHelper.findApiPath( request, SCHEMA_API );
        final String[] segments = Arrays.stream( apiPath.split( "/" ) ).filter( Predicate.not( String::isEmpty ) ).toArray( String[]::new );

        if ( segments.length == 0 )
        {
            return notFound();
        }

        try
        {
            return switch ( segments[0] )
            {
                case "apps" -> handleApps( request, segments );
                case "namespaces" -> handleNamespaces( request, segments );
                case "schemas" -> handleContentSchemas( request, segments );
                case "components" -> handleComponents( request, segments );
                case "macros" -> handleMacros( request, segments );
                case "styles" -> handleStyles( request, segments );
                case "cms" -> handleCms( request, segments );
                case "phrases" -> handlePhrases( request, segments );
                default -> notFound();
            };
        }
        catch ( ForbiddenAccessException e )
        {
            return errorResponse( HttpStatus.FORBIDDEN, e.getMessage() );
        }
        catch ( NamespaceNotFoundException | NotFoundException e )
        {
            return errorResponse( HttpStatus.NOT_FOUND, e.getMessage() );
        }
        catch ( DuplicateElementException e )
        {
            return errorResponse( HttpStatus.CONFLICT, e.getMessage() );
        }
        catch ( JsonProcessingException | IllegalArgumentException e )
        {
            return errorResponse( HttpStatus.BAD_REQUEST, e.getMessage() );
        }
        catch ( RuntimeException e )
        {
            if ( hasJsonParseCause( e ) )
            {
                return errorResponse( HttpStatus.BAD_REQUEST, e.getMessage() );
            }
            throw e;
        }
    }

    private WebResponse handleApps( final WebRequest request, final String[] segments )
    {
        if ( segments.length != 1 )
        {
            return notFound();
        }
        if ( request.getMethod() != HttpMethod.GET )
        {
            return methodNotAllowed();
        }
        return jsonResponse( HttpStatus.OK,
                             schemaService.listApplicationKeys().stream().map( ApplicationKey::toString ).collect( Collectors.toList() ) );
    }

    private WebResponse handleNamespaces( final WebRequest request, final String[] segments )
        throws JsonProcessingException
    {
        if ( segments.length == 1 )
        {
            switch ( request.getMethod() )
            {
                case GET:
                    return jsonResponse( HttpStatus.OK,
                                         schemaService.listNamespaces().stream().map( NamespaceJson::new ).collect( Collectors.toList() ) );
                case POST:
                    final NamespaceParamsJson params = parseBody( request, NamespaceParamsJson.class );
                    if ( params.getKey() == null || params.getKey().isBlank() )
                    {
                        throw new IllegalArgumentException( "key is required" );
                    }
                    final Namespace created = schemaService.createNamespace( CreateNamespaceParams.create()
                                                                                 .key( ApplicationKey.from( params.getKey() ) )
                                                                                 .description( params.getDescription() )
                                                                                 .build() );
                    return jsonResponse( HttpStatus.CREATED, new NamespaceJson( created ) );
                default:
                    return methodNotAllowed();
            }
        }

        if ( segments.length == 2 )
        {
            final ApplicationKey key = ApplicationKey.from( segments[1] );
            switch ( request.getMethod() )
            {
                case GET:
                    final Namespace namespace = schemaService.getNamespace( key );
                    return namespace == null
                        ? errorResponse( HttpStatus.NOT_FOUND, String.format( "Namespace [%s] not found", key ) )
                        : jsonResponse( HttpStatus.OK, new NamespaceJson( namespace ) );
                case PUT:
                    final NamespaceParamsJson params = parseBody( request, NamespaceParamsJson.class );
                    final Namespace updated = schemaService.updateNamespace(
                        UpdateNamespaceParams.create().key( key ).description( params.getDescription() ).build() );
                    return jsonResponse( HttpStatus.OK, new NamespaceJson( updated ) );
                case DELETE:
                    return schemaService.deleteNamespace( key )
                        ? noContent()
                        : errorResponse( HttpStatus.NOT_FOUND, String.format( "Namespace [%s] not found", key ) );
                default:
                    return methodNotAllowed();
            }
        }

        return notFound();
    }

    private WebResponse handleContentSchemas( final WebRequest request, final String[] segments )
        throws JsonProcessingException
    {
        if ( segments.length == 3 )
        {
            if ( request.getMethod() != HttpMethod.GET )
            {
                return methodNotAllowed();
            }
            final DynamicContentSchemaType type = contentSchemaType( segments[2] );
            final List<DynamicSchemaResult<BaseSchema<?>>> results = schemaService.listContentSchemas(
                ListDynamicContentSchemasParams.create().applicationKey( ApplicationKey.from( segments[1] ) ).type( type ).build() );
            return jsonResponse( HttpStatus.OK, results.stream().map( result -> toSchemaJson( result, type ) ).collect( Collectors.toList() ) );
        }

        if ( segments.length == 4 )
        {
            final DynamicContentSchemaType type = contentSchemaType( segments[2] );
            final BaseSchemaName name = schemaName( ApplicationKey.from( segments[1] ), type, segments[3] );
            switch ( request.getMethod() )
            {
                case GET:
                    final DynamicSchemaResult<BaseSchema<?>> result =
                        schemaService.getContentSchema( GetDynamicContentSchemaParams.create().name( name ).type( type ).build() );
                    return result == null
                        ? errorResponse( HttpStatus.NOT_FOUND, String.format( "Schema [%s] not found", name ) )
                        : jsonResponse( HttpStatus.OK, toSchemaJson( result, type ) );
                case POST:
                    final DynamicSchemaResult<BaseSchema<?>> created = schemaService.createContentSchema(
                        CreateDynamicContentSchemaParams.create().name( name ).type( type ).resource( parseResource( request ) ).build() );
                    return jsonResponse( HttpStatus.CREATED, toSchemaJson( created, type ) );
                case PUT:
                    final DynamicSchemaResult<BaseSchema<?>> updated = schemaService.updateContentSchema(
                        UpdateDynamicContentSchemaParams.create().name( name ).type( type ).resource( parseResource( request ) ).build() );
                    return jsonResponse( HttpStatus.OK, toSchemaJson( updated, type ) );
                case DELETE:
                    return schemaService.deleteContentSchema( DeleteDynamicContentSchemaParams.create().name( name ).type( type ).build() )
                        ? noContent()
                        : errorResponse( HttpStatus.NOT_FOUND, String.format( "Schema [%s] not found", name ) );
                default:
                    return methodNotAllowed();
            }
        }

        return notFound();
    }

    private WebResponse handleComponents( final WebRequest request, final String[] segments )
        throws JsonProcessingException
    {
        if ( segments.length == 3 )
        {
            if ( request.getMethod() != HttpMethod.GET )
            {
                return methodNotAllowed();
            }
            final DynamicComponentType type = componentType( segments[2] );
            final List<DynamicSchemaResult<ComponentDescriptor>> results = schemaService.listComponents(
                ListDynamicComponentsParams.create().applicationKey( ApplicationKey.from( segments[1] ) ).type( type ).build() );
            return jsonResponse( HttpStatus.OK,
                                 results.stream().map( result -> toComponentJson( result, type ) ).collect( Collectors.toList() ) );
        }

        if ( segments.length == 4 )
        {
            final DynamicComponentType type = componentType( segments[2] );
            final DescriptorKey key = DescriptorKey.from( ApplicationKey.from( segments[1] ), segments[3] );
            switch ( request.getMethod() )
            {
                case GET:
                    final DynamicSchemaResult<ComponentDescriptor> result =
                        schemaService.getComponent( GetDynamicComponentParams.create().descriptorKey( key ).type( type ).build() );
                    return result == null
                        ? errorResponse( HttpStatus.NOT_FOUND, String.format( "Component [%s] not found", key ) )
                        : jsonResponse( HttpStatus.OK, toComponentJson( result, type ) );
                case POST:
                    final DynamicSchemaResult<ComponentDescriptor> created = schemaService.createComponent(
                        CreateDynamicComponentParams.create().descriptorKey( key ).type( type ).resource( parseResource( request ) ).build() );
                    return jsonResponse( HttpStatus.CREATED, toComponentJson( created, type ) );
                case PUT:
                    final DynamicSchemaResult<ComponentDescriptor> updated = schemaService.updateComponent(
                        UpdateDynamicComponentParams.create().descriptorKey( key ).type( type ).resource( parseResource( request ) ).build() );
                    return jsonResponse( HttpStatus.OK, toComponentJson( updated, type ) );
                case DELETE:
                    return schemaService.deleteComponent( DeleteDynamicComponentParams.create().descriptorKey( key ).type( type ).build() )
                        ? noContent()
                        : errorResponse( HttpStatus.NOT_FOUND, String.format( "Component [%s] not found", key ) );
                default:
                    return methodNotAllowed();
            }
        }

        return notFound();
    }

    private WebResponse handleMacros( final WebRequest request, final String[] segments )
        throws JsonProcessingException
    {
        if ( segments.length == 2 )
        {
            if ( request.getMethod() != HttpMethod.GET )
            {
                return methodNotAllowed();
            }
            final List<DynamicSchemaResult<MacroDescriptor>> results = schemaService.listMacros(
                ListDynamicMacrosParams.create().applicationKey( ApplicationKey.from( segments[1] ) ).build() );
            return jsonResponse( HttpStatus.OK, results.stream().map( SchemaApiHandler::toMacroJson ).collect( Collectors.toList() ) );
        }

        if ( segments.length == 3 )
        {
            final MacroKey key = MacroKey.from( ApplicationKey.from( segments[1] ), segments[2] );
            switch ( request.getMethod() )
            {
                case GET:
                    final DynamicSchemaResult<MacroDescriptor> result =
                        schemaService.getMacro( GetDynamicMacroParams.create().key( key ).build() );
                    return result == null
                        ? errorResponse( HttpStatus.NOT_FOUND, String.format( "Macro [%s] not found", key ) )
                        : jsonResponse( HttpStatus.OK, toMacroJson( result ) );
                case POST:
                    final DynamicSchemaResult<MacroDescriptor> created = schemaService.createMacro(
                        CreateDynamicMacroParams.create().key( key ).resource( parseResource( request ) ).build() );
                    return jsonResponse( HttpStatus.CREATED, toMacroJson( created ) );
                case PUT:
                    final DynamicSchemaResult<MacroDescriptor> updated = schemaService.updateMacro(
                        UpdateDynamicMacroParams.create().key( key ).resource( parseResource( request ) ).build() );
                    return jsonResponse( HttpStatus.OK, toMacroJson( updated ) );
                case DELETE:
                    return schemaService.deleteMacro( DeleteDynamicMacroParams.create().key( key ).build() )
                        ? noContent()
                        : errorResponse( HttpStatus.NOT_FOUND, String.format( "Macro [%s] not found", key ) );
                default:
                    return methodNotAllowed();
            }
        }

        return notFound();
    }

    private WebResponse handleStyles( final WebRequest request, final String[] segments )
        throws JsonProcessingException
    {
        if ( segments.length != 2 )
        {
            return notFound();
        }
        final ApplicationKey key = ApplicationKey.from( segments[1] );
        switch ( request.getMethod() )
        {
            case GET:
                final DynamicSchemaResult<StyleDescriptor> result = schemaService.getStyles( key );
                return result == null
                    ? errorResponse( HttpStatus.NOT_FOUND, String.format( "Styles for application [%s] not found", key ) )
                    : jsonResponse( HttpStatus.OK, toAppJson( key, result.getResource() ) );
            case POST:
                final DynamicSchemaResult<StyleDescriptor> created = schemaService.createStyles(
                    CreateDynamicStylesParams.create().key( key ).resource( parseResource( request ) ).build() );
                return jsonResponse( HttpStatus.CREATED, toAppJson( key, created.getResource() ) );
            case PUT:
                final DynamicSchemaResult<StyleDescriptor> updated = schemaService.updateStyles(
                    UpdateDynamicStylesParams.create().key( key ).resource( parseResource( request ) ).build() );
                return jsonResponse( HttpStatus.OK, toAppJson( key, updated.getResource() ) );
            case DELETE:
                return schemaService.deleteStyles( key )
                    ? noContent()
                    : errorResponse( HttpStatus.NOT_FOUND, String.format( "Styles for application [%s] not found", key ) );
            default:
                return methodNotAllowed();
        }
    }

    private WebResponse handleCms( final WebRequest request, final String[] segments )
        throws JsonProcessingException
    {
        if ( segments.length != 2 )
        {
            return notFound();
        }
        final ApplicationKey key = ApplicationKey.from( segments[1] );
        switch ( request.getMethod() )
        {
            case GET:
                final DynamicSchemaResult<CmsDescriptor> result = schemaService.getCmsDescriptor( key );
                return result == null
                    ? errorResponse( HttpStatus.NOT_FOUND, String.format( "CMS descriptor for application [%s] not found", key ) )
                    : jsonResponse( HttpStatus.OK, toAppJson( key, result.getResource() ) );
            case POST:
                final DynamicSchemaResult<CmsDescriptor> created = schemaService.createCms(
                    CreateDynamicCmsParams.create().key( key ).resource( parseResource( request ) ).build() );
                return jsonResponse( HttpStatus.CREATED, toAppJson( key, created.getResource() ) );
            case PUT:
                final DynamicSchemaResult<CmsDescriptor> updated = schemaService.updateCms(
                    UpdateDynamicCmsParams.create().key( key ).resource( parseResource( request ) ).build() );
                return jsonResponse( HttpStatus.OK, toAppJson( key, updated.getResource() ) );
            case DELETE:
                return schemaService.deleteCms( key )
                    ? noContent()
                    : errorResponse( HttpStatus.NOT_FOUND, String.format( "CMS descriptor for application [%s] not found", key ) );
            default:
                return methodNotAllowed();
        }
    }

    private WebResponse handlePhrases( final WebRequest request, final String[] segments )
        throws JsonProcessingException
    {
        if ( segments.length == 2 )
        {
            if ( request.getMethod() != HttpMethod.GET )
            {
                return methodNotAllowed();
            }
            final ApplicationKey key = ApplicationKey.from( segments[1] );
            return jsonResponse( HttpStatus.OK, schemaService.listPhrases( key )
                .stream()
                .map( resource -> new PhrasesResourceJson( key.toString(), resource ) )
                .collect( Collectors.toList() ) );
        }

        if ( segments.length == 3 )
        {
            final ApplicationKey key = ApplicationKey.from( segments[1] );
            final String name = segments[2];
            switch ( request.getMethod() )
            {
                case GET:
                    final Resource resource =
                        schemaService.getPhrases( GetDynamicPhrasesParams.create().key( key ).name( name ).build() );
                    return resource == null
                        ? errorResponse( HttpStatus.NOT_FOUND, String.format( "Phrases [%s] not found in application [%s]", name, key ) )
                        : jsonResponse( HttpStatus.OK, new PhrasesResourceJson( key.toString(), resource ) );
                case POST:
                    final Resource created = schemaService.createPhrases(
                        CreateDynamicPhrasesParams.create().key( key ).name( name ).resource( parseResource( request ) ).build() );
                    return jsonResponse( HttpStatus.CREATED, new PhrasesResourceJson( key.toString(), created ) );
                case PUT:
                    final Resource updated = schemaService.updatePhrases(
                        UpdateDynamicPhrasesParams.create().key( key ).name( name ).resource( parseResource( request ) ).build() );
                    return jsonResponse( HttpStatus.OK, new PhrasesResourceJson( key.toString(), updated ) );
                case DELETE:
                    return schemaService.deletePhrases( DeleteDynamicPhrasesParams.create().key( key ).name( name ).build() )
                        ? noContent()
                        : errorResponse( HttpStatus.NOT_FOUND, String.format( "Phrases [%s] not found in application [%s]", name, key ) );
                default:
                    return methodNotAllowed();
            }
        }

        return notFound();
    }

    private static SchemaResourceJson toSchemaJson( final DynamicSchemaResult<? extends BaseSchema<?>> result,
                                                    final DynamicContentSchemaType type )
    {
        return new SchemaResourceJson( result.getSchema().getName().toString(), typeSegment( type ), result.getResource() );
    }

    private static ComponentResourceJson toComponentJson( final DynamicSchemaResult<? extends ComponentDescriptor> result,
                                                          final DynamicComponentType type )
    {
        return new ComponentResourceJson( result.getSchema().getKey().toString(), typeSegment( type ), result.getResource() );
    }

    private static MacroResourceJson toMacroJson( final DynamicSchemaResult<MacroDescriptor> result )
    {
        return new MacroResourceJson( result.getSchema().getKey().toString(), result.getResource() );
    }

    private static AppResourceJson toAppJson( final ApplicationKey key, final Resource resource )
    {
        return new AppResourceJson( key.toString(), resource );
    }

    private static BaseSchemaName schemaName( final ApplicationKey applicationKey, final DynamicContentSchemaType type, final String name )
    {
        return switch ( type )
        {
            case CONTENT_TYPE -> ContentTypeName.from( applicationKey, name );
            case FORM_FRAGMENT -> FormFragmentName.from( applicationKey, name );
            case MIXIN -> MixinName.from( applicationKey, name );
        };
    }

    private static DynamicContentSchemaType contentSchemaType( final String segment )
    {
        return DynamicContentSchemaType.valueOf( segment.toUpperCase( Locale.ROOT ).replace( '-', '_' ) );
    }

    private static DynamicComponentType componentType( final String segment )
    {
        return DynamicComponentType.valueOf( segment.toUpperCase( Locale.ROOT ).replace( '-', '_' ) );
    }

    private static String typeSegment( final Enum<?> type )
    {
        return type.name().toLowerCase( Locale.ROOT ).replace( '_', '-' );
    }

    private String parseResource( final WebRequest request )
        throws JsonProcessingException
    {
        final SchemaResourceParamsJson params = parseBody( request, SchemaResourceParamsJson.class );
        if ( params.getResource() == null || params.getResource().isBlank() )
        {
            throw new IllegalArgumentException( "resource is required" );
        }
        return params.getResource();
    }

    private static <T> T parseBody( final WebRequest request, final Class<T> type )
        throws JsonProcessingException
    {
        final String body = request.getBodyAsString();
        if ( body == null || body.isBlank() )
        {
            throw new IllegalArgumentException( "request body is required" );
        }
        return OBJECT_MAPPER.readValue( body, type );
    }

    private static boolean hasJsonParseCause( final Throwable e )
    {
        Throwable cause = e.getCause();
        while ( cause != null )
        {
            if ( cause instanceof JacksonException )
            {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    private static WebResponse jsonResponse( final HttpStatus status, final Object body )
    {
        return WebResponse.create().status( status ).contentType( MediaType.JSON_UTF_8 ).body( toJson( body ) ).build();
    }

    private static WebResponse errorResponse( final HttpStatus status, final String message )
    {
        return jsonResponse( status, new ErrorJson( status.value(), message ) );
    }

    private static WebResponse notFound()
    {
        return errorResponse( HttpStatus.NOT_FOUND, "Resource not found" );
    }

    private static WebResponse methodNotAllowed()
    {
        return errorResponse( HttpStatus.METHOD_NOT_ALLOWED, "Method not allowed" );
    }

    private static WebResponse noContent()
    {
        return WebResponse.create().status( HttpStatus.NO_CONTENT ).build();
    }

    private static String toJson( final Object value )
    {
        try
        {
            return OBJECT_MAPPER.writeValueAsString( value );
        }
        catch ( JsonProcessingException e )
        {
            throw new UncheckedIOException( e );
        }
    }
}
