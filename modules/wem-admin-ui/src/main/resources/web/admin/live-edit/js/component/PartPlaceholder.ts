module LiveEdit.component {

    import ComponentPath = api.content.page.ComponentPath;
    import PageComponentSetDescriptorEvent = api.liveedit.PageComponentSetDescriptorEvent;
    import PartItemType = api.liveedit.part.PartItemType;
    import PageItemType = api.liveedit.PageItemType;

    export class PartPlaceholder extends ComponentPlaceholder {

        private comboBox: api.content.page.part.PartDescriptorComboBox;

        constructor() {
            super(PartItemType.get());

            wemjq(this.getHTMLElement()).on('click', 'input', (e) => {
                wemjq(e.currentTarget).focus();
                e.stopPropagation();
            });
            this.getEl().setData('live-edit-type', 'part');
            var request = new api.content.page.part.GetPartDescriptorsByModulesRequest(PageItemType.get().getSiteTemplate().getModules());
            var loader = new api.content.page.part.PartDescriptorLoader(request);
            this.comboBox = new api.content.page.part.PartDescriptorComboBox(loader);
            loader.load();
            this.comboBox.hide();
            this.appendChild(this.comboBox);

            this.comboBox.onOptionSelected((event: api.ui.selector.OptionSelectedEvent<api.content.page.part.PartDescriptor>) => {
                var componentPath = this.getComponentPath();
                var descriptor: api.content.page.Descriptor = event.getOption().displayValue;
                new PageComponentSetDescriptorEvent(componentPath, descriptor, this).fire();
            });

        }

        select() {
            super.select();
            this.comboBox.show();
            this.comboBox.giveFocus();
        }

        deselect() {
            super.deselect();
            this.comboBox.hide();
        }
    }
}