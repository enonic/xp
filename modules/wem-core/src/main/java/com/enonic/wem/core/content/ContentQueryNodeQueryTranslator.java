package com.enonic.wem.core.content;

import com.enonic.wem.api.content.query.ContentQuery;
import com.enonic.wem.core.entity.query.NodeQuery;

class ContentQueryNodeQueryTranslator
    extends ContentQueryEntityQueryTranslator
{
    public static NodeQuery translate( final ContentQuery contentQuery )
    {
        final NodeQuery.Builder builder = NodeQuery.create();

        doTranslateEntityQueryProperties( contentQuery, builder );

        // TODO: Add paths

        return builder.build();
    }


}
