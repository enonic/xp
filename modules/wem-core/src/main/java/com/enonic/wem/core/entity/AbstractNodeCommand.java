package com.enonic.wem.core.entity;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.core.entity.dao.NodeDao;
import com.enonic.wem.core.index.IndexService;

public abstract class AbstractNodeCommand
{
    protected final Context context;

    protected final IndexService indexService;

    protected final NodeDao nodeDao;

    public AbstractNodeCommand( final Builder builder )
    {
        this.context = builder.context;
        this.indexService = builder.indexService;
        this.nodeDao = builder.nodeDao;
    }

    public Context getContext()
    {
        return context;
    }

    public static class Builder<B extends Builder>
    {
        private Context context;

        private IndexService indexService;

        private NodeDao nodeDao;

        protected Builder( final Context context )
        {
            this.context = context;
        }

        @SuppressWarnings("unchecked")
        public B indexService( final IndexService indexService )
        {
            this.indexService = indexService;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B nodeDao( final NodeDao nodeDao )
        {
            this.nodeDao = nodeDao;
            return (B) this;
        }


    }

}
