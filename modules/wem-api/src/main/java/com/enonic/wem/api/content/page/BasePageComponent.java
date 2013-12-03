package com.enonic.wem.api.content.page;


import com.enonic.wem.api.rendering.Component;

public abstract class BasePageComponent<TEMPLATE extends TemplateKey>
    implements Component
{
    private String id;

    private final TEMPLATE template;

    protected BasePageComponent( final TEMPLATE template )
    {
        this.template = template;
    }

    public TEMPLATE getTemplate()
    {
        return template;
    }
}
