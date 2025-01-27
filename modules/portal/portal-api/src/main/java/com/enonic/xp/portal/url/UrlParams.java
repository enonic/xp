package com.enonic.xp.portal.url;

import java.util.Objects;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.web.WebRequest;

public abstract class UrlParams
{
    private final WebRequest webRequest;

    private final String contentId;

    private final String contentPath;

    private final String urlType;

    private final ContextPathType contextPathType;

    private final Multimap<String, String> queryParams;

    protected UrlParams( final Builder builder )
    {
        this.urlType = Objects.requireNonNullElse( builder.urlType, UrlTypeConstants.SERVER_RELATIVE );
        this.contextPathType = Objects.requireNonNullElse( builder.contextPathType, ContextPathType.RELATIVE );
        this.webRequest = builder.webRequest;
        this.contentId = builder.contentId;
        this.contentPath = builder.contentPath;
        this.queryParams = builder.queryParams;
    }

    public WebRequest getWebRequest()
    {
        return webRequest;
    }

    public String getContentId()
    {
        return contentId;
    }

    public String getContentPath()
    {
        return contentPath;
    }

    public String getUrlType()
    {
        return urlType;
    }

    public ContextPathType getContextPathType()
    {
        return contextPathType;
    }

    public Multimap<String, String> getQueryParams()
    {
        return queryParams;
    }

    public static abstract class Builder<B extends Builder>
    {
        private WebRequest webRequest;

        private String contentId;

        private String contentPath;

        private String urlType;

        private ContextPathType contextPathType;

        private final Multimap<String, String> queryParams = HashMultimap.create();

        Builder()
        {

        }

        @SuppressWarnings("unchecked")
        public B webRequest( final WebRequest webRequest )
        {
            this.webRequest = webRequest;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B contentId( final String contentId )
        {
            this.contentId = contentId;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B contentPath( final String contentPath )
        {
            this.contentPath = contentPath;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B urlType( final String urlType )
        {
            this.urlType = urlType;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B contextPathType( final ContextPathType contextPathType )
        {
            this.contextPathType = contextPathType;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B addQueryParam( final String key, final String value )
        {
            this.queryParams.put( key, value );
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B addQueryParams( final Multimap<String, String> params )
        {
            if ( params != null )
            {
                this.queryParams.putAll( params );
            }
            return (B) this;
        }
    }
}
