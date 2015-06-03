package com.enonic.xp.content.site;


import com.google.common.annotations.Beta;

import com.enonic.xp.form.Form;
import com.enonic.xp.schema.mixin.MixinNames;

@Beta
public final class SiteDescriptor
{
    private final Form form;

    private final MixinNames metaSteps;

    private SiteDescriptor( final Builder builder )
    {
        this.form = builder.form;
        this.metaSteps = builder.metaSteps;
    }

    public Form getForm()
    {
        return form;
    }

    public MixinNames getMetaSteps()
    {
        return metaSteps;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder copyOf( final SiteDescriptor pageDescriptor )
    {
        return new Builder( pageDescriptor );
    }

    public static class Builder
    {
        private Form form;

        private MixinNames metaSteps;

        private Builder( final SiteDescriptor siteDescriptor )
        {
            this.form = siteDescriptor.getForm();
            this.metaSteps = siteDescriptor.getMetaSteps();
        }

        private Builder()
        {
        }

        public Builder form( final Form form )
        {
            this.form = form;
            return this;
        }

        public Builder metaSteps( final MixinNames metaSteps )
        {
            this.metaSteps = metaSteps;
            return this;
        }

        public SiteDescriptor build()
        {
            return new SiteDescriptor( this );
        }
    }
}
