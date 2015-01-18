module api.liveedit.layout {

    import ComponentSetDescriptorEvent = api.liveedit.ComponentSetDescriptorEvent;
    import LayoutItemType = api.liveedit.layout.LayoutItemType;
    import PageItemType = api.liveedit.PageItemType;
    import SiteModel = api.content.site.SiteModel;

    export class LayoutPlaceholder extends ComponentPlaceholder {

        private comboBox: api.content.page.region.LayoutDescriptorComboBox;

        private layoutComponentView: LayoutComponentView;

        constructor(layoutView: LayoutComponentView) {
            super();
            this.layoutComponentView = layoutView;

            this.onClicked((event: MouseEvent) => {
                event.stopPropagation();
            });
            var request = new api.content.page.region.GetLayoutDescriptorsByModulesRequest(layoutView.liveEditModel.getSiteModel().getModuleKeys());
            var loader = new api.content.page.region.LayoutDescriptorLoader(request);
            this.comboBox = new api.content.page.region.LayoutDescriptorComboBox(loader);
            loader.load();
            this.comboBox.hide();
            this.appendChild(this.comboBox);

            this.comboBox.onOptionSelected((event: api.ui.selector.OptionSelectedEvent<api.content.page.region.LayoutDescriptor>) => {
                this.layoutComponentView.showLoadingSpinner();
                var descriptor: api.content.page.Descriptor = event.getOption().displayValue;
                new ComponentSetDescriptorEvent(descriptor, layoutView).fire();
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