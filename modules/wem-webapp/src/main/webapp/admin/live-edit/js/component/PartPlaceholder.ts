module LiveEdit.component {
    export class PartPlaceholder extends ComponentPlaceholder {

        private comboBox: api.content.page.part.PartDescriptorComboBox;

        constructor() {
            this.setComponentType(new ComponentType(Type.PART));
            super();

            $(this.getHTMLElement()).on('click', 'input', (e) => {
                $(e.currentTarget).focus();
                e.stopPropagation();
            });
            this.getEl().setData('live-edit-type', 'part');
            var request = new api.content.page.part.GetPartDescriptorsByModulesRequest(siteTemplate.getModules());
            var loader = new api.content.page.part.PartDescriptorLoader(request);
            this.comboBox = new api.content.page.part.PartDescriptorComboBox(loader);
            this.comboBox.hide();
            this.appendChild(this.comboBox);

            this.comboBox.onOptionSelected((event: api.ui.selector.OptionSelectedEvent<api.content.page.part.PartDescriptor>) => {
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