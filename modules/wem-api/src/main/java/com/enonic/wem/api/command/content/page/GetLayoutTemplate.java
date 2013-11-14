package com.enonic.wem.api.command.content.page;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.LayoutTemplate;
import com.enonic.wem.api.content.page.LayoutTemplateName;

public class GetLayoutTemplate
    extends Command<LayoutTemplate>
{
    private LayoutTemplateName name;

    public GetLayoutTemplate()
    {
    }

    public GetLayoutTemplate byName( final LayoutTemplateName name )
    {
        this.name = name;
        return this;
    }

    public LayoutTemplateName getName()
    {
        return name;
    }

    @Override
    public void validate()
    {

    }
}
