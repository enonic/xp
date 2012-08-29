package com.enonic.wem.web.rest2.resource.userstore;

import org.mockito.ArgumentMatcher;

import com.enonic.cms.core.security.group.GroupSpecification;

public class GroupSpecificationMatcher
    extends ArgumentMatcher<GroupSpecification>
{
    private final GroupSpecification groupSpec;

    public GroupSpecificationMatcher( final GroupSpecification groupSpec )
    {
        this.groupSpec = groupSpec;
    }

    public boolean matches( Object other )
    {
        if ( other == null || !( other instanceof GroupSpecification ) )
        {
            return false;
        }

        final GroupSpecification otherSpec = (GroupSpecification) other;

        if ( ( groupSpec.getKey() == null && otherSpec.getKey() != null ) ||
            ( groupSpec.getKey() != null && !groupSpec.getKey().equals( otherSpec.getKey() ) ) )
        {
            return false;
        }

        if ( ( groupSpec.getUserStoreKey() == null && otherSpec.getUserStoreKey() != null ) ||
            ( groupSpec.getUserStoreKey() != null && !groupSpec.getUserStoreKey().equals( otherSpec.getUserStoreKey() ) ) )
        {
            return false;
        }

        if ( ( groupSpec.getType() == null && otherSpec.getType() != null ) ||
            ( groupSpec.getType() != null && !groupSpec.getType().equals( otherSpec.getType() ) ) )
        {
            return false;
        }

        if ( ( groupSpec.getDeletedState() == null && otherSpec.getDeletedState() != null ) ||
            ( groupSpec.getDeletedState() != null && !groupSpec.getDeletedState().equals( otherSpec.getDeletedState() ) ) )
        {
            return false;
        }

        if ( ( groupSpec.getName() == null && otherSpec.getName() != null ) ||
            ( groupSpec.getName() != null && !groupSpec.getName().equals( otherSpec.getName() ) ) )
        {
            return false;
        }

        if ( ( groupSpec.getSyncValue() == null && otherSpec.getSyncValue() != null ) ||
            ( groupSpec.getSyncValue() != null && !groupSpec.getSyncValue().equals( otherSpec.getSyncValue() ) ) )
        {
            return false;
        }

        return true;
    }
}
