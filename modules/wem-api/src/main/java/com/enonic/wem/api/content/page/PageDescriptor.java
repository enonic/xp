package com.enonic.wem.api.content.page;


import com.enonic.wem.api.form.Form;

public final class PageDescriptor
    implements ComponentDescriptor
{
    private final String displayName;

    private final ControllerSetup controllerSetup;

    private final Form config;

    private PageDescriptor( final Builder builder )
    {
        this.displayName = builder.displayName;
        this.controllerSetup = builder.controllerSetup;
        this.config = builder.config != null ? builder.config : Form.newForm().build();
    }

    @Override
    public String getDisplayName()
    {
        return displayName;
    }

    public ControllerSetup getControllerSetup()
    {
        return controllerSetup;
    }

    @Override
    public Form getConfig()
    {
        return config;
    }

    public static PageDescriptor.Builder newPageDescriptor()
    {
        return new Builder();
    }

    public static class Builder
    {
        private String displayName;

        private ControllerSetup controllerSetup;

        private Form config;

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

        public Builder config( final Form value )
        {
            this.config = value;
            return this;
        }

        public PageDescriptor build()
        {
            return new PageDescriptor( this );
        }
    }
}
