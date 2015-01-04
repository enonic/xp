package com.enonic.wem.core.content;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.CreateContentParams;
import com.enonic.wem.api.data.PropertyPath;
import com.enonic.wem.api.index.IndexConfig;
import com.enonic.wem.api.index.IndexConfigDocument;
import com.enonic.wem.api.index.PatternIndexConfigDocument;
import com.enonic.wem.api.schema.content.ContentTypeName;

import static com.enonic.wem.core.content.ContentPropertyNames.ATTACHMENT;
import static com.enonic.wem.core.content.ContentPropertyNames.DATA;
import static com.enonic.wem.core.content.ContentPropertyNames.DRAFT;
import static com.enonic.wem.core.content.ContentPropertyNames.FORM;
import static com.enonic.wem.core.content.ContentPropertyNames.METADATA;
import static com.enonic.wem.core.content.ContentPropertyNames.PAGE;
import static com.enonic.wem.core.content.ContentPropertyNames.SITE;
import static com.enonic.wem.core.content.ContentPropertyNames.TYPE;

class ContentIndexConfigFactory
{
    public static IndexConfigDocument create( final CreateContentParams params )
    {
        final PatternIndexConfigDocument.Builder config = PatternIndexConfigDocument.create().
            analyzer( ContentConstants.CONTENT_DEFAULT_ANALYZER ).
            add( PAGE, IndexConfig.NONE ).
            add( SITE, IndexConfig.NONE ).
            add( DRAFT, IndexConfig.NONE ).
            add( FORM, IndexConfig.NONE ).
            add( DATA, IndexConfig.BY_TYPE ).
            add( TYPE, IndexConfig.MINIMAL ).
            add( ATTACHMENT, IndexConfig.NONE ).
            defaultConfig( IndexConfig.BY_TYPE );

        final ContentTypeName typeName = params.getType();

        if ( typeName.isMedia() || typeName.isDescendantOfMedia() )
        {
            setFiltersForMediaData( typeName, config );
        }

        return config.build();
    }

    private static void setFiltersForMediaData( final ContentTypeName contentTypeName, PatternIndexConfigDocument.Builder builder )
    {
        // Extend with separate stuff for different contentTypeNames
        builder.add( PropertyPath.from( DATA, METADATA ), IndexConfig.NONE );
        builder.add( PropertyPath.from( DATA, METADATA, "media" ), IndexConfig.MINIMAL );
    }

    public static IndexConfigDocument create( final Content content )
    {
        final PatternIndexConfigDocument.Builder config = PatternIndexConfigDocument.create().
            analyzer( ContentConstants.CONTENT_DEFAULT_ANALYZER ).
            add( PAGE, IndexConfig.NONE ).
            add( SITE, IndexConfig.NONE ).
            add( DRAFT, IndexConfig.NONE ).
            add( FORM, IndexConfig.NONE ).
            add( DATA, IndexConfig.BY_TYPE ).
            add( TYPE, IndexConfig.MINIMAL ).
            defaultConfig( IndexConfig.BY_TYPE );

        if ( content.getType().isMedia() )
        {
            setFiltersForMediaData( content.getType(), config );
        }

        return config.build();
    }
}
