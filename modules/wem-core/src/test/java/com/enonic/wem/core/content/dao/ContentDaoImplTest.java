package com.enonic.wem.core.content.dao;

import javax.jcr.Node;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentAlreadyExistException;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.UnableToDeleteContentException;
import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Value;
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
        contentDao = new ContentDaoImpl();
        indexService = Mockito.mock( IndexService.class );

        ( (ContentDaoImpl) contentDao ).setIndexService( indexService );
    }

    @Test(expected = ContentAlreadyExistException.class)
    public void createRootContent()
        throws Exception
    {
        // setup
        Content content = newContent().path( ContentPath.from( "/" ) ).build();
        content.getContentData().setProperty( "myData", new Value.String( "myValue" ) );

        // exercise
        contentDao.create( content, session );
        commit();

        // verify
        Node contentNode = session.getNode( "/" + ContentDao.CONTENTS_ROOT_PATH + "myspace/root" );
        assertNotNull( contentNode );
    }

    @Test
    public void deleteRootContent()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "/parentContent" ), session );
        contentDao.create( createContent( "/parentContent/contentToDelete" ), session );
        commit();

        // exercise
        try
        {
            contentDao.deleteByPath( ContentPath.from( "/" ), session );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UnableToDeleteContentException );
            assertEquals( "Not able to delete content with path [/]: Root content.", e.getMessage() );
        }
    }

    @Test
    public void createContent_one_data_at_root()
        throws Exception
    {
        // setup
        Content content = newContent().path( ContentPath.from( "/myContent" ) ).build();
        content.getContentData().setProperty( "myData", new Value.String( "myValue" ) );

        // exercise
        contentDao.create( content, session );
        commit();

        // verify
        Node contentNode = session.getNode( "/" + ContentDao.CONTENTS_ROOT_PATH + "myContent" );
        assertNotNull( contentNode );
    }

    @Test
    public void createContent_one_data_at_root_and_one_below()
        throws Exception
    {
        // setup
        Content rootContent = newContent().path( ContentPath.from( "/rootContent" ) ).build();
        rootContent.getContentData().setProperty( "myData", new Value.String( "myValue" ) );

        Content belowRootContent = newContent().path( ContentPath.from( "/rootContent/belowRootContent" ) ).build();
        belowRootContent.getContentData().setProperty( "myData", new Value.String( "myValue" ) );

        // exercise
        contentDao.create( rootContent, session );
        contentDao.create( belowRootContent, session );
        commit();

        // verify
        Node rootContentNode = session.getNode( "/" + ContentDao.CONTENTS_ROOT_PATH + "rootContent" );
        assertNotNull( rootContentNode );

        Node belowRootContentNode = session.getNode( "/" + ContentDao.CONTENTS_ROOT_PATH + "rootContent/belowRootContent" );
        assertNotNull( belowRootContentNode );
    }

    @Test
    public void createContent_one_data_at_root_and_one_in_a_set()
        throws Exception
    {
        // setup
        Content content = newContent().path( ContentPath.from( "/myContent" ) ).build();
        content.getContentData().setProperty( "myData", new Value.String( "1" ) );
        content.getContentData().setProperty( "mySet.myData", new Value.String( "2" ) );
        content.getContentData().setProperty( "mySet.myOtherData", new Value.String( "3" ) );

        // exercise
        contentDao.create( content, session );
        commit();

        // verify
        assertNotNull( session.getNode( "/" + ContentDao.CONTENTS_ROOT_PATH + "myContent" ) );

        Content storedContent = contentDao.selectByPath( ContentPath.from( "/myContent" ), session );

        assertEquals( "1", storedContent.getContentData().getProperty( "myData" ).getString() );
        assertEquals( "2", storedContent.getContentData().getProperty( "mySet.myData" ).getString() );
        assertEquals( "3", storedContent.getContentData().getProperty( "mySet.myOtherData" ).getString() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_content_with_assigned_id_throws_exception()
        throws Exception
    {
        // setup
        Content content = createContent( "/someContent" );
        Content storedContent = contentDao.create( content, session );

        Content anotherContent = newContent( content ).id( storedContent.getId() ).path( ContentPath.from( "/anotherContent" ) ).build();
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
        Content newContent = newContent().path( ContentPath.from( "/myContent" ) ).build();
        newContent.getContentData().setProperty( "myData", new Value.String( "initial value" ) );

        // setup: create content to update
        contentDao.create( newContent, session );
        commit();

        Content updateContent = newContent().path( ContentPath.from( "/myContent" ) ).build();
        updateContent.getContentData().setProperty( "myData", new Value.String( "changed value" ) );

        // exercise
        contentDao.update( updateContent, true, session );

        // verify
        assertNotNull( session.getNode( "/" + ContentDao.CONTENTS_ROOT_PATH + "myContent" ) );

        Content storedContent = contentDao.selectByPath( ContentPath.from( "/myContent" ), session );
        assertEquals( "changed value", storedContent.getContentData().getProperty( "myData" ).getString() );
    }

    @Test
    public void delete_content_at_root()
        throws Exception
    {
        // setup
        Content content = newContent().path( ContentPath.from( "/myContent" ) ).build();
        content.getContentData().setProperty( "myData", new Value.String( "myValue" ) );

        contentDao.create( content, session );
        commit();

        // exercise
        contentDao.deleteByPath( ContentPath.from( "/myContent" ), session );
        commit();

        // verify
        Node contentsNode = session.getNode( "/" + ContentDao.CONTENTS_ROOT_PATH  );
        assertFalse( contentsNode.hasNode( "myContent" ) );
    }

    @Test
    public void delete_content_below_other_content()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "/parentContent" ), session );
        contentDao.create( createContent( "/parentContent/contentToDelete" ), session );
        commit();

        // exercise
        contentDao.deleteByPath( ContentPath.from( "/parentContent/contentToDelete" ), session );
        commit();

        // verify
        Node parentContentNode = session.getNode( "/" + ContentDao.CONTENTS_ROOT_PATH + "parentContent" );
        assertFalse( parentContentNode.hasNode( "contentToDelete" ) );
    }

    @Test
    public void delete_content_which_have_subcontent_throws_exception()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "/parentContent" ), session );
        contentDao.create( createContent( "/parentContent/contentToDelete" ), session );
        commit();

        // exercise
        try
        {
            contentDao.deleteByPath( ContentPath.from( "/parentContent" ), session );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UnableToDeleteContentException );
            assertEquals( "Not able to delete content with path [/parentContent]: Content has child content.", e.getMessage() );
        }
    }

    @Test
    public void delete_content_by_id()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "/parentContent" ), session );
        Content storedContent = contentDao.create( createContent( "/parentContent/contentToDelete" ), session );
        commit();

        // exercise
        contentDao.deleteById( storedContent.getId(), session );
        commit();

        // verify
        Node parentContentNode = session.getNode( "/" + ContentDao.CONTENTS_ROOT_PATH + "parentContent" );
        assertFalse( parentContentNode.hasNode( "contentToDelete" ) );
    }

    @Test
    public void selectContentByPath()
        throws Exception
    {
        // setup
        Content content = newContent().path( ContentPath.from( "/myContent" ) ).build();
        content.getContentData().setProperty( "myData", new Value.String( "myValue" ) );
        content.getContentData().setProperty( "mySet.myData", new Value.String( "myOtherValue" ) );
        contentDao.create( content, session );
        commit();

        // exercise
        Content actualContent = contentDao.selectByPath( ContentPath.from( "/myContent" ), session );

        // verify
        assertNotNull( actualContent );
        assertEquals( "/myContent", content.getPath().toString() );

        DataSet contentData = actualContent.getContentData();
        assertEquals( "myValue", contentData.getProperty( DataPath.from( "myData" ) ).getString() );
        assertEquals( "myOtherValue", contentData.getProperty( DataPath.from( "mySet.myData" ) ).getString() );
    }

    @Test
    public void selectMultipleContentsByPath()
        throws Exception
    {
        // setup
        Content content = newContent().path( ContentPath.from( "/myContent" ) ).build();
        content.getContentData().setProperty( "myData", new Value.String( "myValue" ) );
        content.getContentData().setProperty( "mySet.myData", new Value.String( "myOtherValue" ) );
        contentDao.create( content, session );

        Content content2 = newContent().path( ContentPath.from( "/myContent2" ) ).build();
        content2.getContentData().setProperty( "myData", new Value.String( "myValue2" ) );
        content2.getContentData().setProperty( "mySet.myData", new Value.String( "myOtherValue2" ) );
        contentDao.create( content2, session );
        commit();

        // exercise
        Contents actualContents = contentDao.selectByPaths( ContentPaths.from( "/myContent", "/myContent2" ), session );

        // verify
        assertNotNull( actualContents );
        assertEquals( 2, actualContents.getSize() );
        assertEquals( "/myContent", actualContents.first().getPath().toString() );
        assertEquals( "/myContent2", actualContents.last().getPath().toString() );

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
        Content content = newContent().path( ContentPath.from( "/myContent" ) ).build();
        content.getContentData().setProperty( "myData", new Value.String( "myValue" ) );
        content.getContentData().setProperty( "mySet.myData", new Value.String( "myOtherValue" ) );
        Content storedContent = contentDao.create( content, session );

        Content content2 = newContent().path( ContentPath.from( "/myContent2" ) ).build();
        content2.getContentData().setProperty( "myData", new Value.String( "myValue2" ) );
        content2.getContentData().setProperty( "mySet.myData", new Value.String( "myOtherValue2" ) );
        Content storedContent2 = contentDao.create( content2, session );
        commit();

        // exercise
        Contents actualContents = contentDao.selectByIds( ContentIds.from( storedContent.getId(), storedContent2.getId() ), session );

        // verify
        assertNotNull( actualContents );
        assertEquals( 2, actualContents.getSize() );
        assertEquals( "/myContent", actualContents.first().getPath().toString() );
        assertEquals( "/myContent2", actualContents.last().getPath().toString() );

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
        Content content = newContent().path( ContentPath.from( "/myContent" ) ).build();
        content.getContentData().setProperty( "myData", new Value.String( "myValue" ) );
        content.getContentData().setProperty( "mySet.myData", new Value.String( "myOtherValue" ) );
        Content storedContent = contentDao.create( content, session );
        commit();

        // exercise
        Content actualContent = contentDao.selectById( storedContent.getId(), session );

        // verify
        assertNotNull( actualContent );
        assertEquals( "/myContent", content.getPath().toString() );

        DataSet contentData = actualContent.getContentData();
        assertEquals( "myValue", contentData.getProperty( DataPath.from( "myData" ) ).getString() );
        assertEquals( "myOtherValue", contentData.getProperty( DataPath.from( "mySet.myData" ) ).getString() );
    }

    @Test
    public void select_content_and_check_that_hasChildren_method_returns_correct_results()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "/parentWithoutChildren" ), session );
        contentDao.create( createContent( "/parentWithChild" ), session );
        contentDao.create( createContent( "/parentWithChild/child" ), session );
        commit();

        // execute
        Content contentWithoutChildren = contentDao.selectByPath( ContentPath.from( "/parentWithoutChildren" ), session );
        Content contentWithChild = contentDao.selectByPath( ContentPath.from( "/parentWithChild" ), session );

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
        contentDao.create( createContent( "/myParentContent" ), session );
        contentDao.create( createContent( "/myParentContent2" ), session );
        contentDao.create( createContent( "/myParentContent/myChildContent1" ), session );
        contentDao.create( createContent( "/myParentContent/myChildContent2" ), session );
        commit();

        // exercise
        Contents childContent = contentDao.findChildContent( ContentPath.from( "/myParentContent" ), session );

        // verify
        assertTrue( childContent.isNotEmpty() );
        assertEquals( ContentPath.from( "/myParentContent/myChildContent2" ), childContent.getList().get( 0 ).getPath() );
        assertEquals( ContentPath.from( "/myParentContent/myChildContent1" ), childContent.getList().get( 1 ).getPath() );
    }

    @Test
    public void renameContent()
        throws Exception
    {
        // setup
        Content content = newContent().path( ContentPath.from( "/myContent" ) ).build();
        content.getContentData().setProperty( "myData", new Value.String( "myValue" ) );
        content.getContentData().setProperty( "mySet.myData", new Value.String( "myOtherValue" ) );
        Content storedContent = contentDao.create( content, session );
        commit();

        // exercise
        contentDao.renameContent( storedContent.getId(), "newContentName", session );
        commit();

        // verify
        storedContent = contentDao.selectById( storedContent.getId(), session );
        assertNotNull( storedContent );
        assertEquals( ContentPath.from( "/newContentName" ), storedContent.getPath() );

        Content contentNotFound = contentDao.selectByPath( ContentPath.from( "/myContent" ), session );
        assertNull( contentNotFound );
    }

    @Test(expected = ContentAlreadyExistException.class)
    public void renameContent_to_existing_path()
        throws Exception
    {
        // setup
        contentDao.create( newContent().path( ContentPath.from( "/myExistingContent" ) ).build(), session );

        Content storedContent = contentDao.create( newContent().path( ContentPath.from( "/myContent" ) ).build(), session );
        commit();

        // exercise
        contentDao.renameContent( storedContent.getId(), "myExistingContent", session );
        commit();
    }

    @Test
    public void moveContent()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "/parentA" ), session );
        Content contentToMove = contentDao.create( createContent( "/parentA/contentToMove" ), session );
        contentDao.create( createContent( "/parentB" ), session );
        commit();

        // exercise
        contentDao.moveContent( contentToMove.getId(), ContentPath.from( "/parentB/contentToMove" ), session );
        commit();

        // verify
        Content storedContent = contentDao.selectById( contentToMove.getId(), session );
        assertNotNull( storedContent );
        assertEquals( ContentPath.from( "/parentB/contentToMove" ), storedContent.getPath() );

        Content contentNotFound = contentDao.selectByPath( ContentPath.from( "/parentA/contentToMove" ), session );
        assertNull( contentNotFound );
    }

    private Content createContent( String path )
    {
        return newContent().path( ContentPath.from( path ) ).build();
    }

}
