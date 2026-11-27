package com.enonic.xp.app;

import org.jspecify.annotations.NullMarked;

@NullMarked
@FunctionalInterface
public interface ApplicationDescriptorEditor
{
    void edit( EditableApplicationDescriptor edit );
}
