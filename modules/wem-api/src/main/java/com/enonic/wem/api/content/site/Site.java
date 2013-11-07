package com.enonic.wem.api.content.site;

import com.google.common.collect.ImmutableList;

public class Site
{
    private final SiteTemplateId templateId;

    private final ImmutableList<ModuleConfig> moduleConfigs;

    public Site( final Builder builder )
    {
        this.templateId = builder.templateId;
        this.moduleConfigs = builder.moduleConfigs.build();
    }

    public SiteTemplateId getTemplateId()
    {
        return templateId;
    }

    public ImmutableList<ModuleConfig> getModuleConfigs()
    {
        return moduleConfigs;
    }

    public static Builder newSite()
    {
        return new Builder();
    }

    public static class Builder
    {

        private SiteTemplateId templateId;

        private ImmutableList.Builder<ModuleConfig> moduleConfigs = new ImmutableList.Builder<>();

        public Builder template( SiteTemplateId value )
        {
            this.templateId = value;
            return this;
        }

        public Builder addModuleConfig( ModuleConfig value )
        {
            moduleConfigs.add( value );
            return this;
        }

        public Site build()
        {
            return new Site( this );
        }

    }
}
