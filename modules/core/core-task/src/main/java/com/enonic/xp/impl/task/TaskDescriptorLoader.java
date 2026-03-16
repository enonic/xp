package com.enonic.xp.impl.task;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.DescriptorKeyLocator;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.descriptor.DescriptorLoader;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.task.TaskDescriptor;

@Component(immediate = true)
public final class TaskDescriptorLoader
    implements DescriptorLoader<TaskDescriptor>
{
    private static final String PATH = "/tasks";

    private final DescriptorKeyLocator descriptorKeyLocator;

    @Activate
    public TaskDescriptorLoader( @Reference final ResourceService resourceService )
    {
        this.descriptorKeyLocator = new DescriptorKeyLocator( resourceService, PATH, false );
    }

    @Override
    public Class<TaskDescriptor> getType()
    {
        return TaskDescriptor.class;
    }

    @Override
    public DescriptorKeys find( final ApplicationKey key )
    {
        return descriptorKeyLocator.findKeys( key );
    }

    @Override
    public ResourceKey toResource( final DescriptorKey key )
    {
        return ResourceKey.from( key.getApplicationKey(), PATH + "/" + key.getName() + "/" + key.getName() + ".yml" );
    }

    @Override
    public TaskDescriptor load( final DescriptorKey key, final Resource resource )
    {
        final TaskDescriptor taskDescriptor =
            YmlTaskDescriptorParser.parse( resource.readString(), key.getApplicationKey() ).key( key ).build();
        validateFormItems( taskDescriptor.getConfig() );
        return taskDescriptor;
    }

    private static void validateFormItems( final Iterable<FormItem> formItems )
    {
        for ( FormItem item : formItems )
        {
            switch ( item.getType() )
            {
                case FORM_FRAGMENT:
                    throw new IllegalArgumentException( "TaskDescriptor form cannot contain FormFragment: " + item.getName() );
                case FORM_ITEM_SET:
                    validateFormItems( (FormItemSet) item );
                    break;
                case LAYOUT:
                    validateFormItems( (FieldSet) item );
                    break;
                case FORM_OPTION_SET:
                    for ( FormOptionSetOption option : (FormOptionSet) item )
                    {
                        validateFormItems( option );
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
