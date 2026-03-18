package com.enonic.xp.core.impl.idprovider;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.idprovider.IdProviderDescriptor;
import com.enonic.xp.idprovider.IdProviderDescriptorService;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;

@Component(immediate = true)
public final class IdProviderDescriptorServiceImpl
    implements IdProviderDescriptorService
{
    private ResourceService resourceService;

    @Override
    public IdProviderDescriptor getDescriptor( final ApplicationKey key )
    {
        final ResourceProcessor<ApplicationKey, IdProviderDescriptor> processor = newProcessor( key );
        return this.resourceService.processResource( processor );
    }

    private ResourceProcessor<ApplicationKey, IdProviderDescriptor> newProcessor( final ApplicationKey key )
    {
        return new ResourceProcessor.Builder<ApplicationKey, IdProviderDescriptor>().key( key )
            .segment( "authDescriptor" )
            .keyTranslator( this::toResourceKey )
            .processor( resource -> loadDescriptor( key, resource ) )
            .build();
    }

    private IdProviderDescriptor loadDescriptor( final ApplicationKey key, final Resource resource )
    {
        final String yaml = resource.readString();
        final IdProviderDescriptor descriptor = YmlIdProviderDescriptorParser.parse( yaml, key ).build();
        validateNoFormFragments( descriptor.getConfig() );
        return descriptor;
    }

    private static void validateNoFormFragments( final Iterable<FormItem> formItems )
    {
        for ( FormItem item : formItems )
        {
            switch ( item.getType() )
            {
                case FORM_FRAGMENT:
                    throw new IllegalArgumentException( "IdProviderDescriptor form cannot contain FormFragment: " + item.getName() );
                case FORM_ITEM_SET:
                    validateNoFormFragments( (FormItemSet) item );
                    break;
                case FIELD_SET:
                    validateNoFormFragments( (FieldSet) item );
                    break;
                case FORM_OPTION_SET:
                    for ( FormOptionSetOption option : (FormOptionSet) item )
                    {
                        validateNoFormFragments( option );
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private ResourceKey toResourceKey( final ApplicationKey key )
    {
        return ResourceKey.from( key, "idprovider/idprovider.yml" );
    }

    @Reference
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }
}
