package com.enonic.wem.core.content;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.index.IndexConfig;
import com.enonic.wem.api.index.IndexConfigDocument;
import com.enonic.wem.api.index.PatternIndexConfigDocument;
import com.enonic.wem.api.node.AttachmentPropertyNames;

class ContentIndexConfigFactory
{
    public static IndexConfigDocument create()
    {
        final PatternIndexConfigDocument config = PatternIndexConfigDocument.create().
            analyzer( ContentConstants.CONTENT_DEFAULT_ANALYZER ).
            add( ContentPropertyNames.PAGE_SET, IndexConfig.NONE ).
            add( ContentPropertyNames.SITE_SET, IndexConfig.NONE ).
            add( ContentPropertyNames.DRAFT, IndexConfig.NONE ).
            add( ContentPropertyNames.FORM_SET, IndexConfig.NONE ).
            add( ContentPropertyNames.CONTENT_DATA_SET, IndexConfig.BY_TYPE ).
            add( ContentPropertyNames.CONTENT_TYPE, IndexConfig.MINIMAL ).
            add( AttachmentPropertyNames.ROOT, IndexConfig.NONE ).
            defaultConfig( IndexConfig.BY_TYPE ).
            build();

        return config;
    }


}
