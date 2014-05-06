module app.browse {

    export class ContentBrowseItemPanel extends api.app.browse.BrowseItemPanel<api.content.ContentSummary> {

        createItemStatisticsPanel(): app.view.ContentItemStatisticsPanel {
            return new app.view.ContentItemStatisticsPanel();
        }

    }

}
