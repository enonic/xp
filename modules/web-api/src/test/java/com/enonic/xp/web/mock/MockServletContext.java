package com.enonic.xp.web.mock;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;

public class MockServletContext
    implements ServletContext
{
    @Override
    public String getContextPath()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletContext getContext( final String uripath )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getMajorVersion()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getMinorVersion()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getEffectiveMajorVersion()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getEffectiveMinorVersion()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getMimeType( final String file )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getResourcePaths( final String path )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public URL getResource( final String path )
        throws MalformedURLException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream getResourceAsStream( final String path )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public RequestDispatcher getRequestDispatcher( final String path )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public RequestDispatcher getNamedDispatcher( final String name )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Servlet getServlet( final String name )
        throws ServletException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Enumeration<Servlet> getServlets()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Enumeration<String> getServletNames()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void log( final String msg )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void log( final Exception exception, final String msg )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void log( final String message, final Throwable throwable )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRealPath( final String path )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getServerInfo()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getInitParameter( final String name )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Enumeration<String> getInitParameterNames()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setInitParameter( final String name, final String value )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getAttribute( final String name )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Enumeration<String> getAttributeNames()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAttribute( final String name, final Object object )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAttribute( final String name )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getServletContextName()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletRegistration.Dynamic addServlet( final String servletName, final String className )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletRegistration.Dynamic addServlet( final String servletName, final Servlet servlet )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletRegistration.Dynamic addServlet( final String servletName, final Class<? extends Servlet> servletClass )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends Servlet> T createServlet( final Class<T> clazz )
        throws ServletException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletRegistration getServletRegistration( final String servletName )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public FilterRegistration.Dynamic addFilter( final String filterName, final String className )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public FilterRegistration.Dynamic addFilter( final String filterName, final Filter filter )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public FilterRegistration.Dynamic addFilter( final String filterName, final Class<? extends Filter> filterClass )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends Filter> T createFilter( final Class<T> clazz )
        throws ServletException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public FilterRegistration getFilterRegistration( final String filterName )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, ? extends FilterRegistration> getFilterRegistrations()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public SessionCookieConfig getSessionCookieConfig()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSessionTrackingModes( final Set<SessionTrackingMode> sessionTrackingModes )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addListener( final String className )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends EventListener> void addListener( final T t )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addListener( final Class<? extends EventListener> listenerClass )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends EventListener> T createListener( final Class<T> clazz )
        throws ServletException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public JspConfigDescriptor getJspConfigDescriptor()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClassLoader getClassLoader()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void declareRoles( final String... roleNames )
    {
        throw new UnsupportedOperationException();
    }
}
