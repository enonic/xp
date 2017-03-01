import '../../api.ts';
import {UserBrowseItemsSelectionPanel} from './UserBrowseItemsSelectionPanel';
import {UserTreeGridItem} from './UserTreeGridItem';
import {UserItemStatisticsPanel} from '../view/UserItemStatisticsPanel';
import {UserItemsTreeGrid} from './UserItemsTreeGrid';

export class UserBrowseItemPanel extends api.app.browse.BrowseItemPanel<UserTreeGridItem> {

    createItemSelectionPanel(grid: UserItemsTreeGrid): UserBrowseItemsSelectionPanel {
        return new UserBrowseItemsSelectionPanel(grid);
    }

    createItemStatisticsPanel(): api.app.view.ItemStatisticsPanel<UserTreeGridItem> {
        return new UserItemStatisticsPanel();
    }

}
