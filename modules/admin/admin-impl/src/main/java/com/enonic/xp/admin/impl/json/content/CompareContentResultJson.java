package com.enonic.xp.admin.impl.json.content;

import java.util.Objects;

import com.enonic.xp.content.CompareContentResult;
import com.enonic.xp.content.GetPublishStatusResult;

public class CompareContentResultJson
{
    private final String id;

    private final String compareStatus;

    private final String publishStatus;

    public CompareContentResultJson( final CompareContentResult compareContentResult, final GetPublishStatusResult getPublishStatusResult )
    {
        this.id = compareContentResult.getContentId().toString();
        this.compareStatus = compareContentResult.getCompareStatus().name();
        this.publishStatus = getPublishStatusResult == null || getPublishStatusResult.getPublishStatus() == null
            ? null
            : getPublishStatusResult.getPublishStatus().toString();
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getId()
    {
        return id;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getCompareStatus()
    {
        return compareStatus;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getPublishStatus()
    {
        return publishStatus;
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
        final CompareContentResultJson that = (CompareContentResultJson) o;
        return Objects.equals( id, that.id ) && Objects.equals( compareStatus, that.compareStatus ) &&
            Objects.equals( publishStatus, that.publishStatus );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, compareStatus, publishStatus );
    }
}
