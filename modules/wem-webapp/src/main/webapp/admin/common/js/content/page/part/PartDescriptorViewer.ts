module api.content.page.part {

    export class PartDescriptorViewer extends api.ui.Viewer<PartDescriptor> {

        private namesAndIconView: api.app.NamesAndIconView;

        constructor() {
            super();
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().
                setSize(api.app.NamesAndIconViewSize.small).build();
            this.namesAndIconView.setIconUrl(api.util.getAdminUri('common/images/icons/icoMoon/32x32/puzzle.png'));
            this.appendChild(this.namesAndIconView);
        }

        setObject(partDescriptor: PartDescriptor) {
            super.setObject(partDescriptor);
            this.namesAndIconView.setMainName(partDescriptor.getDisplayName()).
                setSubName(partDescriptor.getName().toString());
        }

        getPreferredHeight(): number {
            return 50;
        }
    }
}