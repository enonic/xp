package com.enonic.xp.project;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.attachment.CreateAttachment;

import static java.util.Objects.requireNonNull;


@NullMarked
public final class ModifyProjectIconParams
{
    private final ProjectName name;

    private final @Nullable CreateAttachment icon;

    private final int scaleWidth;

    private ModifyProjectIconParams( final Builder builder )
    {
        this.name = requireNonNull( builder.name, "name is required" );
        this.icon = builder.icon;
        this.scaleWidth = builder.scaleWidth;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ProjectName getName()
    {
        return name;
    }

    public @Nullable CreateAttachment getIcon()
    {
        return icon;
    }

    public int getScaleWidth()
    {
        return scaleWidth;
    }

    public static final class Builder
    {
        private @Nullable ProjectName name;

        private @Nullable CreateAttachment icon;

        private int scaleWidth;

        private Builder()
        {
        }

        public Builder name( final ProjectName name )
        {
            this.name = name;
            return this;
        }

        public Builder icon( final @Nullable CreateAttachment icon )
        {
            this.icon = icon;
            return this;
        }

        public Builder scaleWidth( final int scaleWidth )
        {
            this.scaleWidth = scaleWidth;
            return this;
        }

        public ModifyProjectIconParams build()
        {
            return new ModifyProjectIconParams( this );
        }
    }
}
