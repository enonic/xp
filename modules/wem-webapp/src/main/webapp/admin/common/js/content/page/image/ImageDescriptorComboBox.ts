module api.content.page.image {

    import RichComboBox = api.ui.selector.combobox.RichComboBox;
    import RichComboBoxBuilder = api.ui.selector.combobox.RichComboBoxBuilder;
    import ComboBoxConfig = api.ui.selector.combobox.ComboBoxConfig;
    import Option = api.ui.selector.Option;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import SelectedOptionView = api.ui.selector.combobox.SelectedOptionView;
    import SelectedOptionsView = api.ui.selector.combobox.SelectedOptionsView;
    import DescriptorKey = api.content.page.DescriptorKey;

    export class ImageDescriptorComboBox extends RichComboBox<ImageDescriptor> {

        constructor(loader: ImageDescriptorLoader) {
            super(new RichComboBoxBuilder<ImageDescriptor>().
                setSelectedOptionsView(new ImageDescriptorSelectedOptionsView()).
                setIdentifierMethod("getKey").
                setLoader(loader).
                setMaximumOccurrences(1));
        }

        setDescriptor(key: DescriptorKey) {

            var descriptorToSelect: ImageDescriptor;

            this.getValues().forEach((descriptor: ImageDescriptor) => {
                if (descriptor.getKey().toString() == key.toString()) {
                    descriptorToSelect = descriptor;
                }
            });
            if (!descriptorToSelect) {
                return;
            }

            var option: Option<ImageDescriptor> = {
                value: descriptorToSelect.getKey().toString(),
                displayValue: descriptorToSelect
            };
            this.comboBox.clearSelection();
            this.comboBox.selectOption(option);
        }

        optionFormatter(row: number, cell: number, descriptor: ImageDescriptor, columnDef: any,
                        dataContext: Option<ImageDescriptor>): string {

            var namesView = new api.app.NamesView()
                .setMainName(descriptor.getDisplayName())
                .setSubName(descriptor.getName().toString());

            return namesView.toString();
        }

        getSelectedOptions(): Option<ImageDescriptor>[] {
            return this.comboBox.getSelectedOptions();
        }

    }

    export class ImageDescriptorSelectedOptionsView extends SelectedOptionsView<ImageDescriptor> {

        createSelectedOption(option: Option<ImageDescriptor>, index: number): SelectedOption<ImageDescriptor> {
            return new SelectedOption<ImageDescriptor>(new ImageDescriptorSelectedOptionView(option), option, index);
        }
    }

    export class ImageDescriptorSelectedOptionView extends SelectedOptionView<ImageDescriptor> {

        private descriptor: ImageDescriptor;

        constructor(option: Option<ImageDescriptor>) {
            this.descriptor = option.displayValue;
            super(option);
            this.addClass("image-descriptor-selected-option-view");
        }

        layout() {
            var namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            namesAndIconView.setIconUrl(api.util.getAdminUri('common/images/icons/icoMoon/32x32/earth.png'))
                .setMainName(this.descriptor.getDisplayName())
                .setSubName(this.descriptor.getName().toString());

            var removeButtonEl = new api.dom.AEl("remove");
            removeButtonEl.onClicked((event: MouseEvent) => {
                this.notifySelectedOptionToBeRemoved();

                event.stopPropagation();
                event.preventDefault();
                return false;
            });

            this.appendChild(removeButtonEl);
            this.appendChild(namesAndIconView);
        }

    }
}