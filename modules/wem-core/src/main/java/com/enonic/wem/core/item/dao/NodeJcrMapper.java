package com.enonic.wem.core.item.dao;


import javax.jcr.RepositoryException;

import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.core.data.serializer.RootDataSetJsonSerializer;
import com.enonic.wem.core.jcr.JcrHelper;
import com.enonic.wem.core.support.dao.IconJcrMapper;

import static com.enonic.wem.core.jcr.JcrHelper.getPropertyDateTime;

class NodeJcrMapper
{
    private static final String CREATED_TIME = "createdTime";

    private static final String CREATOR = "creator";

    private static final String MODIFIED_TIME = "modifiedTime";

    private static final String MODIFIER = "modifier";

    private static final String ROOT_DATA_SET = "rootDataSet";

    private static RootDataSetJsonSerializer rootDataSetJsonSerializer = new RootDataSetJsonSerializer();

    private static IconJcrMapper iconJcrMapper = new IconJcrMapper();

    static void toJcr( final Node node, final javax.jcr.Node nodeNode )
        throws RepositoryException
    {
        JcrHelper.setPropertyDateTime( nodeNode, CREATED_TIME, node.getCreatedTime() );
        JcrHelper.setPropertyUserKey( nodeNode, CREATOR, node.getCreator() );
        JcrHelper.setPropertyDateTime( nodeNode, MODIFIED_TIME, node.getModifiedTime() );
        JcrHelper.setPropertyUserKey( nodeNode, MODIFIER, node.getModifier() );

        final String rootDataSetAsJsonString = rootDataSetJsonSerializer.toString( node.rootDataSet() );
        nodeNode.setProperty( ROOT_DATA_SET, rootDataSetAsJsonString );
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

    static Node.Builder toNode( final javax.jcr.Node nodeNode )
    {
        try
        {
            NodePath nodePath = resolveNodePath( nodeNode );
            NodePath parentNodePath = nodePath.getParentPath();

            final EntityId entityId = new EntityId( nodeNode.getIdentifier() );
            final Node.Builder builder = Node.newNode( entityId, nodeNode.getName() );
            builder.parent( parentNodePath );
            builder.creator( JcrHelper.getPropertyUserKey( nodeNode, CREATOR ) );
            builder.createdTime( getPropertyDateTime( nodeNode, CREATED_TIME ) );
            builder.modifier( JcrHelper.getPropertyUserKey( nodeNode, MODIFIER ) );
            builder.modifiedTime( getPropertyDateTime( nodeNode, MODIFIED_TIME ) );

            final String dataSetAsString = nodeNode.getProperty( ROOT_DATA_SET ).getString();
            final DataSet dataSet = (DataSet) rootDataSetJsonSerializer.toObject( dataSetAsString );
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
