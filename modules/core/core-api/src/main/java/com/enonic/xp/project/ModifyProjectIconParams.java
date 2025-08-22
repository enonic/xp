package com.enonic.xp.project;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.attachment.CreateAttachment;

@PublicApi
public final class ModifyProjectIconParams
{
    private final ProjectName name;

    private final CreateAttachment icon;

    private final int scaleWidth;

    private ModifyProjectIconParams( final Builder builder )
    {
        this.name = builder.name;
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

    public CreateAttachment getIcon()
    {
        return icon;
    }

    public int getScaleWidth()
    {
        return scaleWidth;
    }

    public static final class Builder
    {
        private ProjectName name;

        private CreateAttachment icon;

        private int scaleWidth;

        private Builder()
        {
        }

        public Builder name( final ProjectName name )
        {
            this.name = name;
            return this;
        }

        public Builder icon( final CreateAttachment icon )
        {
            this.icon = icon;
            return this;
        }

        public Builder scaleWidth( final int scaleWidth )
        {
            this.scaleWidth = scaleWidth;
            return this;
        }

        private void validate()
        {
            Objects.requireNonNull( name, "name is required" );
        }

        public ModifyProjectIconParams build()
        {
            validate();
            return new ModifyProjectIconParams( this );
        }
    }
}
