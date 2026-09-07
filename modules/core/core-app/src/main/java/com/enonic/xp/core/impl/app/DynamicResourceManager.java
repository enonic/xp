package com.enonic.xp.core.impl.app;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.resolver.NodeResourceApplicationUrlResolver;
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
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.schema.SchemaNodePropertyNames;

/**
 * Reads and writes schema resources stored as nodes below the application node in system-repo,
 * see {@link ApplicationRepoServiceImpl#applicationNodePath(ApplicationKey)}. Folder paths passed in are absolute node paths
 * below that application node, e.g. {@code /applications/myapp/cms/content-types/mytype}.
 */
final class DynamicResourceManager
{
    private final NodeService nodeService;

    DynamicResourceManager( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    Resource createResource( final NodePath folderPath, final String name, final String resource )
    {
        return ApplicationHelper.runAsAdmin( () -> {

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
                resourceData.setString( SchemaNodePropertyNames.RESOURCE, resource );
            }

            final Node schemaNode = nodeService.create( CreateNodeParams.create()
                                                            .parent( resourceFolder.path() )
                                                            .name( name + ".yaml" )
                                                            .data( resourceData )
                                                            .inheritPermissions( true )
                                                            .refresh( RefreshMode.ALL )
                                                            .build() );

            return new NodeValueResource( resourceKey( schemaNode.path() ), schemaNode );
        } );
    }

    Resource updateResource( final NodePath folderPath, final String name, final String resource )
    {
        return ApplicationHelper.runAsAdmin( () -> {

            final PropertyTree resourceData = new PropertyTree();

            if ( resource != null )
            {
                resourceData.setString( SchemaNodePropertyNames.RESOURCE, resource );
            }

            final Node schemaNode = nodeService.update( UpdateNodeParams.create()
                                                            .path( new NodePath( folderPath, NodeName.from( name + ".yaml" ) ) )
                                                            .editor( toBeEdited -> toBeEdited.data = resourceData )
                                                            .refresh( RefreshMode.ALL )
                                                            .build() );

            return new NodeValueResource( resourceKey( schemaNode.path() ), schemaNode );
        } );
    }

    boolean resourceNodeExists( final NodePath folderPath, final String name )
    {
        return ApplicationHelper.runAsAdmin( () -> nodeService.nodeExists( new NodePath( folderPath, NodeName.from( name + ".yaml" ) ) ) );
    }

    Resource getResource( final NodePath folderPath, final String name )
    {
        final ResourceKey resourceKey = ResourceKey.from( appKeyFromNodePath( folderPath ), resourcePathFromNodePath( folderPath ) + "/" + name + ".yaml" );
        return Optional.ofNullable( resolver( resourceKey.getApplicationKey() ).findResource( resourceKey.getPath() ) )
            .orElseGet( () -> new UrlResource( resourceKey, null ) );
    }

    List<Resource> listResources( final NodePath folderPath )
    {
        final ApplicationKey applicationKey = appKeyFromNodePath( folderPath );
        final NodeResourceApplicationUrlResolver resolver = resolver( applicationKey );
        final Pattern pattern = Pattern.compile( resourcePathFromNodePath( folderPath ) + "/" + ".+/.+\\.yaml" );

        return resolver.findFiles()
            .stream()
            .map( path -> ResourceKey.from( applicationKey, path ) )
            .filter( resourceKey -> pattern.matcher( resourceKey.getPath() ).find() )
            .map( resourceKey -> resolver.findResource( resourceKey.getPath() ) )
            .collect( Collectors.toList() );
    }

    boolean deleteResource( final NodePath folderPath, final String name, final boolean deleteFolder )
    {
        return ApplicationHelper.runAsAdmin( () -> nodeService.delete( DeleteNodeParams.create()
                                                                           .nodePath( deleteFolder
                                                                                          ? folderPath
                                                                                          : new NodePath( folderPath, NodeName.from( name + ".yaml" ) ) )
                                                                           .refresh( RefreshMode.ALL )
                                                                           .build() ) )
            .getNodeIds()
            .isNotEmpty();
    }

    private NodeResourceApplicationUrlResolver resolver( final ApplicationKey applicationKey )
    {
        return ApplicationFactory.createStaticAppNodeResolver( applicationKey, nodeService );
    }

    private static ResourceKey resourceKey( final NodePath nodePath )
    {
        return ResourceKey.from( appKeyFromNodePath( nodePath ), resourcePathFromNodePath( nodePath ) );
    }

    /**
     * The application key is the name of the node right below the applications folder.
     */
    static ApplicationKey appKeyFromNodePath( final NodePath path )
    {
        final String relativeToApplications = relativeToApplicationsFolder( path );
        final int endIndex = relativeToApplications.indexOf( '/' );
        return ApplicationKey.from( endIndex == -1 ? relativeToApplications : relativeToApplications.substring( 0, endIndex ) );
    }

    /**
     * The resource path is the node path relative to the application node, e.g. {@code /cms/content-types/mytype/mytype.yaml}.
     */
    private static String resourcePathFromNodePath( final NodePath path )
    {
        final String relativeToApplications = relativeToApplicationsFolder( path );
        final int beginIndex = relativeToApplications.indexOf( '/' );
        return beginIndex == -1 ? "" : relativeToApplications.substring( beginIndex );
    }

    private static String relativeToApplicationsFolder( final NodePath path )
    {
        final String prefix = ApplicationRepoServiceImpl.APPLICATION_PATH + "/";
        final String pathString = path.toString();
        if ( !pathString.startsWith( prefix ) )
        {
            throw new IllegalArgumentException( "Not an application resource path: " + path );
        }
        return pathString.substring( prefix.length() );
    }
}