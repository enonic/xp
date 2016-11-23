package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.core.impl.content.serializer.PageDataSerializer;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueTypes;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jparsec.util.Lists;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class ContentOutboundDependenciesIdsResolver {
    private static final PageDataSerializer PAGE_SERIALIZER = new PageDataSerializer(ContentPropertyNames.PAGE);

    final ContentService contentService;

    public ContentOutboundDependenciesIdsResolver(final ContentService contentService) {
        this.contentService = contentService;
    }

    public Collection<ContentId> resolve(final ContentId contentId) {
        return resolveOutboundDependenciesIds(contentId);
    }

    private Collection<ContentId> resolveOutboundDependenciesIds(final ContentId contentId) {

        final Content content = this.contentService.getById(contentId);

        final List<ContentId> contentIds = Lists.arrayList();

        final PropertySet contentPageData = new PropertyTree().getRoot();
        if (content.getPage() != null) {
            PAGE_SERIALIZER.toData(content.getPage(), contentPageData);
        }

        Stream.concat(content.getData().getProperties(ValueTypes.REFERENCE).stream(),
                contentPageData.getProperties(ValueTypes.REFERENCE).stream()).forEach(property ->
        {

            final String value = property.getValue().toString();

            if (!contentId.toString().equals(value) && StringUtils.isNotBlank(value)) {
                contentIds.add(ContentId.from(value));
            }
        });

        return contentIds;
    }

}
