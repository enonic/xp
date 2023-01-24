package com.enonic.xp.core.impl.app;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.Node;
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
                resourceData.setXml( SchemaNodePropertyNames.RESOURCE, resource );
            }

            final Node schemaNode = nodeService.create( CreateNodeParams.create()
                                                            .parent( resourceFolder.path() )
                                                            .name( name + ".xml" )
                                                            .data( resourceData )
                                                            .inheritPermissions( true )
                                                            .refresh( RefreshMode.ALL )
                                                            .build() );

            final String applicationKeyAsString = schemaNode.path().getElementAsString( 0 );
            final ApplicationKey applicationKey = ApplicationKey.from( applicationKeyAsString );
            final NodePath resourceKeyPath = schemaNode.path().removeFromBeginning( NodePath.create( applicationKeyAsString ).build() );

            return new NodeValueResource( ResourceKey.from( applicationKey, resourceKeyPath.toString() ), schemaNode );
        } );
    }

    Resource updateResource( final NodePath folderPath, final String name, final String resource )
    {
        return VirtualAppContext.createContext().callWith( () -> {

            final PropertyTree resourceData = new PropertyTree();

            if ( resource != null )
            {
                resourceData.setXml( SchemaNodePropertyNames.RESOURCE, resource );
            }

            final Node schemaNode = nodeService.update( UpdateNodeParams.create()
                                                            .path( NodePath.create( folderPath, name + ".xml" ).build() )
                                                            .editor( toBeEdited -> toBeEdited.data = resourceData )
                                                            .refresh( RefreshMode.ALL )
                                                            .build() );

            final String applicationKeyAsString = schemaNode.path().getElementAsString( 0 );
            final ApplicationKey applicationKey = ApplicationKey.from( applicationKeyAsString );
            final NodePath resourceKeyPath = schemaNode.path().removeFromBeginning( NodePath.create( applicationKeyAsString ).build() );

            return new NodeValueResource( ResourceKey.from( applicationKey, resourceKeyPath.toString() ), schemaNode );
        } );
    }

    boolean resourceNodeExists( final NodePath folderPath, final String name )
    {
        return VirtualAppContext.createContext()
            .callWith( () -> nodeService.nodeExists( NodePath.create( folderPath, name + ".xml" ).build() ) );
    }

    Resource getResource( final NodePath folderPath, final String name )
    {
        final String applicationKeyAsString = folderPath.getElementAsString( 0 );
        final ApplicationKey applicationKey = ApplicationKey.from( applicationKeyAsString );
        final NodePath resourceFolderPath = folderPath.removeFromBeginning( NodePath.create( applicationKeyAsString ).build() );

        final NodePath resourcePath = NodePath.create( resourceFolderPath, name + ".xml" ).build();

        return VirtualAppContext.createContext()
            .callWith( () -> resourceService.getResource( ResourceKey.from( applicationKey, resourcePath.toString() ) ) );
    }

    List<Resource> listResources( final NodePath resourceRootPath )
    {
        final String applicationKeyAsString = resourceRootPath.getElementAsString( 0 );
        final ApplicationKey applicationKey = ApplicationKey.from( applicationKeyAsString );
        final NodePath resourceFolderPath = resourceRootPath.removeFromBeginning( NodePath.create( applicationKeyAsString ).build() );

        return VirtualAppContext.createContext()
            .callWith( () -> resourceService.findFiles( applicationKey, resourceFolderPath + "/" + ".+/.+\\.xml" )
                .stream()
                .map( resourceService::getResource )
                .collect( Collectors.toList() ) );
    }

    boolean deleteResource( final NodePath folderPath, final String name, final boolean deleteFolder )
    {
        return VirtualAppContext.createContext()
            .callWith( () -> nodeService.delete( DeleteNodeParams.create()
                                                     .nodePath( deleteFolder ? folderPath : NodePath.create( folderPath, name + ".xml" ).build() )
                                                     .refresh( RefreshMode.ALL )
                                                     .build() )
                 ).getNodeBranchEntries().isNotEmpty();
    }
}
