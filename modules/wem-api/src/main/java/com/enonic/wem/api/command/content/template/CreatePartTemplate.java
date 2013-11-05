package com.enonic.wem.api.command.content.template;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.PartTemplate;
import com.enonic.wem.api.content.page.PartTemplateId;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleResourceKey;

public final class CreatePartTemplate
    extends Command<PartTemplate>
{
    private PartTemplateId id;

    private String displayName;

    private ModuleResourceKey descriptor;

    private RootDataSet config;

    public CreatePartTemplate templateId( final PartTemplateId id )
    {
        this.id = id;
        return this;
    }

    public CreatePartTemplate displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreatePartTemplate descriptor( final ModuleResourceKey descriptor )
    {
        this.descriptor = descriptor;
        return this;
    }

    public CreatePartTemplate config( final RootDataSet config )
    {
        this.config = config;
        return this;
    }

    @Override
    public void validate()
    {
    }
}
