package com.enonic.xp.site;


import com.google.common.annotations.Beta;

import com.enonic.xp.form.Form;
import com.enonic.xp.schema.mixin.MixinNames;
import com.enonic.xp.site.filter.FilterDescriptors;

@Beta
public final class SiteDescriptor
{
    private final Form form;

    private final MixinNames metaSteps;

    private final FilterDescriptors filterDescriptors;

    private SiteDescriptor( final Builder builder )
    {
        this.form = builder.form;
        this.metaSteps = builder.metaSteps;
        this.filterDescriptors = builder.filterDescriptors;
    }

    public Form getForm()
    {
        return form;
    }

    public MixinNames getMetaSteps()
    {
        return metaSteps;
    }

    public FilterDescriptors getFilterDescriptors()
    {
        return filterDescriptors;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private Form form;

        private MixinNames metaSteps;

        private FilterDescriptors filterDescriptors;

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

        public Builder filterDescriptors( final FilterDescriptors filterDescriptors )
        {
            this.filterDescriptors = filterDescriptors;
            return this;
        }

        public SiteDescriptor build()
        {
            return new SiteDescriptor( this );
        }
    }
}
