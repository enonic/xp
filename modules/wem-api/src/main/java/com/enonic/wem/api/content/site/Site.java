package com.enonic.wem.api.content.site;

import com.google.common.collect.ImmutableList;

public class Site
{
    private final SiteTemplateName templateName;

    private final ImmutableList<ModuleConfig> moduleConfigs;

    public Site( final Builder builder )
    {
        this.templateName = builder.templateName;
        this.moduleConfigs = builder.moduleConfigs.build();
    }

    public SiteTemplateName getTemplateName()
    {
        return templateName;
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

        private SiteTemplateName templateName;

        private ImmutableList.Builder<ModuleConfig> moduleConfigs = new ImmutableList.Builder<>();

        public Builder template( SiteTemplateName value )
        {
            this.templateName = value;
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
