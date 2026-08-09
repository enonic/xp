package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.repo.impl.ReturnValues;

public class GetResult
{
    private final String id;

    private final ReturnValues returnValues;

    private GetResult( Builder builder )
    {
        id = builder.id;
        returnValues = builder.returnValues;
    }

    public ReturnValues getReturnValues()
    {
        return returnValues;
    }

    public String getId()
    {
        return id;
    }

    public static Builder create()
    {
        return new Builder();
    }

    private GetResult()
    {
        this.id = null;
        this.returnValues = null;
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

        private ReturnValues returnValues;

        private Builder()
        {
        }

        public Builder id( String id )
        {
            this.id = id;
            return this;
        }

        public Builder resultFieldValues( final ReturnValues returnValues )
        {
            this.returnValues = returnValues;
            return this;
        }

        public GetResult build()
        {
            return new GetResult( this );
        }
    }
}