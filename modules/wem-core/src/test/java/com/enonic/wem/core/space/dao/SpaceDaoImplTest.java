package com.enonic.wem.core.space.dao;


import javax.jcr.Node;

import org.joda.time.DateTime;
import org.junit.Test;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.schema.type.QualifiedContentTypeName;
import com.enonic.wem.api.exception.SpaceNotFoundException;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.api.space.Spaces;
import com.enonic.wem.core.AbstractJcrTest;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.content.dao.ContentDaoImpl;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.space.Space.newSpace;
import static com.enonic.wem.core.space.dao.AbstractSpaceDaoHandler.SPACES_PATH;
import static org.junit.Assert.*;

public class SpaceDaoImplTest
    extends AbstractJcrTest
{
    private SpaceDao spaceDao;

    private ContentDao contentDao;

    private final static Icon ICON = Icon.from( "imagedata".getBytes(), "image/png" );

    public void setupDao()
        throws Exception
    {
        spaceDao = new SpaceDaoImpl();
        contentDao = new ContentDaoImpl();
    }

    @Test
    public void createSpace()
        throws Exception
    {
        // setup
        final DateTime time = DateTime.now();
        final Space space = newSpace().
            name( "mySpace" ).
            displayName( "My Space" ).
            createdTime( time ).
            modifiedTime( time ).
            icon( ICON ).
            build();

        // exercise
        spaceDao.createSpace( space, session );
        commit();

        // verify
        Node spaceNode = session.getNode( "/" + SPACES_PATH + "mySpace" );
        assertNotNull( spaceNode );
        assertNull( space.getRootContent() );
        assertEquals( time, space.getCreatedTime() );
        assertEquals( time, space.getModifiedTime() );
        assertEquals( ICON, space.getIcon() );
    }

    private ContentId createSpaceRoot( final Space space )
    {
        final Content rootContent = newContent().
            path( ContentPath.rootOf( space.getName() ) ).
            type( QualifiedContentTypeName.space() ).
            displayName( space.getDisplayName() ).
            build();
        return contentDao.create( rootContent, session );
    }

    @Test
    public void getSpace()
        throws Exception
    {
        // setup
        final DateTime time = DateTime.now();
        final Space space = newSpace().
            name( "mySpace" ).
            displayName( "My Space" ).
            createdTime( time ).
            modifiedTime( time ).
            icon( ICON ).
            build();
        final Space space2 = newSpace().
            name( "myOtherSpace" ).
            displayName( "My Other Space" ).
            modifiedTime( time ).
            createdTime( time ).
            build();
        spaceDao.createSpace( space, session );
        spaceDao.createSpace( space2, session );
        createSpaceRoot( space );
        createSpaceRoot( space2 );
        commit();

        // exercise
        final Space spaceRetrieved = spaceDao.getSpace( SpaceName.from( "mySpace" ), session );

        // verify
        assertNotNull( spaceRetrieved );
        assertEquals( "mySpace", spaceRetrieved.getName().name() );
        assertEquals( "My Space", spaceRetrieved.getDisplayName() );
        assertEqualsDateTime( time, spaceRetrieved.getCreatedTime() );
        assertEqualsDateTime( time, spaceRetrieved.getModifiedTime() );
        assertEquals( ICON, spaceRetrieved.getIcon() );
        assertNotNull( spaceRetrieved.getRootContent() );
    }

    @Test
    public void getAllSpaces()
        throws Exception
    {
        // setup
        final DateTime time = DateTime.now();
        final Space space = newSpace().
            name( "mySpace" ).
            displayName( "My Space" ).
            createdTime( time ).
            modifiedTime( time ).
            build();
        final Space space2 = newSpace().
            name( "myOtherSpace" ).
            displayName( "My Other Space" ).
            modifiedTime( time ).
            createdTime( time ).
            build();
        spaceDao.createSpace( space, session );
        spaceDao.createSpace( space2, session );
        final ContentId rootContentId = createSpaceRoot( space );
        final ContentId rootContentId2 = createSpaceRoot( space2 );
        final Space spaceCreated1 = newSpace( space ).rootContent( rootContentId ).build();
        final Space spaceCreated2 = newSpace( space2 ).rootContent( rootContentId2 ).build();
        commit();

        // exercise
        final Spaces spacesRetrieved = spaceDao.getAllSpaces( session );

        // verify
        assertNotNull( spacesRetrieved );
        assertEquals( 2, spacesRetrieved.getSize() );
        assertEquals( spaceCreated1, spacesRetrieved.getSpace( SpaceName.from( "mySpace" ) ) );
        assertEquals( spaceCreated2, spacesRetrieved.getSpace( SpaceName.from( "myOtherSpace" ) ) );
    }

    @Test
    public void deleteSpace()
        throws Exception
    {
        // setup
        final DateTime time = DateTime.now();
        final Space space = newSpace().
            name( "mySpace" ).
            displayName( "My Space" ).
            createdTime( time ).
            modifiedTime( time ).
            build();
        spaceDao.createSpace( space, session );
        createSpaceRoot( space );
        commit();
        final Space spaceCreated = spaceDao.getSpace( SpaceName.from( "mySpace" ), session );
        assertNotNull( spaceCreated );

        // exercise
        spaceDao.deleteSpace( SpaceName.from( "mySpace" ), session );

        // verify
        final Space spaceDeleted = spaceDao.getSpace( SpaceName.from( "mySpace" ), session );
        assertNull( spaceDeleted );
    }

    @Test(expected = SpaceNotFoundException.class)
    public void deleteSpace_not_existing()
        throws Exception
    {
        // exercise
        spaceDao.deleteSpace( SpaceName.from( "mySpace" ), session );
    }

    @Test
    public void updateSpace()
        throws Exception
    {
        // setup
        final DateTime time = DateTime.now();
        final Space space = newSpace().
            name( "mySpace" ).
            displayName( "My Space" ).
            createdTime( time ).
            modifiedTime( time ).
            build();
        spaceDao.createSpace( space, session );
        final ContentId rootContentId = createSpaceRoot( space );
        commit();

        // exercise
        final DateTime updateTime = time.plusHours( 3 ).plusDays( 5 );
        final Space spaceChanges = newSpace().
            name( "mySpace" ).
            displayName( "This is my Space" ).
            createdTime( time ).
            modifiedTime( updateTime ).
            icon( ICON ).
            build();
        spaceDao.updateSpace( spaceChanges, session );

        // verify
        final Space spaceUpdated = spaceDao.getSpace( SpaceName.from( "mySpace" ), session );
        assertNotNull( spaceUpdated );
        assertEquals( "mySpace", spaceUpdated.getName().name() );
        assertEquals( "This is my Space", spaceUpdated.getDisplayName() );
        assertEqualsDateTime( time, spaceUpdated.getCreatedTime() );
        assertEqualsDateTime( updateTime, spaceUpdated.getModifiedTime() );
        assertEquals( rootContentId, spaceUpdated.getRootContent() );
        assertEquals( ICON, spaceUpdated.getIcon() );
    }

    @Test(expected = SpaceNotFoundException.class)
    public void updateSpace_not_existing()
        throws Exception
    {
        // exercise
        final DateTime time = DateTime.now();
        final DateTime updateTime = time.plusHours( 3 ).plusDays( 5 );
        final Space spaceChanges = newSpace().
            name( "mySpace" ).
            displayName( "This is my Space" ).
            createdTime( time ).
            modifiedTime( updateTime ).
            build();
        spaceDao.updateSpace( spaceChanges, session );
    }

    private void assertEqualsDateTime( final DateTime expected, final DateTime actual )
    {
        // Uses DateTime.isEqual() instead of DateTime.equals() ; as equals() considers equals only when instant, timezone and chronology are equal
        // (for same instant specified in different timezone, it returns false)
        if ( !expected.isEqual( actual ) )
        {
            assertEquals( expected, actual );
        }
    }
}
