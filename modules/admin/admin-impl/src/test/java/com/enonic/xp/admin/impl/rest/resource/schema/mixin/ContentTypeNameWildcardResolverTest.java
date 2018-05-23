package com.enonic.xp.admin.impl.rest.resource.schema.mixin;

import java.time.Instant;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.content.GetAllContentTypesParams;

import static org.junit.Assert.*;

public class ContentTypeNameWildcardResolverTest
{
    private ContentTypeNameWildcardResolver resolver;

    @Before
    public void setUp()
    {
        ContentTypeService service = Mockito.mock( ContentTypeService.class );
        Mockito.when( service.getAll( Mockito.isA( GetAllContentTypesParams.class ) ) ).thenReturn( getAllContentTypes() );
        this.resolver = new ContentTypeNameWildcardResolver( service );
    }

    private ContentTypes getAllContentTypes()
    {
        return ContentTypes.from( this.createContentType( "myapp:test1" ), this.createContentType( "myapp:test2" ),
                                  this.createContentType( "otherapp:test1" ) );
    }

    private ContentType createContentType( final String name )
    {
        return ContentType.create().
            name( name ).
            icon( Icon.from( new byte[]{1}, "mime", Instant.now() ) ).
            setBuiltIn( true ).
            build();
    }

    @Test
    public void no_wildcards()
    {
        List<String> toResolve = Lists.newArrayList( "myapp:foo", "myapp:bar" );
        List<String> resolved = resolver.resolveWildcards( toResolve, ( ApplicationKey.from( "myapp" ) ) );

        assertEquals( toResolve, resolved );
    }

    @Test
    public void appWildcards()
    {
        List<String> toResolve = Lists.newArrayList( "${app}:test1", "myapp:bar" );
        List<String> resolved = resolver.resolveWildcards( toResolve, ApplicationKey.from( "myapp" ) );

        assertEquals( 2, resolved.size() );
        assertEquals( "myapp:test1", resolved.get( 0 ) );
        assertEquals( "myapp:bar", resolved.get( 1 ) );
    }

    @Test
    public void anyWildcards()
    {
        List<String> toResolve = Lists.newArrayList( "*:test1", "myapp:bar" );
        List<String> resolved = resolver.resolveWildcards( toResolve, ApplicationKey.from( "myapp" ) );

        assertEquals( 3, resolved.size() );
        assertEquals( "myapp:test1", resolved.get( 0 ) );
        assertEquals( "otherapp:test1", resolved.get( 1 ) );
        assertEquals( "myapp:bar", resolved.get( 2 ) );
    }

    @Test
    public void anyAndAppWildcards()
    {
        List<String> toResolve = Lists.newArrayList( "${app}:test*", "myapp:bar" );
        List<String> resolved = resolver.resolveWildcards( toResolve, ApplicationKey.from( "myapp" ) );

        assertEquals( 3, resolved.size() );
        assertEquals( "myapp:test1", resolved.get( 0 ) );
        assertEquals( "myapp:test2", resolved.get( 1 ) );
        assertEquals( "myapp:bar", resolved.get( 2 ) );
    }
}
