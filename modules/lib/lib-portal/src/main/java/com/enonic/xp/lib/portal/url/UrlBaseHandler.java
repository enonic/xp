package com.enonic.xp.lib.portal.url;

import java.util.function.Supplier;

import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.UrlBase;
import com.enonic.xp.portal.url.UrlBaseParams;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

/**
 * Backs {@code urlBase} of {@code /lib/xp/portal}. The resolved base goes back to the script as is, to be passed to
 * {@code pageUrlParts} and {@code processHtmlParts}.
 */
public final class UrlBaseHandler
    implements ScriptBean
{
    private Supplier<PortalUrlService> urlServiceSupplier;

    private String key;

    private String projectName;

    private String branch;

    @Override
    public void initialize( final BeanContext context )
    {
        this.urlServiceSupplier = context.getService( PortalUrlService.class );
    }

    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setProjectName( final String projectName )
    {
        this.projectName = projectName;
    }

    public void setBranch( final String branch )
    {
        this.branch = branch;
    }

    public UrlBase resolve()
    {
        return urlServiceSupplier.get()
            .urlBase( UrlBaseParams.create().setKey( this.key ).setProjectName( this.projectName ).setBranch( this.branch ).build() );
    }
}
