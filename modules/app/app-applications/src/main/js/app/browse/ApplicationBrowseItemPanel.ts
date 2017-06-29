import '../../api.ts';
import {ApplicationItemStatisticsPanel} from '../view/ApplicationItemStatisticsPanel';
import BrowseItem = api.app.browse.BrowseItem;

export class ApplicationBrowseItemPanel extends api.app.browse.BrowseItemPanel<api.application.Application> {

    constructor() {
        super();
        this.addClass('application-browse-item-panel');
        this.createBackButton();
    }

    private createBackButton() {
        let backButton = new api.dom.DivEl('application-item-statistics-panel-back-button');
        backButton.onClicked((event) => {
            this.addClass('hidden');
        });

        this.itemStatisticsPanel.appendChild(backButton);
    }

    togglePreviewForItem(item?: BrowseItem<api.application.Application>) {
        super.togglePreviewForItem(item);

        if (item) {
            this.removeClass('hidden');
        }
    }

    createItemStatisticsPanel(): api.app.view.ItemStatisticsPanel<api.application.Application> {
        return new ApplicationItemStatisticsPanel();
    }

}
