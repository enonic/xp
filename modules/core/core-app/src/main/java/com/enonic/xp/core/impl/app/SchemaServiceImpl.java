package com.enonic.xp.core.impl.app;

import java.time.Instant;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.Applications;
import com.enonic.xp.app.CreateNamespaceParams;
import com.enonic.xp.app.Namespace;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.project.ProjectRole;
import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.resource.CreateDynamicComponentParams;
import com.enonic.xp.resource.CreateDynamicContentSchemaParams;
import com.enonic.xp.resource.CreateDynamicStylesParams;
import com.enonic.xp.resource.DeleteDynamicComponentParams;
import com.enonic.xp.resource.DeleteDynamicContentSchemaParams;
import com.enonic.xp.resource.CreateDynamicMacroParams;
import com.enonic.xp.resource.DeleteDynamicMacroParams;
import com.enonic.xp.resource.DynamicComponentType;
import com.enonic.xp.resource.DynamicContentSchemaType;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.GetDynamicComponentParams;
import com.enonic.xp.resource.GetDynamicContentSchemaParams;
import com.enonic.xp.resource.GetDynamicMacroParams;
import com.enonic.xp.resource.ListDynamicComponentsParams;
import com.enonic.xp.resource.ListDynamicContentSchemasParams;
import com.enonic.xp.resource.ListDynamicMacrosParams;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.SchemaService;
import com.enonic.xp.resource.UpdateDynamicCmsParams;
import com.enonic.xp.resource.UpdateDynamicComponentParams;
import com.enonic.xp.resource.UpdateDynamicContentSchemaParams;
import com.enonic.xp.resource.UpdateDynamicMacroParams;
import com.enonic.xp.resource.UpdateDynamicStylesParams;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.schema.BaseSchemaName;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentDescriptor;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.style.StyleDescriptor;

