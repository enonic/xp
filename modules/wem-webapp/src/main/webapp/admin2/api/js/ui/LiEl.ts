module api_ui {

    export class LiEl extends api_ui.Element {

        constructor(name?:string, className?:string) {
            super("li", name, className);
        }
    }
}
