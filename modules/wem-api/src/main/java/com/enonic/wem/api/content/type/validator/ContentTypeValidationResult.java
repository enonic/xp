package com.enonic.wem.api.content.type.validator;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;

public class ContentTypeValidationResult
    implements Iterable<ContentTypeValidationError>
{
    private ImmutableList<ContentTypeValidationError> errors;

    private ContentTypeValidationResult( final ImmutableList<ContentTypeValidationError> errors )
    {
        this.errors = errors;
    }

    public ContentTypeValidationError getFirst()
    {
        return errors.isEmpty() ? null : errors.iterator().next();
    }

    public boolean hasErrors()
    {
        return !errors.isEmpty();
    }

    @Override
    public Iterator<ContentTypeValidationError> iterator()
    {
        return errors.iterator();
    }

    public int hashCode()
    {
        return errors.hashCode();
    }

    public String toString()
    {
        return errors.toString();
    }

    public boolean equals( final Object o )
    {
        return ( o instanceof ContentTypeValidationResult ) && this.errors.equals( ( (ContentTypeValidationResult) o ).errors );
    }


    public static ContentTypeValidationResult from( final List<ContentTypeValidationError> exceptions )
    {
        return new ContentTypeValidationResult( ImmutableList.copyOf( exceptions ) );
    }

    public static ContentTypeValidationResult from( final ContentTypeValidationError... exceptions )
    {
        return new ContentTypeValidationResult( ImmutableList.copyOf( exceptions ) );
    }

    public static ContentTypeValidationResult from( final Iterable<ContentTypeValidationError> exceptions )
    {
        return new ContentTypeValidationResult( ImmutableList.copyOf( exceptions ) );
    }

    public static ContentTypeValidationResult empty()
    {
        final ImmutableList<ContentTypeValidationError> list = ImmutableList.of();
        return new ContentTypeValidationResult( list );
    }

    public static Builder newContentTypeValidationResult()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ImmutableList.Builder<ContentTypeValidationError> errors = new ImmutableList.Builder<ContentTypeValidationError>();

        public Builder addError( ContentTypeValidationError error )
        {
            this.errors.add( error );
            return this;
        }

        public ContentTypeValidationResult build()
        {
            return new ContentTypeValidationResult( errors.build() );
        }
    }

}
