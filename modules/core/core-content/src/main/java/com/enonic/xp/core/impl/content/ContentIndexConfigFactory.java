package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.CreateContentTranslatorParams;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.form.Form;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;

import static com.enonic.xp.content.ContentPropertyNames.ATTACHMENT;
import static com.enonic.xp.content.ContentPropertyNames.ATTACHMENT_TEXT_COMPONENT;
import static com.enonic.xp.content.ContentPropertyNames.CREATED_TIME;
import static com.enonic.xp.content.ContentPropertyNames.CREATOR;
import static com.enonic.xp.content.ContentPropertyNames.DATA;
import static com.enonic.xp.content.ContentPropertyNames.EXTRA_DATA;
import static com.enonic.xp.content.ContentPropertyNames.MODIFIED_TIME;
import static com.enonic.xp.content.ContentPropertyNames.MODIFIER;
import static com.enonic.xp.content.ContentPropertyNames.OWNER;
import static com.enonic.xp.content.ContentPropertyNames.PAGE;
import static com.enonic.xp.content.ContentPropertyNames.PAGE_TEXT_COMPONENT;
import static com.enonic.xp.content.ContentPropertyNames.SITE;
import static com.enonic.xp.content.ContentPropertyNames.TYPE;

class ContentIndexConfigFactory
{
    public static IndexConfigDocument create( final CreateContentTranslatorParams params, final ContentTypeService contentTypeService )
    {
        return doCreateIndexConfig( getForm( contentTypeService, params.getType() ) );
    }

    public static IndexConfigDocument create( final Content content, final ContentTypeService contentTypeService )
    {
        return doCreateIndexConfig( getForm( contentTypeService, content.getType() ) );
    }

    private static IndexConfigDocument doCreateIndexConfig( final Form form )
    {
        final PatternIndexConfigDocument.Builder configDocumentBuilder = PatternIndexConfigDocument.create().
            analyzer( ContentConstants.DOCUMENT_INDEX_DEFAULT_ANALYZER ).
            add( CREATOR, IndexConfig.MINIMAL ).
            add( MODIFIER, IndexConfig.MINIMAL ).
            add( CREATED_TIME, IndexConfig.MINIMAL ).
            add( MODIFIED_TIME, IndexConfig.MINIMAL ).
            add( OWNER, IndexConfig.MINIMAL ).
            add( PAGE, IndexConfig.NONE ).
            add( PAGE_TEXT_COMPONENT, IndexConfig.FULLTEXT ).
            add( PropertyPath.from( PAGE, "regions" ), IndexConfig.NONE ).
            add( SITE, IndexConfig.NONE ).
            add( DATA, IndexConfig.BY_TYPE ).
            add( TYPE, IndexConfig.MINIMAL ).
            add( ATTACHMENT, IndexConfig.MINIMAL ).
            add( PropertyPath.from( EXTRA_DATA ), IndexConfig.MINIMAL ).
            defaultConfig( IndexConfig.BY_TYPE );

        configDocumentBuilder.add( ATTACHMENT_TEXT_COMPONENT, IndexConfig.create().
            enabled( true ).
            fulltext( true ).
            includeInAllText( false ).
            nGram( true ).
            decideByType( false ).
            build() );

        final IndexConfigVisitor indexConfigVisitor = new IndexConfigVisitor( DATA, configDocumentBuilder );
        indexConfigVisitor.traverse( form );

        return configDocumentBuilder.build();
    }

    private static Form getForm( final ContentTypeService contentTypeService, final ContentTypeName contentTypeName )
    {
        return contentTypeService.getByName( new GetContentTypeParams().contentTypeName( contentTypeName ) ).getForm();
    }
}
