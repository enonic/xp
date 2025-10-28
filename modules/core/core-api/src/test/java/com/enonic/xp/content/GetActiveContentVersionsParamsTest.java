package com.enonic.xp.content;

import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetActiveContentVersionsParamsTest
{

    private final ContentId contentId = ContentId.from( "a" );

    @Test
    void testEquals()
    {
        Branch branch = Branch.create().value( "branchName" ).build();
        Branches branches = Branches.from( branch );

        GetActiveContentVersionsParams params = GetActiveContentVersionsParams.create().
            contentId( contentId ).
            branches( branches ).
            build();

        assertEquals( params, params );
        assertEquals( params.getContentId(), contentId );
        assertEquals( params.getBranches().getSet(), branches.getSet() );

    }

}
