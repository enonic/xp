module api.content.page.image {

    export class ImageDescriptorViewer extends api.ui.Viewer<ImageDescriptor> {

        private namesAndIconView: api.app.NamesAndIconView;

        constructor() {
            super();
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            this.namesAndIconView.setIconClass("icon-image3 icon-large");
            this.appendChild(this.namesAndIconView);
        }

        setObject(descriptor: ImageDescriptor) {

            super.setObject(descriptor);

            this.namesAndIconView.setMainName(descriptor.getDisplayName());
            this.namesAndIconView.setSubName(descriptor.getName().toString());
        }

        getPreferredHeight(): number {
            return 50;
        }
    }
}