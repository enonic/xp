package com.enonic.xp.schema.content.validator;

import java.util.Iterator;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class ContentTypeValidationResult
    implements Iterable<ContentTypeValidationError>
{
    private final ImmutableList<ContentTypeValidationError> errors;

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
    public String toString()
    {
        return errors.toString();
    }

    public static ContentTypeValidationResult from( final ContentTypeValidationError... errors )
    {
        return new ContentTypeValidationResult( ImmutableList.copyOf( errors ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<ContentTypeValidationError> errors = new ImmutableList.Builder<>();

        private Builder()
        {
        }

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
