package com.enonic.wem.api.content.page;


import com.enonic.wem.api.rendering.Component;

public abstract class PageComponent<ID extends TemplateId>
    implements Component
{
    ID templateId;

    public ID getTemplateId()
    {
        return templateId;
    }
}
