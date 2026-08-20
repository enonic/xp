package com.enonic.xp.node;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ListNodesParamsTest
{
    @Test
    void parent_path()
    {
        final ListNodesParams params = ListNodesParams.create().parentPath( NodePath.ROOT ).build();

        assertEquals( NodePath.ROOT, params.getParentPath() );
    }

    @Test
    void parent_path_is_required()
    {
        assertThrows( NullPointerException.class, () -> ListNodesParams.create().build() );
    }
}
