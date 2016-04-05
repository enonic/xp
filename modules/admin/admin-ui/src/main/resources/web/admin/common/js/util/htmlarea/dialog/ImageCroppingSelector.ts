module api.util.htmlarea.dialog {

    import Dropdown = api.ui.selector.dropdown.Dropdown;
    import Option = api.ui.selector.Option;
    import DropdownConfig = api.ui.selector.dropdown.DropdownConfig;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;

    export class ImageCroppingSelector extends Dropdown<ImageCroppingOption> {


        constructor() {
            super("imageSelector", <DropdownConfig<ImageCroppingOption>>{
                optionDisplayValueViewer: new ImageCroppingOptionViewer(),
                inputPlaceholderText: "Cropping effect"
            });
            this.addClass("image-cropping-selector");

            this.initDropdown();
        }

        private initDropdown() {

            this.addNoneOption();
            this.addCroppingOptions();

            this.onOptionSelected((event: OptionSelectedEvent<ImageCroppingOption>) => {
                if(event.getOption().displayValue.getName() == "none") {
                    this.reset();
                }
            });
        }

        private addNoneOption() {
            var noneOption = new ImageCroppingOption("none", 0 , 0);
            noneOption.setDisplayValue("<None>");

            var option =  {
                value: noneOption.getName(),
                displayValue: noneOption
            };

            this.addOption(option);
        }

        private addCroppingOptions() {
            ImageCroppingOptions.getOptions().forEach((option: Option<ImageCroppingOption>) => {
                this.addOption(option);
            });
        }

    }
}