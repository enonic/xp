module api.util.htmlarea.dialog {

    import Option = api.ui.selector.Option;
    import i18n = api.util.i18n;

    export class ImageCroppingOptions {

        private static INSTANCE: ImageCroppingOptions;

        private SQUARE: ImageCroppingOption = new ImageCroppingOption('square', 1, 1);

        private REGULAR: ImageCroppingOption = new ImageCroppingOption('regular', 4, 3);

        private WIDESCREEN: ImageCroppingOption = new ImageCroppingOption('widescreen', 16, 9);

        private CINEMA: ImageCroppingOption = new ImageCroppingOption('cinema', 21, 9);

        private PORTRAIT: ImageCroppingOption = new ImageCroppingOption('portrait', 3, 4);

        private TALL: ImageCroppingOption = new ImageCroppingOption('tall', 9, 16);

        private SKYSCRAPER: ImageCroppingOption = new ImageCroppingOption('skyscraper', 9, 21);

        private NONE: ImageCroppingOption = new ImageCroppingOption('none', 0, 0, i18n('dialog.image.cropping.none'));

        public static get(): ImageCroppingOptions {
            if (!ImageCroppingOptions.INSTANCE) {
                ImageCroppingOptions.INSTANCE = new ImageCroppingOptions();
            }
            return ImageCroppingOptions.INSTANCE;
        }

        getOptions(): Option<ImageCroppingOption>[] {

            let options: Option<ImageCroppingOption>[] = [];

            this.getCroppingOptions().forEach((imageCroppingOption: ImageCroppingOption) => {
                let option = {
                    value: imageCroppingOption.getName(),
                    displayValue: imageCroppingOption
                };

                options.push(option);
            });

            return options;
        }

        private getCroppingOptions(): ImageCroppingOption[] {
            return [
                this.NONE,
                this.CINEMA,
                this.WIDESCREEN,
                this.REGULAR,
                this.SQUARE,
                this.PORTRAIT,
                this.TALL,
                this.SKYSCRAPER
            ];
        }

        getOptionByProportion(proportion: string): ImageCroppingOption {
            let imageCroppingOption: ImageCroppingOption = null;
            let imageCroppingOptions: ImageCroppingOption[] = this.getCroppingOptions();

            for (let i = 0; i < imageCroppingOptions.length; i++) {
                if (imageCroppingOptions[i].getProportionString() === proportion) {
                    imageCroppingOption = imageCroppingOptions[i];
                    break;
                }
            }

            return imageCroppingOption;
        }

    }
}
