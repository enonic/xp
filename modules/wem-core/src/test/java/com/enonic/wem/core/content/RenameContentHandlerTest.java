package com.enonic.wem.core.content;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.RenameContent;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.attachment.dao.AttachmentDao;
import com.enonic.wem.core.content.dao.ContentDao;

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

    private AttachmentDao attachmentDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.client = Mockito.mock( Client.class );

        super.initialize();

        contentDao = Mockito.mock( ContentDao.class );
        attachmentDao = Mockito.mock( AttachmentDao.class );

        handler = new RenameContentHandler();
        handler.setContext( this.context );
        handler.setContentDao( contentDao );
        handler.setAttachmentDao( attachmentDao );
    }

    @Test
    public void renameContent()
        throws Exception
    {
        // setup
        final ContentId contentId = ContentId.from( "1fad493a-6a72-41a3-bac4-88aba3d83bcc" );
        final Content content = Content.newContent().
            id( contentId ).
            parentPath( ContentPath.ROOT ).
            name( "myContent" ).
            displayName( "MyContent" ).
            owner( UserKey.superUser() ).
            contentData( new ContentData() ).
            build();
        Mockito.when( contentDao.select( isA( ContentId.class ), any( Session.class ) ) ).thenReturn( content );
        Mockito.when( contentDao.renameContent( isA( ContentId.class ), eq( "newName" ), any( Session.class ) ) ).thenReturn( true );
        Mockito.when( attachmentDao.renameAttachments( isA( ContentId.class ), eq( "myContent" ), eq( "newName" ),
                                                       any( Session.class ) ) ).thenReturn( true );

        final RenameContent command = Commands.content().rename().contentId( contentId ).newName( "newName" );

        // exercise
        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        verify( contentDao, Mockito.atLeastOnce() ).renameContent( isA( ContentId.class ), eq( "newName" ), any( Session.class ) );
        boolean renamed = command.getResult();
        assertTrue( renamed );
    }

}
