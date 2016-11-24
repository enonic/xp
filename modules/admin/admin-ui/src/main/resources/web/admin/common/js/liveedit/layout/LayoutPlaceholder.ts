module api.liveedit.layout {

    import LayoutComponent = api.content.page.region.LayoutComponent;
    import PageItemType = api.liveedit.PageItemType;
    import SiteModel = api.content.site.SiteModel;
    import LayoutDescriptor = api.content.page.region.LayoutDescriptor;
    import GetLayoutDescriptorsByApplicationsRequest = api.content.page.region.GetLayoutDescriptorsByApplicationsRequest;
    import LayoutDescriptorLoader = api.content.page.region.LayoutDescriptorLoader;
    import LayoutDescriptorComboBox = api.content.page.region.LayoutDescriptorComboBox;
    import SelectedOptionEvent = api.ui.selector.combobox.SelectedOptionEvent;

    export class LayoutPlaceholder extends ItemViewPlaceholder {

        private comboBox: api.content.page.region.LayoutDescriptorComboBox;

        private layoutComponentView: LayoutComponentView;

        constructor(layoutView: LayoutComponentView) {
            super();
            this.addClassEx("layout-placeholder");
            this.layoutComponentView = layoutView;

            var request = new GetLayoutDescriptorsByApplicationsRequest(layoutView.getLiveEditModel().getSiteModel().getApplicationKeys());
            var loader = new LayoutDescriptorLoader(request);
            loader.setComparator(new api.content.page.DescriptorByDisplayNameComparator());
            this.comboBox = new LayoutDescriptorComboBox(loader);
            loader.load();

            this.appendChild(this.comboBox);

            this.comboBox.onOptionSelected((event: SelectedOptionEvent<LayoutDescriptor>) => {
                this.layoutComponentView.showLoadingSpinner();
                var descriptor = event.getSelectedOption().getOption().displayValue;

                var layoutComponent: LayoutComponent = this.layoutComponentView.getComponent();
                layoutComponent.setDescriptor(descriptor.getKey(), descriptor);
            });

            var siteModel = layoutView.getLiveEditModel().getSiteModel();

            let listener = () => this.reloadDescriptorsOnApplicationChange(siteModel, request);

            siteModel.onApplicationAdded(listener);
            siteModel.onApplicationRemoved(listener);

            this.onRemoved(() => {
                siteModel.unApplicationAdded(listener);
                siteModel.unApplicationRemoved(listener);
            });
        }

        private reloadDescriptorsOnApplicationChange(siteModel: SiteModel, request: GetLayoutDescriptorsByApplicationsRequest) {
            request.setApplicationKeys(siteModel.getApplicationKeys());
            this.comboBox.getLoader().load();
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