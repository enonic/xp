module LiveEdit.component {
    export class PartPlaceholder extends ComponentPlaceholder {

        private comboBox:api.content.page.part.PartDescriptorComboBox;

        constructor() {
            this.setComponentType(new ComponentType(Type.PART));
            super();

            $(this.getHTMLElement()).on('click', 'input', (e) => {
                $(e.currentTarget).focus();
                e.stopPropagation();
            });
            this.getEl().setData('live-edit-type', 'part');
            var request = new api.content.page.part.GetPartDescriptorsByModulesRequest(null);
            var loader = new api.content.page.part.PartDescriptorLoader(request);
            this.comboBox = new api.content.page.part.PartDescriptorComboBox(loader);
            this.comboBox.hide();
            this.appendChild(this.comboBox);

            this.comboBox.addOptionSelectedListener((item) => {
                var componentPath = this.getComponentPath();
                $liveEdit(window).trigger('partComponentSetDescriptor.liveEdit', [item.value, componentPath, this]);
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