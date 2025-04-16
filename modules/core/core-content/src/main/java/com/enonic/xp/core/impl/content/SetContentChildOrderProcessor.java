package com.enonic.xp.core.impl.content;

import java.util.EnumSet;

import com.enonic.xp.content.ContentInheritType;

public class SetContentChildOrderProcessor
    extends InheritedContentDataProcessor
{
    @Override
    protected EnumSet<ContentInheritType> getTypesToProceed()
    {
        return EnumSet.of( ContentInheritType.SORT );
    }
}
