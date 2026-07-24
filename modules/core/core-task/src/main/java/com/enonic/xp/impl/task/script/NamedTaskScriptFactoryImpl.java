package com.enonic.xp.impl.task.script;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
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

        // a missing script fails at submit — existence is checkable without touching any script
        // context. A script without a run export fails when the task runs, ending it FAILED with
        // a clear error; the run itself goes through executeBackground, so the task script's
        // require tree never loads into a request-serving pool context
        if ( !this.scriptService.hasScript( scriptResourceKey ) )
        {
            throw new TaskNotFoundException( descriptor.getKey(), "Missing task script" );
        }

        return new NamedTaskScript( this.scriptService, scriptResourceKey, descriptor, data );
    }
}
