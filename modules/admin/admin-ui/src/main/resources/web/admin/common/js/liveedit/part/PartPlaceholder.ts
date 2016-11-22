module api.liveedit.part {

    import Descriptor = api.content.page.Descriptor;
    import SiteModel = api.content.site.SiteModel;
    import PartComponent = api.content.page.region.PartComponent;
    import PartDescriptor = api.content.page.region.PartDescriptor;
    import PartDescriptorLoader = api.content.page.region.PartDescriptorLoader;
    import PartDescriptorComboBox = api.content.page.region.PartDescriptorComboBox;
    import GetPartDescriptorsByApplicationsRequest = api.content.page.region.GetPartDescriptorsByApplicationsRequest;
    import PartItemType = api.liveedit.part.PartItemType;
    import PageItemType = api.liveedit.PageItemType;
    import SelectedOptionEvent = api.ui.selector.combobox.SelectedOptionEvent;

    export class PartPlaceholder extends ItemViewPlaceholder {

        private comboBox: PartDescriptorComboBox;

        private displayName: api.dom.H2El;

        private partComponentView: PartComponentView;

        constructor(partView: PartComponentView) {
            super();
            this.addClassEx("part-placeholder").addClass(api.StyleHelper.getCommonIconCls("part"));

            this.partComponentView = partView;

            var request = new GetPartDescriptorsByApplicationsRequest(partView.getLiveEditModel().getSiteModel().getApplicationKeys());
            var loader = new PartDescriptorLoader(request);
            loader.setComparator(new api.content.page.DescriptorByDisplayNameComparator());
            this.comboBox = new PartDescriptorComboBox(loader);
            loader.load();

            this.appendChild(this.comboBox);

            this.comboBox.onOptionSelected((event: SelectedOptionEvent<PartDescriptor>) => {
                this.partComponentView.showLoadingSpinner();
                var descriptor: Descriptor = event.getSelectedOption().getOption().displayValue;
                var partComponent: PartComponent = this.partComponentView.getComponent();
                partComponent.setDescriptor(descriptor.getKey(), descriptor);
            });

            var siteModel = partView.getLiveEditModel().getSiteModel();

            let listener = () => this.reloadDescriptorsOnApplicationChange(siteModel, request);

            siteModel.onApplicationAdded(listener);
            siteModel.onApplicationRemoved(listener);

            this.onRemoved(() => {
                siteModel.unApplicationAdded(listener);
                siteModel.unApplicationRemoved(listener);
            });

            this.displayName = new api.dom.H3El('display-name');
            this.appendChild(this.displayName);
            var partComponent = this.partComponentView.getComponent();
            if (partComponent && partComponent.getName()) {
                this.setDisplayName(partComponent.getName().toString());
            }
        }

        private reloadDescriptorsOnApplicationChange(siteModel: SiteModel, request: GetPartDescriptorsByApplicationsRequest) {
            request.setApplicationKeys(siteModel.getApplicationKeys());
            this.comboBox.getLoader().load();
        }

        setDisplayName(name: string) {
            this.displayName.setHtml(name);
        }

        select() {
            this.comboBox.show();
            this.comboBox.giveFocus();
        }

        deselect() {
            this.comboBox.hide();
        }
    }
}