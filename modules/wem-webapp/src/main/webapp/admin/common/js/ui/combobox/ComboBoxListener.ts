module api.ui.combobox {

    export interface ComboBoxListener<T> extends api.event.Listener {

        onInputValueChanged: (oldValue: string, newValue: string, grid: api.ui.grid.Grid<Option<T>>) => void;

        onOptionSelected: (item:Option<T>) => void;


    }

}