package com.enonic.wem.core.content.attachment;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.attachment.DeleteAttachment;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentSelector;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.attachment.dao.AttachmentDao;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.when;

public class DeleteAttachmentHandlerTest
    extends AbstractCommandHandlerTest
{
    private DeleteAttachmentHandler handler;

    private AttachmentDao attachmentDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        attachmentDao = Mockito.mock( AttachmentDao.class );
        handler = new DeleteAttachmentHandler();
        handler.setAttachmentDao( attachmentDao );
    }

    @Test
    public void deleteAttachment()
        throws Exception
    {
        //setup
        when( attachmentDao.deleteAttachment( isA( ContentSelector.class ), isA( String.class ), any( Session.class ) ) ).
            thenReturn( true );

        // exercise
        final DeleteAttachment command =
            Commands.attachment().delete().contentSelector( ContentPath.from( "myspace:/image" ) ).attachmentName( "file.jpg" );
        this.handler.handle( this.context, command );

        // verify
        Mockito.verify( attachmentDao, only() ).deleteAttachment( eq( ContentPath.from( "myspace:/image" ) ), eq( "file.jpg" ),
                                                                  any( Session.class ) );

        assertTrue( command.getResult() );
    }

    @Test
    public void deleteAttachmentNotFound()
        throws Exception
    {
        //setup
        when( attachmentDao.deleteAttachment( isA( ContentSelector.class ), isA( String.class ), any( Session.class ) ) ).
            thenReturn( false );

        // exercise
        final DeleteAttachment command =
            Commands.attachment().delete().contentSelector( ContentPath.from( "myspace:/image" ) ).attachmentName( "file.jpg" );
        this.handler.handle( this.context, command );

        // verify
        Mockito.verify( attachmentDao, only() ).deleteAttachment( eq( ContentPath.from( "myspace:/image" ) ), eq( "file.jpg" ),
                                                                  any( Session.class ) );

        assertFalse( command.getResult() );
    }
}
