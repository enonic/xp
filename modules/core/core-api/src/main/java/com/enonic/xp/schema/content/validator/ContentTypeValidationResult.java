package com.enonic.xp.schema.content.validator;

import java.util.Iterator;
import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

@Beta
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

    @Override
    public int hashCode()
    {
        return errors.hashCode();
    }

    @Override
    public String toString()
    {
        return errors.toString();
    }

    @Override
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

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ImmutableList.Builder<ContentTypeValidationError> errors = new ImmutableList.Builder<>();

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
