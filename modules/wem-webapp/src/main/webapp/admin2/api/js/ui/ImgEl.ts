module api_ui {

    export class ImgEl extends api_ui.AbstractEl {

        private el:HTMLElementHelper;

        constructor(name:string) {
            super( name, "img" );
            this.el = HTMLImageElementHelper.create();
        }

        getImg():HTMLImageElementHelper {
            return <HTMLImageElementHelper>this.el;
        }
    }
}
