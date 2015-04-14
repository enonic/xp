package com.enonic.xp.core.impl.content;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.CreateContentTranslatorParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.module.ModuleService;
import com.enonic.xp.name.NamePrettyfier;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.mixin.MixinService;

import static com.enonic.xp.schema.content.ContentType.newContentType;
import static org.junit.Assert.*;

public class CreateContentCommandTest
{
    private static final Instant CREATED_TIME = LocalDateTime.of( 2013, 1, 1, 12, 0, 0, 0 ).toInstant( ZoneOffset.UTC );

    private final ContentTypeService contentTypeService = Mockito.mock( ContentTypeService.class );

    private final MixinService mixinService = Mockito.mock( MixinService.class );

    private final ModuleService moduleService = Mockito.mock( ModuleService.class );

    private final NodeService nodeService = Mockito.mock( NodeService.class );

    private final ContentNodeTranslator translator = Mockito.mock( ContentNodeTranslator.class );

    private final EventPublisher eventPublisher = Mockito.mock( EventPublisher.class );

    private final MediaInfo mediaInfo = MediaInfo.create().mediaType( "image/jpg" ).build();


    @Test(expected = IllegalArgumentException.class)
    public void content_type_null() {

        CreateContentCommand command = createContentCommand(createContentParams());
        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( null );
        // exercise
        command.execute();
    }

    @Test(expected = ContentNotFoundException.class)
    public void bad_parent_content_path() {
        PropertyTree existingContentData = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        existingContentData.addString( "myData", "aaa" );

        CreateContentParams params = CreateContentParams.create()
            .type( ContentTypeName.site() )
            .parent( ContentPath.from( "/myPath/myContent" ) )
            .contentData( existingContentData )
            .displayName( "displayName" )
            .build();

        CreateContentCommand command = createContentCommand( params );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn(
            newContentType().superType( ContentTypeName.documentMedia() ).name( ContentTypeName.dataMedia() ).build() );

        command.execute();
    }

    @Test
    public void content_params_not_present_test() {
        PropertyTree existingContentData = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        existingContentData.addString( "myData", "aaa" );

        try {
            CreateContentParams.create()
                .type( ContentTypeName.site() )
                .parent( ContentPath.from( "/myPath/myContent" ) )
                .contentData( existingContentData )
                .build();
        } catch ( Exception e ) {
            assertEquals("displayName cannot be null", e.getMessage());
        }

        try {
            CreateContentParams.create()
                .parent( ContentPath.from( "/myPath/myContent" ) )
                .contentData( existingContentData )
                .displayName( "displayName" )
                .build();
        } catch ( Exception e ) {
            assertEquals("type cannot be null", e.getMessage());
        }

        try {
            CreateContentParams.create()
                .type( ContentTypeName.site() )
                .contentData( existingContentData )
                .displayName( "displayName" )
                .build();
        } catch ( Exception e ) {
            assertEquals("parentContentPath cannot be null", e.getMessage());
        }

        try {
            CreateContentParams.create()
                .parent( ContentPath.from( "/myPath/myContent" ) )
                .type( ContentTypeName.site() )
                .displayName( "displayName" )
                .build();
        } catch ( Exception e ) {
            assertEquals("data cannot be null", e.getMessage());
        }
    }

    @Test(expected = NullPointerException.class)
    public void name_generated_from_display_name() {

        final CreateContentParams params = createContentParams();

        CreateContentCommand command = createContentCommand( params );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn(
            newContentType().superType( ContentTypeName.documentMedia() ).name( ContentTypeName.dataMedia() ).build() );

        Mockito.when( this.translator.toCreateNodeParams( Mockito.any( CreateContentTranslatorParams.class ) )).thenAnswer(
            (invocation) ->
            {
                {
                    Object[] args = invocation.getArguments();
                    CreateContentTranslatorParams passedParam = (CreateContentTranslatorParams) args[0];
                    assertEquals( NamePrettyfier.create( params.getDisplayName() ), passedParam.getName().toString() );
                    return new ContentNodeTranslator().toCreateNodeParams( passedParam );
                }
            } );

        // exercise
        command.execute();
    }

    @Test(expected = NullPointerException.class)
    public void name_present_and_unchanged() {

        PropertyTree existingContentData = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        existingContentData.addString( "myData", "aaa" );

        final CreateContentParams params = CreateContentParams.create()
            .name( "myname" )
            .type( ContentTypeName.site() )
            .parent( ContentPath.ROOT )
            .contentData( existingContentData )
            .displayName( "displayName" )
            .build();

        CreateContentCommand command = createContentCommand( params );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn(
            newContentType().superType( ContentTypeName.documentMedia() ).name( ContentTypeName.dataMedia() ).build() );

        Mockito.when( this.translator.toCreateNodeParams( Mockito.any( CreateContentTranslatorParams.class ) )).thenAnswer(
            (invocation) ->
            {
                {
                    Object[] args = invocation.getArguments();
                    CreateContentTranslatorParams passedParam = (CreateContentTranslatorParams) args[0];
                    assertEquals( "myname", passedParam.getName().toString() );
                    return new ContentNodeTranslator().toCreateNodeParams( passedParam );
                }
            } );

        command.execute();
    }

    private CreateContentParams createContentParams () {
        PropertyTree existingContentData = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        existingContentData.addString( "myData", "aaa" );

        CreateContentParams params = CreateContentParams.create()
            .type( ContentTypeName.site() )
            .parent( ContentPath.ROOT )
            .contentData( existingContentData )
            .displayName( "displayName" )
            .build();
        return params;
    }

    private CreateContentCommand createContentCommand(CreateContentParams params) {
        CreateContentCommand command = CreateContentCommand.create().
            params( params ).
            contentTypeService( this.contentTypeService ).
            nodeService( this.nodeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            mediaInfo( this.mediaInfo ).
            mixinService( this.mixinService ).
            moduleService( this.moduleService ).
            build();

        return command;
    }
}
