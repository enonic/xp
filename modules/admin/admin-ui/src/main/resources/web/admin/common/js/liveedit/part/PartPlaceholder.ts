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

    export class PartPlaceholder extends ItemViewPlaceholder {

        private comboBox: PartDescriptorComboBox;

        private displayName: api.dom.H2El;

        private partComponentView: PartComponentView;

        constructor(partView: PartComponentView) {
            super();
            this.addClassEx("part-placeholder");

            this.partComponentView = partView;

            var request = new GetPartDescriptorsByApplicationsRequest(partView.liveEditModel.getSiteModel().getApplicationKeys());
            var loader = new PartDescriptorLoader(request);
            loader.setComparator(new api.content.page.DescriptorByDisplayNameComparator());
            this.comboBox = new PartDescriptorComboBox(loader);
            loader.load();

            this.appendChild(this.comboBox);

            this.comboBox.onOptionSelected((selectedOption: api.ui.selector.combobox.SelectedOption<PartDescriptor>) => {
                this.partComponentView.showLoadingSpinner();
                var descriptor: Descriptor = selectedOption.getOption().displayValue;
                var partComponent: PartComponent = this.partComponentView.getComponent();
                partComponent.setDescriptor(descriptor.getKey(), descriptor);
            });

            partView.liveEditModel.getSiteModel().onPropertyChanged((event: api.PropertyChangedEvent) => {
                if (event.getPropertyName() == SiteModel.PROPERTY_NAME_SITE_CONFIGS) {
                    request.setApplicationKeys(partView.liveEditModel.getSiteModel().getApplicationKeys());
                    loader.load();
                }
            });

            this.displayName = new api.dom.H3El('display-name');
            this.appendChild(this.displayName);
            var partComponent = this.partComponentView.getComponent();
            if (partComponent && partComponent.getName()) {
                this.setDisplayName(partComponent.getName().toString());
            }
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