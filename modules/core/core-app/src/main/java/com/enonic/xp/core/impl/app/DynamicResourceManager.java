package com.enonic.xp.core.impl.app;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.resolver.NodeResourceApplicationUrlResolver;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodeNotFoundException;
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
 * <p>
 * Descriptors and phrases are stored as text in the {@code resource} property, icons as a node binary.
 */
final class DynamicResourceManager
{
    private static final String YAML_EXTENSION = ".yaml";

    private final NodeService nodeService;

    DynamicResourceManager( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    Resource createResource( final NodePath folderPath, final String name, final String resource )
    {
        return createResourceFile( folderPath, name + YAML_EXTENSION, resource );
    }

    Resource createResourceFile( final NodePath folderPath, final String fileName, final String resource )
    {
        return ApplicationHelper.runAsAdmin( () -> {
            final Node resourceFolder = ensureFolder( folderPath );

            final Node schemaNode = nodeService.create( CreateNodeParams.create()
                                                            .parent( resourceFolder.path() )
                                                            .name( fileName )
                                                            .data( textResourceData( resource ) )
                                                            .inheritPermissions( true )
                                                            .refresh( RefreshMode.ALL )
                                                            .build() );

            return new NodeValueResource( resourceKey( schemaNode.path() ), schemaNode );
        } );
    }

    Resource updateResource( final NodePath folderPath, final String name, final String resource )
    {
        return updateResourceFile( folderPath, name + YAML_EXTENSION, resource );
    }

    Resource updateResourceFile( final NodePath folderPath, final String fileName, final String resource )
    {
        return ApplicationHelper.runAsAdmin( () -> {
            final PropertyTree resourceData = textResourceData( resource );

            final Node schemaNode = nodeService.update( UpdateNodeParams.create()
                                                            .path( new NodePath( folderPath, NodeName.from( fileName ) ) )
                                                            .editor( toBeEdited -> toBeEdited.data = resourceData )
                                                            .refresh( RefreshMode.ALL )
                                                            .build() );

            return new NodeValueResource( resourceKey( schemaNode.path() ), schemaNode );
        } );
    }

    /**
     * Stores a binary (an icon) as a node with the mime type in the data and the content attached as the icon binary,
     * the same way {@link ApplicationRepoServiceImpl#persistApplicationSchema} persists the icons shipped by an application.
     * An existing node of the same name is replaced.
     */
    Resource putBinaryResourceFile( final NodePath folderPath, final String fileName, final ByteSource data, final String mimeType )
    {
        return ApplicationHelper.runAsAdmin( () -> {
            final PropertyTree resourceData = new PropertyTree();
            resourceData.setString( SchemaNodePropertyNames.MIME_TYPE, mimeType );
            resourceData.setBinaryReference( SchemaNodePropertyNames.ICON, SchemaResourceNames.ICON_BINARY_REFERENCE );

            final NodePath filePath = new NodePath( folderPath, NodeName.from( fileName ) );

            final Node node;
            if ( nodeService.nodeExists( filePath ) )
            {
                node = nodeService.update( UpdateNodeParams.create()
                                               .path( filePath )
                                               .editor( toBeEdited -> toBeEdited.data = resourceData )
                                               .attachBinary( SchemaResourceNames.ICON_BINARY_REFERENCE, data )
                                               .refresh( RefreshMode.ALL )
                                               .build() );
            }
            else
            {
                node = nodeService.create( CreateNodeParams.create()
                                               .parent( folderPath )
                                               .name( fileName )
                                               .data( resourceData )
                                               .attachBinary( SchemaResourceNames.ICON_BINARY_REFERENCE, data )
                                               .inheritPermissions( true )
                                               .refresh( RefreshMode.ALL )
                                               .build() );
            }

            return new NodeValueResource( resourceKey( node.path() ), data, node.getTimestamp() );
        } );
    }

    boolean resourceNodeExists( final NodePath folderPath, final String name )
    {
        return resourceFileNodeExists( folderPath, name + YAML_EXTENSION );
    }

    boolean resourceFileNodeExists( final NodePath folderPath, final String fileName )
    {
        return ApplicationHelper.runAsAdmin( () -> nodeService.nodeExists( new NodePath( folderPath, NodeName.from( fileName ) ) ) );
    }

    /**
     * Bumps the timestamp of a resource node without changing its content: a descriptor is re-read by the schema services when
     * its icon, stored in a sibling node, is set or removed.
     */
    void touchResourceFile( final NodePath folderPath, final String fileName )
    {
        ApplicationHelper.runAsAdmin( () -> nodeService.update( UpdateNodeParams.create()
                                                                    .path( new NodePath( folderPath, NodeName.from( fileName ) ) )
                                                                    .editor( toBeEdited -> toBeEdited.data.setInstant(
                                                                        SchemaNodePropertyNames.ICON_MODIFIED_TIME, Instant.now() ) )
                                                                    .refresh( RefreshMode.ALL )
                                                                    .build() ) );
    }

    Resource getResource( final NodePath folderPath, final String name )
    {
        return getResourceFile( folderPath, name + YAML_EXTENSION );
    }

    /**
     * @return the resource, which does not {@link Resource#exists() exist} when there is no such node
     */
    Resource getResourceFile( final NodePath folderPath, final String fileName )
    {
        final ResourceKey resourceKey =
            ResourceKey.from( appKeyFromNodePath( folderPath ), resourcePathFromNodePath( folderPath ) + "/" + fileName );
        return Optional.ofNullable( resolver( resourceKey.getApplicationKey() ).findResource( resourceKey.getPath() ) )
            .orElseGet( () -> new UrlResource( resourceKey, null ) );
    }

    /**
     * The descriptors below a root folder: {@code <folder>/<name>/<name>.yaml}.
     */
    List<Resource> listResources( final NodePath folderPath )
    {
        return listResourceFiles( folderPath, ".+/.+\\.yaml" );
    }

    /**
     * @param fileNamePattern regex matched against the resource path relative to the folder, without the leading slash
     */
    List<Resource> listResourceFiles( final NodePath folderPath, final String fileNamePattern )
    {
        final ApplicationKey applicationKey = appKeyFromNodePath( folderPath );
        final NodeResourceApplicationUrlResolver resolver = resolver( applicationKey );
        final Pattern pattern = Pattern.compile( "^" + Pattern.quote( resourcePathFromNodePath( folderPath ) + "/" ) + fileNamePattern + "$" );

        return resolver.findFiles()
            .stream()
            .filter( path -> pattern.matcher( path ).matches() )
            .map( resolver::findResource )
            .collect( Collectors.toList() );
    }

    boolean deleteResource( final NodePath folderPath, final String name, final boolean deleteFolder )
    {
        return deleteResourceFile( folderPath, name + YAML_EXTENSION, deleteFolder );
    }

    boolean deleteResourceFile( final NodePath folderPath, final String fileName, final boolean deleteFolder )
    {
        return ApplicationHelper.runAsAdmin( () -> nodeService.delete( DeleteNodeParams.create()
                                                                           .nodePath( deleteFolder
                                                                                          ? folderPath
                                                                                          : new NodePath( folderPath, NodeName.from( fileName ) ) )
                                                                           .refresh( RefreshMode.ALL )
                                                                           .build() ) )
            .getNodeIds()
            .isNotEmpty();
    }

    /**
     * The folders on the way to a resource are created as needed, but only below the {@code cms} node of the application:
     * the application node and its {@code cms} node mark a node backed application and are never created here.
     */
    private Node ensureFolder( final NodePath folderPath )
    {
        final Node folder = nodeService.getByPath( folderPath );
        if ( folder != null )
        {
            return folder;
        }

        final NodePath cmsPath = cmsPathFromNodePath( folderPath );
        if ( !folderPath.toString().startsWith( cmsPath + "/" ) )
        {
            throw new NodeNotFoundException( String.format( "Node [%s] not found", folderPath ) );
        }

        final Node parent = ensureFolder( folderPath.getParentPath() );

        return nodeService.create( CreateNodeParams.create()
                                       .name( folderPath.getName() )
                                       .parent( parent.path() )
                                       .inheritPermissions( true )
                                       .refresh( RefreshMode.ALL )
                                       .build() );
    }

    private static PropertyTree textResourceData( final String resource )
    {
        final PropertyTree resourceData = new PropertyTree();
        if ( resource != null )
        {
            resourceData.setString( SchemaNodePropertyNames.RESOURCE, resource );
        }
        return resourceData;
    }

    private NodeResourceApplicationUrlResolver resolver( final ApplicationKey applicationKey )
    {
        return ApplicationFactory.createPersistedSchemaResolver( applicationKey, nodeService );
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

    private static NodePath cmsPathFromNodePath( final NodePath path )
    {
        return new NodePath( ApplicationRepoServiceImpl.applicationNodePath( appKeyFromNodePath( path ) ),
                             NodeName.from( SchemaResourceNames.CMS_ROOT_NAME ) );
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
