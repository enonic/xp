module api_ui_combobox {

    export interface ComboBoxConfig<T> {

        iconUrl?: string;

        rowHeight?: number;

        optionFormatter?: (row:number, cell:number, value:T, columnDef:any, dataContext:Slick.SlickData) => string;

        selectedOptionFormatter?: (value:T) => string;

        maximumOccurrences?: number;

        filter?: (item:any, args:any) => boolean;

    }

}