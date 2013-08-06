module api_app_wizard {

    export class WizardStepDeckPanel extends api_ui.DeckPanel {
        constructor() {
            super("WizardStepDeckPanel");
            this.addClass("step-panel");
            //this.removeClass("panel");
        }

        afterRender() {
            super.afterRender();
        }
    }
}