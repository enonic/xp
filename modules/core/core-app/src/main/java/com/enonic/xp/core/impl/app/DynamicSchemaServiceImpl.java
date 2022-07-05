package com.enonic.xp.core.impl.app;

import java.util.List;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.base.Strings;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.issue.VirtualAppConstants;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.resource.CreateDynamicComponentParams;
import com.enonic.xp.resource.CreateDynamicContentSchemaParams;
import com.enonic.xp.resource.CreateDynamicStylesParams;
import com.enonic.xp.resource.DeleteDynamicComponentParams;
import com.enonic.xp.resource.DeleteDynamicContentSchemaParams;
import com.enonic.xp.resource.DynamicComponentType;
import com.enonic.xp.resource.DynamicContentSchemaType;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.resource.GetDynamicComponentParams;
import com.enonic.xp.resource.GetDynamicContentSchemaParams;
import com.enonic.xp.resource.ListDynamicComponentsParams;
import com.enonic.xp.resource.ListDynamicContentSchemasParams;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UpdateDynamicComponentParams;
import com.enonic.xp.resource.UpdateDynamicContentSchemaParams;
import com.enonic.xp.resource.UpdateDynamicSiteParams;
import com.enonic.xp.resource.UpdateDynamicStylesParams;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.schema.BaseSchemaName;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.style.StyleDescriptor;

