module api_ui {

    export class ImgEl extends api_ui.Element {

        constructor(name?:string, className?:string) {
            super("img", name, className, api_ui.ImgHelper.create());
        }

        getEl():api_ui.ImgHelper {
            return <api_ui.ImgHelper>super.getEl();
        }
    }
}
