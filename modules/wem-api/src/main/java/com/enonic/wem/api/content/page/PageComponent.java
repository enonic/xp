package com.enonic.wem.api.content.page;


public abstract class PageComponent<ID extends TemplateId>
    implements Component
{
    private final ID templateId;

    protected PageComponent( final ID templateId )
    {
        this.templateId = templateId;
    }

    public ID getTemplateId()
    {
        return templateId;
    }
}
