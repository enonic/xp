package com.enonic.xp.project;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.content.ApplyPermissionsListener;

import static java.util.Objects.requireNonNull;


@NullMarked
public final class SetProjectReadAccessParams
{
    private final ProjectName name;

    private final boolean isPublic;

    private final @Nullable ApplyPermissionsListener listener;

    private SetProjectReadAccessParams( final Builder builder )
    {
        this.name = requireNonNull( builder.name, "name is required" );
        this.isPublic = builder.isPublic;
        this.listener = builder.listener;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ProjectName getName()
    {
        return name;
    }

    public boolean isPublic()
    {
        return isPublic;
    }

    public @Nullable ApplyPermissionsListener getListener()
    {
        return listener;
    }

    public static final class Builder
    {
        private @Nullable ProjectName name;

        private boolean isPublic;

        private @Nullable ApplyPermissionsListener listener;

        private Builder()
        {
        }

        public Builder name( final ProjectName name )
        {
            this.name = name;
            return this;
        }

        public Builder isPublic( final boolean isPublic )
        {
            this.isPublic = isPublic;
            return this;
        }

        public Builder listener( final @Nullable ApplyPermissionsListener listener )
        {
            this.listener = listener;
            return this;
        }

        public SetProjectReadAccessParams build()
        {
            return new SetProjectReadAccessParams( this );
        }
    }
}
