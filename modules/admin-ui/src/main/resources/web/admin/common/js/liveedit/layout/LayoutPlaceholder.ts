module api.liveedit.layout {

    import LayoutComponent = api.content.page.region.LayoutComponent;
    import PageItemType = api.liveedit.PageItemType;
    import SiteModel = api.content.site.SiteModel;
    import LayoutDescriptor = api.content.page.region.LayoutDescriptor;
    import GetLayoutDescriptorsByModulesRequest = api.content.page.region.GetLayoutDescriptorsByModulesRequest;
    import LayoutDescriptorLoader = api.content.page.region.LayoutDescriptorLoader;
    import LayoutDescriptorComboBox = api.content.page.region.LayoutDescriptorComboBox;

    export class LayoutPlaceholder extends ComponentPlaceholder {

        private comboBox: api.content.page.region.LayoutDescriptorComboBox;

        private layoutComponentView: LayoutComponentView;

        constructor(layoutView: LayoutComponentView) {
            super();
            this.addClass("layout-placeholder");
            this.layoutComponentView = layoutView;

            var request = new GetLayoutDescriptorsByModulesRequest(layoutView.liveEditModel.getSiteModel().getModuleKeys());
            var loader = new LayoutDescriptorLoader(request);
            loader.setComparator(new api.content.page.DescriptorByDisplayNameComparator());
            this.comboBox = new LayoutDescriptorComboBox(loader);
            loader.load();

            this.appendChild(this.comboBox);

            this.comboBox.onOptionSelected((event: api.ui.selector.OptionSelectedEvent<LayoutDescriptor>) => {
                this.layoutComponentView.showLoadingSpinner();
                var descriptor = event.getOption().displayValue;

                var layoutComponent: LayoutComponent = this.layoutComponentView.getComponent();
                layoutComponent.setDescriptor(descriptor.getKey(), descriptor);
            });

            layoutView.liveEditModel.getSiteModel().onPropertyChanged((event: api.PropertyChangedEvent) => {
                if (event.getPropertyName() == SiteModel.PROPERTY_NAME_MODULE_CONFIGS) {
                    request.setModuleKeys(layoutView.liveEditModel.getSiteModel().getModuleKeys());
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