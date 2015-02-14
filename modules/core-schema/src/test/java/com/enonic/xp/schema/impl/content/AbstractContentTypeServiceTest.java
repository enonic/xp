package com.enonic.xp.schema.impl.content;

import org.junit.Before;
import org.mockito.Mockito;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeProvider;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.xp.schema.impl.content.ContentTypeServiceImpl;

import static org.junit.Assert.*;

public abstract class AbstractContentTypeServiceTest
{
    protected ContentTypeService service;

    protected MixinService mixinService;

    private ContentTypeServiceImpl serviceImpl;

    private ContentTypeProvider provider;

    @Before
    public final void setup()
    {
        this.mixinService = Mockito.mock( MixinService.class );
        this.serviceImpl = new ContentTypeServiceImpl();
        this.serviceImpl.setMixinService( this.mixinService );
        this.service = this.serviceImpl;
    }

    protected final void register( final ContentType... types )
    {
        this.provider = () -> ContentTypes.from( types );
        this.serviceImpl.addProvider( this.provider );
    }

    protected final void unregister()
    {
        this.serviceImpl.removeProvider( this.provider );
    }

    protected final ContentType createContentType( final String name, final String displayName )
    {
        return ContentType.newContentType().superType( ContentTypeName.structured() ).displayName( displayName ).name( name ).build();
    }

    protected final void verifyContentType( final String contentTypeName, final String displayName, final ContentTypes result )
    {
        final ContentType contentType = result.getContentType( ContentTypeName.from( contentTypeName ) );
        assertNotNull( contentType );
        assertEquals( contentTypeName, contentType.getName().toString() );
        assertEquals( displayName, contentType.getDisplayName() );
    }
}
