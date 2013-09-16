package com.enonic.wem.admin.rest.resource.content.json;

import com.enonic.wem.admin.json.content.ContentSummaryJson;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.facet.Facets;

public class FacetedContentSummaryListJson
    extends AbstractFacetedContentListJson<ContentSummaryJson>
{
    public FacetedContentSummaryListJson( Content content, Facets facets )
    {
        super( content, facets );
    }

    public FacetedContentSummaryListJson( final Contents contents, final Facets facets )
    {
        super( contents, facets );
    }

    @Override
    protected ContentSummaryJson createItem( final Content content )
    {
        return new ContentSummaryJson( content );
    }
}
