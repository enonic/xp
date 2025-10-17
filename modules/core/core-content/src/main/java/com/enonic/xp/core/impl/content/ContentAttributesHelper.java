package com.enonic.xp.core.impl.content;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import com.enonic.xp.content.Content;

public class ContentAttributesHelper
{
    private static final Map<String, Function<Content, ?>> FIELD_GETTERS =
        Map.of( "displayName", Content::getDisplayName, "data", Content::getData, "x", Content::getAllExtraData, "page", Content::getPage,
                "owner", Content::getOwner, "language", Content::getLanguage, "publish", Content::getPublishInfo, "workflow",
                Content::getWorkflowInfo, "variantOf", Content::getVariantOf, "attachments", Content::getAttachments );

    public static List<String> modifiedFields( Content existingContent, Content updatedContent )
    {
        return FIELD_GETTERS.entrySet()
            .stream()
            .filter( e -> !Objects.equals( e.getValue().apply( existingContent ), e.getValue().apply( updatedContent ) ) )
            .map( Map.Entry::getKey )
            .sorted()
            .toList();
    }
}
