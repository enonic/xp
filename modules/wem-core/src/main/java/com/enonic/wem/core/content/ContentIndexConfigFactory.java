package com.enonic.wem.core.content;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.index.IndexConfig;
import com.enonic.wem.api.index.IndexConfigDocument;
import com.enonic.wem.api.index.PatternIndexConfigDocument;

class ContentIndexConfigFactory
{
    public static IndexConfigDocument create()
    {
        final PatternIndexConfigDocument config = PatternIndexConfigDocument.create().
            analyzer( ContentConstants.CONTENT_DEFAULT_ANALYZER ).
            add( ContentPropertyNames.PAGE, IndexConfig.NONE ).
            add( ContentPropertyNames.SITE, IndexConfig.NONE ).
            add( ContentPropertyNames.DRAFT, IndexConfig.NONE ).
            add( ContentPropertyNames.FORM, IndexConfig.NONE ).
            add( ContentPropertyNames.DATA, IndexConfig.BY_TYPE ).
            add( ContentPropertyNames.TYPE, IndexConfig.MINIMAL ).
            defaultConfig( IndexConfig.BY_TYPE ).
            build();

        return config;
    }


}
