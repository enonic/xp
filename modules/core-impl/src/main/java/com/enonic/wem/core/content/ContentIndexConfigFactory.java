package com.enonic.wem.core.content;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.CreateContentTranslatorParams;
import com.enonic.wem.api.data.PropertyPath;
import com.enonic.wem.api.index.IndexConfig;
import com.enonic.wem.api.index.IndexConfigDocument;
import com.enonic.wem.api.index.PatternIndexConfigDocument;

import static com.enonic.wem.core.content.ContentPropertyNames.ATTACHMENT;
import static com.enonic.wem.core.content.ContentPropertyNames.DATA;
import static com.enonic.wem.core.content.ContentPropertyNames.FORM;
import static com.enonic.wem.core.content.ContentPropertyNames.METADATA;
import static com.enonic.wem.core.content.ContentPropertyNames.PAGE;
import static com.enonic.wem.core.content.ContentPropertyNames.SITE;
import static com.enonic.wem.core.content.ContentPropertyNames.TYPE;
import static com.enonic.wem.core.content.ContentPropertyNames.VALID;

class ContentIndexConfigFactory
{
    public static IndexConfigDocument create( final CreateContentTranslatorParams params )
    {
        return doCreateIndexConfig();
    }

    public static IndexConfigDocument create( final Content content )
    {
        return doCreateIndexConfig();
    }

    private static IndexConfigDocument doCreateIndexConfig()
    {
        final PatternIndexConfigDocument.Builder config = PatternIndexConfigDocument.create().
            analyzer( ContentConstants.CONTENT_DEFAULT_ANALYZER ).
            add( PAGE, IndexConfig.NONE ).
            add( SITE, IndexConfig.NONE ).
            add( VALID, IndexConfig.NONE ).
            add( FORM, IndexConfig.NONE ).
            add( DATA, IndexConfig.BY_TYPE ).
            add( TYPE, IndexConfig.MINIMAL ).
            add( ATTACHMENT, IndexConfig.NONE ).
            add( PropertyPath.from( DATA, METADATA ), IndexConfig.NONE ).
            add( PropertyPath.from( DATA, METADATA, "media" ), IndexConfig.MINIMAL ).
            defaultConfig( IndexConfig.BY_TYPE );

        return config.build();
    }

}
