package com.enonic.wem.api.command.content.page;


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

    public GetPartTemplate byName( final PartTemplateName name )
    {
        this.name = name;
        return this;
    }

    public PartTemplateName getName()
    {
        return name;
    }

    @Override
    public void validate()
    {

    }
}
