package com.enonic.xp.portal.postprocess;

import java.util.List;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;


public interface PostProcessInjection
{
    List<String> inject( PortalRequest portalRequest, PortalResponse portalResponse, HtmlTag htmlTag );
}
