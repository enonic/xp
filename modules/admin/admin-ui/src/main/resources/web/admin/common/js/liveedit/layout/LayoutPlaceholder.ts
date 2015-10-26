module api.liveedit.layout {

    import LayoutComponent = api.content.page.region.LayoutComponent;
    import PageItemType = api.liveedit.PageItemType;
    import SiteModel = api.content.site.SiteModel;
    import LayoutDescriptor = api.content.page.region.LayoutDescriptor;
    import GetLayoutDescriptorsByApplicationsRequest = api.content.page.region.GetLayoutDescriptorsByApplicationsRequest;
    import LayoutDescriptorLoader = api.content.page.region.LayoutDescriptorLoader;
    import LayoutDescriptorComboBox = api.content.page.region.LayoutDescriptorComboBox;

    export class LayoutPlaceholder extends ItemViewPlaceholder {

        private comboBox: api.content.page.region.LayoutDescriptorComboBox;

        private layoutComponentView: LayoutComponentView;

        constructor(layoutView: LayoutComponentView) {
            super();
            this.addClassEx("layout-placeholder");
            this.layoutComponentView = layoutView;

            var request = new GetLayoutDescriptorsByApplicationsRequest(layoutView.liveEditModel.getSiteModel().getApplicationKeys());
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
                if (event.getPropertyName() == SiteModel.PROPERTY_NAME_SITE_CONFIGS) {
                    request.setApplicationKeys(layoutView.liveEditModel.getSiteModel().getApplicationKeys());
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