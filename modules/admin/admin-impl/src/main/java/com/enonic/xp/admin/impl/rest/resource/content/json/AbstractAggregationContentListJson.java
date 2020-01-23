package com.enonic.xp.admin.impl.rest.resource.content.json;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.admin.impl.json.aggregation.AggregationJson;
import com.enonic.xp.admin.impl.json.aggregation.BucketAggregationJson;
import com.enonic.xp.admin.impl.json.content.AbstractContentListJson;
import com.enonic.xp.admin.impl.json.content.ContentIdJson;
import com.enonic.xp.admin.impl.rest.resource.content.ComponentNameResolver;
import com.enonic.xp.admin.impl.rest.resource.content.ContentIconUrlResolver;
import com.enonic.xp.admin.impl.rest.resource.content.ContentPrincipalsResolver;
import com.enonic.xp.aggregation.Aggregation;
import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentListMetaData;
import com.enonic.xp.content.Contents;

public abstract class AbstractAggregationContentListJson<T extends ContentIdJson>
    extends AbstractContentListJson<T>
{
    private ImmutableSet<AggregationJson> aggregations;


    public AbstractAggregationContentListJson( final Content content, final ContentListMetaData contentListMetaData,
                                               final Aggregations aggregations, final ContentIconUrlResolver iconUrlResolver,
                                               final ContentPrincipalsResolver contentPrincipalsResolver,
                                               final ComponentNameResolver componentNameResolver )
    {
        this( Contents.from( content ), contentListMetaData, ImmutableSet.copyOf( aggregations.getSet() ), iconUrlResolver,
              contentPrincipalsResolver, componentNameResolver );
    }

    public AbstractAggregationContentListJson( final Contents contents, final ContentListMetaData contentListMetaData,
                                               final ImmutableSet<Aggregation> aggregations, final ContentIconUrlResolver iconUrlResolver,
                                               final ContentPrincipalsResolver contentPrincipalsResolver,
                                               final ComponentNameResolver componentNameResolver )
    {
        super( contents, contentListMetaData, iconUrlResolver, contentPrincipalsResolver, componentNameResolver );

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
