module api_ui_combobox {

    export interface ComboBoxConfig<T> {

        iconUrl?: string;

        rowHeight?: number;

        optionFormatter?: (row:number, cell:number, value:T, columnDef:any, dataContext:Slick.SlickData) => string;

        selectedOptionsView: ComboBoxSelectedOptionsView<T>;

        maximumOccurrences?: number;

        filter?: (item:any, args:any) => boolean;

    }

}