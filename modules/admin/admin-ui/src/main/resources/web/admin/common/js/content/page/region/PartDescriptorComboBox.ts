module api.content.page.region {

    import RichComboBox = api.ui.selector.combobox.RichComboBox;
    import RichComboBoxBuilder = api.ui.selector.combobox.RichComboBoxBuilder;
    import ComboBoxConfig = api.ui.selector.combobox.ComboBoxConfig;
    import Option = api.ui.selector.Option;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import BaseSelectedOptionView = api.ui.selector.combobox.BaseSelectedOptionView;
    import BaseSelectedOptionsView = api.ui.selector.combobox.BaseSelectedOptionsView;
    import DescriptorKey = api.content.page.DescriptorKey;
    import ApplicationKey = api.application.ApplicationKey;

    export class PartDescriptorComboBox extends RichComboBox<PartDescriptor> {

        constructor() {
            super(new RichComboBoxBuilder<PartDescriptor>()
                .setIdentifierMethod('getKey')
                .setOptionDisplayValueViewer(new PartDescriptorViewer())
                .setSelectedOptionsView(new PartDescriptorSelectedOptionsView())
                .setLoader(new PartDescriptorLoader())
                .setMaximumOccurrences(1).setNextInputFocusWhenMaxReached(false)
                .setNoOptionsText('No parts available'));
        }

        loadDescriptors(applicationKeys: ApplicationKey[]) {
            (<PartDescriptorLoader>this.loader).setApplicationKeys(applicationKeys);
            this.loader.load();
        }

        getDescriptor(descriptorKey: DescriptorKey): PartDescriptor {
            let option = this.getOptionByValue(descriptorKey.toString());
            if (option) {
                return option.displayValue;
            }
            return null;
        }

        setDescriptor(descriptor: PartDescriptor) {

            this.clearSelection();
            if (descriptor) {
                let optionToSelect: Option<PartDescriptor> = this.getOptionByValue(descriptor.getKey().toString());
                if (!optionToSelect) {
                    optionToSelect = {
                        value: descriptor.getKey().toString(),
                        displayValue: descriptor
                    };
                    this.addOption(optionToSelect);
                }
                this.selectOption(optionToSelect);
            }
        }

    }

    export class PartDescriptorSelectedOptionsView extends BaseSelectedOptionsView<PartDescriptor> {

        createSelectedOption(option: Option<PartDescriptor>): SelectedOption<PartDescriptor> {
            return new SelectedOption<PartDescriptor>(new PartDescriptorSelectedOptionView(option), this.count());
        }
    }

    export class PartDescriptorSelectedOptionView extends BaseSelectedOptionView<PartDescriptor> {

        private descriptor: PartDescriptor;

        constructor(option: Option<PartDescriptor>) {
            super(option);

            this.descriptor = option.displayValue;
            this.addClass('part-descriptor-selected-option-view');
        }

        doRender(): wemQ.Promise<boolean> {

            let namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            namesAndIconView.setIconClass(api.StyleHelper.getCommonIconCls('part') + ' icon-medium')
                .setMainName(this.descriptor.getDisplayName())
                .setSubName(this.descriptor.getKey().toString());

            let removeButtonEl = new api.dom.AEl('remove');
            removeButtonEl.onClicked((event: MouseEvent) => {
                this.notifyRemoveClicked();

                event.stopPropagation();
                event.preventDefault();
                return false;
            });

            this.appendChildren<api.dom.Element>(removeButtonEl, namesAndIconView);

            return wemQ(true);
        }

    }
}
