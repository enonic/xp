package com.enonic.xp.schema.content;

import com.enonic.xp.form.Form;
import com.enonic.xp.schema.formfragment.FormFragmentDescriptor;
import com.enonic.xp.schema.formfragment.FormFragmentName;


public interface CmsFormFragmentService
{
    FormFragmentDescriptor getByName( FormFragmentName name );

    Form inlineFormItems( Form form );
}
