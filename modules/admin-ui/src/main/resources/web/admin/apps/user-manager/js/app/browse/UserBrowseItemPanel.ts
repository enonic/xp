module app.browse {

    export class UserBrowseItemPanel extends api.app.browse.BrowseItemPanel<app.browse.UserTreeGridItem> {

        createItemSelectionPanel(): UserBrowseItemsSelectionPanel {
            return new UserBrowseItemsSelectionPanel();
        }

        createItemStatisticsPanel(): api.app.view.ItemStatisticsPanel<app.browse.UserTreeGridItem> {
            return new app.view.UserItemStatisticsPanel();
        }

    }
}