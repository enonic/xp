package com.enonic.wem.api.command.template;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.Template;
import com.enonic.wem.api.content.page.TemplateId;

public class GetTemplate<T extends Template>
    extends Command<T>
{
    private TemplateId id;

    public GetTemplate( final TemplateId id )
    {
        this.id = id;
    }

    public TemplateId getId()
    {
        return id;
    }

    @Override
    public void validate()
    {

    }
}
