module api.util.htmlarea.dialog {

    import Dropdown = api.ui.selector.dropdown.Dropdown;
    import Option = api.ui.selector.Option;
    import DropdownConfig = api.ui.selector.dropdown.DropdownConfig;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
    import i18n = api.util.i18n;

    export class ImageCroppingSelector extends Dropdown<ImageCroppingOption> {

        constructor() {
            super('imageSelector', <DropdownConfig<ImageCroppingOption>>{
                optionDisplayValueViewer: new ImageCroppingOptionViewer(),
                inputPlaceholderText: i18n('dialog.image.cropping.effect')
            });
            this.addClass('image-cropping-selector');

            this.initDropdown();
        }

        private initDropdown() {
            this.addCroppingOptions();

            this.onOptionSelected((event: OptionSelectedEvent<ImageCroppingOption>) => {
                if(event.getOption().displayValue.getName() === 'none') {
                    this.reset();
                }
            });
        }

        private addCroppingOptions() {
            ImageCroppingOptions.get().getOptions().forEach((option: Option<ImageCroppingOption>) => {
                this.addOption(option);
            });
        }

        public addCustomScaleOption(value: string): Option<ImageCroppingOption> {
            const scaleRegex = /^(\d+):(\d+)$/;
            if (!scaleRegex.test(value)) {
                return null;
            }

            const result = scaleRegex.exec(value);
            const customOption: ImageCroppingOption = new ImageCroppingOption('custom', parseInt(result[1], 10), parseInt(result[2], 10));

            const option: Option<ImageCroppingOption> = {
                value: customOption.getName(),
                displayValue: customOption
            };

            this.addOption(option);

            return option;
        }

    }
}
