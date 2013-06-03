package com.enonic.wem.core.content.attachment;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.attachment.GetAttachment;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentSelector;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.binary.Binary;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.attachment.dao.AttachmentDao;

import static com.enonic.wem.api.content.attachment.Attachment.newAttachment;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetAttachmentHandlerTest
    extends AbstractCommandHandlerTest
{
    private GetAttachmentHandler handler;

    private AttachmentDao attachmentDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        attachmentDao = Mockito.mock( AttachmentDao.class );
        handler = new GetAttachmentHandler();
        handler.setAttachmentDao( attachmentDao );
    }

    @Test
    public void getAttachment()
        throws Exception
    {
        // setup
        final Binary binary = Binary.from( "some data".getBytes() );
        final Attachment attachment = newAttachment().binary( binary ).name( "file.jpg" ).mimeType( "image/jpeg" ).label( "small" ).build();
        when( attachmentDao.getAttachment( isA( ContentSelector.class ), isA( String.class ), any( Session.class ) ) ).thenReturn(
            attachment );

        // exercise

        final GetAttachment command =
            Commands.attachment().get().contentSelector( ContentPath.from( "myspace:/image" ) ).attachmentName( "file.jpg" );
        this.handler.handle( this.context, command );

        // verify
        verify( attachmentDao, atLeastOnce() ).getAttachment( eq( ContentPath.from( "myspace:/image" ) ), eq( "file.jpg" ),
                                                              any( Session.class ) );
        assertEquals( attachment, command.getResult() );
    }

}
