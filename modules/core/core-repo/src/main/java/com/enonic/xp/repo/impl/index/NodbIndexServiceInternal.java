package com.enonic.xp.repo.impl.index;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

@Component(configurationPid = "com.enonic.xp.storage.nodb", configurationPolicy = ConfigurationPolicy.REQUIRE,
    service = IndexServiceInternal.class, property = {"storage.backend=nodb", "service.ranking:Integer=100"})
public class NodbIndexServiceInternal
    implements IndexServiceInternal
{
    private static final String BACKEND_NODB = "nodb";

    @Activate
    public void activate( final Map<String, String> properties )
    {
        final String backend = properties.get( "backend" );
        if ( !BACKEND_NODB.equals( backend ) )
        {
            throw new IllegalStateException(
                "com.enonic.xp.storage.nodb is configured but backend=[" + backend + "] (expected [" + BACKEND_NODB +
                    "]); nodb IndexServiceInternal not activated." );
        }
    }

    @Override
    public void deleteIndices( final String... indexNames )
    {
        throw new UnsupportedOperationException(
            "deleting indices by raw name is Elasticsearch snapshot/restore vocabulary and has no nodb equivalent" );
    }

    @Override
    public void closeIndices( final String... indices )
    {
    }

    @Override
    public void openIndices( final String... indices )
    {
    }

    @Override
    public boolean waitForYellowStatus( final String... indexNames )
    {
        return true;
    }

    @Override
    public boolean isMaster()
    {
        return true;
    }
}
