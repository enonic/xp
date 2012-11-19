package com.enonic.wem.core.content.type;

import java.util.List;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import com.enonic.wem.api.command.content.type.GetContentTypes;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypeFetcher;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.api.content.type.MockContentTypeFetcher;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.api.content.type.form.FormItemSet;
import com.enonic.wem.api.content.type.form.Input;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.type.dao.ContentTypeDao;

import static com.enonic.wem.api.content.type.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.type.form.Input.newInput;
import static com.enonic.wem.api.content.type.form.inputtype.InputTypes.TEXT_AREA;
import static com.enonic.wem.api.content.type.form.inputtype.InputTypes.TEXT_LINE;

@Component
public final class GetContentTypesHandler
    extends CommandHandler<GetContentTypes>
{
    private ContentTypeFetcher contentTypeFetcher;

    private ContentTypeDao contentTypeDao;

    public GetContentTypesHandler()
    {
        super( GetContentTypes.class );
        this.contentTypeFetcher = createMockContentTypeFetcher();
    }

    @Override
    public void handle( final CommandContext context, final GetContentTypes command )
        throws Exception
    {
        final Session session = context.getJcrSession();
        final ContentTypes contentTypes;
        if ( command.isGetAll() )
        {
            contentTypes = getAllContentTypes( session );
        }
        else
        {
            final QualifiedContentTypeNames contentTypeNames = command.getNames();
            contentTypes = getContentTypes( session, contentTypeNames );
        }
        command.setResult( contentTypes );
    }

    private ContentTypes getAllContentTypes( final Session session )
    {
        final ContentTypes contentTypes = contentTypeDao.retrieveAllContentTypes( session );
        if ( !contentTypes.isEmpty() )
        {
            return contentTypes;
        }
        // TODO remove this, returning mock values for now
        return getMockContentTypes( QualifiedContentTypeNames.from( "News:Article", "News:Article2" ) );

    }

    private ContentTypes getContentTypes( final Session session, final QualifiedContentTypeNames contentTypeNames )
    {
        final ContentTypes contentTypes = contentTypeDao.retrieveContentTypes( session, contentTypeNames );
        if ( !contentTypes.isEmpty() )
        {
            return contentTypes;
        }
        // TODO remove this, returning mock values for now
        return getMockContentTypes( contentTypeNames );
    }

    private ContentTypes getMockContentTypes( final QualifiedContentTypeNames contentTypeNames )
    {
        final List<ContentType> contentTypeList = Lists.newArrayList();
        for ( QualifiedContentTypeName name : contentTypeNames )
        {
            final ContentType contentType = contentTypeFetcher.getContentType( name );
            if ( contentType != null )
            {
                contentTypeList.add( contentType );
            }
        }
        return ContentTypes.from( contentTypeList );
    }

    private ContentTypeFetcher createMockContentTypeFetcher()
    {
        MockContentTypeFetcher mockContentTypeFetcher = new MockContentTypeFetcher();
        final ContentType articleContentType = new ContentType();
        articleContentType.setModule( Module.newModule().name( "News" ).build() );
        articleContentType.setName( "Article" );
        final Input title =
            newInput().name( "title" ).type( TEXT_LINE ).label( "Title" ).required( true ).helpText( "Article title" ).build();
        final Input category =
            newInput().name( "preface" ).type( TEXT_LINE ).label( "Preface" ).helpText( "Preface of the article" ).build();
        final Input body =
            newInput().name( "body" ).type( TEXT_AREA ).label( "Body" ).required( true ).helpText( "Body of the article" ).build();
        articleContentType.addFormItem( title );
        articleContentType.addFormItem( category );
        articleContentType.addFormItem( body );
        mockContentTypeFetcher.add( articleContentType );

        final ContentType article2ContentType = new ContentType();
        article2ContentType.setModule( Module.newModule().name( "News" ).build() );
        article2ContentType.setName( "Article2" );
        article2ContentType.addFormItem( title.copy() );
        article2ContentType.addFormItem( category.copy() );
        article2ContentType.addFormItem( body.copy() );
        FormItemSet formItemSet = newFormItemSet().name( "related" ).build();
        formItemSet.add( newInput().name( "author" ).label( "Author" ).type( TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "category" ).label( "Category" ).type( TEXT_LINE ).build() );
        article2ContentType.addFormItem( formItemSet );
        mockContentTypeFetcher.add( article2ContentType );

        return mockContentTypeFetcher;
    }

    @Autowired
    public void setContentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
    }
}
