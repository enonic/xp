package com.enonic.wem.core.content.schema.content;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.schema.content.UpdateContentTypes;
import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.ContentTypeFetcher;
import com.enonic.wem.api.content.schema.content.ContentTypes;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.content.schema.content.editor.ContentTypeEditor;
import com.enonic.wem.api.content.schema.content.validator.ContentTypeValidationResult;
import com.enonic.wem.api.content.schema.content.validator.ContentTypeValidator;
import com.enonic.wem.api.content.schema.content.validator.InvalidContentTypeException;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.schema.content.dao.ContentTypeDao;

import static com.enonic.wem.api.content.schema.content.validator.ContentTypeValidator.newContentTypeValidator;

@Component
public final class UpdateContentTypesHandler
    extends CommandHandler<UpdateContentTypes>
{
    private ContentTypeDao contentTypeDao;

    public UpdateContentTypesHandler()
    {
        super( UpdateContentTypes.class );
    }

    @Override
    public void handle( final CommandContext context, final UpdateContentTypes command )
        throws Exception
    {
        final Session session = context.getJcrSession();

        final QualifiedContentTypeNames qualifiedNames = command.getNames();
        final ContentTypeEditor editor = command.getEditor();
        int contentTypesUpdated = 0;
        for ( QualifiedContentTypeName qualifiedName : qualifiedNames )
        {
            final ContentType contentType = selectContentType( qualifiedName, session );
            if ( contentType != null )
            {
                final ContentType modifiedContentType = editor.edit( contentType );
                if ( modifiedContentType != null )
                {
                    validate( modifiedContentType, session );
                    contentTypeDao.update( modifiedContentType, session );
                    contentTypesUpdated++;
                }
            }
        }

        session.save();
        command.setResult( contentTypesUpdated );
    }

    private void validate( final ContentType contentType, final Session session )
    {
        final ContentTypeFetcher fetcher = new InternalContentTypeFetcher( session, contentTypeDao );
        final ContentTypeValidator validator = newContentTypeValidator().contentTypeFetcher( fetcher ).build();
        validator.validate( contentType );
        final ContentTypeValidationResult validationResult = validator.getResult();

        if ( !validationResult.hasErrors() )
        {
            return;
        }

        throw new InvalidContentTypeException( contentType, validationResult.getFirst().getErrorMessage() );
    }

    private ContentType selectContentType( final QualifiedContentTypeName contentTypeName, final Session session )
    {
        final ContentTypes contentTypes = contentTypeDao.select( QualifiedContentTypeNames.from( contentTypeName ), session );
        return contentTypes.isEmpty() ? null : contentTypes.first();
    }

    @Autowired
    public void setContentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
    }
}
