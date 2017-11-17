package com.enonic.xp.core.impl.content.index.processor;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;

import static com.enonic.xp.content.ContentPropertyNames.APPLICATION_KEY;
import static com.enonic.xp.content.ContentPropertyNames.ATTACHMENT;
import static com.enonic.xp.content.ContentPropertyNames.CREATED_TIME;
import static com.enonic.xp.content.ContentPropertyNames.CREATOR;
import static com.enonic.xp.content.ContentPropertyNames.DATA;
import static com.enonic.xp.content.ContentPropertyNames.MODIFIED_TIME;
import static com.enonic.xp.content.ContentPropertyNames.MODIFIER;
import static com.enonic.xp.content.ContentPropertyNames.OWNER;
import static com.enonic.xp.content.ContentPropertyNames.SITE;
import static com.enonic.xp.content.ContentPropertyNames.SITECONFIG;
import static com.enonic.xp.content.ContentPropertyNames.TYPE;

public class BaseConfigProcessor
    implements ContentIndexConfigProcessor
{
    @Override
    public PatternIndexConfigDocument.Builder processDocument( final PatternIndexConfigDocument.Builder builder )
    {
        builder.analyzer( ContentConstants.DOCUMENT_INDEX_DEFAULT_ANALYZER ).
            add( CREATOR, IndexConfig.MINIMAL ).
            add( MODIFIER, IndexConfig.MINIMAL ).
            add( CREATED_TIME, IndexConfig.MINIMAL ).
            add( MODIFIED_TIME, IndexConfig.MINIMAL ).
            add( OWNER, IndexConfig.MINIMAL ).
            add( PropertyPath.from( DATA, SITECONFIG, APPLICATION_KEY ), IndexConfig.MINIMAL ).
            add( SITE, IndexConfig.NONE ).
            add( TYPE, IndexConfig.MINIMAL ).
            add( ATTACHMENT, IndexConfig.MINIMAL ).
            defaultConfig( IndexConfig.BY_TYPE );

        return builder;
    }
}
