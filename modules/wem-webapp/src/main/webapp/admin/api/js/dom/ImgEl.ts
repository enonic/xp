module api_dom {

    export class ImgEl extends Element {

        /* 1px x 1px gif with a 1bit palette */
        static PLACEHOLDER = "data:image/gif;base64,R0lGODlhAQABAIAAAP///////yH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==";

        constructor(src?:string, idPrefix?:string, className?:string) {
            super("img", idPrefix, className, ImgHelper.create());
            this.getEl().setSrc(src ? src : ImgEl.PLACEHOLDER);
        }

        getEl():ImgHelper {
            return <ImgHelper>super.getEl();
        }
    }
}
