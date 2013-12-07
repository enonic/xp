package com.enonic.wem.admin.json.content.page;

import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.api.content.page.Template;


public class TemplateSummaryJson
    implements ItemJson
{
    protected final Template template;

    private final boolean editable;

    private final boolean deletable;

    public TemplateSummaryJson( final Template template )
    {
        this.template = template;
        this.editable = true;
        this.deletable = true;
    }

    public String getKey()
    {
        return template.getKey().toString();
    }

    public String getName()
    {
        return template.getName().toString();
    }

    public String getDisplayName()
    {
        return template.getDisplayName();
    }

    public String getDescriptorModuleResourceKey()
    {
        return template.getDescriptor().toString();
    }

    @Override
    public boolean getDeletable()
    {
        return deletable;
    }

    @Override
    public boolean getEditable()
    {
        return editable;
    }

}
