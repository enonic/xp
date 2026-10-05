package com.enonic.xp.portal.impl.macro;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemPath;
import com.enonic.xp.macro.MacroDescriptor;

/**
 * Names the parameters of a macro as the inputs of its descriptor's form: a parameter written in another case than its
 * input is named as the input.
 */
public final class MacroParamNames
{
    private final Form form;

    private final Map<String, String> namesIgnoringCase = new HashMap<>();

    public MacroParamNames( final MacroDescriptor descriptor )
    {
        this.form = descriptor.getForm();
        for ( final FormItem formItem : form )
        {
            namesIgnoringCase.put( formItem.getName().toLowerCase( Locale.ROOT ), formItem.getName() );
        }
    }

    /**
     * @param name name of the parameter, as written
     * @return the name of the input of the form matching it, or the name as written when none does
     */
    public String of( final String name )
    {
        if ( form.getFormItem( FormItemPath.from( name ) ) != null )
        {
            return name;
        }
        return namesIgnoringCase.getOrDefault( name.toLowerCase( Locale.ROOT ), name );
    }
}
