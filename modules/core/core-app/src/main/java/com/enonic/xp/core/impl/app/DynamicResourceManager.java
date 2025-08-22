package com.enonic.xp.core.impl.app;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.SchemaNodePropertyNames;

final class DynamicResourceManager
{
    private final NodeService nodeService;

    private final ResourceService resourceService;

    DynamicResourceManager( final NodeService nodeService, final ResourceService resourceService )
    {
        this.nodeService = nodeService;
        this.resourceService = resourceService;
    }

    Resource createResource( final NodePath folderPath, final String name, final String resource )
    {
        return createResource( folderPath, name, resource, "xml" );
    }

    Resource createResource( final NodePath folderPath, final String name, final String resource, final String extension )
    {
        return VirtualAppContext.createContext().callWith( () -> {

            Node resourceFolder = nodeService.getByPath( folderPath );
            if ( resourceFolder == null )
            {
                resourceFolder = nodeService.create( CreateNodeParams.create()
                                                         .name( folderPath.getName() )
                                                         .parent( folderPath.getParentPath() )
                                                         .inheritPermissions( true )
                                                         .refresh( RefreshMode.ALL )
                                                         .build() );
            }

            final PropertyTree resourceData = new PropertyTree();

            if ( resource != null )
            {
                if ( "xml".equals( extension ) )
                {
                    resourceData.setXml( SchemaNodePropertyNames.RESOURCE, resource );
                }
                else
                {
                    resourceData.setString( SchemaNodePropertyNames.RESOURCE, resource );
                }
            }

            final Node schemaNode = nodeService.create( CreateNodeParams.create()
                                                            .parent( resourceFolder.path() )
                                                            .name( name + "." + extension )
                                                            .data( resourceData )
                                                            .inheritPermissions( true )
                                                            .refresh( RefreshMode.ALL )
                                                            .build() );

            return new NodeValueResource(
                ResourceKey.from( appKeyFromNodePath( folderPath ), resourcePathFromNodePath( schemaNode.path() ) ), schemaNode );
        } );
    }

    Resource updateResource( final NodePath folderPath, final String name, final String resource )
    {
        return updateResource( folderPath, name, resource, "xml" );
    }

    Resource updateResource( final NodePath folderPath, final String name, final String resource, final String extension )
    {
        return VirtualAppContext.createContext().callWith( () -> {

            final PropertyTree resourceData = new PropertyTree();

            if ( resource != null )
            {
                if ( "xml".equals( extension ) )
                {
                    resourceData.setXml( SchemaNodePropertyNames.RESOURCE, resource );
                }
                else
                {
                    resourceData.setString( SchemaNodePropertyNames.RESOURCE, resource );
                }
            }

            final Node schemaNode = nodeService.update( UpdateNodeParams.create()
                                                            .path( new NodePath( folderPath, NodeName.from( name + "." + extension ) ) )
                                                            .editor( toBeEdited -> toBeEdited.data = resourceData )
                                                            .refresh( RefreshMode.ALL )
                                                            .build() );

            return new NodeValueResource(
                ResourceKey.from( appKeyFromNodePath( schemaNode.path() ), resourcePathFromNodePath( schemaNode.path() ) ), schemaNode );
        } );
    }

    boolean resourceNodeExists( final NodePath folderPath, final String name )
    {
        return resourceNodeExists( folderPath, name, "xml" );
    }

    boolean resourceNodeExists( final NodePath folderPath, final String name, final String extension )
    {
        return VirtualAppContext.createContext()
            .callWith( () -> nodeService.nodeExists( new NodePath( folderPath, NodeName.from( name + "." + extension ) ) ) );
    }

    Resource getResource( final NodePath folderPath, final String name )
    {
        return getResource( folderPath, name, "xml" );
    }

    Resource getResource( final NodePath folderPath, final String name, final String extension )
    {
        return VirtualAppContext.createContext()
            .callWith( () -> resourceService.getResource( ResourceKey.from( appKeyFromNodePath( folderPath ),
                                                                            resourcePathFromNodePath( folderPath ) + "/" + name + "." +
                                                                                extension ) ) );
    }

    List<Resource> listResources( final NodePath folderPath )
    {
        return listResources( folderPath, "xml" );
    }

    List<Resource> listResources( final NodePath folderPath, final String extension )
    {
        return VirtualAppContext.createContext()
            .callWith( () -> resourceService.findFiles( appKeyFromNodePath( folderPath ),
                                                        resourcePathFromNodePath( folderPath ) + "/" + ".+/.+\\." + extension )
                .stream()
                .map( resourceService::getResource )
                .collect( Collectors.toList() ) );
    }

    boolean deleteResource( final NodePath folderPath, final String name, final boolean deleteFolder )
    {
        return deleteResource( folderPath, name, deleteFolder, "xml" );
    }

    boolean deleteResource( final NodePath folderPath, final String name, final boolean deleteFolder, final String extension )
    {
        return VirtualAppContext.createContext()
            .callWith( () -> nodeService.delete( DeleteNodeParams.create()
                                                     .nodePath( deleteFolder
                                                                    ? folderPath
                                                                    : new NodePath( folderPath, NodeName.from( name + "." + extension ) ) )
                                                     .refresh( RefreshMode.ALL )
                                                     .build() ) )
            .getNodeBranchEntries()
            .isNotEmpty();
    }

    public static ApplicationKey appKeyFromNodePath( final NodePath path )
    {
        final String pathString = path.toString();
        final int endIndex = pathString.indexOf( '/', 1 );
        return ApplicationKey.from( pathString.substring( 1, endIndex == -1 ? pathString.length() : endIndex ) );
    }

    private static String resourcePathFromNodePath( final NodePath path )
    {
        final String pathString = path.toString();
        final int beginIndex = pathString.indexOf( '/', 1 );
        return pathString.substring( beginIndex == -1 ? 1 : beginIndex );
    }
}
