package com.enonic.wem.core.content;

import com.enonic.wem.api.entity.EntityIndexConfig;
import com.enonic.wem.api.entity.EntityPatternIndexConfig;
import com.enonic.wem.api.entity.EntityPropertyIndexConfig;
import com.enonic.wem.api.entity.PropertyIndexConfig;

public class ContentEntityIndexConfigFactory
{
    public static final String CONTENT_COLLECTION_NAME = "content";

    public static EntityIndexConfig create()
    {
        final EntityPatternIndexConfig.Builder builder = EntityPropertyIndexConfig.newPatternIndexConfig().
            collection( CONTENT_COLLECTION_NAME ).
            addConfig( ContentNodeTranslator.PAGE_CONFIG_PATH, PropertyIndexConfig.INDEXNON_PROPERTY_CONFIG ).
            addConfig( ContentNodeTranslator.CONTENT_DATA_PATH, PropertyIndexConfig.INDEXALL_PROPERTY_CONFIG ).
            addConfig( ContentNodeTranslator.FORM_PATH, PropertyIndexConfig.INDEXNON_PROPERTY_CONFIG ).
            addConfig( ContentNodeTranslator.SITE_CONFIG_PATH, PropertyIndexConfig.INDEXNON_PROPERTY_CONFIG ).
            defaultConfig( PropertyIndexConfig.newPropertyIndexConfig().
                enabled( true ).
                fulltextEnabled( true ).
                tokenizedEnabled( true ).
                build() );

        return builder.build();
    }
}
