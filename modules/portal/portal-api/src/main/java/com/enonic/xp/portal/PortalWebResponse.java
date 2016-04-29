package com.enonic.xp.portal;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableListMultimap;

import com.enonic.xp.portal.postprocess.HtmlTag;
import com.enonic.xp.web.handler.WebResponseImpl;

@Beta
public final class PortalWebResponse
    extends WebResponseImpl
{
    private boolean postProcess;

    private ImmutableListMultimap<HtmlTag, String> contributions;

    private boolean applyFilters;


    public boolean isPostProcess()
    {
        return postProcess;
    }

    public ImmutableListMultimap<HtmlTag, String> getContributions()
    {
        return contributions;
    }

    public boolean isApplyFilters()
    {
        return applyFilters;
    }

    public void setPostProcess( final boolean postProcess )
    {
        this.postProcess = postProcess;
    }

    public void setContributions( final ImmutableListMultimap<HtmlTag, String> contributions )
    {
        this.contributions = contributions;
    }

    public void setApplyFilters( final boolean applyFilters )
    {
        this.applyFilters = applyFilters;
    }
}
