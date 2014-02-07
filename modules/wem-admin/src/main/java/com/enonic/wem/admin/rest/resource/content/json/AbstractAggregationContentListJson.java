package com.enonic.wem.admin.rest.resource.content.json;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.admin.json.aggregation.AggregationJson;
import com.enonic.wem.admin.json.aggregation.BucketAggregationJson;
import com.enonic.wem.admin.json.content.AbstractContentListJson;
import com.enonic.wem.admin.json.content.ContentIdJson;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.query.aggregation.Aggregation;
import com.enonic.wem.api.query.aggregation.Aggregations;
import com.enonic.wem.api.query.aggregation.BucketAggregation;

public abstract class AbstractAggregationContentListJson<T extends ContentIdJson>
    extends AbstractContentListJson<T>
{
    private ImmutableSet<AggregationJson> aggregations;


    public AbstractAggregationContentListJson( final Content content, final Aggregations aggregations )
    {
        this( Contents.from( content ), ImmutableSet.copyOf( aggregations.getSet() ) );
    }

    public AbstractAggregationContentListJson( final Contents contents, final ImmutableSet<Aggregation> aggregations )
    {
        super( contents );

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
