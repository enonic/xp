module api_ui {

    export class ButtonEl extends api_ui.Element {

        constructor(name?:string, className?:string) {
            super("button", name, className);
        }
    }
}
