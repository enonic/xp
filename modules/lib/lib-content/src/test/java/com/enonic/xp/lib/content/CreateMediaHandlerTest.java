package com.enonic.xp.lib.content;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAlreadyExistsException;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateMediaParams;
import com.enonic.xp.content.Media;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateMediaHandlerTest
    extends BaseContentHandlerTest
{
    @Test
    void testExample()
    {
        Mockito.when( this.contentService.create( Mockito.any( CreateMediaParams.class ) ) ).thenAnswer(
            mock -> createContent( (CreateMediaParams) mock.getArguments()[0] ) );

        runScript( "/lib/xp/examples/content/createMedia.js" );
    }

    @Test
    void createMedia()
    {
        Mockito.when( this.contentService.create( Mockito.any( CreateMediaParams.class ) ) ).thenAnswer(
            mock -> createContent( (CreateMediaParams) mock.getArguments()[0] ) );

        runFunction( "/test/CreateMediaHandlerTest.js", "createMedia" );

        final ArgumentCaptor<CreateMediaParams> argumentCaptor = ArgumentCaptor.forClass( CreateMediaParams.class );
        Mockito.verify( this.contentService, Mockito.times( 1 ) ).create( argumentCaptor.capture() );

        assertEquals( 0.5, argumentCaptor.getValue().getFocalX(), 0 );
        assertEquals( 0.5, argumentCaptor.getValue().getFocalY(), 0 );
    }

    @Test
    void createMediaWithFocalPoints()
    {
        Mockito.when( this.contentService.create( Mockito.any( CreateMediaParams.class ) ) ).thenAnswer(
            mock -> createContent( (CreateMediaParams) mock.getArguments()[0] ) );

        runFunction( "/test/CreateMediaHandlerTest.js", "createMediaWithFocalPoints" );

        final ArgumentCaptor<CreateMediaParams> argumentCaptor = ArgumentCaptor.forClass( CreateMediaParams.class );
        Mockito.verify( this.contentService, Mockito.times( 1 ) ).create( argumentCaptor.capture() );

        assertEquals( 0.3, argumentCaptor.getValue().getFocalX(), 0 );
        assertEquals( 0.1, argumentCaptor.getValue().getFocalY(), 0 );
    }

    @Test
    void createMediaAsPDFDocument()
    {
        Mockito.when( this.contentService.create( Mockito.any( CreateMediaParams.class ) ) ).thenAnswer( mock -> {
            final CreateMediaParams params = (CreateMediaParams) mock.getArguments()[0];

            final PropertyTree propertyTree = new PropertyTree();
            final PropertySet attachmentSet = propertyTree.newSet();
            attachmentSet.addString( "attachment", params.getName().toString() );

            propertyTree.addSet( "media", attachmentSet );

            return Media.create().
                id( ContentId.from( "dbc077af-fb97-4b17-a567-ad69e85f1010" ) ).
                name( params.getName() ).
                parentPath( params.getParent() ).
                type( ContentTypeName.documentMedia() ).
                displayName( params.getName().toString() ).
                valid( true ).
                creator( PrincipalKey.ofAnonymous() ).
                data( propertyTree ).
                attachments( Attachments.create().
                    add( Attachment.create().
                        name( "documentName.pdf" ).
                        label( "source" ).
                        mimeType( "application/pdf" ).
                        size( 653453 ).
                        build() ).
                    build() ).
                createdTime( Instant.parse( "1975-01-08T00:00:00Z" ) ).build();
        } );

        runFunction( "/test/CreateMediaHandlerTest.js", "createMediaAsPDF" );
    }

    private Content createContent( final CreateMediaParams params )
    {
        final Content.Builder builder = Content.create();
        builder.id( ContentId.from( "123456" ) );
        builder.name( params.getName() );
        builder.parentPath( params.getParent() );
        builder.valid( false );
        builder.creator( PrincipalKey.ofAnonymous() );
        builder.createdTime( Instant.parse( "1975-01-08T00:00:00Z" ) );
        return builder.build();
    }

    @Test
    void createMediaAutoGenerateNameWithExistingName()
    {
        final ContentAlreadyExistsException exception =
            new ContentAlreadyExistsException( ContentPath.from( "/a/b/my-content.jpg" ), RepositoryId.from( "some.repo" ),
                                               Branch.from( "draft" ) );
        Mockito.when( this.contentService.create( Mockito.any( CreateMediaParams.class ) ) ).thenThrow( exception ).
            thenAnswer( mock -> createContent( (CreateMediaParams) mock.getArguments()[0] ) );

        Mockito.when( this.contentService.contentExists( Mockito.eq( ContentPath.from( "/a/b/my-content.jpg" ) ) ) ).thenReturn( true );
        Mockito.when( this.contentService.contentExists( Mockito.eq( ContentPath.from( "/a/b/my-content-1.jpg" ) ) ) ).thenReturn( true );
        Mockito.when( this.contentService.contentExists( Mockito.eq( ContentPath.from( "/a/b/my-content-2.jpg" ) ) ) ).thenReturn( true );

        runFunction( "/test/CreateMediaHandlerTest.js", "createMediaAutoGenerateName" );
    }

    public static ByteSource createByteSource( final String value )
    {
        return ByteSource.wrap( value.getBytes() );
    }
}
