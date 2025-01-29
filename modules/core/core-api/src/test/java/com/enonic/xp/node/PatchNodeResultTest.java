package com.enonic.xp.node;

import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PatchNodeResultTest
{
    @Test
    void shouldBuildSuccessfullyWhenValidNodeIdAndResults()
    {
        NodeId nodeId = NodeId.from( "valid-node-id" );
        Branch branch = Branch.from( "master" );
        Node node = mock( Node.class );
        when( node.id() ).thenReturn( nodeId );

        PatchNodeResult result = PatchNodeResult.create().nodeId( nodeId ).addResult( branch, node ).build();

        assertThat( result.getNodeId() ).isEqualTo( nodeId );
        assertThat( result.getResults() ).hasSize( 1 );
        assertThat( result.getResult( branch ) ).isEqualTo( node );
    }

    @Test
    void shouldFailWhenNodeIdIsNullButResultsArePresent()
    {
        Branch branch = Branch.from( "master" );
        Node node = mock( Node.class );

        assertThatThrownBy( () -> PatchNodeResult.create().addResult( branch, node ).build() ).isInstanceOf(
            IllegalArgumentException.class ).hasMessage( "Node id cannot be null" );
    }

    @Test
    void shouldFailWhenNodeIdDoesNotMatchResultNodeId()
    {
        NodeId nodeId = NodeId.from( "valid-node-id" );
        NodeId differentNodeId = NodeId.from( "different-node-id" );
        Branch branch = Branch.from( "master" );
        Node node = mock( Node.class );
        when( node.id() ).thenReturn( differentNodeId );

        assertThatThrownBy( () -> PatchNodeResult.create().nodeId( nodeId ).addResult( branch, node ).build() ).isInstanceOf(
            IllegalArgumentException.class ).hasMessage( "Node id does not match" );
    }

    @Test
    void shouldBuildSuccessfullyWhenNoResultsAndNoNodeId()
    {
        PatchNodeResult result = PatchNodeResult.create().build();

        assertThat( result.getNodeId() ).isNull();
        assertThat( result.getResults() ).isEmpty();
    }
}
