package com.enonic.wem.core.content.dao;


import javax.jcr.Node;

import org.junit.Test;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.exception.SpaceNotFoundException;
import com.enonic.wem.api.exception.UnableToDeleteContentException;
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
        content.setData( "myData", "myValue" );

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
        content.setData( "myData", "myValue" );

        // exercise
        contentDao.create( content, session );
        commit();
    }

    @Test
    public void createContent_one_data_at_root()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );

        Content content = newContent().path( ContentPath.from( "myspace:myContent" ) ).build();
        content.setData( "myData", "myValue" );

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
        rootContent.setData( "myData", "myValue" );

        Content belowRootContent = newContent().path( ContentPath.from( "myspace:rootContent/belowRootContent" ) ).build();
        belowRootContent.setData( "myData", "myValue" );

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
        content.setData( "myData", "1" );
        content.setData( "mySet.myData", "2" );
        content.setData( "mySet.myOtherData", "3" );

        // exercise
        contentDao.create( content, session );
        commit();

        // verify
        assertNotNull( session.getNode( "/" + ContentDao.SPACES_PATH + "myspace/root/myContent" ) );

        Content storedContent = contentDao.select( ContentPath.from( "myspace:myContent" ), session );

        assertEquals( "1", storedContent.getData( "myData" ).asString() );
        assertEquals( "2", storedContent.getData( "mySet.myData" ).asString() );
        assertEquals( "3", storedContent.getData( "mySet.myOtherData" ).asString() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_content_with_assigned_id_throws_exception()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );

        final Content content = createContent( "myspace:someContent" );
        final ContentId createdContentId = contentDao.create( content, session );

        final Content anotherContent =
            newContent( content ).id( createdContentId ).path( ContentPath.from( "myspace:anotherContent" ) ).build();
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
        newContent.setData( "myData", "initial value" );

        // setup: create content to update
        contentDao.create( newContent, session );
        commit();

        Content updateContent = newContent().path( ContentPath.from( "myspace:myContent" ) ).build();
        updateContent.setData( "myData", "changed value" );

        // exercise
        contentDao.update( updateContent, true, session );

        // verify
        assertNotNull( session.getNode( "/" + ContentDao.SPACES_PATH + "myspace/root/myContent" ) );

        Content storedContent = contentDao.select( ContentPath.from( "myspace:myContent" ), session );
        assertEquals( "changed value", storedContent.getData( "myData" ).asString() );
    }

    @Test
    public void delete_content_at_root()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );

        Content content = newContent().path( ContentPath.from( "myspace:myContent" ) ).build();
        content.setData( "myData", "myValue" );

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
            assertEquals( "Not able to delete content with path [myspace:parentContent]: Content has child content.", e.getMessage() );
        }
    }

    @Test
    public void delete_content_by_id()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        contentDao.create( createContent( "myspace:parentContent" ), session );
        final ContentId contentId = contentDao.create( createContent( "myspace:parentContent/contentToDelete" ), session );
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
        content.setData( "myData", "myValue" );
        content.setData( "mySet.myData", "myOtherValue" );
        contentDao.create( content, session );
        commit();

        // exercise
        Content actualContent = contentDao.select( ContentPath.from( "myspace:myContent" ), session );

        // verify
        assertNotNull( actualContent );
        assertEquals( "myspace:myContent", content.getPath().toString() );

        ContentData contentData = actualContent.getData();
        assertEquals( "myValue", contentData.getData( EntryPath.from( "myData" ) ).asString() );
        assertEquals( "myOtherValue", contentData.getData( EntryPath.from( "mySet.myData" ) ).asString() );
    }

    @Test
    public void findMultipleContentsByPath()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        final Content content = newContent().path( ContentPath.from( "myspace:myContent" ) ).build();
        content.setData( "myData", "myValue" );
        content.setData( "mySet.myData", "myOtherValue" );
        contentDao.create( content, session );

        final Content content2 = newContent().path( ContentPath.from( "myspace:myContent2" ) ).build();
        content2.setData( "myData", "myValue2" );
        content2.setData( "mySet.myData", "myOtherValue2" );
        contentDao.create( content2, session );
        commit();

        // exercise
        final Contents actualContents = contentDao.select( ContentPaths.from( "myspace:myContent", "myspace:myContent2" ), session );

        // verify
        assertNotNull( actualContents );
        assertEquals( 2, actualContents.getSize() );
        assertEquals( "myspace:myContent", actualContents.first().getPath().toString() );
        assertEquals( "myspace:myContent2", actualContents.last().getPath().toString() );

        final ContentData contentData1 = actualContents.first().getData();
        assertEquals( "myValue", contentData1.getData( EntryPath.from( "myData" ) ).asString() );
        assertEquals( "myOtherValue", contentData1.getData( EntryPath.from( "mySet.myData" ) ).asString() );

        final ContentData contentData2 = actualContents.last().getData();
        assertEquals( "myValue2", contentData2.getData( EntryPath.from( "myData" ) ).asString() );
        assertEquals( "myOtherValue2", contentData2.getData( EntryPath.from( "mySet.myData" ) ).asString() );
    }

    @Test
    public void findMultipleContentsById()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        final Content content = newContent().path( ContentPath.from( "myspace:myContent" ) ).build();
        content.setData( "myData", "myValue" );
        content.setData( "mySet.myData", "myOtherValue" );
        final ContentId contentId1 = contentDao.create( content, session );

        final Content content2 = newContent().path( ContentPath.from( "myspace:myContent2" ) ).build();
        content2.setData( "myData", "myValue2" );
        content2.setData( "mySet.myData", "myOtherValue2" );
        final ContentId contentId2 = contentDao.create( content2, session );
        commit();

        // exercise
        final Contents actualContents = contentDao.select( ContentIds.from( contentId1, contentId2 ), session );

        // verify
        assertNotNull( actualContents );
        assertEquals( 2, actualContents.getSize() );
        assertEquals( "myspace:myContent", actualContents.first().getPath().toString() );
        assertEquals( "myspace:myContent2", actualContents.last().getPath().toString() );

        final ContentData contentData1 = actualContents.first().getData();
        assertEquals( "myValue", contentData1.getData( EntryPath.from( "myData" ) ).asString() );
        assertEquals( "myOtherValue", contentData1.getData( EntryPath.from( "mySet.myData" ) ).asString() );

        final ContentData contentData2 = actualContents.last().getData();
        assertEquals( "myValue2", contentData2.getData( EntryPath.from( "myData" ) ).asString() );
        assertEquals( "myOtherValue2", contentData2.getData( EntryPath.from( "mySet.myData" ) ).asString() );
    }

    @Test
    public void findContentById()
        throws Exception
    {
        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        Content content = newContent().path( ContentPath.from( "myspace:myContent" ) ).build();
        content.setData( "myData", "myValue" );
        content.setData( "mySet.myData", "myOtherValue" );
        final ContentId contentId = contentDao.create( content, session );
        commit();

        // exercise
        Content actualContent = contentDao.select( contentId, session );

        // verify
        assertNotNull( actualContent );
        assertEquals( "myspace:myContent", content.getPath().toString() );

        ContentData contentData = actualContent.getData();
        assertEquals( "myValue", contentData.getData( EntryPath.from( "myData" ) ).asString() );
        assertEquals( "myOtherValue", contentData.getData( EntryPath.from( "mySet.myData" ) ).asString() );
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
        final Content content = newContent().path( ContentPath.from( "myspace:myContent" ) ).build();
        content.setData( "myData", "myValue" );
        content.setData( "mySet.myData", "myOtherValue" );
        final ContentId contentId = contentDao.create( content, session );

        // exercise
        contentDao.renameContent( ContentPath.from( "myspace:myContent" ), "newContentName", session );
        commit();

        // verify
        final Content storedContent = contentDao.select( contentId, session );
        assertNotNull( storedContent );
        assertEquals( ContentPath.from( "myspace:newContentName" ), storedContent.getPath() );

        final Content contentNotFound = contentDao.select( ContentPath.from( "myspace:myContent" ), session );
        assertNull( contentNotFound );
    }

    private Content createContent( String path )
    {
        return newContent().path( ContentPath.from( path ) ).build();
    }

}
