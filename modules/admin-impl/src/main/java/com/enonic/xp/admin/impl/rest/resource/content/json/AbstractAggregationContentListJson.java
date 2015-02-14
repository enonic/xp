package com.enonic.xp.admin.impl.rest.resource.content.json;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.admin.impl.json.aggregation.AggregationJson;
import com.enonic.xp.admin.impl.json.aggregation.BucketAggregationJson;
import com.enonic.xp.admin.impl.json.content.AbstractContentListJson;
import com.enonic.xp.admin.impl.json.content.ContentIdJson;
import com.enonic.xp.admin.impl.rest.resource.content.ContentIconUrlResolver;
import com.enonic.xp.admin.impl.rest.resource.content.ContentPrincipalsResolver;
import com.enonic.xp.core.aggregation.Aggregation;
import com.enonic.xp.core.aggregation.Aggregations;
import com.enonic.xp.core.aggregation.BucketAggregation;
import com.enonic.xp.core.content.Content;
import com.enonic.xp.core.content.ContentListMetaData;
import com.enonic.xp.core.content.Contents;
import com.enonic.xp.core.form.InlineMixinsToFormItemsTransformer;

public abstract class AbstractAggregationContentListJson<T extends ContentIdJson>
    extends AbstractContentListJson<T>
{
    private ImmutableSet<AggregationJson> aggregations;


    public AbstractAggregationContentListJson( final Content content, final ContentListMetaData contentListMetaData,
                                               final Aggregations aggregations, final ContentIconUrlResolver iconUrlResolver,
                                               final InlineMixinsToFormItemsTransformer inlineMixinsToFormItemsTransformer,
                                               final ContentPrincipalsResolver contentPrincipalsResolver )
    {
        this( Contents.from( content ), contentListMetaData, ImmutableSet.copyOf( aggregations.getSet() ), iconUrlResolver,
              inlineMixinsToFormItemsTransformer, contentPrincipalsResolver );
    }

    public AbstractAggregationContentListJson( final Contents contents, final ContentListMetaData contentListMetaData,
                                               final ImmutableSet<Aggregation> aggregations, final ContentIconUrlResolver iconUrlResolver,
                                               final InlineMixinsToFormItemsTransformer inlineMixinsToFormItemsTransformer,
                                               final ContentPrincipalsResolver contentPrincipalsResolver )
    {
        super( contents, contentListMetaData, iconUrlResolver, inlineMixinsToFormItemsTransformer, contentPrincipalsResolver );

        ImmutableSet.Builder<AggregationJson> builder = ImmutableSet.builder();

        for ( final Aggregation aggregation : aggregations )
        {
            if ( aggregation instanceof BucketAggregation )
            {
                builder.add( new BucketAggregationJson( (BucketAggregation) aggregation ) );
            }
        }

        this.aggregations = builder.build();
    }

    public ImmutableSet<AggregationJson> getAggregations()
    {
        return aggregations;
    }

    @Override
    protected abstract T createItem( final Content content );
}
