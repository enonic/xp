package com.enonic.xp.core.impl.app;

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
import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.resource.GetDynamicComponentParams;
import com.enonic.xp.resource.GetDynamicContentSchemaParams;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.UpdateDynamicComponentParams;
import com.enonic.xp.resource.UpdateDynamicContentSchemaParams;
import com.enonic.xp.resource.UpdateDynamicSiteParams;
import com.enonic.xp.resource.UpdateDynamicStylesParams;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.schema.BaseSchemaName;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.style.StyleDescriptor;

@Component(immediate = true, service = {DynamicSchemaService.class, DynamicSchemaServiceInternal.class})
public class DynamicSchemaServiceImpl
    implements DynamicSchemaService, DynamicSchemaServiceInternal
{
    private final DynamicResourceManager dynamicResourceManager;

    private final DynamicResourceParser dynamicResourceParser;

    @Activate
    public DynamicSchemaServiceImpl( @Reference final NodeService nodeService )
    {
        this.dynamicResourceManager = new DynamicResourceManager( nodeService );
        this.dynamicResourceParser = new DynamicResourceParser();
    }

    @Override
    public ComponentDescriptor createComponent( final CreateDynamicComponentParams params )
    {
        final ComponentDescriptor descriptor =
            dynamicResourceParser.parseComponent( params.getKey(), params.getType(), params.getResource() );

        final NodePath resourceFolderPath = createComponentFolderPath( params.getKey(), params.getType() );
        dynamicResourceManager.createResource( resourceFolderPath, params.getKey().getApplicationKey(), params.getKey().getName(),
                                               params.getResource() );

        return descriptor;

    }

    @Override
    public ComponentDescriptor updateComponent( final UpdateDynamicComponentParams params )
    {
        final ComponentDescriptor descriptor =
            dynamicResourceParser.parseComponent( params.getKey(), params.getType(), params.getResource() );

        final NodePath resourceFolderPath = createComponentFolderPath( params.getKey(), params.getType() );
        dynamicResourceManager.updateResource( resourceFolderPath, params.getKey().getApplicationKey(), params.getKey().getName(),
                                               params.getResource() );

        return descriptor;
    }


    @Override
    public BaseSchema<?> createContentSchema( final CreateDynamicContentSchemaParams params )
    {
        final BaseSchema<?> schema = dynamicResourceParser.parseSchema( params.getName(), params.getType(), params.getResource() );

        final NodePath resourceFolderPath = createSchemaFolderPath( params.getName(), params.getType() );
        dynamicResourceManager.createResource( resourceFolderPath, params.getName().getApplicationKey(), params.getName().getLocalName(),
                                               params.getResource() );

        return schema;
    }

    @Override
    public BaseSchema<?> updateContentSchema( final UpdateDynamicContentSchemaParams params )
    {
        final BaseSchema<?> schema = dynamicResourceParser.parseSchema( params.getName(), params.getType(), params.getResource() );

        final NodePath resourceFolderPath = createSchemaFolderPath( params.getName(), params.getType() );
        dynamicResourceManager.updateResource( resourceFolderPath, params.getName().getApplicationKey(), params.getName().getLocalName(),
                                               params.getResource() );
        return schema;
    }

    @Override
    public SiteDescriptor createSite( final ApplicationKey key, final String resource )
    {
        final SiteDescriptor site = dynamicResourceParser.parseSite( key, resource );

        final NodePath resourceFolderPath = createSiteFolderPath( key );
        dynamicResourceManager.createResource( resourceFolderPath, key, VirtualAppConstants.SITE_ROOT_NAME, resource, false );

        return site;
    }

    @Override
    public SiteDescriptor updateSite( final UpdateDynamicSiteParams params )
    {
        final SiteDescriptor site = dynamicResourceParser.parseSite( params.getKey(), params.getResource() );

        final NodePath resourceFolderPath = createSiteFolderPath( params.getKey() );
        dynamicResourceManager.updateResource( resourceFolderPath, params.getKey(), VirtualAppConstants.SITE_ROOT_NAME,
                                               params.getResource() );

        return site;
    }

    @Override
    public StyleDescriptor createStyles( final CreateDynamicStylesParams params )
    {
        final StyleDescriptor styles = dynamicResourceParser.parseStyles( params.getKey(), params.getResource() );

        final NodePath resourceFolderPath = createSiteFolderPath( params.getKey() );
        dynamicResourceManager.createResource( resourceFolderPath, params.getKey(), VirtualAppConstants.STYLES_NAME, params.getResource(),
                                               false );

        return styles;
    }

    @Override
    public StyleDescriptor updateStyles( final UpdateDynamicStylesParams params )
    {
        final StyleDescriptor styles = dynamicResourceParser.parseStyles( params.getKey(), params.getResource() );

        final NodePath resourceFolderPath = createSiteFolderPath( params.getKey() );
        dynamicResourceManager.updateResource( resourceFolderPath, params.getKey(), VirtualAppConstants.STYLES_NAME, params.getResource() );

        return styles;
    }

    @Override
    public ComponentDescriptor getComponent( final GetDynamicComponentParams params )
    {
        final NodePath resourceFolderPath = createComponentFolderPath( params.getKey(), params.getType() );
        final Resource resource =
            dynamicResourceManager.getResource( resourceFolderPath, params.getKey().getApplicationKey(), params.getKey().getName() );

        return dynamicResourceParser.parseComponent( params.getKey(), params.getType(), resource.readString() );
    }

    @Override
    public BaseSchema<?> getContentSchema( final GetDynamicContentSchemaParams params )
    {
        final NodePath resourceFolderPath = createSchemaFolderPath( params.getName(), params.getType() );
        final Resource resource =
            dynamicResourceManager.getResource( resourceFolderPath, params.getName().getApplicationKey(), params.getName().getLocalName() );

        return dynamicResourceParser.parseSchema( params.getName(), params.getType(), resource.readString() );
    }

    @Override
    public SiteDescriptor getSite( final ApplicationKey key )
    {
        final NodePath resourceFolderPath = createSiteFolderPath( key );
        final Resource resource = dynamicResourceManager.getResource( resourceFolderPath, key, VirtualAppConstants.SITE_ROOT_NAME );

        return dynamicResourceParser.parseSite( key, resource.readString() );
    }

    @Override
    public StyleDescriptor getStyles( final ApplicationKey key )
    {
        final NodePath resourceFolderPath = createSiteFolderPath( key );
        final Resource resource = dynamicResourceManager.getResource( resourceFolderPath, key, VirtualAppConstants.STYLES_NAME );

        return dynamicResourceParser.parseStyles( key, resource.readString() );
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
        final String resourceRootName = getComponentRootName( dynamicType );

        return NodePath.create( VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT,
                                "/" + key.getApplicationKey() + "/" + VirtualAppConstants.SITE_ROOT_NAME + "/" + resourceRootName + "/" +
                                    key.getName() ).build();
    }

    private NodePath createSchemaFolderPath( final BaseSchemaName key, final DynamicContentSchemaType dynamicType )
    {
        final String resourceRootName = getSchemaRootName( dynamicType );

        return NodePath.create( VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT,
                                "/" + key.getApplicationKey() + "/" + VirtualAppConstants.SITE_ROOT_NAME + "/" + resourceRootName + "/" +
                                    key.getLocalName() ).build();
    }

    private NodePath createSiteFolderPath( final ApplicationKey key )
    {
        return NodePath.create( VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT,
                                "/" + key.getName() + "/" + VirtualAppConstants.SITE_ROOT_NAME ).build();
    }

    private String getSchemaRootName( final DynamicContentSchemaType type )
    {
        switch ( type )
        {
            case CONTENT_TYPE:
                return VirtualAppConstants.CONTENT_TYPE_ROOT_NAME;
            case MIXIN:
                return VirtualAppConstants.MIXIN_ROOT_NAME;
            case X_DATA:
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
