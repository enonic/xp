package com.enonic.wem.admin.json.content.page;

import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.api.content.page.PageTemplate;


public class PageTemplateSummaryJson
    implements ItemJson
{
    protected final PageTemplate pageTemplate;

    private final boolean editable;

    private final boolean deletable;

    public PageTemplateSummaryJson( final PageTemplate template )
    {
        this.pageTemplate = template;
        this.editable = true;
        this.deletable = true;
    }

    public String getKey()
    {
        return pageTemplate.getKey().toString();
    }

    public String getDisplayName()
    {
        return pageTemplate.getDisplayName();
    }

    public String getDescriptorKey()
    {
        return pageTemplate.getDescriptor().toString();
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
