package com.enonic.xp.schema.content;

import static java.util.Objects.requireNonNull;


public final class GetContentTypeParams
{
    private ContentTypeName contentTypeName;

    public static GetContentTypeParams from( final ContentTypeName contentTypeName )
    {
        return new GetContentTypeParams().contentTypeName( contentTypeName );
    }

    public ContentTypeName getContentTypeName()
    {
        return this.contentTypeName;
    }

    public GetContentTypeParams contentTypeName( final ContentTypeName contentTypeName )
    {
        this.contentTypeName = contentTypeName;
        return this;
    }

    public GetContentTypeParams contentTypeName( final String value )
    {
        this.contentTypeName = ContentTypeName.from( value );
        return this;
    }

    public void validate()
    {
        requireNonNull( this.contentTypeName, "contentTypeName is required" );
    }
}
