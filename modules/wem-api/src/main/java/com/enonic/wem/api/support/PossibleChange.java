package com.enonic.wem.api.support;


public class PossibleChange<T>
{
    public final T from;

    public final T to;

    public final boolean isChange;

    private PossibleChange( final Builder<T> builder )
    {
        this.from = builder.from;
        this.to = builder.to;

        if ( from == null && to == null )
        {
            this.isChange = false;
        }
        else if ( from == null )
        {
            this.isChange = true;
        }
        else if ( to == null )
        {
            this.isChange = true;
        }
        else if ( builder.checkMethod == CheckMethod.EQUALS )
        {
            this.isChange = !from.equals( to );
        }
        else
        {
            this.isChange = from != to;
        }
    }

    void addChange( Changes.Builder builder )
    {
        if ( isChange )
        {
            builder.recordChange( new Change<T>( this.from, this.to ) );
        }
    }

    public static <T> Builder<T> newPossibleChange()
    {
        return new Builder<>();
    }

    public static class Builder<T>
    {
        private CheckMethod checkMethod = CheckMethod.EQUALS;

        private T from;

        private T to;

        public Builder<T> from( T from )
        {
            this.from = from;
            return this;
        }

        public Builder<T> to( T to )
        {
            this.to = to;
            return this;
        }

        public Builder<T> checkMethod( CheckMethod value )
        {
            this.checkMethod = value;
            return this;
        }

        public PossibleChange<T> build()
        {
            return new PossibleChange<>( this );
        }
    }

    public static enum CheckMethod
    {
        EQUALS, INSTANCE
    }
}
