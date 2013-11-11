package com.enonic.wem.api.command.content.template;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.TemplateName;

public final class DeleteTemplate
    extends Command<Boolean>
{
    private TemplateName templateName;

    public DeleteTemplate template( TemplateName templateName )
    {
        this.templateName = templateName;
        return this;
    }

    @Override
    public void validate()
    {
    }
}
