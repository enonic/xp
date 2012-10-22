package com.enonic.wem.core.content.dao;


import javax.jcr.Node;

import org.junit.Test;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.itest.AbstractJcrTest;

import static org.junit.Assert.*;

public class ContentDaoImplTest
    extends AbstractJcrTest
{
    private ContentDao contentDao;

    public void setupDao()
        throws Exception
    {
        contentDao = new ContentDaoImpl();
    }

    @Test
    public void createContent_one_data_at_root()
        throws Exception
    {
        // setup
        Content content = new Content();
        content.setPath( ContentPath.from( "myContent" ) );
        content.setData( "myData", "myValue" );

        // exercise
        contentDao.createContent( session, content );
        commit();

        // verify
        Node contentNode = session.getNode( "/" + ContentDaoConstants.CONTENTS_PATH + "myContent" );
        assertNotNull( contentNode );

        assertFalse( contentNode.hasNode( "type" ) );

        Node contentDataNode = contentNode.getNode( "data" );
        assertNotNull( contentDataNode );

        Node myDataNode = contentDataNode.getNode( "myData" );
        assertNotNull( myDataNode );

        assertEquals( "myValue", myDataNode.getProperty( "value" ).getString() );
    }

    @Test
    public void createContent_one_data_at_root_and_one_in_a_set()
        throws Exception
    {
        // setup
        Content content = new Content();
        content.setPath( ContentPath.from( "myContent" ) );
        content.setData( "myData", "1" );
        content.setData( "mySet.myData", "2" );
        content.setData( "mySet.myOtherData", "3" );

        // exercise
        contentDao.createContent( session, content );
        commit();

        // verify
        Node contentNode = session.getNode( "/" + ContentDaoConstants.CONTENTS_PATH + "myContent" );

        assertFalse( contentNode.hasNode( "type" ) );

        Node contentDataNode = contentNode.getNode( "data" );

        assertEquals( "1", contentDataNode.getNode( "myData" ).getProperty( "value" ).getString() );
        assertEquals( "2", contentDataNode.getNode( "mySet/value/myData" ).getProperty( "value" ).getString() );
        assertEquals( "3", contentDataNode.getNode( "mySet/value/myOtherData" ).getProperty( "value" ).getString() );
    }

    @Test
    public void findContent()
        throws Exception
    {
        // setup
        Content content = new Content();
        content.setPath( ContentPath.from( "myContent" ) );
        content.setData( "myData", "myValue" );
        content.setData( "mySet.myData", "myOtherValue" );
        contentDao.createContent( session, content );
        commit();

        // exercise
        Content actualContent = contentDao.findContent( session, ContentPath.from( "myContent" ) );

        // verify
        assertNotNull( actualContent );
        assertEquals( "myContent", content.getPath().toString() );

        ContentData contentData = actualContent.getData();
        assertEquals( "myValue", contentData.getData( new EntryPath( "myData" ) ).getString() );
        assertEquals( "myOtherValue", contentData.getData( new EntryPath( "mySet.myData" ) ).getString() );
    }

}
