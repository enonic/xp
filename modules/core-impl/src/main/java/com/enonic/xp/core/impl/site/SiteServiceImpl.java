package com.enonic.xp.core.impl.site;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleService;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;

@Component(immediate = true)
public class SiteServiceImpl
    implements SiteService
{
    private ModuleService moduleService;

    @Override
    public SiteDescriptor getDescriptor( final ModuleKey moduleKey )
    {
        final Module module = this.moduleService.getModule( moduleKey );
        if ( module == null )
        {
            return null;
        }

        return SiteDescriptor.create().
            form( module.getConfig() ).
            metaSteps( module.getMetaSteps() ).
            build();
    }

    @Reference
    public void setModuleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
    }
}
