package com.enonic.xp.core.impl.app;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.issue.VirtualAppConstants;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.resource.CreateDynamicSchemaParams;
import com.enonic.xp.resource.DeleteDynamicSchemaParams;
import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.resource.DynamicSchemaType;
import com.enonic.xp.resource.GetDynamicSchemaParams;
import com.enonic.xp.resource.NodeValueResource;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.schema.SchemaNodePropertyNames;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.xml.XmlException;
import com.enonic.xp.xml.parser.XmlContentTypeParser;
import com.enonic.xp.xml.parser.XmlLayoutDescriptorParser;
import com.enonic.xp.xml.parser.XmlPageDescriptorParser;
import com.enonic.xp.xml.parser.XmlPartDescriptorParser;

@Component(immediate = true, service = DynamicSchemaService.class)
public class DynamicSchemaServiceImpl
    implements DynamicSchemaService
{
    private final NodeService nodeService;

    @Activate
    public DynamicSchemaServiceImpl( @Reference final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    @Override
    public Resource create( final CreateDynamicSchemaParams params )
    {
        validate( params );

        final String resourceRootName = getResourceRootName( params.getType() );

        return VirtualAppContext.createContext().callWith( () -> {

            final NodePath resourceFolderPath = createResourceFolderPath( params.getKey(), resourceRootName );

            final Node resourceFolder = nodeService.create(
                CreateNodeParams.create().name( resourceFolderPath.getName() ).parent( resourceFolderPath.getParentPath() ).build() );

            final PropertyTree resourceData = new PropertyTree();
            if ( params.getResource() != null )
            {
                resourceData.setXml( SchemaNodePropertyNames.RESOURCE, params.getResource() );
            }

            final Node schemaNode = nodeService.create( CreateNodeParams.create()
                                                            .parent( resourceFolder.path() )
                                                            .name( params.getKey().getName() + ".xml" )
                                                            .data( resourceData )
                                                            .build() );

            return new NodeValueResource( ResourceKey.from( params.getKey().getApplicationKey(), schemaNode.path().toString() ),
                                          schemaNode );
        } );
    }

    @Override
    public Resource get( final GetDynamicSchemaParams params )
    {
        final String resourceRootName = getResourceRootName( params.getType() );
        final NodePath resourceFolderPath = createResourceFolderPath( params.getKey(), resourceRootName );

        final NodePath resourceNodePath = NodePath.create( resourceFolderPath, params.getKey().getName() + ".xml" ).build();

        return VirtualAppContext.createContext().callWith( () -> {
            final Node schemaNode = nodeService.getByPath( resourceNodePath );

            return new NodeValueResource( ResourceKey.from( params.getKey().getApplicationKey(), schemaNode.path().toString() ),
                                          schemaNode );
        } );
    }

    @Override
    public boolean delete( final DeleteDynamicSchemaParams params )
    {
        final String resourceRootName = getResourceRootName( params.getType() );
        final NodePath resourceFolderPath = createResourceFolderPath( params.getKey(), resourceRootName );

        return VirtualAppContext.createContext().callWith( () -> nodeService.deleteByPath( resourceFolderPath ).isNotEmpty() );
    }

    private void validate( final CreateDynamicSchemaParams params )
    {
        switch ( params.getType() )
        {
            case PAGE:
                validatePageDescriptor( params.getKey(), params.getResource() );
                break;
            case PART:
                validatePartDescriptor( params.getKey(), params.getResource() );
                break;
            case LAYOUT:
                validateLayoutDescriptor( params.getKey(), params.getResource() );
                break;
            case WIDGET:
                validateWidgetDescriptor( params.getKey(), params.getResource() );
                break;
            case CONTENT_TYPE:
                validateContentTypeDescriptor( params.getKey(), params.getResource() );
                break;
            default:
                throw new IllegalArgumentException( String.format( "unknown schema type: '%s'", params.getType() ) );
        }
    }

    private NodePath createResourceFolderPath( final DescriptorKey key, final String resourceRootName )
    {
        return NodePath.create( VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT,
                                "/" + key.getApplicationKey() + "/" + VirtualAppConstants.SITE_ROOT_NAME + "/" + resourceRootName + "/" +
                                    key.getName() ).build();
    }

    private void validatePageDescriptor( final DescriptorKey key, final String resource )
    {
        final PageDescriptor.Builder builder = PageDescriptor.create().key( key );
        try
        {
            final XmlPageDescriptorParser parser = new XmlPageDescriptorParser();
            parser.builder( builder );
            parser.currentApplication( key.getApplicationKey() );
            parser.source( resource );
            builder.key( key );
            parser.parse();
        }
        catch ( final Exception e )
        {
            throw new XmlException( e, "Could not load page descriptor [" + key + "]: " + e.getMessage() );
        }
    }

    private void validatePartDescriptor( final DescriptorKey key, final String resource )
    {
        final PartDescriptor.Builder builder = PartDescriptor.create();
        builder.key( key );
        try
        {
            final XmlPartDescriptorParser parser = new XmlPartDescriptorParser();
            parser.builder( builder );
            parser.currentApplication( key.getApplicationKey() );
            parser.source( resource );
            parser.parse();
        }
        catch ( final Exception e )
        {
            throw new XmlException( e, "Could not parse part descriptor [" + key + "]: " + e.getMessage() );
        }
    }

    private void validateLayoutDescriptor( final DescriptorKey key, final String resource )
    {
        final LayoutDescriptor.Builder builder = LayoutDescriptor.create();
        builder.key( key );
        try
        {
            final XmlLayoutDescriptorParser parser = new XmlLayoutDescriptorParser();
            parser.builder( builder );
            parser.currentApplication( key.getApplicationKey() );
            parser.source( resource );
            parser.parse();
        }
        catch ( final Exception e )
        {
            throw new XmlException( e, "Could not parse layout descriptor [" + key + "]: " + e.getMessage() );
        }
    }

    private void validateWidgetDescriptor( final DescriptorKey key, final String resource )
    {

    }

    private void validateContentTypeDescriptor( final DescriptorKey key, final String resource )
    {
        final ContentType.Builder builder = ContentType.create();

        final XmlContentTypeParser parser = new XmlContentTypeParser();
        parser.currentApplication( key.getApplicationKey() );
        parser.source( resource );
        parser.builder( builder );
        parser.parse();
    }

    private String getResourceRootName( final DynamicSchemaType type )
    {
        switch ( type )
        {
            case PAGE:
                return VirtualAppConstants.PAGE_ROOT_NAME;
            case PART:
                return VirtualAppConstants.PART_ROOT_NAME;
            case LAYOUT:
                return VirtualAppConstants.LAYOUT_ROOT_NAME;
            case CONTENT_TYPE:
                return VirtualAppConstants.CONTENT_TYPE_ROOT_NAME;
            case WIDGET:
                return VirtualAppConstants.WIDGET_ROOT_NAME;
            default:
                throw new IllegalArgumentException( "invalid resource type: " + type );
        }
    }
}
