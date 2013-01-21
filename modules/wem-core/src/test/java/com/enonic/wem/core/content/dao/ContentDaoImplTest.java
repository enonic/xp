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
        contentDao = new ContentDaoImpl();
    }

    @Test
    public void createContent_one_data_at_root()
        throws Exception
    {
        // setup
        Content content = newContent().path( ContentPath.from( "myContent" ) ).build();
        content.setData( "myData", "myValue" );

        // exercise
        contentDao.createContent( content, session );
        commit();

        // verify
        Node contentNode = session.getNode( "/" + ContentDao.CONTENTS_PATH + "myContent" );
        assertNotNull( contentNode );
    }

    @Test
    public void createContent_one_data_at_root_and_one_below()
        throws Exception
    {
        // setup
        Content rootContent = newContent().path( ContentPath.from( "rootContent" ) ).build();
        rootContent.setData( "myData", "myValue" );

        Content belowRootContent = newContent().path( ContentPath.from( "rootContent/belowRootContent" ) ).build();
        belowRootContent.setData( "myData", "myValue" );

        // exercise
        contentDao.createContent( rootContent, session );
        commit();
        contentDao.createContent( belowRootContent, session );
        commit();

        // verify
        Node rootContentNode = session.getNode( "/" + ContentDao.CONTENTS_PATH + "rootContent" );
        assertNotNull( rootContentNode );

        Node belowRootContentNode = session.getNode( "/" + ContentDao.CONTENTS_PATH + "rootContent/belowRootContent" );
        assertNotNull( belowRootContentNode );
    }

    @Test
    public void createContent_one_data_at_root_and_one_in_a_set()
        throws Exception
    {
        // setup
        Content content = newContent().path( ContentPath.from( "myContent" ) ).build();
        content.setData( "myData", "1" );
        content.setData( "mySet.myData", "2" );
        content.setData( "mySet.myOtherData", "3" );

        // exercise
        contentDao.createContent( content, session );
        commit();

        // verify
        assertNotNull( session.getNode( "/" + ContentDao.CONTENTS_PATH + "myContent" ) );

        Content storedContent = contentDao.findContent( ContentPath.from( "myContent" ), session );

        assertEquals( "1", storedContent.getData( "myData" ).asString() );
        assertEquals( "2", storedContent.getData( "mySet.myData" ).asString() );
        assertEquals( "3", storedContent.getData( "mySet.myOtherData" ).asString() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_content_with_assigned_id_throws_exception()
        throws Exception
    {
        // setup
        final Content content = createContent( "someContent" );
        final ContentId createdContentId = contentDao.createContent( content, session );

        final Content anotherContent = newContent( content ).id( createdContentId ).path( ContentPath.from( "anotherContent" ) ).build();
        assertNotNull( anotherContent.getId() );
        contentDao.createContent( anotherContent, session );

        // exercise
        commit();
    }

    @Test
    public void updateContent_one_data_at_root()
        throws Exception
    {
        // setup
        Content newContent = newContent().path( ContentPath.from( "myContent" ) ).build();
        newContent.setData( "myData", "initial value" );

        // setup: create content to update
        contentDao.createContent( newContent, session );
        commit();

        Content updateContent = newContent().path( ContentPath.from( "myContent" ) ).build();
        updateContent.setData( "myData", "changed value" );

        // exercise
        contentDao.updateContent( updateContent, true, session );

        // verify
        assertNotNull( session.getNode( "/" + ContentDao.CONTENTS_PATH + "myContent" ) );

        Content storedContent = contentDao.findContent( ContentPath.from( "myContent" ), session );
        assertEquals( "changed value", storedContent.getData( "myData" ).asString() );
    }

    @Test
    public void delete_content_at_root()
        throws Exception
    {
        // setup
        Content content = newContent().path( ContentPath.from( "myContent" ) ).build();
        content.setData( "myData", "myValue" );

        contentDao.createContent( content, session );
        commit();

        // exercise
        contentDao.deleteContent( ContentPath.from( "myContent" ), session );
        commit();

        // verify
        Node contentsNode = session.getNode( "/" + ContentDao.CONTENTS_PATH );
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
        Node parentContentNode = session.getNode( "/" + ContentDao.CONTENTS_PATH + "parentContent" );
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
            assertTrue( e instanceof UnableToDeleteContentException );
            assertEquals( "Not able to delete content with path [parentContent]: Content has child content.", e.getMessage() );
        }
    }

    @Test
    public void delete_content_by_id()
        throws Exception
    {
        // setup
        contentDao.createContent( createContent( "parentContent" ), session );
        final ContentId contentId = contentDao.createContent( createContent( "parentContent/contentToDelete" ), session );
        commit();

        // exercise
        contentDao.deleteContent( contentId, session );
        commit();

        // verify
        Node parentContentNode = session.getNode( "/" + ContentDao.CONTENTS_PATH + "parentContent" );
        assertFalse( parentContentNode.hasNode( "contentToDelete" ) );
    }

    @Test
    public void findContentByPath()
        throws Exception
    {
        // setup
        Content content = newContent().path( ContentPath.from( "myContent" ) ).build();
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
        assertEquals( "myValue", contentData.getData( EntryPath.from( "myData" ) ).asString() );
        assertEquals( "myOtherValue", contentData.getData( EntryPath.from( "mySet.myData" ) ).asString() );
    }

    @Test
    public void findMultipleContentsByPath()
        throws Exception
    {
        // setup
        final Content content = newContent().path( ContentPath.from( "myContent" ) ).build();
        content.setData( "myData", "myValue" );
        content.setData( "mySet.myData", "myOtherValue" );
        contentDao.createContent( content, session );

        final Content content2 = newContent().path( ContentPath.from( "myContent2" ) ).build();
        content2.setData( "myData", "myValue2" );
        content2.setData( "mySet.myData", "myOtherValue2" );
        contentDao.createContent( content2, session );
        commit();

        // exercise
        final Contents actualContents = contentDao.findContents( ContentPaths.from( "myContent", "myContent2" ), session );

        // verify
        assertNotNull( actualContents );
        assertEquals( 2, actualContents.getSize() );
        assertEquals( "myContent", actualContents.first().getPath().toString() );
        assertEquals( "myContent2", actualContents.last().getPath().toString() );

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
        final Content content = newContent().path( ContentPath.from( "myContent" ) ).build();
        content.setData( "myData", "myValue" );
        content.setData( "mySet.myData", "myOtherValue" );
        final ContentId contentId1 = contentDao.createContent( content, session );

        final Content content2 = newContent().path( ContentPath.from( "myContent2" ) ).build();
        content2.setData( "myData", "myValue2" );
        content2.setData( "mySet.myData", "myOtherValue2" );
        final ContentId contentId2 = contentDao.createContent( content2, session );
        commit();

        // exercise
        final Contents actualContents = contentDao.findContents( ContentIds.from( contentId1, contentId2 ), session );

        // verify
        assertNotNull( actualContents );
        assertEquals( 2, actualContents.getSize() );
        assertEquals( "myContent", actualContents.first().getPath().toString() );
        assertEquals( "myContent2", actualContents.last().getPath().toString() );

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
        Content content = newContent().path( ContentPath.from( "myContent" ) ).build();
        content.setData( "myData", "myValue" );
        content.setData( "mySet.myData", "myOtherValue" );
        final ContentId contentId = contentDao.createContent( content, session );
        commit();

        // exercise
        Content actualContent = contentDao.findContent( contentId, session );

        // verify
        assertNotNull( actualContent );
        assertEquals( "myContent", content.getPath().toString() );

        ContentData contentData = actualContent.getData();
        assertEquals( "myValue", contentData.getData( EntryPath.from( "myData" ) ).asString() );
        assertEquals( "myOtherValue", contentData.getData( EntryPath.from( "mySet.myData" ) ).asString() );
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
    public void getContentTree_given_persisted_tree_of_nine_content_then_a_tree_of_size_9_is_returned()
        throws Exception
    {
        // setup
        contentDao.createContent( createContent( "branch-A" ), session );
        contentDao.createContent( createContent( "branch-A/branch-A-A" ), session );
        contentDao.createContent( createContent( "branch-B" ), session );
        contentDao.createContent( createContent( "branch-B/branch-B-A" ), session );
        contentDao.createContent( createContent( "branch-B/branch-B-A/branch-B-A-A" ), session );
        contentDao.createContent( createContent( "branch-B/branch-B-A/branch-B-A-B" ), session );
        contentDao.createContent( createContent( "branch-B/branch-B-A/branch-B-A-C" ), session );
        contentDao.createContent( createContent( "branch-B/branch-B-B" ), session );
        contentDao.createContent( createContent( "branch-B/branch-B-B/branch-B-B-A" ), session );
        commit();

        // exercise
        Tree<Content> tree = contentDao.getContentTree( session );

        // verify
        assertEquals( 9, tree.deepSize() );
    }

    @Test
    public void renameContent()
        throws Exception
    {
        // setup
        final Content content = newContent().path( ContentPath.from( "myContent" ) ).build();
        content.setData( "myData", "myValue" );
        content.setData( "mySet.myData", "myOtherValue" );
        final ContentId contentId = contentDao.createContent( content, session );

        // exercise
        contentDao.renameContent( ContentPath.from( "myContent" ), "newContentName", session );
        commit();

        // verify
        final Content storedContent = contentDao.findContent( contentId, session );
        assertNotNull( storedContent );
        assertEquals( ContentPath.from( "newContentName" ), storedContent.getPath() );

        final Content contentNotFound = contentDao.findContent( ContentPath.from( "myContent" ), session );
        assertNull( contentNotFound );
    }

    private Content createContent( String path )
    {
        return newContent().path( ContentPath.from( path ) ).build();
    }

}
