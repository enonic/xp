package com.enonic.wem.core.content.page;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptors;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.module.Modules;

final class GetPageDescriptorsByModulesCommand
    extends AbstractGetPageDescriptorCommand
{
    private final static Pattern PATTERN = Pattern.compile( "cms/pages/([^/]+)/page.xml" );

    private ModuleKeys moduleKeys;

    private ModuleService moduleService;

    public PageDescriptors execute()
    {
        final Modules modules = this.moduleService.getModules( this.moduleKeys );
        return getDescriptorsFromModules( modules );
    }

    public GetPageDescriptorsByModulesCommand moduleKeys( final ModuleKeys moduleKeys )
    {
        this.moduleKeys = moduleKeys;
        return this;
    }

    public final GetPageDescriptorsByModulesCommand moduleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
        return this;
    }

    private PageDescriptors getDescriptorsFromModules( final Modules modules )
    {
        final List<PageDescriptor> pageDescriptors = new ArrayList<>();
        for ( final Module module : modules )
        {
            final List<String> componentNames = module.getResourcePaths().stream().
                map( PATTERN::matcher ).
                filter( Matcher::matches ).
                map( ( matcher ) -> matcher.group( 1 ) ).
                collect( Collectors.toList() );

            for ( final String componentName : componentNames )
            {
                final DescriptorKey key = DescriptorKey.from( module.getKey(), componentName );
                final PageDescriptor pageDescriptor = getDescriptor( key );
                if ( pageDescriptor != null )
                {
                    pageDescriptors.add( pageDescriptor );
                }
            }
        }

        return PageDescriptors.from( pageDescriptors );
    }

}