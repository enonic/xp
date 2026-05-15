package com.enonic.xp.project;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;


@NullMarked
public final class ModifyProjectParams
{
    private final ProjectName name;

    private final ProjectEditor editor;

    private ModifyProjectParams( final Builder builder )
    {
        this.name = requireNonNull( builder.name, "name is required" );
        this.editor = requireNonNull( builder.editor, "editor is required" );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ProjectName getName()
    {
        return name;
    }

    public ProjectEditor getEditor()
    {
        return editor;
    }

    public static final class Builder
    {
        private @Nullable ProjectName name;

        private @Nullable ProjectEditor editor;

        private Builder()
        {
        }

        public Builder name( final ProjectName name )
        {
            this.name = name;
            return this;
        }

        public Builder editor( final ProjectEditor editor )
        {
            this.editor = editor;
            return this;
        }

        public ModifyProjectParams build()
        {
            return new ModifyProjectParams( this );
        }
    }
}
