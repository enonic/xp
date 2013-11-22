package com.enonic.wem.core.content;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.entity.EntityIndexConfig;

public class ContentEntityIndexConfigFactory
{

    public static final String CONTENT_COLLECTION_NAME = "content";

    public static EntityIndexConfig create( final RootDataSet rootDataSet )
    {
        final EntityIndexConfig.Builder builder = EntityIndexConfig.newEntityIndexConfig();

        builder.collection( CONTENT_COLLECTION_NAME );

        ContentPropertyIndexConfigVisitor visitor = new ContentPropertyIndexConfigVisitor( builder );
        visitor.traverse( rootDataSet );

        return builder.build();
    }

}
