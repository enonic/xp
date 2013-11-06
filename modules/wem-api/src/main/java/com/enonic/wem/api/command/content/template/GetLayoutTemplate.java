package com.enonic.wem.api.command.content.template;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.LayoutTemplate;
import com.enonic.wem.api.content.page.LayoutTemplateId;

public class GetLayoutTemplate
    extends Command<LayoutTemplate>
{
    private LayoutTemplateId id;

    public GetLayoutTemplate()
    {
    }

    public GetLayoutTemplate templateId( final LayoutTemplateId id )
    {
        this.id = id;
        return this;
    }

    public LayoutTemplateId getId()
    {
        return id;
    }

    @Override
    public void validate()
    {

    }
}
