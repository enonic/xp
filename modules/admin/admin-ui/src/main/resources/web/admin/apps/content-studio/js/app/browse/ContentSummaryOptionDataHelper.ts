import '../../api.ts';

import TreeNode = api.ui.treegrid.TreeNode;
import ContentSummary = api.content.ContentSummary;
import OptionDataHelper = api.ui.selector.OptionDataHelper;
import ContentSummaryBuilder = api.content.ContentSummaryBuilder;
import ContentState = api.schema.content.ContentState;
import ContentName = api.content.ContentName;
import ContentPath = api.content.ContentPath;

export class ContentSummaryOptionDataHelper implements OptionDataHelper<ContentSummary> {

    hasChildren(data: ContentSummary): boolean {
        return data ? data.hasChildren() : false;
    }

    getDataId(data: ContentSummary): string {
        return data ? data.getId() : '';
    }

    isChildAndAncestor(possibleChild: ContentSummary, possibleParent: ContentSummary) {
        return possibleChild.getPath().isDescendantOf(possibleParent.getPath());
    }
}
