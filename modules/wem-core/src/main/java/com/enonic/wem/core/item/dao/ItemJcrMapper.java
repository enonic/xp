package com.enonic.wem.core.item.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.item.Item;
import com.enonic.wem.api.item.ItemId;
import com.enonic.wem.core.content.serializer.DataJsonSerializer;
import com.enonic.wem.core.jcr.JcrHelper;

import static com.enonic.wem.core.jcr.JcrHelper.getPropertyDateTime;

class ItemJcrMapper
{
    private static final String CREATED_TIME = "createdTime";

    private static final String CREATOR = "creator";

    private static final String MODIFIED_TIME = "modifiedTime";

    private static final String MODIFIER = "modifier";

    private static final String ROOT_DATA_SET = "rootDataSet";

    private static DataJsonSerializer dataJsonSerializer = new DataJsonSerializer();

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

    static Item.Builder toItem( final Node itemNode )
    {
        try
        {
            final ItemId itemId = new ItemId( itemNode.getIdentifier() );
            final Item.Builder builder = Item.newItem( itemId, itemNode.getName() );
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
}
