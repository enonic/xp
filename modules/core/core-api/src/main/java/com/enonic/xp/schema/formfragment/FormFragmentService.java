package com.enonic.xp.schema.formfragment;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;

@PublicApi
public interface FormFragmentService
{
    FormFragmentDescriptor getByName( FormFragmentName name );

    FormFragmentDescriptors getAll();

    FormFragmentDescriptors getByApplication( ApplicationKey applicationKey );

    Form inlineFormItems( Form form );
}
