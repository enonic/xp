package com.enonic.wem.admin.json.content;

import java.util.List;

import com.enonic.wem.admin.json.content.site.SiteJson;
import com.enonic.wem.admin.rest.resource.content.ContentIconUrlResolver;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.page.PageJson;
import com.enonic.wem.api.data.DataJson;
import com.enonic.wem.api.data.DataSetJson;
import com.enonic.wem.api.form.FormJson;

@SuppressWarnings("UnusedDeclaration")
public final class ContentJson
    extends ContentSummaryJson
{
    private final DataSetJson data;

    private final FormJson form;

    private final SiteJson siteJson;

    private final PageJson pageJson;

    public ContentJson( final Content content, final ContentIconUrlResolver iconUrlResolver )
    {
        super( content, iconUrlResolver );
        this.data = new DataSetJson( content.getContentData() );
        this.form = content.getForm() != null ? new FormJson( content.getForm() ) : null;
        this.siteJson = content.isSite() ? new SiteJson( content.getSite() ) : null;
        this.pageJson = content.isPage() ? new PageJson( content.getPage() ) : null;
    }

    public List<DataJson> getData()
    {
        return data.getValue();
    }

    public FormJson getForm()
    {
        return form;
    }

    public SiteJson getSite()
    {
        return siteJson;
    }

    public PageJson getPage()
    {
        return pageJson;
    }
}
