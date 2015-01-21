module api.app.wizard {

    export class WizardStep {

        private tabBarItem: api.ui.tab.TabBarItem;

        private stepForm: WizardStepForm;

        constructor(label: string, stepForm: WizardStepForm) {
            this.tabBarItem = new api.ui.tab.TabBarItemBuilder().setLabel(label).build();
            this.stepForm = stepForm;
        }

        getTabBarItem(): api.ui.tab.TabBarItem {
            return this.tabBarItem;
        }

        getStepForm(): WizardStepForm {
            return this.stepForm;
        }

    }
}