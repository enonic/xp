package com.enonic.wem.core.content.page.part;

import com.enonic.wem.api.content.page.part.PartDescriptors;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;

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
