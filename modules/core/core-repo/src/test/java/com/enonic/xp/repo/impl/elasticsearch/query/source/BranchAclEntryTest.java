package com.enonic.xp.repo.impl.elasticsearch.query.source;

import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class BranchAclEntryTest
{

    @Test
    void equals()
    {

        final Branch branch = Branch.from( "fisk" );
        final PrincipalKeys keys = PrincipalKeys.from( PrincipalKey.ofRole( "fisk" ) );
        final BranchAclEntry entry1 = new BranchAclEntry( branch, keys );
        final BranchAclEntry entry2 = new BranchAclEntry( branch, keys );

        assertEquals( entry1, entry2 );
    }

    @Test
    void not_equals()
    {

        final Branch branch = Branch.from( "fisk" );
        final BranchAclEntry entry1 = new BranchAclEntry( branch, PrincipalKeys.from( PrincipalKey.ofRole( "fisk" ) ));
        final BranchAclEntry entry2 = new BranchAclEntry( branch, PrincipalKeys.from( PrincipalKey.ofRole( "ost" ) ));

        assertNotEquals( entry1, entry2 );
    }
}
