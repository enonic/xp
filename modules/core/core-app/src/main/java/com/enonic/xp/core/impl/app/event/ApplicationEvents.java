package com.enonic.xp.core.impl.app.event;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.event.Event;

public final class ApplicationEvents
{
    private ApplicationEvents()
    {
    }

    public static final String EVENT_TYPE = "application";

    public static final String APPLICATION_KEY_KEY = "applicationKey";

    public static final String SYSTEM_APPLICATION = "systemApplication";

    public static final String EVENT_TYPE_KEY = "eventType";

    public static final String INSTALLED = "INSTALLED";

    public static final String STARTED = "STARTED";

    public static final String STOPPED = "STOPPED";

    public static final String UNINSTALLED = "UNINSTALLED";

    public static final String UPDATED = "UPDATED";

    public static final String KIND_KEY = "kind";

    public static final String NAME_KEY = "name";

    public static final String ACTION_KEY = "action";

    public static final String KIND_APPLICATION = "Application";

    public static final String KIND_CMS = "CMS";

    public static final String KIND_CONTENT_TYPE = "ContentType";

    public static final String KIND_FORM_FRAGMENT = "FormFragment";

    public static final String KIND_MIXIN = "Mixin";

    public static final String KIND_PART = "Part";

    public static final String KIND_LAYOUT = "Layout";

    public static final String KIND_PAGE = "Page";

    public static final String KIND_MACRO = "Macro";

    public static final String KIND_STYLE = "Style";

    public static final String KIND_PHRASES = "Phrases";

    public static final String ACTION_CREATED = "created";

    public static final String ACTION_UPDATED = "updated";

    public static final String ACTION_DELETED = "deleted";

    public static Event installed( ApplicationKey applicationKey )
    {
        return event( applicationKey, INSTALLED );
    }

    public static Event uninstalled( ApplicationKey applicationKey )
    {
        return event( applicationKey, UNINSTALLED );
    }

    public static Event started( ApplicationKey applicationKey )
    {
        return event( applicationKey, STARTED );
    }

    public static Event stopped( ApplicationKey applicationKey )
    {
        return event( applicationKey, STOPPED );
    }

    /**
     * The definition of the application changed: its descriptor ({@code Application}), its cms descriptor ({@code CMS}), or one of
     * the schemas it provides, named by its {@code kind} and {@code name}. The kind is the {@code kind} the descriptor declares
     * ({@code ContentType}, {@code Part}, {@code Style}, ...), {@code Phrases} for the i18n phrases, which declare none.
     * Whoever caches what the application provides re-reads it.
     * Published once per change, on the cluster node where it was made, to the whole cluster.
     *
     * @param kind   one of the {@code KIND_*} values
     * @param name   the key of the schema changed ({@code myapp:mytype}), or the file name of the phrases; {@code null} for the kinds
     *               there is one of per application: {@code Application}, {@code CMS} and {@code Style}
     * @param action one of the {@code ACTION_*} values
     */
    public static Event updated( final ApplicationKey applicationKey, final String kind, final String name, final String action )
    {
        final Event.Builder builder = Event.create( EVENT_TYPE )
            .distributed( true )
            .value( APPLICATION_KEY_KEY, applicationKey )
            .value( SYSTEM_APPLICATION, false )
            .value( EVENT_TYPE_KEY, UPDATED )
            .value( KIND_KEY, kind )
            .value( ACTION_KEY, action );
        if ( name != null )
        {
            builder.value( NAME_KEY, name );
        }
        return builder.build();
    }


    private static Event event( final ApplicationKey applicationKey, final String type )
    {
        return Event.create( EVENT_TYPE )
            .distributed( false )
            .value( APPLICATION_KEY_KEY, applicationKey )
            .value( SYSTEM_APPLICATION, false )
            .value( EVENT_TYPE_KEY, type )
            .build();
    }
}
