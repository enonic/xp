package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.CreateContentTranslatorParams;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.PatternIndexConfigDocument;

import static com.enonic.xp.content.ContentPropertyNames.ATTACHMENT;
import static com.enonic.xp.content.ContentPropertyNames.DATA;
import static com.enonic.xp.content.ContentPropertyNames.FORM;
import static com.enonic.xp.content.ContentPropertyNames.METADATA;
import static com.enonic.xp.content.ContentPropertyNames.PAGE;
import static com.enonic.xp.content.ContentPropertyNames.SITE;
import static com.enonic.xp.content.ContentPropertyNames.TYPE;
import static com.enonic.xp.content.ContentPropertyNames.VALID;

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
