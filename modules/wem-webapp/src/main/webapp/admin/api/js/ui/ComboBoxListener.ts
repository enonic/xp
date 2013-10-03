module api_ui {

    export interface ComboBoxListener extends api_event.Listener {

        onInputValueChanged?: (oldValue: string, newValue: string, grid: api_grid.Grid) => void;

    }

}