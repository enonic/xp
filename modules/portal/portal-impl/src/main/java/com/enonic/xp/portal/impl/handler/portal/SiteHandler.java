package com.enonic.xp.portal.impl.handler.portal;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.handler.BaseSiteHandler;
import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionMapper;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.handler.WebHandler;

import static com.google.common.base.Strings.nullToEmpty;

@Component(immediate = true, service = WebHandler.class, configurationPid = "com.enonic.xp.portal")
public class SiteHandler
    extends BaseSiteHandler
{
    private static final String SITE_BASE = "/site";
    private static final String SITE_PREFIX = SITE_BASE + "/";

    private List<PrincipalKey> draftBranchAllowedFor;

    @Activate
    @Modified
    public void activate( final PortalConfig config )
    {
        this.draftBranchAllowedFor = Arrays.stream( nullToEmpty( config.draftBranchAllowedFor() ).split( ",", -1 ) )
            .map( String::trim )
            .map( PrincipalKey::from )
            .collect( Collectors.toList() );
    }

    @Override
    protected boolean canHandle( final WebRequest webRequest )
    {
        return webRequest.getRawPath().startsWith( SITE_PREFIX );
    }

    @Override
    protected PortalRequest createPortalRequest( final WebRequest webRequest, final WebResponse webResponse )
    {
        final String baseSubPath = webRequest.getRawPath().substring( ( SITE_PREFIX.length() ) );
        final PortalRequest portalRequest = doCreatePortalRequest( webRequest, SITE_BASE, baseSubPath );

        if ( ContentConstants.BRANCH_DRAFT.equals( portalRequest.getBranch() ) )
        {
            final String endpointPath = portalRequest.getEndpointPath();
            if ( endpointPath != null && ( endpointPath.startsWith( "/_/asset/" ) || endpointPath.startsWith( "/_/idprovider/" ) ) )
            {
                return portalRequest;
            }
            final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
            if ( !authInfo.hasRole( RoleKeys.ADMIN ) && authInfo.getPrincipals().stream().noneMatch( draftBranchAllowedFor::contains ) )
            {
                throw WebException.forbidden( "You don't have permission to access this resource" );
            }
        }

        return portalRequest;
    }

    @Reference
    public void setWebExceptionMapper( final ExceptionMapper exceptionMapper )
    {
        this.exceptionMapper = exceptionMapper;
    }

    @Reference
    public void setExceptionRenderer( final ExceptionRenderer exceptionRenderer )
    {
        this.exceptionRenderer = exceptionRenderer;
    }
}
