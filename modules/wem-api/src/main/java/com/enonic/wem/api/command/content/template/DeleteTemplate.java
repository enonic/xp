package com.enonic.wem.api.command.content.template;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.TemplateId;

public final class DeleteTemplate
    extends Command<Boolean>
{
    private TemplateId templateId;

    public DeleteTemplate template( TemplateId templateId )
    {
        this.templateId = templateId;
        return this;
    }

    @Override
    public void validate()
    {
    }
}
