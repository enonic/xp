package com.enonic.wem.api.content.page;


import com.enonic.wem.api.form.Form;

public final class PageDescriptor
    implements ComponentDescriptor
{
    private final String displayName;

    private final ControllerSetup controllerSetup;

    /**
     * Only for display in PageTemplate.
     */
    private final Form pageTemplateConfig;

    /**
     * Only for display in Page.
     */
    private final Form config;

    /**
     * Only for display in LiveEdit.
     */
    private final Form liveEditConfig;

    private PageDescriptor( final Builder builder )
    {
        this.displayName = builder.displayName;
        this.controllerSetup = builder.controllerSetup;
        this.pageTemplateConfig = builder.pageTemplateConfig != null ? builder.pageTemplateConfig : Form.newForm().build();
        this.config = builder.config != null ? builder.config : Form.newForm().build();
        this.liveEditConfig = builder.liveEditConfig != null ? builder.liveEditConfig : Form.newForm().build();
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public ControllerSetup getControllerSetup()
    {
        return controllerSetup;
    }

    public Form getPageTemplateConfig()
    {
        return pageTemplateConfig;
    }

    public Form getConfig()
    {
        return config;
    }

    public Form getLiveEditConfig()
    {
        return liveEditConfig;
    }

    public static PageDescriptor.Builder newPageDescriptor()
    {
        return new Builder();
    }

    public static class Builder
    {
        private String displayName;

        private ControllerSetup controllerSetup;

        private Form pageTemplateConfig;

        private Form config;

        private Form liveEditConfig;

        private Builder()
        {
        }

        public Builder displayName( final String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public Builder controllerSetup( final ControllerSetup value )
        {
            this.controllerSetup = value;
            return this;
        }

        public Builder pageTemplateConfig( final Form value )
        {
            this.pageTemplateConfig = value;
            return this;
        }

        public Builder pageConfig( final Form value )
        {
            this.config = value;
            return this;
        }

        public Builder liveEditConfig( final Form value )
        {
            this.liveEditConfig = value;
            return this;
        }

        public PageDescriptor build()
        {
            return new PageDescriptor( this );
        }
    }
}
