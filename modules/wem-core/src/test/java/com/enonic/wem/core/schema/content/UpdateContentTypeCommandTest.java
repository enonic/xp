package com.enonic.wem.core.schema.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.event.EventPublisher;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.api.schema.content.GetContentTypesParams;
import com.enonic.wem.api.schema.content.UpdateContentTypeParams;
import com.enonic.wem.api.schema.content.UpdateContentTypeResult;
import com.enonic.wem.api.schema.content.editor.ContentTypeEditor;
import com.enonic.wem.api.schema.content.validator.InvalidContentTypeException;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;

import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.schema.content.editor.SetContentTypeEditor.newSetContentTypeEditor;
import static junit.framework.Assert.assertEquals;

public class UpdateContentTypeCommandTest
{
    private UpdateContentTypeCommand command;

    private ContentTypeDao contentTypeDao;

    private MixinService mixinService;

    private ContentTypeService contentTypeService;

    private EventPublisher eventPublisher;

    @Before
    public void setUp()
        throws Exception
    {
        this.mixinService = Mockito.mock( MixinService.class );
        this.contentTypeDao = Mockito.mock( ContentTypeDao.class );
        this.contentTypeService = Mockito.mock( ContentTypeService.class );
        this.eventPublisher = Mockito.mock( EventPublisher.class );

        command = new UpdateContentTypeCommand().
            contentTypeDao( this.contentTypeDao ).
            mixinService( this.mixinService ).
            contentTypeService( this.contentTypeService ).
            eventPublisher( this.eventPublisher );
    }

    @Test
    public void updateContentType()
        throws Exception
    {
        // setup
        Mockito.when( contentTypeService.getByNames( Mockito.isA( GetContentTypesParams.class ) ) ).thenReturn(
            ContentTypes.from( ContentTypesInitializer.STRUCTURED ) );

        final String contentType1Name = "mymodule-1.0.0:my-contenttype-1";
        final String contentType2Name = "mymodule-1.0.0:my-contenttype-2";

        final String displayName1 = "DisplayName";
        final String displayName2 = "DisplayName2";

        final String description1 = "Description";
        final String description2 = "Description2";

        final ContentTypeName name = ContentTypeName.from( contentType1Name );

        final ContentType.Builder ctb1 = ContentType.
            newContentType().
            displayName( displayName1 ).
            name( contentType1Name ).
            setBuiltIn().
            description( description1 );
        final ContentType ct1 = ctb1.build();

        final ContentType.Builder ctb2 = ContentType.
            newContentType().
            displayName( displayName2 ).
            name( contentType2Name ).
            description( description2 );
        final ContentType ct2 = ctb2.build();

        final ContentTypes allContentTypes = ContentTypes.from( ct1, ct2 );
        Mockito.when( contentTypeDao.getAllContentTypes() ).thenReturn( allContentTypes );

        Mockito.when( contentTypeDao.getContentType( Mockito.eq( name ) ) ).thenReturn( ctb1 );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( ct1 );

        final UpdateContentTypeParams params = new UpdateContentTypeParams().contentTypeName( name );
        final ContentTypeEditor editor = newSetContentTypeEditor().
            displayName( "Changed" ).
            setAbstract( false ).
            superType( ContentTypeName.structured() ).
            build();
        params.editor( editor );

        // exercise
        final UpdateContentTypeResult result = this.command.params( params ).execute();

        // verify
        assertEquals( UpdateContentTypeResult.SUCCESS, result );
    }

    @Test(expected = InvalidContentTypeException.class)
    public void given_superType_that_is_final_when_handle_then_InvalidContentTypeException()
        throws Exception
    {
        // setup
        final String contentType1Name = "mymodule-1.0.0:my-contenttype-1";
        final String contentType2Name = "mymodule-1.0.0:my-contenttype-2";

        final String displayName1 = "DisplayName";
        final String displayName2 = "DisplayName2";

        final String description1 = "Description";
        final String description2 = "Description2";

        final ContentTypeName name = ContentTypeName.from( contentType1Name );

        Mockito.when( contentTypeService.getByNames( Mockito.isA( GetContentTypesParams.class ) ) ).thenReturn(
            ContentTypes.from( ContentTypesInitializer.STRUCTURED ) );

        ContentType existingContentType = newContentType().
            name( "mymodule-1.0.0:my_content_type" ).
            displayName( "My content type" ).
            setAbstract( false ).
            setFinal( true ).
            superType( ContentTypeName.structured() ).
            build();

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( existingContentType );

        final UpdateContentTypeParams params = new UpdateContentTypeParams().contentTypeName( ContentTypeName.from( contentType1Name ) );
        final ContentTypeEditor editor = newSetContentTypeEditor().
            displayName( "Changed" ).
            setAbstract( false ).
            superType( ContentTypeName.shortcut() ).
            build();
        params.editor( editor );

        final ContentType.Builder ctb1 = ContentType.
            newContentType().
            displayName( displayName1 ).
            name( contentType1Name ).
            setBuiltIn().
            description( description1 );
        final ContentType ct1 = ctb1.build();

        final ContentType.Builder ctb2 = ContentType.
            newContentType().
            displayName( displayName2 ).
            name( contentType2Name ).
            description( description2 );
        final ContentType ct2 = ctb2.build();

        final ContentTypes allContentTypes = ContentTypes.from( ct1, ct2 );
        Mockito.when( contentTypeDao.getAllContentTypes() ).thenReturn( allContentTypes );

        Mockito.when( contentTypeDao.getContentType( Mockito.eq( name ) ) ).thenReturn( ctb1 );

        // exercise
        this.command.params( params ).execute();
    }
}
