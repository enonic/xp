package com.enonic.wem.admin.json.content;

import java.util.List;

import com.enonic.wem.admin.json.content.page.PageJson;
import com.enonic.wem.admin.json.content.site.SiteJson;
import com.enonic.wem.admin.rest.resource.content.ContentIconUrlResolver;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.data.DataJson;
import com.enonic.wem.api.data.DataSetJson;
import com.enonic.wem.api.form.FormJson;
import com.enonic.wem.api.form.MixinReferencesToFormItemsTransformer;

@SuppressWarnings("UnusedDeclaration")
public final class ContentJson
    extends ContentSummaryJson
{
    private final DataSetJson data;

    private final FormJson form;

    private final SiteJson siteJson;

    private final PageJson pageJson;

    public ContentJson( final Content content, final ContentIconUrlResolver iconUrlResolver,
                        final MixinReferencesToFormItemsTransformer mixinReferencesToFormItemsTransformer )
    {
        super( content, iconUrlResolver );
        this.data = new DataSetJson( content.getContentData() );
        this.form = FormJson.resolveJson( content.getForm(), mixinReferencesToFormItemsTransformer );
        this.siteJson = content.hasSite() ? new SiteJson( content.getSite() ) : null;
        this.pageJson = content.hasPage() ? new PageJson( content.getPage() ) : null;
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
