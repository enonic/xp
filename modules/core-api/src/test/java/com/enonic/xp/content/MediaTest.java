package com.enonic.xp.content;

import java.time.Instant;

import org.junit.Test;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.attachment.ImageAttachmentScale;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.Assert.*;

public class MediaTest
{
    @Test
    public void getBestFitImageAttachment_notMedia()
    {
        Media media = Media.create().
            id( ContentId.from( "id" ) ).
            path( "/path/deep" ).
            createdTime( Instant.now() ).
            type( ContentTypeName.archiveMedia() ).
            displayName( "My Content" ).
            modifiedTime( Instant.now() ).
            build();

        assertNull( media.getBestFitImageAttachment( 256 ) );
    }

    @Test
    public void getBestFitImageAttachment_noAttachments()
    {
        Media media = Media.create().
            id( ContentId.from( "id" ) ).
            path( "/path/deep" ).
            createdTime( Instant.now() ).
            type( ContentTypeName.imageMedia() ).
            displayName( "My Content" ).
            modifiedTime( Instant.now() ).
            build();

        assertNull( media.getBestFitImageAttachment( 256 ) );
    }

    @Test
    public void getBestFitImageAttachment_onlySource()
    {
        Attachment source = Attachment.newAttachment().
            mimeType( "image/jpg" ).
            size( 1024 ).
            label( "source" ).
            name( "MyImage.jpg" ).
            build();

        Media media = Media.create().
            id( ContentId.from( "id" ) ).
            path( "/path/deep" ).
            createdTime( Instant.now() ).
            type( ContentTypeName.imageMedia() ).
            displayName( "My Content" ).
            modifiedTime( Instant.now() ).
            attachments( Attachments.from( source ) ).
            build();

        assertEquals( source, media.getBestFitImageAttachment( 256 ) );
    }

    @Test
    public void getBestFitImageAttachment_outOfBounds()
    {
        Media media = Media.create().
            id( ContentId.from( "id" ) ).
            path( "/path/deep" ).
            createdTime( Instant.now() ).
            type( ContentTypeName.imageMedia() ).
            displayName( "My Content" ).
            modifiedTime( Instant.now() ).
            attachments( generateAllSizesAttachments() ).
            build();

        assertEquals( "source", media.getBestFitImageAttachment( 3000 ).getLabel() );
        assertEquals( "small", media.getBestFitImageAttachment( -1 ).getLabel() );
    }

    @Test
    public void getBestFitImageAttachment()
    {
        Media media = Media.create().
            id( ContentId.from( "id" ) ).
            path( "/path/deep" ).
            createdTime( Instant.now() ).
            type( ContentTypeName.imageMedia() ).
            displayName( "My Content" ).
            modifiedTime( Instant.now() ).
            attachments( generateAllSizesAttachments() ).
            build();

        for ( ImageAttachmentScale scale : ImageAttachmentScale.getScalesOrderedBySizeAsc() )
        {
            assertEquals( scale.getLabel(), media.getBestFitImageAttachment( scale.getSize() - 1 ).getLabel() );
        }
    }

    private Attachments generateAllSizesAttachments()
    {
        Attachment source = Attachment.newAttachment().
            mimeType( "image/jpg" ).
            size( 10709855 ).
            label( "source" ).
            name( "MyImage.jpg" ).
            build();

        Attachment small = Attachment.newAttachment().
            mimeType( "image/jpg" ).
            size( 24217 ).
            label( "small" ).
            name( "MyImage_small.jpg" ).
            build();

        Attachment medium = Attachment.newAttachment().
            mimeType( "image/jpg" ).
            size( 94293 ).
            label( "medium" ).
            name( "MyImage_medium.jpg" ).
            build();

        Attachment large = Attachment.newAttachment().
            mimeType( "image/jpg" ).
            size( 363595 ).
            label( "large" ).
            name( "MyImage_large.jpg" ).
            build();

        Attachment extra_large = Attachment.newAttachment().
            mimeType( "image/jpg" ).
            size( 1248953 ).
            label( "extra-large" ).
            name( "MyImage_extra-large.jpg" ).
            build();

        return Attachments.from( source, small, medium, large, extra_large );
    }

}
