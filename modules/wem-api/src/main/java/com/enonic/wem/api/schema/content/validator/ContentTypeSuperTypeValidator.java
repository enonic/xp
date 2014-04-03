package com.enonic.wem.api.schema.content.validator;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.GetContentTypeParams;

import static com.enonic.wem.api.schema.content.validator.ContentTypeValidationResult.newContentTypeValidationResult;

public class ContentTypeSuperTypeValidator
{
    private final ContentTypeService contentTypeService;

    private final ContentTypeValidationResult.Builder resultBuilder = newContentTypeValidationResult();

    private ContentTypeSuperTypeValidator( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }

    public void validate( final ContentTypeName contentTypeName, final ContentTypeName superTypeContentName )
    {
        if ( superTypeContentName != null )
        {
            final GetContentTypeParams params = new GetContentTypeParams().contentTypeName( superTypeContentName );
            final ContentType superType = contentTypeService.getByName( params );

            if ( superType == null )
            {
                registerError( new ContentTypeValidationError( "superType not found: " + superTypeContentName, contentTypeName ) );
                return;
            }
            if ( superType.isFinal() )
            {
                registerError(
                    new ContentTypeValidationError( "Cannot inherit from a final ContentType: " + superType.getName(), contentTypeName ) );
            }
        }
    }

    public static Builder newContentTypeSuperTypeValidator()
    {
        return new Builder();
    }

    private void registerError( final ContentTypeValidationError error )
    {
        resultBuilder.addError( error );
    }

    public ContentTypeValidationResult getResult()
    {
        return resultBuilder.build();
    }

    public static class Builder
    {
        private ContentTypeService contentTypeService;

        public Builder contentTypeService( final ContentTypeService contentTypeService )
        {
            this.contentTypeService = contentTypeService;
            return this;
        }

        public ContentTypeSuperTypeValidator build()
        {
            return new ContentTypeSuperTypeValidator( this.contentTypeService );
        }
    }

}
