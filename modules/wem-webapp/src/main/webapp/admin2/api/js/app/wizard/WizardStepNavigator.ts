module api_app_wizard {

    export class WizardStepNavigator extends api_ui_tab.TabBar {

        constructor() {
            super("WizardStepNavigator", "step-navigator");
        }

        addNavigationItem(step:api_ui_tab.TabBarItem) {
            super.addNavigationItem(step);

            if (this.getSize() == 1) {
                step.addClass("first");
                this.selectNavigationItem(0);
            }
        }

        nextStep() {
            var nextIndex = Math.min( this.getSelectedIndex() + 1, this.getSize() - 1 );
            this.selectNavigationItem(nextIndex);
        }

        previousStep() {
            var previousIndex = Math.max( this.getSelectedIndex() - 1, 0 );
            this.selectNavigationItem(previousIndex);
        }

        hasNext():boolean {
            return this.getSelectedIndex() < this.getSize() - 1;
        }

        hasPrevious():boolean {
            return this.getSelectedIndex() > 0;
        }

    }
}