package com.enonic.xp.lib.content;

import org.mockito.Mockito;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.core.impl.PropertyTreeMarshallerServiceFactory;
import com.enonic.xp.form.PropertyTreeMarshallerService;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.CmsFormFragmentService;
import com.enonic.xp.schema.xdata.MixinService;
import com.enonic.xp.site.CmsService;
import com.enonic.xp.testing.ScriptTestSupport;

public abstract class BaseContentHandlerTest
    extends ScriptTestSupport
{
    protected ContentService contentService;

    protected ContentTypeService contentTypeService;

    protected CmsFormFragmentService formFragmentService;

    protected MixinService xDataService;

    protected CmsService cmsService;

    protected PropertyTreeMarshallerService propertyTreeMarshallerService;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();

        this.contentService = Mockito.mock( ContentService.class );
        this.contentTypeService = Mockito.mock( ContentTypeService.class );
        this.formFragmentService = Mockito.mock( CmsFormFragmentService.class );
        this.xDataService = Mockito.mock( MixinService.class );
        this.cmsService = Mockito.mock( CmsService.class );
        this.propertyTreeMarshallerService = PropertyTreeMarshallerServiceFactory.newInstance();
        addService( ContentService.class, this.contentService );
        addService( CmsFormFragmentService.class, this.formFragmentService );
        addService( ContentTypeService.class, this.contentTypeService );
        addService( MixinService.class, this.xDataService );
        addService( CmsService.class, this.cmsService );
        addService( PropertyTreeMarshallerService.class, this.propertyTreeMarshallerService );
    }
}
