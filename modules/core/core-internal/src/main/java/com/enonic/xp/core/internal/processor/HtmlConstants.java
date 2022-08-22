package com.enonic.xp.core.internal.processor;

import java.util.regex.Pattern;

public class HtmlConstants
{
    public static final int MATCH_INDEX = 1;

    public static final int TAG_NAME_INDEX = MATCH_INDEX + 1;

    public static final int ATTR_INDEX = TAG_NAME_INDEX + 1;

    public static final int ATTR_VALUE_INDEX = ATTR_INDEX + 1;

    public static final int LINK_INDEX = ATTR_VALUE_INDEX + 1;

    public static final int TYPE_INDEX = LINK_INDEX + 1;

    public static final int MODE_INDEX = TYPE_INDEX + 1;

    public static final int ID_INDEX = MODE_INDEX + 1;

    public static final int PARAMS_INDEX = ID_INDEX + 1;

    public static final int NB_GROUPS = ID_INDEX;

    public static final String INLINE_MODE = "inline";

    public static final String MEDIA_TYPE = "media";

    public static final String CONTENT_TYPE = "content";

    public static final String IMAGE_TYPE = "image";

    public static final String DOWNLOAD_MODE = "download";

    public static final Pattern CONTENT_PATTERN = Pattern.compile(
        "(<(\\w+)[^>]+?(href|src)=(\"((" + CONTENT_TYPE + "|" + MEDIA_TYPE + "|" + IMAGE_TYPE + ")://(?:(" + DOWNLOAD_MODE + "|" +
            INLINE_MODE + ")/)?([0-9a-z-/]+)(\\?[^\"]+)?)\"))", Pattern.MULTILINE | Pattern.UNIX_LINES );
}
