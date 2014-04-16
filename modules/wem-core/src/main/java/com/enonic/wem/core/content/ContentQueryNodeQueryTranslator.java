package com.enonic.wem.core.content;

import com.enonic.wem.api.content.query.ContentQuery;
import com.enonic.wem.api.entity.query.NodeQuery;

public class ContentQueryNodeQueryTranslator
    extends ContentQueryEntityQueryTranslator
{
    public NodeQuery translate( final ContentQuery contentQuery )
    {
        final NodeQuery.Builder builder = NodeQuery.newNodeQuery();

        doTranslateEntityQueryProperties( contentQuery, builder );

        // TODO: Add paths

        return builder.build();
    }


}
