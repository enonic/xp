module api_ui {

    export class DivEl extends api_ui.Element {

        constructor(name?:string, className?:string) {
            super("div", name, className);
        }
    }
}
