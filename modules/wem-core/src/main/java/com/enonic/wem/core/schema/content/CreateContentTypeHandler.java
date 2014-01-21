package com.enonic.wem.core.schema.content;

import javax.inject.Inject;

import org.joda.time.DateTime;

import com.enonic.wem.api.command.schema.content.CreateContentType;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.validator.ContentTypeSuperTypeValidator;
import com.enonic.wem.api.schema.content.validator.ContentTypeValidationResult;
import com.enonic.wem.api.schema.content.validator.InvalidContentTypeException;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;

import static com.enonic.wem.api.schema.content.ContentType.newContentType;


public final class CreateContentTypeHandler
    extends CommandHandler<CreateContentType>
{
    private ContentTypeDao contentTypeDao;

    @Override
    public void handle()
        throws Exception
    {
        validate( command.getName(), command.getSuperType() );

        final ContentType createdContentType = newContentType().
            name( command.getName() ).
            superType( command.getSuperType() ).
            allowChildContent( command.getAllowChildContent() ).
            contentDisplayNameScript( command.getContentDisplayNameScript() ).
            form( command.getForm().copy() ).
            builtIn( command.isBuiltIn() ).
            setAbstract( command.isAbstract() ).
            setFinal( command.isFinal() ).
            displayName( command.getDisplayName() ).
            schemaIcon( command.getSchemaIcon() ).
            createdTime( DateTime.now() ).
            //creator( ... ).
                build();

        contentTypeDao.createContentType( createdContentType );

        command.setResult( createdContentType );
    }

    private void validate( final ContentTypeName contentTypeName, final ContentTypeName contentTypeSuperTypeName )
    {
        final ContentTypeSuperTypeValidator validator =
            ContentTypeSuperTypeValidator.newContentTypeSuperTypeValidator().client( context.getClient() ).build();
        validator.validate( contentTypeName, contentTypeSuperTypeName );
        final ContentTypeValidationResult validationResult = validator.getResult();

        if ( !validationResult.hasErrors() )
        {
            return;
        }

        throw new InvalidContentTypeException( contentTypeName, validationResult.getFirst().getErrorMessage() );
    }

    @Inject
    public void setContentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
    }
}
