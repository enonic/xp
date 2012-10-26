package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.core.jcr.JcrHelper;

class ContentJcrMapper
{
    private static final String TYPE = "type";

    private static final String DATA = "data";

    private DataJcrMapper dataMapper = new DataJcrMapper();

    void toJcr( final Content content, final Node contentNode )
        throws RepositoryException
    {
        if ( content.getType() != null )
        {
            contentNode.setProperty( TYPE, content.getType().getQualifiedName().toString() );
        }
        else
        {
            contentNode.setProperty( TYPE, (String) null );
        }

        final Node contentDataNode = JcrHelper.getOrAddNode( contentNode, DATA );
        dataMapper.fromDataSetToJcr( content.getData(), contentDataNode );
    }

    void toContent( final Node contentNode, final Content content )
        throws RepositoryException
    {
        final Node contentDataNode = contentNode.getNode( "data" );
        content.setData( toContentData( contentDataNode, new EntryPath() ) );
    }

    private ContentData toContentData( final Node contentDataNode, final EntryPath parentPath )
        throws RepositoryException
    {
        final ContentData contentData = new ContentData();
        final NodeIterator dataNodesIt = contentDataNode.getNodes();
        final DataSet dataSet = dataMapper.toDataSet( dataNodesIt, parentPath );
        contentData.setDataSet( dataSet );
        return contentData;
    }
}
