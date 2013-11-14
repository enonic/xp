package com.enonic.wem.api.support;


public class PossibleChange<T>
{
    public final String property;

    public final T from;

    public final T to;

    public final boolean isChange;

    private PossibleChange( final Builder<T> builder )
    {
        this.property = builder.property;
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
        else
        {
            this.isChange = !from.equals( to );
        }
    }

    void addChange( Changes.Builder builder )
    {
        if ( isChange )
        {
            builder.recordChange( new Change<T>( this.property, this.from, this.to ) );
        }
    }

    public static <T> Builder<T> newPossibleChange( final String property )
    {
        return new Builder<>( property );
    }

    public static class Builder<T>
    {
        private String property;

        private T from;

        private T to;

        public Builder( final String property )
        {
            this.property = property;
        }

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

        public PossibleChange<T> build()
        {
            return new PossibleChange<>( this );
        }
    }
}
