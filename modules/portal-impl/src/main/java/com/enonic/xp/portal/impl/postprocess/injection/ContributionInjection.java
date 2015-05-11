package com.enonic.xp.portal.impl.postprocess.injection;

import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.postprocess.PostProcessInjection;

@Component(immediate = true)
public final class ContributionInjection
    implements PostProcessInjection
{
    public ContributionInjection()
    {
    }

    @Override
    public List<String> inject( final PortalContext context, final Tag tag )
    {
        return context.getResponse().getContributions( tag );
    }
}
