package com.enonic.xp.impl.server.rest;

import java.io.IOException;
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
import com.google.common.io.ByteSource;
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
import com.enonic.xp.icon.Icon;
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
import com.enonic.xp.resource.Resource;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.schema.BaseSchemaName;
import com.enonic.xp.schema.CreateCmsParams;
import com.enonic.xp.schema.CreateComponentParams;
import com.enonic.xp.schema.CreateContentSchemaParams;
import com.enonic.xp.schema.CreateMacroParams;
import com.enonic.xp.schema.CreatePhrasesParams;
import com.enonic.xp.schema.CreateStylesParams;
import com.enonic.xp.schema.DeleteMacroParams;
import com.enonic.xp.schema.DeletePhrasesParams;
import com.enonic.xp.schema.GetMacroParams;
import com.enonic.xp.schema.GetPhrasesParams;
import com.enonic.xp.schema.ListMacrosParams;
import com.enonic.xp.schema.SchemaResult;
import com.enonic.xp.schema.SchemaService;
import com.enonic.xp.schema.SetComponentIconParams;
import com.enonic.xp.schema.SetMacroIconParams;
import com.enonic.xp.schema.SetSchemaIconParams;
import com.enonic.xp.schema.UpdateCmsParams;
import com.enonic.xp.schema.UpdateComponentParams;
import com.enonic.xp.schema.UpdateContentSchemaParams;
import com.enonic.xp.schema.UpdateMacroParams;
import com.enonic.xp.schema.UpdatePhrasesParams;
import com.enonic.xp.schema.UpdateStylesParams;
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
            final String type = typeSegment( segments[2] );
            final ApplicationKey applicationKey = ApplicationKey.from( segments[1] );
            final List<? extends SchemaResult<? extends BaseSchema<?>>> results = switch ( type )
            {
                case "content-type" -> schemaService.listContentTypes( applicationKey );
                case "form-fragment" -> schemaService.listFormFragments( applicationKey );
                case "mixin" -> schemaService.listMixins( applicationKey );
                default -> throw new IllegalArgumentException( String.format( "unknown schema type: %s", segments[2] ) );
            };
            return jsonResponse( HttpStatus.OK,
                                 results.stream().map( result -> toSchemaJson( result, type ) ).collect( Collectors.toList() ) );
        }

        if ( segments.length == 4 )
        {
            final String type = typeSegment( segments[2] );
            final BaseSchemaName name = schemaName( ApplicationKey.from( segments[1] ), type, segments[3] );
            return handleContentSchema( request, type, name );
        }

        if ( segments.length == 5 && "icon".equals( segments[4] ) )
        {
            final String type = typeSegment( segments[2] );
            final BaseSchemaName name = schemaName( ApplicationKey.from( segments[1] ), type, segments[3] );
            return handleContentSchemaIcon( request, type, name );
        }

        return notFound();
    }

    private WebResponse handleContentSchemaIcon( final WebRequest request, final String type, final BaseSchemaName name )
    {
        switch ( request.getMethod() )
        {
            case PUT:
                final SetSchemaIconParams params = SetSchemaIconParams.create()
                    .name( name )
                    .data( readBinaryBody( request ) )
                    .mimeType( requireIconContentType( request ) )
                    .build();
                switch ( type )
                {
                    case "content-type" -> schemaService.setContentTypeIcon( params );
                    case "form-fragment" -> schemaService.setFormFragmentIcon( params );
                    case "mixin" -> schemaService.setMixinIcon( params );
                    default -> throw new IllegalArgumentException( String.format( "unknown schema type: %s", type ) );
                }
                return noContent();
            case GET:
                final Icon icon = switch ( type )
                {
                    case "content-type" -> schemaService.getContentTypeIcon( (ContentTypeName) name );
                    case "form-fragment" -> schemaService.getFormFragmentIcon( (FormFragmentName) name );
                    case "mixin" -> schemaService.getMixinIcon( (MixinName) name );
                    default -> throw new IllegalArgumentException( String.format( "unknown schema type: %s", type ) );
                };
                return icon == null
                    ? errorResponse( HttpStatus.NOT_FOUND, String.format( "Icon for schema [%s] not found", name ) )
                    : iconResponse( icon );
            case DELETE:
                final boolean deleted = switch ( type )
                {
                    case "content-type" -> schemaService.deleteContentTypeIcon( (ContentTypeName) name );
                    case "form-fragment" -> schemaService.deleteFormFragmentIcon( (FormFragmentName) name );
                    case "mixin" -> schemaService.deleteMixinIcon( (MixinName) name );
                    default -> throw new IllegalArgumentException( String.format( "unknown schema type: %s", type ) );
                };
                return deleted
                    ? noContent()
                    : errorResponse( HttpStatus.NOT_FOUND, String.format( "Icon for schema [%s] not found", name ) );
            default:
                return methodNotAllowed();
        }
    }

    private WebResponse handleContentSchema( final WebRequest request, final String type, final BaseSchemaName name )
        throws JsonProcessingException
    {
        switch ( request.getMethod() )
        {
            case GET:
                final SchemaResult<? extends BaseSchema<?>> result = switch ( type )
                {
                    case "content-type" -> schemaService.getContentType( (ContentTypeName) name );
                    case "form-fragment" -> schemaService.getFormFragment( (FormFragmentName) name );
                    case "mixin" -> schemaService.getMixin( (MixinName) name );
                    default -> throw new IllegalArgumentException( String.format( "unknown schema type: %s", type ) );
                };
                return result == null
                    ? errorResponse( HttpStatus.NOT_FOUND, String.format( "Schema [%s] not found", name ) )
                    : jsonResponse( HttpStatus.OK, toSchemaJson( result, type ) );
            case POST:
                final CreateContentSchemaParams createParams =
                    CreateContentSchemaParams.create().name( name ).resource( parseResource( request ) ).build();
                final SchemaResult<? extends BaseSchema<?>> created = switch ( type )
                {
                    case "content-type" -> schemaService.createContentType( createParams );
                    case "form-fragment" -> schemaService.createFormFragment( createParams );
                    case "mixin" -> schemaService.createMixin( createParams );
                    default -> throw new IllegalArgumentException( String.format( "unknown schema type: %s", type ) );
                };
                return jsonResponse( HttpStatus.CREATED, toSchemaJson( created, type ) );
            case PUT:
                final UpdateContentSchemaParams updateParams =
                    UpdateContentSchemaParams.create().name( name ).resource( parseResource( request ) ).build();
                final SchemaResult<? extends BaseSchema<?>> updated = switch ( type )
                {
                    case "content-type" -> schemaService.updateContentType( updateParams );
                    case "form-fragment" -> schemaService.updateFormFragment( updateParams );
                    case "mixin" -> schemaService.updateMixin( updateParams );
                    default -> throw new IllegalArgumentException( String.format( "unknown schema type: %s", type ) );
                };
                return jsonResponse( HttpStatus.OK, toSchemaJson( updated, type ) );
            case DELETE:
                final boolean deleted = switch ( type )
                {
                    case "content-type" -> schemaService.deleteContentType( (ContentTypeName) name );
                    case "form-fragment" -> schemaService.deleteFormFragment( (FormFragmentName) name );
                    case "mixin" -> schemaService.deleteMixin( (MixinName) name );
                    default -> throw new IllegalArgumentException( String.format( "unknown schema type: %s", type ) );
                };
                return deleted ? noContent() : errorResponse( HttpStatus.NOT_FOUND, String.format( "Schema [%s] not found", name ) );
            default:
                return methodNotAllowed();
        }
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
            final String type = typeSegment( segments[2] );
            final ApplicationKey applicationKey = ApplicationKey.from( segments[1] );
            final List<? extends SchemaResult<? extends ComponentDescriptor>> results = switch ( type )
            {
                case "part" -> schemaService.listParts( applicationKey );
                case "layout" -> schemaService.listLayouts( applicationKey );
                case "page" -> schemaService.listPages( applicationKey );
                default -> throw new IllegalArgumentException( String.format( "unknown component type: %s", segments[2] ) );
            };
            return jsonResponse( HttpStatus.OK,
                                 results.stream().map( result -> toComponentJson( result, type ) ).collect( Collectors.toList() ) );
        }

        if ( segments.length == 4 )
        {
            final String type = typeSegment( segments[2] );
            final DescriptorKey key = DescriptorKey.from( ApplicationKey.from( segments[1] ), segments[3] );
            return handleComponent( request, type, key );
        }

        if ( segments.length == 5 && "icon".equals( segments[4] ) )
        {
            final String type = typeSegment( segments[2] );
            final DescriptorKey key = DescriptorKey.from( ApplicationKey.from( segments[1] ), segments[3] );
            return handleComponentIcon( request, type, key );
        }

        return notFound();
    }

    private WebResponse handleComponentIcon( final WebRequest request, final String type, final DescriptorKey key )
    {
        switch ( type )
        {
            case "part":
                break;
            case "layout":
            case "page":
                throw new IllegalArgumentException( String.format( "icons are not supported for component type: %s", type ) );
            default:
                throw new IllegalArgumentException( String.format( "unknown component type: %s", type ) );
        }

        switch ( request.getMethod() )
        {
            case PUT:
                schemaService.setPartIcon( SetComponentIconParams.create()
                                               .descriptorKey( key )
                                               .data( readBinaryBody( request ) )
                                               .mimeType( requireIconContentType( request ) )
                                               .build() );
                return noContent();
            case GET:
                final Icon icon = schemaService.getPartIcon( key );
                return icon == null
                    ? errorResponse( HttpStatus.NOT_FOUND, String.format( "Icon for component [%s] not found", key ) )
                    : iconResponse( icon );
            case DELETE:
                return schemaService.deletePartIcon( key )
                    ? noContent()
                    : errorResponse( HttpStatus.NOT_FOUND, String.format( "Icon for component [%s] not found", key ) );
            default:
                return methodNotAllowed();
        }
    }

    private WebResponse handleComponent( final WebRequest request, final String type, final DescriptorKey key )
        throws JsonProcessingException
    {
        switch ( request.getMethod() )
        {
            case GET:
                final SchemaResult<? extends ComponentDescriptor> result = switch ( type )
                {
                    case "part" -> schemaService.getPart( key );
                    case "layout" -> schemaService.getLayout( key );
                    case "page" -> schemaService.getPage( key );
                    default -> throw new IllegalArgumentException( String.format( "unknown component type: %s", type ) );
                };
                return result == null
                    ? errorResponse( HttpStatus.NOT_FOUND, String.format( "Component [%s] not found", key ) )
                    : jsonResponse( HttpStatus.OK, toComponentJson( result, type ) );
            case POST:
                final CreateComponentParams createParams =
                    CreateComponentParams.create().descriptorKey( key ).resource( parseResource( request ) ).build();
                final SchemaResult<? extends ComponentDescriptor> created = switch ( type )
                {
                    case "part" -> schemaService.createPart( createParams );
                    case "layout" -> schemaService.createLayout( createParams );
                    case "page" -> schemaService.createPage( createParams );
                    default -> throw new IllegalArgumentException( String.format( "unknown component type: %s", type ) );
                };
                return jsonResponse( HttpStatus.CREATED, toComponentJson( created, type ) );
            case PUT:
                final UpdateComponentParams updateParams =
                    UpdateComponentParams.create().descriptorKey( key ).resource( parseResource( request ) ).build();
                final SchemaResult<? extends ComponentDescriptor> updated = switch ( type )
                {
                    case "part" -> schemaService.updatePart( updateParams );
                    case "layout" -> schemaService.updateLayout( updateParams );
                    case "page" -> schemaService.updatePage( updateParams );
                    default -> throw new IllegalArgumentException( String.format( "unknown component type: %s", type ) );
                };
                return jsonResponse( HttpStatus.OK, toComponentJson( updated, type ) );
            case DELETE:
                final boolean deleted = switch ( type )
                {
                    case "part" -> schemaService.deletePart( key );
                    case "layout" -> schemaService.deleteLayout( key );
                    case "page" -> schemaService.deletePage( key );
                    default -> throw new IllegalArgumentException( String.format( "unknown component type: %s", type ) );
                };
                return deleted ? noContent() : errorResponse( HttpStatus.NOT_FOUND, String.format( "Component [%s] not found", key ) );
            default:
                return methodNotAllowed();
        }
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
            final List<SchemaResult<MacroDescriptor>> results =
                schemaService.listMacros( ListMacrosParams.create().applicationKey( ApplicationKey.from( segments[1] ) ).build() );
            return jsonResponse( HttpStatus.OK, results.stream().map( SchemaApiHandler::toMacroJson ).collect( Collectors.toList() ) );
        }

        if ( segments.length == 4 && "icon".equals( segments[3] ) )
        {
            final MacroKey key = MacroKey.from( ApplicationKey.from( segments[1] ), segments[2] );
            return handleMacroIcon( request, key );
        }

        if ( segments.length == 3 )
        {
            final MacroKey key = MacroKey.from( ApplicationKey.from( segments[1] ), segments[2] );
            switch ( request.getMethod() )
            {
                case GET:
                    final SchemaResult<MacroDescriptor> result = schemaService.getMacro( GetMacroParams.create().key( key ).build() );
                    return result == null
                        ? errorResponse( HttpStatus.NOT_FOUND, String.format( "Macro [%s] not found", key ) )
                        : jsonResponse( HttpStatus.OK, toMacroJson( result ) );
                case POST:
                    final SchemaResult<MacroDescriptor> created =
                        schemaService.createMacro( CreateMacroParams.create().key( key ).resource( parseResource( request ) ).build() );
                    return jsonResponse( HttpStatus.CREATED, toMacroJson( created ) );
                case PUT:
                    final SchemaResult<MacroDescriptor> updated =
                        schemaService.updateMacro( UpdateMacroParams.create().key( key ).resource( parseResource( request ) ).build() );
                    return jsonResponse( HttpStatus.OK, toMacroJson( updated ) );
                case DELETE:
                    return schemaService.deleteMacro( DeleteMacroParams.create().key( key ).build() )
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
                final SchemaResult<StyleDescriptor> result = schemaService.getStyles( key );
                return result == null
                    ? errorResponse( HttpStatus.NOT_FOUND, String.format( "Styles for application [%s] not found", key ) )
                    : jsonResponse( HttpStatus.OK, toAppJson( key, result.getResource() ) );
            case POST:
                final SchemaResult<StyleDescriptor> created =
                    schemaService.createStyles( CreateStylesParams.create().key( key ).resource( parseResource( request ) ).build() );
                return jsonResponse( HttpStatus.CREATED, toAppJson( key, created.getResource() ) );
            case PUT:
                final SchemaResult<StyleDescriptor> updated =
                    schemaService.updateStyles( UpdateStylesParams.create().key( key ).resource( parseResource( request ) ).build() );
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
                final SchemaResult<CmsDescriptor> result = schemaService.getCmsDescriptor( key );
                return result == null
                    ? errorResponse( HttpStatus.NOT_FOUND, String.format( "CMS descriptor for application [%s] not found", key ) )
                    : jsonResponse( HttpStatus.OK, toAppJson( key, result.getResource() ) );
            case POST:
                final SchemaResult<CmsDescriptor> created =
                    schemaService.createCms( CreateCmsParams.create().key( key ).resource( parseResource( request ) ).build() );
                return jsonResponse( HttpStatus.CREATED, toAppJson( key, created.getResource() ) );
            case PUT:
                final SchemaResult<CmsDescriptor> updated =
                    schemaService.updateCms( UpdateCmsParams.create().key( key ).resource( parseResource( request ) ).build() );
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
                    final Resource resource = schemaService.getPhrases( GetPhrasesParams.create().key( key ).name( name ).build() );
                    return resource == null
                        ? errorResponse( HttpStatus.NOT_FOUND, String.format( "Phrases [%s] not found in application [%s]", name, key ) )
                        : jsonResponse( HttpStatus.OK, new PhrasesResourceJson( key.toString(), resource ) );
                case POST:
                    final Resource created = schemaService.createPhrases(
                        CreatePhrasesParams.create().key( key ).name( name ).resource( parseResource( request ) ).build() );
                    return jsonResponse( HttpStatus.CREATED, new PhrasesResourceJson( key.toString(), created ) );
                case PUT:
                    final Resource updated = schemaService.updatePhrases(
                        UpdatePhrasesParams.create().key( key ).name( name ).resource( parseResource( request ) ).build() );
                    return jsonResponse( HttpStatus.OK, new PhrasesResourceJson( key.toString(), updated ) );
                case DELETE:
                    return schemaService.deletePhrases( DeletePhrasesParams.create().key( key ).name( name ).build() )
                        ? noContent()
                        : errorResponse( HttpStatus.NOT_FOUND, String.format( "Phrases [%s] not found in application [%s]", name, key ) );
                default:
                    return methodNotAllowed();
            }
        }

        return notFound();
    }

    private WebResponse handleMacroIcon( final WebRequest request, final MacroKey key )
    {
        switch ( request.getMethod() )
        {
            case PUT:
                schemaService.setMacroIcon( SetMacroIconParams.create()
                                                .key( key )
                                                .data( readBinaryBody( request ) )
                                                .mimeType( requireIconContentType( request ) )
                                                .build() );
                return noContent();
            case GET:
                final Icon icon = schemaService.getMacroIcon( key );
                return icon == null
                    ? errorResponse( HttpStatus.NOT_FOUND, String.format( "Icon for macro [%s] not found", key ) )
                    : iconResponse( icon );
            case DELETE:
                return schemaService.deleteMacroIcon( key )
                    ? noContent()
                    : errorResponse( HttpStatus.NOT_FOUND, String.format( "Icon for macro [%s] not found", key ) );
            default:
                return methodNotAllowed();
        }
    }

    private static ByteSource readBinaryBody( final WebRequest request )
    {
        try
        {
            final byte[] bytes = request.getRawRequest().getInputStream().readAllBytes();
            if ( bytes.length == 0 )
            {
                throw new IllegalArgumentException( "request body is required" );
            }
            return ByteSource.wrap( bytes );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private static String requireIconContentType( final WebRequest request )
    {
        final String contentType = request.getContentType();
        if ( contentType == null || contentType.isBlank() )
        {
            throw new IllegalArgumentException( "Content-Type is required" );
        }
        final MediaType mediaType = MediaType.parse( contentType ).withoutParameters();
        if ( mediaType.is( MediaType.SVG_UTF_8.withoutParameters() ) )
        {
            return "image/svg+xml";
        }
        if ( mediaType.is( MediaType.PNG ) )
        {
            return "image/png";
        }
        throw new IllegalArgumentException( String.format( "unsupported icon content type: %s", contentType ) );
    }

    private static WebResponse iconResponse( final Icon icon )
    {
        return WebResponse.create()
            .status( HttpStatus.OK )
            .contentType( MediaType.parse( icon.getMimeType() ) )
            .body( icon.toByteArray() )
            .build();
    }

    private static SchemaResourceJson toSchemaJson( final SchemaResult<? extends BaseSchema<?>> result, final String type )
    {
        return new SchemaResourceJson( result.getSchema().getName().toString(), type, result.getResource() );
    }

    private static ComponentResourceJson toComponentJson( final SchemaResult<? extends ComponentDescriptor> result, final String type )
    {
        return new ComponentResourceJson( result.getSchema().getKey().toString(), type, result.getResource() );
    }

    private static MacroResourceJson toMacroJson( final SchemaResult<MacroDescriptor> result )
    {
        return new MacroResourceJson( result.getSchema().getKey().toString(), result.getResource() );
    }

    private static AppResourceJson toAppJson( final ApplicationKey key, final Resource resource )
    {
        return new AppResourceJson( key.toString(), resource );
    }

    private static BaseSchemaName schemaName( final ApplicationKey applicationKey, final String type, final String name )
    {
        return switch ( type )
        {
            case "content-type" -> ContentTypeName.from( applicationKey, name );
            case "form-fragment" -> FormFragmentName.from( applicationKey, name );
            case "mixin" -> MixinName.from( applicationKey, name );
            default -> throw new IllegalArgumentException( String.format( "unknown schema type: %s", type ) );
        };
    }

    private static String typeSegment( final String segment )
    {
        return segment.toLowerCase( Locale.ROOT );
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
