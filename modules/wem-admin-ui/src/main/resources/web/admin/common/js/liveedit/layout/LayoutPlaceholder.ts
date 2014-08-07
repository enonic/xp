module api.liveedit.layout {

    import PageComponentSetDescriptorEvent = api.liveedit.PageComponentSetDescriptorEvent;
    import LayoutItemType = api.liveedit.layout.LayoutItemType;
    import PageItemType = api.liveedit.PageItemType;

    export class LayoutPlaceholder extends api.dom.DivEl {

        private comboBox: api.content.page.layout.LayoutDescriptorComboBox;

        constructor(layoutView: LayoutComponentView) {
            super();

            this.onClicked((event: MouseEvent) => {
                event.stopPropagation();
            });
            var request = new api.content.page.layout.GetLayoutDescriptorsByModulesRequest(PageItemType.get().getSiteTemplate().getModules());
            var loader = new api.content.page.layout.LayoutDescriptorLoader(request);
            this.comboBox = new api.content.page.layout.LayoutDescriptorComboBox(loader);
            loader.load();
            this.comboBox.hide();
            this.appendChild(this.comboBox);

            this.comboBox.onOptionSelected((event: api.ui.selector.OptionSelectedEvent<api.content.page.layout.LayoutDescriptor>) => {
                var descriptor: api.content.page.Descriptor = event.getOption().displayValue;
                new PageComponentSetDescriptorEvent(descriptor, layoutView).fire();
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