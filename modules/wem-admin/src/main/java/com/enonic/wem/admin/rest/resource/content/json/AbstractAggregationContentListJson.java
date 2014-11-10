package com.enonic.wem.admin.rest.resource.content.json;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.admin.json.aggregation.AggregationJson;
import com.enonic.wem.admin.json.aggregation.BucketAggregationJson;
import com.enonic.wem.admin.json.content.AbstractContentListJson;
import com.enonic.wem.admin.json.content.ContentIdJson;
import com.enonic.wem.admin.rest.resource.content.ContentIconUrlResolver;
import com.enonic.wem.admin.rest.resource.content.ContentPrincipalsResolver;
import com.enonic.wem.api.aggregation.Aggregation;
import com.enonic.wem.api.aggregation.Aggregations;
import com.enonic.wem.api.aggregation.BucketAggregation;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentListMetaData;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.form.MixinReferencesToFormItemsTransformer;

public abstract class AbstractAggregationContentListJson<T extends ContentIdJson>
    extends AbstractContentListJson<T>
{
    private ImmutableSet<AggregationJson> aggregations;


    public AbstractAggregationContentListJson( final Content content, final ContentListMetaData contentListMetaData,
                                               final Aggregations aggregations, final ContentIconUrlResolver iconUrlResolver,
                                               final MixinReferencesToFormItemsTransformer mixinReferencesToFormItemsTransformer,
                                               final ContentPrincipalsResolver contentPrincipalsResolver )
    {
        this( Contents.from( content ), contentListMetaData, ImmutableSet.copyOf( aggregations.getSet() ), iconUrlResolver,
              mixinReferencesToFormItemsTransformer, contentPrincipalsResolver );
    }

    public AbstractAggregationContentListJson( final Contents contents, final ContentListMetaData contentListMetaData,
                                               final ImmutableSet<Aggregation> aggregations, final ContentIconUrlResolver iconUrlResolver,
                                               final MixinReferencesToFormItemsTransformer mixinReferencesToFormItemsTransformer,
                                               final ContentPrincipalsResolver contentPrincipalsResolver )
    {
        super( contents, contentListMetaData, iconUrlResolver, mixinReferencesToFormItemsTransformer, contentPrincipalsResolver );

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
