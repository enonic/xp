package com.enonic.xp.page;


import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.schema.content.ContentTypeName;

@PublicApi
public final class GetDefaultPageTemplateParams
{
    private final ContentId site;

    private final ContentPath sitePath;

    private final ContentTypeName contentType;

    public GetDefaultPageTemplateParams( final Builder builder )
    {
        site = builder.site;
        sitePath = builder.sitePath;
        contentType = builder.contentType;
    }

    public ContentId getSite()
    {
        return site;
    }

    public ContentPath getSitePath()
    {
        return sitePath;
    }

    public ContentTypeName getContentType()
    {
        return contentType;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private ContentId site;

        private ContentPath sitePath;

        private ContentTypeName contentType;

        private Builder()
        {
        }

        public Builder site( final ContentId site )
        {
            this.site = site;
            return this;
        }

        public Builder sitePath( final ContentPath sitePath )
        {
            this.sitePath = sitePath;
            return this;
        }

        public Builder contentType( final ContentTypeName contentType )
        {
            this.contentType = contentType;
            return this;
        }

        public GetDefaultPageTemplateParams build()
        {
            return new GetDefaultPageTemplateParams( this );
        }

    }
}
