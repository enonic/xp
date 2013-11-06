package com.enonic.wem.api.command.content.template;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.PartTemplate;
import com.enonic.wem.api.content.page.PartTemplateId;

public final class UpdatePartTemplate
    extends Command<Boolean>
{
    private PartTemplateId templateId;

    private TemplateEditor<PartTemplate> editor;

    public UpdatePartTemplate templateId( final PartTemplateId templateId )
    {
        this.templateId = templateId;
        return this;
    }

    public UpdatePartTemplate editor( final TemplateEditor<PartTemplate> editor )
    {
        this.editor = editor;
        return this;
    }

    @Override
    public void validate()
    {
    }
}
