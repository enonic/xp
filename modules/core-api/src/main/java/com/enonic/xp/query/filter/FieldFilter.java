package com.enonic.xp.query.filter;

public abstract class FieldFilter
    extends Filter
{
    private final String fieldName;

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
