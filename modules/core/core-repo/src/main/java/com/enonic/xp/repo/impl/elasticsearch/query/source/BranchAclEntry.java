package com.enonic.xp.repo.impl.elasticsearch.query.source;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.security.PrincipalKeys;

class BranchAclEntry
{
    BranchAclEntry( final Branch branch, final PrincipalKeys acl )
    {
        this.branch = branch;
        this.acl = acl;
    }

    private Branch branch;

    private PrincipalKeys acl;

    public Branch getBranch()
    {
        return branch;
    }

    public PrincipalKeys getAcl()
    {
        return acl;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final BranchAclEntry branchAcl = (BranchAclEntry) o;

        if ( branch != null ? !branch.equals( branchAcl.branch ) : branchAcl.branch != null )
        {
            return false;
        }
        return acl != null ? acl.equals( branchAcl.acl ) : branchAcl.acl == null;

    }

    @Override
    public int hashCode()
    {
        int result = branch != null ? branch.hashCode() : 0;
        result = 31 * result + ( acl != null ? acl.hashCode() : 0 );
        return result;
    }

}
