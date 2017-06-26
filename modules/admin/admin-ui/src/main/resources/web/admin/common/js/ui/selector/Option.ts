module api.ui.selector {

    export interface Option<T> extends Slick.SlickData {

        value:string;

        displayValue:T;

        indices?: string[];

        readOnly?: boolean;

        empty? :boolean;

        disabled?: boolean;
    }
}
