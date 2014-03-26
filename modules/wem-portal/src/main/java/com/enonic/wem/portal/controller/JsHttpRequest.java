package com.enonic.wem.portal.controller;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.core.header.QualitySourceMediaType;

import com.enonic.wem.api.rendering.RenderingMode;

public class JsHttpRequest
{
    private final HttpRequestContext raw;

    private final MultivaluedMap<String, String> queryParameters;

    private RenderingMode mode;

    public JsHttpRequest( final HttpRequestContext raw )
    {
        this.raw = raw;
        this.queryParameters = raw.getQueryParameters();
    }

    public String getMethod()
    {
        return this.raw.getMethod();
    }

    public MultivaluedMap<String, String> getParams()
    {
        return queryParameters;
    }

    public RenderingMode getMode()
    {
        return mode;
    }

    public void setMode( final RenderingMode mode )
    {
        this.mode = mode;
    }


    public URI getBaseUri()
    {
        return this.raw.getBaseUri();
    }

    public UriBuilder getBaseUriBuilder()
    {
        return this.raw.getBaseUriBuilder();
    }

    public URI getRequestUri()
    {
        return this.raw.getRequestUri();
    }

    public UriBuilder getRequestUriBuilder()
    {
        return this.raw.getRequestUriBuilder();
    }

    public URI getAbsolutePath()
    {
        return this.raw.getAbsolutePath();
    }

    public UriBuilder getAbsolutePathBuilder()
    {
        return this.raw.getAbsolutePathBuilder();
    }

    public String getPath()
    {
        return this.raw.getPath();
    }

    public String getPath( boolean decode )
    {
        return this.raw.getPath( decode );
    }

    public List<PathSegment> getPathSegments()
    {
        return this.raw.getPathSegments();
    }

    public List<PathSegment> getPathSegments( boolean decode )
    {
        return this.raw.getPathSegments( decode );
    }

    public MultivaluedMap<String, String> getQueryParameters()
    {
        return this.raw.getQueryParameters();
    }

    public MultivaluedMap<String, String> getQueryParameters( boolean decode )
    {
        return this.raw.getQueryParameters( decode );
    }

    public String getHeaderValue( String name )
    {
        return this.raw.getHeaderValue( name );
    }

    public MediaType getAcceptableMediaType( List<MediaType> mediaTypes )
    {
        return this.raw.getAcceptableMediaType( mediaTypes );
    }

    public List<MediaType> getAcceptableMediaTypes( List<QualitySourceMediaType> qualitySourceMediaTypes )
    {
        return this.raw.getAcceptableMediaTypes( qualitySourceMediaTypes );
    }

    public MultivaluedMap<String, String> getCookieNameValueMap()
    {
        return this.raw.getCookieNameValueMap();
    }

    public <T> T getEntity( Class<T> tClass )
        throws WebApplicationException
    {
        return this.raw.getEntity( tClass );
    }

    public <T> T getEntity( Class<T> tClass, Type type, Annotation[] annotations )
        throws WebApplicationException
    {
        return this.raw.getEntity( tClass, type, annotations );
    }

    public Form getFormParameters()
    {
        return this.raw.getFormParameters();
    }

}
