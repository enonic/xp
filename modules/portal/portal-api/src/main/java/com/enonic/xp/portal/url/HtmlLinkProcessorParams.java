package com.enonic.xp.portal.url;

import java.util.Map;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.html.HtmlElement;

public class HtmlLinkProcessorParams
{
    private final HtmlElement element;

    private final String contentId;

    private final String mode;

    private final String type;

    private final Map<String, String> queryParams;

    private final PortalRequest portalRequest;

    private final Runnable defaultProcessor;

    private HtmlLinkProcessorParams( final Builder builder )
    {
        this.element = builder.element;
        this.contentId = builder.contentId;
        this.mode = builder.mode;
        this.type = builder.type;
        this.portalRequest = builder.portalRequest;
        this.queryParams = builder.queryParams;
        this.defaultProcessor = builder.defaultProcessor;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public HtmlElement getElement()
    {
        return element;
    }

    public String getContentId()
    {
        return contentId;
    }

    public String getMode()
    {
        return mode;
    }

    public String getType()
    {
        return type;
    }

    public Map<String, String> getQueryParams()
    {
        return queryParams;
    }

    public PortalRequest getPortalRequest()
    {
        return portalRequest;
    }

    public void makeDefault()
    {
        if ( defaultProcessor != null )
        {
            defaultProcessor.run();
        }
    }

    public static class Builder
    {
        private HtmlElement element;

        private String contentId;

        private String mode;

        private String type;

        private Map<String, String> queryParams;

        private PortalRequest portalRequest;

        private Runnable defaultProcessor;

        public Builder setElement( final HtmlElement element )
        {
            this.element = element;
            return this;
        }

        public Builder setContentId( final String contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder setMode( final String mode )
        {
            this.mode = mode;
            return this;
        }

        public Builder setType( final String type )
        {
            this.type = type;
            return this;
        }

        public Builder setQueryParams( final Map<String, String> queryParams )
        {
            this.queryParams = queryParams;
            return this;
        }

        public Builder setPortalRequest( final PortalRequest portalRequest )
        {
            this.portalRequest = portalRequest;
            return this;
        }

        public Builder setDefaultProcessor( final Runnable defaultProcessor )
        {
            this.defaultProcessor = defaultProcessor;
            return this;
        }

        public HtmlLinkProcessorParams build()
        {
            return new HtmlLinkProcessorParams( this );
        }
    }
}
