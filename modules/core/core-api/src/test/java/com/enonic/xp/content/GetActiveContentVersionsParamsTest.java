package com.enonic.xp.content;

import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetActiveContentVersionsParamsTest
{
    @Test
    void testBuild()
    {
        final ContentId contentId = ContentId.from( "a" );
        final Branches branches = Branches.from( Branch.from( "draft" ), Branch.from( "master" ) );

        GetActiveContentVersionsParams params = GetActiveContentVersionsParams.create().
            contentId( contentId ).
            branches( branches ).build();

        assertEquals( contentId, params.getContentId() );
        assertEquals( branches, params.getBranches() );
    }

    @Test
    void testNullContentId()
    {
        assertThrows( NullPointerException.class, () -> GetActiveContentVersionsParams.create().
            branches( Branches.from( Branch.from( "draft" ) ) ).build() );
    }

    @Test
    void testNullBranches()
    {
        assertThrows( NullPointerException.class, () -> GetActiveContentVersionsParams.create().
            contentId( ContentId.from( "a" ) ).build() );
    }
}
