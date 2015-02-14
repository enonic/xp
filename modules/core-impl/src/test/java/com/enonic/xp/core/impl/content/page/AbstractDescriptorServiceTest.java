package com.enonic.xp.core.impl.content.page;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

import com.enonic.xp.core.content.page.DescriptorKey;
import com.enonic.xp.core.module.Module;
import com.enonic.xp.core.module.ModuleKey;
import com.enonic.xp.core.module.ModuleService;
import com.enonic.xp.core.module.Modules;
import com.enonic.xp.core.resource.ResourceKey;
import com.enonic.xp.core.resource.ResourceUrlTestHelper;

public abstract class AbstractDescriptorServiceTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File modulesDir;

    protected ModuleService moduleService;

    @Before
    public final void setup()
        throws Exception
    {
        this.modulesDir = this.temporaryFolder.newFolder( "modules" );
        ResourceUrlTestHelper.mockModuleScheme().modulesDir( this.modulesDir );
        this.moduleService = Mockito.mock( ModuleService.class );
    }

    protected final void createFile( final ResourceKey key, final String content )
        throws Exception
    {
        final String path = key.getModule().toString() + key.getPath();
        final File file = new File( this.modulesDir, path );
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

    protected final Module createModule( final String key )
    {
        final ModuleKey moduleKey = ModuleKey.from( key );

        final Module module = Mockito.mock( Module.class );
        Mockito.when( module.getKey() ).thenReturn( moduleKey );

        Mockito.when( this.moduleService.getModule( moduleKey ) ).thenReturn( module );
        return module;
    }

    protected final Modules createModules( final String... keys )
    {
        final List<Module> list = Lists.newArrayList();
        for ( final String key : keys )
        {
            list.add( createModule( key ) );
        }

        final Modules modules = Modules.from( list );
        Mockito.when( this.moduleService.getAllModules() ).thenReturn( modules );
        Mockito.when( this.moduleService.getModules( modules.getModuleKeys() ) ).thenReturn( modules );
        return modules;
    }

    protected final void mockResourcePaths( final Module module, final String... paths )
    {
        Mockito.when( module.getResourcePaths() ).thenReturn( Sets.newHashSet( paths ) );
    }
}
