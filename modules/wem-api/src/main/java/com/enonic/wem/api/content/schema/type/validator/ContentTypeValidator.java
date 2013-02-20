package com.enonic.wem.api.content.schema.type.validator;

import com.enonic.wem.api.content.schema.type.ContentType;
import com.enonic.wem.api.content.schema.type.ContentTypeFetcher;

import static com.enonic.wem.api.content.schema.type.validator.ContentTypeValidationResult.newContentTypeValidationResult;

/**
 * Validator for validating that a ContentType is setup correctly.
 */
public class ContentTypeValidator
{
    private final ContentTypeFetcher contentTypeFetcher;

    private final ContentTypeValidationResult.Builder resultBuilder = newContentTypeValidationResult();

    private ContentTypeValidator( final ContentTypeFetcher contentTypeFetcher )
    {
        this.contentTypeFetcher = contentTypeFetcher;
    }

    public ContentTypeValidationResult getResult()
    {
        return resultBuilder.build();
    }

    public void validate( final ContentType contentType )
    {
        doValidateContentType( contentType );
    }

    private void doValidateContentType( final ContentType contentType )
    {
        if ( contentType.getSuperType() != null )
        {
            ContentType superType = contentTypeFetcher.getContentType( contentType.getSuperType() );
            if ( superType.isFinal() )
            {
                registerError( new ContentTypeValidationError( "Cannot inherit from a final ContentType: " + superType.getQualifiedName(),
                                                               contentType ) );
            }
        }
    }

    private void registerError( final ContentTypeValidationError error )
    {
        resultBuilder.addError( error );
    }

    public static Builder newContentTypeValidator()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ContentTypeFetcher contentTypeFetcher;

        public Builder contentTypeFetcher( final ContentTypeFetcher value )
        {
            this.contentTypeFetcher = value;
            return this;
        }

        public ContentTypeValidator build()
        {
            return new ContentTypeValidator( contentTypeFetcher );
        }
    }
}
