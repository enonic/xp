package com.enonic.wem.core.content.dao;


import javax.jcr.Node;

import org.joda.time.DateTime;
import org.junit.Test;

import com.enonic.wem.api.content.space.Space;
import com.enonic.wem.api.content.space.SpaceName;
import com.enonic.wem.api.content.space.Spaces;
import com.enonic.wem.api.exception.SpaceNotFoundException;
import com.enonic.wem.core.AbstractJcrTest;

import static com.enonic.wem.api.content.space.Space.newSpace;
import static com.enonic.wem.core.content.dao.AbstractSpaceDaoHandler.SPACES_PATH;
import static com.enonic.wem.core.content.dao.AbstractSpaceDaoHandler.SPACE_CONTENT_ROOT_NODE;
import static org.junit.Assert.*;

public class SpaceDaoImplTest
    extends AbstractJcrTest
{
    private SpaceDao spaceDao;

    public void setupDao()
        throws Exception
    {
        spaceDao = new SpaceDaoImpl();
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
            build();

        // exercise
        final Space createdSpace = spaceDao.createSpace( space, session );
        commit();

        // verify
        Node spaceNode = session.getNode( "/" + SPACES_PATH + "mySpace" );
        assertNotNull( spaceNode );
        assertNotNull( createdSpace.getRootContent() );
        assertEquals( createdSpace.getRootContent().id(), spaceNode.getNode( SPACE_CONTENT_ROOT_NODE ).getIdentifier() );
        assertEquals( time, createdSpace.getCreatedTime() );
        assertEquals( time, createdSpace.getModifiedTime() );
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
            build();
        final Space space2 = newSpace().
            name( "myOtherSpace" ).
            displayName( "My Other Space" ).
            modifiedTime( time ).
            createdTime( time ).
            build();
        spaceDao.createSpace( space, session );
        spaceDao.createSpace( space2, session );
        commit();

        // exercise
        final Space spaceRetrieved = spaceDao.getSpace( SpaceName.from( "mySpace" ), session );

        // verify
        assertNotNull( spaceRetrieved );
        assertEquals( "mySpace", spaceRetrieved.getName().name() );
        assertEquals( "My Space", spaceRetrieved.getDisplayName() );
        assertEqualsDateTime( time, spaceRetrieved.getCreatedTime() );
        assertEqualsDateTime( time, spaceRetrieved.getModifiedTime() );
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
        final Space spaceCreated1 = spaceDao.createSpace( space, session );
        final Space spaceCreated2 = spaceDao.createSpace( space2, session );
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
        final Space spaceCreated = spaceDao.createSpace( space, session );
        commit();

        // exercise
        final DateTime updateTime = time.plusHours( 3 ).plusDays( 5 );
        final Space spaceChanges = newSpace().
            name( "mySpace" ).
            displayName( "This is my Space" ).
            createdTime( time ).
            modifiedTime( updateTime ).
            build();
        spaceDao.updateSpace( spaceChanges, session );

        // verify
        final Space spaceUpdated = spaceDao.getSpace( SpaceName.from( "mySpace" ), session );
        assertNotNull( spaceUpdated );
        assertEquals( "mySpace", spaceUpdated.getName().name() );
        assertEquals( "This is my Space", spaceUpdated.getDisplayName() );
        assertEqualsDateTime( time, spaceUpdated.getCreatedTime() );
        assertEqualsDateTime( updateTime, spaceUpdated.getModifiedTime() );
        assertEquals( spaceCreated.getRootContent(), spaceUpdated.getRootContent() );
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
