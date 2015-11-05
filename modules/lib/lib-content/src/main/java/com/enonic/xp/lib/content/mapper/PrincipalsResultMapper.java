package com.enonic.xp.lib.content.mapper;

import com.enonic.xp.lib.auth.PrincipalMapper;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.security.Principal;
import com.enonic.xp.security.Principals;

public final class PrincipalsResultMapper
    implements MapSerializable
{
    private final Principals principals;

    private final long total;

    public PrincipalsResultMapper( final Principals principals, final long total )
    {
        this.principals = principals;
        this.total = total;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "total", this.total );
        gen.value( "count", this.principals.getSize() );
        serialize( gen, this.principals );
    }

    private void serialize( final MapGenerator gen, final Principals principals )
    {
        gen.array( "hits" );
        for ( final Principal principal : principals )
        {
            gen.map();
            new PrincipalMapper( principal ).serialize( gen );
            gen.end();
        }
        gen.end();
    }
}
