module api.ui.selector.combobox {

    export interface Option<T> extends Slick.SlickData {

        value:string;

        displayValue:T;

    }

}

