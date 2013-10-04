package com.enonic.wem.core.content;

import javax.jcr.Session;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.GetRootContent;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentSelector;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.Spaces;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.space.dao.SpaceDao;

import static org.junit.Assert.*;

public class GetRootContentHandlerTest
    extends AbstractCommandHandlerTest
{

    private GetRootContentHandler handler;

    private SpaceDao spaceDao;

    private ContentDao contentDao;

    @Before
    public void setUp()
        throws Exception
    {

        super.client = Mockito.mock( Client.class );
        super.initialize();

        handler = new GetRootContentHandler();
        handler.setContext( this.context );

        spaceDao = Mockito.mock( SpaceDao.class );

        contentDao = Mockito.mock( ContentDao.class );

        handler.setContentDao( contentDao );
        handler.setSpaceDao( spaceDao );
    }

    @Test
    public void getRootContent_no_spaces()
        throws Exception
    {

        GetRootContent getRootContentCommand = new GetRootContent();
        Mockito.when( spaceDao.getAllSpaces( Mockito.isA( Session.class ) ) ).thenReturn( Spaces.empty() );

        handler.handle( getRootContentCommand );

        Mockito.verify( contentDao, Mockito.times( 0 ) ).select( Mockito.isA( ContentSelector.class ), Mockito.isA( Session.class ) );

        assertEquals( 0, getRootContentCommand.getResult().getSize() );
    }

    @Test
    public void getRootContent_empty_spaces()
        throws Exception
    {
        GetRootContent getRootContentCommand = new GetRootContent();

        Mockito.when( spaceDao.getAllSpaces( Mockito.isA( Session.class ) ) ).thenReturn(
            Spaces.from( createSpace( "test1" ), createSpace( "test2" ) ) );

        handler.handle( getRootContentCommand );

        Mockito.verify( contentDao, Mockito.times( 2 ) ).select( Mockito.isA( ContentSelector.class ), Mockito.isA( Session.class ) );

        assertEquals( 0, getRootContentCommand.getResult().getSize() );

    }

    @Test
    public void getRootContent_non_empty_spaces()
        throws Exception
    {
        GetRootContent getRootContentCommand = new GetRootContent();

        Space test1 = createSpace( "test1" );
        Space test2 = createSpace( "test2" );

        Content root1 = createContent( "root1" );
        Content root2 = createContent( "root2" );

        Mockito.when( spaceDao.getAllSpaces( Mockito.isA( Session.class ) ) ).thenReturn( Spaces.from( test1, test2 ) );

        Mockito.when( contentDao.select( Mockito.eq( ContentPath.rootOf( test1.getName() ) ), Mockito.isA( Session.class ) ) ).thenReturn(
            root1 );

        Mockito.when( contentDao.select( Mockito.eq( ContentPath.rootOf( test2.getName() ) ), Mockito.isA( Session.class ) ) ).thenReturn(
            root2 );

        handler.handle( getRootContentCommand );

        Mockito.verify( contentDao, Mockito.times( 2 ) ).select( Mockito.isA( ContentSelector.class ), Mockito.isA( Session.class ) );

        assertEquals( 2, getRootContentCommand.getResult().getSize() );

        assertEquals( getRootContentCommand.getResult(), Contents.from( root1, root2 ) );

    }

    private Space createSpace( String name )
    {
        return Space.newSpace().displayName( name ).createdTime( DateTime.now() ).modifiedTime( DateTime.now() ).name( name ).build();
    }

    private Content createContent( String name )
    {
        return Content.newContent().name( name ).displayName( name ).modifiedTime( DateTime.now() ).createdTime( DateTime.now() ).build();
    }
}
