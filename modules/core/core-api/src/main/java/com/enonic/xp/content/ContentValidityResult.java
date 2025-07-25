package com.enonic.xp.content;

public final class ContentValidityResult
{
    private static final ContentValidityResult EMPTY = create().
        notValidContentIds( ContentIds.empty() ).
        notReadyContentIds( ContentIds.empty() ).
        build();

    private final ContentIds allProblematicContentIds;

    private final ContentIds notValidContentIds;

    private final ContentIds notReadyContentIds;

    private ContentValidityResult( Builder builder )
    {
        notValidContentIds = builder.notValidContentIds;
        notReadyContentIds = builder.notReadyContentIds;
        allProblematicContentIds = ContentIds.create().
            addAll( notValidContentIds ).
            addAll( notReadyContentIds ).
            build();
    }

    public ContentIds getNotValidContentIds()
    {
        return notValidContentIds;
    }

    public ContentIds getNotReadyContentIds()
    {
        return notReadyContentIds;
    }

    public ContentIds getAllProblematicContentIds()
    {
        return allProblematicContentIds;
    }

    public boolean allValid()
    {
        return allProblematicContentIds.isEmpty();
    }

    public static ContentValidityResult empty()
    {
        return EMPTY;
    }

    public static ContentValidityResult.Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private ContentIds notValidContentIds;

        private ContentIds notReadyContentIds;

        private Builder()
        {
            notValidContentIds = ContentIds.empty();
            notReadyContentIds = ContentIds.empty();
        }

        public Builder notValidContentIds( final ContentIds invalidContentIds )
        {
            this.notValidContentIds = invalidContentIds;
            return this;
        }

        public Builder notReadyContentIds( final ContentIds notReadyContentIds )
        {
            this.notReadyContentIds = notReadyContentIds;
            return this;
        }

        public ContentValidityResult build()
        {
            return new ContentValidityResult( this );
        }
    }
}
