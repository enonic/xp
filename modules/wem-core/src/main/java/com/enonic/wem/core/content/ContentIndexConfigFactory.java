package com.enonic.wem.core.content;

import com.enonic.wem.api.entity.NodeIndexConfig;
import com.enonic.wem.api.entity.NodePatternIndexConfig;
import com.enonic.wem.api.entity.NodePropertyIndexConfig;
import com.enonic.wem.api.entity.PropertyIndexConfig;
import com.enonic.wem.core.index.IndexConstants;

public class ContentIndexConfigFactory
{
    public static NodeIndexConfig create()
    {
        final NodePatternIndexConfig.Builder builder = NodePropertyIndexConfig.newPatternIndexConfig().
            collection( IndexConstants.CONTENT_COLLECTION_NAME ).
            analyzer( "content_default" ).
            addConfig( ContentDataSerializer.PAGE, PropertyIndexConfig.SKIP ).
            addConfig( ContentDataSerializer.CONTENT_DATA, PropertyIndexConfig.FULL ).
            addConfig( ContentDataSerializer.FORM, PropertyIndexConfig.SKIP ).
            addConfig( ContentDataSerializer.SITE, PropertyIndexConfig.SKIP ).
            decideFulltextByValueType( true ).
            defaultConfig( PropertyIndexConfig.create().
                enabled( true ).
                fulltextEnabled( true ).
                nGramEnabled( true ).
                build() );

        return builder.build();
    }
}
