package com.enonic.xp.admin.impl.rest.resource.application.json;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.Contents;

public class ContentReferencesJson
{
    private final ImmutableList<ContentReferenceJson> references;

    public ContentReferencesJson( final Contents contents )
    {
        final ImmutableList.Builder<ContentReferenceJson> builder = ImmutableList.builder();
        if(contents != null)
        {
            for ( final Content content : contents )
            {
                builder.add( new ContentReferenceJson( content ) );
            }
        }
        this.references = builder.build();
    }

    public ImmutableList<ContentReferenceJson> getReferences()
    {
        return references;
    }
}
