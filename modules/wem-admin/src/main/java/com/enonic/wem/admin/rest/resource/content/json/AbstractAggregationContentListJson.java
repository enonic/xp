package com.enonic.wem.admin.rest.resource.content.json;

import com.enonic.wem.admin.json.aggregation.AggregationsJson;
import com.enonic.wem.admin.json.content.AbstractContentListJson;
import com.enonic.wem.admin.json.content.ContentIdJson;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.query.aggregation.Aggregations;

public abstract class AbstractAggregationContentListJson<T extends ContentIdJson>
    extends AbstractContentListJson<T>
{
    private AggregationsJson aggregationsJson;

    public AbstractAggregationContentListJson( final Content content, final Aggregations aggregations )
    {
        this( Contents.from( content ), aggregations );
    }

    public AbstractAggregationContentListJson( final Contents contents, final Aggregations aggregations )
    {
        super( contents );
        this.aggregationsJson = new AggregationsJson( aggregations );
    }

    public AggregationsJson getAggregationsJson()
    {
        return aggregationsJson;
    }

    @Override
    protected abstract T createItem( final Content content );
}
