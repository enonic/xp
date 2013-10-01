package com.enonic.wem.api.content.page;


public class PageTemplate
{
    private PageTemplateId id;

    private PageTemplate( final Builder builder )
    {
        this.id = builder.id;
    }

    PageTemplateId id()
    {
        return id;
    }

    public static class Builder
    {
        private PageTemplateId id;

        public Builder id( final PageTemplateId value )
        {
            this.id = value;
            return this;
        }
    }
}
