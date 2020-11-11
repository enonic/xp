package com.enonic.xp.impl.task.script;

import java.net.URL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.impl.script.PortalScriptServiceImpl;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.script.impl.async.ScriptAsyncService;
import com.enonic.xp.script.impl.standard.ScriptRuntimeFactoryImpl;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskDescriptorService;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskNotFoundException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class NamedTaskFactoryImplTest
{
    private NamedTaskScriptFactoryImpl namedTaskScriptFactory;

    @BeforeEach
    void setUp()
    {
        final PortalScriptService portalScriptService = setupPortalScriptService();

        TaskDescriptorService taskDescriptorService = mock( TaskDescriptorService.class );
        namedTaskScriptFactory = new NamedTaskScriptFactoryImpl( portalScriptService, taskDescriptorService );
    }

    private PortalScriptService setupPortalScriptService()
    {
        final BundleContext bundleContext = mock( BundleContext.class );

        final Bundle bundle = mock( Bundle.class );
        Mockito.when( bundle.getBundleContext() ).thenReturn( bundleContext );

        final Application application = mock( Application.class );
        Mockito.when( application.getBundle() ).thenReturn( bundle );
        Mockito.when( application.getClassLoader() ).thenReturn( getClass().getClassLoader() );
        Mockito.when( application.isStarted() ).thenReturn( true );
        Mockito.when( application.getConfig() ).thenReturn( ConfigBuilder.create().build() );

        final ApplicationService applicationService = mock( ApplicationService.class );
        Mockito.when( applicationService.getInstalledApplication( ApplicationKey.from( "myapplication" ) ) ).thenReturn( application );

        ResourceService resourceService = mock( ResourceService.class );
        final Answer<Object> getResource = invocation -> {
            final ResourceKey resourceKey = (ResourceKey) invocation.getArguments()[0];
            final URL resourceUrl =
                NamedTaskFactoryImplTest.class.getResource( "/" + resourceKey.getApplicationKey() + resourceKey.getPath() );
            return new UrlResource( resourceKey, resourceUrl );
        };
        Mockito.when( resourceService.getResource( Mockito.any() ) ).thenAnswer( getResource );

        final ScriptAsyncService scriptAsyncService = mock( ScriptAsyncService.class );

        final ScriptRuntimeFactoryImpl runtimeFactory =
            new ScriptRuntimeFactoryImpl( applicationService, resourceService, scriptAsyncService );

        final PortalScriptServiceImpl scriptService = new PortalScriptServiceImpl( runtimeFactory );
        scriptService.initialize();

        return scriptService;
    }

    @Test
    void createExisting()
    {
        final RunnableTask runnableTask = namedTaskScriptFactory.create( DescriptorKey.from( "myapplication:mytask" ), new PropertyTree() );
        assertNotNull( runnableTask );
        runnableTask.run( TaskId.from( "123" ), null );
    }

    @Test
    void createMissingRunExport()
    {
        assertThrows( TaskNotFoundException.class,
                      () -> namedTaskScriptFactory.create( DescriptorKey.from( "myapplication:mytask2" ), new PropertyTree() ) );
    }

    @Test
    void createNotExisting()
    {
        assertThrows( TaskNotFoundException.class,
                      () -> namedTaskScriptFactory.create( DescriptorKey.from( "myapplication:mytask3" ), new PropertyTree() ) );
    }
}
