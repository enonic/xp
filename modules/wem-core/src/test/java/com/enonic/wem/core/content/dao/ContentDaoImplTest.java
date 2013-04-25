package com.enonic.wem.core.content.dao;


import javax.jcr.Node;

import org.junit.Test;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentAlreadyExistException;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.UnableToDeleteContentException;
import com.enonic.wem.api.content.data.DataPath;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.Value;
import com.enonic.wem.api.exception.SpaceNotFoundException;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.core.AbstractJcrTest;

import static com.enonic.wem.api.content.Content.newContent;
import static org.junit.Assert.*;

public class ContentDaoImplTest
    extends AbstractJcrTest
{
    private ContentDao contentDao;

    public void setupDao()
        throws Exception
    {
        session.getNode( "/wem/spaces" ).addNode( "myspace" );
        contentDao = new ContentDaoImpl();
    }

    @Test
    public void createRootContent()
        throws Exception
    {
        // setup
        Content content = newContent().path( ContentPath.from( "myspace:/" ) ).build();
        content.getContentData().setProperty( "myData", new Value.Text( "myValue" ) );

        // exercise
        contentDao.create( content, session );
        commit();

        // verify
        Node contentNode = session.getNode( "/" + ContentDao.SPACES_PATH + "myspace/root" );
        assertNotNull( contentNode );
    }

    @Test(expected = SpaceNotFoundException.class)
    public void createRootContent_missing_space()
        throws Exception
    {
        // setup
        Content content = newContent().path( ContentPath.from( "otherspace:/" ) ).build();
        content.getContentData().setProperty( "myData", new Value.Text( "myValue" ) );

        // exercise
        contentDao.create( content, session );
        commit();
    }

    @Test
    public void deleteRootContent()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        contentDao.create( createContent( "myspace:parentContent" ), session );
        contentDao.create( createContent( "myspace:parentContent/contentToDelete" ), session );
        commit();

        // exercise
        try
        {
            contentDao.delete( ContentPath.from( "myspace:/" ), session );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UnableToDeleteContentException );
            assertEquals( "Not able to delete content with path [myspace:/]: Root content of a space.", e.getMessage() );
        }
    }

    @Test
    public void createContent_one_data_at_root()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );

        Content content = newContent().path( ContentPath.from( "myspace:myContent" ) ).build();
        content.getContentData().setProperty( "myData", new Value.Text( "myValue" ) );

        // exercise
        contentDao.create( content, session );
        commit();

        // verify
        Node contentNode = session.getNode( "/" + ContentDao.SPACES_PATH + "myspace/root/myContent" );
        assertNotNull( contentNode );
    }

    @Test
    public void createContent_one_data_at_root_and_one_below()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );

        Content rootContent = newContent().path( ContentPath.from( "myspace:rootContent" ) ).build();
        rootContent.getContentData().setProperty( "myData", new Value.Text( "myValue" ) );

        Content belowRootContent = newContent().path( ContentPath.from( "myspace:rootContent/belowRootContent" ) ).build();
        belowRootContent.getContentData().setProperty( "myData", new Value.Text( "myValue" ) );

        // exercise
        contentDao.create( rootContent, session );
        contentDao.create( belowRootContent, session );
        commit();

        // verify
        Node rootContentNode = session.getNode( "/" + ContentDao.SPACES_PATH + "myspace/root/rootContent" );
        assertNotNull( rootContentNode );

        Node belowRootContentNode = session.getNode( "/" + ContentDao.SPACES_PATH + "myspace/root/rootContent/belowRootContent" );
        assertNotNull( belowRootContentNode );
    }

    @Test
    public void createContent_one_data_at_root_and_one_in_a_set()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );

        Content content = newContent().path( ContentPath.from( "myspace:myContent" ) ).build();
        content.getContentData().setProperty( "myData", new Value.Text( "1" ) );
        content.getContentData().setProperty( "mySet.myData", new Value.Text( "2" ) );
        content.getContentData().setProperty( "mySet.myOtherData", new Value.Text( "3" ) );

        // exercise
        contentDao.create( content, session );
        commit();

        // verify
        assertNotNull( session.getNode( "/" + ContentDao.SPACES_PATH + "myspace/root/myContent" ) );

        Content storedContent = contentDao.select( ContentPath.from( "myspace:myContent" ), session );

        assertEquals( "1", storedContent.getContentData().getProperty( "myData" ).getString() );
        assertEquals( "2", storedContent.getContentData().getProperty( "mySet.myData" ).getString() );
        assertEquals( "3", storedContent.getContentData().getProperty( "mySet.myOtherData" ).getString() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_content_with_assigned_id_throws_exception()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );

        Content content = createContent( "myspace:someContent" );
        ContentId createdContentId = contentDao.create( content, session );

        Content anotherContent = newContent( content ).id( createdContentId ).path( ContentPath.from( "myspace:anotherContent" ) ).build();
        assertNotNull( anotherContent.getId() );
        contentDao.create( anotherContent, session );

        // exercise
        commit();
    }

    @Test
    public void updateContent_one_data_at_root()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );

        Content newContent = newContent().path( ContentPath.from( "myspace:myContent" ) ).build();
        newContent.getContentData().setProperty( "myData", new Value.Text( "initial value" ) );

        // setup: create content to update
        contentDao.create( newContent, session );
        commit();

        Content updateContent = newContent().path( ContentPath.from( "myspace:myContent" ) ).build();
        updateContent.getContentData().setProperty( "myData", new Value.Text( "changed value" ) );

        // exercise
        contentDao.update( updateContent, true, session );

        // verify
        assertNotNull( session.getNode( "/" + ContentDao.SPACES_PATH + "myspace/root/myContent" ) );

        Content storedContent = contentDao.select( ContentPath.from( "myspace:myContent" ), session );
        assertEquals( "changed value", storedContent.getContentData().getProperty( "myData" ).getString() );
    }

    @Test
    public void delete_content_at_root()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );

        Content content = newContent().path( ContentPath.from( "myspace:myContent" ) ).build();
        content.getContentData().setProperty( "myData", new Value.Text( "myValue" ) );

        contentDao.create( content, session );
        commit();

        // exercise
        contentDao.delete( ContentPath.from( "myspace:myContent" ), session );
        commit();

        // verify
        Node contentsNode = session.getNode( "/" + ContentDao.SPACES_PATH + "myspace/root" );
        assertFalse( contentsNode.hasNode( "myContent" ) );
    }

    @Test
    public void delete_content_below_other_content()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        contentDao.create( createContent( "myspace:parentContent" ), session );
        contentDao.create( createContent( "myspace:parentContent/contentToDelete" ), session );
        commit();

        // exercise
        contentDao.delete( ContentPath.from( "myspace:parentContent/contentToDelete" ), session );
        commit();

        // verify
        Node parentContentNode = session.getNode( "/" + ContentDao.SPACES_PATH + "myspace/root/parentContent" );
        assertFalse( parentContentNode.hasNode( "contentToDelete" ) );
    }

    @Test
    public void delete_content_which_have_subcontent_throws_exception()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        contentDao.create( createContent( "myspace:parentContent" ), session );
        contentDao.create( createContent( "myspace:parentContent/contentToDelete" ), session );
        commit();

        // exercise
        try
        {
            contentDao.delete( ContentPath.from( "myspace:parentContent" ), session );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UnableToDeleteContentException );
            assertEquals( "Not able to delete content with path [myspace:/parentContent]: Content has child content.", e.getMessage() );
        }
    }

    @Test
    public void delete_content_by_id()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        contentDao.create( createContent( "myspace:parentContent" ), session );
        ContentId contentId = contentDao.create( createContent( "myspace:parentContent/contentToDelete" ), session );
        commit();

        // exercise
        contentDao.delete( contentId, session );
        commit();

        // verify
        Node parentContentNode = session.getNode( "/" + ContentDao.SPACES_PATH + "myspace/root/parentContent" );
        assertFalse( parentContentNode.hasNode( "contentToDelete" ) );
    }

    @Test
    public void findContentByPath()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        Content content = newContent().path( ContentPath.from( "myspace:myContent" ) ).build();
        content.getContentData().setProperty( "myData", new Value.Text( "myValue" ) );
        content.getContentData().setProperty( "mySet.myData", new Value.Text( "myOtherValue" ) );
        contentDao.create( content, session );
        commit();

        // exercise
        Content actualContent = contentDao.select( ContentPath.from( "myspace:myContent" ), session );

        // verify
        assertNotNull( actualContent );
        assertEquals( "myspace:/myContent", content.getPath().toString() );

        DataSet contentData = actualContent.getContentData();
        assertEquals( "myValue", contentData.getProperty( DataPath.from( "myData" ) ).getString() );
        assertEquals( "myOtherValue", contentData.getProperty( DataPath.from( "mySet.myData" ) ).getString() );
    }

    @Test
    public void findMultipleContentsByPath()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        Content content = newContent().path( ContentPath.from( "myspace:myContent" ) ).build();
        content.getContentData().setProperty( "myData", new Value.Text( "myValue" ) );
        content.getContentData().setProperty( "mySet.myData", new Value.Text( "myOtherValue" ) );
        contentDao.create( content, session );

        Content content2 = newContent().path( ContentPath.from( "myspace:myContent2" ) ).build();
        content2.getContentData().setProperty( "myData", new Value.Text( "myValue2" ) );
        content2.getContentData().setProperty( "mySet.myData", new Value.Text( "myOtherValue2" ) );
        contentDao.create( content2, session );
        commit();

        // exercise
        Contents actualContents = contentDao.select( ContentPaths.from( "myspace:myContent", "myspace:myContent2" ), session );

        // verify
        assertNotNull( actualContents );
        assertEquals( 2, actualContents.getSize() );
        assertEquals( "myspace:/myContent", actualContents.first().getPath().toString() );
        assertEquals( "myspace:/myContent2", actualContents.last().getPath().toString() );

        DataSet contentData1 = actualContents.first().getContentData();
        assertEquals( "myValue", contentData1.getProperty( DataPath.from( "myData" ) ).getString() );
        assertEquals( "myOtherValue", contentData1.getProperty( DataPath.from( "mySet.myData" ) ).getString() );

        DataSet contentData2 = actualContents.last().getContentData();
        assertEquals( "myValue2", contentData2.getProperty( DataPath.from( "myData" ) ).getString() );
        assertEquals( "myOtherValue2", contentData2.getProperty( DataPath.from( "mySet.myData" ) ).getString() );
    }

    @Test
    public void findMultipleContentsById()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        Content content = newContent().path( ContentPath.from( "myspace:myContent" ) ).build();
        content.getContentData().setProperty( "myData", new Value.Text( "myValue" ) );
        content.getContentData().setProperty( "mySet.myData", new Value.Text( "myOtherValue" ) );
        ContentId contentId1 = contentDao.create( content, session );

        Content content2 = newContent().path( ContentPath.from( "myspace:myContent2" ) ).build();
        content2.getContentData().setProperty( "myData", new Value.Text( "myValue2" ) );
        content2.getContentData().setProperty( "mySet.myData", new Value.Text( "myOtherValue2" ) );
        ContentId contentId2 = contentDao.create( content2, session );
        commit();

        // exercise
        Contents actualContents = contentDao.select( ContentIds.from( contentId1, contentId2 ), session );

        // verify
        assertNotNull( actualContents );
        assertEquals( 2, actualContents.getSize() );
        assertEquals( "myspace:/myContent", actualContents.first().getPath().toString() );
        assertEquals( "myspace:/myContent2", actualContents.last().getPath().toString() );

        DataSet contentData1 = actualContents.first().getContentData();
        assertEquals( "myValue", contentData1.getProperty( DataPath.from( "myData" ) ).getString() );
        assertEquals( "myOtherValue", contentData1.getProperty( DataPath.from( "mySet.myData" ) ).getString() );

        DataSet contentData2 = actualContents.last().getContentData();
        assertEquals( "myValue2", contentData2.getProperty( DataPath.from( "myData" ) ).getString() );
        assertEquals( "myOtherValue2", contentData2.getProperty( DataPath.from( "mySet.myData" ) ).getString() );
    }

    @Test
    public void findContentById()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        Content content = newContent().path( ContentPath.from( "myspace:myContent" ) ).build();
        content.getContentData().setProperty( "myData", new Value.Text( "myValue" ) );
        content.getContentData().setProperty( "mySet.myData", new Value.Text( "myOtherValue" ) );
        ContentId contentId = contentDao.create( content, session );
        commit();

        // exercise
        Content actualContent = contentDao.select( contentId, session );

        // verify
        assertNotNull( actualContent );
        assertEquals( "myspace:/myContent", content.getPath().toString() );

        DataSet contentData = actualContent.getContentData();
        assertEquals( "myValue", contentData.getProperty( DataPath.from( "myData" ) ).getString() );
        assertEquals( "myOtherValue", contentData.getProperty( DataPath.from( "mySet.myData" ) ).getString() );
    }

    @Test
    public void findChildContent()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        contentDao.create( createContent( "myspace:myParentContent" ), session );
        contentDao.create( createContent( "myspace:myParentContent2" ), session );
        contentDao.create( createContent( "myspace:myParentContent/myChildContent1" ), session );
        contentDao.create( createContent( "myspace:myParentContent/myChildContent2" ), session );
        commit();

        // exercise
        Contents childContent = contentDao.findChildContent( ContentPath.from( "myspace:myParentContent" ), session );

        // verify
        assertTrue( childContent.isNotEmpty() );
        assertEquals( ContentPath.from( "myspace:myParentContent/myChildContent2" ), childContent.getList().get( 0 ).getPath() );
        assertEquals( ContentPath.from( "myspace:myParentContent/myChildContent1" ), childContent.getList().get( 1 ).getPath() );
    }

    @Test
    public void getContentTree_given_persisted_tree_of_nine_content_then_a_tree_of_size_9_is_returned()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        contentDao.create( createContent( "myspace:branch-A" ), session );
        contentDao.create( createContent( "myspace:branch-A/branch-A-A" ), session );
        contentDao.create( createContent( "myspace:branch-B" ), session );
        contentDao.create( createContent( "myspace:branch-B/branch-B-A" ), session );
        contentDao.create( createContent( "myspace:branch-B/branch-B-A/branch-B-A-A" ), session );
        contentDao.create( createContent( "myspace:branch-B/branch-B-A/branch-B-A-B" ), session );
        contentDao.create( createContent( "myspace:branch-B/branch-B-A/branch-B-A-C" ), session );
        contentDao.create( createContent( "myspace:branch-B/branch-B-B" ), session );
        contentDao.create( createContent( "myspace:branch-B/branch-B-B/branch-B-B-A" ), session );
        commit();

        // exercise
        Tree<Content> tree = contentDao.getContentTree( session );

        // verify
        assertEquals( 10, tree.deepSize() );
    }

    @Test
    public void renameContent()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        Content content = newContent().path( ContentPath.from( "myspace:myContent" ) ).build();
        content.getContentData().setProperty( "myData", new Value.Text( "myValue" ) );
        content.getContentData().setProperty( "mySet.myData", new Value.Text( "myOtherValue" ) );
        ContentId contentId = contentDao.create( content, session );
        commit();

        // exercise
        contentDao.renameContent( contentId, "newContentName", session );
        commit();

        // verify
        Content storedContent = contentDao.select( contentId, session );
        assertNotNull( storedContent );
        assertEquals( ContentPath.from( "myspace:newContentName" ), storedContent.getPath() );

        Content contentNotFound = contentDao.select( ContentPath.from( "myspace:myContent" ), session );
        assertNull( contentNotFound );
    }

    @Test(expected = ContentAlreadyExistException.class)
    public void renameContent_to_existing_path()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        contentDao.create( newContent().path( ContentPath.from( "myspace:myExistingContent" ) ).build(), session );

        ContentId contentId = contentDao.create( newContent().path( ContentPath.from( "myspace:myContent" ) ).build(), session );
        commit();

        // exercise
        contentDao.renameContent( contentId, "myExistingContent", session );
        commit();
    }

    @Test
    public void moveContent()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        contentDao.create( createContent( "myspace:/parentA" ), session );
        ContentId contentToMove = contentDao.create( createContent( "myspace:parentA/contentToMove" ), session );
        contentDao.create( createContent( "myspace:/parentB" ), session );
        commit();

        // exercise
        contentDao.moveContent( contentToMove, ContentPath.from( "myspace:/parentB/contentToMove" ), session );
        commit();

        // verify
        Content storedContent = contentDao.select( contentToMove, session );
        assertNotNull( storedContent );
        assertEquals( ContentPath.from( "myspace:/parentB/contentToMove" ), storedContent.getPath() );

        Content contentNotFound = contentDao.select( ContentPath.from( "myspace:/parentA/contentToMove" ), session );
        assertNull( contentNotFound );
    }

    private Content createContent( String path )
    {
        return newContent().path( ContentPath.from( path ) ).build();
    }

}
