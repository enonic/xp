module api.liveedit.part {

    import Descriptor = api.content.page.Descriptor;
    import SiteModel = api.content.site.SiteModel;
    import PartDescriptor = api.content.page.region.PartDescriptor;
    import PartDescriptorLoader = api.content.page.region.PartDescriptorLoader;
    import PartDescriptorComboBox = api.content.page.region.PartDescriptorComboBox;
    import GetPartDescriptorsByModulesRequest = api.content.page.region.GetPartDescriptorsByModulesRequest;
    import ComponentSetDescriptorEvent = api.liveedit.ComponentSetDescriptorEvent;
    import PartItemType = api.liveedit.part.PartItemType;
    import PageItemType = api.liveedit.PageItemType;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;

    export class PartPlaceholder extends ComponentPlaceholder {

        private comboBox: PartDescriptorComboBox;

        private partComponentView: PartComponentView;

        constructor(partView: PartComponentView) {
            super();
            this.partComponentView = partView;

            this.onClicked((event: MouseEvent) => {
                event.stopPropagation();
            });
            var request = new GetPartDescriptorsByModulesRequest(partView.liveEditModel.getSiteModel().getModuleKeys());
            var loader = new PartDescriptorLoader(request);
            loader.setComparator(new api.content.page.DescriptorByDisplayNameComparator());
            this.comboBox = new PartDescriptorComboBox(loader);
            loader.load();
            this.comboBox.hide();
            this.appendChild(this.comboBox);

            this.comboBox.onOptionSelected((event: OptionSelectedEvent<PartDescriptor>) => {
                this.partComponentView.showLoadingSpinner();
                var descriptor: Descriptor = event.getOption().displayValue;
                new ComponentSetDescriptorEvent(descriptor, partView).fire();
            });

            partView.liveEditModel.getSiteModel().onPropertyChanged((event: api.PropertyChangedEvent) => {
                if (event.getPropertyName() == SiteModel.PROPERTY_NAME_MODULE_CONFIGS) {
                    request.setModuleKeys(partView.liveEditModel.getSiteModel().getModuleKeys());
                    loader.load();
                }
            });
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