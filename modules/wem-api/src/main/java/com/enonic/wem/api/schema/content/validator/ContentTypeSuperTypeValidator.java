package com.enonic.wem.api.schema.content.validator;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;

import static com.enonic.wem.api.schema.content.validator.ContentTypeValidationResult.newContentTypeValidationResult;

public class ContentTypeSuperTypeValidator
{
    private final Client client;

    private final ContentTypeValidationResult.Builder resultBuilder = newContentTypeValidationResult();

    public ContentTypeSuperTypeValidator( final Client client )
    {
        this.client = client;
    }

    public void validate( final ContentTypeName contentTypeName, final ContentTypeName superTypeContentName )
    {
        if ( superTypeContentName != null )
        {
            ContentType superType = client.execute( Commands.contentType().get().byName().contentTypeName( superTypeContentName ) );

            if ( superType == null )
            {
                registerError( new ContentTypeValidationError( "superType not found: " + superTypeContentName, contentTypeName ) );
                return;
            }
            if ( superType.isFinal() )
            {
                registerError( new ContentTypeValidationError( "Cannot inherit from a final ContentType: " + superType.getContentTypeName(),
                                                               contentTypeName ) );
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
        private Client client;

        public Builder client( final Client value )
        {
            this.client = value;
            return this;
        }

        public ContentTypeSuperTypeValidator build()
        {
            return new ContentTypeSuperTypeValidator( client );
        }
    }

}
