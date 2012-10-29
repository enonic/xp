package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.core.content.data.ContentDataSerializerJson;

class ContentJcrMapper
{
    private static final String TYPE = "type";

    private static final String DATA = "data";

    private ContentDataSerializerJson contentDataSerializerJson = new ContentDataSerializerJson();

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

        final String dataAsJson = contentDataSerializerJson.toString( content.getData() );
        contentNode.setProperty( DATA, dataAsJson );
    }

    void toContent( final Node contentNode, final Content content )
        throws RepositoryException
    {
        String dataAsJson = contentNode.getProperty( "data" ).getString();
        final ContentData contentData = contentDataSerializerJson.toObject( dataAsJson );
        content.setData( contentData );
    }
}
