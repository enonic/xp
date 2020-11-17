package com.enonic.xp.impl.task.script;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.PropertyTreeMarshallerService;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceNotFoundException;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.task.TaskDescriptor;
import com.enonic.xp.task.TaskDescriptorService;
import com.enonic.xp.task.TaskNotFoundException;

@Component
public class NamedTaskScriptFactoryImpl
    implements NamedTaskFactory
{
    private static final String TASKS_PATH_PREFIX = "tasks/";

    private final PortalScriptService scriptService;

    private final TaskDescriptorService taskDescriptorService;

    private final PropertyTreeMarshallerService propertyTreeMarshallerService;

    @Activate
    public NamedTaskScriptFactoryImpl( @Reference final PortalScriptService scriptService,
                                       @Reference final TaskDescriptorService taskDescriptorService,
                                       @Reference final PropertyTreeMarshallerService propertyTreeMarshallerService )
    {
        this.scriptService = scriptService;
        this.taskDescriptorService = taskDescriptorService;
        this.propertyTreeMarshallerService = propertyTreeMarshallerService;
    }

    public NamedTaskScript create( final DescriptorKey key, final PropertyTree data )
    {
        return doCreate( key, data, true );
    }

    public NamedTaskScript createLegacy( final DescriptorKey key, final PropertyTree data )
    {
        return doCreate( key, data, false );
    }

    private NamedTaskScript doCreate( final DescriptorKey key, final PropertyTree data, final boolean marshal )
    {
        final TaskDescriptor taskDescriptor = taskDescriptorService.getTask( key );

        final ResourceKey scriptResourceKey =
            ResourceKey.from( key.getApplicationKey(), TASKS_PATH_PREFIX + key.getName() + "/" + key.getName() + ".js" );

        final ScriptExports exports;
        try
        {
            exports = this.scriptService.execute( scriptResourceKey );
        }
        catch ( ResourceNotFoundException e )
        {
            throw new TaskNotFoundException( key, "Missing task script" );
        }

        final boolean exists = exports.hasMethod( NamedTaskScript.SCRIPT_METHOD_NAME );
        if ( !exists )
        {
            throw new TaskNotFoundException( key, "Missing exported function '" + NamedTaskScript.SCRIPT_METHOD_NAME + "' in task script" );
        }

        final PropertyTree namedTaskConfig =
            marshal ? propertyTreeMarshallerService.marshal( data.toMap(), taskDescriptor.getConfig(), true ) : data;

        return new NamedTaskScript( exports, taskDescriptor, namedTaskConfig );
    }
}
