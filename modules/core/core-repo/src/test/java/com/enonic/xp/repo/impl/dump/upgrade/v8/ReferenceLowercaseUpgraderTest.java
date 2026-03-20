package com.enonic.xp.repo.impl.dump.upgrade.v8;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.util.Reference;

import static org.assertj.core.api.Assertions.assertThat;

class ReferenceLowercaseUpgraderTest
{
    private final ReferenceLowercaseUpgrader upgrader = new ReferenceLowercaseUpgrader();

    private static final RepositoryId REPO = RepositoryId.from( "com.enonic.cms.default" );

    @Test
    void uppercase_reference_is_lowercased()
    {
        final NodeStoreVersion nodeVersion = createNodeVersion( "MyReference" );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( REPO, nodeVersion );

        assertThat( result ).isNotNull();
        assertThat( result.data().getProperty( "myRef" ).getReference().toString() ).isEqualTo( "myreference" );
    }

    @Test
    void already_lowercase_reference_is_unchanged()
    {
        final NodeStoreVersion nodeVersion = createNodeVersion( "myreference" );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( REPO, nodeVersion );

        assertThat( result ).isNull();
    }

    private static NodeStoreVersion createNodeVersion( final String referenceValue )
    {
        final PropertyTree data = new PropertyTree();
        data.addReference( "myRef", createReference( referenceValue ) );

        return NodeStoreVersion.create().id( NodeId.from( "test-node" ) ).data( data ).build();
    }

    private static Reference createReference( final String value )
    {
        // NodeId validation rejects uppercase characters, but legacy dump data may contain them.
        // Use reflection to bypass validation and simulate legacy data.
        try
        {
            final NodeId nodeId = NodeId.from( "placeholder" );
            final Field field = nodeId.getClass().getSuperclass().getDeclaredField( "value" );
            field.setAccessible( true );
            field.set( nodeId, value );
            return new Reference( nodeId );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }
}