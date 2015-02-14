package com.enonic.xp.core.impl.content.page.region;

import com.enonic.xp.content.page.region.LayoutDescriptors;
import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleKey;

final class GetLayoutDescriptorsByModuleCommand
    extends AbstractGetLayoutDescriptorCommand<GetLayoutDescriptorsByModuleCommand>
{
    private ModuleKey moduleKey;

    public LayoutDescriptors execute()
    {
        final Module module = this.moduleService.getModule( this.moduleKey );
        return getDescriptorsFromModule( module );
    }

    public GetLayoutDescriptorsByModuleCommand moduleKey( final ModuleKey moduleKey )
    {
        this.moduleKey = moduleKey;
        return this;
    }
}
