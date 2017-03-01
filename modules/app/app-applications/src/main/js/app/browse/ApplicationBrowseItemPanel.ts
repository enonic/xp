import '../../api.ts';
import {ApplicationBrowseItemsSelectionPanel} from './ApplicationBrowseItemsSelectionPanel';
import {ApplicationItemStatisticsPanel} from '../view/ApplicationItemStatisticsPanel';
import {ApplicationTreeGrid} from './ApplicationTreeGrid';

export class ApplicationBrowseItemPanel extends api.app.browse.BrowseItemPanel<api.application.Application> {

    createItemSelectionPanel(grid: ApplicationTreeGrid): ApplicationBrowseItemsSelectionPanel {
        return new ApplicationBrowseItemsSelectionPanel(grid);
    }

    createItemStatisticsPanel(): api.app.view.ItemStatisticsPanel<api.application.Application> {
        return new ApplicationItemStatisticsPanel();
    }

}
