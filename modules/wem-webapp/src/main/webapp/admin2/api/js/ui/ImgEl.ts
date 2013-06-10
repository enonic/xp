module api_ui {

    export class ImgEl extends api_ui.Element {

        constructor(name?:string) {
            super( "img", name, HTMLImageElementHelper.create() );
        }

        getEl():HTMLImageElementHelper {
            return <HTMLImageElementHelper>super.getEl();
        }
    }
}
