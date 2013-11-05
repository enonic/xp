package com.enonic.wem.api.command.content.template;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.Template;
import com.enonic.wem.api.content.page.TemplateId;

public class GetTemplate
    extends Command<Template>
{
    private TemplateId id;

    public GetTemplate()
    {
    }

    public GetTemplate templateId( final TemplateId id )
    {
        this.id = id;
        return this;
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
