package com.enonic.xp.lib.content;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.EditableContentMetadata;
import com.enonic.xp.content.UpdateMetadataParams;
import com.enonic.xp.content.UpdateMetadataResult;

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

        when( this.contentService.updateMetadata( Mockito.isA( UpdateMetadataParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdateMetadata( (UpdateMetadataParams) invocationOnMock.getArguments()[0], content ) );

        runScript( "/lib/xp/examples/content/updateMetadata.js" );
    }

    @Test
    void updateLanguage()
    {
        final Content content = TestDataFixtures.newSmallContent();
        when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        when( this.contentService.updateMetadata( isA( UpdateMetadataParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdateMetadata( (UpdateMetadataParams) invocationOnMock.getArguments()[0], content ) );

        runFunction( "/test/UpdateMetadataHandlerTest.js", "updateLanguage" );
    }

    @Test
    void updateOwner()
    {
        final Content content = TestDataFixtures.newSmallContent();
        when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        when( this.contentService.updateMetadata( isA( UpdateMetadataParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdateMetadata( (UpdateMetadataParams) invocationOnMock.getArguments()[0], content ) );

        runFunction( "/test/UpdateMetadataHandlerTest.js", "updateOwner" );
    }

    @Test
    void updateLanguageAndOwner()
    {
        final Content content = TestDataFixtures.newSmallContent();
        when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        when( this.contentService.updateMetadata( isA( UpdateMetadataParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdateMetadata( (UpdateMetadataParams) invocationOnMock.getArguments()[0], content ) );

        runFunction( "/test/UpdateMetadataHandlerTest.js", "updateLanguageAndOwner" );
    }

    private UpdateMetadataResult invokeUpdateMetadata( final UpdateMetadataParams params, final Content content )
    {
        final EditableContentMetadata editableMetadata = new EditableContentMetadata( content );
        
        if ( params.getEditor() != null )
        {
            params.getEditor().edit( editableMetadata );
        }

        final Content updatedContent = editableMetadata.build();

        // Always returns both draft and master branches
        return UpdateMetadataResult.create()
            .contentId( updatedContent.getId() )
            .addResult( Branch.from( "draft" ), updatedContent )
            .addResult( Branch.from( "master" ), updatedContent )
            .build();
    }
}
