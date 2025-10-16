package com.enonic.xp.core.impl.schema.fragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormFragment;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.content.CmsFormFragmentService;
import com.enonic.xp.schema.formfragment.FormFragmentDescriptor;
import com.enonic.xp.schema.formfragment.FormFragmentName;

@Component(immediate = true)
public final class CmsFormFragmentServiceImpl
    implements CmsFormFragmentService
{
    private static final Logger LOG = LoggerFactory.getLogger( CmsFormFragmentServiceImpl.class );

    private final CmsFormFragmentLoader cmsFormFragmentLoader;

    @Activate
    public CmsFormFragmentServiceImpl( @Reference final ResourceService resourceService )
    {
        this.cmsFormFragmentLoader = new CmsFormFragmentLoader( resourceService );
    }

    @Override
    public FormFragmentDescriptor getByName( final FormFragmentName name )
    {
        return cmsFormFragmentLoader.get( name );
    }

    @Override
    public Form inlineFormItems( final Form form )
    {
        return doInlineFormItems( form, new HashSet<>() );
    }

    private Form doInlineFormItems( final Form form, final Set<FormFragmentName> fragmentNames )
    {
        final Form.Builder transformedForm = Form.create();
        final List<FormItem> transformedFormItems = transformFormItems( form, fragmentNames );
        transformedFormItems.forEach( transformedForm::addFormItem );
        return transformedForm.build();
    }

    private List<FormItem> transformFormItems( final Iterable<FormItem> iterable, final Set<FormFragmentName> inlineFragmentStack )
    {
        final List<FormItem> formItems = new ArrayList<>();
        for ( final FormItem formItem : iterable )
        {
            if ( formItem instanceof FormFragment )
            {
                final FormFragment inline = (FormFragment) formItem;
                final FormFragmentName formFragmentName = inline.getFormFragmentName();
                final FormFragmentDescriptor fragmentDescriptor = getByName( formFragmentName );
                if ( fragmentDescriptor != null )
                {
                    if ( inlineFragmentStack.contains( formFragmentName ) )
                    {
                        final String error = "Cycle detected in form fragment [" + fragmentDescriptor.getName() +
                            "]. It contains a form fragment that references itself.";
                        LOG.error( error );
                        throw new IllegalArgumentException( error );
                    }

                    inlineFragmentStack.add( formFragmentName );
                    final Form fragmentForm = doInlineFormItems( fragmentDescriptor.getForm(), inlineFragmentStack );
                    inlineFragmentStack.remove( formFragmentName );

                    for ( final FormItem formFragmentFormItem : fragmentForm )
                    {
                        formItems.add( formFragmentFormItem.copy() );
                    }
                }
                else
                {
                    throw new IllegalArgumentException( "Form fragment [" + formFragmentName + "] not found" );
                }
            }
            else if ( formItem instanceof FormItemSet )
            {
                final FormItemSet.Builder formItemSetBuilder = FormItemSet.create( (FormItemSet) formItem );
                formItemSetBuilder.clearFormItems();
                formItemSetBuilder.addFormItems( transformFormItems( (FormItemSet) formItem, inlineFragmentStack ) );
                formItems.add( formItemSetBuilder.build() );
            }
            else if ( formItem instanceof FieldSet )
            {
                final FieldSet.Builder formItemSetBuilder = FieldSet.create( (FieldSet) formItem );
                formItemSetBuilder.clearFormItems();
                formItemSetBuilder.addFormItems( transformFormItems( (FieldSet) formItem, inlineFragmentStack ) );
                formItems.add( formItemSetBuilder.build() );
            }
            else if ( formItem instanceof FormOptionSet )
            {
                final FormOptionSet.Builder formOptionSetBuilder = FormOptionSet.create( (FormOptionSet) formItem );
                formOptionSetBuilder.clearOptions();
                for ( FormOptionSetOption option : (FormOptionSet) formItem )
                {
                    final FormOptionSetOption.Builder optionBuilder = FormOptionSetOption.create( option );
                    optionBuilder.clearFormItems();
                    optionBuilder.addFormItems( transformFormItems( option, inlineFragmentStack ) );
                    formOptionSetBuilder.addOptionSetOption( optionBuilder.build() );
                }
                formItems.add( formOptionSetBuilder.build() );
            }
            else
            {
                formItems.add( formItem.copy() );
            }
        }
        return formItems;
    }
}
