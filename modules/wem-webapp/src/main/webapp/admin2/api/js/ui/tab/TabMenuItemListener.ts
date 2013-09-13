module api_ui_tab {

    export interface TabMenuItemListener extends api_event.Listener {

        onLabelChanged?: (newValue:string, oldValue:string) => void;

    }

}