package com.enonic.wem.api.command.content.page;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.LayoutTemplate;
import com.enonic.wem.api.content.page.LayoutTemplateName;

public final class UpdateLayoutTemplate
    extends Command<Boolean>
{
    private LayoutTemplateName templateName;

    private TemplateEditor<LayoutTemplate> editor;

    public UpdateLayoutTemplate withName( final LayoutTemplateName templateName )
    {
        this.templateName = templateName;
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
