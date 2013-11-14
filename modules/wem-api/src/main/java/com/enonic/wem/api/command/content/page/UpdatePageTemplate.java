package com.enonic.wem.api.command.content.page;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateName;

public final class UpdatePageTemplate
    extends Command<Boolean>
{
    private PageTemplateName templateName;

    private TemplateEditor<PageTemplate> editor;

    public UpdatePageTemplate withName( final PageTemplateName templateName )
    {
        this.templateName = templateName;
        return this;
    }

    public UpdatePageTemplate editor( final TemplateEditor<PageTemplate> editor )
    {
        this.editor = editor;
        return this;
    }

    @Override
    public void validate()
    {
    }
}
