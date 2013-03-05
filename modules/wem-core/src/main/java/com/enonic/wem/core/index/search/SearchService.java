package com.enonic.wem.core.index.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    @Autowired
    public void setElasticsearchIndexService( final ElasticsearchIndexService elasticsearchIndexService )
    {
        this.elasticsearchIndexService = elasticsearchIndexService;
    }
}
