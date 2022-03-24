package com.enonic.xp.core.impl.app;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.resource.NodeValueResource;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.schema.SchemaNodePropertyNames;

final class DynamicResourceManager
{
    private final NodeService nodeService;

    DynamicResourceManager( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    Resource createResource( final NodePath folderPath, final ApplicationKey applicationKey, final String name, final String resource )
    {
        return this.createResource( folderPath, applicationKey, name, resource, true );
    }

    Resource createResource( final NodePath folderPath, final ApplicationKey applicationKey, final String name, final String resource,
                             final boolean createFolder )
    {
        return VirtualAppContext.createContext().callWith( () -> {

            final Node resourceFolder = createFolder
                ? nodeService.create( CreateNodeParams.create().name( folderPath.getName() ).parent( folderPath.getParentPath() ).build() )
                : nodeService.getByPath( folderPath );
            final PropertyTree resourceData = new PropertyTree();

            if ( resource != null )
            {
                resourceData.setXml( SchemaNodePropertyNames.RESOURCE, resource );
            }

            final Node schemaNode = nodeService.create(
                CreateNodeParams.create().parent( resourceFolder.path() ).name( name + ".xml" ).data( resourceData ).build() );

            return new NodeValueResource( ResourceKey.from( applicationKey, schemaNode.path().toString() ), schemaNode );
        } );
    }

    Resource updateResource( final NodePath folderPath, final ApplicationKey applicationKey, final String name, final String resource )
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
                                                            .build() );

            return new NodeValueResource( ResourceKey.from( applicationKey, schemaNode.path().toString() ), schemaNode );
        } );
    }

    Resource getResource( final NodePath folderPath, final ApplicationKey applicationKey, final String name )
    {
        final NodePath resourceNodePath = NodePath.create( folderPath, name + ".xml" ).build();

        return VirtualAppContext.createContext().callWith( () -> {
            final Node schemaNode = nodeService.getByPath( resourceNodePath );

            return new NodeValueResource( ResourceKey.from( applicationKey, schemaNode.path().toString() ), schemaNode );
        } );
    }

    boolean deleteResource( final NodePath folderPath, final String name, final boolean deleteFolder )
    {
        return VirtualAppContext.createContext()
            .callWith( () -> nodeService.deleteByPath( deleteFolder ? folderPath : NodePath.create( folderPath, name + ".xml" ).build() )
                .isNotEmpty() );
    }
}
