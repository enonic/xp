package com.enonic.xp.repository;

import java.util.Objects;
import java.util.function.Consumer;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class UpdateRepositoryParams
{
    private final RepositoryId repositoryId;

    private final Consumer<EditableRepository> editor;

    private UpdateRepositoryParams( final Builder builder )
    {
        this.repositoryId = builder.repositoryId;
        this.editor = builder.editor;
    }

    public Consumer<EditableRepository> getEditor()
    {
        return editor;
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private RepositoryId repositoryId;

        private Consumer<EditableRepository> editor;

        private Builder()
        {
        }

        public Builder repositoryId( final RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return this;
        }

        public Builder editor( final Consumer<EditableRepository> editor )
        {
            this.editor = editor;
            return this;
        }

        private void validate()
        {
            Objects.requireNonNull( repositoryId, "repositoryId is required" );
        }


        public UpdateRepositoryParams build()
        {
            validate();
            return new UpdateRepositoryParams( this );
        }
    }
}
