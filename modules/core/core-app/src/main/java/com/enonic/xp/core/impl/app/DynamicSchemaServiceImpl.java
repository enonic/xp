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
import com.enonic.xp.resource.DeleteDynamicComponentParams;
import com.enonic.xp.resource.DeleteDynamicContentSchemaParams;
import com.enonic.xp.resource.DeleteDynamicPhrasesParams;
import com.enonic.xp.resource.DynamicComponentType;
import com.enonic.xp.resource.DynamicContentSchemaType;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.resource.GetDynamicComponentParams;
import com.enonic.xp.resource.GetDynamicContentSchemaParams;
import com.enonic.xp.resource.GetDynamicPhrasesParams;
import com.enonic.xp.resource.ListDynamicComponentsParams;
import com.enonic.xp.resource.ListDynamicContentSchemasParams;
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

    @Activate
    public DynamicSchemaServiceImpl( @Reference final NodeService nodeService )
    {
        this.dynamicResourceManager = new DynamicResourceManager( nodeService );
        this.dynamicResourceParser = new DynamicResourceParser();
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

        return new DynamicSchemaResult<>( (T) wrapDescriptor( descriptor, resource.getTimestamp(), null ), resource );

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

        final Icon icon = hasIcon( params.getType() ) ? loadIcon( resourceFolderPath, params.getKey().getName() ) : null;

        return new DynamicSchemaResult<>( (T) wrapDescriptor( descriptor, resource.getTimestamp(), icon ), resource );
    }

    @Override
    public Icon setComponentIcon( final SetDynamicComponentIconParams params )
    {
        requireAdminRole();

        if ( !hasIcon( params.getType() ) )
        {
            throw new IllegalArgumentException( String.format( "icons are not supported for component type: %s", params.getType() ) );
        }

        return doSetIcon( createComponentFolderPath( params.getKey(), params.getType() ), params.getKey().getName(), params.getData(),
                          params.getMimeType(), params.getKey().toString() );
    }

    @Override
    public Icon getComponentIcon( final GetDynamicComponentParams params )
    {
        requireAdminRole();

        return hasIcon( params.getType() )
            ? loadIcon( createComponentFolderPath( params.getKey(), params.getType() ), params.getKey().getName() )
            : null;
    }

    @Override
    public boolean deleteComponentIcon( final DeleteDynamicComponentParams params )
    {
        requireAdminRole();

        return hasIcon( params.getType() ) &&
            doDeleteIcon( createComponentFolderPath( params.getKey(), params.getType() ), params.getKey().getName() );
    }

    @Override
    public <T extends BaseSchema<?>> DynamicSchemaResult<T> createContentSchema( final CreateDynamicContentSchemaParams params )
    {
        requireAdminRole();

        final BaseSchema<?> schema = dynamicResourceParser.parseSchema( params.getName(), params.getType(), params.getResource() );

        final NodePath resourceFolderPath = createSchemaFolderPath( params.getName(), params.getType() );
        final Resource resource =
            dynamicResourceManager.createResource( resourceFolderPath, params.getName().getLocalName(), params.getResource() );

        return new DynamicSchemaResult<>( (T) wrapSchema( schema, resource.getTimestamp(), null ), resource );
    }

    @Override
    public <T extends BaseSchema<?>> DynamicSchemaResult<T> updateContentSchema( final UpdateDynamicContentSchemaParams params )
    {
        requireAdminRole();

        final BaseSchema<?> schema = dynamicResourceParser.parseSchema( params.getName(), params.getType(), params.getResource() );

        final NodePath resourceFolderPath = createSchemaFolderPath( params.getName(), params.getType() );
        final Resource resource =
            dynamicResourceManager.updateResource( resourceFolderPath, params.getName().getLocalName(), params.getResource() );

        final Icon icon = loadIcon( resourceFolderPath, params.getName().getLocalName() );

        return new DynamicSchemaResult<>( (T) wrapSchema( schema, resource.getTimestamp(), icon ), resource );
    }

    @Override
    public Icon setContentSchemaIcon( final SetDynamicContentSchemaIconParams params )
    {
        requireAdminRole();

        return doSetIcon( createSchemaFolderPath( params.getName(), params.getType() ), params.getName().getLocalName(), params.getData(),
                          params.getMimeType(), params.getName().toString() );
    }

    @Override
    public Icon getContentSchemaIcon( final GetDynamicContentSchemaParams params )
    {
        requireAdminRole();

        return loadIcon( createSchemaFolderPath( params.getName(), params.getType() ), params.getName().getLocalName() );
    }

    @Override
    public boolean deleteContentSchemaIcon( final DeleteDynamicContentSchemaParams params )
    {
        requireAdminRole();

        return doDeleteIcon( createSchemaFolderPath( params.getName(), params.getType() ), params.getName().getLocalName() );
    }

    @Override
    public DynamicSchemaResult<CmsDescriptor> createCms( final CreateDynamicCmsParams params )
    {
        requireAdminRole();

        final CmsDescriptor site = dynamicResourceParser.parseCms( params.getKey(), params.getResource() );

        final NodePath resourceFolderPath = createCmsFolderPath( params.getKey() );
        final Resource createdResource =
            dynamicResourceManager.createResource( resourceFolderPath, SchemaResourceNames.CMS_ROOT_NAME, params.getResource() );

        return new DynamicSchemaResult<>(
            CmsDescriptor.copyOf( site ).modifiedTime( Instant.ofEpochMilli( createdResource.getTimestamp() ) ).build(), createdResource );
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

        return new DynamicSchemaResult<>(
            CmsDescriptor.copyOf( cmsDescriptor ).modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) ).build(), resource );
    }

    @Override
    public DynamicSchemaResult<StyleDescriptor> createStyles( final CreateDynamicStylesParams params )
    {
        requireAdminRole();

        final StyleDescriptor styles = dynamicResourceParser.parseStyles( params.getKey(), params.getResource() );

        final NodePath resourceFolderPath = createStylesFolderPath( params.getKey() );
        final Resource resource =
            dynamicResourceManager.createResource( resourceFolderPath, SchemaResourceNames.STYLE_NAME, params.getResource() );

        return new DynamicSchemaResult<>(
            StyleDescriptor.copyOf( styles ).modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) ).build(), resource );
    }

    @Override
    public DynamicSchemaResult<StyleDescriptor> updateStyles( final UpdateDynamicStylesParams params )
    {
        requireAdminRole();

        final StyleDescriptor styles = dynamicResourceParser.parseStyles( params.getKey(), params.getResource() );

        final NodePath resourceFolderPath = createStylesFolderPath( params.getKey() );
        final Resource resource =
            dynamicResourceManager.updateResource( resourceFolderPath, SchemaResourceNames.STYLE_NAME, params.getResource() );

        return new DynamicSchemaResult<>(
            StyleDescriptor.copyOf( styles ).modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) ).build(), resource );
    }

    @Override
    public <T extends ComponentDescriptor> DynamicSchemaResult<T> getComponent( final GetDynamicComponentParams params )
    {
        requireAdminRole();

        final NodePath resourceFolderPath = createComponentFolderPath( params.getKey(), params.getType() );
        final Resource resource = dynamicResourceManager.getResource( resourceFolderPath, params.getKey().getName() );

        if ( resource.exists() && resource.getSize() > 0 )
        {
            final ComponentDescriptor descriptor =
                dynamicResourceParser.parseComponent( params.getKey(), params.getType(), resource.readString() );
            final Icon icon = hasIcon( params.getType() ) ? loadIcon( resourceFolderPath, params.getKey().getName() ) : null;
            return new DynamicSchemaResult<>( (T) wrapDescriptor( descriptor, resource.getTimestamp(), icon ), resource );
        }
        return null;
    }

    @Override
    public <T extends ComponentDescriptor> List<DynamicSchemaResult<T>> listComponents( final ListDynamicComponentsParams params )
    {
        requireAdminRole();

        return dynamicResourceManager.listResources( createComponentRootPath( params.getKey(), params.getType() ) )
            .stream()
            .map( resource -> {
                final DescriptorKey descriptorKey = DescriptorKey.from( params.getKey(), getResourceName( resource.getKey() ) );
                final ComponentDescriptor descriptor =
                    dynamicResourceParser.parseComponent( descriptorKey, params.getType(), resource.readString() );
                final Icon icon = hasIcon( params.getType() )
                    ? loadIcon( createComponentFolderPath( descriptorKey, params.getType() ), descriptorKey.getName() )
                    : null;

                return new DynamicSchemaResult<>( (T) wrapDescriptor( descriptor, resource.getTimestamp(), icon ), resource );
            } )
            .collect( Collectors.<DynamicSchemaResult<T>>toList() );
    }


    @Override
    public <T extends BaseSchema<?>> DynamicSchemaResult<T> getContentSchema( final GetDynamicContentSchemaParams params )
    {
        requireAdminRole();

        final NodePath resourceFolderPath = createSchemaFolderPath( params.getName(), params.getType() );
        final Resource resource = dynamicResourceManager.getResource( resourceFolderPath, params.getName().getLocalName() );

        if ( resource.exists() && resource.getSize() > 0 )
        {
            final BaseSchema<?> schema = dynamicResourceParser.parseSchema( params.getName(), params.getType(), resource.readString() );
            final Icon icon = loadIcon( resourceFolderPath, params.getName().getLocalName() );
            return new DynamicSchemaResult<>( (T) wrapSchema( schema, resource.getTimestamp(), icon ), resource );
        }

        return null;
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
        requireAdminRole();

        final NodePath componentRootPath = createSchemaRootPath( params.getKey(), params.getType() );

        return dynamicResourceManager.listResources( componentRootPath ).stream().map( resource -> {

            final BaseSchemaName schemaName = getSchemaName( params.getKey(), params.getType(), getResourceName( resource.getKey() ) );
            final BaseSchema<?> schema = dynamicResourceParser.parseSchema( schemaName, params.getType(), resource.readString() );
            final Icon icon = loadIcon( createSchemaFolderPath( schemaName, params.getType() ), schemaName.getLocalName() );

            return new DynamicSchemaResult<T>( (T) wrapSchema( schema, resource.getTimestamp(), icon ), resource );
        } ).collect( Collectors.<DynamicSchemaResult<T>>toList() );
    }

    @Override
    public boolean deleteCms( final ApplicationKey key )
    {
        requireAdminRole();

        final NodePath resourceFolderPath = createCmsFolderPath( key );
        return dynamicResourceManager.deleteResource( resourceFolderPath, SchemaResourceNames.CMS_ROOT_NAME, false );
    }

    @Override
    public boolean deleteStyles( final ApplicationKey key )
    {
        requireAdminRole();

        final NodePath resourceFolderPath = createStylesFolderPath( key );
        return dynamicResourceManager.deleteResource( resourceFolderPath, SchemaResourceNames.STYLE_NAME, false );
    }

    @Override
    public DynamicSchemaResult<MacroDescriptor> createMacro( final CreateDynamicMacroParams params )
    {
        requireAdminRole();

        final MacroDescriptor descriptor = dynamicResourceParser.parseMacro( params.getKey(), params.getResource() );

        final NodePath resourceFolderPath = createMacroFolderPath( params.getKey() );
        final Resource resource =
            dynamicResourceManager.createResource( resourceFolderPath, params.getKey().getName(), params.getResource() );

        return new DynamicSchemaResult<>( wrapMacro( descriptor, resource.getTimestamp(), null ), resource );
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

        return new DynamicSchemaResult<>( wrapMacro( descriptor, resource.getTimestamp(), icon ), resource );
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

        return dynamicResourceManager.deleteResource( createMacroFolderPath( key ), key.getName(), true );
    }

    @Override
    public Icon setMacroIcon( final SetDynamicMacroIconParams params )
    {
        requireAdminRole();

        return doSetIcon( createMacroFolderPath( params.getKey() ), params.getKey().getName(), params.getData(), params.getMimeType(),
                          params.getKey().toString() );
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

        return doDeleteIcon( createMacroFolderPath( key ), key.getName() );
    }

    @Override
    public Resource createPhrases( final CreateDynamicPhrasesParams params )
    {
        requireAdminRole();

        return dynamicResourceManager.createResourceFile( createPhrasesFolderPath( params.getKey() ), phrasesFileName( params.getName() ),
                                                          params.getResource() );
    }

    @Override
    public Resource updatePhrases( final UpdateDynamicPhrasesParams params )
    {
        requireAdminRole();

        return dynamicResourceManager.updateResourceFile( createPhrasesFolderPath( params.getKey() ), phrasesFileName( params.getName() ),
                                                          params.getResource() );
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

        return dynamicResourceManager.resourceFileNodeExists( folderPath, fileName ) &&
            dynamicResourceManager.deleteResourceFile( folderPath, fileName, false );
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
            dynamicResourceManager.deleteResourceFile( folderPath, oppositeFileName, false );
        }

        final Resource resource = dynamicResourceManager.putBinaryResourceFile( folderPath, localName + "." + extension, data, mimeType );

        dynamicResourceManager.touchResourceFile( folderPath, descriptorFileName );

        return Icon.from( resource.readBytes(), mimeType, Instant.ofEpochMilli( resource.getTimestamp() ) );
    }

    private boolean doDeleteIcon( final NodePath folderPath, final String localName )
    {
        boolean deleted = false;
        for ( final String extension : List.of( SchemaResourcePaths.SVG_EXTENSION, SchemaResourcePaths.PNG_EXTENSION ) )
        {
            final String fileName = localName + "." + extension;
            if ( dynamicResourceManager.resourceFileNodeExists( folderPath, fileName ) )
            {
                deleted |= dynamicResourceManager.deleteResourceFile( folderPath, fileName, false );
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

    private NodePath createComponentFolderPath( final DescriptorKey key, final DynamicComponentType dynamicType )
    {
        final NodePath componentRootPath = createComponentRootPath( key.getApplicationKey(), dynamicType );
        return new NodePath( componentRootPath, NodeName.from( key.getName() ) );
    }

    private NodePath createComponentRootPath( final ApplicationKey key, final DynamicComponentType dynamicType )
    {
        return new NodePath( createCmsFolderPath( key ), NodeName.from( getComponentRootName( dynamicType ) ) );
    }

    private NodePath createSchemaFolderPath( final BaseSchemaName key, final DynamicContentSchemaType dynamicType )
    {
        final NodePath schemaRootPath = createSchemaRootPath( key.getApplicationKey(), dynamicType );
        return new NodePath( schemaRootPath, NodeName.from( key.getLocalName() ) );
    }

    private NodePath createSchemaRootPath( final ApplicationKey key, final DynamicContentSchemaType dynamicType )
    {
        return new NodePath( createCmsFolderPath( key ), NodeName.from( getSchemaRootName( dynamicType ) ) );
    }

    private NodePath createMacroFolderPath( final MacroKey key )
    {
        return new NodePath( createMacroRootPath( key.getApplicationKey() ), NodeName.from( key.getName() ) );
    }

    private NodePath createMacroRootPath( final ApplicationKey key )
    {
        return new NodePath( createCmsFolderPath( key ), NodeName.from( SchemaResourcePaths.MACROS_ROOT_NAME ) );
    }

    private NodePath createPhrasesFolderPath( final ApplicationKey key )
    {
        return NodePath.create( createCmsFolderPath( key ) )
            .addElement( SchemaResourcePaths.I18N_ROOT_NAME )
            .addElement( SchemaResourcePaths.PHRASES_ROOT_NAME )
            .build();
    }

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
