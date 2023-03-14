package com.enonic.xp.index;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public abstract class AbstractIndexConfigDocument
    implements IndexConfigDocument
{
    private final String analyzer;

    AbstractIndexConfigDocument( final Builder builder )
    {
        this.analyzer = builder.analyzer;
    }

    @Override
    public String getAnalyzer()
    {
        return this.analyzer;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final AbstractIndexConfigDocument that = (AbstractIndexConfigDocument) o;
        return Objects.equals( analyzer, that.analyzer );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( analyzer );
    }

    static class Builder<B extends Builder>
    {
        private String analyzer;

        @SuppressWarnings("unchecked")
        public B analyzer( final String analyzer )
        {
            this.analyzer = analyzer;
            return (B) this;
        }


    }

}
