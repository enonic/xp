package com.enonic.xp.impl.task.script;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceNotFoundException;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskDescriptor;

@Component(immediate = true, service = NamedTaskScriptFactory.class)
public class NamedTaskScriptFactory
{
    private static final String TASKS_PATH_PREFIX = "tasks/";

    private PortalScriptService scriptService;

    public NamedTaskScriptFactory()
    {
    }

    public RunnableTask create( final TaskDescriptor task, final PropertyTree config )
    {
        final ResourceKey scriptResourceKey = toControllerResourceKey( task );

        final ScriptExports exports;
        try
        {
            exports = this.scriptService.execute( scriptResourceKey );
        }
        catch ( ResourceNotFoundException e )
        {
            return null;
        }

        final boolean exists = exports.hasMethod( NamedTaskScript.SCRIPT_METHOD_NAME );
        if ( !exists )
        {
            return null;
        }

        return new NamedTaskScript( exports, task, config );
    }

    private ResourceKey toControllerResourceKey( final TaskDescriptor task )
    {
        final DescriptorKey key = task.getKey();
        return ResourceKey.from( key.getApplicationKey(), TASKS_PATH_PREFIX + key.getName() + "/" + key.getName() + ".js" );
    }

    @Reference
    public void setScriptService( final PortalScriptService scriptService )
    {
        this.scriptService = scriptService;
    }
}
