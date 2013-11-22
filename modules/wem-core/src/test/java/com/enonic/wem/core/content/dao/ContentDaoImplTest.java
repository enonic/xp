package com.enonic.wem.core.content.dao;

import javax.jcr.Node;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentAlreadyExistException;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.UnableToDeleteContentException;
import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.exception.SpaceNotFoundException;
import com.enonic.wem.core.AbstractJcrTest;
import com.enonic.wem.core.index.IndexService;

import static com.enonic.wem.api.content.Content.newContent;
import static org.junit.Assert.*;

public class ContentDaoImplTest
    extends AbstractJcrTest
{
    private ContentDao contentDao;

    private IndexService indexService;

    public void setupDao()
        throws Exception
    {
        session.getNode( "/wem/spaces" ).addNode( "myspace" );
        contentDao = new ContentDaoImpl();
        indexService = Mockito.mock( IndexService.class );

        ( (ContentDaoImpl) contentDao ).setIndexService( indexService );
    }

    @Test
    public void createRootContent()
        throws Exception
    {
        // setup
        Content content = newContent().path( ContentPath.from( "myspace:/" ) ).build();
        content.getContentData().setProperty( "myData", new Value.String( "myValue" ) );

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
        content.getContentData().setProperty( "myData", new Value.String( "myValue" ) );

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
        content.getContentData().setProperty( "myData", new Value.String( "myValue" ) );

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
        rootContent.getContentData().setProperty( "myData", new Value.String( "myValue" ) );

        Content belowRootContent = newContent().path( ContentPath.from( "myspace:rootContent/belowRootContent" ) ).build();
        belowRootContent.getContentData().setProperty( "myData", new Value.String( "myValue" ) );

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
        content.getContentData().setProperty( "myData", new Value.String( "1" ) );
        content.getContentData().setProperty( "mySet.myData", new Value.String( "2" ) );
        content.getContentData().setProperty( "mySet.myOtherData", new Value.String( "3" ) );

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
        newContent.getContentData().setProperty( "myData", new Value.String( "initial value" ) );

        // setup: create content to update
        contentDao.create( newContent, session );
        commit();

        Content updateContent = newContent().path( ContentPath.from( "myspace:myContent" ) ).build();
        updateContent.getContentData().setProperty( "myData", new Value.String( "changed value" ) );

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
        content.getContentData().setProperty( "myData", new Value.String( "myValue" ) );

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
    public void selectContentByPath()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        Content content = newContent().path( ContentPath.from( "myspace:myContent" ) ).build();
        content.getContentData().setProperty( "myData", new Value.String( "myValue" ) );
        content.getContentData().setProperty( "mySet.myData", new Value.String( "myOtherValue" ) );
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
    public void selectMultipleContentsByPath()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        Content content = newContent().path( ContentPath.from( "myspace:myContent" ) ).build();
        content.getContentData().setProperty( "myData", new Value.String( "myValue" ) );
        content.getContentData().setProperty( "mySet.myData", new Value.String( "myOtherValue" ) );
        contentDao.create( content, session );

        Content content2 = newContent().path( ContentPath.from( "myspace:myContent2" ) ).build();
        content2.getContentData().setProperty( "myData", new Value.String( "myValue2" ) );
        content2.getContentData().setProperty( "mySet.myData", new Value.String( "myOtherValue2" ) );
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
    public void selectMultipleContentsById()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        Content content = newContent().path( ContentPath.from( "myspace:myContent" ) ).build();
        content.getContentData().setProperty( "myData", new Value.String( "myValue" ) );
        content.getContentData().setProperty( "mySet.myData", new Value.String( "myOtherValue" ) );
        ContentId contentId1 = contentDao.create( content, session );

        Content content2 = newContent().path( ContentPath.from( "myspace:myContent2" ) ).build();
        content2.getContentData().setProperty( "myData", new Value.String( "myValue2" ) );
        content2.getContentData().setProperty( "mySet.myData", new Value.String( "myOtherValue2" ) );
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
    public void selectContentById()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        Content content = newContent().path( ContentPath.from( "myspace:myContent" ) ).build();
        content.getContentData().setProperty( "myData", new Value.String( "myValue" ) );
        content.getContentData().setProperty( "mySet.myData", new Value.String( "myOtherValue" ) );
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
    public void select_content_and_check_that_hasChildren_method_returns_correct_results()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        contentDao.create( createContent( "myspace:parentWithoutChildren" ), session );
        contentDao.create( createContent( "myspace:parentWithChild" ), session );
        contentDao.create( createContent( "myspace:parentWithChild/child" ), session );
        commit();

        // execute
        Content contentWithoutChildren = contentDao.select( ContentPath.from( "myspace:parentWithoutChildren" ), session );
        Content contentWithChild = contentDao.select( ContentPath.from( "myspace:parentWithChild" ), session );

        // verify
        assertNotNull( contentWithoutChildren );
        assertFalse( contentWithoutChildren.hasChildren() );
        assertNotNull( contentWithChild );
        assertTrue( contentWithChild.hasChildren() );
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
    public void renameContent()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        Content content = newContent().path( ContentPath.from( "myspace:myContent" ) ).build();
        content.getContentData().setProperty( "myData", new Value.String( "myValue" ) );
        content.getContentData().setProperty( "mySet.myData", new Value.String( "myOtherValue" ) );
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
