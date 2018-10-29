package com.enonic.xp.admin.impl.json.content;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.admin.impl.rest.resource.content.ContentIconUrlResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.content.ContentTypeIconUrlResolver;
import com.enonic.xp.content.ContentDependenciesAggregation;
import com.enonic.xp.schema.content.ContentTypeName;

public class DependenciesAggregationJson
{
    private String type;

    private String iconUrl;

    private List<ContentSummaryJson> contents;

    public DependenciesAggregationJson( final ContentDependenciesAggregation aggregation,
                                        final ContentTypeIconUrlResolver contentTypeIconUrlResolver,
                                        final ContentIconUrlResolver contentIconUrlResolver )
    {
        this.type = aggregation.getType().toString();
        this.contents =
            aggregation.getContents().stream().map( content -> new ContentSummaryJson( content, contentIconUrlResolver ) ).collect(
                Collectors.toList() );
        this.iconUrl = contentTypeIconUrlResolver.resolve( ContentTypeName.from( this.type ) );
    }

    public String getType()
    {
        return type;
    }

    public String getIconUrl()
    {
        return iconUrl;
    }

    public List<ContentSummaryJson> getContents()
    {
        return contents;
    }
}
