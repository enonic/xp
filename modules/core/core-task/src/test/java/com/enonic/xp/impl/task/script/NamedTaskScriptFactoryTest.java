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
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.impl.script.PortalScriptServiceImpl;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.script.impl.standard.ScriptRuntimeFactoryImpl;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskDescriptor;
import com.enonic.xp.task.TaskId;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class NamedTaskScriptFactoryTest
{
    private NamedTaskScriptFactory namedTaskScriptFactory;

    @BeforeEach
    public void setUp()
    {
        final PortalScriptService portalScriptService = setupPortalScriptService();

        namedTaskScriptFactory = new NamedTaskScriptFactory();
        namedTaskScriptFactory.setScriptService( portalScriptService );
    }

    private PortalScriptService setupPortalScriptService()
    {
        final BundleContext bundleContext = Mockito.mock( BundleContext.class );

        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getBundleContext() ).thenReturn( bundleContext );

        final Application application = Mockito.mock( Application.class );
        Mockito.when( application.getBundle() ).thenReturn( bundle );
        Mockito.when( application.getClassLoader() ).thenReturn( getClass().getClassLoader() );

        final ApplicationService applicationService = Mockito.mock( ApplicationService.class );
        Mockito.when( applicationService.getInstalledApplication( ApplicationKey.from( "myapplication" ) ) ).thenReturn( application );

        ResourceService resourceService = Mockito.mock( ResourceService.class );
        final Answer<Object> getResource = invocation ->
        {
            final ResourceKey resourceKey = (ResourceKey) invocation.getArguments()[0];
            final URL resourceUrl =
                NamedTaskScriptFactoryTest.class.getResource( "/" + resourceKey.getApplicationKey() + resourceKey.getPath() );
            return new UrlResource( resourceKey, resourceUrl );
        };
        Mockito.when( resourceService.getResource( Mockito.any() ) ).thenAnswer( getResource );

        final ScriptRuntimeFactoryImpl runtimeFactory = new ScriptRuntimeFactoryImpl();
        runtimeFactory.setApplicationService( applicationService );
        runtimeFactory.setResourceService( resourceService );

        final PortalScriptServiceImpl scriptService = new PortalScriptServiceImpl();
        scriptService.setScriptRuntimeFactory( runtimeFactory );
        scriptService.initialize();

        return scriptService;
    }

    @Test
    public void createExisting()
        throws Exception
    {
        final TaskDescriptor taskDescriptor = TaskDescriptor.create().key( DescriptorKey.from( "myapplication:mytask" ) ).build();
        final RunnableTask runnableTask = namedTaskScriptFactory.create( taskDescriptor, new PropertyTree() );
        assertNotNull( runnableTask );
        runnableTask.run( TaskId.from( "123" ), null );
    }

    @Test
    public void createMissingRunExport()
        throws Exception
    {
        final TaskDescriptor taskDescriptor = TaskDescriptor.create().key( DescriptorKey.from( "myapplication:mytask2" ) ).build();
        final RunnableTask runnableTask = namedTaskScriptFactory.create( taskDescriptor, new PropertyTree() );
        assertNull( runnableTask );
    }

    @Test
    public void createNotExisting()
        throws Exception
    {
        final TaskDescriptor taskDescriptor = TaskDescriptor.create().key( DescriptorKey.from( "myapplication:mytask3" ) ).build();
        final RunnableTask runnableTask = namedTaskScriptFactory.create( taskDescriptor, new PropertyTree() );
        assertNull( runnableTask );
    }

}
