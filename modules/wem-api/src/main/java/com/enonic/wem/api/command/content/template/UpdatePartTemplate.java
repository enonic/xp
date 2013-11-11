package com.enonic.wem.api.command.content.template;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.PartTemplate;
import com.enonic.wem.api.content.page.PartTemplateName;

public final class UpdatePartTemplate
    extends Command<Boolean>
{
    private PartTemplateName templateName;

    private TemplateEditor<PartTemplate> editor;

    public UpdatePartTemplate templateName( final PartTemplateName templateName )
    {
        this.templateName = templateName;
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
