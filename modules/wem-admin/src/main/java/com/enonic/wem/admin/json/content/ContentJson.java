package com.enonic.wem.admin.json.content;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.enonic.wem.admin.json.content.page.PageJson;
import com.enonic.wem.admin.json.content.site.SiteJson;
import com.enonic.wem.admin.rest.resource.content.ContentIconUrlResolver;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Metadata;
import com.enonic.wem.api.data.DataJson;
import com.enonic.wem.api.data.DataSetJson;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.form.FormJson;
import com.enonic.wem.api.form.MixinReferencesToFormItemsTransformer;

@SuppressWarnings("UnusedDeclaration")
public final class ContentJson
    extends ContentSummaryJson
{
    private final DataSetJson data;

    private final List<MetadataJson> metadata;

    private final FormJson form;

    private final SiteJson siteJson;

    private final PageJson pageJson;

    public ContentJson( final Content content, final ContentIconUrlResolver iconUrlResolver,
                        final MixinReferencesToFormItemsTransformer mixinReferencesToFormItemsTransformer )
    {
        super( content, iconUrlResolver );
        this.data = new DataSetJson( content.getContentData() );

        this.metadata = new ArrayList<>();
        if (content.getAllMetadata() != null)
        {
            for ( Metadata item : content.getAllMetadata() )
            {
                this.metadata.add( new MetadataJson( item ) );
            }
        }

        this.form = FormJson.resolveJson( content.getForm(), mixinReferencesToFormItemsTransformer );
        this.siteJson = content.hasSite() ? new SiteJson( content.getSite() ) : null;
        this.pageJson = content.hasPage() ? new PageJson( content.getPage() ) : null;
    }

    public List<DataJson> getData()
    {
        return data.getValue();
    }

    public List<MetadataJson> getMetadata()
    {
        return this.metadata;
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
