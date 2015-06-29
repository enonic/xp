package com.enonic.xp.portal.impl.jslib.content;

import java.time.Instant;

import org.junit.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.portal.impl.jslib.AbstractHandlerTest;
import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.security.PrincipalKey;

public class CreateContentHandlerTest
    extends AbstractHandlerTest
{
    private ContentService contentService;

    @Override
    protected CommandHandler createHandler()
        throws Exception
    {
        this.contentService = Mockito.mock( ContentService.class );

        final CreateContentHandler handler = new CreateContentHandler();
        handler.setContentService( this.contentService );

        return handler;
    }

    @Test
    public void createContent()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        data.addLong( "a", 1l );
        data.addLong( "b", 2l );
        data.addString( "c", "1" );
        data.addString( "c", "2" );
        final PropertySet d = data.addSet( "d" );
        final PropertySet e = d.addSet( "e" );
        e.addDouble( "f", 3.6 );
        e.addBoolean( "g", true );

        Mockito.when( this.contentService.create( Mockito.any( CreateContentParams.class ) ) ).thenAnswer(
            mock -> createContent( (CreateContentParams) mock.getArguments()[0] ) );

        Mockito.when( this.contentService.translateToPropertyTree( Mockito.isA( JsonNode.class ),
                                                                   Mockito.eq( ContentTypeName.from( "test:myContentType" ) ) ) ).
            thenReturn( data );

        final PropertyTree extraData = new PropertyTree();
        extraData.addDouble( "a", 1.0 );

        Mockito.when( this.contentService.translateToPropertyTree( Mockito.isA( JsonNode.class ),
                                                                   Mockito.eq( MixinName.from( "com.enonic.mymodule:myschema" ) ) ) ).
            thenReturn( extraData );

        execute( "createContent" );
    }

    private Content createContent( final CreateContentParams params )
    {
        final Content.Builder builder = Content.create();
        builder.id( ContentId.from( "123456" ) );
        builder.name( params.getName() );
        builder.parentPath( params.getParent() );
        builder.displayName( params.getDisplayName() );
        builder.valid( false );
        builder.type( params.getType() );
        builder.data( params.getData() );
        builder.creator( PrincipalKey.ofAnonymous() );
        builder.createdTime( Instant.parse( "1975-01-08T00:00:00Z" ) );

        if ( params.getExtraDatas() != null )
        {
            builder.extraDatas( ExtraDatas.from( params.getExtraDatas() ) );
        }

        return builder.build();
    }
}
