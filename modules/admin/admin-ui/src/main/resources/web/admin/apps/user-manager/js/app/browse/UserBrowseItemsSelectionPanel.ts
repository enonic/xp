import '../../api.ts';
import {UserTreeGridItemViewer} from './UserTreeGridItemViewer';
import {UserTreeGridItem} from './UserTreeGridItem';

import BrowseItem = api.app.browse.BrowseItem;

export class UserBrowseItemsSelectionPanel extends api.app.browse.BrowseItemsSelectionPanel<UserTreeGridItem> {

    createItemViewer(item: BrowseItem<UserTreeGridItem>): UserTreeGridItemViewer {
        let viewer = new UserTreeGridItemViewer();
        viewer.setObject(item.getModel());
        return viewer;
    }
}
