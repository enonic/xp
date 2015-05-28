package com.enonic.xp.portal.postprocess;

import java.util.List;

import com.google.common.annotations.Beta;

import com.enonic.xp.portal.PortalContext;

@Beta
public interface PostProcessInjection
{
    List<String> inject( PortalContext context, HtmlTag htmlTag );
}
