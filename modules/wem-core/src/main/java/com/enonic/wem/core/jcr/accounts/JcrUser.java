package com.enonic.wem.core.jcr.accounts;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;

public final class JcrUser
    extends JcrAccountBase
    implements JcrAccount
{
    private byte[] photo;

    private String email;

    private boolean hasPhoto = false;

    private DateTime lastLogged;

    private String created;

    private JcrUserInfo userInfo;

    private final Map<String, JcrGroup> memberships;

    public JcrUser()
    {
        super( JcrAccountType.USER );
        this.memberships = new HashMap<String, JcrGroup>();
    }

    public DateTime getLastLogged()
    {
        return lastLogged;
    }

    public void setLastLogged( DateTime lastLogged )
    {
        this.lastLogged = lastLogged;
    }

    public String getCreated()
    {
        return created;
    }

    public void setCreated( String created )
    {
        this.created = created;
    }

    public JcrUserInfo getUserInfo()
    {
        return userInfo;
    }

    public void setUserInfo( JcrUserInfo userInfo )
    {
        this.userInfo = userInfo;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail( final String email )
    {
        this.email = email;
    }

    public boolean hasPhoto()
    {
        return this.hasPhoto;
    }

    public void setHasPhoto( boolean hasPhoto )
    {
        this.hasPhoto = hasPhoto;
    }

    public byte[] getPhoto()
    {
        return photo;
    }

    public void setPhoto( byte[] photo )
    {
        this.photo = photo;
    }

    public Set<JcrGroup> getMemberships()
    {
        return new HashSet<JcrGroup>( memberships.values() );
    }

    public void setMemberships( final Collection<JcrGroup> memberships )
    {
        this.memberships.clear();
        for ( JcrGroup membership : memberships )
        {
            this.memberships.put( membership.getId(), membership );
        }
    }

    public void addMembership( final JcrGroup membership )
    {
        this.memberships.put( membership.getId(), membership );
    }

    public boolean isMemberOf( final JcrGroup group )
    {
        return this.memberships.containsKey( group.getId() );
    }
}
