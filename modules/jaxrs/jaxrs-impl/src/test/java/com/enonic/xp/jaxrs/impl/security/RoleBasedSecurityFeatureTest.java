package com.enonic.xp.jaxrs.impl.security;

import java.lang.reflect.Method;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class RoleBasedSecurityFeatureTest
{
    public final class Resource1
    {
        public void method()
        {
            // Do nothing
        }
    }

    @RolesAllowed("admin")
    public final class Resource2
    {
        public void method()
        {
            // Do nothing
        }
    }

    public final class Resource3
    {
        @RolesAllowed("admin")
        public void method()
        {
            // Do nothing
        }
    }

    @RolesAllowed("admin")
    @PermitAll
    public final class Resource4
    {
        @PermitAll
        public void method()
        {
            // Do nothing
        }
    }

    @RolesAllowed("admin")
    @DenyAll
    public final class Resource5
    {
        @DenyAll
        public void method()
        {
            // Do nothing
        }
    }

    private ResourceInfo info;

    private FeatureContext context;

    private RoleBasedSecurityFeature feature;

    @Before
    public void setup()
    {
        this.info = Mockito.mock( ResourceInfo.class );
        this.context = Mockito.mock( FeatureContext.class );
        this.feature = new RoleBasedSecurityFeature();
    }

    private void verifyFilter( final int times )
    {
        Mockito.verify( this.context, Mockito.times( times ) ).register( Mockito.any( RoleBasedSecurityFilter.class ),
                                                                         Mockito.eq( Priorities.AUTHORIZATION ) );
    }

    @SuppressWarnings("unchecked")
    private void mockInfo( final Class clz, final String methodName )
        throws Exception
    {
        final Method method = clz.getMethod( methodName );

        Mockito.when( this.info.getResourceClass() ).thenReturn( clz );
        Mockito.when( this.info.getResourceMethod() ).thenReturn( method );
    }

    @Test
    public void class_method_null()
    {
        this.feature.configure( this.info, this.context );
        verifyFilter( 0 );
    }

    @Test
    public void no_annotations()
        throws Exception
    {
        // Just so it's used
        new Resource1().method();

        mockInfo( Resource1.class, "method" );
        this.feature.configure( this.info, this.context );
        verifyFilter( 0 );
    }

    @Test
    public void roles_allowed_class()
        throws Exception
    {
        // Just so it's used
        new Resource2().method();

        mockInfo( Resource2.class, "method" );
        this.feature.configure( this.info, this.context );
        verifyFilter( 1 );
    }

    @Test
    public void roles_allowed_method()
        throws Exception
    {
        // Just so it's used
        new Resource3().method();

        mockInfo( Resource3.class, "method" );
        this.feature.configure( this.info, this.context );
        verifyFilter( 1 );
    }

    @Test
    public void permitAll()
        throws Exception
    {
        // Just so it's used
        new Resource4().method();

        mockInfo( Resource4.class, "method" );
        this.feature.configure( this.info, this.context );
        verifyFilter( 1 );
    }

    @Test
    public void denyAll()
        throws Exception
    {
        // Just so it's used
        new Resource5().method();

        mockInfo( Resource5.class, "method" );
        this.feature.configure( this.info, this.context );
        verifyFilter( 1 );
    }
}
