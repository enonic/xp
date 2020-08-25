package com.enonic.xp.admin.impl.rest.resource.content;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.UriInfo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CmsResourceDynamicFeatureTest
{
    private CmsResourceFilter filter;

    private ContainerRequestContext context;

    @BeforeEach
    public void init()
    {
        this.context = Mockito.mock( ContainerRequestContext.class );
        this.filter = new CmsResourceFilter();

        ContextAccessor.INSTANCE.set( ContextBuilder.create().
            repositoryId( ContentConstants.CONTENT_REPO_ID ).
            build() );
    }

    @Test
    public void default_project()
        throws IOException
    {
        final UriInfo uriInfo = Mockito.mock( UriInfo.class );
        Mockito.when( uriInfo.getPath() ).thenReturn( "/admin/rest/content/update" );

        Mockito.when( context.getUriInfo() ).thenReturn( uriInfo );
        this.filter.filter( context );

        assertEquals( "com.enonic.cms.default", ContextAccessor.current().getRepositoryId().toString() );
    }

    @Test
    public void set_project()
        throws IOException
    {
        final UriInfo uriInfo = Mockito.mock( UriInfo.class );
        Mockito.when( uriInfo.getPath() ).thenReturn( "/admin/rest/cms/project1/layer1/update" );

        Mockito.when( context.getUriInfo() ).thenReturn( uriInfo );
        this.filter.filter( context );

        assertEquals( "com.enonic.cms.project1", ContextAccessor.current().getRepositoryId().toString() );
    }
}
