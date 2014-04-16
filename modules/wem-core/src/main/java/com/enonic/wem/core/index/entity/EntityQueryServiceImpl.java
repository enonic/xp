package com.enonic.wem.core.index.entity;

import com.enonic.wem.api.entity.query.EntityQuery;
import com.enonic.wem.core.index.elastic.ElasticsearchQuery;
import com.enonic.wem.core.index.node.QueryService;
import com.enonic.wem.core.index.query.EntityQueryTranslator;

public class EntityQueryServiceImpl
    extends QueryService<EntityQuery>
{
    private EntityQueryTranslator translator = new EntityQueryTranslator();

    @Override
    protected ElasticsearchQuery translateQuery( final EntityQuery query )
    {
        return translator.translate( query );
    }

}
