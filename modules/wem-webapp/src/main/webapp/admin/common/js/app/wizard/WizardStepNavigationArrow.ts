module api_app_wizard {

    export class WizardStepNavigationArrow extends api_dom.DivEl {
        static NEXT = "next";
        static PREVIOUS = "prev";

        private navigator:WizardStepNavigator;
        private direction:string;

        constructor(direction:string, navigator:WizardStepNavigator) {
            super();
            this.navigator = navigator;
            this.direction = direction;

            this.getEl().addClass("navigation-arrow");
            this.getEl().addClass(this.direction);
            this.getEl().addEventListener("click", (e) => {
                (this.direction == WizardStepNavigationArrow.NEXT) ? this.navigator.nextStep() : this.navigator.previousStep();
            });
            this.update();

            this.navigator.addListener({
                onNavigationItemAdded: (step:api_ui.PanelNavigationItem) => {
                    this.update();
                },
                onNavigationItemSelected: (step:api_ui.PanelNavigationItem) => {
                    this.update();
                }
            });
        }

        private update() {
            var show = (this.direction == WizardStepNavigationArrow.NEXT) ? this.navigator.hasNext() : this.navigator.hasPrevious();
            show ? this.show() : this.hide();
        }
    }
}