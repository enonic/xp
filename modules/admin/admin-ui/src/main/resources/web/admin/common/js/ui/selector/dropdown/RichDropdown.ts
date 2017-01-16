module api.ui.selector.dropdown {

    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;

    export class RichDropdown<OPTION_DISPLAY_VALUE> extends Dropdown<OPTION_DISPLAY_VALUE> {

        protected loader: api.util.loader.BaseLoader<any, OPTION_DISPLAY_VALUE>;

        constructor(dropdownConfig: DropdownConfig<OPTION_DISPLAY_VALUE>, name: string = "") {
            super(name, dropdownConfig);

            this.loader = this.createLoader();

            this.initLoaderListeners();
        }

        protected createLoader(): api.util.loader.BaseLoader<any, OPTION_DISPLAY_VALUE> {
            throw new Error('Must be implemented by inheritors');
        }

        private initLoaderListeners() {
            this.loader.onLoadedData(this.handleLoadedData.bind(this));

            this.loader.onLoadingData((event: api.util.loader.event.LoadingDataEvent) => {
                this.setEmptyDropdownText("Searching...");
            });
        }

        load() {
            this.loader.load();
        }

        protected getLoader(): api.util.loader.BaseLoader<any, OPTION_DISPLAY_VALUE> {
            return this.loader;
        }

        showDropdown() {
            super.showDropdown();
            this.load();
        }

        protected handleLoadedData(event: LoadedDataEvent<OPTION_DISPLAY_VALUE>) {
            this.setOptions(this.createOptions(event.getData()));
        }

        private createOptions(values: OPTION_DISPLAY_VALUE[]): Option<OPTION_DISPLAY_VALUE>[] {
            let options = [];

            values.forEach((value: OPTION_DISPLAY_VALUE) => {
                options.push(this.createOption(value));
            });

            return options;
        }

        protected createOption(value: OPTION_DISPLAY_VALUE): Option<OPTION_DISPLAY_VALUE> {
            throw new Error("Must be implemented by inheritors");
        }

    }
}
