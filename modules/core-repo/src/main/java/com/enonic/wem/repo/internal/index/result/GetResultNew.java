package com.enonic.wem.repo.internal.index.result;

public class GetResultNew
{
    private final String id;

    private final ResultFieldValues resultFieldValues;

    private GetResultNew( Builder builder )
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

    private GetResultNew()
    {
        this.id = null;
        this.resultFieldValues = null;
    }

    public static GetResultNew empty()
    {
        return new GetResultNew();
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

        public GetResultNew build()
        {
            return new GetResultNew( this );
        }
    }
}