package com.enonic.wem.core.index.entity;

import javax.inject.Inject;

import com.enonic.wem.core.index.elastic.ElasticsearchIndexService;
import com.enonic.wem.core.index.elastic.ElasticsearchQuery;
import com.enonic.wem.core.index.query.EntityQueryTranslator;
import com.enonic.wem.query.EntityQuery;

public class EntitySearchServiceImpl
    implements EntitySearchService
{
    private EntityQueryTranslator translator = new EntityQueryTranslator();

    private ElasticsearchIndexService elasticsearchIndexService;

    @Override
    public EntitySearchResult find( final EntityQuery entityQuery )
    {
        final ElasticsearchQuery query = translator.translate( entityQuery );

        final EntitySearchResult result = elasticsearchIndexService.search( query );

        return result;
    }

    @Inject
    public void setElasticsearchIndexService( final ElasticsearchIndexService elasticsearchIndexService )
    {
        this.elasticsearchIndexService = elasticsearchIndexService;
    }
}
