module api.app.wizard {

    export class WizardStep {

        private tabBarItem: api.ui.tab.TabBarItem;

        private panel: api.ui.panel.Panel;

        constructor(label: string, panel: api.ui.panel.Panel) {
            this.tabBarItem = new api.ui.tab.TabBarItem(label, {removable: false});
            this.panel = panel;
        }

        getTabBarItem(): api.ui.tab.TabBarItem {
            return this.tabBarItem;
        }

        getPanel(): api.ui.panel.Panel {
            return this.panel;
        }

    }
}