package com.enonic.wem.api.command.content.template;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.TemplateIds;
import com.enonic.wem.api.content.page.Templates;

public final class GetTemplates
    extends Command<Templates>
{
    private boolean getAllTemplates = false;

    private TemplateIds templateIds;

    public GetTemplates templates( final TemplateIds templateIds )
    {
        this.templateIds = templateIds;
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

    public TemplateIds getTemplates()
    {
        return templateIds;
    }

    @Override
    public void validate()
    {

    }
}
