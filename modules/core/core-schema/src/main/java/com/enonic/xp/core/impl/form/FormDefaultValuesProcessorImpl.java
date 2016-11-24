package com.enonic.xp.core.impl.form;

import java.util.stream.StreamSupport;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.Value;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormDefaultValuesProcessor;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypes;

import static com.enonic.xp.form.FormItemType.FORM_ITEM_SET;
import static com.enonic.xp.form.FormItemType.FORM_OPTION_SET;
import static com.enonic.xp.form.FormItemType.FORM_OPTION_SET_OPTION;
import static com.enonic.xp.form.FormItemType.INPUT;
import static com.enonic.xp.form.FormItemType.LAYOUT;

@Component(immediate = true)
public final class FormDefaultValuesProcessorImpl
    implements FormDefaultValuesProcessor
{
    private final static Logger LOG = LoggerFactory.getLogger( FormDefaultValuesProcessorImpl.class );

    public void setDefaultValues( final Form form, final PropertyTree data )
    {
        processFormItems( form.getFormItems(), data, PropertyPath.from( "" ) );
    }

    private void processFormItems( final Iterable<FormItem> formItems, final PropertyTree data, final PropertyPath parentPath )
    {
        StreamSupport.stream( formItems.spliterator(), false ).forEach( formItem -> {
            if ( formItem.getType() == INPUT )
            {
                Input input = formItem.toInput();
                if ( input.getDefaultValue() != null )
                {
                    try
                    {
                        final Value defaultValue = InputTypes.BUILTIN.resolve( input.getInputType() ).
                            createDefaultValue( input );
                        if ( defaultValue != null )
                        {
                            data.setProperty( PropertyPath.from( parentPath, input.getName() ), defaultValue );
                        }
                    }
                    catch ( IllegalArgumentException ex )
                    {
                        LOG.warn(
                            "Invalid default value for " + input.getInputType() + " input type with name '" + input.getName() + "': '" +
                                input.getDefaultValue().getRootValue() + "'" + ( ex.getMessage() == null ? "" : " - " + ex.getMessage() ) );
                    }
                }
            }
            else if ( formItem.getType() == FORM_ITEM_SET )
            {
                processFormItems( formItem.toFormItemSet().getFormItems(), data, PropertyPath.from( parentPath, formItem.getName() ) );
            }
            else if ( formItem.getType() == LAYOUT && formItem.toLayout() instanceof FieldSet )
            {
                processFormItems( (FieldSet) formItem.toLayout(), data, parentPath );
            }
            else if ( formItem.getType() == FORM_OPTION_SET_OPTION )
            {
                FormOptionSetOption option = formItem.toFormOptionSetOption();
                if ( option.isDefaultOption() )
                {
                    processFormItems( option.getFormItems(), data, PropertyPath.from( parentPath, formItem.getName() ) );
                }
            }
            else if ( formItem.getType() == FORM_OPTION_SET )
            {
                processFormItems( formItem.toFormOptionSet().getFormItems(), data, PropertyPath.from( parentPath, formItem.getName() ) );
            }
        } );
    }
}
