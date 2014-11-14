package com.enonic.wem.core.content;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentIndexPaths;
import com.enonic.wem.api.index.IndexConfig;
import com.enonic.wem.api.index.IndexConfigDocument;
import com.enonic.wem.api.index.PatternIndexConfigDocument;

class ContentIndexConfigFactory
{
    public static IndexConfigDocument create()
    {
        final PatternIndexConfigDocument config = PatternIndexConfigDocument.create().
            analyzer( ContentConstants.CONTENT_DEFAULT_ANALYZER ).
            add( ContentIndexPaths.PAGE, IndexConfig.NONE ).
            add( ContentIndexPaths.SITE, IndexConfig.NONE ).
            add( ContentIndexPaths.DRAFT, IndexConfig.NONE ).
            add( ContentIndexPaths.FORM, IndexConfig.NONE ).
            add( ContentIndexPaths.CONTENT_DATA, IndexConfig.BY_TYPE ).
            add( ContentIndexPaths.CONTENT_TYPE_FIELD_NAME, IndexConfig.MINIMAL ).
            defaultConfig( IndexConfig.BY_TYPE ).
            build();

        return config;
    }


}
