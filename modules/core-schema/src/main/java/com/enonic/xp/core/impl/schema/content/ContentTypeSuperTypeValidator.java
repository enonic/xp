package com.enonic.xp.core.impl.schema.content;

import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.content.validator.ContentTypeValidationError;
import com.enonic.xp.schema.content.validator.ContentTypeValidationResult;

final class ContentTypeSuperTypeValidator
{
    private final ContentTypeService contentTypeService;

    private final ContentTypeValidationResult.Builder resultBuilder = ContentTypeValidationResult.create();

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

    public static Builder create()
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
