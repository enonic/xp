module api_ui_tab {

    export interface TabBarListener extends api_event.Listener {

        onStepAdded?: (step:api_ui.PanelNavigationItem) => void;

        onStepShown?: (step:api_ui.PanelNavigationItem) => void;

    }

}