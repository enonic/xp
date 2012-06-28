package com.enonic.wem.core.jcr.accounts;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class JcrGroup
    extends JcrAccountBase
    implements JcrAccount
{
    private final Set<JcrAccount> members;

    private int membersCount;

    private String description;

    public JcrGroup()
    {
        super( JcrAccountType.GROUP );
        this.members = new HashSet<JcrAccount>();
    }

    protected JcrGroup( JcrAccountType type )
    {
        super( type );
        this.members = new HashSet<JcrAccount>();
    }

    public boolean hasPhoto()
    {
        return false;
    }

    public int getMembersCount()
    {
        return membersCount;
    }

    public void setMembersCount( int membersCount )
    {
        this.membersCount = membersCount;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public Set<JcrAccount> getMembers()
    {
        return members;
    }

    public void setMembers( final Collection<JcrAccount> members )
    {
        this.members.clear();
        for ( JcrAccount member : members )
        {
            this.members.add( member );
        }
    }

    public void addMember( final JcrAccount member )
    {
        this.members.add( member );
    }

    public boolean hasMember( final JcrAccount member )
    {
        return this.members.contains( member );
    }
}
