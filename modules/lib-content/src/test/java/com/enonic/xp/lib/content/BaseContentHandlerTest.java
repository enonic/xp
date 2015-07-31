package com.enonic.xp.lib.content;

import org.junit.Before;
import org.mockito.Mockito;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.testing.script.ScriptTestSupport;

abstract class BaseContentHandlerTest
    extends ScriptTestSupport
{
    protected ContentService contentService;

    protected ContentTypeService contentTypeService;

    protected MixinService mixinService;

    @Before
    public void setup()
    {
        this.contentService = Mockito.mock( ContentService.class );
        this.contentTypeService = Mockito.mock( ContentTypeService.class );
        this.mixinService = Mockito.mock( MixinService.class );
        addService( ContentService.class, this.contentService );
        addService( MixinService.class, this.mixinService );
        addService( ContentTypeService.class, this.contentTypeService );
    }
}
