package com.enonic.wem.core.content.dao;


import javax.jcr.Node;

import org.junit.Test;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentTree;
import com.enonic.wem.api.content.Contents;
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
        contentDao.createContent( content, session );
        commit();

        // verify
        Node contentNode = session.getNode( "/" + ContentDaoConstants.CONTENTS_PATH + "myContent" );
        assertNotNull( contentNode );
    }

    @Test
    public void createContent_one_data_at_root_and_one_below()
        throws Exception
    {
        // setup
        Content rootContent = new Content();
        rootContent.setPath( ContentPath.from( "rootContent" ) );
        rootContent.setData( "myData", "myValue" );

        Content belowRootContent = new Content();
        belowRootContent.setPath( ContentPath.from( "rootContent/belowRootContent" ) );
        belowRootContent.setData( "myData", "myValue" );

        // exercise
        contentDao.createContent( rootContent, session );
        commit();
        contentDao.createContent( belowRootContent, session );
        commit();

        // verify
        Node rootContentNode = session.getNode( "/" + ContentDaoConstants.CONTENTS_PATH + "rootContent" );
        assertNotNull( rootContentNode );

        Node belowRootContentNode = session.getNode( "/" + ContentDaoConstants.CONTENTS_PATH + "rootContent/belowRootContent" );
        assertNotNull( belowRootContentNode );
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
        contentDao.createContent( content, session );
        commit();

        // verify
        assertNotNull( session.getNode( "/" + ContentDaoConstants.CONTENTS_PATH + "myContent" ) );

        Content storedContent = contentDao.findContent( ContentPath.from( "myContent" ), session );

        assertEquals( "1", storedContent.getData( "myData" ).getString() );
        assertEquals( "2", storedContent.getData( "mySet.myData" ).getString() );
        assertEquals( "3", storedContent.getData( "mySet.myOtherData" ).getString() );
    }

    @Test
    public void updateContent_one_data_at_root()
        throws Exception
    {
        // setup
        Content newContent = new Content();
        newContent.setPath( ContentPath.from( "myContent" ) );
        newContent.setData( "myData", "initial value" );

        // setup: create content to update
        contentDao.createContent( newContent, session );
        commit();

        Content updateContent = new Content();
        updateContent.setPath( ContentPath.from( "myContent" ) );
        updateContent.setData( "myData", "changed value" );

        // exercise
        contentDao.updateContent( updateContent, session );

        // verify
        assertNotNull( session.getNode( "/" + ContentDaoConstants.CONTENTS_PATH + "myContent" ) );

        Content storedContent = contentDao.findContent( ContentPath.from( "myContent" ), session );
        assertEquals( "changed value", storedContent.getData( "myData" ).getString() );
    }

    @Test
    public void delete_content_at_root()
        throws Exception
    {
        // setup
        Content content = new Content();
        content.setPath( ContentPath.from( "myContent" ) );
        content.setData( "myData", "myValue" );

        contentDao.createContent( content, session );
        commit();

        // exercise
        contentDao.deleteContent( ContentPath.from( "myContent" ), session );
        commit();

        // verify
        Node contentsNode = session.getNode( "/" + ContentDaoConstants.CONTENTS_PATH );
        assertFalse( contentsNode.hasNode( "myContent" ) );
    }

    @Test
    public void delete_content_below_other_content()
        throws Exception
    {
        // setup
        contentDao.createContent( createContent( "parentContent" ), session );
        contentDao.createContent( createContent( "parentContent/contentToDelete" ), session );
        commit();

        // exercise
        contentDao.deleteContent( ContentPath.from( "parentContent/contentToDelete" ), session );
        commit();

        // verify
        Node parentContentNode = session.getNode( "/" + ContentDaoConstants.CONTENTS_PATH + "parentContent" );
        assertFalse( parentContentNode.hasNode( "contentToDelete" ) );
    }

    @Test
    public void delete_content_which_have_subcontent_throws_exception()
        throws Exception
    {
        // setup
        contentDao.createContent( createContent( "parentContent" ), session );
        contentDao.createContent( createContent( "parentContent/contentToDelete" ), session );
        commit();

        // exercise
        try
        {
            contentDao.deleteContent( ContentPath.from( "parentContent" ), session );
            fail( "Expected excetion" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "Content with sub content cannot be deleted: parentContent", e.getMessage() );
        }
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
        contentDao.createContent( content, session );
        commit();

        // exercise
        Content actualContent = contentDao.findContent( ContentPath.from( "myContent" ), session );

        // verify
        assertNotNull( actualContent );
        assertEquals( "myContent", content.getPath().toString() );

        ContentData contentData = actualContent.getData();
        assertEquals( "myValue", contentData.getData( new EntryPath( "myData" ) ).getString() );
        assertEquals( "myOtherValue", contentData.getData( new EntryPath( "mySet.myData" ) ).getString() );
    }

    @Test
    public void findChildContent()
        throws Exception
    {
        // setup
        contentDao.createContent( createContent( "myParentContent" ), session );
        commit();
        contentDao.createContent( createContent( "myParentContent2" ), session );
        commit();
        contentDao.createContent( createContent( "myParentContent/myChildContent1" ), session );
        commit();
        contentDao.createContent( createContent( "myParentContent/myChildContent2" ), session );
        commit();

        // exercise
        Contents childContent = contentDao.findChildContent( ContentPath.from( "myParentContent" ), session );

        // verify
        assertTrue( childContent.isNotEmpty() );
        assertEquals( ContentPath.from( "myParentContent/myChildContent2" ), childContent.getList().get( 0 ).getPath() );
        assertEquals( ContentPath.from( "myParentContent/myChildContent1" ), childContent.getList().get( 1 ).getPath() );
    }

    @Test
    public void getContentTree()
        throws Exception
    {
        // setup
        contentDao.createContent( createContent( "branch-A" ), session );
        contentDao.createContent( createContent( "branch-A/branch-A-A" ), session );
        contentDao.createContent( createContent( "branch-B" ), session );
        contentDao.createContent( createContent( "branch-B/branch-B-A" ), session );
        contentDao.createContent( createContent( "branch-B/branch-B-A/branch-B-A-A" ), session );
        contentDao.createContent( createContent( "branch-B/branch-B-B" ), session );
        contentDao.createContent( createContent( "branch-B/branch-B-B/branch-B-B-A" ), session );
        commit();

        // exercise
        ContentTree tree = contentDao.getContentTree( session );

        // verify
    }

    private Content createContent( String path )
    {
        Content content = new Content();
        content.setPath( ContentPath.from( path ) );
        return content;
    }

}
