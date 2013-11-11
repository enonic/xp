package com.enonic.wem.api.command.content.template;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.TemplateNames;
import com.enonic.wem.api.content.page.Templates;

public final class GetTemplates
    extends Command<Templates>
{
    private boolean getAllTemplates = false;

    private TemplateNames templateNames;

    public GetTemplates templates( final TemplateNames templateNames )
    {
        this.templateNames = templateNames;
        return this;
    }

    public GetTemplates all()
    {
        getAllTemplates = true;
        return this;
    }

    boolean isGetAll()
    {
        return getAllTemplates;
    }

    public TemplateNames getTemplates()
    {
        return templateNames;
    }

    @Override
    public void validate()
    {

    }
}
