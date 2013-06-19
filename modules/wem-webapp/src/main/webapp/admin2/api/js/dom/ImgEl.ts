module api_dom {

    export class ImgEl extends Element {

        constructor(src:string, idPrefix?:string, className?:string) {
            super("img", idPrefix, className, ImgHelper.create());
            this.getEl().setSrc(src);
        }

        getEl():ImgHelper {
            return <ImgHelper>super.getEl();
        }
    }
}
