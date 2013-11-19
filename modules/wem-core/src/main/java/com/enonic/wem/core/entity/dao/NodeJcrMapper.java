package com.enonic.wem.core.entity.dao;


import javax.jcr.RepositoryException;

import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.serializer.RootDataSetJsonSerializer;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.core.jcr.JcrHelper;
import com.enonic.wem.core.support.dao.IconJcrMapper;
import com.enonic.wem.core.support.dao.IndexConfigJcrMapper;

import static com.enonic.wem.core.jcr.JcrHelper.getPropertyDateTime;

class NodeJcrMapper
{
    private static final String CREATED_TIME = "createdTime";

    private static final String CREATOR = "creator";

    private static final String MODIFIED_TIME = "modifiedTime";

    private static final String MODIFIER = "modifier";

    private static final String ROOT_DATA_SET = "rootDataSet";

    private static RootDataSetJsonSerializer rootDataSetJsonSerializer = new RootDataSetJsonSerializer();

    private static final IndexConfigJcrMapper indexConfigJcrMapper = new IndexConfigJcrMapper();

    private static IconJcrMapper iconJcrMapper = new IconJcrMapper();

    static void toJcr( final Node node, final javax.jcr.Node jcrNode )
        throws RepositoryException
    {
        JcrHelper.setPropertyDateTime( jcrNode, CREATED_TIME, node.getCreatedTime() );
        JcrHelper.setPropertyUserKey( jcrNode, CREATOR, node.getCreator() );
        JcrHelper.setPropertyDateTime( jcrNode, MODIFIED_TIME, node.getModifiedTime() );
        JcrHelper.setPropertyUserKey( jcrNode, MODIFIER, node.getModifier() );

        if ( node.icon() != null )
        {
            iconJcrMapper.toJcr( node.icon(), jcrNode );
        }

        final String rootDataSetAsJsonString = rootDataSetJsonSerializer.toString( node.data() );
        jcrNode.setProperty( ROOT_DATA_SET, rootDataSetAsJsonString );

        if ( node.getEntityIndexConfig() != null )
        {
            indexConfigJcrMapper.toJcr( node.getEntityIndexConfig(), jcrNode );
        }
    }

    static void updateNodeJcrNode( final UpdateNodeArgs updateNodeArgs, final javax.jcr.Node nodeNode )
        throws RepositoryException
    {
        final DateTime now = DateTime.now();

        JcrHelper.setPropertyDateTime( nodeNode, MODIFIED_TIME, now );
        JcrHelper.setPropertyUserKey( nodeNode, MODIFIER, updateNodeArgs.updater() );
        iconJcrMapper.toJcr( updateNodeArgs.icon(), nodeNode );

        final String rootDataSetAsJsonString = rootDataSetJsonSerializer.toString( updateNodeArgs.rootDataSet() );
        nodeNode.setProperty( ROOT_DATA_SET, rootDataSetAsJsonString );
    }

    static Node.Builder toNode( final javax.jcr.Node jcrNode )
    {
        try
        {
            NodePath nodePath = resolveNodePath( jcrNode );
            NodePath parentNodePath = nodePath.getParentPath();

            final EntityId entityId = EntityId.from( jcrNode.getIdentifier() );
            final Node.Builder builder = Node.newNode( entityId, jcrNode.getName() );
            builder.parent( parentNodePath );
            builder.creator( JcrHelper.getPropertyUserKey( jcrNode, CREATOR ) );
            builder.createdTime( getPropertyDateTime( jcrNode, CREATED_TIME ) );
            builder.modifier( JcrHelper.getPropertyUserKey( jcrNode, MODIFIER ) );
            builder.modifiedTime( getPropertyDateTime( jcrNode, MODIFIED_TIME ) );
            builder.icon( iconJcrMapper.toIcon( jcrNode ) );
            builder.entityIndexConfig( indexConfigJcrMapper.toEntityIndexConfig( jcrNode ) );

            final String dataSetAsString = jcrNode.getProperty( ROOT_DATA_SET ).getString();
            final DataSet dataSet = rootDataSetJsonSerializer.toObject( dataSetAsString );
            builder.rootDataSet( dataSet.toRootDataSet() );
            return builder;

        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( "Failed to read Node from Jcr-Node", e );
        }
    }

    private static NodePath resolveNodePath( javax.jcr.Node node )
        throws RepositoryException
    {
        final String jcrNodePath = node.getPath();
        Preconditions.checkState( jcrNodePath.startsWith( "/" + NodeJcrHelper.ITEMS_PATH ),
                                  "path to node does not start with [/" + NodeJcrHelper.ITEMS_PATH + "] as expected: " + jcrNodePath );
        final String nodePath = jcrNodePath.substring( NodeJcrHelper.ITEMS_PATH.length(), jcrNodePath.length() );
        return new NodePath( nodePath );
    }
}
