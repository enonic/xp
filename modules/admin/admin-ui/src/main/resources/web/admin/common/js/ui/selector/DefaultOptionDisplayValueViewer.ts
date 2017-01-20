module api.ui.selector {

    export class DefaultOptionDisplayValueViewer extends api.ui.Viewer<any> {

        constructor() {
            super();
        }

        setObject(object: any) {
            this.getEl().setInnerHtml(object.toString());

            return super.setObject(object);
        }

        getPreferredHeight(): number {
            return 34;
        }
    }

}
