module app.browse {

    export class ContentBrowseItemPanel extends api.app.browse.BrowseItemPanel<api.content.ContentSummary> {

        createItemSelectionPanel(): ContentBrowseItemsSelectionPanel {
            return new ContentBrowseItemsSelectionPanel();
        }

        createItemStatisticsPanel(): app.view.ContentItemStatisticsPanel {
            return new app.view.ContentItemStatisticsPanel();
        }

    }

}
