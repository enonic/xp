package com.enonic.xp.lib.auth;

import com.enonic.xp.lib.common.PrincipalMapper;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.security.Principal;
import com.enonic.xp.security.Principals;

public final class PrincipalsResultMapper
    implements MapSerializable
{
    private final Principals principals;

    private final long total;

    private final boolean detailed;

    public PrincipalsResultMapper( final Principals principals, final long total )
    {
        this( principals, total, false );
    }

    public PrincipalsResultMapper( final Principals principals, final long total, final boolean detailed )
    {
        this.principals = principals;
        this.total = total;
        this.detailed = detailed;
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
            new PrincipalMapper( principal, detailed ).serialize( gen );
            gen.end();
        }
        gen.end();
    }
}
