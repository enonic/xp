import '../../api.ts';
import {UserBrowseItemsSelectionPanel} from './UserBrowseItemsSelectionPanel';
import {UserTreeGridItem} from './UserTreeGridItem';
import {UserItemStatisticsPanel} from '../view/UserItemStatisticsPanel';

export class UserBrowseItemPanel extends api.app.browse.BrowseItemPanel<UserTreeGridItem> {

    createItemSelectionPanel(): UserBrowseItemsSelectionPanel {
        return new UserBrowseItemsSelectionPanel();
    }

    createItemStatisticsPanel(): api.app.view.ItemStatisticsPanel<UserTreeGridItem> {
        return new UserItemStatisticsPanel();
    }

}
