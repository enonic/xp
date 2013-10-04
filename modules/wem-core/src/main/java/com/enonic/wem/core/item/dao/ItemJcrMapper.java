package com.enonic.wem.core.item.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.item.Item;
import com.enonic.wem.api.item.ItemId;
import com.enonic.wem.api.item.ItemPath;
import com.enonic.wem.core.content.serializer.DataJsonSerializer;
import com.enonic.wem.core.jcr.JcrHelper;
import com.enonic.wem.core.support.dao.IconJcrMapper;

import static com.enonic.wem.core.jcr.JcrHelper.getPropertyDateTime;

class ItemJcrMapper
{
    private static final String CREATED_TIME = "createdTime";

    private static final String CREATOR = "creator";

    private static final String MODIFIED_TIME = "modifiedTime";

    private static final String MODIFIER = "modifier";

    private static final String ROOT_DATA_SET = "rootDataSet";

    private static DataJsonSerializer dataJsonSerializer = new DataJsonSerializer();

    private static IconJcrMapper iconJcrMapper = new IconJcrMapper();

    static void toJcr( final Item item, final Node itemNode )
        throws RepositoryException
    {
        JcrHelper.setPropertyDateTime( itemNode, CREATED_TIME, item.getCreatedTime() );
        JcrHelper.setPropertyUserKey( itemNode, CREATOR, item.getCreator() );
        JcrHelper.setPropertyDateTime( itemNode, MODIFIED_TIME, item.getModifiedTime() );
        JcrHelper.setPropertyUserKey( itemNode, MODIFIER, item.getModifier() );

        final String rootDataSetAsJsonString = dataJsonSerializer.toString( item.rootDataSet() );
        itemNode.setProperty( ROOT_DATA_SET, rootDataSetAsJsonString );
    }

    static void updateItemNode( final UpdateItemArgs updateItemArgs, final Node itemNode )
        throws RepositoryException
    {
        final DateTime now = DateTime.now();

        final Item existingItem = ItemJcrMapper.toItem( itemNode ).build();
        if ( updateItemArgs.readAt() != null && existingItem.getModifiedTime().isAfter( updateItemArgs.readAt() ) )
        {
            throw new ItemModifiedSinceRead( updateItemArgs.readAt(), existingItem );
        }

        JcrHelper.setPropertyDateTime( itemNode, MODIFIED_TIME, now );
        JcrHelper.setPropertyUserKey( itemNode, MODIFIER, updateItemArgs.updater() );
        iconJcrMapper.toJcr( updateItemArgs.icon(), itemNode );

        final String rootDataSetAsJsonString = dataJsonSerializer.toString( updateItemArgs.rootDataSet() );
        itemNode.setProperty( ROOT_DATA_SET, rootDataSetAsJsonString );
    }

    static Item.Builder toItem( final Node itemNode )
    {
        try
        {
            ItemPath itemPath = resolveItemPath( itemNode );
            ItemPath parentItemPath = itemPath.getParentPath();

            final ItemId itemId = new ItemId( itemNode.getIdentifier() );
            final Item.Builder builder = Item.newItem( itemId, itemNode.getName() );
            builder.parent( parentItemPath );
            builder.creator( JcrHelper.getPropertyUserKey( itemNode, CREATOR ) );
            builder.createdTime( getPropertyDateTime( itemNode, CREATED_TIME ) );
            builder.modifier( JcrHelper.getPropertyUserKey( itemNode, MODIFIER ) );
            builder.modifiedTime( getPropertyDateTime( itemNode, MODIFIED_TIME ) );

            final String dataSetAsString = itemNode.getProperty( ROOT_DATA_SET ).getString();
            final DataSet dataSet = (DataSet) dataJsonSerializer.toObject( dataSetAsString );
            builder.rootDataSet( dataSet.toRootDataSet() );
            return builder;

        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( "Failed to read Item from Node", e );
        }
    }

    private static ItemPath resolveItemPath( Node node )
        throws RepositoryException
    {
        final String nodePath = node.getPath();
        Preconditions.checkState( nodePath.startsWith( "/" + ItemJcrHelper.ITEMS_PATH ),
                                  "path to node does not start with [/" + ItemJcrHelper.ITEMS_PATH + "] as expected: " + nodePath );
        final String itemPath = nodePath.substring( ItemJcrHelper.ITEMS_PATH.length(), nodePath.length() );
        return new ItemPath( itemPath );
    }
}
