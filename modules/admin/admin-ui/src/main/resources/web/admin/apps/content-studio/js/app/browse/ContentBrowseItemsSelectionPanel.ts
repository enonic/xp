import '../../api.ts';
import {ContentBrowseItem} from './ContentBrowseItem';
import {ContentTreeGrid} from './ContentTreeGrid';

import BrowseItem = api.app.browse.BrowseItem;
import BrowseItemsSelectionPanel = api.app.browse.BrowseItemsSelectionPanel;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import ContentSummaryAndCompareStatusViewer = api.content.ContentSummaryAndCompareStatusViewer;
import ContentSummaryViewer = api.content.ContentSummaryViewer;
import Toolbar = api.ui.toolbar.Toolbar;
import BrowseItemsChanges = api.app.browse.BrowseItemsChanges;
import ClearSelectionAction = api.ui.treegrid.actions.ClearSelectionAction;

export class ContentBrowseItemsSelectionPanel extends BrowseItemsSelectionPanel<ContentSummaryAndCompareStatus> {

    constructor(grid: ContentTreeGrid) {
        super(grid);
        this.addClass('content-browse-items-selection-panel');
    }

    getDefaultLimit() {
        return 10;
    }

    createItemViewer(item: ContentBrowseItem): ContentSummaryAndCompareStatusViewer {
        let viewer = new ContentSummaryAndCompareStatusViewer();
        viewer.setObject(item.getModel());
        return viewer;
    }

}
