module api.liveedit.part {

    import Descriptor = api.content.page.Descriptor;
    import PartDescriptor = api.content.page.part.PartDescriptor;
    import PartDescriptorLoader = api.content.page.part.PartDescriptorLoader;
    import PartDescriptorComboBox = api.content.page.part.PartDescriptorComboBox;
    import GetPartDescriptorsByModulesRequest = api.content.page.part.GetPartDescriptorsByModulesRequest;
    import PageComponentSetDescriptorEvent = api.liveedit.PageComponentSetDescriptorEvent;
    import PartItemType = api.liveedit.part.PartItemType;
    import PageItemType = api.liveedit.PageItemType;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;

    export class PartPlaceholder extends api.dom.DivEl {

        private comboBox: PartDescriptorComboBox;

        private partComponentView: PartComponentView;

        constructor(partView: PartComponentView) {
            super();
            this.partComponentView = partView;

            this.onClicked((event: MouseEvent) => {
                event.stopPropagation();
            });
            var request = new GetPartDescriptorsByModulesRequest(PageItemType.get().getSiteTemplate().getModules());
            var loader = new PartDescriptorLoader(request);
            this.comboBox = new PartDescriptorComboBox(loader);
            loader.load();
            this.comboBox.hide();
            this.appendChild(this.comboBox);

            this.comboBox.onOptionSelected((event: OptionSelectedEvent<PartDescriptor>) => {
                this.partComponentView.showLoadingSpinner();
                var descriptor: Descriptor = event.getOption().displayValue;
                new PageComponentSetDescriptorEvent(descriptor, partView).fire();
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