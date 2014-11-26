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
            add( ContentFieldNames.PAGE_SET, IndexConfig.NONE ).
            add( ContentFieldNames.SITE_SET, IndexConfig.NONE ).
            add( ContentFieldNames.DRAFT, IndexConfig.NONE ).
            add( ContentFieldNames.FORM_SET, IndexConfig.NONE ).
            add( ContentFieldNames.CONTENT_DATA_SET, IndexConfig.BY_TYPE ).
            add( ContentFieldNames.CONTENT_TYPE, IndexConfig.MINIMAL ).
            defaultConfig( IndexConfig.BY_TYPE ).
            build();

        return config;
    }


}
