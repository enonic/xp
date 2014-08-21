package com.enonic.wem.portal.internal.content;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.rendering.RenderingMode;

public class GetContentArguments
{
    private final RenderingMode mode;

    private final String contentSelector;

    private final Context context;

    private GetContentArguments( final Builder builder )
    {
        this.mode = builder.mode;
        this.contentSelector = builder.contentSelector;
        this.context = builder.context;
    }

    public RenderingMode getMode()
    {
        return mode;
    }

    public String getContentSelector()
    {
        return contentSelector;
    }

    public Context getContext()
    {
        return context;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private RenderingMode mode;

        private String contentSelector;

        private Context context;

        public Builder mode( final RenderingMode mode )
        {
            this.mode = mode;
            return this;
        }

        public Builder contentSelector( final String contentSelector )
        {
            this.contentSelector = contentSelector;
            return this;
        }

        public Builder context( final Context context )
        {
            this.context = context;
            return this;
        }

        public GetContentArguments build()
        {
            return new GetContentArguments( this );
        }
    }

}
