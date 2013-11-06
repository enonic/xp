package com.enonic.wem.api.command.content.template;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.LayoutTemplate;
import com.enonic.wem.api.content.page.LayoutTemplateId;

public final class UpdateLayoutTemplate
    extends Command<Boolean>
{
    private LayoutTemplateId templateId;

    private TemplateEditor<LayoutTemplate> editor;

    public UpdateLayoutTemplate templateId( final LayoutTemplateId templateId )
    {
        this.templateId = templateId;
        return this;
    }

    public UpdateLayoutTemplate editor( final TemplateEditor<LayoutTemplate> editor )
    {
        this.editor = editor;
        return this;
    }

    @Override
    public void validate()
    {
    }
}
