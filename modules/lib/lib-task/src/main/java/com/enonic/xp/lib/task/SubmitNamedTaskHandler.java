package com.enonic.xp.lib.task;

import java.util.Map;
import java.util.function.Supplier;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.lib.common.FormJsonToPropertyTreeTranslator;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.task.TaskDescriptor;
import com.enonic.xp.task.TaskDescriptorService;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

public final class SubmitNamedTaskHandler
    implements ScriptBean
{
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Supplier<TaskService> taskServiceSupplier;

    private Supplier<MixinService> mixinServiceSupplier;

    private Supplier<TaskDescriptorService> taskDescriptorServiceSupplier;

    private String name;

    private Map<String, Object> config;

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setConfig( final ScriptValue config )
    {
        this.config = config != null ? config.getMap() : null;
    }

    public String submit()
    {
        name = name == null ? "" : name;

        final DescriptorKey taskKey;
        if ( name.contains( ":" ) )
        {
            taskKey = DescriptorKey.from( name );
        }
        else
        {
            final ApplicationKey app = getApplication();
            if ( app == null )
            {
                throw new RuntimeException( "Could not resolve current application for named task: '" + name + "'" );
            }
            taskKey = DescriptorKey.from( app, name );
        }

        final TaskService taskService = taskServiceSupplier.get();
        final TaskDescriptorService taskDescriptorService = taskDescriptorServiceSupplier.get();

        final TaskDescriptor descriptor = taskDescriptorService.getTasks().filter( ( td ) -> td.getKey().equals( taskKey ) ).first();
        final Form taskDescriptorConfig = descriptor == null ? Form.create().build() : descriptor.getConfig();
        final PropertyTree configParams = translateToPropertyTree( config, taskDescriptorConfig );
        final TaskId taskId = taskService.submitTask( taskKey, configParams );

        return taskId.toString();
    }

    private ApplicationKey getApplication()
    {
        PortalRequest portalRequest = PortalRequestAccessor.get();
        if ( portalRequest != null )
        {
            return portalRequest.getApplicationKey();
        }
        else
        {
            return ApplicationKey.from( SubmitNamedTaskHandler.class );
        }
    }

    private JsonNode createJson( final Map<String, Object> value )
    {
        return MAPPER.valueToTree( value );
    }

    private PropertyTree translateToPropertyTree( final Map<String, Object> configValues, final Form form )
    {
        if ( configValues == null )
        {
            return new PropertyTree();
        }
        return new FormJsonToPropertyTreeTranslator( inlineMixins( form ), true ).translate( createJson( configValues ) );
    }

    private Form inlineMixins( final Form form )
    {
        return mixinServiceSupplier.get().inlineFormItems( form );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        taskServiceSupplier = context.getService( TaskService.class );
        mixinServiceSupplier = context.getService( MixinService.class );
        taskDescriptorServiceSupplier = context.getService( TaskDescriptorService.class );
    }
}
