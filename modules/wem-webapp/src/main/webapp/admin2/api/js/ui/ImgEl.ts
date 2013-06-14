module api_ui {

    export class ImgEl extends api_ui.Element {

        constructor(src:string, idPrefix?:string, className?:string) {
            super("img", idPrefix, className, api_ui.ImgHelper.create());
            this.getEl().setSrc(src);
        }

        getEl():api_ui.ImgHelper {
            return <api_ui.ImgHelper>super.getEl();
        }
    }
}
