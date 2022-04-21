package com.enonic.xp.core.impl.app;

import java.util.List;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

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
import com.enonic.xp.resource.ListDynamicSchemasParams;
import com.enonic.xp.resource.Resource;
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
    public DynamicSchemaResult<ComponentDescriptor> createComponent( final CreateDynamicComponentParams params )
    {
        final ComponentDescriptor descriptor =
            dynamicResourceParser.parseComponent( params.getKey(), params.getType(), params.getResource() );

        final NodePath resourceFolderPath = createComponentFolderPath( params.getKey(), params.getType(), true );
        final Resource resource =
            dynamicResourceManager.createResource( resourceFolderPath, params.getKey().getApplicationKey(), params.getKey().getName(),
                                                   params.getResource() );

        return new DynamicSchemaResult<>( descriptor, resource );

    }

    @Override
    public DynamicSchemaResult<ComponentDescriptor> updateComponent( final UpdateDynamicComponentParams params )
    {
        final ComponentDescriptor descriptor =
            dynamicResourceParser.parseComponent( params.getKey(), params.getType(), params.getResource() );

        final NodePath resourceFolderPath = createComponentFolderPath( params.getKey(), params.getType(), true );
        final Resource resource =
            dynamicResourceManager.updateResource( resourceFolderPath, params.getKey().getApplicationKey(), params.getKey().getName(),
                                                   params.getResource() );

        return new DynamicSchemaResult<>( descriptor, resource );
    }


    @Override
    public DynamicSchemaResult<BaseSchema<?>> createContentSchema( final CreateDynamicContentSchemaParams params )
    {
        final BaseSchema<?> schema = dynamicResourceParser.parseSchema( params.getName(), params.getType(), params.getResource() );

        final NodePath resourceFolderPath = createSchemaFolderPath( params.getName(), params.getType(), true );
        final Resource resource = dynamicResourceManager.createResource( resourceFolderPath, params.getName().getApplicationKey(),
                                                                         params.getName().getLocalName(), params.getResource() );

        return new DynamicSchemaResult<>( schema, resource );
    }

    @Override
    public DynamicSchemaResult<BaseSchema<?>> updateContentSchema( final UpdateDynamicContentSchemaParams params )
    {
        final BaseSchema<?> schema = dynamicResourceParser.parseSchema( params.getName(), params.getType(), params.getResource() );

        final NodePath resourceFolderPath = createSchemaFolderPath( params.getName(), params.getType(), true );
        final Resource resource = dynamicResourceManager.updateResource( resourceFolderPath, params.getName().getApplicationKey(),
                                                                         params.getName().getLocalName(), params.getResource() );
        return new DynamicSchemaResult<>( schema, resource );
    }

    @Override
    public SiteDescriptor createSite( final ApplicationKey key, final String resource )
    {
        final SiteDescriptor site = dynamicResourceParser.parseSite( key, resource );

        final NodePath resourceFolderPath = createSiteFolderPath( key, true );
        dynamicResourceManager.createResource( resourceFolderPath, key, VirtualAppConstants.SITE_ROOT_NAME, resource, false );

        return site;
    }

    @Override
    public DynamicSchemaResult<SiteDescriptor> updateSite( final UpdateDynamicSiteParams params )
    {
        final SiteDescriptor site = dynamicResourceParser.parseSite( params.getKey(), params.getResource() );

        final NodePath resourceFolderPath = createSiteFolderPath( params.getKey(), true );
        final Resource resource =
            dynamicResourceManager.updateResource( resourceFolderPath, params.getKey(), VirtualAppConstants.SITE_ROOT_NAME,
                                                   params.getResource() );

        return new DynamicSchemaResult<>( site, resource );
    }

    @Override
    public DynamicSchemaResult<StyleDescriptor> createStyles( final CreateDynamicStylesParams params )
    {
        final StyleDescriptor styles = dynamicResourceParser.parseStyles( params.getKey(), params.getResource() );

        final NodePath resourceFolderPath = createSiteFolderPath( params.getKey(), true );
        final Resource resource =
            dynamicResourceManager.createResource( resourceFolderPath, params.getKey(), VirtualAppConstants.STYLES_NAME,
                                                   params.getResource(), false );

        return new DynamicSchemaResult<>( styles, resource );
    }

    @Override
    public DynamicSchemaResult<StyleDescriptor> updateStyles( final UpdateDynamicStylesParams params )
    {
        final StyleDescriptor styles = dynamicResourceParser.parseStyles( params.getKey(), params.getResource() );

        final NodePath resourceFolderPath = createSiteFolderPath( params.getKey(), true );
        final Resource resource =
            dynamicResourceManager.updateResource( resourceFolderPath, params.getKey(), VirtualAppConstants.STYLES_NAME,
                                                   params.getResource() );

        return new DynamicSchemaResult<>( styles, resource );
    }

    @Override
    public DynamicSchemaResult<ComponentDescriptor> getComponent( final GetDynamicComponentParams params )
    {
        final NodePath resourceFolderPath = createComponentFolderPath( params.getKey(), params.getType(), false );
        final Resource resource =
            dynamicResourceManager.getResource( resourceFolderPath, params.getKey().getApplicationKey(), params.getKey().getName() );

        if ( resource.exists() && resource.getSize() > 0 )
        {
            final ComponentDescriptor descriptor =
                dynamicResourceParser.parseComponent( params.getKey(), params.getType(), resource.readString() );
            return new DynamicSchemaResult<>( descriptor, resource );
        }
        return null;
    }

    @Override
    public List<DynamicSchemaResult<ComponentDescriptor>> listComponents( final ListDynamicComponentsParams params )
    {
        return dynamicResourceManager.listResources( params.getKey(), createComponentRootPath( params.getKey(), params.getType(), false ) )
            .stream()
            .map( resource -> {
                final ComponentDescriptor descriptor =
                    dynamicResourceParser.parseComponent( DescriptorKey.from( params.getKey(), resource.getKey().getName() ),
                                                          params.getType(), resource.readString() );

                return new DynamicSchemaResult<>( descriptor, resource );
            } )
            .collect( Collectors.<DynamicSchemaResult<ComponentDescriptor>>toList() );
    }


    @Override
    public DynamicSchemaResult<BaseSchema<?>> getContentSchema( final GetDynamicContentSchemaParams params )
    {
        final NodePath resourceFolderPath = createSchemaFolderPath( params.getName(), params.getType(), false );
        final Resource resource =
            dynamicResourceManager.getResource( resourceFolderPath, params.getName().getApplicationKey(), params.getName().getLocalName() );

        if ( resource.exists() && resource.getSize() > 0 )
        {
            final BaseSchema<?> schema = dynamicResourceParser.parseSchema( params.getName(), params.getType(), resource.readString() );
            return new DynamicSchemaResult<>( schema, resource );
        }

        return null;
    }

    @Override
    public DynamicSchemaResult<SiteDescriptor> getSite( final ApplicationKey key )
    {
        final NodePath resourceFolderPath = createSiteFolderPath( key, false );

        final Resource resource = dynamicResourceManager.getResource( resourceFolderPath, key, VirtualAppConstants.SITE_ROOT_NAME );

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
        final NodePath resourceFolderPath = createSiteFolderPath( key, false );
        final Resource resource = dynamicResourceManager.getResource( resourceFolderPath, key, VirtualAppConstants.STYLES_NAME );

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
        final NodePath resourceFolderPath = createComponentFolderPath( params.getKey(), params.getType(), true );
        return dynamicResourceManager.deleteResource( resourceFolderPath, params.getKey().getName(), true );
    }

    @Override
    public boolean deleteContentSchema( final DeleteDynamicContentSchemaParams params )
    {
        final NodePath resourceFolderPath = createSchemaFolderPath( params.getName(), params.getType(), true );
        return dynamicResourceManager.deleteResource( resourceFolderPath, params.getName().getLocalName(), true );
    }

    @Override
    public List<DynamicSchemaResult<BaseSchema<?>>> listSchemas( final ListDynamicSchemasParams params )
    {
        final NodePath componentRootPath = createSchemaRootPath( params.getKey(), params.getType(), false );

        return dynamicResourceManager.listResources( params.getKey(), componentRootPath ).stream().map( resource -> {

            final BaseSchema<?> schema =
                dynamicResourceParser.parseSchema( getSchemaName( params.getKey(), params.getType(), resource.getKey().getName() ),
                                                   params.getType(), resource.readString() );

            return new DynamicSchemaResult<BaseSchema<?>>( schema, resource );
        } ).collect( Collectors.<DynamicSchemaResult<BaseSchema<?>>>toList() );
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

    @Override
    public boolean deleteSite( final ApplicationKey key )
    {
        final NodePath resourceFolderPath = createSiteFolderPath( key, true );
        return dynamicResourceManager.deleteResource( resourceFolderPath, VirtualAppConstants.SITE_ROOT_NAME, false );
    }

    @Override
    public boolean deleteStyles( final ApplicationKey key )
    {
        final NodePath resourceFolderPath = createSiteFolderPath( key, true );
        return dynamicResourceManager.deleteResource( resourceFolderPath, VirtualAppConstants.STYLES_NAME, false );
    }

    private NodePath createComponentFolderPath( final DescriptorKey key, final DynamicComponentType dynamicType, final boolean absolute )
    {
        final NodePath componentRootPath = createComponentRootPath( key.getApplicationKey(), dynamicType, absolute );
        return NodePath.create( componentRootPath, key.getName() ).build();
    }

    private NodePath createComponentRootPath( final ApplicationKey key, final DynamicComponentType dynamicType, final boolean absolute )
    {
        final String resourceRootName = getComponentRootName( dynamicType );
        return NodePath.create( absolute
                                    ? ( VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT + "/" + key + "/" )
                                    : "" + VirtualAppConstants.SITE_ROOT_NAME + "/" + resourceRootName ).build();
    }

    private NodePath createSchemaFolderPath( final BaseSchemaName key, final DynamicContentSchemaType dynamicType, final boolean absolute )
    {
        final NodePath schemaRootPath = createSchemaRootPath( key.getApplicationKey(), dynamicType, absolute );
        return NodePath.create( schemaRootPath, key.getLocalName() ).build();

    }

    private NodePath createSchemaRootPath( final ApplicationKey key, final DynamicContentSchemaType dynamicType, final boolean absolute )
    {
        final String resourceRootName = getSchemaRootName( dynamicType );
        return NodePath.create( absolute
                                    ? ( VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT + "/" + key + "/" )
                                    : "" + VirtualAppConstants.SITE_ROOT_NAME + "/" + resourceRootName ).build();
    }


    private NodePath createSiteFolderPath( final ApplicationKey key, final boolean absolute )
    {
        return NodePath.create( absolute
                                    ? ( VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT + "/" + key.getName() + "/" )
                                    : "" + VirtualAppConstants.SITE_ROOT_NAME ).build();
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
