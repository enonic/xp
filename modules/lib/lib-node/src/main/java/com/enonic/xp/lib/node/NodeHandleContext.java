package com.enonic.xp.lib.node;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKey;

public class NodeHandleContext
{
    private RepositoryId repoId;

    private Branch branch;

    private String username;

    private String idProvider;

    private PrincipalKey[] principals;

    public RepositoryId getRepoId()
    {
        return repoId;
    }

    public Branch getBranch()
    {
        return branch;
    }

    public String getUsername()
    {
        return username;
    }

    public String getIdProvider()
    {
        return idProvider;
    }

    public PrincipalKey[] getPrincipals()
    {
        return principals;
    }

    public void setRepoId( final String repoId )
    {
        this.repoId = RepositoryId.from( repoId );
    }

    public void setBranch( final String branch )
    {
        this.branch = Branch.from( branch );
    }

    public void setUsername( final String username )
    {
        this.username = username;
    }

    public void setIdProvider( final String idProvider )
    {
        this.idProvider = idProvider;
    }

    public void setPrincipals( final String[] principals )
    {
        if ( principals != null )
        {
            this.principals = new PrincipalKey[principals.length];
            for ( int i = 0; i < principals.length; i++ )
            {
                this.principals[i] = PrincipalKey.from( principals[i] );
            }
        }
        else
        {
            this.principals = null;
        }
    }
}
