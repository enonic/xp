module api_app_wizard {

    export class WizardStep extends api_ui_tab.TabBarItem {

        constructor(label:string) {
            super(label, {removable: false});
        }

    }
}