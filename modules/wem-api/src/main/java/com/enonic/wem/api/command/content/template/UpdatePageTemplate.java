package com.enonic.wem.api.command.content.template;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateId;

public final class UpdatePageTemplate
    extends Command<Boolean>
{
    private PageTemplateId templateId;

    private TemplateEditor<PageTemplate> editor;

    public UpdatePageTemplate templateId( final PageTemplateId templateId )
    {
        this.templateId = templateId;
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