@Component(immediate = true, service = {SchemaService.class, DynamicSchemaServiceInternal.class})
public class SchemaServiceImpl
    implements SchemaService, DynamicSchemaServiceInternal
{
    private static final String PROJECT_OWNER_ROLE_SUFFIX = "." + ProjectRole.OWNER.name().toLowerCase();

    private final DynamicResourceManager dynamicResourceManager;

    private final DynamicResourceParser dynamicResourceParser;

    private final ApplicationRegistry applicationRegistry;

    private final VirtualAppService virtualAppService;

    @Activate
    public SchemaServiceImpl( @Reference final NodeService nodeService, @Reference final ResourceService resourceService,
                              @Reference final ApplicationRegistry applicationRegistry, @Reference final VirtualAppService virtualAppService )
    {
        this.dynamicResourceManager = new DynamicResourceManager( nodeService, resourceService );
        this.dynamicResourceParser = new DynamicResourceParser();
        this.applicationRegistry = applicationRegistry;
        this.virtualAppService = virtualAppService;
    }

    @Override
    public Application get( final ApplicationKey key )
    {
        final Application installedApplication = applicationRegistry.get( key );
        return installedApplication != null ? installedApplication : virtualAppService.get( key );
    }

    @Override
    public Applications list()
    {
        return Applications.from( Stream.concat( applicationRegistry.getAll().stream(), virtualAppService.list().stream() )
                                      .collect( Collectors.toMap( Application::getKey, Function.identity(), ( first, second ) -> first ) )
                                      .values() );
    }

    @Override
    public Namespace createNamespace( final CreateNamespaceParams params )
    {
        return this.virtualAppService.create( params );
    }

    @Override
    public boolean deleteNamespace( final ApplicationKey key )
    {
        return this.virtualAppService.delete( key );
    }

    @Override
    public Namespace getNamespace( final ApplicationKey key )
    {
        return this.virtualAppService.getNamespace( key );
    }

    @Override
    public List<Namespace> listNamespaces()
    {
        return this.virtualAppService.listNamespaces();
    }

    @Override
    public <T extends ComponentDescriptor> DynamicSchemaResult<T> createComponent( final CreateDynamicComponentParams params )
    {
        requireAdminRole();

        final ComponentDescriptor descriptor =
            dynamicResourceParser.parseComponent( params.getKey(), params.getType(), params.getResource() );

        final NodePath resourceFolderPath = createComponentFolderPath( params.getKey(), params.getType() );
        final Resource resource =
            dynamicResourceManager.createResource( resourceFolderPath, params.getKey().getName(), params.getResource() );

        return new DynamicSchemaResult<>( (T) wrapDescriptor( descriptor, resource.getTimestamp() ), resource );

    }

    @Override
    public <T extends ComponentDescriptor> DynamicSchemaResult<T> updateComponent( final UpdateDynamicComponentParams params )
    {
        requireAdminRole();

        final ComponentDescriptor descriptor =
            dynamicResourceParser.parseComponent( params.getKey(), params.getType(), params.getResource() );

        final NodePath resourceFolderPath = createComponentFolderPath( params.getKey(), params.getType() );
        final Resource resource =
            dynamicResourceManager.updateResource( resourceFolderPath, params.getKey().getName(), params.getResource() );

        return new DynamicSchemaResult<>( (T) wrapDescriptor( descriptor, resource.getTimestamp() ), resource );
    }


    @Override
    public <T extends BaseSchema<?>> DynamicSchemaResult<T> createContentSchema( final CreateDynamicContentSchemaParams params )
    {
        requireAdminRole();

        final BaseSchema<?> schema = dynamicResourceParser.parseSchema( params.getName(), params.getType(), params.getResource() );

        final NodePath resourceFolderPath = createSchemaFolderPath( params.getName(), params.getType() );
        final Resource resource =
            dynamicResourceManager.createResource( resourceFolderPath, params.getName().getLocalName(), params.getResource() );

        return new DynamicSchemaResult<>( (T) wrapSchema( schema, resource.getTimestamp() ), resource );
    }

    @Override
    public <T extends BaseSchema<?>> DynamicSchemaResult<T> updateContentSchema( final UpdateDynamicContentSchemaParams params )
    {
        requireAdminRole();

        final BaseSchema<?> schema = dynamicResourceParser.parseSchema( params.getName(), params.getType(), params.getResource() );

        final NodePath resourceFolderPath = createSchemaFolderPath( params.getName(), params.getType() );
        final Resource resource =
            dynamicResourceManager.updateResource( resourceFolderPath, params.getName().getLocalName(), params.getResource() );
        return new DynamicSchemaResult<>( (T) wrapSchema( schema, resource.getTimestamp() ), resource );
    }

    @Override
    public DynamicSchemaResult<CmsDescriptor> createCms( final CreateDynamicCmsParams params )
    {
        requireAdminRole();

        final CmsDescriptor site = dynamicResourceParser.parseCms( params.getKey(), params.getResource() );

        final NodePath resourceFolderPath = createCmsFolderPath( params.getKey() );
        final Resource createdResource =
            dynamicResourceManager.createResource( resourceFolderPath, VirtualAppConstants.CMS_ROOT_NAME, params.getResource() );

        return new DynamicSchemaResult<>(
            CmsDescriptor.copyOf( site ).modifiedTime( Instant.ofEpochMilli( createdResource.getTimestamp() ) ).build(), createdResource );
    }

    @Override
    public DynamicSchemaResult<CmsDescriptor> updateCms( final UpdateDynamicCmsParams params )
    {
        requireAdminRole();

        final CmsDescriptor cmsDescriptor = dynamicResourceParser.parseCms( params.getKey(), params.getResource() );

        final NodePath resourceFolderPath = createCmsFolderPath( params.getKey() );

        final Resource resource = dynamicResourceManager.resourceNodeExists( resourceFolderPath, VirtualAppConstants.CMS_ROOT_NAME )
            ? dynamicResourceManager.updateResource( resourceFolderPath, VirtualAppConstants.CMS_ROOT_NAME, params.getResource() )
            : dynamicResourceManager.createResource( resourceFolderPath, VirtualAppConstants.CMS_ROOT_NAME, params.getResource() );

        return new DynamicSchemaResult<>(
            CmsDescriptor.copyOf( cmsDescriptor ).modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) ).build(), resource );
    }

    @Override
    public DynamicSchemaResult<StyleDescriptor> createStyles( final CreateDynamicStylesParams params )
    {
        requireAdminRole();

        final StyleDescriptor styles = dynamicResourceParser.parseStyles( params.getKey(), params.getResource() );

        final NodePath resourceFolderPath =
            NodePath.create( createCmsFolderPath( params.getKey() ) ).addElement( VirtualAppConstants.STYLE_ROOT_NAME ).build();
        final Resource resource =
            dynamicResourceManager.createResource( resourceFolderPath, VirtualAppConstants.STYLE_NAME, params.getResource() );

        return new DynamicSchemaResult<>(
            StyleDescriptor.copyOf( styles ).modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) ).build(), resource );
    }

    @Override
    public DynamicSchemaResult<StyleDescriptor> updateStyles( final UpdateDynamicStylesParams params )
    {
        requireAdminRole();

        final StyleDescriptor styles = dynamicResourceParser.parseStyles( params.getKey(), params.getResource() );

        final NodePath resourceFolderPath =
            NodePath.create( createCmsFolderPath( params.getKey() ) ).addElement( VirtualAppConstants.STYLE_ROOT_NAME ).build();
        final Resource resource =
            dynamicResourceManager.updateResource( resourceFolderPath, VirtualAppConstants.STYLE_NAME, params.getResource() );

        return new DynamicSchemaResult<>(
            StyleDescriptor.copyOf( styles ).modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) ).build(), resource );
    }

    @Override
    public <T extends ComponentDescriptor> DynamicSchemaResult<T> getComponent( final GetDynamicComponentParams params )
    {
        requireReadAccess();

        return VirtualAppContext.createAdminContext().callWith( () -> {
            final NodePath resourceFolderPath = createComponentFolderPath( params.getKey(), params.getType() );
            final Resource resource = dynamicResourceManager.getResource( resourceFolderPath, params.getKey().getName() );

            if ( resource.exists() && resource.getSize() > 0 )
            {
                final ComponentDescriptor descriptor =
                    dynamicResourceParser.parseComponent( params.getKey(), params.getType(), resource.readString() );
                return new DynamicSchemaResult<>( (T) wrapDescriptor( descriptor, resource.getTimestamp() ), resource );
            }
            return null;
        } );
    }

    @Override
    public <T extends ComponentDescriptor> List<DynamicSchemaResult<T>> listComponents( final ListDynamicComponentsParams params )
    {
        requireListAccess();

        return VirtualAppContext.createAdminContext().callWith( () -> dynamicResourceManager.listResources(
                createComponentRootPath( params.getKey(), params.getType() ) )
            .stream()
            .map( resource -> {
                final ComponentDescriptor descriptor =
                    dynamicResourceParser.parseComponent( DescriptorKey.from( params.getKey(), getResourceName( resource.getKey() ) ),
                                                          params.getType(), resource.readString() );

                return new DynamicSchemaResult<>( (T) wrapDescriptor( descriptor, resource.getTimestamp() ), resource );
            } )
            .collect( Collectors.<DynamicSchemaResult<T>>toList() ) );
    }


    @Override
    public <T extends BaseSchema<?>> DynamicSchemaResult<T> getContentSchema( final GetDynamicContentSchemaParams params )
    {
        requireReadAccess();

        return VirtualAppContext.createAdminContext().callWith( () -> {
            final NodePath resourceFolderPath = createSchemaFolderPath( params.getName(), params.getType() );
            final Resource resource = dynamicResourceManager.getResource( resourceFolderPath, params.getName().getLocalName() );

            if ( resource.exists() && resource.getSize() > 0 )
            {
                final BaseSchema<?> schema =
                    dynamicResourceParser.parseSchema( params.getName(), params.getType(), resource.readString() );
                return new DynamicSchemaResult<>( (T) wrapSchema( schema, resource.getTimestamp() ), resource );
            }

            return null;
        } );
    }

    @Override
    public DynamicSchemaResult<CmsDescriptor> getCmsDescriptor( final ApplicationKey key )
    {
        requireReadAccess();

        return VirtualAppContext.createAdminContext().callWith( () -> {
            final NodePath resourceFolderPath = createCmsFolderPath( key );

            final Resource resource = dynamicResourceManager.getResource( resourceFolderPath, VirtualAppConstants.CMS_ROOT_NAME );

            if ( resource.exists() && resource.getSize() > 0 )
            {
                final CmsDescriptor siteDescriptor = dynamicResourceParser.parseCms( key, resource.readString() );
                return new DynamicSchemaResult<>(
                    CmsDescriptor.copyOf( siteDescriptor ).modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) ).build(),
                    resource );
            }
            return null;
        } );
    }

    @Override
    public DynamicSchemaResult<StyleDescriptor> getStyles( final ApplicationKey key )
    {
        requireReadAccess();

        return VirtualAppContext.createAdminContext().callWith( () -> {
            final NodePath resourceFolderPath =
                NodePath.create( createCmsFolderPath( key ) ).addElement( VirtualAppConstants.STYLE_ROOT_NAME ).build();
            final Resource resource = dynamicResourceManager.getResource( resourceFolderPath, VirtualAppConstants.STYLE_NAME );

            if ( resource.exists() && resource.getSize() > 0 )
            {
                final StyleDescriptor descriptor = dynamicResourceParser.parseStyles( key, resource.readString() );
                return new DynamicSchemaResult<>(
                    StyleDescriptor.copyOf( descriptor ).modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) ).build(),
                    resource );
            }
            return null;
        } );
    }

    @Override
    public boolean deleteComponent( final DeleteDynamicComponentParams params )
    {
        requireAdminRole();

        final NodePath resourceFolderPath = createComponentFolderPath( params.getKey(), params.getType() );
        return dynamicResourceManager.deleteResource( resourceFolderPath, params.getKey().getName(), true );
    }

    @Override
    public boolean deleteContentSchema( final DeleteDynamicContentSchemaParams params )
    {
        requireAdminRole();

        final NodePath resourceFolderPath = createSchemaFolderPath( params.getName(), params.getType() );
        return dynamicResourceManager.deleteResource( resourceFolderPath, params.getName().getLocalName(), true );
    }

    @Override
    public <T extends BaseSchema<?>> List<DynamicSchemaResult<T>> listContentSchemas( final ListDynamicContentSchemasParams params )
    {
        requireListAccess();

        final NodePath componentRootPath = createSchemaRootPath( params.getKey(), params.getType() );

        return VirtualAppContext.createAdminContext()
            .callWith( () -> dynamicResourceManager.listResources( componentRootPath ).stream().map( resource -> {

                final BaseSchema<?> schema = dynamicResourceParser.parseSchema(
                    getSchemaName( params.getKey(), params.getType(), getResourceName( resource.getKey() ) ), params.getType(),
                    resource.readString() );

                return new DynamicSchemaResult<T>( (T) wrapSchema( schema, resource.getTimestamp() ), resource );
            } ).collect( Collectors.<DynamicSchemaResult<T>>toList() ) );
    }

    @Override
    public boolean deleteCms( final ApplicationKey key )
    {
        requireAdminRole();

        final NodePath resourceFolderPath = createCmsFolderPath( key );
        return dynamicResourceManager.deleteResource( resourceFolderPath, VirtualAppConstants.CMS_ROOT_NAME, false );
    }

    @Override
    public boolean deleteStyles( final ApplicationKey key )
    {
        requireAdminRole();

        final NodePath resourceFolderPath =
            NodePath.create( createCmsFolderPath( key ) ).addElement( VirtualAppConstants.STYLE_ROOT_NAME ).build();
        return dynamicResourceManager.deleteResource( resourceFolderPath, VirtualAppConstants.STYLE_NAME, false );
    }

    @Override
    public DynamicSchemaResult<MacroDescriptor> createMacro( final CreateDynamicMacroParams params )
    {
        requireAdminRole();

        final MacroDescriptor descriptor = dynamicResourceParser.parseMacro( params.getKey(), params.getResource() );

        final NodePath resourceFolderPath = createMacroFolderPath( params.getKey() );
        final Resource resource =
            dynamicResourceManager.createResource( resourceFolderPath, params.getKey().getName(), params.getResource() );

        return new DynamicSchemaResult<>(
            MacroDescriptor.copyOf( descriptor ).modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) ).build(), resource );
    }

    @Override
    public DynamicSchemaResult<MacroDescriptor> updateMacro( final UpdateDynamicMacroParams params )
    {
        requireAdminRole();

        final MacroDescriptor descriptor = dynamicResourceParser.parseMacro( params.getKey(), params.getResource() );

        final NodePath resourceFolderPath = createMacroFolderPath( params.getKey() );
        final Resource resource =
            dynamicResourceManager.updateResource( resourceFolderPath, params.getKey().getName(), params.getResource() );

        return new DynamicSchemaResult<>(
            MacroDescriptor.copyOf( descriptor ).modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) ).build(), resource );
    }

    @Override
    public DynamicSchemaResult<MacroDescriptor> getMacro( final GetDynamicMacroParams params )
    {
        requireReadAccess();

        return VirtualAppContext.createAdminContext().callWith( () -> {
            final NodePath resourceFolderPath = createMacroFolderPath( params.getKey() );
            final Resource resource = dynamicResourceManager.getResource( resourceFolderPath, params.getKey().getName() );

            if ( resource.exists() && resource.getSize() > 0 )
            {
                final MacroDescriptor descriptor = dynamicResourceParser.parseMacro( params.getKey(), resource.readString() );
                return new DynamicSchemaResult<>(
                    MacroDescriptor.copyOf( descriptor ).modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) ).build(),
                    resource );
            }
            return null;
        } );
    }

    @Override
    public List<DynamicSchemaResult<MacroDescriptor>> listMacros( final ListDynamicMacrosParams params )
    {
        requireListAccess();

        return VirtualAppContext.createAdminContext().callWith( () -> dynamicResourceManager.listResources(
                createMacroRootPath( params.getKey() ) )
            .stream()
            .map( resource -> {
                final MacroDescriptor descriptor =
                    dynamicResourceParser.parseMacro( MacroKey.from( params.getKey(), getResourceName( resource.getKey() ) ),
                                                      resource.readString() );

                return new DynamicSchemaResult<>(
                    MacroDescriptor.copyOf( descriptor ).modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) ).build(),
                    resource );
            } )
            .collect( Collectors.toList() ) );
    }

    @Override
    public boolean deleteMacro( final DeleteDynamicMacroParams params )
    {
        requireAdminRole();

        final NodePath resourceFolderPath = createMacroFolderPath( params.getKey() );
        return dynamicResourceManager.deleteResource( resourceFolderPath, params.getKey().getName(), true );
    }

    private BaseSchemaName getSchemaName( final ApplicationKey applicationKey, final DynamicContentSchemaType type, final String name )
    {
        switch ( type )
        {
            case CONTENT_TYPE:
                return ContentTypeName.from( applicationKey, name );
            case FORM_FRAGMENT:
                return FormFragmentName.from( applicationKey, name );
            case MIXIN:
                return MixinName.from( applicationKey, name );
            default:
                throw new IllegalArgumentException( "invalid schema type: " + type );
        }
    }

    private String getResourceName( final ResourceKey resourceKey )
    {
        return resourceKey.getName().substring( 0, resourceKey.getName().lastIndexOf( "." + resourceKey.getExtension() ) );
    }

    private NodePath createComponentFolderPath( final DescriptorKey key, final DynamicComponentType dynamicType )
    {
        final NodePath componentRootPath = createComponentRootPath( key.getApplicationKey(), dynamicType );
        return new NodePath( componentRootPath, NodeName.from( key.getName() ) );
    }

    private NodePath createComponentRootPath( final ApplicationKey key, final DynamicComponentType dynamicType )
    {
        final String resourceRootName = getComponentRootName( dynamicType );
        return NodePath.create( VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT )
            .addElement( key.toString() )
            .addElement( VirtualAppConstants.CMS_ROOT_NAME )
            .addElement( resourceRootName )
            .build();
    }

    private NodePath createSchemaFolderPath( final BaseSchemaName key, final DynamicContentSchemaType dynamicType )
    {
        final NodePath schemaRootPath = createSchemaRootPath( key.getApplicationKey(), dynamicType );
        return new NodePath( schemaRootPath, NodeName.from( key.getLocalName() ) );
    }

    private NodePath createSchemaRootPath( final ApplicationKey key, final DynamicContentSchemaType dynamicType )
    {
        final String resourceRootName = getSchemaRootName( dynamicType );
        return NodePath.create( VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT )
            .addElement( key.toString() )
            .addElement( VirtualAppConstants.CMS_ROOT_NAME )
            .addElement( resourceRootName )
            .build();
    }

    private NodePath createCmsFolderPath( final ApplicationKey key )
    {
        return NodePath.create( VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT )
            .addElement( key.toString() )
            .addElement( VirtualAppConstants.CMS_ROOT_NAME )
            .build();
    }

    private NodePath createMacroFolderPath( final MacroKey key )
    {
        final NodePath macroRootPath = createMacroRootPath( key.getApplicationKey() );
        return new NodePath( macroRootPath, NodeName.from( key.getName() ) );
    }

    private NodePath createMacroRootPath( final ApplicationKey key )
    {
        return NodePath.create( VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT )
            .addElement( key.toString() )
            .addElement( VirtualAppConstants.CMS_ROOT_NAME )
            .addElement( VirtualAppConstants.MACROS_ROOT_NAME )
            .build();
    }

    private String getSchemaRootName( final DynamicContentSchemaType type )
    {
        switch ( type )
        {
            case CONTENT_TYPE:
                return VirtualAppConstants.CONTENT_TYPE_ROOT_NAME;
            case FORM_FRAGMENT:
                return VirtualAppConstants.FORM_FRAGMENTS_ROOT_NAME;
            case MIXIN:
                return VirtualAppConstants.MIXINS_ROOT_NAME;
            default:
                throw new IllegalArgumentException( "invalid dynamic schema type: " + type );
        }
    }

    private String getComponentRootName( final DynamicComponentType type )
    {
        switch ( type )
        {
            case PAGE:
                return VirtualAppConstants.PAGE_ROOT_NAME;
            case PART:
                return VirtualAppConstants.PART_ROOT_NAME;
            case LAYOUT:
                return VirtualAppConstants.LAYOUT_ROOT_NAME;
            default:
                throw new IllegalArgumentException( "invalid dynamic component type: " + type );
        }
    }

    private void requireAdminRole()
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        final boolean hasAdminRole = authInfo.hasRole( RoleKeys.ADMIN ) || authInfo.hasRole( RoleKeys.SCHEMA_ADMIN );
        if ( !hasAdminRole )
        {
            throw new ForbiddenAccessException( authInfo.getUser() );
        }
    }

    private void requireListAccess()
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        final boolean hasListAccess = hasAdminAccess( authInfo ) || authInfo.getPrincipals()
            .stream()
            .anyMatch( principal -> isProjectRole( principal ) && principal.getId().endsWith( PROJECT_OWNER_ROLE_SUFFIX ) );
        if ( !hasListAccess )
        {
            throw new ForbiddenAccessException( authInfo.getUser() );
        }
    }

    private void requireReadAccess()
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        final boolean hasReadAccess =
            hasAdminAccess( authInfo ) || authInfo.getPrincipals().stream().anyMatch( SchemaServiceImpl::isProjectRole );
        if ( !hasReadAccess )
        {
            throw new ForbiddenAccessException( authInfo.getUser() );
        }
    }

    private static boolean hasAdminAccess( final AuthenticationInfo authInfo )
    {
        return authInfo.hasRole( RoleKeys.ADMIN ) || authInfo.hasRole( RoleKeys.SCHEMA_ADMIN ) ||
            authInfo.hasRole( RoleKeys.CONTENT_MANAGER_ADMIN );
    }

    private static boolean isProjectRole( final PrincipalKey principal )
    {
        return principal.isRole() && principal.getId().startsWith( ProjectConstants.PROJECT_NAME_PREFIX );
    }

    private ComponentDescriptor wrapDescriptor( final ComponentDescriptor componentDescriptor, final long modifiedTime )
    {
        if ( componentDescriptor instanceof PageDescriptor )
        {
            return PageDescriptor.copyOf( (PageDescriptor) componentDescriptor )
                .modifiedTime( Instant.ofEpochMilli( modifiedTime ) )
                .build();
        }
        if ( componentDescriptor instanceof PartDescriptor )
        {
            return PartDescriptor.copyOf( (PartDescriptor) componentDescriptor )
                .modifiedTime( Instant.ofEpochMilli( modifiedTime ) )
                .build();
        }
        if ( componentDescriptor instanceof LayoutDescriptor )
        {
            return LayoutDescriptor.copyOf( (LayoutDescriptor) componentDescriptor )
                .modifiedTime( Instant.ofEpochMilli( modifiedTime ) )
                .build();
        }

        throw new IllegalArgumentException( "unknown type of ComponentDescriptor: " + componentDescriptor.getKey() );
    }

    private BaseSchema<?> wrapSchema( final BaseSchema<?> baseSchema, final long modifiedTime )
    {
        if ( baseSchema instanceof ContentType )
        {
            return ContentType.create( (ContentType) baseSchema ).modifiedTime( Instant.ofEpochMilli( modifiedTime ) ).build();
        }
        if ( baseSchema instanceof FormFragmentDescriptor )
        {
            return FormFragmentDescriptor.create( (FormFragmentDescriptor) baseSchema )
                .modifiedTime( Instant.ofEpochMilli( modifiedTime ) )
                .build();
        }
        if ( baseSchema instanceof MixinDescriptor )
        {
            return MixinDescriptor.create( (MixinDescriptor) baseSchema ).modifiedTime( Instant.ofEpochMilli( modifiedTime ) ).build();
        }

        throw new IllegalArgumentException( "unknown type of BaseSchema: " + baseSchema.getName() );
    }
}