@Component(immediate = true, service = {DynamicSchemaService.class, DynamicSchemaServiceInternal.class})
public class DynamicSchemaServiceImpl
    implements DynamicSchemaService, DynamicSchemaServiceInternal
{
    private final DynamicResourceManager dynamicResourceManager;

    private final DynamicResourceParser dynamicResourceParser;

    @Activate
    public DynamicSchemaServiceImpl( @Reference final NodeService nodeService, @Reference final ResourceService resourceService )
    {
        this.dynamicResourceManager = new DynamicResourceManager( nodeService, resourceService );
        this.dynamicResourceParser = new DynamicResourceParser();
    }

    @Override
    public <T extends ComponentDescriptor> DynamicSchemaResult<T> createComponent( final CreateDynamicComponentParams params )
    {
        final ComponentDescriptor descriptor =
            dynamicResourceParser.parseComponent( params.getKey(), params.getType(), params.getResource() );

        final NodePath resourceFolderPath = createComponentFolderPath( params.getKey(), params.getType() );
        final Resource resource =
            dynamicResourceManager.createResource( resourceFolderPath, params.getKey().getName(), params.getResource() );

        return new DynamicSchemaResult<>( (T) descriptor, resource );

    }

    @Override
    public <T extends ComponentDescriptor> DynamicSchemaResult<T> updateComponent( final UpdateDynamicComponentParams params )
    {
        final ComponentDescriptor descriptor =
            dynamicResourceParser.parseComponent( params.getKey(), params.getType(), params.getResource() );

        final NodePath resourceFolderPath = createComponentFolderPath( params.getKey(), params.getType() );
        final Resource resource =
            dynamicResourceManager.updateResource( resourceFolderPath, params.getKey().getName(), params.getResource() );

        return new DynamicSchemaResult<>( (T) descriptor, resource );
    }


    @Override
    public <T extends BaseSchema<?>> DynamicSchemaResult<T> createContentSchema( final CreateDynamicContentSchemaParams params )
    {
        final BaseSchema<?> schema = dynamicResourceParser.parseSchema( params.getName(), params.getType(), params.getResource() );

        final NodePath resourceFolderPath = createSchemaFolderPath( params.getName(), params.getType() );
        final Resource resource =
            dynamicResourceManager.createResource( resourceFolderPath, params.getName().getLocalName(), params.getResource() );

        return new DynamicSchemaResult<>( (T) schema, resource );
    }

    @Override
    public <T extends BaseSchema<?>> DynamicSchemaResult<T> updateContentSchema( final UpdateDynamicContentSchemaParams params )
    {
        final BaseSchema<?> schema = dynamicResourceParser.parseSchema( params.getName(), params.getType(), params.getResource() );

        final NodePath resourceFolderPath = createSchemaFolderPath( params.getName(), params.getType() );
        final Resource resource =
            dynamicResourceManager.updateResource( resourceFolderPath, params.getName().getLocalName(), params.getResource() );
        return new DynamicSchemaResult<>( (T) schema, resource );
    }

    @Override
    public DynamicSchemaResult<SiteDescriptor> createSite( final CreateDynamicSiteParams params )
    {
        final SiteDescriptor site = dynamicResourceParser.parseSite( params.getKey(), params.getResource() );

        final NodePath resourceFolderPath = createSiteFolderPath( params.getKey() );
        final Resource createdResource =
            dynamicResourceManager.createResource( resourceFolderPath, VirtualAppConstants.SITE_ROOT_NAME, params.getResource() );

        return new DynamicSchemaResult<>( site, createdResource );
    }

    @Override
    public DynamicSchemaResult<SiteDescriptor> updateSite( final UpdateDynamicSiteParams params )
    {
        final SiteDescriptor site = dynamicResourceParser.parseSite( params.getKey(), params.getResource() );

        final NodePath resourceFolderPath = createSiteFolderPath( params.getKey() );
        final Resource resource =
            dynamicResourceManager.updateResource( resourceFolderPath, VirtualAppConstants.SITE_ROOT_NAME, params.getResource() );

        return new DynamicSchemaResult<>( site, resource );
    }

    @Override
    public DynamicSchemaResult<StyleDescriptor> createStyles( final CreateDynamicStylesParams params )
    {
        final StyleDescriptor styles = dynamicResourceParser.parseStyles( params.getKey(), params.getResource() );

        final NodePath resourceFolderPath = createSiteFolderPath( params.getKey() );
        final Resource resource =
            dynamicResourceManager.createResource( resourceFolderPath, VirtualAppConstants.STYLES_NAME, params.getResource() );

        return new DynamicSchemaResult<>( styles, resource );
    }

    @Override
    public DynamicSchemaResult<StyleDescriptor> updateStyles( final UpdateDynamicStylesParams params )
    {
        final StyleDescriptor styles = dynamicResourceParser.parseStyles( params.getKey(), params.getResource() );

        final NodePath resourceFolderPath = createSiteFolderPath( params.getKey() );
        final Resource resource =
            dynamicResourceManager.updateResource( resourceFolderPath, VirtualAppConstants.STYLES_NAME, params.getResource() );

        return new DynamicSchemaResult<>( styles, resource );
    }

    @Override
    public <T extends ComponentDescriptor> DynamicSchemaResult<T> getComponent( final GetDynamicComponentParams params )
    {
        final NodePath resourceFolderPath = createComponentFolderPath( params.getKey(), params.getType() );
        final Resource resource = dynamicResourceManager.getResource( resourceFolderPath, params.getKey().getName() );

        if ( resource.exists() && resource.getSize() > 0 )
        {
            final ComponentDescriptor descriptor =
                dynamicResourceParser.parseComponent( params.getKey(), params.getType(), resource.readString() );
            return new DynamicSchemaResult<>( (T) descriptor, resource );
        }
        return null;
    }

    @Override
    public <T extends ComponentDescriptor> List<DynamicSchemaResult<T>> listComponents( final ListDynamicComponentsParams params )
    {
        return dynamicResourceManager.listResources( createComponentRootPath( params.getKey(), params.getType() ) )
            .stream()
            .map( resource -> {
                final ComponentDescriptor descriptor =
                    dynamicResourceParser.parseComponent( DescriptorKey.from( params.getKey(), getResourceName( resource.getKey() ) ),
                                                          params.getType(), resource.readString() );

                return new DynamicSchemaResult<>( (T) descriptor, resource );
            } )
            .collect( Collectors.<DynamicSchemaResult<T>>toList() );
    }


    @Override
    public <T extends BaseSchema<?>> DynamicSchemaResult<T> getContentSchema( final GetDynamicContentSchemaParams params )
    {
        final NodePath resourceFolderPath = createSchemaFolderPath( params.getName(), params.getType() );
        final Resource resource = dynamicResourceManager.getResource( resourceFolderPath, params.getName().getLocalName() );

        if ( resource.exists() && resource.getSize() > 0 )
        {
            final BaseSchema<?> schema = dynamicResourceParser.parseSchema( params.getName(), params.getType(), resource.readString() );
            return new DynamicSchemaResult<>( (T) schema, resource );
        }

        return null;
    }

    @Override
    public DynamicSchemaResult<SiteDescriptor> getSite( final ApplicationKey key )
    {
        final NodePath resourceFolderPath = createSiteFolderPath( key );

        final Resource resource = dynamicResourceManager.getResource( resourceFolderPath, VirtualAppConstants.SITE_ROOT_NAME );

        if ( resource.exists() && resource.getSize() > 0 )
        {
            final SiteDescriptor siteDescriptor = dynamicResourceParser.parseSite( key, resource.readString() );
            return new DynamicSchemaResult<>( siteDescriptor, resource );
        }
        return null;
    }

    @Override
    public DynamicSchemaResult<StyleDescriptor> getStyles( final ApplicationKey key )
    {
        final NodePath resourceFolderPath = createSiteFolderPath( key );
        final Resource resource = dynamicResourceManager.getResource( resourceFolderPath, VirtualAppConstants.STYLES_NAME );

        if ( resource.exists() && resource.getSize() > 0 )
        {
            final StyleDescriptor descriptor = dynamicResourceParser.parseStyles( key, resource.readString() );
            return new DynamicSchemaResult<>( descriptor, resource );
        }
        return null;
    }

    @Override
    public boolean deleteComponent( final DeleteDynamicComponentParams params )
    {
        final NodePath resourceFolderPath = createComponentFolderPath( params.getKey(), params.getType() );
        return dynamicResourceManager.deleteResource( resourceFolderPath, params.getKey().getName(), true );
    }

    @Override
    public boolean deleteContentSchema( final DeleteDynamicContentSchemaParams params )
    {
        final NodePath resourceFolderPath = createSchemaFolderPath( params.getName(), params.getType() );
        return dynamicResourceManager.deleteResource( resourceFolderPath, params.getName().getLocalName(), true );
    }

    @Override
    public <T extends BaseSchema<?>> List<DynamicSchemaResult<T>> listContentSchemas( final ListDynamicContentSchemasParams params )
    {
        final NodePath componentRootPath = createSchemaRootPath( params.getKey(), params.getType() );

        return dynamicResourceManager.listResources( componentRootPath ).stream().map( resource -> {

            final BaseSchema<?> schema =
                dynamicResourceParser.parseSchema( getSchemaName( params.getKey(), params.getType(), getResourceName( resource.getKey() ) ),
                                                   params.getType(), resource.readString() );

            return new DynamicSchemaResult<T>( (T) schema, resource );
        } ).collect( Collectors.<DynamicSchemaResult<T>>toList() );
    }

    private BaseSchemaName getSchemaName( final ApplicationKey applicationKey, final DynamicContentSchemaType type, final String name )
    {
        switch ( type )
        {
            case CONTENT_TYPE:
                return ContentTypeName.from( applicationKey, name );
            case MIXIN:
                return MixinName.from( applicationKey, name );
            case XDATA:
                return XDataName.from( applicationKey, name );
            default:
                throw new IllegalArgumentException( "invalid schema type: " + type );
        }
    }

    private String getResourceName( final ResourceKey resourceKey )
    {
        if ( Strings.isNullOrEmpty( resourceKey.getExtension() ) )
        {
            return resourceKey.getName();
        }
        else
        {
            return resourceKey.getName().substring( 0, resourceKey.getName().lastIndexOf( "." + resourceKey.getExtension() ) );
        }
    }

    @Override
    public boolean deleteSite( final ApplicationKey key )
    {
        final NodePath resourceFolderPath = createSiteFolderPath( key );
        return dynamicResourceManager.deleteResource( resourceFolderPath, VirtualAppConstants.SITE_ROOT_NAME, false );
    }

    @Override
    public boolean deleteStyles( final ApplicationKey key )
    {
        final NodePath resourceFolderPath = createSiteFolderPath( key );
        return dynamicResourceManager.deleteResource( resourceFolderPath, VirtualAppConstants.STYLES_NAME, false );
    }

    private NodePath createComponentFolderPath( final DescriptorKey key, final DynamicComponentType dynamicType )
    {
        final NodePath componentRootPath = createComponentRootPath( key.getApplicationKey(), dynamicType );
        return NodePath.create( componentRootPath, key.getName() ).build();
    }

    private NodePath createComponentRootPath( final ApplicationKey key, final DynamicComponentType dynamicType )
    {
        final String resourceRootName = getComponentRootName( dynamicType );
        return NodePath.create(
            ( VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT + "/" + key + "/" ) + VirtualAppConstants.SITE_ROOT_NAME + "/" +
                resourceRootName ).build();
    }

    private NodePath createSchemaFolderPath( final BaseSchemaName key, final DynamicContentSchemaType dynamicType )
    {
        final NodePath schemaRootPath = createSchemaRootPath( key.getApplicationKey(), dynamicType );
        return NodePath.create( schemaRootPath, key.getLocalName() ).build();

    }

    private NodePath createSchemaRootPath( final ApplicationKey key, final DynamicContentSchemaType dynamicType )
    {
        final String resourceRootName = getSchemaRootName( dynamicType );
        return NodePath.create(
                VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT + "/" + key + "/" + VirtualAppConstants.SITE_ROOT_NAME + "/" + resourceRootName )
            .build();
    }


    private NodePath createSiteFolderPath( final ApplicationKey key )
    {
        return NodePath.create(
            VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT + "/" + key.getName() + "/" + VirtualAppConstants.SITE_ROOT_NAME ).build();
    }

    private String getSchemaRootName( final DynamicContentSchemaType type )
    {
        switch ( type )
        {
            case CONTENT_TYPE:
                return VirtualAppConstants.CONTENT_TYPE_ROOT_NAME;
            case MIXIN:
                return VirtualAppConstants.MIXIN_ROOT_NAME;
            case XDATA:
                return VirtualAppConstants.X_DATA_ROOT_NAME;
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
}
