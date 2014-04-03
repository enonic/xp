package com.enonic.wem.api.entity;

import com.enonic.wem.api.data.DataPath;

public abstract class EntityIndexConfig
{
    private final String analyzer;

    private final String collection;

    private final boolean decideFulltextByValueType;


    EntityIndexConfig( final Builder builder )
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

    public static EntityPatternIndexConfig.Builder newPatternIndexConfig()
    {
        return new EntityPatternIndexConfig.Builder();
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

}
