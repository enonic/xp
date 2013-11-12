package com.enonic.wem.api.content.page;


import com.enonic.wem.api.rendering.Component;

public abstract class PageComponent<NAME extends TemplateName>
    implements Component
{
    private final NAME templateName;

    protected PageComponent( final NAME templateName )
    {
        this.templateName = templateName;
    }

    public NAME getTemplateName()
    {
        return templateName;
    }
}
