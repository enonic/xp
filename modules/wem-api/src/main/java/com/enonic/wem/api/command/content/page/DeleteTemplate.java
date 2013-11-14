package com.enonic.wem.api.command.content.page;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.TemplateName;

public final class DeleteTemplate
    extends Command<Boolean>
{
    private TemplateName templateName;

    public DeleteTemplate byName( TemplateName templateName )
    {
        this.templateName = templateName;
        return this;
    }

    @Override
    public void validate()
    {
    }
}
