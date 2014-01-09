package com.enonic.wem.api.entity;

import com.enonic.wem.api.data.DataPath;

public abstract class EntityIndexConfig
{
    private final String analyzer;

    private final String collection;

    EntityIndexConfig( final String analyzer, final String collection )
    {
        this.analyzer = analyzer;
        this.collection = collection;
    }

    public String getAnalyzer()
    {
        return analyzer;
    }

    public String getCollection()
    {
        return collection;
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

    }

}
