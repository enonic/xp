package com.enonic.xp.page;


import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.schema.content.ContentTypeName;

@PublicApi
public class GetDefaultPageTemplateParams
{
    private final ContentId site;

    private final ContentTypeName contentType;

    public GetDefaultPageTemplateParams( final Builder builder )
    {
        site = builder.site;
        contentType = builder.contentType;
    }

    public ContentId getSite()
    {
        return site;
    }

    public ContentTypeName getContentType()
    {
        return contentType;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ContentId site;

        private ContentTypeName contentType;

        public Builder site( final ContentId site )
        {
            this.site = site;
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
