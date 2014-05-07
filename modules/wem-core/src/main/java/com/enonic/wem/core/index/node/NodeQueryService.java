package com.enonic.wem.core.index.node;

import com.enonic.wem.api.entity.query.NodeQuery;
import com.enonic.wem.core.elastic.ElasticsearchQuery;
import com.enonic.wem.core.index.query.EntityQueryTranslator;

public class NodeQueryService
    extends QueryService<NodeQuery>
{
    private EntityQueryTranslator translator = new EntityQueryTranslator();

    @Override
    protected ElasticsearchQuery translateQuery( final NodeQuery query )
    {
        return translator.translate( query );
    }
}
