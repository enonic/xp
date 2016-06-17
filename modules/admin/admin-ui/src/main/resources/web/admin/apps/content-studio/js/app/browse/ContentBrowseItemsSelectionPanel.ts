import "../../api.ts";
import {ContentBrowseItem} from "./ContentBrowseItem";

import BrowseItem = api.app.browse.BrowseItem;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import ContentSummaryAndCompareStatusViewer = api.content.ContentSummaryAndCompareStatusViewer;
import ContentSummaryViewer = api.content.ContentSummaryViewer;

export class ContentBrowseItemsSelectionPanel extends api.app.browse.BrowseItemsSelectionPanel<ContentSummaryAndCompareStatus> {

    createItemViewer(item: ContentBrowseItem): ContentSummaryAndCompareStatusViewer {
        var viewer = new ContentSummaryAndCompareStatusViewer();
        viewer.setObject(item.getModel());
        return viewer;
    }
}
