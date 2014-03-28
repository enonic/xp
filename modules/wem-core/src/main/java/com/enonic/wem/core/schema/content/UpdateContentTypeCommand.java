package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.command.schema.content.ContentTypeService;
import com.enonic.wem.api.command.schema.content.GetContentTypeParams;
import com.enonic.wem.api.command.schema.content.UpdateContentTypeParams;
import com.enonic.wem.api.command.schema.content.UpdateContentTypeResult;
import com.enonic.wem.api.command.schema.mixin.MixinService;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeAlreadyExistException;
import com.enonic.wem.api.schema.content.ContentTypeNotFoundException;
import com.enonic.wem.api.schema.content.validator.ContentTypeSuperTypeValidator;
import com.enonic.wem.api.schema.content.validator.ContentTypeValidationResult;
import com.enonic.wem.api.schema.content.validator.InvalidContentTypeException;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;


final class UpdateContentTypeCommand
{
    private ContentTypeService contentTypeService;

    private ContentTypeDao contentTypeDao;

    private MixinService mixinService;

    private UpdateContentTypeParams params;

    UpdateContentTypeResult execute()
    {
        params.validate();

        return doExecute();
    }

    private UpdateContentTypeResult doExecute()
    {
        final ContentType persistedContentType = new GetContentTypeCommand()
            .params( new GetContentTypeParams().contentTypeName( params.getContentTypeName() ) )
            .contentTypeDao( this.contentTypeDao )
            .mixinService( this.mixinService )
            .execute();

        if ( persistedContentType == null )
        {
            throw new ContentTypeNotFoundException( params.getContentTypeName() );
        }

        final ContentType editedContentType = params.getEditor().edit( persistedContentType );

        if ( ( editedContentType != null ) && ( editedContentType != persistedContentType ) )
        {
            persistedContentType.checkIllegalEdit( editedContentType );
            validate( editedContentType );

            if ( !persistedContentType.getName().equals( editedContentType.getName() ) )
            {
                // renamed
                final ContentType existing = new GetContentTypeCommand()
                    .params( new GetContentTypeParams().contentTypeName( editedContentType.getName() ) )
                    .contentTypeDao( this.contentTypeDao )
                    .mixinService( this.mixinService )
                    .execute();
                if ( existing != null )
                {
                    throw new ContentTypeAlreadyExistException( editedContentType.getName() );
                }

                contentTypeDao.updateContentType( editedContentType );
                contentTypeDao.deleteContentType( persistedContentType.getName() );
            }
            else
            {
                contentTypeDao.updateContentType( editedContentType );
            }
        }

        return UpdateContentTypeResult.SUCCESS;
    }

    private void validate( final ContentType contentType )
    {
        final ContentTypeSuperTypeValidator validator = ContentTypeSuperTypeValidator.
            newContentTypeSuperTypeValidator().
            contentTypeService( this.contentTypeService ).
            build();

        validator.validate( contentType.getName(), contentType.getSuperType() );

        final ContentTypeValidationResult validationResult = validator.getResult();

        if ( !validationResult.hasErrors() )
        {
            return;
        }

        throw new InvalidContentTypeException( contentType.getName(), validationResult.getFirst().getErrorMessage() );
    }

    UpdateContentTypeCommand params( final UpdateContentTypeParams params )
    {
        this.params = params;
        return this;
    }

    UpdateContentTypeCommand contentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
        return this;
    }

    UpdateContentTypeCommand contentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
        return this;
    }

    UpdateContentTypeCommand mixinService( final MixinService mixinService )
    {
        this.mixinService = mixinService;
        return this;
    }
}
