import "../../api.ts";

import Panel = api.ui.panel.Panel;
import TabMenuItemBuilder = api.ui.tab.TabMenuItemBuilder;

import {ContentItemPreviewPanel} from "./ContentItemPreviewPanel";

export class ContentItemStatisticsPanel extends api.app.view.ItemStatisticsPanel<api.content.ContentSummaryAndCompareStatus> {

    private previewPanel: ContentItemPreviewPanel;

    constructor() {
        super("content-item-statistics-panel");

        this.previewPanel = new ContentItemPreviewPanel();
        this.previewPanel.setDoOffset(false);
        this.appendChild(this.previewPanel);
    }

    setItem(item: api.app.view.ViewItem<api.content.ContentSummaryAndCompareStatus>) {
        if (this.getItem() != item) {
            super.setItem(item);
            this.previewPanel.setItem(item);
        }
    }

}
