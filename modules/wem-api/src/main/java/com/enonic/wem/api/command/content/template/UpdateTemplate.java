package com.enonic.wem.api.command.content.template;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.TemplateId;

public class UpdateTemplate
    extends Command<Boolean>
{
    private TemplateId templateId;

    private TemplateEditor editor;

    public UpdateTemplate templateId( final TemplateId templateId )
    {
        this.templateId = templateId;
        return this;
    }

    public UpdateTemplate editor( final TemplateEditor editor )
    {
        this.editor = editor;
        return this;
    }

    @Override
    public void validate()
    {
    }
}
