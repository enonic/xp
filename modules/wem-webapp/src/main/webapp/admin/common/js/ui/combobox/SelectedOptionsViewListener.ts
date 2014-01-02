module api.ui.combobox {

    export interface SelectedOptionsViewListener<T> extends api.event.Listener {

        onSelectedOptionRemoved:(item:Option<T>) => void;

    }

}