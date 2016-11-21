package com.enonic.xp.admin.impl.json.content;

import com.enonic.xp.admin.impl.rest.resource.schema.content.ContentTypeIconUrlResolver;
import com.enonic.xp.content.ContentDependenciesAggregation;
import com.enonic.xp.schema.content.ContentTypeName;

public class DependenciesAggregationJson
{
    private String type;

    private Long count;

    private String iconUrl;

    public DependenciesAggregationJson(final ContentDependenciesAggregation aggregation, final ContentTypeIconUrlResolver contentTypeIconUrlResolver )
    {
        this.type = aggregation.getType().toString();
        this.count = aggregation.getCount();
        this.iconUrl = contentTypeIconUrlResolver.resolve( ContentTypeName.from( this.type ) );
    }

    public DependenciesAggregationJson( final String type, final Long count, final String iconUrl )
    {
        this.type = type;
        this.count = count;
        this.iconUrl = iconUrl;
    }

    public String getType()
    {
        return type;
    }

    public long getCount()
    {
        return count;
    }

    public String getIconUrl()
    {
        return iconUrl;
    }
}
