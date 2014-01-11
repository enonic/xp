module api.dom {

    export class ImgEl extends Element {

        /* 1px x 1px gif with a 1bit palette */
        static PLACEHOLDER = "data:image/gif;base64,R0lGODlhAQABAIAAAP///////yH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==";

        constructor(src?:string, generateId?:boolean, className?:string) {
            super(new ElementProperties().setTagName("img").setGenerateId(generateId).setClassName(className).setHelper(ImgHelper.create()));
            this.getEl().setSrc(src ? src : ImgEl.PLACEHOLDER);
        }

        getEl():ImgHelper {
            return <ImgHelper>super.getEl();
        }
    }
}
