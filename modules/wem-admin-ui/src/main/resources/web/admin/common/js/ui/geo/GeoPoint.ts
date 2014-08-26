module api.ui.geo {

    export class GeoPoint extends api.dom.DivEl {

        private latitudeInput: api.ui.text.TextInput;

        private longitudeInput: api.ui.text.TextInput;

        private latitudeChangedListeners: {(event: ValueChangedEvent):void}[] = [];

        private longitudeChangedListeners: {(event: ValueChangedEvent):void}[] = [];

        constructor() {
            super("geopoint");
            this.latitudeInput = new api.ui.text.TextInput();
            this.latitudeInput.getEl().setAttribute("title", "latitude");
            this.longitudeInput = new api.ui.text.TextInput();
            this.longitudeInput.getEl().setAttribute("title", "longitude");
            this.layoutItems();
            this.latitudeInput.onValueChanged((event: ValueChangedEvent) => {
                this.latitudeChangedListeners.forEach((listener) => {
                    listener(event);
                });
            });
//            this.latitudeInput.onKeyPressed((event:KeyboardEvent) => {
//                var symbol = String.fromCharCode((<any> event).charCode);
//               if( !this.isNumber(symbol)){
//                   event.preventDefault();
//                   return false
//               }
//
//            });
            this.longitudeInput.onValueChanged((event: ValueChangedEvent) => {
                this.longitudeChangedListeners.forEach((listener) => {
                    listener(event);
                });
            });
            this.onShown((event) => {
                this.latitudeInput.giveFocus();
            })
        }

        private isNumber(str: string): boolean {
            var pattern = /^[+-]?\d+(\.\d+)?$/;
            return pattern.test(str);
        }

        private layoutItems() {
            this.removeChildren();
            var latitudeDiv = new api.dom.DivEl("geopoint-input");
            latitudeDiv.appendChild(this.latitudeInput);
            var longitudeDiv = new api.dom.DivEl("geopoint-input");
            longitudeDiv.appendChild(this.longitudeInput);
            this.appendChild(latitudeDiv);
            this.appendChild(longitudeDiv);
            this.latitudeInput.setPlaceholder(_i18n('latitude'));
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