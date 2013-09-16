package com.enonic.wem.admin.rest.resource.content.json;

import com.enonic.wem.admin.json.content.ContentJson;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.facet.Facets;

public class FacetedContentListJson
    extends AbstractFacetedContentListJson<ContentJson>
{
    public FacetedContentListJson( Content content, Facets facets )
    {
        super( content, facets );
    }

    public FacetedContentListJson( final Contents contents, final Facets facets )
    {
        super( contents, facets );
    }

    @Override
    protected ContentJson createItem( final Content content )
    {
        return new ContentJson( content );
    }
}
