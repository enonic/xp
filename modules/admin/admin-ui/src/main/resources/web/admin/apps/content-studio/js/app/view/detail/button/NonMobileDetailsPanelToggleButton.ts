import "../../../../api.ts";

export class NonMobileDetailsPanelToggleButton extends api.dom.DivEl {

    constructor() {
        super('button', api.StyleHelper.COMMON_PREFIX);
        this.addClass('non-mobile-details-panel-toggle-button');

        this.onClicked(() => {
            this.toggleClass('expanded');
        });
    }
}
