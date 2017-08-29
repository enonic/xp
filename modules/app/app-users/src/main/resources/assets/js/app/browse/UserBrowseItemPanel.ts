import '../../api.ts';
import {UserTreeGridItem} from './UserTreeGridItem';
import {UserItemStatisticsPanel} from '../view/UserItemStatisticsPanel';

export class UserBrowseItemPanel extends api.app.browse.BrowseItemPanel<UserTreeGridItem> {

    createItemStatisticsPanel(): api.app.view.ItemStatisticsPanel<UserTreeGridItem> {
        return new UserItemStatisticsPanel();
    }

}
