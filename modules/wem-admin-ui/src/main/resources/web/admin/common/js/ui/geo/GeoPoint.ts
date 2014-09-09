module api.ui.geo {

    export class GeoPoint extends api.dom.DivEl {

        private latitudeInput: api.ui.text.TextInput;

        private longitudeInput: api.ui.text.TextInput;

        constructor() {
            super("geo-point");

            this.latitudeInput = new api.ui.text.TextInput();
            this.latitudeInput.getEl().setAttribute("title", "latitude").addClass("geo-point-input");

            this.longitudeInput = new api.ui.text.TextInput();
            this.longitudeInput.getEl().setAttribute("title", "longitude").addClass("geo-point-input");

            this.layoutItems();
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
            this.latitudeInput.onValueChanged(listener);
        }

        onLongitudeChanged(listener: (event: api.ui.ValueChangedEvent)=>void) {
            this.longitudeInput.onValueChanged(listener);
        }

        setGeoPoint(value: api.util.GeoPoint): GeoPoint {
            this.latitudeInput.setValue("" + value.getLatitude());
            this.longitudeInput.setValue("" + value.getLongitude());
            return this;
        }

        getLatitude(): string {
            return this.latitudeInput.getValue();
        }

        getLongitude(): string {
            return this.longitudeInput.getValue();
        }

    }
}