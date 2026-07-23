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

        // everything goes through the background view, so the task script's require tree never
        // loads into a request-serving pool context. Validation stays eager — hasMethod runs the
        // script's top level (in a throwaway private context on pooled engines), so a missing
        // script or missing run function still fails at submit, and each run gets a fresh context
        final ScriptExports exports;
        final boolean exists;
        try
        {
            exports = this.scriptService.executeBackground( scriptResourceKey );
            // a missing script surfaces at executeBackground on engines that resolve eagerly, and
            // at the first invocation (hasMethod) on pooled engines, where the view is lazy
            exists = exports.hasMethod( NamedTaskScript.SCRIPT_METHOD_NAME );
        }
        catch ( ResourceNotFoundException e )
        {
            throw new TaskNotFoundException( descriptor.getKey(), "Missing task script" );
        }
        if ( !exists )
        {
            throw new TaskNotFoundException( descriptor.getKey(),
                                             "Missing exported function '" + NamedTaskScript.SCRIPT_METHOD_NAME + "' in task script" );
        }

        return new NamedTaskScript( exports, descriptor, data );
    }
}
