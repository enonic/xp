package com.enonic.xp.lib.content;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.EditableContentMetadata;
import com.enonic.xp.content.UpdateContentMetadataParams;
import com.enonic.xp.content.UpdateContentMetadataResult;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

public class UpdateMetadataHandlerTest
    extends BaseContentHandlerTest
{
    @Test
    void testExample()
    {
        final Content content = TestDataFixtures.newExampleContent();
        when( this.contentService.getByPath( Mockito.any() ) ).thenReturn( content );

        when( this.contentService.updateMetadata( Mockito.isA( UpdateContentMetadataParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdateMetadata( invocationOnMock.getArgument( 0 ), content ) );

        runScript( "/lib/xp/examples/content/updateMetadata.js" );
    }

    @Test
    void updateLanguage()
    {
        final Content content = TestDataFixtures.newSmallContent();
        when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        when( this.contentService.updateMetadata( isA( UpdateContentMetadataParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdateMetadata( invocationOnMock.getArgument( 0 ), content ) );

        runFunction( "/test/UpdateMetadataHandlerTest.js", "updateLanguage" );
    }

    @Test
    void unsetLanguage()
    {
        final Content content = TestDataFixtures.newSmallContent();
        when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        when( this.contentService.updateMetadata( isA( UpdateContentMetadataParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdateMetadata( invocationOnMock.getArgument( 0 ), content ) );

        runFunction( "/test/UpdateMetadataHandlerTest.js", "unsetLanguage" );
    }

    @Test
    void updateOwner()
    {
        final Content content = TestDataFixtures.newSmallContent();
        when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        when( this.contentService.updateMetadata( isA( UpdateContentMetadataParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdateMetadata( invocationOnMock.getArgument( 0 ), content ) );

        runFunction( "/test/UpdateMetadataHandlerTest.js", "updateOwner" );
    }

    @Test
    void updateLanguageAndOwner()
    {
        final Content content = TestDataFixtures.newSmallContent();
        when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        when( this.contentService.updateMetadata( isA( UpdateContentMetadataParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdateMetadata( invocationOnMock.getArgument( 0 ), content ) );

        runFunction( "/test/UpdateMetadataHandlerTest.js", "updateLanguageAndOwner" );
    }

    private UpdateContentMetadataResult invokeUpdateMetadata( final UpdateContentMetadataParams params, final Content content )
    {
        final EditableContentMetadata editableMetadata = new EditableContentMetadata( content );

        params.getEditor().edit( editableMetadata );

        final Content updatedContent = editableMetadata.build();

        return UpdateContentMetadataResult.create().content( updatedContent ).build();
    }
}
