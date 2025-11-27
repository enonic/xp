package com.enonic.xp.site;


import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.schema.mixin.MixinName;

@PublicApi
public final class MixinMapping
{
    private final MixinName mixinName;

    private final String allowContentTypes;

    private final boolean optional;

    public MixinMapping( final Builder builder )
    {
        this.mixinName = builder.mixinName;
        this.allowContentTypes = builder.allowContentTypes;
        this.optional = builder.optional;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public MixinName getMixinName()
    {
        return mixinName;
    }

    public String getAllowContentTypes()
    {
        return allowContentTypes;
    }

    public boolean isOptional()
    {
        return optional;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final MixinMapping that = (MixinMapping) o;
        return Objects.equals( mixinName, that.mixinName ) && Objects.equals( allowContentTypes, that.allowContentTypes ) &&
            optional == that.optional;
    }

    @Override
    public int hashCode()
    {

        return Objects.hash( mixinName, allowContentTypes, optional );
    }

    public static final class Builder
    {
        private MixinName mixinName;

        private String allowContentTypes;

        private boolean optional;

        private Builder()
        {
        }

        public Builder mixinName( final MixinName mixinName )
        {
            this.mixinName = mixinName;
            return this;
        }

        public Builder allowContentTypes( final String allowContentTypes )
        {
            this.allowContentTypes = allowContentTypes;
            return this;
        }

        public Builder optional( final boolean optional )
        {
            this.optional = optional;
            return this;
        }

        private void validate()
        {
            Objects.requireNonNull( mixinName, "mixinName is required" );
        }

        public MixinMapping build()
        {
            validate();
            return new MixinMapping( this );
        }
    }
}
