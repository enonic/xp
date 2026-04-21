package com.enonic.xp.lib.content;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentVersionId;
import com.enonic.xp.content.ValidationError;
import com.enonic.xp.content.ValidationErrorCode;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.util.BinaryReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetContentHandlerTest
    extends BaseContentHandlerTest
{
    @Test
    void testExample()
    {
        final Content content = TestDataFixtures.newExampleContent();
        Mockito.when( this.contentService.getByPath( Mockito.any() ) ).thenReturn( content );
        Mockito.when(
            this.contentService.getByIdAndVersionId( Mockito.any( ContentId.class ), Mockito.any( ContentVersionId.class ) ) ).thenReturn(
            content );

        runScript( "/lib/xp/examples/content/get.js" );
    }

    @Test
    void getById()
    {
        final Content content = TestDataFixtures.newContent();
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        runFunction( "/test/GetContentHandlerTest.js", "getById" );
    }

    @Test
    void getByIdWithPageAsFragment()
    {
        final Content content = TestDataFixtures.newContentWithPageAsFragment();
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        runFunction( "/test/GetContentHandlerTest.js", "getByIdWithPageAsFragment" );
    }

    @Test
    void getByPath()
    {
        final Content content = TestDataFixtures.newContent();
        Mockito.when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );

        runFunction( "/test/GetContentHandlerTest.js", "getByPath" );
    }

    @Test
    void getById_notFound()
    {
        final ContentId id = ContentId.from( "123456" );
        Mockito.when( this.contentService.getById( Mockito.any() ) )
            .thenThrow( ContentNotFoundException.class );


        runFunction( "/test/GetContentHandlerTest.js", "getById_notFound" );
    }

    @Test
    void getByPath_notFound()
    {
        final ContentPath path = ContentPath.from( "/a/b/mycontent" );
        Mockito.when( this.contentService.getByPath( path ) )
            .thenThrow( ContentNotFoundException.class );

        runFunction( "/test/GetContentHandlerTest.js", "getByPath_notFound" );
    }

    @Test
    void getByIdAndVersionId()
    {
        final Content content = TestDataFixtures.newExampleContent();

        final ContentId contentId = ContentId.from( "mycontentid" );
        final ContentVersionId versionId = ContentVersionId.from( "versionId" );
        Mockito.when( this.contentService.getByIdAndVersionId( contentId, versionId ) ).thenReturn( content );

        runFunction( "/test/GetContentHandlerTest.js", "getByIdAndVersionId" );
    }

    @Test
    void getByIdAndVersionId_NotFound()
    {
        final ContentId contentId = ContentId.from( "mycontentid" );
        final ContentVersionId versionId = ContentVersionId.from( "versionId" );
        Mockito.when( this.contentService.getByIdAndVersionId( contentId, versionId ) ).thenThrow(
            ContentNotFoundException.class );

        runFunction( "/test/GetContentHandlerTest.js", "getByIdAndVersionId_notFound" );
    }

    @Test
    void getById_withValidationErrors()
    {
        final ValidationErrorCode code = ValidationErrorCode.from( ApplicationKey.from( "com.enonic.myapp" ), "REQUIRED" );
        final Content content = Content.create( TestDataFixtures.newContent() )
            .validationErrors( ValidationErrors.create()
                                   .add( ValidationError.generalError( code )
                                             .message( "Missing value", true )
                                             .i18n( "err.required" )
                                             .args( "field1" )
                                             .build() )
                                   .add( ValidationError.dataError( code, PropertyPath.from( "set.field" ) ).build() )
                                   .add( ValidationError.attachmentError( code, BinaryReference.from( "image.png" ) ).build() )
                                   .build() )
            .build();
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        runFunction( "/test/GetContentHandlerTest.js", "getById_withValidationErrors" );
    }

    @Test
    void getById_withNullValidationErrors()
    {
        final Content content = TestDataFixtures.newContent();
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        runFunction( "/test/GetContentHandlerTest.js", "getById_withNullValidationErrors" );
    }

    @Test
    void getByIdInLayer()
    {
        final Content content = TestDataFixtures.newExampleLayerContentBuilder().build();

        final ContentId contentId = ContentId.from( "mycontentid" );
        Mockito.when( this.contentService.getById( contentId ) ).thenReturn( content );

        final ScriptValue result = runFunction( "/test/GetContentHandlerTest.js", "getByIdInLayer" );

        assertEquals( 2, result.getMember( "inherit" ).getArray().size() );
        assertEquals( "CONTENT", result.getMember( "inherit" ).getArray().get( 0 ).getValue() );
        assertEquals( "NAME", result.getMember( "inherit" ).getArray().get( 1 ).getValue() );
        assertEquals( "origin", result.getMember( "originProject" ).getValue() );
    }

}
