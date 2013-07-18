module app {

    export class ContentAppBarTabMenuItem extends api_app.AppBarTabMenuItem {

        constructor(label:string, editing?:bool) {
            super(label, editing);
        }
    }
}