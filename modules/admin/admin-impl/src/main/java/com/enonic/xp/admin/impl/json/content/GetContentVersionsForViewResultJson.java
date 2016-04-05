package com.enonic.xp.admin.impl.json.content;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.xp.admin.impl.rest.resource.content.ContentPrincipalsResolver;
import com.enonic.xp.content.ActiveContentVersionEntry;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.content.ContentVersions;
import com.enonic.xp.content.FindContentVersionsResult;
import com.enonic.xp.content.GetActiveContentVersionsResult;

public class GetContentVersionsForViewResultJson
{
    private ActiveContentVersionEntryJson activeVersion;

    private Set<ContentVersionViewJson> contentVersions = Sets.newLinkedHashSet();

    private final long totalHits;

    private final long hits;

    private final int from;

    private final int size;

    public GetContentVersionsForViewResultJson( final FindContentVersionsResult allVersions,
                                                final GetActiveContentVersionsResult activeVersions,
                                                final ContentPrincipalsResolver principalsResolver )
    {

        final ContentVersions filteredVersions = filterContentVersions( allVersions );

        this.totalHits = allVersions.getTotalHits();
        this.from = allVersions.getFrom();
        this.size = allVersions.getSize();
        this.hits = allVersions.getHits();

        for ( final ContentVersion contentVersion : filteredVersions )
        {
            this.contentVersions.add(
                new ContentVersionViewJson( contentVersion, principalsResolver.findPrincipal( contentVersion.getModifier() ),
                                            findWorkspaces( contentVersion, activeVersions ) ) );
        }

        final ActiveContentVersionEntry activeVersion = getActiveContentVersion( activeVersions );

        if ( activeVersion != null )
        {
            this.activeVersion = new ActiveContentVersionEntryJson( activeVersion, principalsResolver );
        }
    }

    private List<String> findWorkspaces( final ContentVersion contentVersion, final GetActiveContentVersionsResult activeVersions )
    {
        final List<String> result = new ArrayList<>();
        activeVersions.getActiveContentVersions().stream().filter(
            activeVersion -> activeVersion.getContentVersion().getId().equals( contentVersion.getId() ) ).
            forEach( activeVersion -> result.add( activeVersion.getBranch().toString() ) );
        return result;
    }

    private ActiveContentVersionEntry getActiveContentVersion( final GetActiveContentVersionsResult activeVersions )
    {
        return activeVersions.getActiveContentVersions().stream().filter(
            activeVersion -> ContentConstants.BRANCH_DRAFT.equals( activeVersion.getBranch() ) ).findFirst().orElse( null );
    }

    private ContentVersions filterContentVersions( final FindContentVersionsResult allVersions )
    {

        if ( allVersions.getHits() == 0 )
        {
            return allVersions.getContentVersions();
        }

        final Iterator<ContentVersion> iterator = allVersions.getContentVersions().iterator();

        final ContentVersions.Builder filteredContentVersions = ContentVersions.create().
            contentId( allVersions.getContentVersions().getContentId() );

        ContentVersion previouslyAdded = iterator.next();
        filteredContentVersions.add( previouslyAdded );

        int msRangeFilter = 500;

        while ( iterator.hasNext() )
        {
            final ContentVersion contentVersion = iterator.next();
            if ( Math.abs( previouslyAdded.getModified().toEpochMilli() - contentVersion.getModified().toEpochMilli() ) > msRangeFilter )
            {
                filteredContentVersions.add( contentVersion );
                previouslyAdded = contentVersion;
            }
        }

        return filteredContentVersions.build();
    }

    @SuppressWarnings("UnusedDeclaration")
    public ActiveContentVersionEntryJson getActiveVersion()
    {
        return activeVersion;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Set<ContentVersionViewJson> getContentVersions()
    {
        return contentVersions;
    }

    public long getTotalHits()
    {
        return totalHits;
    }

    public long getHits()
    {
        return hits;
    }

    public int getFrom()
    {
        return from;
    }

    public int getSize()
    {
        return size;
    }
}
