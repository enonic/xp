module api.content.page.image {

    import RichComboBox = api.ui.combobox.RichComboBox;
    import RichComboBoxBuilder = api.ui.combobox.RichComboBoxBuilder;
    import ComboBoxConfig = api.ui.combobox.ComboBoxConfig;
    import Option = api.ui.combobox.Option;
    import SelectedOption = api.ui.combobox.SelectedOption;
    import SelectedOptionView = api.ui.combobox.SelectedOptionView;
    import SelectedOptionsView = api.ui.combobox.SelectedOptionsView;

    export class ImageDescriptorComboBox extends RichComboBox<ImageDescriptor> {

        constructor(loader: ImageDescriptorLoader) {
            super(new RichComboBoxBuilder<ImageDescriptor>().
                setSelectedOptionsView(new ImageDescriptorSelectedOptionsView()).
                setIdentifierMethod("getKey").
                setLoader(loader));
        }

        setDescriptor(descriptor: ImageDescriptor) {
            var option: Option<ImageDescriptor> = {
                value: descriptor.getKey().toString(),
                displayValue: descriptor
            };
            this.comboBox.selectOption(option);
        }

        optionFormatter(row: number, cell: number, descriptor: ImageDescriptor, columnDef: any,
                        dataContext: Option<ImageDescriptor>): string {

            var namesView = new api.app.NamesView()
                .setMainName(descriptor.getDisplayName())
                .setSubName(descriptor.getName().toString());

            return namesView.toString();
        }

        createConfig(): ComboBoxConfig<ImageDescriptor> {

            var config: ComboBoxConfig<ImageDescriptor> = super.createConfig();
            config.maximumOccurrences = 1;
            return config;
        }

        getSelectedData(): Option<ImageDescriptor>[] {
            return this.comboBox.getSelectedData();
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
            removeButtonEl.getEl().addEventListener('click', (event: Event) => {
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