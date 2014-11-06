module app.browse {

    export class UserBrowseItemPanel extends api.app.browse.BrowseItemPanel<app.browse.UserTreeGridItem> {

        constructor() {
            super();
        }

        createItemStatisticsPanel(): api.app.view.ItemStatisticsPanel<app.browse.UserTreeGridItem> {
            return new app.view.UserItemStatisticsPanel();
        }

    }
}