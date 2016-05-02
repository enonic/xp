import "../../api.ts";

import BrowseItem = api.app.browse.BrowseItem;
import {UserTreeGridItemViewer} from "./UserTreeGridItemViewer";
import {UserTreeGridItem} from "./UserTreeGridItem";

export class UserBrowseItemsSelectionPanel extends api.app.browse.BrowseItemsSelectionPanel<UserTreeGridItem> {

    createItemViewer(item: BrowseItem<UserTreeGridItem>): UserTreeGridItemViewer {
        var viewer = new UserTreeGridItemViewer();
        viewer.setObject(item.getModel());
        return viewer;
    }
}
