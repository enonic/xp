module api.content.page.region {

    import RichComboBox = api.ui.selector.combobox.RichComboBox;
    import RichComboBoxBuilder = api.ui.selector.combobox.RichComboBoxBuilder;
    import ComboBoxConfig = api.ui.selector.combobox.ComboBoxConfig;
    import Option = api.ui.selector.Option;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import BaseSelectedOptionView = api.ui.selector.combobox.BaseSelectedOptionView;
    import BaseSelectedOptionsView = api.ui.selector.combobox.BaseSelectedOptionsView;
    import DescriptorKey = api.content.page.DescriptorKey;

    export class PartDescriptorComboBox extends RichComboBox<PartDescriptor> {

        constructor(loader: PartDescriptorLoader) {
            super(new RichComboBoxBuilder<PartDescriptor>().
                setIdentifierMethod("getKey").
                setOptionDisplayValueViewer(new PartDescriptorViewer()).
                setSelectedOptionsView(new PartDescriptorSelectedOptionsView()).
                setLoader(loader).
                setMaximumOccurrences(1).
                setNextInputFocusWhenMaxReached(false));
        }

        setDescriptor(key: DescriptorKey) {

            var descriptorToSelect: PartDescriptor;

            this.getSelectedDisplayValues().forEach((descriptor: PartDescriptor) => {
                if (descriptor.getKey().toString() == key.toString()) {
                    descriptorToSelect = descriptor;
                }
            });
            if (!descriptorToSelect) {
                return;
            }

            var option: Option<PartDescriptor> = {
                value: descriptorToSelect.getKey().toString(),
                displayValue: descriptorToSelect
            };
            this.comboBox.clearSelection();
            this.comboBox.selectOption(option);
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
            this.descriptor = option.displayValue;
            super(option);
            this.addClass("part-descriptor-selected-option-view");
        }

        layout() {
            var namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            namesAndIconView.setIconClass("icon-puzzle icon-medium")
                .setMainName(this.descriptor.getDisplayName())
                .setSubName(this.descriptor.getKey().toString());

            var removeButtonEl = new api.dom.AEl("remove");
            removeButtonEl.onClicked((event: MouseEvent) => {
                this.notifySelectedOptionRemoveRequested();

                event.stopPropagation();
                event.preventDefault();
                return false;
            });

            this.appendChild(removeButtonEl);
            this.appendChild(namesAndIconView);
        }

    }
}