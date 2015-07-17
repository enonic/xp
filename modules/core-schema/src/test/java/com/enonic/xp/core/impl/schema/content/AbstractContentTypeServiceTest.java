package com.enonic.xp.core.impl.schema.content;

import java.util.stream.Collectors;

import org.junit.Before;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeRegistry;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.mixin.MixinService;

import static org.junit.Assert.*;

public abstract class AbstractContentTypeServiceTest
{
    protected ContentTypeService service;

    protected MixinService mixinService;

    private ContentTypeServiceImpl serviceImpl;

    private ContentTypeRegistry registryImpl;

    private ApplicationService applicationService;

    @Before
    public final void setup()
    {
        this.mixinService = Mockito.mock( MixinService.class );
        this.serviceImpl = new ContentTypeServiceImpl();
        this.applicationService = Mockito.mock( ApplicationService.class );
        this.registryImpl = Mockito.mock( ContentTypeRegistry.class );
        this.serviceImpl.setMixinService( this.mixinService );
        this.serviceImpl.setContentTypeRegistry( this.registryImpl );
        this.service = this.serviceImpl;
    }

    protected final void register( final ContentType... types )
    {
        final ContentTypes contentTypes = ContentTypes.from( types );
        contentTypes.stream().forEach( elem -> {
            Mockito.when( registryImpl.get( elem.getName() ) ).thenReturn( elem );
            Mockito.when( registryImpl.getByModule( elem.getName().getApplicationKey() ) ).
                thenReturn( ContentTypes.from( contentTypes.stream().
                    filter( e -> e.getName().getApplicationKey().equals( elem.getName().getApplicationKey() ) ).
                    collect( Collectors.toList() ) ) );
        } );
        Mockito.when( registryImpl.getAll() ).thenReturn( contentTypes );

    }

    protected final ContentType createContentType( final String name, final String displayName )
    {
        return ContentType.create().superType( ContentTypeName.structured() ).displayName( displayName ).name( name ).build();
    }

    protected final void verifyContentType( final String contentTypeName, final String displayName, final ContentTypes result )
    {
        final ContentType contentType = result.getContentType( ContentTypeName.from( contentTypeName ) );
        assertNotNull( contentType );
        assertEquals( contentTypeName, contentType.getName().toString() );
        assertEquals( displayName, contentType.getDisplayName() );
    }
}
