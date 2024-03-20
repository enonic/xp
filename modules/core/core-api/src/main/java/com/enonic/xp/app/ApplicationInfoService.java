package com.enonic.xp.app;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.content.Contents;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.idprovider.IdProviderDescriptor;
import com.enonic.xp.macro.MacroDescriptors;
import com.enonic.xp.page.PageDescriptors;
import com.enonic.xp.region.LayoutDescriptors;
import com.enonic.xp.region.PartDescriptors;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.relationship.RelationshipTypes;
import com.enonic.xp.security.IdProviders;
import com.enonic.xp.task.TaskDescriptor;

@PublicApi
public interface ApplicationInfoService
{
    ContentTypes getContentTypes( ApplicationKey applicationKey );

    PageDescriptors getPageDescriptors( ApplicationKey applicationKey );

    PartDescriptors getPartDescriptors( ApplicationKey applicationKey );

    LayoutDescriptors getLayoutDescriptors( ApplicationKey applicationKey );

    RelationshipTypes getRelationshipTypes( ApplicationKey applicationKey );

    MacroDescriptors getMacroDescriptors( ApplicationKey applicationKey );

    Descriptors<TaskDescriptor> getTaskDescriptors( ApplicationKey applicationKey );

    @Deprecated
    Contents getContentReferences( ApplicationKey applicationKey );

    IdProviders getIdProviderReferences( ApplicationKey applicationKey );

    IdProviderDescriptor getIdProviderDescriptor( ApplicationKey applicationKey );

    ApplicationInfo getApplicationInfo( ApplicationKey applicationKey );
}
