package com.enonic.wem.core.content;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.RenameContent;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.content.dao.ContentIdFactory;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;

public class RenameContentHandlerTest
    extends AbstractCommandHandlerTest
{
    private RenameContentHandler handler;

    private ContentDao contentDao;


    @Before
    public void setUp()
        throws Exception
    {
        super.client = Mockito.mock( Client.class );

        super.initialize();

        contentDao = Mockito.mock( ContentDao.class );

        handler = new RenameContentHandler();
        handler.setContentDao( contentDao );
    }

    @Test
    public void renameContent()
        throws Exception
    {
        // setup
        Mockito.when( contentDao.renameContent( isA( ContentId.class ), eq( "newName" ), any( Session.class ) ) ).thenReturn( true );

        final ContentId contentId = ContentIdFactory.from( "1fad493a-6a72-41a3-bac4-88aba3d83bcc" );
        final RenameContent command = Commands.content().rename().contentId( contentId ).newName( "newName" );

        // exercise
        this.handler.handle( this.context, command );

        // verify
        verify( contentDao, Mockito.atLeastOnce() ).renameContent( isA( ContentId.class ), eq( "newName" ), any( Session.class ) );
        boolean renamed = command.getResult();
        assertTrue( renamed );
    }

}
