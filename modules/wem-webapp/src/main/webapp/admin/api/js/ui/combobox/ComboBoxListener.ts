module api_ui_combobox {

    export interface ComboBoxListener extends api_event.Listener {

        onInputValueChanged?: (oldValue: string, newValue: string, grid: api_grid.Grid) => void;

    }

}