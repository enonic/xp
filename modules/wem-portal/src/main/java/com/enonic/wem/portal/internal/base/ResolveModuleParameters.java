package com.enonic.wem.portal.internal.base;

import com.enonic.wem.api.context.Context;

public class ResolveModuleParameters
{
    private final String contentPath;

    private final String module;

    private final Context context;

    private ResolveModuleParameters( Builder builder )
    {
        contentPath = builder.contentPath;
        module = builder.module;
        context = builder.context;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public String getContentPath()
    {
        return contentPath;
    }

    public String getModule()
    {
        return module;
    }

    public Context getContext()
    {
        return context;
    }

    public static final class Builder
    {
        private String contentPath;

        private String module;

        private Context context;

        private Builder()
        {
        }

        public Builder contentPath( String contentPath )
        {
            this.contentPath = contentPath;
            return this;
        }

        public Builder module( String module )
        {
            this.module = module;
            return this;
        }

        public Builder context( Context context )
        {
            this.context = context;
            return this;
        }

        public ResolveModuleParameters build()
        {
            return new ResolveModuleParameters( this );
        }
    }
}
