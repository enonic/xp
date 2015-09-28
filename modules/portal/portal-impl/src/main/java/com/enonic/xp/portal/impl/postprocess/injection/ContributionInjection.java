package com.enonic.xp.portal.impl.postprocess.injection;

import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.postprocess.HtmlTag;
import com.enonic.xp.portal.postprocess.PostProcessInjection;

@Component(immediate = true)
public final class ContributionInjection
    implements PostProcessInjection
{
    public ContributionInjection()
    {
    }

    @Override
    public List<String> inject( final PortalRequest portalRequest, final PortalResponse portalResponse, final HtmlTag htmlTag )
    {
        return portalResponse.getContributions( htmlTag );
    }
}
