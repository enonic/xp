module api.ui.selector.combobox {

    export class LoaderComboBox<OPTION_DISPLAY_VALUE> extends ComboBox<OPTION_DISPLAY_VALUE> {

        private loader: api.util.loader.BaseLoader<any, OPTION_DISPLAY_VALUE>;

        private tempValue: string;

        public static debug: boolean = false;

        constructor(name: string, config: ComboBoxConfig<OPTION_DISPLAY_VALUE>,
                    loader: api.util.loader.BaseLoader<any, OPTION_DISPLAY_VALUE>) {
            super(name, config);

            this.addClass('loader-combobox');
            this.loader = loader;
        }

        public setLoader(loader: api.util.loader.BaseLoader<any, OPTION_DISPLAY_VALUE>) {
            this.loader = loader;
        }

        protected doSetValue(value: string, silent?: boolean) {
            if (this.getComboBoxDropdownGrid().isTreeGrid()) {

                if(!value || this.getOptionByValue(value)) {
                    super.doSetValue(value, silent);
                } else {
                    this.getOptionDataLoader().load(this.splitValues(value)).then(() => {
                        super.doSetValue(value, silent);
                        this.refreshValueChanged(silent);
                        this.refreshDirtyState(silent);
                    });
                }

            } else {
                if (!this.loader.isLoaded()) {
                    if (RichComboBox.debug) {
                        console.debug(this.toString() + '.doSetValue: loader is not loaded, saving temp value = ' + value);
                    }
                    this.tempValue = value;
                }
                this.doWhenLoaded(() => {
                    if (this.tempValue) {
                        if (RichComboBox.debug) {
                            console.debug(this.toString() + '.doSetValue: clearing temp value = ' + this.tempValue);
                        }
                        delete this.tempValue;
                    }
                    super.doSetValue(value, silent);
                }, value);
            }
        }

        protected doGetValue(): string {
            if (!this.loader.isLoaded() && this.tempValue != null) {
                if (RichComboBox.debug) {
                    console.debug('RichComboBox: loader is not loaded, returning temp value = ' + this.tempValue);
                }
                return this.tempValue;
            } else {
                return super.doGetValue();
            }
        }

        private doWhenLoaded(callback: Function, value: string) {
            if (this.loader.isLoaded()) {
                let optionsMissing = !api.util.StringHelper.isEmpty(value) && this.splitValues(value).some((val) => {
                        return !this.getOptionByValue(val);
                    });
                if (optionsMissing) { // option needs loading
                    this.loader.preLoad(value).then(() => {
                        callback();
                    });
                } else { // empty option
                    callback();
                }
            } else {
                if (RichComboBox.debug) {
                    console.debug(this.toString() + '.doWhenLoaded: waiting to be loaded');
                }
                let singleLoadListener = ((data) => {
                    if (RichComboBox.debug) {
                        console.debug(this.toString() + '.doWhenLoaded: on loaded');
                    }
                    callback(data);
                    this.loader.unLoadedData(singleLoadListener);
                });
                this.loader.onLoadedData(singleLoadListener);
                if (!api.util.StringHelper.isEmpty(value) && this.loader.isNotStarted()) {
                    this.loader.preLoad(value);
                }
            }
        }
    }
}
