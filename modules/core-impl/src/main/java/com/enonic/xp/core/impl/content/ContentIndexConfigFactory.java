package com.enonic.xp.core.impl.content;

import com.enonic.xp.core.content.Content;
import com.enonic.xp.core.content.ContentConstants;
import com.enonic.xp.core.content.CreateContentTranslatorParams;
import com.enonic.xp.core.data.PropertyPath;
import com.enonic.xp.core.index.IndexConfig;
import com.enonic.xp.core.index.IndexConfigDocument;
import com.enonic.xp.core.index.PatternIndexConfigDocument;

import static com.enonic.xp.core.content.ContentPropertyNames.ATTACHMENT;
import static com.enonic.xp.core.content.ContentPropertyNames.DATA;
import static com.enonic.xp.core.content.ContentPropertyNames.FORM;
import static com.enonic.xp.core.content.ContentPropertyNames.METADATA;
import static com.enonic.xp.core.content.ContentPropertyNames.PAGE;
import static com.enonic.xp.core.content.ContentPropertyNames.SITE;
import static com.enonic.xp.core.content.ContentPropertyNames.TYPE;
import static com.enonic.xp.core.content.ContentPropertyNames.VALID;

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
