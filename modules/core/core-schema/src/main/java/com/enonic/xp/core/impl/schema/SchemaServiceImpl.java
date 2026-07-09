package com.enonic.xp.core.impl.schema;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.CreateNamespaceParams;
import com.enonic.xp.app.Namespace;
import com.enonic.xp.app.UpdateNamespaceParams;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.impl.schema.parser.YmlCmsDescriptorParser;
import com.enonic.xp.core.impl.schema.parser.YmlContentTypeParser;
import com.enonic.xp.core.impl.schema.parser.YmlLayoutDescriptorParser;
import com.enonic.xp.core.impl.schema.parser.YmlMixinDescriptorParser;
import com.enonic.xp.core.impl.schema.parser.YmlPageDescriptorParser;
import com.enonic.xp.core.impl.schema.parser.YmlPartDescriptorParser;
import com.enonic.xp.core.impl.schema.parser.YmlStyleDescriptorParser;
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
import com.enonic.xp.schema.CreateCmsParams;
import com.enonic.xp.schema.CreateComponentParams;
import com.enonic.xp.schema.CreateContentSchemaParams;
import com.enonic.xp.schema.CreatePhrasesParams;
import com.enonic.xp.schema.CreateStylesParams;
import com.enonic.xp.schema.CreateMacroParams;
import com.enonic.xp.schema.DeleteMacroParams;
import com.enonic.xp.schema.DeletePhrasesParams;
import com.enonic.xp.schema.SchemaResult;
import com.enonic.xp.schema.GetMacroParams;
import com.enonic.xp.schema.GetPhrasesParams;
import com.enonic.xp.schema.ListMacrosParams;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.SchemaService;
import com.enonic.xp.schema.UpdateCmsParams;
import com.enonic.xp.schema.UpdateComponentParams;
import com.enonic.xp.schema.UpdateContentSchemaParams;
import com.enonic.xp.schema.UpdateMacroParams;
import com.enonic.xp.schema.UpdatePhrasesParams;
import com.enonic.xp.schema.UpdateStylesParams;
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

