package com.enonic.xp.portal.url;

import com.google.common.collect.Multimap;

import com.enonic.wem.api.content.ContentId;

import static com.google.common.base.Strings.emptyToNull;

public final class AttachmentUrlBuilder
    extends PortalUrlBuilder<AttachmentUrlBuilder>
{
    private String mediaId;

    private String name;

    private String label;

    public AttachmentUrlBuilder mediaId( final String value )
    {
        this.mediaId = emptyToNull( value );
        return this;
    }

    public AttachmentUrlBuilder mediaId( final ContentId value )
    {
        return mediaId( value != null ? value.toString() : null );
    }

    public AttachmentUrlBuilder name( final String value )
    {
        this.name = emptyToNull( value );
        return this;
    }

    public AttachmentUrlBuilder label( final String value )
    {
        this.label = emptyToNull( value );
        return this;
    }

    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        super.buildUrl( url, params );

        appendPart( url, "_" );
        appendPart( url, "attachment" );

        if ( this.mediaId != null )
        {
            appendPart( url, "id" );
            appendPart( url, this.mediaId );
        }

        if ( this.name != null )
        {
            appendPart( url, this.name );
        }
        else if ( this.label != null )
        {
            appendPart( url, this.label );
        }
    }
}
