module api_ui_combobox {

    export interface OptionData<T> extends Slick.SlickData {

        value:string;

        displayValue:T;

    }

}

