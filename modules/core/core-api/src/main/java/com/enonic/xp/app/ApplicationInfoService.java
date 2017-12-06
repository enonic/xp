package com.enonic.xp.app;

import com.google.common.annotations.Beta;

import com.enonic.xp.auth.AuthDescriptor;
import com.enonic.xp.content.Contents;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.macro.MacroDescriptors;
import com.enonic.xp.page.PageDescriptors;
import com.enonic.xp.region.LayoutDescriptors;
import com.enonic.xp.region.PartDescriptors;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.relationship.RelationshipTypes;
import com.enonic.xp.security.UserStores;
import com.enonic.xp.task.TaskDescriptor;

@Beta
public interface ApplicationInfoService
{
    ContentTypes getContentTypes( final ApplicationKey applicationKey );

    PageDescriptors getPageDescriptors( final ApplicationKey applicationKey );

    PartDescriptors getPartDescriptors( final ApplicationKey applicationKey );

    LayoutDescriptors getLayoutDescriptors( final ApplicationKey applicationKey );

    RelationshipTypes getRelationshipTypes( final ApplicationKey applicationKey );

    MacroDescriptors getMacroDescriptors( final ApplicationKey applicationKey );

    Descriptors<TaskDescriptor> getTaskDescriptors( final ApplicationKey applicationKey );

    Contents getContentReferences( final ApplicationKey applicationKey );

    UserStores getUserStoreReferences( final ApplicationKey applicationKey );

    AuthDescriptor getAuthDescriptor( final ApplicationKey applicationKey );

    ApplicationInfo getApplicationInfo( final ApplicationKey applicationKey );
}
