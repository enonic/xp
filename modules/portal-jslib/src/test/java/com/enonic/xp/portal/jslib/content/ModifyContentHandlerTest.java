package com.enonic.xp.portal.jslib.content;

import org.junit.Test;
import org.mockito.Mockito;

import junit.framework.Assert;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentEditor;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.EditableContent;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.script.command.CommandHandler;
import com.enonic.xp.portal.jslib.AbstractHandlerTest;

public class ModifyContentHandlerTest
    extends AbstractHandlerTest
{
    private ContentService contentService;

    @Override
    protected CommandHandler createHandler()
        throws Exception
    {
        this.contentService = Mockito.mock( ContentService.class );

        final ModifyContentHandler handler = new ModifyContentHandler();
        handler.setContentService( this.contentService );

        return handler;
    }

    @Test
    public void modifyById()
        throws Exception
    {
        Mockito.when( this.contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdate( (UpdateContentParams) invocationOnMock.getArguments()[0] ) );

        execute( "modifyById" );
    }

    @Test
    public void modifyByPath()
        throws Exception
    {
        final Content content = ContentFixtures.newContent();
        Mockito.when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );

        Mockito.when( this.contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdate( (UpdateContentParams) invocationOnMock.getArguments()[0] ) );

        execute( "modifyByPath" );
    }

    private Content invokeUpdate( final UpdateContentParams params )
    {
        Assert.assertEquals( ContentId.from( "123456" ), params.getContentId() );

        final ContentEditor editor = params.getEditor();
        Assert.assertNotNull( editor );

        final Content content = ContentFixtures.newContent();
        final EditableContent editable = new EditableContent( content );

        editor.edit( editable );
        return editable.build();
    }

    @Test
    public void modify_notFound()
        throws Exception
    {
        execute( "modify_notFound" );
    }
}
