package com.enonic.wem.api.content.page;


public abstract class Component<ID extends TemplateId>
    implements Renderable
{
    private final ID templateId;

    protected Component( final ID templateId )
    {
        this.templateId = templateId;
    }

    public ID getTemplateId()
    {
        return templateId;
    }
}
