package com.enonic.xp.admin.impl.rest.resource.content.json;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.admin.impl.json.content.ContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.ContentIconUrlResolver;
import com.enonic.xp.admin.impl.rest.resource.content.ContentPrincipalsResolver;
import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentListMetaData;
import com.enonic.xp.content.Contents;
import com.enonic.xp.form.InlineMixinsToFormItemsTransformer;

public class AggregationsContentListJson
    extends AbstractAggregationContentListJson<ContentJson>
{
    private final ContentPrincipalsResolver contentPrincipalsResolver;

    public AggregationsContentListJson( final Content content, final ContentListMetaData contentListMetaData,
                                        final Aggregations aggregations, final ContentIconUrlResolver iconUrlResolver,
                                        final InlineMixinsToFormItemsTransformer inlineMixinsToFormItemsTransformer,
                                        final ContentPrincipalsResolver contentPrincipalsResolver )
    {
        super( content, contentListMetaData, aggregations, iconUrlResolver, inlineMixinsToFormItemsTransformer,
               contentPrincipalsResolver );
        this.contentPrincipalsResolver = contentPrincipalsResolver;
    }

    public AggregationsContentListJson( final Contents contents, final ContentListMetaData contentListMetaData,
                                        final Aggregations aggregations, final ContentIconUrlResolver iconUrlResolver,
                                        final InlineMixinsToFormItemsTransformer inlineMixinsToFormItemsTransformer,
                                        final ContentPrincipalsResolver contentPrincipalsResolver )
    {
        super( contents, contentListMetaData, ImmutableSet.copyOf( aggregations ), iconUrlResolver, inlineMixinsToFormItemsTransformer,
               contentPrincipalsResolver );
        this.contentPrincipalsResolver = contentPrincipalsResolver;
    }

    @Override
    protected ContentJson createItem( final Content content )
    {
        return new ContentJson( content, iconUrlResolver, inlineMixinsToFormItemsTransformer, contentPrincipalsResolver );
    }
}
