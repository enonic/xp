package com.enonic.wem.core.index.search;

import org.springframework.stereotype.Component;

import com.google.inject.Inject;

import com.enonic.wem.api.content.query.ContentIndexQuery;
import com.enonic.wem.core.index.content.ContentSearchResults;
import com.enonic.wem.core.index.elastic.ElasticsearchIndexService;

@Component
public class SearchService
{
    private ElasticsearchIndexService elasticsearchIndexService;

    public ContentSearchResults search( final ContentIndexQuery contentIndexQuery )
    {
        return elasticsearchIndexService.search( contentIndexQuery );
    }

    @Inject
    public void setElasticsearchIndexService( final ElasticsearchIndexService elasticsearchIndexService )
    {
        this.elasticsearchIndexService = elasticsearchIndexService;
    }
}
