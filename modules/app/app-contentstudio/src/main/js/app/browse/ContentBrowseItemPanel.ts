import '../../api.ts';
import {ContentItemStatisticsPanel} from '../view/ContentItemStatisticsPanel';

export class ContentBrowseItemPanel extends api.app.browse.BrowseItemPanel<api.content.ContentSummaryAndCompareStatus> {

    createItemStatisticsPanel(): ContentItemStatisticsPanel {
        return new ContentItemStatisticsPanel();
    }

}
