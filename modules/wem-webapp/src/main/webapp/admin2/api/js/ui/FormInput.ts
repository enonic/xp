module api_ui {
    export interface FormInput {
        getValue():string;
        getName():string;
        setValue(value:string);
    }
}