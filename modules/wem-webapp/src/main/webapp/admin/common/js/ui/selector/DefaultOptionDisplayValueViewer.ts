module api.ui.selector {

    export class DefaultOptionDisplayValueViewer extends api.ui.Viewer<any> {

        constructor() {
            super();
        }

        setObject(object: any) {
            super.setObject(object);
            this.getEl().setInnerHtml(object.toString());
        }

        getPreferredHeight(): number {
            return 34;
        }
    }

}