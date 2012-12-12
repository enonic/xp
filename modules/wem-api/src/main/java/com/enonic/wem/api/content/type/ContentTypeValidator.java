package com.enonic.wem.api.content.type;

import java.util.ArrayList;
import java.util.List;

public class ContentTypeValidator
{

    final private ContentTypeFetcher contentTypeFetcher;

    final private boolean recordExceptions;

    private List<InvalidContentTypeException> invalidContentTypeExceptions = new ArrayList<InvalidContentTypeException>();

    private ContentTypeValidator( final ContentTypeFetcher contentTypeFetcher, final boolean recordExceptions )
    {
        this.contentTypeFetcher = contentTypeFetcher;
        this.recordExceptions = recordExceptions;
    }

    public void validate( ContentType contentType )
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
                registerInvalidContentTypeException( new CannotInheritFromFinalContentTypeException( contentType ) );
            }
        }
    }

    private void registerInvalidContentTypeException( final InvalidContentTypeException invalidContentTypeException )
    {
        if ( !recordExceptions )
        {
            throw invalidContentTypeException;
        }
        invalidContentTypeExceptions.add( invalidContentTypeException );
    }

    public List<InvalidContentTypeException> getInvalidContentTypeExceptions()
    {
        return invalidContentTypeExceptions;
    }

    public static Builder newContentTypeValidator()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ContentTypeFetcher contentTypeFetcher;

        private boolean recordExceptions;

        public Builder recordExceptions( final boolean value )
        {
            this.recordExceptions = value;
            return this;
        }

        public Builder contentTypeFetcher( final ContentTypeFetcher value )
        {
            this.contentTypeFetcher = value;
            return this;
        }

        public ContentTypeValidator build()
        {
            return new ContentTypeValidator( contentTypeFetcher, recordExceptions );
        }
    }
}
