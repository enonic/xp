package com.enonic.wem.api.content.page;


import com.enonic.wem.api.rendering.Component;

public abstract class BasePageComponent<NAME extends TemplateName>
    implements Component
{
    private final NAME template;

    protected BasePageComponent( final NAME template )
    {
        this.template = template;
    }

    public NAME getTemplate()
    {
        return template;
    }
}
