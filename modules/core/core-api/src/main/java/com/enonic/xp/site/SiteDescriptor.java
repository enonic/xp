package com.enonic.xp.site;


import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.schema.xdata.XDataNames;
import com.enonic.xp.site.filter.FilterDescriptors;
import com.enonic.xp.site.mapping.ControllerMappingDescriptors;

@Beta
public final class SiteDescriptor
{
    private static final String SITE_DESCRIPTOR_PATH = "site/site.xml";

    private final Form form;

    private final XDataNames metaSteps;

    private final FilterDescriptors filterDescriptors;

    private final ControllerMappingDescriptors mappingDescriptors;

    private SiteDescriptor( final Builder builder )
    {
        this.form = builder.form;
        this.metaSteps = builder.metaSteps;
        this.filterDescriptors = builder.filterDescriptors != null ? builder.filterDescriptors : FilterDescriptors.empty();
        this.mappingDescriptors = builder.mappingDescriptors != null ? builder.mappingDescriptors : ControllerMappingDescriptors.empty();
    }

    public Form getForm()
    {
        return form;
    }

    public XDataNames getMetaSteps()
    {
        return metaSteps;
    }

    public FilterDescriptors getFilterDescriptors()
    {
        return filterDescriptors;
    }

    public ControllerMappingDescriptors getMappingDescriptors()
    {
        return mappingDescriptors;
    }

    public static ResourceKey toResourceKey( final ApplicationKey applicationKey )
    {
        return ResourceKey.from( applicationKey, SITE_DESCRIPTOR_PATH );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static SiteDescriptor.Builder copyOf( final SiteDescriptor siteDescriptor )
    {
        return new Builder( siteDescriptor );
    }

    public static class Builder
    {
        private Form form;

        private XDataNames metaSteps;

        private FilterDescriptors filterDescriptors;

        private ControllerMappingDescriptors mappingDescriptors;

        private Builder()
        {
        }

        private Builder( final SiteDescriptor siteDescriptor )
        {
            this.form = siteDescriptor.form != null ? siteDescriptor.form.copy() : null;
            this.metaSteps = siteDescriptor.metaSteps;
            this.filterDescriptors = siteDescriptor.filterDescriptors;
            this.mappingDescriptors = siteDescriptor.mappingDescriptors;
        }

        public Builder form( final Form form )
        {
            this.form = form;
            return this;
        }

        public Builder metaSteps( final XDataNames metaSteps )
        {
            this.metaSteps = metaSteps;
            return this;
        }

        public Builder filterDescriptors( final FilterDescriptors filterDescriptors )
        {
            this.filterDescriptors = filterDescriptors;
            return this;
        }

        public Builder mappingDescriptors( final ControllerMappingDescriptors mappingDescriptors )
        {
            this.mappingDescriptors = mappingDescriptors;
            return this;
        }

        public SiteDescriptor build()
        {
            return new SiteDescriptor( this );
        }
    }
}
