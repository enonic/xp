package com.enonic.xp.content;

import java.util.Objects;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
@NullMarked
public final class UpdateContentMetadataParams
{
    private final ContentId contentId;

    private final ContentMetadataEditor editor;

    private UpdateContentMetadataParams( final Builder builder )
    {
        this.contentId = Objects.requireNonNull( builder.contentId, "contentId is required" );
        this.editor = Objects.requireNonNull( builder.editor, "editor is required" );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public ContentMetadataEditor getEditor()
    {
        return editor;
    }

    public static final class Builder
    {
        private @Nullable ContentId contentId;

        private @Nullable ContentMetadataEditor editor;

        private Builder()
        {
        }

        public Builder contentId( final ContentId id )
        {
            this.contentId = id;
            return this;
        }

        public Builder editor( final ContentMetadataEditor editor )
        {
            this.editor = editor;
            return this;
        }

        public UpdateContentMetadataParams build()
        {
            return new UpdateContentMetadataParams( this );
        }
    }
}
