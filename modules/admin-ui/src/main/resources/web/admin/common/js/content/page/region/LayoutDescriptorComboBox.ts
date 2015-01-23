module api.content.page.region {

    import RichComboBox = api.ui.selector.combobox.RichComboBox;
    import RichComboBoxBuilder = api.ui.selector.combobox.RichComboBoxBuilder;
    import ComboBoxConfig = api.ui.selector.combobox.ComboBoxConfig;
    import Option = api.ui.selector.Option;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import BaseSelectedOptionView = api.ui.selector.combobox.BaseSelectedOptionView;
    import BaseSelectedOptionsView = api.ui.selector.combobox.BaseSelectedOptionsView;
    import DescriptorKey = api.content.page.DescriptorKey;

    export class LayoutDescriptorComboBox extends RichComboBox<LayoutDescriptor> {

        constructor(loader: LayoutDescriptorLoader) {
            super(new RichComboBoxBuilder<LayoutDescriptor>().
                setIdentifierMethod("getKey").
                setOptionDisplayValueViewer(new LayoutDescriptorViewer()).
                setSelectedOptionsView(new LayoutDescriptorSelectedOptionsView()).
                setLoader(loader).
                setMaximumOccurrences(1).
                setNextInputFocusWhenMaxReached(false));
        }
        getDescriptor(descriptorKey: DescriptorKey): LayoutDescriptor {
            var option = this.comboBox.getOptionByValue(descriptorKey.toString());
            if(option) {
                return option.displayValue;
            }
            return null;
        }

        setDescriptor(descriptor: LayoutDescriptor) {

            this.comboBox.clearSelection(false, false);
            if (descriptor) {
                var optionToSelect: Option<LayoutDescriptor> = this.comboBox.getOptionByValue(descriptor.getKey().toString());
                if (!optionToSelect) {
                    optionToSelect = {
                        value: descriptor.getKey().toString(),
                        displayValue: descriptor
                    };
                    this.comboBox.addOption(optionToSelect);
                }
                this.comboBox.selectOption(optionToSelect);
            }
        }
    }

    export class LayoutDescriptorSelectedOptionsView extends BaseSelectedOptionsView<LayoutDescriptor> {

        createSelectedOption(option: Option<LayoutDescriptor>): SelectedOption<LayoutDescriptor> {
            return new SelectedOption<LayoutDescriptor>(new LayoutDescriptorSelectedOptionView(option), this.count());
        }
    }

    export class LayoutDescriptorSelectedOptionView extends BaseSelectedOptionView<LayoutDescriptor> {

        private descriptor: LayoutDescriptor;

        constructor(option: Option<LayoutDescriptor>) {
            this.descriptor = option.displayValue;
            super(option);
            this.addClass("layout-descriptor-selected-option-view");
        }

        layout() {
            var namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            namesAndIconView.setIconClass("icon-earth icon-medium")
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