package com.enonic.xp.portal.impl.postprocess.injection;

import java.util.List;
import java.util.stream.Collectors;

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
    public String inject( final PortalContext context, final Tag tag )
    {
        final List<String> contributions = context.getResponse().getContributions( tag );
        return contributions.isEmpty() ? null : contributions.stream().distinct().collect( Collectors.joining() );
    }
}
