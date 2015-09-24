package com.enonic.xp.page;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.form.Form;
import com.enonic.xp.resource.FileResource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeys;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.mixin.MixinService;

public abstract class AbstractDescriptorServiceTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File applicationsDir;

    protected ApplicationService applicationService;

    protected ResourceService resourceService;

    protected MixinService mixinService;

    @Before
    public final void setup()
        throws Exception
    {
        this.applicationsDir = this.temporaryFolder.newFolder( "applications" );
        this.applicationService = Mockito.mock( ApplicationService.class );
        resourceService = Mockito.mock( ResourceService.class );
        Mockito.when( resourceService.getResource( Mockito.any() ) ).thenAnswer( invocation -> {
            final ResourceKey resourceKey = (ResourceKey) invocation.getArguments()[0];
            final String path = resourceKey.getApplicationKey().toString() + resourceKey.getPath().toString();
            final File resourceFile = new File( applicationsDir, path );
            return new FileResource( resourceKey, resourceFile );
        } );
        mixinService = Mockito.mock( MixinService.class );
        Mockito.when( mixinService.inlineFormItems( Mockito.any() ) ).thenReturn( Form.create().build() );
    }

    protected final void createFile( final ResourceKey key, final String content )
        throws Exception
    {
        final String path = key.getApplicationKey().toString() + key.getPath();
        final File file = new File( this.applicationsDir, path );
        Assert.assertTrue( file.getParentFile().mkdirs() );

        Files.write( content, file, Charsets.UTF_8 );
    }

    protected final DescriptorKey createDescriptor( final String key )
        throws Exception
    {
        final DescriptorKey descriptorKey = DescriptorKey.from( key );
        final ResourceKey resourceKey = toResourceKey( descriptorKey );
        final String xml = toDescriptorXml( descriptorKey );

        createFile( resourceKey, xml );
        return descriptorKey;
    }

    protected final void createDescriptors( final String... keys )
        throws Exception
    {
        for ( final String key : keys )
        {
            createDescriptor( key );
        }
    }

    protected abstract ResourceKey toResourceKey( DescriptorKey key );

    protected abstract String toDescriptorXml( DescriptorKey key );

    protected final Application createApplication( final String key )
    {
        final ApplicationKey applicationKey = ApplicationKey.from( key );

        final Application application = Mockito.mock( Application.class );
        Mockito.when( application.getKey() ).thenReturn( applicationKey );

        Mockito.when( this.applicationService.getApplication( applicationKey ) ).thenReturn( application );
        return application;
    }

    protected final Applications createApplications( final String... keys )
    {
        final List<Application> list = Lists.newArrayList();
        for ( final String key : keys )
        {
            list.add( createApplication( key ) );
        }

        final Applications applications = Applications.from( list );
        Mockito.when( this.applicationService.getAllApplications() ).thenReturn( applications );
        Mockito.when( this.applicationService.getApplications( applications.getApplicationKeys() ) ).thenReturn( applications );
        return applications;
    }

    protected final void mockResources( final Application application, final String rootPath, final String filePattern,
                                        final boolean recurse, final String... paths )
        throws MalformedURLException
    {
        List<ResourceKey> resourceKeyList = new ArrayList<ResourceKey>();
        for ( final String path : paths )
        {
            final ResourceKey resourceKey = ResourceKey.from( application.getKey(), path );
            resourceKeyList.add( resourceKey );
        }
        ResourceKeys resourceKeys = ResourceKeys.from( resourceKeyList );

        Mockito.when( this.resourceService.findResourceKeys( application.getKey(), rootPath, filePattern, recurse ) ).thenReturn(
            resourceKeys );
    }
}
