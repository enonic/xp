package com.enonic.xp.portal.impl.jslib.content;

import org.junit.Test;
import org.mockito.Mockito;

import junit.framework.Assert;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentEditor;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.EditableContent;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.portal.impl.jslib.AbstractHandlerTest;
import com.enonic.xp.portal.impl.jslib.ContentFixtures;
import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinService;

public class ModifyContentHandlerTest
    extends AbstractHandlerTest
{
    private ContentService contentService;

    private MixinService mixinService;

    @Override
    protected CommandHandler createHandler()
        throws Exception
    {
        this.contentService = Mockito.mock( ContentService.class );
        this.mixinService = Mockito.mock( MixinService.class );

        final ModifyContentHandler handler = new ModifyContentHandler();
        handler.setContentService( this.contentService );
        handler.setMixinService( this.mixinService );

        return handler;
    }

    @Test
    public void modifyById()
        throws Exception
    {
        final Mixin metaMixin = Mixin.newMixin().name( "com.enonic.mymodule:myschema" ).build();
        Mockito.when( this.mixinService.getByName( Mockito.eq( metaMixin.getName() ) ) ).thenReturn( metaMixin );
        final Mixin metaMixin2 = Mixin.newMixin().name( "com.enonic.mymodule:other" ).build();
        Mockito.when( this.mixinService.getByName( Mockito.eq( metaMixin2.getName() ) ) ).thenReturn( metaMixin2 );

        Mockito.when( this.contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdate( (UpdateContentParams) invocationOnMock.getArguments()[0] ) );

        execute( "modifyById" );
    }

    @Test
    public void modifyByPath()
        throws Exception
    {
        final Mixin metaMixin = Mixin.newMixin().name( "com.enonic.mymodule:myschema" ).build();
        Mockito.when( this.mixinService.getByName( Mockito.eq( metaMixin.getName() ) ) ).thenReturn( metaMixin );
        final Mixin metaMixin2 = Mixin.newMixin().name( "com.enonic.mymodule:other" ).build();
        Mockito.when( this.mixinService.getByName( Mockito.eq( metaMixin2.getName() ) ) ).thenReturn( metaMixin2 );

        final Content content = ContentFixtures.newSmallContent();
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

        final Content content = ContentFixtures.newSmallContent();
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
