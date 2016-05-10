package com.enonic.xp.portal;

import com.google.common.annotations.Beta;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import com.enonic.xp.portal.postprocess.HtmlTag;
import com.enonic.xp.web.handler.WebResponseImpl;

@Beta
public final class PortalWebResponse
    extends WebResponseImpl
{
    private boolean postProcess = true;

    private ListMultimap<HtmlTag, String> contributions = ArrayListMultimap.create();

    private boolean applyFilters = true;


    public boolean isPostProcess()
    {
        return postProcess;
    }

    public ListMultimap<HtmlTag, String> getContributions()
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

    public void setContributions( final ListMultimap<HtmlTag, String> contributions )
    {
        this.contributions = contributions;
    }

    public void setApplyFilters( final boolean applyFilters )
    {
        this.applyFilters = applyFilters;
    }

    //TODO Temporary fix until renaming of PortalWebResponse to PortalResponse
    @Deprecated
    public static PortalWebResponse convertToPortalWebResponse( final PortalResponse portalResponse )
    {
        final PortalWebResponse portalWebResponse = new PortalWebResponse();
        portalWebResponse.setStatus( portalResponse.getStatus() );
        portalWebResponse.setContentType( portalResponse.getContentType() );
        portalWebResponse.getHeaders().putAll( portalResponse.getHeaders() );
        portalWebResponse.getCookies().addAll( portalResponse.getCookies() );
        portalWebResponse.setWebSocketConfig( portalResponse.getWebSocket() );
        portalWebResponse.setBody( portalResponse.getBody() );
        portalWebResponse.setPostProcess( portalResponse.isPostProcess() );
        portalWebResponse.setContributions( portalResponse.getContributions() );
        portalWebResponse.setApplyFilters( portalResponse.applyFilters() );
        return portalWebResponse;
    }
}
