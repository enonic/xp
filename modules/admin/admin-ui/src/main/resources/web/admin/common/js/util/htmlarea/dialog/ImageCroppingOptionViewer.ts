module api.util.htmlarea.dialog {

    export class ImageCroppingOptionViewer extends api.ui.Viewer<ImageCroppingOption> {

        private nameView: ImageCroppingNameView;

        constructor() {
            super();

            this.nameView = new ImageCroppingNameView(false);
            this.appendChild(this.nameView);
        }

        setObject(object: ImageCroppingOption) {
            super.setObject(object);

            this.nameView.setMainName(object.getDisplayValue());
        }

        getPreferredHeight(): number {
            return 26;
        }
    }
}