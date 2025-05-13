package com.enonic.xp.impl.task.script;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceNotFoundException;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.task.TaskDescriptor;
import com.enonic.xp.task.TaskNotFoundException;

@Component
public class NamedTaskScriptFactoryImpl
    implements NamedTaskFactory
{
    private static final String TASKS_PATH_PREFIX = "tasks/";

    private final PortalScriptService scriptService;

    @Activate
    public NamedTaskScriptFactoryImpl( @Reference final PortalScriptService scriptService )
    {
        this.scriptService = scriptService;
    }

    @Override
    public NamedTaskScript create( final TaskDescriptor descriptor, final PropertyTree data )
    {
        return doCreate( descriptor, data );
    }

    private NamedTaskScript doCreate( final TaskDescriptor descriptor, final PropertyTree data )
    {
        final ResourceKey scriptResourceKey = ResourceKey.from( descriptor.getApplicationKey(),
                                                                TASKS_PATH_PREFIX + descriptor.getName() + "/" + descriptor.getName() +
                                                                    ".js" );

        final ScriptExports exports;
        try
        {
            exports = this.scriptService.execute( scriptResourceKey );
        }
        catch ( ResourceNotFoundException e )
        {
            throw new TaskNotFoundException( descriptor.getKey(), "Missing task script" );
        }

        final boolean exists = exports.hasMethod( NamedTaskScript.SCRIPT_METHOD_NAME );
        if ( !exists )
        {
            throw new TaskNotFoundException( descriptor.getKey(),
                                             "Missing exported function '" + NamedTaskScript.SCRIPT_METHOD_NAME + "' in task script" );
        }

        return new NamedTaskScript( exports, descriptor, data );
    }
}
