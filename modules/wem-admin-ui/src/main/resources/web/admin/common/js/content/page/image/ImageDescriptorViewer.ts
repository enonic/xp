module api.content.page.image {

    export class ImageDescriptorViewer extends api.ui.Viewer<ImageDescriptor> {

        private namesAndIconView: api.app.NamesAndIconView;

        constructor() {
            super();
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            this.namesAndIconView.setIconUrl(api.util.getAdminUri('common/images/icons/icoMoon/32x32/pictures.png'));
            // TODO: this.namesAndIconView.setIconClass("icon-image");
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