package com.enonic.xp.content;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class UpdateMetadataParams
{
    private final ContentId id;

    private final ContentMetadataEditor editor;

    private UpdateMetadataParams( final Builder builder )
    {
        this.id = builder.id;
        this.editor = builder.editor;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentId getContentId()
    {
        return id;
    }

    public ContentMetadataEditor getEditor()
    {
        return editor;
    }

    public static final class Builder
    {
        private ContentId id;

        private ContentMetadataEditor editor;

        private Builder()
        {
        }

        public Builder contentId( final ContentId id )
        {
            this.id = id;
            return this;
        }

        public Builder editor( final ContentMetadataEditor editor )
        {
            this.editor = editor;
            return this;
        }

        public UpdateMetadataParams build()
        {
            Objects.requireNonNull( this.id, "ContentId is required" );
            return new UpdateMetadataParams( this );
        }
    }
}
