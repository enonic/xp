module api.ui.tab {

    export interface TabMenuItemListener extends api.event.Listener {

        onSelected?: (tab:TabMenuItem) => void;

        onClose?: (tab:TabMenuItem) => void;

        onLabelChanged?: (newValue:string, oldValue:string) => void;

    }

}