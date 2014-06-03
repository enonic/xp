module api.liveedit.part {

    import ComponentPath = api.content.page.ComponentPath;
    import PageComponentSetDescriptorEvent = api.liveedit.PageComponentSetDescriptorEvent;
    import PartItemType = api.liveedit.part.PartItemType;
    import PageItemType = api.liveedit.PageItemType;

    export class PartPlaceholder extends api.dom.Element {

        private comboBox: api.content.page.part.PartDescriptorComboBox;

        constructor(partView: PartView) {
            super(new api.dom.ElementProperties().setTagName("div"));

            wemjq(this.getHTMLElement()).on('click', 'input', (e) => {
                wemjq(e.currentTarget).focus();
                e.stopPropagation();
            });
            var request = new api.content.page.part.GetPartDescriptorsByModulesRequest(PageItemType.get().getSiteTemplate().getModules());
            var loader = new api.content.page.part.PartDescriptorLoader(request);
            this.comboBox = new api.content.page.part.PartDescriptorComboBox(loader);
            loader.load();
            this.comboBox.hide();
            this.appendChild(this.comboBox);

            this.comboBox.onOptionSelected((event: api.ui.selector.OptionSelectedEvent<api.content.page.part.PartDescriptor>) => {
                var componentPath = partView.getComponentPath();
                var descriptor: api.content.page.Descriptor = event.getOption().displayValue;
                new PageComponentSetDescriptorEvent(componentPath, descriptor, partView).fire();
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