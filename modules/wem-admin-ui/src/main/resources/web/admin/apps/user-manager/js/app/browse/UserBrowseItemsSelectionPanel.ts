module app.browse {

    import BrowseItem = api.app.browse.BrowseItem;

    export class UserBrowseItemsSelectionPanel extends api.app.browse.BrowseItemsSelectionPanel<UserTreeGridItem> {

        createItemViewer(item: BrowseItem<UserTreeGridItem>): UserTreeGridItemViewer  {
            var viewer = new UserTreeGridItemViewer();
            viewer.setObject(item.getModel());
            return viewer;
        }
    }
}