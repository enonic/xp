module LiveEdit.component {
    export class LayoutPlaceholder extends ComponentPlaceholder {

        private comboBox:api.content.page.layout.LayoutDescriptorComboBox;

        constructor() {
            this.setComponentType(new ComponentType(Type.LAYOUT));
            super();

            this.getEl().setData('live-edit-type', "layout");

            var request = new api.content.page.layout.GetLayoutDescriptorsByModulesRequest(siteTemplate.getModules());
            var loader = new api.content.page.layout.LayoutDescriptorLoader(request);
            this.comboBox = new api.content.page.layout.LayoutDescriptorComboBox(loader);
            loader.load();
            this.comboBox.hide();
            this.appendChild(this.comboBox);

            this.comboBox.onOptionSelected((event: api.ui.selector.OptionSelectedEvent<api.content.page.layout.LayoutDescriptor>) => {
                var componentPath = this.getComponentPath();
                var descriptor: api.content.page.Descriptor = event.getOption().displayValue;
                $liveEdit(window).trigger('pageComponentSetDescriptor.liveEdit', [descriptor, componentPath, this]);
            });
        }

        onSelect() {
            super.onSelect();
            this.comboBox.show();
            this.comboBox.giveFocus();
        }

        onDeselect() {
            super.onDeselect();
            this.comboBox.hide();
        }
    }
}