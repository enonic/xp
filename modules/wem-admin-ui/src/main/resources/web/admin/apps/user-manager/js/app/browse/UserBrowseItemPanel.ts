module app.browse {

    export class UserBrowseItemPanel extends api.app.browse.BrowseItemPanel<api.security.UserTreeGridItem> {

        constructor() {
            super();
        }

        createItemStatisticsPanel(): api.app.view.ItemStatisticsPanel<api.security.UserTreeGridItem> {
            return new app.view.UserItemStatisticsPanel();
        }

    }
}