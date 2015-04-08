package com.enonic.xp.content;

import org.junit.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;

import static org.junit.Assert.*;

public class GetActiveContentVersionsParamsTest
{

    private final ContentId contentId = ContentId.from( "a" );

    @Test
    public void testEquals()
    {
        Branch branch = Branch.create().name( "branchName" ).build();
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