@Component(immediate = true, service = SchemaService.class)
public class SchemaServiceImpl
    implements SchemaService
{
    private static final String PROJECT_OWNER_ROLE_SUFFIX = "." + ProjectRole.OWNER.name().toLowerCase();

    private final NamespaceResourceManager dynamicResourceManager;

    private final ApplicationService applicationService;

    private final NamespaceAppService namespaceAppService;

    private final SchemaAuditLogSupport schemaAuditLogSupport;

    @Activate
    public SchemaServiceImpl( @Reference final NodeService nodeService, @Reference final ResourceService resourceService,
                              @Reference final ApplicationService applicationService, @Reference final NamespaceAppService namespaceAppService,
                              @Reference final SchemaAuditLogSupport schemaAuditLogSupport )
    {
        this.dynamicResourceManager = new NamespaceResourceManager( nodeService, resourceService );
        this.applicationService = applicationService;
        this.namespaceAppService = namespaceAppService;
        this.schemaAuditLogSupport = schemaAuditLogSupport;
    }

    @Override
    public ApplicationKeys listApplicationKeys()
    {
        return ApplicationKeys.from( Stream.concat( applicationService.getInstalledApplications().stream().map( Application::getKey ),
                                                    namespaceAppService.listNamespaces().stream().map( Namespace::getKey ) )
                                         .collect( Collectors.toCollection( LinkedHashSet::new ) ) );
    }

    @Override
    public Namespace createNamespace( final CreateNamespaceParams params )
    {
        final Namespace namespace = this.namespaceAppService.create( params );

        schemaAuditLogSupport.createNamespace( params, namespace );

        return namespace;
    }

    @Override
    public Namespace updateNamespace( final UpdateNamespaceParams params )
    {
        final Namespace namespace = this.namespaceAppService.update( params );

        schemaAuditLogSupport.updateNamespace( params, namespace );

        return namespace;
    }

    @Override
    public boolean deleteNamespace( final ApplicationKey key )
    {
        final boolean deleted = this.namespaceAppService.delete( key );

        if ( deleted )
        {
            schemaAuditLogSupport.deleteNamespace( key );
        }

        return deleted;
    }

    @Override
    public Namespace getNamespace( final ApplicationKey key )
    {
        return this.namespaceAppService.getNamespace( key );
    }

    @Override
    public List<Namespace> listNamespaces()
    {
        return this.namespaceAppService.listNamespaces();
    }

    @Override
    public SchemaResult<PartDescriptor> createPart( final CreateComponentParams params )
    {
        return doCreateComponent( params, ComponentType.PART );
    }

    @Override
    public SchemaResult<PartDescriptor> updatePart( final UpdateComponentParams params )
    {
        return doUpdateComponent( params, ComponentType.PART );
    }

    @Override
    public SchemaResult<PartDescriptor> getPart( final DescriptorKey key )
    {
        return doGetComponent( key, ComponentType.PART );
    }

    @Override
    public List<SchemaResult<PartDescriptor>> listParts( final ApplicationKey key )
    {
        return doListComponents( key, ComponentType.PART );
    }

    @Override
    public boolean deletePart( final DescriptorKey key )
    {
        return doDeleteComponent( key, ComponentType.PART );
    }

    @Override
    public SchemaResult<LayoutDescriptor> createLayout( final CreateComponentParams params )
    {
        return doCreateComponent( params, ComponentType.LAYOUT );
    }

    @Override
    public SchemaResult<LayoutDescriptor> updateLayout( final UpdateComponentParams params )
    {
        return doUpdateComponent( params, ComponentType.LAYOUT );
    }

    @Override
    public SchemaResult<LayoutDescriptor> getLayout( final DescriptorKey key )
    {
        return doGetComponent( key, ComponentType.LAYOUT );
    }

    @Override
    public List<SchemaResult<LayoutDescriptor>> listLayouts( final ApplicationKey key )
    {
        return doListComponents( key, ComponentType.LAYOUT );
    }

    @Override
    public boolean deleteLayout( final DescriptorKey key )
    {
        return doDeleteComponent( key, ComponentType.LAYOUT );
    }

    @Override
    public SchemaResult<PageDescriptor> createPage( final CreateComponentParams params )
    {
        return doCreateComponent( params, ComponentType.PAGE );
    }

    @Override
    public SchemaResult<PageDescriptor> updatePage( final UpdateComponentParams params )
    {
        return doUpdateComponent( params, ComponentType.PAGE );
    }

    @Override
    public SchemaResult<PageDescriptor> getPage( final DescriptorKey key )
    {
        return doGetComponent( key, ComponentType.PAGE );
    }

    @Override
    public List<SchemaResult<PageDescriptor>> listPages( final ApplicationKey key )
    {
        return doListComponents( key, ComponentType.PAGE );
    }

    @Override
    public boolean deletePage( final DescriptorKey key )
    {
        return doDeleteComponent( key, ComponentType.PAGE );
    }

    @Override
    public SchemaResult<ContentType> createContentType( final CreateContentSchemaParams params )
    {
        return doCreateContentSchema( params, ContentSchemaType.CONTENT_TYPE );
    }

    @Override
    public SchemaResult<ContentType> updateContentType( final UpdateContentSchemaParams params )
    {
        return doUpdateContentSchema( params, ContentSchemaType.CONTENT_TYPE );
    }

    @Override
    public SchemaResult<ContentType> getContentType( final ContentTypeName name )
    {
        return doGetContentSchema( name, ContentSchemaType.CONTENT_TYPE );
    }

    @Override
    public List<SchemaResult<ContentType>> listContentTypes( final ApplicationKey key )
    {
        return doListContentSchemas( key, ContentSchemaType.CONTENT_TYPE );
    }

    @Override
    public boolean deleteContentType( final ContentTypeName name )
    {
        return doDeleteContentSchema( name, ContentSchemaType.CONTENT_TYPE );
    }

    @Override
    public SchemaResult<FormFragmentDescriptor> createFormFragment( final CreateContentSchemaParams params )
    {
        return doCreateContentSchema( params, ContentSchemaType.FORM_FRAGMENT );
    }

    @Override
    public SchemaResult<FormFragmentDescriptor> updateFormFragment( final UpdateContentSchemaParams params )
    {
        return doUpdateContentSchema( params, ContentSchemaType.FORM_FRAGMENT );
    }

    @Override
    public SchemaResult<FormFragmentDescriptor> getFormFragment( final FormFragmentName name )
    {
        return doGetContentSchema( name, ContentSchemaType.FORM_FRAGMENT );
    }

    @Override
    public List<SchemaResult<FormFragmentDescriptor>> listFormFragments( final ApplicationKey key )
    {
        return doListContentSchemas( key, ContentSchemaType.FORM_FRAGMENT );
    }

    @Override
    public boolean deleteFormFragment( final FormFragmentName name )
    {
        return doDeleteContentSchema( name, ContentSchemaType.FORM_FRAGMENT );
    }

    @Override
    public SchemaResult<MixinDescriptor> createMixin( final CreateContentSchemaParams params )
    {
        return doCreateContentSchema( params, ContentSchemaType.MIXIN );
    }

    @Override
    public SchemaResult<MixinDescriptor> updateMixin( final UpdateContentSchemaParams params )
    {
        return doUpdateContentSchema( params, ContentSchemaType.MIXIN );
    }

    @Override
    public SchemaResult<MixinDescriptor> getMixin( final MixinName name )
    {
        return doGetContentSchema( name, ContentSchemaType.MIXIN );
    }

    @Override
    public List<SchemaResult<MixinDescriptor>> listMixins( final ApplicationKey key )
    {
        return doListContentSchemas( key, ContentSchemaType.MIXIN );
    }

    @Override
    public boolean deleteMixin( final MixinName name )
    {
        return doDeleteContentSchema( name, ContentSchemaType.MIXIN );
    }

    private <T extends ComponentDescriptor> SchemaResult<T> doCreateComponent( final CreateComponentParams params,
                                                                               final ComponentType type )
    {
        requireAdminRole();

        final ComponentDescriptor descriptor = parseComponent( params.getKey(), type, params.getResource() );

        final NodePath resourceFolderPath = createComponentFolderPath( params.getKey(), type );
        final Resource resource =
            dynamicResourceManager.createResource( resourceFolderPath, params.getKey().getName(), params.getResource() );

        final SchemaResult<T> result = new SchemaResult<>( (T) wrapDescriptor( descriptor, resource.getTimestamp() ), resource );

        schemaAuditLogSupport.createComponent( params, type, result );

        return result;
    }

    private <T extends ComponentDescriptor> SchemaResult<T> doUpdateComponent( final UpdateComponentParams params,
                                                                               final ComponentType type )
    {
        requireAdminRole();

        final ComponentDescriptor descriptor = parseComponent( params.getKey(), type, params.getResource() );

        final NodePath resourceFolderPath = createComponentFolderPath( params.getKey(), type );
        final Resource resource =
            dynamicResourceManager.updateResource( resourceFolderPath, params.getKey().getName(), params.getResource() );

        final SchemaResult<T> result = new SchemaResult<>( (T) wrapDescriptor( descriptor, resource.getTimestamp() ), resource );

        schemaAuditLogSupport.updateComponent( params, type, result );

        return result;
    }

    private <T extends BaseSchema<?>> SchemaResult<T> doCreateContentSchema( final CreateContentSchemaParams params,
                                                                             final ContentSchemaType type )
    {
        requireAdminRole();

        final BaseSchema<?> schema = parseSchema( params.getName(), type, params.getResource() );

        final NodePath resourceFolderPath = createSchemaFolderPath( params.getName(), type );
        final Resource resource =
            dynamicResourceManager.createResource( resourceFolderPath, params.getName().getLocalName(), params.getResource() );

        final SchemaResult<T> result = new SchemaResult<>( (T) wrapSchema( schema, resource.getTimestamp() ), resource );

        schemaAuditLogSupport.createContentSchema( params, type, result );

        return result;
    }

    private <T extends BaseSchema<?>> SchemaResult<T> doUpdateContentSchema( final UpdateContentSchemaParams params,
                                                                             final ContentSchemaType type )
    {
        requireAdminRole();

        final BaseSchema<?> schema = parseSchema( params.getName(), type, params.getResource() );

        final NodePath resourceFolderPath = createSchemaFolderPath( params.getName(), type );
        final Resource resource =
            dynamicResourceManager.updateResource( resourceFolderPath, params.getName().getLocalName(), params.getResource() );

        final SchemaResult<T> result = new SchemaResult<>( (T) wrapSchema( schema, resource.getTimestamp() ), resource );

        schemaAuditLogSupport.updateContentSchema( params, type, result );

        return result;
    }

    @Override
    public SchemaResult<CmsDescriptor> createCms( final CreateCmsParams params )
    {
        requireAdminRole();

        final CmsDescriptor site = parseCms( params.getKey(), params.getResource() );

        final NodePath resourceFolderPath = createCmsFolderPath( params.getKey() );
        final Resource createdResource =
            dynamicResourceManager.createResource( resourceFolderPath, NamespaceConstants.CMS_ROOT_NAME, params.getResource() );

        final SchemaResult<CmsDescriptor> result = new SchemaResult<>(
            CmsDescriptor.copyOf( site ).modifiedTime( Instant.ofEpochMilli( createdResource.getTimestamp() ) ).build(), createdResource );

        schemaAuditLogSupport.createCms( params, result );

        return result;
    }

    @Override
    public SchemaResult<CmsDescriptor> updateCms( final UpdateCmsParams params )
    {
        requireAdminRole();

        final CmsDescriptor cmsDescriptor = parseCms( params.getKey(), params.getResource() );

        final NodePath resourceFolderPath = createCmsFolderPath( params.getKey() );

        final Resource resource =
            dynamicResourceManager.updateResource( resourceFolderPath, NamespaceConstants.CMS_ROOT_NAME, params.getResource() );

        final SchemaResult<CmsDescriptor> result = new SchemaResult<>(
            CmsDescriptor.copyOf( cmsDescriptor ).modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) ).build(), resource );

        schemaAuditLogSupport.updateCms( params, result );

        return result;
    }

    @Override
    public SchemaResult<StyleDescriptor> createStyles( final CreateStylesParams params )
    {
        requireAdminRole();

        final StyleDescriptor styles = parseStyles( params.getKey(), params.getResource() );

        final NodePath resourceFolderPath =
            NodePath.create( createCmsFolderPath( params.getKey() ) ).addElement( NamespaceConstants.STYLE_ROOT_NAME ).build();
        final Resource resource =
            dynamicResourceManager.createResource( resourceFolderPath, NamespaceConstants.STYLE_NAME, params.getResource() );

        final SchemaResult<StyleDescriptor> result = new SchemaResult<>(
            StyleDescriptor.copyOf( styles ).modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) ).build(), resource );

        schemaAuditLogSupport.createStyles( params, result );

        return result;
    }

    @Override
    public SchemaResult<StyleDescriptor> updateStyles( final UpdateStylesParams params )
    {
        requireAdminRole();

        final StyleDescriptor styles = parseStyles( params.getKey(), params.getResource() );

        final NodePath resourceFolderPath =
            NodePath.create( createCmsFolderPath( params.getKey() ) ).addElement( NamespaceConstants.STYLE_ROOT_NAME ).build();
        final Resource resource =
            dynamicResourceManager.updateResource( resourceFolderPath, NamespaceConstants.STYLE_NAME, params.getResource() );

        final SchemaResult<StyleDescriptor> result = new SchemaResult<>(
            StyleDescriptor.copyOf( styles ).modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) ).build(), resource );

        schemaAuditLogSupport.updateStyles( params, result );

        return result;
    }

    @Override
    public Resource createPhrases( final CreatePhrasesParams params )
    {
        requireAdminRole();

        final Resource resource = dynamicResourceManager.createResourceFile( createPhrasesFolderPath( params.getKey() ),
                                                                             phrasesFileName( params.getName() ), params.getResource() );

        schemaAuditLogSupport.createPhrases( params, resource );

        return resource;
    }

    @Override
    public Resource updatePhrases( final UpdatePhrasesParams params )
    {
        requireAdminRole();

        final Resource resource = dynamicResourceManager.updateResourceFile( createPhrasesFolderPath( params.getKey() ),
                                                                             phrasesFileName( params.getName() ), params.getResource() );

        schemaAuditLogSupport.updatePhrases( params, resource );

        return resource;
    }

    @Override
    public Resource getPhrases( final GetPhrasesParams params )
    {
        requireReadAccess();

        return NamespaceContext.createAdminContext().callWith( () -> {
            final Resource resource =
                dynamicResourceManager.getResourceFile( createPhrasesFolderPath( params.getKey() ), phrasesFileName( params.getName() ) );

            return resource.exists() && resource.getSize() > 0 ? resource : null;
        } );
    }

    @Override
    public List<Resource> listPhrases( final ApplicationKey key )
    {
        requireListAccess();

        return NamespaceContext.createAdminContext()
            .callWith( () -> dynamicResourceManager.listResourceFiles( createPhrasesFolderPath( key ), "[^/]+\\.properties" ) );
    }

    @Override
    public boolean deletePhrases( final DeletePhrasesParams params )
    {
        requireAdminRole();

        final boolean deleted = dynamicResourceManager.deleteResourceFile( createPhrasesFolderPath( params.getKey() ),
                                                                           phrasesFileName( params.getName() ), false );

        if ( deleted )
        {
            schemaAuditLogSupport.deletePhrases( params );
        }

        return deleted;
    }

    private <T extends ComponentDescriptor> SchemaResult<T> doGetComponent( final DescriptorKey key, final ComponentType type )
    {
        requireReadAccess();

        return NamespaceContext.createAdminContext().callWith( () -> {
            final NodePath resourceFolderPath = createComponentFolderPath( key, type );
            final Resource resource = dynamicResourceManager.getResource( resourceFolderPath, key.getName() );

            if ( resource.exists() && resource.getSize() > 0 )
            {
                final ComponentDescriptor descriptor = parseComponent( key, type, resource.readString() );
                return new SchemaResult<>( (T) wrapDescriptor( descriptor, resource.getTimestamp() ), resource );
            }
            return null;
        } );
    }

    private <T extends ComponentDescriptor> List<SchemaResult<T>> doListComponents( final ApplicationKey key, final ComponentType type )
    {
        requireListAccess();

        return NamespaceContext.createAdminContext().callWith( () -> dynamicResourceManager.listResources(
                createComponentRootPath( key, type ) )
            .stream()
            .map( resource -> {
                final ComponentDescriptor descriptor =
                    parseComponent( DescriptorKey.from( key, getResourceName( resource.getKey() ) ), type,
                                                          resource.readString() );

                return new SchemaResult<>( (T) wrapDescriptor( descriptor, resource.getTimestamp() ), resource );
            } )
            .collect( Collectors.<SchemaResult<T>>toList() ) );
    }


    private <T extends BaseSchema<?>> SchemaResult<T> doGetContentSchema( final BaseSchemaName name, final ContentSchemaType type )
    {
        requireReadAccess();

        return NamespaceContext.createAdminContext().callWith( () -> {
            final NodePath resourceFolderPath = createSchemaFolderPath( name, type );
            final Resource resource = dynamicResourceManager.getResource( resourceFolderPath, name.getLocalName() );

            if ( resource.exists() && resource.getSize() > 0 )
            {
                final BaseSchema<?> schema = parseSchema( name, type, resource.readString() );
                return new SchemaResult<>( (T) wrapSchema( schema, resource.getTimestamp() ), resource );
            }

            return null;
        } );
    }

    @Override
    public SchemaResult<CmsDescriptor> getCmsDescriptor( final ApplicationKey key )
    {
        requireReadAccess();

        return NamespaceContext.createAdminContext().callWith( () -> {
            final NodePath resourceFolderPath = createCmsFolderPath( key );

            final Resource resource = dynamicResourceManager.getResource( resourceFolderPath, NamespaceConstants.CMS_ROOT_NAME );

            if ( resource.exists() && resource.getSize() > 0 )
            {
                final CmsDescriptor siteDescriptor = parseCms( key, resource.readString() );
                return new SchemaResult<>(
                    CmsDescriptor.copyOf( siteDescriptor ).modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) ).build(),
                    resource );
            }
            return null;
        } );
    }

    @Override
    public SchemaResult<StyleDescriptor> getStyles( final ApplicationKey key )
    {
        requireReadAccess();

        return NamespaceContext.createAdminContext().callWith( () -> {
            final NodePath resourceFolderPath =
                NodePath.create( createCmsFolderPath( key ) ).addElement( NamespaceConstants.STYLE_ROOT_NAME ).build();
            final Resource resource = dynamicResourceManager.getResource( resourceFolderPath, NamespaceConstants.STYLE_NAME );

            if ( resource.exists() && resource.getSize() > 0 )
            {
                final StyleDescriptor descriptor = parseStyles( key, resource.readString() );
                return new SchemaResult<>(
                    StyleDescriptor.copyOf( descriptor ).modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) ).build(),
                    resource );
            }
            return null;
        } );
    }

    private boolean doDeleteComponent( final DescriptorKey key, final ComponentType type )
    {
        requireAdminRole();

        final NodePath resourceFolderPath = createComponentFolderPath( key, type );
        final boolean deleted = dynamicResourceManager.deleteResource( resourceFolderPath, key.getName(), true );

        if ( deleted )
        {
            schemaAuditLogSupport.deleteComponent( key, type );
        }

        return deleted;
    }

    private boolean doDeleteContentSchema( final BaseSchemaName name, final ContentSchemaType type )
    {
        requireAdminRole();

        final NodePath resourceFolderPath = createSchemaFolderPath( name, type );
        final boolean deleted = dynamicResourceManager.deleteResource( resourceFolderPath, name.getLocalName(), true );

        if ( deleted )
        {
            schemaAuditLogSupport.deleteContentSchema( name, type );
        }

        return deleted;
    }

    private <T extends BaseSchema<?>> List<SchemaResult<T>> doListContentSchemas( final ApplicationKey key, final ContentSchemaType type )
    {
        requireListAccess();

        final NodePath componentRootPath = createSchemaRootPath( key, type );

        return NamespaceContext.createAdminContext()
            .callWith( () -> dynamicResourceManager.listResources( componentRootPath ).stream().map( resource -> {

                final BaseSchema<?> schema =
                    parseSchema( getSchemaName( key, type, getResourceName( resource.getKey() ) ), type,
                                                       resource.readString() );

                return new SchemaResult<T>( (T) wrapSchema( schema, resource.getTimestamp() ), resource );
            } ).collect( Collectors.<SchemaResult<T>>toList() ) );
    }

    @Override
    public boolean deleteCms( final ApplicationKey key )
    {
        requireAdminRole();

        final NodePath resourceFolderPath = createCmsFolderPath( key );
        final boolean deleted = dynamicResourceManager.deleteResource( resourceFolderPath, NamespaceConstants.CMS_ROOT_NAME, false );

        if ( deleted )
        {
            schemaAuditLogSupport.deleteCms( key );
        }

        return deleted;
    }

    @Override
    public boolean deleteStyles( final ApplicationKey key )
    {
        requireAdminRole();

        final NodePath resourceFolderPath =
            NodePath.create( createCmsFolderPath( key ) ).addElement( NamespaceConstants.STYLE_ROOT_NAME ).build();
        final boolean deleted = dynamicResourceManager.deleteResource( resourceFolderPath, NamespaceConstants.STYLE_NAME, false );

        if ( deleted )
        {
            schemaAuditLogSupport.deleteStyles( key );
        }

        return deleted;
    }

    @Override
    public SchemaResult<MacroDescriptor> createMacro( final CreateMacroParams params )
    {
        requireAdminRole();

        final MacroDescriptor descriptor = parseMacro( params.getKey(), params.getResource() );

        final NodePath resourceFolderPath = createMacroFolderPath( params.getKey() );
        final Resource resource =
            dynamicResourceManager.createResource( resourceFolderPath, params.getKey().getName(), params.getResource() );

        final SchemaResult<MacroDescriptor> result = new SchemaResult<>(
            MacroDescriptor.copyOf( descriptor ).modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) ).build(), resource );

        schemaAuditLogSupport.createMacro( params, result );

        return result;
    }

    @Override
    public SchemaResult<MacroDescriptor> updateMacro( final UpdateMacroParams params )
    {
        requireAdminRole();

        final MacroDescriptor descriptor = parseMacro( params.getKey(), params.getResource() );

        final NodePath resourceFolderPath = createMacroFolderPath( params.getKey() );
        final Resource resource =
            dynamicResourceManager.updateResource( resourceFolderPath, params.getKey().getName(), params.getResource() );

        final SchemaResult<MacroDescriptor> result = new SchemaResult<>(
            MacroDescriptor.copyOf( descriptor ).modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) ).build(), resource );

        schemaAuditLogSupport.updateMacro( params, result );

        return result;
    }

    @Override
    public SchemaResult<MacroDescriptor> getMacro( final GetMacroParams params )
    {
        requireReadAccess();

        return NamespaceContext.createAdminContext().callWith( () -> {
            final NodePath resourceFolderPath = createMacroFolderPath( params.getKey() );
            final Resource resource = dynamicResourceManager.getResource( resourceFolderPath, params.getKey().getName() );

            if ( resource.exists() && resource.getSize() > 0 )
            {
                final MacroDescriptor descriptor = parseMacro( params.getKey(), resource.readString() );
                return new SchemaResult<>(
                    MacroDescriptor.copyOf( descriptor ).modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) ).build(),
                    resource );
            }
            return null;
        } );
    }

    @Override
    public List<SchemaResult<MacroDescriptor>> listMacros( final ListMacrosParams params )
    {
        requireListAccess();

        return NamespaceContext.createAdminContext().callWith( () -> dynamicResourceManager.listResources(
                createMacroRootPath( params.getKey() ) )
            .stream()
            .map( resource -> {
                final MacroDescriptor descriptor =
                    parseMacro( MacroKey.from( params.getKey(), getResourceName( resource.getKey() ) ),
                                                      resource.readString() );

                return new SchemaResult<>(
                    MacroDescriptor.copyOf( descriptor ).modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) ).build(),
                    resource );
            } )
            .collect( Collectors.toList() ) );
    }

    @Override
    public boolean deleteMacro( final DeleteMacroParams params )
    {
        requireAdminRole();

        final NodePath resourceFolderPath = createMacroFolderPath( params.getKey() );
        final boolean deleted = dynamicResourceManager.deleteResource( resourceFolderPath, params.getKey().getName(), true );

        if ( deleted )
        {
            schemaAuditLogSupport.deleteMacro( params );
        }

        return deleted;
    }

    private BaseSchemaName getSchemaName( final ApplicationKey applicationKey, final ContentSchemaType type, final String name )
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

    private NodePath createComponentFolderPath( final DescriptorKey key, final ComponentType dynamicType )
    {
        final NodePath componentRootPath = createComponentRootPath( key.getApplicationKey(), dynamicType );
        return new NodePath( componentRootPath, NodeName.from( key.getName() ) );
    }

    private NodePath createComponentRootPath( final ApplicationKey key, final ComponentType dynamicType )
    {
        final String resourceRootName = getComponentRootName( dynamicType );
        return NodePath.create( NamespaceConstants.NAMESPACE_APP_ROOT_PARENT )
            .addElement( key.toString() )
            .addElement( NamespaceConstants.CMS_ROOT_NAME )
            .addElement( resourceRootName )
            .build();
    }

    private NodePath createSchemaFolderPath( final BaseSchemaName key, final ContentSchemaType dynamicType )
    {
        final NodePath schemaRootPath = createSchemaRootPath( key.getApplicationKey(), dynamicType );
        return new NodePath( schemaRootPath, NodeName.from( key.getLocalName() ) );
    }

    private NodePath createSchemaRootPath( final ApplicationKey key, final ContentSchemaType dynamicType )
    {
        final String resourceRootName = getSchemaRootName( dynamicType );
        return NodePath.create( NamespaceConstants.NAMESPACE_APP_ROOT_PARENT )
            .addElement( key.toString() )
            .addElement( NamespaceConstants.CMS_ROOT_NAME )
            .addElement( resourceRootName )
            .build();
    }

    private NodePath createCmsFolderPath( final ApplicationKey key )
    {
        return NodePath.create( NamespaceConstants.NAMESPACE_APP_ROOT_PARENT )
            .addElement( key.toString() )
            .addElement( NamespaceConstants.CMS_ROOT_NAME )
            .build();
    }

    private NodePath createPhrasesFolderPath( final ApplicationKey key )
    {
        return NodePath.create( createCmsFolderPath( key ) )
            .addElement( NamespaceConstants.I18N_ROOT_NAME )
            .addElement( NamespaceConstants.PHRASES_ROOT_NAME )
            .build();
    }

    private static String phrasesFileName( final String name )
    {
        return name.endsWith( ".properties" ) ? name : name + ".properties";
    }

    private NodePath createMacroFolderPath( final MacroKey key )
    {
        final NodePath macroRootPath = createMacroRootPath( key.getApplicationKey() );
        return new NodePath( macroRootPath, NodeName.from( key.getName() ) );
    }

    private NodePath createMacroRootPath( final ApplicationKey key )
    {
        return NodePath.create( NamespaceConstants.NAMESPACE_APP_ROOT_PARENT )
            .addElement( key.toString() )
            .addElement( NamespaceConstants.CMS_ROOT_NAME )
            .addElement( NamespaceConstants.MACROS_ROOT_NAME )
            .build();
    }

    private String getSchemaRootName( final ContentSchemaType type )
    {
        switch ( type )
        {
            case CONTENT_TYPE:
                return NamespaceConstants.CONTENT_TYPE_ROOT_NAME;
            case FORM_FRAGMENT:
                return NamespaceConstants.FORM_FRAGMENTS_ROOT_NAME;
            case MIXIN:
                return NamespaceConstants.MIXINS_ROOT_NAME;
            default:
                throw new IllegalArgumentException( "invalid dynamic schema type: " + type );
        }
    }

    private String getComponentRootName( final ComponentType type )
    {
        switch ( type )
        {
            case PAGE:
                return NamespaceConstants.PAGE_ROOT_NAME;
            case PART:
                return NamespaceConstants.PART_ROOT_NAME;
            case LAYOUT:
                return NamespaceConstants.LAYOUT_ROOT_NAME;
            default:
                throw new IllegalArgumentException( "invalid dynamic component type: " + type );
        }
    }

    private static ComponentDescriptor parseComponent( final DescriptorKey key, final ComponentType type, final String resource )
    {
        switch ( type )
        {
            case PAGE:
                return YmlPageDescriptorParser.parse( resource, key.getApplicationKey() ).key( key ).build();
            case PART:
                final PartDescriptor.Builder builder = YmlPartDescriptorParser.parse( resource, key.getApplicationKey() );
                builder.key( key );
                return builder.build();
            case LAYOUT:
                return YmlLayoutDescriptorParser.parse( resource, key.getApplicationKey() ).key( key ).build();
            default:
                throw new IllegalArgumentException( String.format( "unknown dynamic component type: '%s'", type ) );
        }
    }

    private static BaseSchema<?> parseSchema( final BaseSchemaName name, final ContentSchemaType type, final String resource )
    {
        switch ( type )
        {
            case CONTENT_TYPE:
                try
                {
                    final ContentType.Builder builder = YmlContentTypeParser.parse( resource, name.getApplicationKey() );
                    builder.name( (ContentTypeName) name );
                    return builder.build();
                }
                catch ( Exception e )
                {
                    throw new RuntimeException( String.format( "Could not parse dynamic content type [%s]", name ), e );
                }
            case FORM_FRAGMENT:
                return YmlFormFragmentParser.parse( resource, name.getApplicationKey() ).name( (FormFragmentName) name ).build();
            case MIXIN:
                return YmlMixinDescriptorParser.parse( resource, name.getApplicationKey() ).name( (MixinName) name ).build();
            default:
                throw new IllegalArgumentException( String.format( "unknown dynamic schema type: '%s'", type ) );
        }
    }

    private static CmsDescriptor parseCms( final ApplicationKey applicationKey, final String resource )
    {
        return YmlCmsDescriptorParser.parse( resource, applicationKey ).build();
    }

    private static StyleDescriptor parseStyles( final ApplicationKey applicationKey, final String resource )
    {
        return YmlStyleDescriptorParser.parse( resource, applicationKey ).build();
    }

    private static MacroDescriptor parseMacro( final MacroKey key, final String resource )
    {
        return YmlMacroDescriptorParser.parse( resource, key.getApplicationKey() ).key( key ).build();
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
