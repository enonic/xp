package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.resource.CreateDynamicComponentParams;
import com.enonic.xp.resource.CreateDynamicContentSchemaParams;
import com.enonic.xp.resource.CreateDynamicMacroParams;
import com.enonic.xp.resource.CreateDynamicPhrasesParams;
import com.enonic.xp.resource.CreateDynamicStylesParams;
import com.enonic.xp.resource.DeleteDynamicPhrasesParams;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.resource.GetDynamicPhrasesParams;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.SetDynamicComponentIconParams;
import com.enonic.xp.resource.SetDynamicContentSchemaIconParams;
import com.enonic.xp.resource.SetDynamicMacroIconParams;
import com.enonic.xp.resource.UpdateDynamicCmsParams;
import com.enonic.xp.resource.UpdateDynamicComponentParams;
import com.enonic.xp.resource.UpdateDynamicContentSchemaParams;
import com.enonic.xp.resource.UpdateDynamicMacroParams;
import com.enonic.xp.resource.UpdateDynamicPhrasesParams;
import com.enonic.xp.resource.UpdateDynamicStylesParams;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.schema.BaseSchemaName;
import com.enonic.xp.schema.SchemaNotFoundException;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentDescriptor;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.style.StyleDescriptor;

@Component(immediate = true, service = {DynamicSchemaService.class, DynamicSchemaServiceInternal.class})
public class DynamicSchemaServiceImpl
    implements DynamicSchemaService, DynamicSchemaServiceInternal
{
    private static final long MAX_ICON_SIZE = 100 * 1024;

    private static final String YAML_EXTENSION = "yaml";

    private static final String PROPERTIES_EXTENSION = ".properties";

    private final DynamicResourceManager dynamicResourceManager;

    private final DynamicResourceParser dynamicResourceParser;

    private final DynamicSchemaAuditLogSupport auditLogSupport;

    @Activate
    public DynamicSchemaServiceImpl( @Reference final NodeService nodeService,
                                     @Reference final DynamicSchemaAuditLogSupport auditLogSupport )
    {
        this.dynamicResourceManager = new DynamicResourceManager( nodeService );
        this.dynamicResourceParser = new DynamicResourceParser();
        this.auditLogSupport = auditLogSupport;
    }

    @Override
    public DynamicSchemaResult<PartDescriptor> createPart( final CreateDynamicComponentParams params )
    {
        return doCreateComponent( params, DynamicComponentType.PART );
    }

    @Override
    public DynamicSchemaResult<PartDescriptor> updatePart( final UpdateDynamicComponentParams params )
    {
        return doUpdateComponent( params, DynamicComponentType.PART );
    }

    @Override
    public DynamicSchemaResult<PartDescriptor> getPart( final DescriptorKey key )
    {
        return doGetComponent( key, DynamicComponentType.PART );
    }

    @Override
    public List<DynamicSchemaResult<PartDescriptor>> listParts( final ApplicationKey key )
    {
        return doListComponents( key, DynamicComponentType.PART );
    }

    @Override
    public boolean deletePart( final DescriptorKey key )
    {
        return doDeleteComponent( key, DynamicComponentType.PART );
    }

    @Override
    public Icon setPartIcon( final SetDynamicComponentIconParams params )
    {
        requireAdminRole();

        final Icon icon = doSetIcon( createComponentFolderPath( params.getKey(), DynamicComponentType.PART ), params.getKey().getName(),
                                     params.getData(), params.getMimeType(), params.getKey().toString() );

        auditLogSupport.setComponentIcon( params.getKey(), DynamicComponentType.PART, params.getMimeType(), icon.getSize() );

        return icon;
    }

    @Override
    public Icon getPartIcon( final DescriptorKey key )
    {
        requireAdminRole();

        return loadIcon( createComponentFolderPath( key, DynamicComponentType.PART ), key.getName() );
    }

    @Override
    public boolean deletePartIcon( final DescriptorKey key )
    {
        requireAdminRole();

        final boolean deleted = doDeleteIcon( createComponentFolderPath( key, DynamicComponentType.PART ), key.getName() );

        if ( deleted )
        {
            auditLogSupport.deleteComponentIcon( key, DynamicComponentType.PART );
        }

        return deleted;
    }

    @Override
    public DynamicSchemaResult<LayoutDescriptor> createLayout( final CreateDynamicComponentParams params )
    {
        return doCreateComponent( params, DynamicComponentType.LAYOUT );
    }

    @Override
    public DynamicSchemaResult<LayoutDescriptor> updateLayout( final UpdateDynamicComponentParams params )
    {
        return doUpdateComponent( params, DynamicComponentType.LAYOUT );
    }

    @Override
    public DynamicSchemaResult<LayoutDescriptor> getLayout( final DescriptorKey key )
    {
        return doGetComponent( key, DynamicComponentType.LAYOUT );
    }

    @Override
    public List<DynamicSchemaResult<LayoutDescriptor>> listLayouts( final ApplicationKey key )
    {
        return doListComponents( key, DynamicComponentType.LAYOUT );
    }

    @Override
    public boolean deleteLayout( final DescriptorKey key )
    {
        return doDeleteComponent( key, DynamicComponentType.LAYOUT );
    }

    @Override
    public DynamicSchemaResult<PageDescriptor> createPage( final CreateDynamicComponentParams params )
    {
        return doCreateComponent( params, DynamicComponentType.PAGE );
    }

    @Override
    public DynamicSchemaResult<PageDescriptor> updatePage( final UpdateDynamicComponentParams params )
    {
        return doUpdateComponent( params, DynamicComponentType.PAGE );
    }

    @Override
    public DynamicSchemaResult<PageDescriptor> getPage( final DescriptorKey key )
    {
        return doGetComponent( key, DynamicComponentType.PAGE );
    }

    @Override
    public List<DynamicSchemaResult<PageDescriptor>> listPages( final ApplicationKey key )
    {
        return doListComponents( key, DynamicComponentType.PAGE );
    }

    @Override
    public boolean deletePage( final DescriptorKey key )
    {
        return doDeleteComponent( key, DynamicComponentType.PAGE );
    }

    @Override
    public DynamicSchemaResult<ContentType> createContentType( final CreateDynamicContentSchemaParams params )
    {
        return doCreateSchema( params, DynamicContentSchemaType.CONTENT_TYPE );
    }

    @Override
    public DynamicSchemaResult<ContentType> updateContentType( final UpdateDynamicContentSchemaParams params )
    {
        return doUpdateSchema( params, DynamicContentSchemaType.CONTENT_TYPE );
    }

    @Override
    public DynamicSchemaResult<ContentType> getContentType( final ContentTypeName name )
    {
        return doGetSchema( name, DynamicContentSchemaType.CONTENT_TYPE );
    }

    @Override
    public List<DynamicSchemaResult<ContentType>> listContentTypes( final ApplicationKey key )
    {
        return doListSchemas( key, DynamicContentSchemaType.CONTENT_TYPE );
    }

    @Override
    public boolean deleteContentType( final ContentTypeName name )
    {
        return doDeleteSchema( name, DynamicContentSchemaType.CONTENT_TYPE );
    }

    @Override
    public Icon setContentTypeIcon( final SetDynamicContentSchemaIconParams params )
    {
        return doSetSchemaIcon( params, DynamicContentSchemaType.CONTENT_TYPE );
    }

    @Override
    public Icon getContentTypeIcon( final ContentTypeName name )
    {
        return doGetSchemaIcon( name, DynamicContentSchemaType.CONTENT_TYPE );
    }

    @Override
    public boolean deleteContentTypeIcon( final ContentTypeName name )
    {
        return doDeleteSchemaIcon( name, DynamicContentSchemaType.CONTENT_TYPE );
    }

    @Override
    public DynamicSchemaResult<FormFragmentDescriptor> createFormFragment( final CreateDynamicContentSchemaParams params )
    {
        return doCreateSchema( params, DynamicContentSchemaType.FORM_FRAGMENT );
    }

    @Override
    public DynamicSchemaResult<FormFragmentDescriptor> updateFormFragment( final UpdateDynamicContentSchemaParams params )
    {
        return doUpdateSchema( params, DynamicContentSchemaType.FORM_FRAGMENT );
    }

    @Override
    public DynamicSchemaResult<FormFragmentDescriptor> getFormFragment( final FormFragmentName name )
    {
        return doGetSchema( name, DynamicContentSchemaType.FORM_FRAGMENT );
    }

    @Override
    public List<DynamicSchemaResult<FormFragmentDescriptor>> listFormFragments( final ApplicationKey key )
    {
        return doListSchemas( key, DynamicContentSchemaType.FORM_FRAGMENT );
    }

    @Override
    public boolean deleteFormFragment( final FormFragmentName name )
    {
        return doDeleteSchema( name, DynamicContentSchemaType.FORM_FRAGMENT );
    }

    @Override
    public Icon setFormFragmentIcon( final SetDynamicContentSchemaIconParams params )
    {
        return doSetSchemaIcon( params, DynamicContentSchemaType.FORM_FRAGMENT );
    }

    @Override
    public Icon getFormFragmentIcon( final FormFragmentName name )
    {
        return doGetSchemaIcon( name, DynamicContentSchemaType.FORM_FRAGMENT );
    }

    @Override
    public boolean deleteFormFragmentIcon( final FormFragmentName name )
    {
        return doDeleteSchemaIcon( name, DynamicContentSchemaType.FORM_FRAGMENT );
    }

    @Override
    public DynamicSchemaResult<MixinDescriptor> createMixin( final CreateDynamicContentSchemaParams params )
    {
        return doCreateSchema( params, DynamicContentSchemaType.MIXIN );
    }

    @Override
    public DynamicSchemaResult<MixinDescriptor> updateMixin( final UpdateDynamicContentSchemaParams params )
    {
        return doUpdateSchema( params, DynamicContentSchemaType.MIXIN );
    }

    @Override
    public DynamicSchemaResult<MixinDescriptor> getMixin( final MixinName name )
    {
        return doGetSchema( name, DynamicContentSchemaType.MIXIN );
    }

    @Override
    public List<DynamicSchemaResult<MixinDescriptor>> listMixins( final ApplicationKey key )
    {
        return doListSchemas( key, DynamicContentSchemaType.MIXIN );
    }

    @Override
    public boolean deleteMixin( final MixinName name )
    {
        return doDeleteSchema( name, DynamicContentSchemaType.MIXIN );
    }

    @Override
    public Icon setMixinIcon( final SetDynamicContentSchemaIconParams params )
    {
        return doSetSchemaIcon( params, DynamicContentSchemaType.MIXIN );
    }

    @Override
    public Icon getMixinIcon( final MixinName name )
    {
        return doGetSchemaIcon( name, DynamicContentSchemaType.MIXIN );
    }

    @Override
    public boolean deleteMixinIcon( final MixinName name )
    {
        return doDeleteSchemaIcon( name, DynamicContentSchemaType.MIXIN );
    }

    private <T extends ComponentDescriptor> DynamicSchemaResult<T> doCreateComponent( final CreateDynamicComponentParams params,
                                                                                    final DynamicComponentType type )
    {
        requireAdminRole();

        final ComponentDescriptor descriptor = dynamicResourceParser.parseComponent( params.getKey(), type, params.getResource() );

        final NodePath resourceFolderPath = createComponentFolderPath( params.getKey(), type );
        final Resource resource =
            dynamicResourceManager.createResource( resourceFolderPath, params.getKey().getName(), params.getResource() );

        final DynamicSchemaResult<T> result =
            new DynamicSchemaResult<>( (T) wrapDescriptor( descriptor, resource.getTimestamp(), null ), resource );

        auditLogSupport.createComponent( params, type, result );

        return result;
    }

    private <T extends ComponentDescriptor> DynamicSchemaResult<T> doUpdateComponent( final UpdateDynamicComponentParams params,
                                                                                    final DynamicComponentType type )
    {
        requireAdminRole();

        final ComponentDescriptor descriptor = dynamicResourceParser.parseComponent( params.getKey(), type, params.getResource() );

        final NodePath resourceFolderPath = createComponentFolderPath( params.getKey(), type );
        final Resource resource =
            dynamicResourceManager.updateResource( resourceFolderPath, params.getKey().getName(), params.getResource() );

        final Icon icon = hasIcon( type ) ? loadIcon( resourceFolderPath, params.getKey().getName() ) : null;

        final DynamicSchemaResult<T> result =
            new DynamicSchemaResult<>( (T) wrapDescriptor( descriptor, resource.getTimestamp(), icon ), resource );

        auditLogSupport.updateComponent( params, type, result );

        return result;
    }

    private <T extends ComponentDescriptor> DynamicSchemaResult<T> doGetComponent( final DescriptorKey key, final DynamicComponentType type )
    {
        requireAdminRole();

        final NodePath resourceFolderPath = createComponentFolderPath( key, type );
        final Resource resource = dynamicResourceManager.getResource( resourceFolderPath, key.getName() );

        if ( resource.exists() && resource.getSize() > 0 )
        {
            final ComponentDescriptor descriptor = dynamicResourceParser.parseComponent( key, type, resource.readString() );
            final Icon icon = hasIcon( type ) ? loadIcon( resourceFolderPath, key.getName() ) : null;
            return new DynamicSchemaResult<>( (T) wrapDescriptor( descriptor, resource.getTimestamp(), icon ), resource );
        }
        return null;
    }

    private <T extends ComponentDescriptor> List<DynamicSchemaResult<T>> doListComponents( final ApplicationKey key,
                                                                                         final DynamicComponentType type )
    {
        requireAdminRole();

        return dynamicResourceManager.listResources( createComponentRootPath( key, type ) ).stream().map( resource -> {
            final DescriptorKey descriptorKey = DescriptorKey.from( key, getResourceName( resource.getKey() ) );
            final ComponentDescriptor descriptor = dynamicResourceParser.parseComponent( descriptorKey, type, resource.readString() );
            final Icon icon = hasIcon( type ) ? loadIcon( createComponentFolderPath( descriptorKey, type ), descriptorKey.getName() ) : null;

            return new DynamicSchemaResult<>( (T) wrapDescriptor( descriptor, resource.getTimestamp(), icon ), resource );
        } ).collect( Collectors.<DynamicSchemaResult<T>>toList() );
    }

    private boolean doDeleteComponent( final DescriptorKey key, final DynamicComponentType type )
    {
        requireAdminRole();

        final boolean deleted = deleteSchemaResources( createComponentFolderPath( key, type ), key.getName() );

        if ( deleted )
        {
            auditLogSupport.deleteComponent( key, type );
        }

        return deleted;
    }

    private <T extends BaseSchema<?>> DynamicSchemaResult<T> doCreateSchema( final CreateDynamicContentSchemaParams params,
                                                                           final DynamicContentSchemaType type )
    {
        requireAdminRole();
        requireSchemaName( params.getName(), type );

        final BaseSchema<?> schema = dynamicResourceParser.parseSchema( params.getName(), type, params.getResource() );

        final NodePath resourceFolderPath = createSchemaFolderPath( params.getName(), type );
        final Resource resource =
            dynamicResourceManager.createResource( resourceFolderPath, params.getName().getLocalName(), params.getResource() );

        final DynamicSchemaResult<T> result = new DynamicSchemaResult<>( (T) wrapSchema( schema, resource.getTimestamp(), null ), resource );

        auditLogSupport.createContentSchema( params, type, result );

        return result;
    }

    private <T extends BaseSchema<?>> DynamicSchemaResult<T> doUpdateSchema( final UpdateDynamicContentSchemaParams params,
                                                                           final DynamicContentSchemaType type )
    {
        requireAdminRole();
        requireSchemaName( params.getName(), type );

        final BaseSchema<?> schema = dynamicResourceParser.parseSchema( params.getName(), type, params.getResource() );

        final NodePath resourceFolderPath = createSchemaFolderPath( params.getName(), type );
        final Resource resource =
            dynamicResourceManager.updateResource( resourceFolderPath, params.getName().getLocalName(), params.getResource() );

        final Icon icon = loadIcon( resourceFolderPath, params.getName().getLocalName() );

        final DynamicSchemaResult<T> result = new DynamicSchemaResult<>( (T) wrapSchema( schema, resource.getTimestamp(), icon ), resource );

        auditLogSupport.updateContentSchema( params, type, result );

        return result;
    }

    private <T extends BaseSchema<?>> DynamicSchemaResult<T> doGetSchema( final BaseSchemaName name, final DynamicContentSchemaType type )
    {
        requireAdminRole();

        final NodePath resourceFolderPath = createSchemaFolderPath( name, type );
        final Resource resource = dynamicResourceManager.getResource( resourceFolderPath, name.getLocalName() );

        if ( resource.exists() && resource.getSize() > 0 )
        {
            final BaseSchema<?> schema = dynamicResourceParser.parseSchema( name, type, resource.readString() );
            final Icon icon = loadIcon( resourceFolderPath, name.getLocalName() );
            return new DynamicSchemaResult<>( (T) wrapSchema( schema, resource.getTimestamp(), icon ), resource );
        }

        return null;
    }

    private <T extends BaseSchema<?>> List<DynamicSchemaResult<T>> doListSchemas( final ApplicationKey key,
                                                                                final DynamicContentSchemaType type )
    {
        requireAdminRole();

        return dynamicResourceManager.listResources( createSchemaRootPath( key, type ) ).stream().map( resource -> {
            final BaseSchemaName schemaName = getSchemaName( key, type, getResourceName( resource.getKey() ) );
            final BaseSchema<?> schema = dynamicResourceParser.parseSchema( schemaName, type, resource.readString() );
            final Icon icon = loadIcon( createSchemaFolderPath( schemaName, type ), schemaName.getLocalName() );

            return new DynamicSchemaResult<T>( (T) wrapSchema( schema, resource.getTimestamp(), icon ), resource );
        } ).collect( Collectors.<DynamicSchemaResult<T>>toList() );
    }

    private boolean doDeleteSchema( final BaseSchemaName name, final DynamicContentSchemaType type )
    {
        requireAdminRole();

        final boolean deleted = deleteSchemaResources( createSchemaFolderPath( name, type ), name.getLocalName() );

        if ( deleted )
        {
            auditLogSupport.deleteContentSchema( name, type );
        }

        return deleted;
    }

    private Icon doSetSchemaIcon( final SetDynamicContentSchemaIconParams params, final DynamicContentSchemaType type )
    {
        requireAdminRole();
        requireSchemaName( params.getName(), type );

        final Icon icon = doSetIcon( createSchemaFolderPath( params.getName(), type ), params.getName().getLocalName(), params.getData(),
                                     params.getMimeType(), params.getName().toString() );

        auditLogSupport.setContentSchemaIcon( params.getName(), type, params.getMimeType(), icon.getSize() );

        return icon;
    }

    private Icon doGetSchemaIcon( final BaseSchemaName name, final DynamicContentSchemaType type )
    {
        requireAdminRole();

        return loadIcon( createSchemaFolderPath( name, type ), name.getLocalName() );
    }

    private boolean doDeleteSchemaIcon( final BaseSchemaName name, final DynamicContentSchemaType type )
    {
        requireAdminRole();

        final boolean deleted = doDeleteIcon( createSchemaFolderPath( name, type ), name.getLocalName() );

        if ( deleted )
        {
            auditLogSupport.deleteContentSchemaIcon( name, type );
        }

        return deleted;
    }

    @Override
    public DynamicSchemaResult<CmsDescriptor> createCms( final CreateDynamicCmsParams params )
    {
        requireAdminRole();

        final CmsDescriptor site = dynamicResourceParser.parseCms( params.getKey(), params.getResource() );

        final NodePath resourceFolderPath = createCmsFolderPath( params.getKey() );
        final Resource createdResource =
            dynamicResourceManager.createResource( resourceFolderPath, SchemaResourceNames.CMS_ROOT_NAME, params.getResource() );

        final DynamicSchemaResult<CmsDescriptor> result = new DynamicSchemaResult<>(
            CmsDescriptor.copyOf( site ).modifiedTime( Instant.ofEpochMilli( createdResource.getTimestamp() ) ).build(), createdResource );

        auditLogSupport.createCms( params, result );

        return result;
    }

    @Override
    public DynamicSchemaResult<CmsDescriptor> updateCms( final UpdateDynamicCmsParams params )
    {
        requireAdminRole();

        final CmsDescriptor cmsDescriptor = dynamicResourceParser.parseCms( params.getKey(), params.getResource() );

        final NodePath resourceFolderPath = createCmsFolderPath( params.getKey() );

        final Resource resource = dynamicResourceManager.resourceNodeExists( resourceFolderPath, SchemaResourceNames.CMS_ROOT_NAME )
            ? dynamicResourceManager.updateResource( resourceFolderPath, SchemaResourceNames.CMS_ROOT_NAME, params.getResource() )
            : dynamicResourceManager.createResource( resourceFolderPath, SchemaResourceNames.CMS_ROOT_NAME, params.getResource() );

        final DynamicSchemaResult<CmsDescriptor> result = new DynamicSchemaResult<>(
            CmsDescriptor.copyOf( cmsDescriptor ).modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) ).build(), resource );

        auditLogSupport.updateCms( params, result );

        return result;
    }

    @Override
    public DynamicSchemaResult<StyleDescriptor> createStyles( final CreateDynamicStylesParams params )
    {
        requireAdminRole();

        final StyleDescriptor styles = dynamicResourceParser.parseStyles( params.getKey(), params.getResource() );

        final NodePath resourceFolderPath = createStylesFolderPath( params.getKey() );
        final Resource resource =
            dynamicResourceManager.createResource( resourceFolderPath, SchemaResourceNames.STYLE_NAME, params.getResource() );

        final DynamicSchemaResult<StyleDescriptor> result = new DynamicSchemaResult<>(
            StyleDescriptor.copyOf( styles ).modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) ).build(), resource );

        auditLogSupport.createStyles( params, result );

        return result;
    }

    @Override
    public DynamicSchemaResult<StyleDescriptor> updateStyles( final UpdateDynamicStylesParams params )
    {
        requireAdminRole();

        final StyleDescriptor styles = dynamicResourceParser.parseStyles( params.getKey(), params.getResource() );

        final NodePath resourceFolderPath = createStylesFolderPath( params.getKey() );
        final Resource resource =
            dynamicResourceManager.updateResource( resourceFolderPath, SchemaResourceNames.STYLE_NAME, params.getResource() );

        final DynamicSchemaResult<StyleDescriptor> result = new DynamicSchemaResult<>(
            StyleDescriptor.copyOf( styles ).modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) ).build(), resource );

        auditLogSupport.updateStyles( params, result );

        return result;
    }

    @Override
    public DynamicSchemaResult<CmsDescriptor> getCmsDescriptor( final ApplicationKey key )
    {
        requireAdminRole();

        final NodePath resourceFolderPath = createCmsFolderPath( key );

        final Resource resource = dynamicResourceManager.getResource( resourceFolderPath, SchemaResourceNames.CMS_ROOT_NAME );

        if ( resource.exists() && resource.getSize() > 0 )
        {
            final CmsDescriptor siteDescriptor = dynamicResourceParser.parseCms( key, resource.readString() );
            return new DynamicSchemaResult<>(
                CmsDescriptor.copyOf( siteDescriptor ).modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) ).build(), resource );
        }
        return null;
    }

    @Override
    public DynamicSchemaResult<StyleDescriptor> getStyles( final ApplicationKey key )
    {
        requireAdminRole();

        final NodePath resourceFolderPath = createStylesFolderPath( key );
        final Resource resource = dynamicResourceManager.getResource( resourceFolderPath, SchemaResourceNames.STYLE_NAME );

        if ( resource.exists() && resource.getSize() > 0 )
        {
            final StyleDescriptor descriptor = dynamicResourceParser.parseStyles( key, resource.readString() );
            return new DynamicSchemaResult<>(
                StyleDescriptor.copyOf( descriptor ).modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) ).build(), resource );
        }
        return null;
    }

    @Override
    public boolean deleteCms( final ApplicationKey key )
    {
        requireAdminRole();

        final NodePath resourceFolderPath = createCmsFolderPath( key );
        final boolean deleted = dynamicResourceManager.deleteResource( resourceFolderPath, SchemaResourceNames.CMS_ROOT_NAME );

        if ( deleted )
        {
            auditLogSupport.deleteCms( key );
        }

        return deleted;
    }

    @Override
    public boolean deleteStyles( final ApplicationKey key )
    {
        requireAdminRole();

        final NodePath resourceFolderPath = createStylesFolderPath( key );
        final boolean deleted = dynamicResourceManager.deleteResource( resourceFolderPath, SchemaResourceNames.STYLE_NAME );

        if ( deleted )
        {
            auditLogSupport.deleteStyles( key );
        }

        return deleted;
    }

    @Override
    public DynamicSchemaResult<MacroDescriptor> createMacro( final CreateDynamicMacroParams params )
    {
        requireAdminRole();

        final MacroDescriptor descriptor = dynamicResourceParser.parseMacro( params.getKey(), params.getResource() );

        final NodePath resourceFolderPath = createMacroFolderPath( params.getKey() );
        final Resource resource =
            dynamicResourceManager.createResource( resourceFolderPath, params.getKey().getName(), params.getResource() );

        final DynamicSchemaResult<MacroDescriptor> result =
            new DynamicSchemaResult<>( wrapMacro( descriptor, resource.getTimestamp(), null ), resource );

        auditLogSupport.createMacro( params, result );

        return result;
    }

    @Override
    public DynamicSchemaResult<MacroDescriptor> updateMacro( final UpdateDynamicMacroParams params )
    {
        requireAdminRole();

        final MacroDescriptor descriptor = dynamicResourceParser.parseMacro( params.getKey(), params.getResource() );

        final NodePath resourceFolderPath = createMacroFolderPath( params.getKey() );
        final Resource resource =
            dynamicResourceManager.updateResource( resourceFolderPath, params.getKey().getName(), params.getResource() );

        final Icon icon = loadIcon( resourceFolderPath, params.getKey().getName() );

        final DynamicSchemaResult<MacroDescriptor> result =
            new DynamicSchemaResult<>( wrapMacro( descriptor, resource.getTimestamp(), icon ), resource );

        auditLogSupport.updateMacro( params, result );

        return result;
    }

    @Override
    public DynamicSchemaResult<MacroDescriptor> getMacro( final MacroKey key )
    {
        requireAdminRole();

        final NodePath resourceFolderPath = createMacroFolderPath( key );
        final Resource resource = dynamicResourceManager.getResource( resourceFolderPath, key.getName() );

        if ( resource.exists() && resource.getSize() > 0 )
        {
            final MacroDescriptor descriptor = dynamicResourceParser.parseMacro( key, resource.readString() );
            final Icon icon = loadIcon( resourceFolderPath, key.getName() );
            return new DynamicSchemaResult<>( wrapMacro( descriptor, resource.getTimestamp(), icon ), resource );
        }
        return null;
    }

    @Override
    public List<DynamicSchemaResult<MacroDescriptor>> listMacros( final ApplicationKey key )
    {
        requireAdminRole();

        return dynamicResourceManager.listResources( createMacroRootPath( key ) ).stream().map( resource -> {
            final MacroKey macroKey = MacroKey.from( key, getResourceName( resource.getKey() ) );
            final MacroDescriptor descriptor = dynamicResourceParser.parseMacro( macroKey, resource.readString() );
            final Icon icon = loadIcon( createMacroFolderPath( macroKey ), macroKey.getName() );

            return new DynamicSchemaResult<>( wrapMacro( descriptor, resource.getTimestamp(), icon ), resource );
        } ).collect( Collectors.toList() );
    }

    @Override
    public boolean deleteMacro( final MacroKey key )
    {
        requireAdminRole();

        final boolean deleted = deleteSchemaResources( createMacroFolderPath( key ), key.getName() );

        if ( deleted )
        {
            auditLogSupport.deleteMacro( key );
        }

        return deleted;
    }

    @Override
    public Icon setMacroIcon( final SetDynamicMacroIconParams params )
    {
        requireAdminRole();

        final Icon icon = doSetIcon( createMacroFolderPath( params.getKey() ), params.getKey().getName(), params.getData(),
                                     params.getMimeType(), params.getKey().toString() );

        auditLogSupport.setMacroIcon( params.getKey(), params.getMimeType(), icon.getSize() );

        return icon;
    }

    @Override
    public Icon getMacroIcon( final MacroKey key )
    {
        requireAdminRole();

        return loadIcon( createMacroFolderPath( key ), key.getName() );
    }

    @Override
    public boolean deleteMacroIcon( final MacroKey key )
    {
        requireAdminRole();

        final boolean deleted = doDeleteIcon( createMacroFolderPath( key ), key.getName() );

        if ( deleted )
        {
            auditLogSupport.deleteMacroIcon( key );
        }

        return deleted;
    }

    @Override
    public Resource createPhrases( final CreateDynamicPhrasesParams params )
    {
        requireAdminRole();

        final Resource resource =
            dynamicResourceManager.createResourceFile( createPhrasesFolderPath( params.getKey() ), phrasesFileName( params.getName() ),
                                                       params.getResource() );

        auditLogSupport.createPhrases( params, resource );

        return resource;
    }

    @Override
    public Resource updatePhrases( final UpdateDynamicPhrasesParams params )
    {
        requireAdminRole();

        final Resource resource =
            dynamicResourceManager.updateResourceFile( createPhrasesFolderPath( params.getKey() ), phrasesFileName( params.getName() ),
                                                       params.getResource() );

        auditLogSupport.updatePhrases( params, resource );

        return resource;
    }

    @Override
    public Resource getPhrases( final GetDynamicPhrasesParams params )
    {
        requireAdminRole();

        final Resource resource =
            dynamicResourceManager.getResourceFile( createPhrasesFolderPath( params.getKey() ), phrasesFileName( params.getName() ) );

        return resource.exists() ? resource : null;
    }

    @Override
    public List<Resource> listPhrases( final ApplicationKey key )
    {
        requireAdminRole();

        return dynamicResourceManager.listResourceFiles( createPhrasesFolderPath( key ), "[^/]+\\" + PROPERTIES_EXTENSION );
    }

    @Override
    public boolean deletePhrases( final DeleteDynamicPhrasesParams params )
    {
        requireAdminRole();

        final NodePath folderPath = createPhrasesFolderPath( params.getKey() );
        final String fileName = phrasesFileName( params.getName() );

        final boolean deleted = dynamicResourceManager.resourceFileNodeExists( folderPath, fileName ) &&
            dynamicResourceManager.deleteResourceFile( folderPath, fileName );

        if ( deleted )
        {
            auditLogSupport.deletePhrases( params );
        }

        return deleted;
    }

    /**
     * The icon is stored next to the descriptor as {@code <name>.svg} or {@code <name>.png}. An icon of the other format is removed,
     * so a schema never has two icons. The descriptor node is touched so that the descriptor is re-read with the new icon.
     */
    private Icon doSetIcon( final NodePath folderPath, final String localName, final ByteSource data, final String mimeType,
                            final String schemaId )
    {
        final String extension = iconExtension( mimeType );
        final long size = iconSize( data );

        if ( size == 0 )
        {
            throw new IllegalArgumentException( "icon data is required" );
        }
        if ( size > MAX_ICON_SIZE )
        {
            throw new IllegalArgumentException( String.format( "icon size exceeds the maximum allowed %d bytes", MAX_ICON_SIZE ) );
        }

        final String descriptorFileName = localName + "." + YAML_EXTENSION;
        if ( !dynamicResourceManager.resourceFileNodeExists( folderPath, descriptorFileName ) )
        {
            throw new SchemaNotFoundException( String.format( "Schema [%s] not found", schemaId ) );
        }

        final String oppositeFileName = localName + "." +
            ( SchemaResourcePaths.SVG_EXTENSION.equals( extension ) ? SchemaResourcePaths.PNG_EXTENSION : SchemaResourcePaths.SVG_EXTENSION );
        if ( dynamicResourceManager.resourceFileNodeExists( folderPath, oppositeFileName ) )
        {
            dynamicResourceManager.deleteResourceFile( folderPath, oppositeFileName );
        }

        final Resource resource = dynamicResourceManager.putBinaryResourceFile( folderPath, localName + "." + extension, data, mimeType );

        dynamicResourceManager.touchResourceFile( folderPath, descriptorFileName );

        return Icon.from( resource.readBytes(), mimeType, Instant.ofEpochMilli( resource.getTimestamp() ) );
    }

    // the descriptor and its icon
    private boolean deleteSchemaResources( final NodePath folderPath, final String localName )
    {
        final String descriptorFileName = localName + "." + YAML_EXTENSION;
        if ( !dynamicResourceManager.resourceFileNodeExists( folderPath, descriptorFileName ) )
        {
            return false;
        }

        for ( final String extension : List.of( SchemaResourcePaths.SVG_EXTENSION, SchemaResourcePaths.PNG_EXTENSION ) )
        {
            final String fileName = localName + "." + extension;
            if ( dynamicResourceManager.resourceFileNodeExists( folderPath, fileName ) )
            {
                dynamicResourceManager.deleteResourceFile( folderPath, fileName );
            }
        }

        return dynamicResourceManager.deleteResourceFile( folderPath, descriptorFileName );
    }

    private boolean doDeleteIcon( final NodePath folderPath, final String localName )
    {
        boolean deleted = false;
        for ( final String extension : List.of( SchemaResourcePaths.SVG_EXTENSION, SchemaResourcePaths.PNG_EXTENSION ) )
        {
            final String fileName = localName + "." + extension;
            if ( dynamicResourceManager.resourceFileNodeExists( folderPath, fileName ) )
            {
                deleted |= dynamicResourceManager.deleteResourceFile( folderPath, fileName );
            }
        }

        final String descriptorFileName = localName + "." + YAML_EXTENSION;
        if ( deleted && dynamicResourceManager.resourceFileNodeExists( folderPath, descriptorFileName ) )
        {
            dynamicResourceManager.touchResourceFile( folderPath, descriptorFileName );
        }

        return deleted;
    }

    private Icon loadIcon( final NodePath folderPath, final String localName )
    {
        final Icon svgIcon = loadIcon( dynamicResourceManager.getResourceFile( folderPath, localName + "." + SchemaResourcePaths.SVG_EXTENSION ),
                                       SchemaResourcePaths.SVG_MIME_TYPE );
        if ( svgIcon != null )
        {
            return svgIcon;
        }

        return loadIcon( dynamicResourceManager.getResourceFile( folderPath, localName + "." + SchemaResourcePaths.PNG_EXTENSION ),
                         SchemaResourcePaths.PNG_MIME_TYPE );
    }

    private static Icon loadIcon( final Resource resource, final String mimeType )
    {
        if ( !resource.exists() )
        {
            return null;
        }
        return Icon.from( resource.readBytes(), mimeType, Instant.ofEpochMilli( resource.getTimestamp() ) );
    }

    private static String iconExtension( final String mimeType )
    {
        switch ( mimeType )
        {
            case SchemaResourcePaths.SVG_MIME_TYPE:
                return SchemaResourcePaths.SVG_EXTENSION;
            case SchemaResourcePaths.PNG_MIME_TYPE:
                return SchemaResourcePaths.PNG_EXTENSION;
            default:
                throw new IllegalArgumentException( String.format( "unsupported icon mime type: %s", mimeType ) );
        }
    }

    private static long iconSize( final ByteSource data )
    {
        try
        {
            return data.size();
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    // only parts have icons among the components
    private static boolean hasIcon( final DynamicComponentType type )
    {
        return type == DynamicComponentType.PART;
    }

    // the params carry a BaseSchemaName, so a name of another kind than the method implies is rejected up front
    private static void requireSchemaName( final BaseSchemaName name, final DynamicContentSchemaType type )
    {
        final Class<? extends BaseSchemaName> expected = switch ( type )
        {
            case CONTENT_TYPE -> ContentTypeName.class;
            case FORM_FRAGMENT -> FormFragmentName.class;
            case MIXIN -> MixinName.class;
        };
        if ( !expected.isInstance( name ) )
        {
            throw new IllegalArgumentException(
                String.format( "expected %s but got %s: %s", expected.getSimpleName(), name.getClass().getSimpleName(), name ) );
        }
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

    private static String phrasesFileName( final String name )
    {
        return name.endsWith( PROPERTIES_EXTENSION ) ? name : name + PROPERTIES_EXTENSION;
    }

    // schemas are stored flat: the descriptor <root>/<name>.yaml and its icon <root>/<name>.svg|png live in the root folder of their kind
    private NodePath createComponentFolderPath( final DescriptorKey key, final DynamicComponentType dynamicType )
    {
        return createComponentRootPath( key.getApplicationKey(), dynamicType );
    }

    private NodePath createComponentRootPath( final ApplicationKey key, final DynamicComponentType dynamicType )
    {
        return new NodePath( createCmsFolderPath( key ), NodeName.from( getComponentRootName( dynamicType ) ) );
    }

    private NodePath createSchemaFolderPath( final BaseSchemaName key, final DynamicContentSchemaType dynamicType )
    {
        return createSchemaRootPath( key.getApplicationKey(), dynamicType );
    }

    private NodePath createSchemaRootPath( final ApplicationKey key, final DynamicContentSchemaType dynamicType )
    {
        return new NodePath( createCmsFolderPath( key ), NodeName.from( getSchemaRootName( dynamicType ) ) );
    }

    private NodePath createMacroFolderPath( final MacroKey key )
    {
        return createMacroRootPath( key.getApplicationKey() );
    }

    private NodePath createMacroRootPath( final ApplicationKey key )
    {
        return new NodePath( createCmsFolderPath( key ), NodeName.from( SchemaResourceNames.MACROS_ROOT_NAME ) );
    }

    private NodePath createPhrasesFolderPath( final ApplicationKey key )
    {
        return NodePath.create( createCmsFolderPath( key ) )
            .addElement( SchemaResourceNames.I18N_ROOT_NAME )
            .addElement( SchemaResourceNames.PHRASES_ROOT_NAME )
            .build();
    }

    // the style descriptor keeps its folder: cms/style/style.yaml
    private NodePath createStylesFolderPath( final ApplicationKey key )
    {
        return new NodePath( createCmsFolderPath( key ), NodeName.from( SchemaResourceNames.STYLE_ROOT_NAME ) );
    }

    // the persisted schema of an application lives below its node in system-repo
    private NodePath createCmsFolderPath( final ApplicationKey key )
    {
        return new NodePath( ApplicationRepoServiceImpl.applicationNodePath( key ), NodeName.from( SchemaResourceNames.CMS_ROOT_NAME ) );
    }

    private String getSchemaRootName( final DynamicContentSchemaType type )
    {
        switch ( type )
        {
            case CONTENT_TYPE:
                return SchemaResourceNames.CONTENT_TYPE_ROOT_NAME;
            case FORM_FRAGMENT:
                return SchemaResourceNames.FORM_FRAGMENTS_ROOT_NAME;
            case MIXIN:
                return SchemaResourceNames.MIXINS_ROOT_NAME;
            default:
                throw new IllegalArgumentException( "invalid dynamic schema type: " + type );
        }
    }

    private String getComponentRootName( final DynamicComponentType type )
    {
        switch ( type )
        {
            case PAGE:
                return SchemaResourceNames.PAGE_ROOT_NAME;
            case PART:
                return SchemaResourceNames.PART_ROOT_NAME;
            case LAYOUT:
                return SchemaResourceNames.LAYOUT_ROOT_NAME;
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

    private ComponentDescriptor wrapDescriptor( final ComponentDescriptor componentDescriptor, final long modifiedTime, final Icon icon )
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
                .icon( icon )
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

    private BaseSchema<?> wrapSchema( final BaseSchema<?> baseSchema, final long modifiedTime, final Icon icon )
    {
        if ( baseSchema instanceof ContentType )
        {
            return ContentType.create( (ContentType) baseSchema ).modifiedTime( Instant.ofEpochMilli( modifiedTime ) ).icon( icon ).build();
        }
        if ( baseSchema instanceof FormFragmentDescriptor )
        {
            return FormFragmentDescriptor.create( (FormFragmentDescriptor) baseSchema )
                .modifiedTime( Instant.ofEpochMilli( modifiedTime ) )
                .icon( icon )
                .build();
        }
        if ( baseSchema instanceof MixinDescriptor )
        {
            return MixinDescriptor.create( (MixinDescriptor) baseSchema )
                .modifiedTime( Instant.ofEpochMilli( modifiedTime ) )
                .icon( icon )
                .build();
        }

        throw new IllegalArgumentException( "unknown type of BaseSchema: " + baseSchema.getName() );
    }

    private static MacroDescriptor wrapMacro( final MacroDescriptor descriptor, final long modifiedTime, final Icon icon )
    {
        return MacroDescriptor.copyOf( descriptor ).modifiedTime( Instant.ofEpochMilli( modifiedTime ) ).icon( icon ).build();
    }
}
