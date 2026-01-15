package com.enonic.xp.content;

import java.util.Objects;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
@NullMarked
public final class UpdateWorkflowParams
{
    private final ContentId contentId;

    private final WorkflowEditor editor;

    private UpdateWorkflowParams( final Builder builder )
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

    public WorkflowEditor getEditor()
    {
        return editor;
    }

    public static final class Builder
    {
        @Nullable
        private ContentId contentId;

        @Nullable
        private WorkflowEditor editor;

        private Builder()
        {
        }

        public Builder contentId( final ContentId id )
        {
            this.contentId = id;
            return this;
        }

        public Builder editor( final WorkflowEditor editor )
        {
            this.editor = editor;
            return this;
        }

        public UpdateWorkflowParams build()
        {
            return new UpdateWorkflowParams( this );
        }
    }
}
