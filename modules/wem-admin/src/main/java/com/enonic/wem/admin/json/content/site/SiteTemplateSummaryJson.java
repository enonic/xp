package com.enonic.wem.admin.json.content.site;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.admin.rest.resource.content.site.template.SiteTemplateIconUrlResolver;
import com.enonic.wem.admin.rest.resource.content.site.template.json.VendorJson;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.admin.json.content.ContentTypeFilterJson;


@SuppressWarnings("UnusedDeclaration")
public class SiteTemplateSummaryJson
    implements ItemJson
{
    protected final SiteTemplate siteTemplate;

    private final boolean editable;

    private final boolean deletable;

    private final ContentTypeFilterJson contentTypeFilterJson;

    private final String iconUrl;

    public SiteTemplateSummaryJson( final SiteTemplate siteTemplate, final SiteTemplateIconUrlResolver urlResolver )
    {
        this.siteTemplate = siteTemplate;
        this.editable = true;
        this.deletable = true;
        this.contentTypeFilterJson = new ContentTypeFilterJson( siteTemplate.getContentTypeFilter() );
        this.iconUrl = urlResolver.resolve( siteTemplate );
    }

    public String getKey()
    {
        return siteTemplate.getKey().toString();
    }

    public String getDisplayName()
    {
        return siteTemplate.getDisplayName();
    }

    public String getName()
    {
        return siteTemplate.getName().toString();
    }

    public String getDescription()
    {
        return siteTemplate.getDescription();
    }

    public String getUrl()
    {
        return siteTemplate.getUrl();
    }

    public VendorJson getVendor()
    {
        return new VendorJson( siteTemplate.getVendor() );
    }

    public List<String> getModules()
    {
        return Lists.transform( siteTemplate.getModules().getList(), new Function<ModuleKey, String>()
        {
            @Override
            public String apply( final ModuleKey moduleKey )
            {
                return moduleKey.toString();
            }
        } );
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


    public ContentTypeFilterJson getContentTypeFilter()
    {
        return contentTypeFilterJson;
    }

    public List<String> getPageTemplateKeys()
    {
        return templatesAsKeyList( siteTemplate.getPageTemplates().getList() );
    }

    public String getIconUrl()
    {
        return iconUrl;
    }

    private List<String> templatesAsKeyList( final List<PageTemplate> templateList )
    {
        return Lists.transform( templateList, new Function<PageTemplate, String>()
        {
            @Override
            public String apply( final PageTemplate template )
            {
                return template.getKey().toString();
            }
        } );
    }

}
