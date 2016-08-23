package com.enonic.xp.content;

import org.junit.Test;

import com.enonic.xp.branch.BranchId;
import com.enonic.xp.branch.BranchIds;

import static org.junit.Assert.*;

public class GetActiveContentVersionsParamsTest
{

    private final ContentId contentId = ContentId.from( "a" );

    @Test
    public void testEquals()
    {
        BranchId branchId = BranchId.create().value( "branchName" ).build();
        BranchIds branchIds = BranchIds.from( branchId );

        GetActiveContentVersionsParams params = GetActiveContentVersionsParams.create().
            contentId( contentId ).
            branches( branchIds ).
            build();

        assertEquals( params, params );
        assertEquals( params.getContentId(), contentId );
        assertEquals( params.getBranchIds().getSet(), branchIds.getSet() );

    }

}
