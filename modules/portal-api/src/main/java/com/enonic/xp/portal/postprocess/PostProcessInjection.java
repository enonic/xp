package com.enonic.xp.portal.postprocess;

import java.util.List;

import com.google.common.annotations.Beta;

import com.enonic.xp.portal.PortalRequest;


@Beta
public interface PostProcessInjection
{
    List<String> inject( PortalRequest portalRequest, HtmlTag htmlTag );
}
