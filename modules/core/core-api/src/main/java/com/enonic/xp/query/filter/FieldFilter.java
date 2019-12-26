package com.enonic.xp.query.filter;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public abstract class FieldFilter
    extends Filter
{
    protected final String fieldName;

    protected FieldFilter( final Builder builder )
    {
        super( builder );
        this.fieldName = builder.fieldName;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public static class Builder<B extends Builder>
        extends Filter.Builder<B>
    {
        private String fieldName;

        @SuppressWarnings("unchecked")
        public B fieldName( final String fieldName )
        {
            this.fieldName = fieldName;
            return (B) this;
        }

    }


}
