import '../../../../api.ts';

export class NonMobileDetailsPanelToggleButton extends api.dom.ButtonEl {

    constructor() {
        super();
        this.addClass('non-mobile-details-panel-toggle-button');

        this.onClicked(() => {
            this.toggleClass('expanded');
        });
    }
}
