package com.enonic.wem.core.content.dao;

import org.junit.Test;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentSelectors;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.core.AbstractJcrTest;

import static com.enonic.wem.api.content.Content.newContent;
import static org.junit.Assert.*;


public class ContentDaoHandlerGetContentTreeTest
    extends AbstractJcrTest
{

    private ContentDao contentDao;

    @Override
    protected void setupDao()
        throws Exception
    {
        session.getNode( "/wem/spaces" ).addNode( "myspace" );
        contentDao = new ContentDaoImpl();
    }

    @Test
    public void testGetContentTree()
        throws Exception
    {

        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        contentDao.create( createContent( "myspace:parentContent" ), session );
        contentDao.create( createContent( "myspace:parentContent/contentToDelete" ), session );
        commit();

        // exercise
        final Tree<Content> contentTree = contentDao.getContentTree( session );
        commit();

        assertNotNull( contentTree );


    }


    @Test
    public void testGetContentTree_select_topnodes()
        throws Exception
    {

        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        final ContentId aa = contentDao.create( createContent( "myspace:contentroot1" ), session );
        contentDao.create( createContent( "myspace:contentroot1/contentroot1child1" ), session );
        final ContentId ab = contentDao.create( createContent( "myspace:contentroot2" ), session );
        contentDao.create( createContent( "myspace:contentroot2/contentroot2child1" ), session );
        contentDao.create( createContent( "myspace:contentroot3" ), session );
        contentDao.create( createContent( "myspace:contentroot3/contentroot3child1" ), session );

        commit();

        ContentSelectors<ContentId> selectors = ContentIds.from( aa, ab );

        // exercise
        final Tree<Content> contentTree = contentDao.getContentTree( session, selectors );
        commit();

        // verify
        assertNotNull( contentTree );
        assertEquals( 2, contentTree.size() );
        assertEquals( 4, contentTree.deepSize() );
    }


    @Test
    public void testGetContentTree_select_topnodes_parent_and_child()
        throws Exception
    {

        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        final ContentId aa = contentDao.create( createContent( "myspace:contentroot1" ), session );
        final ContentId aaa = contentDao.create( createContent( "myspace:contentroot1/contentroot1child1" ), session );
        final ContentId ab = contentDao.create( createContent( "myspace:contentroot2" ), session );
        final ContentId aba = contentDao.create( createContent( "myspace:contentroot2/contentroot2child1" ), session );

        commit();

        ContentSelectors<ContentId> selectors = ContentIds.from( aa, aaa, ab, aba );

        // exercise
        final Tree<Content> contentTree = contentDao.getContentTree( session, selectors );
        commit();

        // verify
        assertNotNull( contentTree );
        assertEquals( 4, contentTree.size() );
        assertEquals( 6, contentTree.deepSize() );
    }


    @Test
    public void testGetContentTree_empty_topnode_set()
        throws Exception
    {

        // setup
        contentDao.create( createContent( "myspace:/" ), session );
        contentDao.create( createContent( "myspace:contentroot1" ), session );
        contentDao.create( createContent( "myspace:contentroot1/contentroot1child1" ), session );
        contentDao.create( createContent( "myspace:contentroot2" ), session );
        contentDao.create( createContent( "myspace:contentroot2/contentroot2child1" ), session );

        commit();

        ContentSelectors<ContentId> selectors = ContentIds.empty();

        // exercise
        final Tree<Content> contentTree = contentDao.getContentTree( session, selectors );
        commit();

        // verify
        assertNotNull( contentTree );
        assertEquals( 0, contentTree.size() );
    }

     /*
        final ContentId a = contentDao.create( createContent( "myspace:/" ), session );
        final ContentId aa = contentDao.create( createContent( "myspace:contentroot1" ), session );
        final ContentId aaa = contentDao.create( createContent( "myspace:contentroot1/contentroot1child1" ), session );
        final ContentId ab = contentDao.create( createContent( "myspace:contentroot2" ), session );
        final ContentId aba = contentDao.create( createContent( "myspace:contentroot2/contentroot2child1" ), session );
        final ContentId ac = contentDao.create( createContent( "myspace:contentroot3" ), session );
        final ContentId aca = contentDao.create( createContent( "myspace:contentroot3/contentroot3child1" ), session );
        final ContentId ad = contentDao.create( createContent( "myspace:contentroot4" ), session );
        final ContentId ada = contentDao.create( createContent( "myspace:contentroot4/contentroot4child1" ), session );
        final ContentId adb = contentDao.create( createContent( "myspace:contentroot4/contentroot4child2" ), session );
      */


    private Content createContent( final String path )
    {
        return newContent().path( ContentPath.from( path ) ).build();
    }
}
