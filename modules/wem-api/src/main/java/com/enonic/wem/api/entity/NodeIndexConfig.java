package com.enonic.wem.api.entity;

import com.enonic.wem.api.data.DataPath;

public abstract class NodeIndexConfig
{
    private final String analyzer;

    private final String collection;

    private final boolean decideFulltextByValueType;

    NodeIndexConfig( final Builder builder )
    {
        this.analyzer = builder.analyzer;
        this.collection = builder.collection;
        this.decideFulltextByValueType = builder.decideFulltextByValueType;
    }

    public String getAnalyzer()
    {
        return analyzer;
    }

    public String getCollection()
    {
        return collection;
    }

    public boolean isDecideFulltextByValueType()
    {
        return decideFulltextByValueType;
    }

    public static NodePatternIndexConfig.Builder newPatternIndexConfig()
    {
        return new NodePatternIndexConfig.Builder();
    }

    public abstract PropertyIndexConfig getPropertyIndexConfig( final DataPath dataPath );

    static class Builder<T extends Builder>
    {
        String analyzer;

        String collection;

        boolean decideFulltextByValueType = false;

        public T analyzer( final String analyzer )
        {
            this.analyzer = analyzer;
            return (T) this;
        }

        public T collection( final String collection )
        {
            this.collection = collection;
            return (T) this;
        }

        public T decideFulltextByValueType( final boolean value )
        {
            this.decideFulltextByValueType = value;
            return (T) this;
        }

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

        final NodeIndexConfig that = (NodeIndexConfig) o;

        if ( decideFulltextByValueType != that.decideFulltextByValueType )
        {
            return false;
        }
        if ( analyzer != null ? !analyzer.equals( that.analyzer ) : that.analyzer != null )
        {
            return false;
        }
        if ( collection != null ? !collection.equals( that.collection ) : that.collection != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = analyzer != null ? analyzer.hashCode() : 0;
        result = 31 * result + ( collection != null ? collection.hashCode() : 0 );
        result = 31 * result + ( decideFulltextByValueType ? 1 : 0 );
        return result;
    }
}
