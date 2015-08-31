package com.enonic.wem.repo.internal.index.result;

public class GetResult
{
    private final String id;

    private final ResultFieldValues resultFieldValues;

    private GetResult( Builder builder )
    {
        id = builder.id;
        resultFieldValues = builder.resultFieldValues;
    }

    public ResultFieldValues getResultFieldValues()
    {
        return resultFieldValues;
    }

    public static Builder create()
    {
        return new Builder();
    }

    private GetResult()
    {
        this.id = null;
        this.resultFieldValues = null;
    }

    public static GetResult empty()
    {
        return new GetResult();
    }

    public boolean isEmpty()
    {
        return this.id == null;
    }

    public static final class Builder
    {
        private String id;

        private ResultFieldValues resultFieldValues;

        private Builder()
        {
        }

        public Builder id( String id )
        {
            this.id = id;
            return this;
        }

        public Builder resultFieldValues( final ResultFieldValues resultFieldValues )
        {
            this.resultFieldValues = resultFieldValues;
            return this;
        }

        public GetResult build()
        {
            return new GetResult( this );
        }
    }
}