module api_app_wizard {

    export class WizardStepNavigationArrow extends api_dom.DivEl {
        static NEXT = "next";
        static PREVIOUS = "prev";

        private navigator;
        private direction:string;

        constructor(direction:string, navigator:WizardStepNavigator) {
            super();
            this.navigator = navigator;
            this.direction = direction;

            this.getEl().addClass("navigation-arrow");
            this.getEl().addClass(this.direction);
            this.getEl().addEventListener("click", (e) => {
                if (this.direction == WizardStepNavigationArrow.NEXT) {
                    this.navigator.nextStep();
                } else {
                    this.navigator.previousStep();
                }
            });
            this.update();

            WizardStepEvent.on((event) => {
                this.update();
            })
        }

        private update() {
            var show;
            if (this.direction == WizardStepNavigationArrow.NEXT) {
                show = this.navigator.hasNext();
            } else if (this.direction == WizardStepNavigationArrow.PREVIOUS) {
                show = this.navigator.hasPrevious();
            }
            if (show) {
                this.show();
            } else {
                this.hide();
            }
        }
    }
}