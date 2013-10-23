package com.enonic.wem.admin.rest.resource.content.json;

import com.enonic.wem.admin.json.content.ContentIdJson;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.facet.Facets;

public class FacetedContentIdListJson
    extends AbstractFacetedContentListJson<ContentIdJson>
{
    public FacetedContentIdListJson( final Content content, final Facets facets )
    {
        super( content, facets );
    }

    public FacetedContentIdListJson( final Contents contents, final Facets facets )
    {
        super( contents, facets );
    }

    @Override
    protected ContentIdJson createItem( final Content content )
    {
        return new ContentIdJson( content.getId() );
    }
}
