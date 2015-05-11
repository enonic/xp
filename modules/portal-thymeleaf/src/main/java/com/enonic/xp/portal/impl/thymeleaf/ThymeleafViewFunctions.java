package com.enonic.xp.portal.impl.thymeleaf;

import java.util.List;

import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.view.ViewFunctionParams;
import com.enonic.xp.portal.view.ViewFunctionService;

final class ThymeleafViewFunctions
{
    protected ViewFunctionService viewFunctionService;

    protected PortalContext context;

    public String assetUrl( final List<String> params )
    {
        return execute( "assetUrl", params ).toString();
    }

    public String pageUrl( final List<String> params )
    {
        return execute( "pageUrl", params ).toString();
    }

    public String attachmentUrl( final List<String> params )
    {
        return execute( "attachmentUrl", params ).toString();
    }

    public String componentUrl( final List<String> params )
    {
        return execute( "componentUrl", params ).toString();
    }

    public String imageUrl( final List<String> params )
    {
        return execute( "imageUrl", params ).toString();
    }

    public String serviceUrl( final List<String> params )
    {
        return execute( "serviceUrl", params ).toString();
    }

    public String imagePlaceholder( final List<String> params )
    {
        return execute( "imagePlaceholder", params ).toString();
    }

    public String processHtml( final List<String> params )
    {
        return execute( "processHtml", params ).toString();
    }
    
    public String localize( final List<String> params )
    {
        return execute( "i18n.localize", params ).toString();
    }
    
    private Object execute( final String name, final List<String> args )
    {
        final ViewFunctionParams params = new ViewFunctionParams().name( name ).args( args ).context( this.context );
        return this.viewFunctionService.execute( params );
    }
}
