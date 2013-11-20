module api_ui_combobox {

    export interface ComboBoxSelectedOptionsViewListener<T> extends api_event.Listener {

        onSelectedOptionRemoved:(item:Option<T>) => void;

    }

}