package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

public abstract class AbstractRangeFunctionArg<T>
    implements RangeFunctionArg<T>
{
    String fieldName;

    private boolean includeFrom = false;

    private boolean includeTo = false;

    private T from;

    private T to;

    @Override
    public T getTo()
    {
        return this.to;
    }

    @Override
    public T getFrom()
    {
        return this.from;
    }

    @Override
    public boolean includeFrom()
    {
        return includeFrom;
    }

    public boolean includeTo()
    {
        return includeTo;
    }

    public void setFieldName( final String fieldName )
    {
        this.fieldName = fieldName;
    }

    public void setIncludeFrom( final boolean includeFrom )
    {
        this.includeFrom = includeFrom;
    }

    public void setIncludeTo( final boolean includeTo )
    {
        this.includeTo = includeTo;
    }

    public void setFrom( final T from )
    {
        this.from = from;
    }

    public void setTo( final T to )
    {
        this.to = to;
    }
}
