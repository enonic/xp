module api_ui_tab {

    export interface TabMenuItemListener extends api_event.Listener {

        onSelected?: (tab:TabMenuItem) => void;

        onClose?: (tab:TabMenuItem) => void;

        onLabelChanged?: (newValue:string, oldValue:string) => void;

    }

}