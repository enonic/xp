package com.enonic.wem.api.entity;

import com.enonic.wem.api.data.DataPath;

public abstract class IndexConfigDocumentOldShit
{
    private final String analyzer;

    IndexConfigDocumentOldShit( final Builder builder )
    {
        this.analyzer = builder.analyzer;
    }

    public String getAnalyzer()
    {
        return analyzer;
    }

    public abstract PropertyIndexConfig getIndexConfig( final DataPath dataPath );

    static class Builder<T extends Builder>
    {
        String analyzer;

        @SuppressWarnings("unchecked")
        public T analyzer( final String analyzer )
        {
            this.analyzer = analyzer;
            return (T) this;
        }
    }
}
