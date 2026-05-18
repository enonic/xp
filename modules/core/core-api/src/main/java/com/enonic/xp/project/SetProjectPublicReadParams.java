package com.enonic.xp.project;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.content.ApplyPermissionsListener;

import static java.util.Objects.requireNonNull;


@NullMarked
public final class SetProjectPublicReadParams
{
    private final ProjectName name;

    private final boolean publicRead;

    private final @Nullable ApplyPermissionsListener listener;

    private SetProjectPublicReadParams( final Builder builder )
    {
        this.name = requireNonNull( builder.name, "name is required" );
        this.publicRead = builder.publicRead;
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

    public boolean isPublicRead()
    {
        return publicRead;
    }

    public @Nullable ApplyPermissionsListener getListener()
    {
        return listener;
    }

    public static final class Builder
    {
        private @Nullable ProjectName name;

        private boolean publicRead;

        private @Nullable ApplyPermissionsListener listener;

        private Builder()
        {
        }

        public Builder name( final ProjectName name )
        {
            this.name = name;
            return this;
        }

        public Builder publicRead( final boolean publicRead )
        {
            this.publicRead = publicRead;
            return this;
        }

        public Builder listener( final @Nullable ApplyPermissionsListener listener )
        {
            this.listener = listener;
            return this;
        }

        public SetProjectPublicReadParams build()
        {
            return new SetProjectPublicReadParams( this );
        }
    }
}
