package com.enonic.xp.style;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.descriptor.DescriptorKey;

/** Provides application style descriptors and predefined image styles. */
@NullMarked
public interface StyleDescriptorService
{
    /**
     * Resolves and validates a predefined image style.
     *
     * @param key the application and name identifying the image style
     * @return the resolved style snapshot
     * @throws ImageStyleNotFoundException if the application has no image style with that name
     * @throws IllegalArgumentException if the style's image processing settings are invalid
     */
    ImageStyle getImageStyle( DescriptorKey key );

    /**
     * Finds the style descriptor supplied by an application.
     *
     * @param key the application key
     * @return the application's style descriptor
     */
    @Nullable StyleDescriptor getByApplication( ApplicationKey key );

    /**
     * Finds style descriptors supplied by the specified applications, omitting applications without a descriptor.
     *
     * @param applicationKeys the applications to inspect
     * @return the available style descriptors
     */
    StyleDescriptors getByApplications( ApplicationKeys applicationKeys );

    /**
     * Finds style descriptors supplied by installed applications.
     *
     * @return the available style descriptors
     */
    StyleDescriptors getAll();
}
