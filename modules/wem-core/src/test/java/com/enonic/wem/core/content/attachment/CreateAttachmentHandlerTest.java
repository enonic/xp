package com.enonic.wem.core.content.attachment;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.attachment.CreateAttachment;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.binary.Binary;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.attachment.dao.AttachmentDao;

import static com.enonic.wem.api.content.attachment.Attachment.newAttachment;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CreateAttachmentHandlerTest
    extends AbstractCommandHandlerTest
{
    private CreateAttachmentHandler handler;

    private AttachmentDao attachmentDao;


    @Before
    public void setUp()
        throws Exception
    {
        super.client = Mockito.mock( Client.class );
        super.initialize();

        attachmentDao = Mockito.mock( AttachmentDao.class );

        handler = new CreateAttachmentHandler();
        handler.setAttachmentDao( attachmentDao );
    }

    @Test
    public void createAttachment()
        throws Exception
    {
        // setup
        final Binary binary = Binary.from( "some binary data".getBytes() );
        final Attachment attachment = newAttachment().name( "file.jpg" ).label( "small" ).mimeType( "image/jpeg" ).binary( binary ).build();
        final CreateAttachment command =
            Commands.attachment().create().contentSelector( ContentPath.from( "myspace:/image" ) ).attachment( attachment );

        // exercise
        this.handler.handle( this.context, command );

        // verify
        verify( attachmentDao, times( 1 ) ).createAttachment( isA( ContentPath.class ), isA( Attachment.class ), any( Session.class ) );
    }
}
