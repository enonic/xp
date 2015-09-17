package com.enonic.xp.core.impl.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.CreateContentTranslatorParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.name.NamePrettyfier;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.site.SiteService;

import static org.junit.Assert.*;

public class CreateContentCommandTest
{
    private ContentTypeService contentTypeService;

    private MixinService mixinService;

    private SiteService siteService;

    private NodeService nodeService;

    private OldContentNodeTranslator oldTranslator = Mockito.mock( OldContentNodeTranslator.class );

    private ContentNodeTranslator translator = Mockito.mock( ContentNodeTranslator.class );

    private EventPublisher eventPublisher;

    private MediaInfo mediaInfo;


    @Before
    public void setUp()
        throws Exception
    {
        siteService = Mockito.mock( SiteService.class );
        nodeService = Mockito.mock( NodeService.class );
        eventPublisher = Mockito.mock( EventPublisher.class );
        mediaInfo = MediaInfo.create().mediaType( "image/jpg" ).build();
        mixinService = Mockito.mock( MixinService.class );
        contentTypeService = Mockito.mock( ContentTypeService.class );
    }

    @Test(expected = IllegalArgumentException.class)
    public void content_type_null()
    {

        CreateContentCommand command = createContentCommand( createContentParams() );
        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( null );
        // exercise
        command.execute();
    }

    @Test(expected = ContentNotFoundException.class)
    public void bad_parent_content_path()
    {
        PropertyTree existingContentData = new PropertyTree();
        existingContentData.addString( "myData", "aaa" );

        CreateContentParams params = CreateContentParams.create().
            type( ContentTypeName.site() ).
            parent( ContentPath.from( "/myPath/myContent" ) ).
            contentData( existingContentData ).
            displayName( "displayName" ).
            build();

        CreateContentCommand command = createContentCommand( params );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().superType( ContentTypeName.documentMedia() ).name( ContentTypeName.dataMedia() ).build() );

        command.execute();
    }

    @Test
    public void content_params_not_present_test()
    {
        PropertyTree data = new PropertyTree();
        data.addString( "myData", "aaa" );

        try
        {
            CreateContentParams.create().
                type( ContentTypeName.site() ).
                parent( ContentPath.from( "/myPath/myContent" ) ).
                contentData( data ).
                build();
        }
        catch ( Exception e )
        {
            assertEquals( "displayName cannot be null", e.getMessage() );
        }

        try
        {
            CreateContentParams.create().
                parent( ContentPath.from( "/myPath/myContent" ) ).
                contentData( data ).
                displayName( "displayName" ).
                build();
        }
        catch ( Exception e )
        {
            assertEquals( "type cannot be null", e.getMessage() );
        }

        try
        {
            CreateContentParams.create().
                type( ContentTypeName.site() ).
                contentData( data ).
                displayName( "displayName" ).
                build();
        }
        catch ( Exception e )
        {
            assertEquals( "parentContentPath cannot be null", e.getMessage() );
        }

        try
        {
            CreateContentParams.create().
                parent( ContentPath.from( "/myPath/myContent" ) ).
                type( ContentTypeName.site() ).
                displayName( "displayName" ).
                build();
        }
        catch ( Exception e )
        {
            assertEquals( "data cannot be null", e.getMessage() );
        }
    }

    @Test(expected = NullPointerException.class)
    public void name_generated_from_display_name()
    {
        final CreateContentParams params = createContentParams();

        CreateContentCommand command = createContentCommand( params );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().superType( ContentTypeName.documentMedia() ).name( ContentTypeName.dataMedia() ).build() );

        Mockito.when( this.oldTranslator.toCreateNodeParams( Mockito.any( CreateContentTranslatorParams.class ) ) ).thenAnswer(
            ( invocation ) -> {
                {
                    Object[] args = invocation.getArguments();
                    CreateContentTranslatorParams passedParam = (CreateContentTranslatorParams) args[0];
                    assertEquals( NamePrettyfier.create( params.getDisplayName() ), passedParam.getName().toString() );
                    return new OldContentNodeTranslator().toCreateNodeParams( passedParam );
                }
            } );

        // exercise
        command.execute();
    }

    @Test(expected = NullPointerException.class)
    public void name_present_and_unchanged()
    {

        PropertyTree existingContentData = new PropertyTree();
        existingContentData.addString( "myData", "aaa" );

        final CreateContentParams params =
            CreateContentParams.create().name( "myname" ).type( ContentTypeName.site() ).parent( ContentPath.ROOT ).contentData(
                existingContentData ).displayName( "displayName" ).build();

        CreateContentCommand command = createContentCommand( params );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().superType( ContentTypeName.documentMedia() ).name( ContentTypeName.dataMedia() ).build() );

        Mockito.when( this.oldTranslator.toCreateNodeParams( Mockito.any( CreateContentTranslatorParams.class ) ) ).thenAnswer(
            ( invocation ) -> {
                {
                    Object[] args = invocation.getArguments();
                    CreateContentTranslatorParams passedParam = (CreateContentTranslatorParams) args[0];
                    assertEquals( "myname", passedParam.getName().toString() );
                    return new OldContentNodeTranslator().toCreateNodeParams( passedParam );
                }
            } );

        command.execute();
    }

    private CreateContentParams createContentParams()
    {
        PropertyTree existingContentData = new PropertyTree();
        existingContentData.addString( "myData", "aaa" );

        CreateContentParams params = CreateContentParams.create().type( ContentTypeName.site() ).parent( ContentPath.ROOT ).contentData(
            existingContentData ).displayName( "displayName" ).build();
        return params;
    }

    private CreateContentCommand createContentCommand( CreateContentParams params )
    {
        CreateContentCommand command = CreateContentCommand.create().
            params( params ).
            contentTypeService( this.contentTypeService ).
            nodeService( this.nodeService ).
            oldTranslator( this.oldTranslator ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            mediaInfo( this.mediaInfo ).
            mixinService( this.mixinService ).
            siteService( this.siteService ).
            build();

        return command;
    }
}
