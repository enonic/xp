module api_ui_combobox {

    export interface ComboBoxConfig {

        iconUrl?: string;

        rowHeight?: number;

        optionFormatter?: (row:number, cell:number, value:any, columnDef:any, dataContext:Slick.SlickData) => string;

        selectedOptionFormatter?: (value:any) => string;

        maximumOccurrences?: number;

        filter?: (item:any, args:any) => boolean;

    }

}