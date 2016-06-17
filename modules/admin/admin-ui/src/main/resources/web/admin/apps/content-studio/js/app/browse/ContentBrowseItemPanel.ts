import "../../api.ts";
import {ContentBrowseItem} from "./ContentBrowseItem";
import {ContentBrowseItemsSelectionPanel} from "./ContentBrowseItemsSelectionPanel";
import {ContentItemStatisticsPanel} from "../view/ContentItemStatisticsPanel";
import {ContentTreeGrid} from "./ContentTreeGrid";

export class ContentBrowseItemPanel extends api.app.browse.BrowseItemPanel<api.content.ContentSummaryAndCompareStatus> {

    private grid: ContentTreeGrid;

    constructor(grid: ContentTreeGrid) {
        this.grid = grid;
        super();
    }

    createItemSelectionPanel(): ContentBrowseItemsSelectionPanel {
        return new ContentBrowseItemsSelectionPanel(this.grid);
    }

    createItemStatisticsPanel(): ContentItemStatisticsPanel {
        return new ContentItemStatisticsPanel();
    }

    setItems(items: ContentBrowseItem[]): api.app.browse.BrowseItemsChanges<api.content.ContentSummaryAndCompareStatus> {
        return super.setItems(items);
    }

    getItems(): ContentBrowseItem[] {
        return <ContentBrowseItem[]>super.getItems();
    }

}
