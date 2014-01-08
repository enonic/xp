package com.enonic.wem.core.index.entity;

import javax.inject.Inject;

import com.enonic.wem.api.entity.query.EntityQuery;
import com.enonic.wem.core.index.elastic.ElasticsearchIndexService;
import com.enonic.wem.core.index.elastic.ElasticsearchQuery;
import com.enonic.wem.core.index.query.EntityQueryTranslator;

public class EntitySearchServiceImpl
    implements EntitySearchService
{
    private EntityQueryTranslator translator = new EntityQueryTranslator();

    private ElasticsearchIndexService elasticsearchIndexService;

    @Override
    public EntityQueryResult find( final EntityQuery entityQuery )
    {
        final ElasticsearchQuery query = translator.translate( entityQuery );

        final EntityQueryResult result = elasticsearchIndexService.search( query );

        return result;
    }

    @Inject
    public void setElasticsearchIndexService( final ElasticsearchIndexService elasticsearchIndexService )
    {
        this.elasticsearchIndexService = elasticsearchIndexService;
    }
}
