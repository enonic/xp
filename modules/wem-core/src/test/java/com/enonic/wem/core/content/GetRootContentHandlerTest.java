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
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.dao.ContentDao;

import static org.junit.Assert.*;

public class GetRootContentHandlerTest
    extends AbstractCommandHandlerTest
{

    private GetRootContentHandler handler;

    private ContentDao contentDao;

    @Before
    public void setUp()
        throws Exception
    {

        super.client = Mockito.mock( Client.class );
        super.initialize();

        handler = new GetRootContentHandler();
        handler.setContext( this.context );

        contentDao = Mockito.mock( ContentDao.class );

        handler.setContentDao( contentDao );
    }

    @Test
    public void getRootContent_no_content()
        throws Exception
    {
        GetRootContent getRootContentCommand = new GetRootContent();
        Mockito.when( contentDao.findChildContent( Mockito.eq( ContentPath.ROOT ), Mockito.isA( Session.class ) ) ).thenReturn(
            Contents.empty() );

        this.handler.setCommand( getRootContentCommand );
        handler.handle();

        assertEquals( 0, getRootContentCommand.getResult().getSize() );
    }

    @Test
    public void getRootContent_existing_content()
        throws Exception
    {
        GetRootContent getRootContentCommand = new GetRootContent();

        Content root1 = createContent( "root1" );
        Content root2 = createContent( "root2" );

        Mockito.when( contentDao.findChildContent( Mockito.eq( ContentPath.ROOT ), Mockito.isA( Session.class ) ) ).thenReturn(
            Contents.from( root1, root2 ) );

        this.handler.setCommand( getRootContentCommand );
        handler.handle();

        assertEquals( 2, getRootContentCommand.getResult().getSize() );

        assertEquals( getRootContentCommand.getResult(), Contents.from( root1, root2 ) );
    }

    private Content createContent( String name )
    {
        return Content.newContent().
            name( name ).displayName( name ).
            parentPath( ContentPath.ROOT ).
            modifiedTime( DateTime.now() ).
            createdTime( DateTime.now() ).build();
    }
}
