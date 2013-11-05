package com.enonic.wem.api.command.content.template;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.LayoutTemplateId;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleResourceKey;

public class CreateLayoutTemplate
    extends Command<PageTemplate>
{
    private LayoutTemplateId id;

    private String displayName;

    private ModuleResourceKey descriptor;

    private RootDataSet config;

    public CreateLayoutTemplate templateId( final LayoutTemplateId id )
    {
        this.id = id;
        return this;
    }

    public CreateLayoutTemplate displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreateLayoutTemplate descriptor( final ModuleResourceKey descriptor )
    {
        this.descriptor = descriptor;
        return this;
    }

    public CreateLayoutTemplate config( final RootDataSet config )
    {
        this.config = config;
        return this;
    }

    @Override
    public void validate()
    {
    }
}
