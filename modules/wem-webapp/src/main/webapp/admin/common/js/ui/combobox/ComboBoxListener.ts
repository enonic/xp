module api_ui_combobox {

    export interface ComboBoxListener<T> extends api_event.Listener {

        onInputValueChanged: (oldValue: string, newValue: string, grid: api_ui_grid.Grid<Option<T>>) => void;

        onOptionSelected: (item:Option<T>) => void;


    }

}