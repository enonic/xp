package com.enonic.xp.portal.impl.auth;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.auth.AuthDescriptorService;
import com.enonic.xp.portal.impl.error.ErrorHandlerScriptFactory;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.web.filter.OncePerRequestFilter;

@Component(immediate = true, configurationPid = "com.enonic.xp.portal.auth", service = Filter.class,
    property = {"osgi.http.whiteboard.filter.pattern=/", "service.ranking:Integer=30", "osgi.http.whiteboard.filter.dispatcher=FORWARD",
        "osgi.http.whiteboard.filter.dispatcher=REQUEST"})
public final class AuthFilter
    extends OncePerRequestFilter
{
    private static final Pattern PROPERTY_PATTERN = Pattern.compile( "^mapping\\.([^\\.]+)\\.(path|userstore)" );


    private SecurityService securityService;

    private AuthDescriptorService authDescriptorService;

    private ErrorHandlerScriptFactory errorHandlerScriptFactory;

    private Map<String, String> pathConfig;

    private Map<String, String> userstoreConfig;

    @Activate
    public void configure( final Map<String, String> config )
    {
        ImmutableMap.Builder<String, String> pathConfigBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<String, String> userstoreConfigBuilder = ImmutableMap.builder();

        for ( Map.Entry<String, String> property : config.entrySet() )
        {
            final String propertyKey = property.getKey().trim();
            final Matcher matcher = PROPERTY_PATTERN.matcher( propertyKey );
            if ( matcher.matches() )
            {
                if ( "path".equals( matcher.group( 2 ) ) )
                {
                    pathConfigBuilder.put( matcher.group( 1 ), property.getValue().trim() );
                }
                else
                {
                    userstoreConfigBuilder.put( matcher.group( 1 ), property.getValue().trim() );
                }
            }
        }
        this.pathConfig = pathConfigBuilder.build();
        this.userstoreConfig = userstoreConfigBuilder.build();
    }

    @Override
    protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
        throws Exception
    {
        String userstoreName = null;
        final String requestURI = req.getRequestURI();
        final Optional<Map.Entry<String, String>> foundMappingEntry = pathConfig.entrySet().
            stream().
            filter( mappingEntry -> requestURI.startsWith( mappingEntry.getValue() ) ).
            max( ( entry1, entry2 ) -> entry1.getValue().length() - entry2.getValue().length() );

        if ( foundMappingEntry.isPresent() )
        {
            final String mappingName = foundMappingEntry.get().getKey();
            userstoreName = userstoreConfig.get( mappingName );
        }

        if ( userstoreName != null )
        {
            final AuthResponseWrapper responseWrapper =
                new AuthResponseWrapper( req, res, securityService, authDescriptorService, errorHandlerScriptFactory, userstoreName );
            chain.doFilter( req, responseWrapper );
        }
        else
        {
            chain.doFilter( req, res );
        }


    }

    @Reference
    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }

    @Reference
    public void setAuthDescriptorService( final AuthDescriptorService authDescriptorService )
    {
        this.authDescriptorService = authDescriptorService;
    }

    @Reference
    public void setControllerScriptFactory( final ErrorHandlerScriptFactory errorHandlerScriptFactory )
    {
        this.errorHandlerScriptFactory = errorHandlerScriptFactory;
    }
}
