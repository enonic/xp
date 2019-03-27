package com.enonic.xp.lib.content;

import org.mockito.Mockito;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.testing.ScriptTestSupport;

public abstract class BaseContentHandlerTest
    extends ScriptTestSupport
{
    protected ContentService contentService;

    protected ContentTypeService contentTypeService;

    protected MixinService mixinService;

    protected XDataService xDataService;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();

        this.contentService = Mockito.mock( ContentService.class );
        this.contentTypeService = Mockito.mock( ContentTypeService.class );
        this.mixinService = Mockito.mock( MixinService.class );
        this.xDataService = Mockito.mock( XDataService.class );
        addService( ContentService.class, this.contentService );
        addService( MixinService.class, this.mixinService );
        addService( ContentTypeService.class, this.contentTypeService );
        addService( XDataService.class, this.xDataService );
    }
}
