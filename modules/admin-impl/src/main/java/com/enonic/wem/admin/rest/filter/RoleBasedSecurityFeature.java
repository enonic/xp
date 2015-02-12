package com.enonic.wem.admin.rest.filter;

import java.lang.reflect.Method;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import org.osgi.service.component.annotations.Component;

import com.enonic.wem.admin.AdminResource;

@Component(immediate = true)
@Provider
public final class RoleBasedSecurityFeature
    implements DynamicFeature, AdminResource
{
    @Override
    public void configure( final ResourceInfo resourceInfo, final FeatureContext configurable )
    {
        final Class declaring = resourceInfo.getResourceClass();
        final Method method = resourceInfo.getResourceMethod();

        if ( declaring == null || method == null )
        {
            return;
        }

        String[] rolesAllowed = null;
        boolean denyAll;
        boolean permitAll;

        RolesAllowed allowed = (RolesAllowed) declaring.getAnnotation( RolesAllowed.class );
        RolesAllowed methodAllowed = method.getAnnotation( RolesAllowed.class );

        if ( methodAllowed != null )
        {
            allowed = methodAllowed;
        }

        if ( allowed != null )
        {
            rolesAllowed = allowed.value();
        }

        denyAll = ( declaring.isAnnotationPresent( DenyAll.class ) && !method.isAnnotationPresent( RolesAllowed.class ) &&
            !method.isAnnotationPresent( PermitAll.class ) ) || method.isAnnotationPresent( DenyAll.class );

        permitAll = ( declaring.isAnnotationPresent( PermitAll.class ) && !method.isAnnotationPresent( RolesAllowed.class ) &&
            !method.isAnnotationPresent( DenyAll.class ) ) || method.isAnnotationPresent( PermitAll.class );

        if ( rolesAllowed != null || denyAll || permitAll )
        {
            final RoleBasedSecurityFilter filter = new RoleBasedSecurityFilter( rolesAllowed, denyAll, permitAll );
            configurable.register( filter, Priorities.AUTHORIZATION );
        }
    }
}
