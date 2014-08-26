module api.ui.geo {

    export class GeoPoint extends api.dom.DivEl {

        private latitudeInput: api.ui.text.TextInput;

        private longitudeInput: api.ui.text.TextInput;

        private latitudeChangedListeners: {(event: ValueChangedEvent):void}[] = [];

        private longitudeChangedListeners: {(event: ValueChangedEvent):void}[] = [];

        constructor() {
            super("geo-point");

            this.latitudeInput = new api.ui.text.TextInput();
            this.latitudeInput.getEl().setAttribute("title", "latitude").addClass("geo-point-input");

            this.longitudeInput = new api.ui.text.TextInput();
            this.longitudeInput.getEl().setAttribute("title", "longitude").addClass("geo-point-input");

            this.layoutItems();

            this.latitudeInput.onValueChanged((event: ValueChangedEvent) => {
                this.latitudeChangedListeners.forEach((listener) => {
                    listener(event);
                });
            });

            this.longitudeInput.onValueChanged((event: ValueChangedEvent) => {
                this.longitudeChangedListeners.forEach((listener) => {
                    listener(event);
                });
            });

            this.onShown((event) => {
                this.latitudeInput.giveFocus();
            })
        }

        private layoutItems() {
            this.removeChildren();

            this.appendChild(this.latitudeInput);
            this.latitudeInput.setPlaceholder(_i18n('latitude'));

            this.appendChild(this.longitudeInput);
            this.longitudeInput.setPlaceholder(_i18n('longitude'));

            return this;
        }

        onLatitudeChanged(listener: (event: api.ui.ValueChangedEvent)=>void) {
            this.latitudeChangedListeners.push(listener);
        }

        onLongitudeChanged(listener: (event: api.ui.ValueChangedEvent)=>void) {
            this.longitudeChangedListeners.push(listener);
        }

        setLatitude(value: string): GeoPoint {
            this.latitudeInput.setValue(value);
            return this;
        }

        getLatitude(): string {
            return this.latitudeInput.getValue();
        }

        setLongitude(value: string): GeoPoint {
            this.longitudeInput.setValue(value);
            return this;
        }

        getLongitude(): string {
            return this.longitudeInput.getValue();
        }

    }
}