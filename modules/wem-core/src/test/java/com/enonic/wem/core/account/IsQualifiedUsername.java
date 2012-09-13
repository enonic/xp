package com.enonic.wem.core.account;

import org.mockito.ArgumentMatcher;

import com.enonic.cms.core.security.user.QualifiedUsername;

/**
 * Argument matcher for simulating QualifiedUsername.equals in mocked objects.
 */
public class IsQualifiedUsername
    extends ArgumentMatcher<QualifiedUsername>
{
    private final QualifiedUsername qualifiedName;

    public IsQualifiedUsername( final QualifiedUsername qualifiedName )
    {
        this.qualifiedName = qualifiedName;
    }

    public boolean matches( Object other )
    {
        if ( other == null )
        {
            return false;
        }
        final QualifiedUsername otherQualifiedName = (QualifiedUsername) other;
        return this.qualifiedName.getUsername().equals( otherQualifiedName.getUsername() ) &&
            this.qualifiedName.getUserStoreName().equals( otherQualifiedName.getUserStoreName() );
    }
}
