package com.enonic.xp.core.impl.app;


public final class SchemaResourceNames
{
    public static final String FORM_FRAGMENTS_ROOT_NAME = "form-fragments";

    public static final String MIXINS_ROOT_NAME = "mixins";

    public static final String STYLE_ROOT_NAME = "style";

    public static final String CMS_ROOT_NAME = "cms";

    public static final String CONTENT_TYPE_ROOT_NAME = "content-types";

    public static final String PART_ROOT_NAME = "parts";

    public static final String PAGE_ROOT_NAME = "pages";

    public static final String LAYOUT_ROOT_NAME = "layouts";

    public static final String MACROS_ROOT_NAME = "macros";

    public static final String I18N_ROOT_NAME = "i18n";

    public static final String PHRASES_ROOT_NAME = "phrases";

    public static final String STYLE_NAME = "style";

    /**
     * Binary of a schema icon node (content type, part, macro, ...). The application icon is attached to the descriptor node under
     * its file name instead ({@link SchemaResourcePaths#appIconBinaryReference(String)}), so the descriptor node is never mistaken
     * for an icon node.
     */

    private SchemaResourceNames()
    {

    }

}
