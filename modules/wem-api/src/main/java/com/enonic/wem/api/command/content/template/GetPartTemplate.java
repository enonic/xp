package com.enonic.wem.api.command.content.template;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.PartTemplate;
import com.enonic.wem.api.content.page.PartTemplateId;

public class GetPartTemplate
    extends Command<PartTemplate>
{
    private PartTemplateId id;

    public GetPartTemplate()
    {
    }

    public GetPartTemplate templateId( final PartTemplateId id )
    {
        this.id = id;
        return this;
    }

    public PartTemplateId getId()
    {
        return id;
    }

    @Override
    public void validate()
    {

    }
}
