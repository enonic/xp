import '../../api.ts';
import {ApplicationItemStatisticsPanel} from '../view/ApplicationItemStatisticsPanel';

export class ApplicationBrowseItemPanel extends api.app.browse.BrowseItemPanel<api.application.Application> {

    createItemStatisticsPanel(): api.app.view.ItemStatisticsPanel<api.application.Application> {
        return new ApplicationItemStatisticsPanel();
    }

}
