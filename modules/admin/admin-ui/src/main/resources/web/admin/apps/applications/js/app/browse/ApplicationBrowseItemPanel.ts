import '../../api.ts';
import {ApplicationBrowseItemsSelectionPanel} from './ApplicationBrowseItemsSelectionPanel';
import {ApplicationItemStatisticsPanel} from '../view/ApplicationItemStatisticsPanel';

export class ApplicationBrowseItemPanel extends api.app.browse.BrowseItemPanel<api.application.Application> {

    createItemSelectionPanel(): ApplicationBrowseItemsSelectionPanel {
        return new ApplicationBrowseItemsSelectionPanel();
    }

    createItemStatisticsPanel(): api.app.view.ItemStatisticsPanel<api.application.Application> {
        return new ApplicationItemStatisticsPanel();
    }

}
