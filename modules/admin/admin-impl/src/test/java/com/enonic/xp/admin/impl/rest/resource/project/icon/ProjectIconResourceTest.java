package com.enonic.xp.admin.impl.rest.resource.project.icon;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.jaxrs.impl.MockRestResponse;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectIconResourceTest
    extends AdminResourceTestSupport
{
    @TempDir
    public static Path temporaryFolder;

    private static byte[] iconSourceData;

    private ProjectService projectService;

    @BeforeAll
    public static void beforeAll()
        throws IOException
    {
        System.setProperty( "xp.home", temporaryFolder.toFile().getPath() );

        try (InputStream stream = ProjectIconResourceTest.class.getResourceAsStream( "projecticon1.png" ))
        {
            iconSourceData = stream.readAllBytes();
        }
    }

    @BeforeEach
    public void beforeEach()
    {
        final Project project = createProject( "project1", "project name", "project description", Attachment.create().
            name( "logo.png" ).
            mimeType( "image/png" ).
            label( "small" ).
            build() );

        Mockito.when( projectService.get( project.getName() ) ).thenReturn( project );
        Mockito.when( projectService.getIcon( project.getName() ) ).
            thenReturn( ByteSource.wrap( iconSourceData ) );
    }

    @Override
    protected ProjectIconResource getResourceInstance()
    {
        projectService = Mockito.mock( ProjectService.class );

        final ProjectIconResource resource = new ProjectIconResource();
        resource.setProjectService( projectService );

        return resource;
    }

    @Test
    public void get_icon()
        throws Exception
    {
        final MockRestResponse response = request().
            path( "project/icon/project1" ).
            queryParam( "scaleSize", "0" ).
            get();

        assertTrue( Arrays.equals( iconSourceData, response.getData() ) );
    }

    @Test
    public void test_timestamp_header()
        throws Exception
    {
        final MockRestResponse response = request().
            path( "project/icon/project1" ).
            queryParam( "ts", "123" ).
            get();

        assertEquals( "no-transform, max-age=2147483647", response.getHeader( "Cache-Control" ) );
    }

    @Test
    public void test_project_not_found()
        throws Exception
    {
        Mockito.when( projectService.get( ProjectName.from( "project1" ) ) ).thenReturn( null );
        final MockRestResponse response = request().path( "project/icon/project1" ).get();

        assertEquals( 500, response.getStatus() );
    }

    @Test
    public void test_icon_not_found()
        throws Exception
    {
        Mockito.when( projectService.getIcon( ProjectName.from( "project1" ) ) ).
            thenReturn( null );
        final MockRestResponse response = request().path( "project/icon/project1" ).get();

        assertEquals( 500, response.getStatus() );
    }


    private Project createProject( final String name, final String displayName, final String description, final Attachment icon )
    {
        return Project.create().
            name( ProjectName.from( name ) ).
            displayName( displayName ).
            description( description ).
            icon( icon ).
            build();
    }
}
