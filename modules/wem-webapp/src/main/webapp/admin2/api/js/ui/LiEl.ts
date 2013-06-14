module api_ui {

    export class LiEl extends api_ui.Element {

        constructor(idPrefix?:string, className?:string) {
            super("li", idPrefix, className);
        }
    }
}
