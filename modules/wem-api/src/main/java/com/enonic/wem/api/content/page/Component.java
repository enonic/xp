package com.enonic.wem.api.content.page;


public abstract class Component<NAME extends TemplateName>
    implements Renderable
{
    private final NAME templateName;

    protected Component( final NAME templateName )
    {
        this.templateName = templateName;
    }

    public NAME getTemplateName()
    {
        return templateName;
    }
}
