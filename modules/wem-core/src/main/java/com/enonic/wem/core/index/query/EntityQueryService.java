package com.enonic.wem.core.index.query;

import com.enonic.wem.api.entity.query.EntityQuery;
import com.enonic.wem.core.elastic.ElasticsearchQuery;

public class EntityQueryService
    extends QueryService<EntityQuery>
{
    private EntityQueryTranslator translator = new EntityQueryTranslator();

    @Override
    protected ElasticsearchQuery translateQuery( final EntityQuery query )
    {
        return translator.translate( query );
    }

}
