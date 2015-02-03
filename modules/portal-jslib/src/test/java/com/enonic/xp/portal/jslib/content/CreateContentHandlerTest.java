package com.enonic.xp.portal.jslib.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.CreateContentParams;
import com.enonic.wem.api.content.Metadatas;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.script.command.CommandHandler;
import com.enonic.xp.portal.jslib.AbstractHandlerTest;

public class CreateContentHandlerTest
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

        final CreateContentHandler handler = new CreateContentHandler();
        handler.setContentService( this.contentService );
        handler.setMixinService( this.mixinService );

        return handler;
    }

    @Test
    public void createContent()
        throws Exception
    {
        Mockito.when( this.contentService.create( Mockito.any( CreateContentParams.class ) ) ).thenAnswer(
            mock -> createContent( (CreateContentParams) mock.getArguments()[0] ) );
        final Mixin metaMixin = Mixin.newMixin().name( "mymodule:test" ).build();
        Mockito.when( this.mixinService.getByLocalName( Mockito.eq( "test" ) ) ).thenReturn( metaMixin );

        execute( "createContent" );
    }

    private Content createContent( final CreateContentParams params )
    {
        final Content.Builder builder = Content.newContent();
        builder.id( ContentId.from( "123456" ) );
        builder.name( params.getName() );
        builder.parentPath( params.getParent() );
        builder.displayName( params.getDisplayName() );
        builder.valid( params.isRequireValid() );
        builder.type( params.getType() );
        builder.data( params.getData() );

        if ( params.getMetadata() != null )
        {
            builder.metadata( Metadatas.from( params.getMetadata() ) );
        }

        return builder.build();
    }
}
