package com.enonic.xp.content;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.enonic.xp.content.attachment.Attachment;

public class Media
    extends Content
{
    protected Media( final Builder builder )
    {
        super( builder );
    }

    public boolean isImage()
    {
        return getType().isImageMedia();
    }

    public Attachment getMediaAttachment()
    {
        final String mediaAttachmentName = getData().getString( ContentPropertyNames.MEDIA );
        if ( mediaAttachmentName == null )
        {
            return null;
        }

        return getAttachments().byName( mediaAttachmentName );
    }

    public Attachment getMediaAttachment(int size)
    {
        List<Attachment> sortedBySize = new ArrayList<>( getAttachments().getList() );
        Collections.sort( sortedBySize, (a, b) ->  (int)(a.getSize() - b.getSize())  );

        for(Attachment attachment: sortedBySize) {
            if((size * size) < attachment.getSize())
                return attachment;
        }

        final String mediaAttachmentName = getData().getString( ContentPropertyNames.MEDIA );
        if ( mediaAttachmentName == null )
        {
            return null;
        }

        return getAttachments().byName( mediaAttachmentName );
    }

    public Attachment getSourceAttachment()
    {
        return getAttachments().byLabel( "source" );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        return super.equals( o );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final Media source )
    {
        return new Builder( source );
    }

    public static class Builder
        extends Content.Builder<Builder, Media>
    {

        public Builder( final Media source )
        {
            super( source );
        }

        public Builder()
        {
            super();
        }

        public Media build()
        {
            return new Media( this );
        }
    }
}
