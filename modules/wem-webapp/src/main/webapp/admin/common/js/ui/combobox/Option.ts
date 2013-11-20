module api_ui_combobox {

    export interface Option<T> extends Slick.SlickData {

        value:string;

        displayValue:T;

    }

}

