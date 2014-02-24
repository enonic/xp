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
            this.comboBox.hide();
            this.appendChild(this.comboBox);

            this.comboBox.addOptionSelectedListener((item) => {
                var componentPath = this.getComponentPath();
                //$liveEdit(window).trigger('partComponentSetDescriptor.liveEdit', [item.value, componentPath, this]);
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