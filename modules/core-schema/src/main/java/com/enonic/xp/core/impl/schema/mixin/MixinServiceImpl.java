package com.enonic.xp.core.impl.schema.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.InlineMixin;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.schema.mixin.Mixins;

@Component(immediate = true)
public final class MixinServiceImpl
    implements MixinService, BundleListener
{
    private final Map<ApplicationKey, Mixins> map;

    private ApplicationService applicationService;

    private BundleContext context;

    public MixinServiceImpl()
    {
        this.map = Maps.newConcurrentMap();
    }

    @Activate
    public void start( final ComponentContext context )
    {
        this.context = context.getBundleContext();
        this.context.addBundleListener( this );
    }

    @Deactivate
    public void stop()
    {
        this.context.removeBundleListener( this );
    }

    @Override
    public Mixin getByName( final MixinName name )
    {
        return getByApplication( name.getApplicationKey() ).getMixin( name );
    }

    @Override
    public Mixin getByLocalName( final String localName )
    {
        return getAll().
            stream().
            filter( mixin -> mixin.getName().getLocalName().equals( localName ) ).
            findFirst().
            orElse( null );
    }

    @Override
    public Mixins getAll()
    {
        final Set<Mixin> mixinList = Sets.newLinkedHashSet();

        //Gets builtin mixins
        for ( ApplicationKey systemReservedApplicationKey : ApplicationKey.SYSTEM_RESERVED_APPLICATION_KEYS )
        {
            final Mixins mixins = getByApplication( systemReservedApplicationKey );
            mixinList.addAll( mixins.getList() );
        }

        //Gets application mixins
        for ( Application application : this.applicationService.getAllApplications() )
        {
            final Mixins mixins = getByApplication( application.getKey() );
            mixinList.addAll( mixins.getList() );
        }

        return Mixins.from( mixinList );
    }

    @Override
    public Mixins getByApplication( final ApplicationKey applicationKey )
    {
        return this.map.computeIfAbsent( applicationKey, this::loadByApplication );
    }

    private Mixins loadByApplication( final ApplicationKey applicationKey )
    {
        Mixins mixins = null;

        if ( ApplicationKey.SYSTEM_RESERVED_APPLICATION_KEYS.contains( applicationKey ) )
        {
            mixins = new BuiltinMixinsLoader().loadByApplication( applicationKey );
        }
        else
        {
            final Application application = this.applicationService.getApplication( applicationKey );
            if ( application != null && application.isStarted() )
            {
                final MixinLoader mixinLoader = new MixinLoader( application.getBundle() );
                mixins = mixinLoader.loadMixins();
            }
        }

        if ( mixins == null )
        {
            mixins = Mixins.empty();
        }

        return mixins;
    }

    @Override
    public void bundleChanged( final BundleEvent event )
    {
        if ( BundleEvent.STARTED == event.getType() || BundleEvent.STOPPED == event.getType() )
        {
            this.map.remove( ApplicationKey.from( event.getBundle() ) );
        }
    }

    @Override
    public Mixins getByContentType( final ContentType contentType )
    {
        return Mixins.from( contentType.getMetadata().stream().
            map( this::getByName ).
            filter( Objects::nonNull ).
            collect( Collectors.toSet() ) );
    }

    @Override
    public Form inlineFormItems( final Form form )
    {
        final Form.Builder transformedForm = Form.create();
        final List<FormItem> transformedFormItems = transformFormItems( form );

        for ( final FormItem formItem : transformedFormItems )
        {
            transformedForm.addFormItem( formItem );
        }
        return transformedForm.build();
    }

    private List<FormItem> transformFormItems( final Iterable<FormItem> iterable )
    {
        final List<FormItem> formItems = new ArrayList<>();
        for ( final FormItem formItem : iterable )
        {
            if ( formItem instanceof InlineMixin )
            {
                final InlineMixin inline = (InlineMixin) formItem;
                final Mixin mixin = getByName( inline.getMixinName() );
                if ( mixin != null )
                {
                    for ( FormItem mixinFormItem : mixin.getFormItems() )
                    {
                        formItems.add( mixinFormItem.copy() );
                    }
                }
                else
                {
                    throw new RuntimeException( "Mixin [" + inline.getMixinName() + "] not found" );
                }
            }
            else if ( formItem instanceof FormItemSet )
            {
                final FormItemSet.Builder formItemSetBuilder = FormItemSet.create( (FormItemSet) formItem );
                formItemSetBuilder.clearFormItems();
                formItemSetBuilder.addFormItems( transformFormItems( (FormItemSet) formItem ) );
                formItems.add( formItemSetBuilder.build() );
            }
            else if ( formItem instanceof FieldSet )
            {
                final FieldSet.Builder formItemSetBuilder = FieldSet.create( (FieldSet) formItem );
                formItemSetBuilder.clearFormItems();
                formItemSetBuilder.addFormItems( transformFormItems( (FieldSet) formItem ) );
                formItems.add( formItemSetBuilder.build() );
            }
            else
            {
                formItems.add( formItem.copy() );
            }
        }
        return formItems;
    }

    @Reference
    public void setApplicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }

}
