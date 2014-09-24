package com.enonic.wem.core.content;

import com.enonic.wem.api.index.IndexConfig;
import com.enonic.wem.api.index.IndexConfigDocumentNew;
import com.enonic.wem.api.index.PatternBasedIndexConfigDocument;

class ContentIndexConfigFactory
{
    public static IndexConfigDocumentNew create()
    {
        final PatternBasedIndexConfigDocument config = PatternBasedIndexConfigDocument.create().
            analyzer( "content_default" ).
            add( ContentDataSerializer.PAGE, IndexConfig.NONE ).
            add( ContentDataSerializer.SITE, IndexConfig.NONE ).
            add( ContentDataSerializer.DRAFT, IndexConfig.NONE ).
            add( ContentDataSerializer.FORM, IndexConfig.NONE ).
            add( ContentDataSerializer.CONTENT_DATA, IndexConfig.BY_TYPE ).
            add( ContentDataSerializer.CONTENT_TYPE_FIELD_NAME, IndexConfig.MINIMAL ).
            defaultConfig( IndexConfig.BY_TYPE ).
            build();

        return config;
    }


}
