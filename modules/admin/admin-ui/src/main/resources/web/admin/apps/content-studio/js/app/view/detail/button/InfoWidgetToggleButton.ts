import '../../../../api.ts';
import {DetailsView} from '../DetailsView';

export class InfoWidgetToggleButton extends api.dom.DivEl {

    constructor(detailsView: DetailsView) {
        super('info-widget-toggle-button');

        this.onClicked((event) => {
            this.setActive();
            detailsView.activateDefaultWidget();
        });
    }

    setActive() {
        this.addClass('active');
    }

    setInactive() {
        this.removeClass('active');
    }
}
