package com.enonic.wem.api.command.content.template;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.PartTemplate;
import com.enonic.wem.api.content.page.PartTemplateName;

public class GetPartTemplate
    extends Command<PartTemplate>
{
    private PartTemplateName name;

    public GetPartTemplate()
    {
    }

    public GetPartTemplate templateName( final PartTemplateName name )
    {
        this.name = name;
        return this;
    }

    public PartTemplateName getId()
    {
        return name;
    }

    @Override
    public void validate()
    {

    }
}
