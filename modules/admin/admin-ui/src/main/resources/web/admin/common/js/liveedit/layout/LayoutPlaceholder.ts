module api.liveedit.layout {

    import LayoutComponent = api.content.page.region.LayoutComponent;
    import PageItemType = api.liveedit.PageItemType;
    import SiteModel = api.content.site.SiteModel;
    import LayoutDescriptor = api.content.page.region.LayoutDescriptor;
    import LayoutDescriptorComboBox = api.content.page.region.LayoutDescriptorComboBox;
    import SelectedOptionEvent = api.ui.selector.combobox.SelectedOptionEvent;

    export class LayoutPlaceholder extends ItemViewPlaceholder {

        private comboBox: api.content.page.region.LayoutDescriptorComboBox;

        private layoutComponentView: LayoutComponentView;

        constructor(layoutView: LayoutComponentView) {
            super();
            this.addClassEx("layout-placeholder");
            this.layoutComponentView = layoutView;

            this.comboBox = new LayoutDescriptorComboBox();
            this.comboBox.loadDescriptors(layoutView.getLiveEditModel().getSiteModel().getApplicationKeys());

            this.appendChild(this.comboBox);

            this.comboBox.onOptionSelected((event: SelectedOptionEvent<LayoutDescriptor>) => {
                this.layoutComponentView.showLoadingSpinner();
                let descriptor = event.getSelectedOption().getOption().displayValue;

                let layoutComponent: LayoutComponent = this.layoutComponentView.getComponent();
                layoutComponent.setDescriptor(descriptor.getKey(), descriptor);
            });

            let siteModel = layoutView.getLiveEditModel().getSiteModel();

            let listener = () => this.reloadDescriptorsOnApplicationChange(siteModel);

            siteModel.onApplicationAdded(listener);
            siteModel.onApplicationRemoved(listener);

            this.onRemoved(() => {
                siteModel.unApplicationAdded(listener);
                siteModel.unApplicationRemoved(listener);
            });
        }

        private reloadDescriptorsOnApplicationChange(siteModel: SiteModel) {
            this.comboBox.loadDescriptors(siteModel.getApplicationKeys());
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
