package com.enonic.xp.lib.portal.url;

import java.util.List;
import java.util.function.Supplier;

import com.enonic.xp.portal.url.BaseUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ProcessHtmlPartsParams;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.script.serializer.MapSerializable;

/**
 * Backs {@code processHtmlParts} of {@code /lib/xp/portal}.
 */
public final class ProcessHtmlPartsHandler
    implements ScriptBean
{
    private Supplier<PortalUrlService> urlServiceSupplier;

    private String value;

    private String baseId;

    private String basePath;

    private String baseProjectName;

    private String baseBranch;

    private List<Integer> imageWidths;

    private String imageSizes;

    @Override
    public void initialize( final BeanContext context )
    {
        this.urlServiceSupplier = context.getService( PortalUrlService.class );
    }

    public void setValue( final String value )
    {
        this.value = value;
    }

    public void setBaseId( final String baseId )
    {
        this.baseId = baseId;
    }

    public void setBasePath( final String basePath )
    {
        this.basePath = basePath;
    }

    public void setBaseProjectName( final String baseProjectName )
    {
        this.baseProjectName = baseProjectName;
    }

    public void setBaseBranch( final String baseBranch )
    {
        this.baseBranch = baseBranch;
    }

    public void setImageWidths( final List<Integer> imageWidths )
    {
        this.imageWidths = imageWidths;
    }

    public void setImageSizes( final String imageSizes )
    {
        this.imageSizes = imageSizes;
    }

    public MapSerializable process()
    {
        final BaseUrlParams base = BaseUrlParams.create()
            .setId( this.baseId )
            .setPath( this.basePath )
            .setProjectName( this.baseProjectName )
            .setBranch( this.baseBranch )
            .build();

        return new ProcessedHtmlMapper( urlServiceSupplier.get()
                                            .processHtmlParts( ProcessHtmlPartsParams.create()
                                                                   .value( this.value )
                                                                   .base( base )
                                                                   .imageWidths( this.imageWidths )
                                                                   .imageSizes( this.imageSizes )
                                                                   .build() ) );
    }
}
