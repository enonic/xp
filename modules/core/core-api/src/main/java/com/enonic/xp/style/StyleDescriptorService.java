package com.enonic.xp.style;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;

public interface StyleDescriptorService
{

    StyleDescriptor getByApplication( ApplicationKey key );

    StyleDescriptors getByApplications( ApplicationKeys applicationKeys );

    StyleDescriptors getAll();

}
