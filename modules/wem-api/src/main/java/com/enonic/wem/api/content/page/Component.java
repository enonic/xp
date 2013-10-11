package com.enonic.wem.api.content.page;


public abstract class Component<ID extends TemplateId>
{
    ID templateId;

    public ID getTemplateId()
    {
        return templateId;
    }
}
