package com.enonic.wem.core.schema.content;

import org.joda.time.Instant;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.CreateContentTypeParams;
import com.enonic.wem.api.schema.content.validator.ContentTypeSuperTypeValidator;
import com.enonic.wem.api.schema.content.validator.ContentTypeValidationResult;
import com.enonic.wem.api.schema.content.validator.InvalidContentTypeException;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;

import static com.enonic.wem.api.schema.content.ContentType.newContentType;


final class CreateContentTypeCommand
{
    private ContentTypeService contentTypeService;

    private ContentTypeDao contentTypeDao;

    private CreateContentTypeParams params;

    ContentType execute()
    {
        params.validate();

        return doExecute();
    }

    private ContentType doExecute()
    {
        validate( params.getName(), params.getSuperType() );

        final ContentType contentType = newContentType().
            name( params.getName() ).
            superType( params.getSuperType() ).
            allowChildContent( params.getAllowChildContent() ).
            contentDisplayNameScript( params.getContentDisplayNameScript() ).
            form( params.getForm().copy() ).
            builtIn( params.isBuiltIn() ).
            setAbstract( params.isAbstract() ).
            setFinal( params.isFinal() ).
            displayName( params.getDisplayName() ).
            icon( params.getSchemaIcon() ).
            createdTime( Instant.now() ).
            build();

        return this.contentTypeDao.createContentType( contentType );
    }

    private void validate( final ContentTypeName contentTypeName, final ContentTypeName contentTypeSuperTypeName )
    {
        final ContentTypeSuperTypeValidator validator = ContentTypeSuperTypeValidator.
            newContentTypeSuperTypeValidator().
            contentTypeService( this.contentTypeService ).
            build();
        validator.validate( contentTypeName, contentTypeSuperTypeName );
        final ContentTypeValidationResult validationResult = validator.getResult();

        if ( !validationResult.hasErrors() )
        {
            return;
        }

        throw new InvalidContentTypeException( contentTypeName, validationResult.getFirst().getErrorMessage() );
    }

    CreateContentTypeCommand params( final CreateContentTypeParams params )
    {
        this.params = params;
        return this;
    }

    CreateContentTypeCommand contentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
        return this;
    }

    CreateContentTypeCommand contentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
        return this;
    }
}
