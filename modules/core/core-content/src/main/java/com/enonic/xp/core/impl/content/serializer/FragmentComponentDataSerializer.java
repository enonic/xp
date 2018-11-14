package com.enonic.xp.core.impl.content.serializer;


import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.region.FragmentComponent;
import com.enonic.xp.region.FragmentComponentType;
import com.enonic.xp.util.Reference;

final class FragmentComponentDataSerializer
    extends ComponentDataSerializer<FragmentComponent>
{
    private static final String ID = "id";

    private static final String DEFAULT_NAME = "Fragment";

    private final ContentService contentService;

    public FragmentComponentDataSerializer( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Override
    public void applyComponentToData( final FragmentComponent component, final PropertySet asData )
    {
        final PropertySet specBlock = asData.addSet( component.getType().toString() );

        if ( component.getFragment() != null )
        {
            specBlock.addReference( ID, Reference.from( component.getFragment().toString() ) );
        }
    }

    @Override
    public FragmentComponent fromData( final PropertySet data )
    {
        final FragmentComponent.Builder component = FragmentComponent.create().name( DEFAULT_NAME );

        final PropertySet specialBlockSet = data.getSet( FragmentComponentType.INSTANCE.toString() );

        if ( specialBlockSet != null && specialBlockSet.isNotNull( ID ) )
        {
            final ContentId contentId = ContentId.from( specialBlockSet.getString( ID ) );
            component.fragment( contentId );
            component.name( getContentDisplayName( contentId ) );
        }

        return component.build();
    }

    private String getContentDisplayName( final ContentId contentId )
    {
        try
        {
            final Content content = contentService.getById( contentId );
            return content.getDisplayName();
        }
        catch ( final ContentNotFoundException e )
        {
            return DEFAULT_NAME;
        }
    }
}
