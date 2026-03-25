package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.upgrade.NodeVersionUpgrader;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.util.Reference;

public class ReferenceLowercaseUpgrader
    implements NodeVersionUpgrader
{
    private static final Logger LOG = LoggerFactory.getLogger( ReferenceLowercaseUpgrader.class );

    @Override
    public NodeStoreVersion upgradeNodeVersion( final RepositoryId repositoryId, final NodeStoreVersion nodeVersion )
    {
        boolean modified = false;

        for ( final Property property : nodeVersion.data().getProperties( ValueTypes.REFERENCE ) )
        {
            if ( property.hasNullValue() )
            {
                continue;
            }

            final String value = property.getReference().toString();
            final String lowercased = value.toLowerCase( Locale.ROOT );

            if ( !value.equals( lowercased ) )
            {
                property.setValue( ValueFactory.newReference( Reference.from( lowercased ) ) );
                modified = true;
            }
        }

        if ( modified )
        {
            LOG.info( "Lowercased reference properties for node [{}] in repository [{}]", nodeVersion.id(), repositoryId );
        }
        return modified ? nodeVersion : null;
    }
}
