package com.enonic.wem.admin.rest.resource.content.json;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.admin.json.content.ContentJson;
import com.enonic.wem.admin.rest.resource.content.ContentIconUrlResolver;
import com.enonic.wem.admin.rest.resource.content.ContentPrincipalsResolver;
import com.enonic.wem.api.aggregation.Aggregations;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentListMetaData;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.form.MixinReferencesToFormItemsTransformer;

public class AggregationsContentListJson
    extends AbstractAggregationContentListJson<ContentJson>
{
    private final ContentPrincipalsResolver contentPrincipalsResolver;

    public AggregationsContentListJson( final Content content, final ContentListMetaData contentListMetaData,
                                        final Aggregations aggregations, final ContentIconUrlResolver iconUrlResolver,
                                        final MixinReferencesToFormItemsTransformer mixinReferencesToFormItemsTransformer,
                                        final ContentPrincipalsResolver contentPrincipalsResolver )
    {
        super( content, contentListMetaData, aggregations, iconUrlResolver, mixinReferencesToFormItemsTransformer,
               contentPrincipalsResolver );
        this.contentPrincipalsResolver = contentPrincipalsResolver;
    }

    public AggregationsContentListJson( final Contents contents, final ContentListMetaData contentListMetaData,
                                        final Aggregations aggregations, final ContentIconUrlResolver iconUrlResolver,
                                        final MixinReferencesToFormItemsTransformer mixinReferencesToFormItemsTransformer,
                                        final ContentPrincipalsResolver contentPrincipalsResolver )
    {
        super( contents, contentListMetaData, ImmutableSet.copyOf( aggregations ), iconUrlResolver, mixinReferencesToFormItemsTransformer,
               contentPrincipalsResolver );
        this.contentPrincipalsResolver = contentPrincipalsResolver;
    }

    @Override
    protected ContentJson createItem( final Content content )
    {
        return new ContentJson( content, iconUrlResolver, mixinReferencesToFormItemsTransformer, contentPrincipalsResolver );
    }
}
