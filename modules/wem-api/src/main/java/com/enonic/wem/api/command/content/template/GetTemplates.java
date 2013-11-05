package com.enonic.wem.api.command.content.template;


import java.util.List;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.Template;
import com.enonic.wem.api.content.page.TemplateId;

public final class GetTemplates
    extends Command<List<Template>>
{
    private boolean getAllTemplates = false;

    private List<TemplateId> templateIds;

    public GetTemplates modules( final List<TemplateId> templateIds )
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

    public List<TemplateId> getTemplates()
    {
        return templateIds;
    }

    @Override
    public void validate()
    {

    }
}
