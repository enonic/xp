module app.browse {

    export class ContentBrowseItemPanel extends api.app.browse.BrowseItemPanel<api.content.ContentSummaryAndCompareStatus> {

        createItemSelectionPanel(): ContentBrowseItemsSelectionPanel {
            return new ContentBrowseItemsSelectionPanel();
        }

        createItemStatisticsPanel(): app.view.ContentItemStatisticsPanel {
            return new app.view.ContentItemStatisticsPanel();
        }

        setItems(items: ContentBrowseItem[]): api.app.browse.BrowseItemsChanges {
            return super.setItems(items);
        }

        getItems(): ContentBrowseItem[] {
            return <ContentBrowseItem[]>super.getItems();
        }

    }

}
