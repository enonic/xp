module api_app_wizard {

    export class WizardStep {

        private tabBarItem:api_ui_tab.TabBarItem;

        private panel:api_ui.Panel;

        constructor(label:string, panel:api_ui.Panel) {
            this.tabBarItem = new api_ui_tab.TabBarItem(label, {removable: false});
            this.panel = panel;
        }

        getTabBarItem():api_ui_tab.TabBarItem {
            return this.tabBarItem;
        }

        getPanel():api_ui.Panel {
            return this.panel;
        }

    }
}