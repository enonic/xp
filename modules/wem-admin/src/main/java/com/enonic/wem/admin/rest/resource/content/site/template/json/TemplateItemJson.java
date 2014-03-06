package com.enonic.wem.admin.rest.resource.content.site.template.json;

import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.site.SiteTemplate;


@SuppressWarnings("UnusedDeclaration")
public final class TemplateItemJson
    implements ItemJson
{
    private final boolean editable;

    private final boolean deletable;

    private final String name;

    private final String displayName;

    private final String key;

    private final boolean hasChildren;

    private final boolean isSiteTemplate;

    public TemplateItemJson( final SiteTemplate siteTemplate )
    {
        this.editable = true;
        this.deletable = true;
        this.name = siteTemplate.getName().toString();
        this.displayName = siteTemplate.getDisplayName();
        this.key = siteTemplate.getKey().toString();
        this.hasChildren = siteTemplate.getPageTemplates().isNotEmpty();
        this.isSiteTemplate = true;
    }

    public TemplateItemJson( final PageTemplate pageTemplate )
    {
        this.editable = false;
        this.deletable = false;
        this.name = pageTemplate.getName().toString();
        this.displayName = pageTemplate.getDisplayName();
        this.key = pageTemplate.getKey().toString();
        this.hasChildren = false;
        this.isSiteTemplate = false;
    }

    public String getKey()
    {
        return this.key;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }

    public String getName()
    {
        return this.name;
    }

    public boolean getHasChildren()
    {
        return this.hasChildren;
    }

    public String getTemplateType()
    {
        return this.isSiteTemplate ? "SITE" : "PAGE";
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
