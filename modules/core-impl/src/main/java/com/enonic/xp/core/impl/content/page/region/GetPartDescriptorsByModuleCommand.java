package com.enonic.xp.core.impl.content.page.region;

import com.enonic.xp.core.content.page.region.PartDescriptors;
import com.enonic.xp.core.module.Module;
import com.enonic.xp.core.module.ModuleKey;

final class GetPartDescriptorsByModuleCommand
    extends AbstractGetPartDescriptorCommand<GetPartDescriptorsByModuleCommand>
{
    private ModuleKey moduleKey;

    public PartDescriptors execute()
    {
        final Module module = this.moduleService.getModule( this.moduleKey );
        return getDescriptorsFromModule( module );
    }

    public GetPartDescriptorsByModuleCommand moduleKey( final ModuleKey moduleKey )
    {
        this.moduleKey = moduleKey;
        return this;
    }
}
