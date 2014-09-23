module api.ui.geo {

    export class GeoPoint extends api.dom.DivEl {

        private geoLocationInput: api.ui.text.TextInput;

        constructor() {
            super("geo-point");

            this.geoLocationInput = new api.ui.text.TextInput();
            this.geoLocationInput.getEl().setAttribute("title", "geo location").addClass("geo-point-input");

            this.layoutItems();
            this.onShown((event) => {
                this.geoLocationInput.giveFocus();
            })
        }

        private layoutItems() {
            this.removeChildren();

            this.appendChild(this.geoLocationInput);
            this.geoLocationInput.setPlaceholder(_i18n('geo location'));
            return this;
        }

        onValueChanged(listener: (event: api.ui.ValueChangedEvent)=>void) {
            this.geoLocationInput.onValueChanged(listener);
        }


        setGeoPoint(value: api.util.GeoPoint): GeoPoint {
            this.geoLocationInput.setValue("" + value.getLatitude() + "," + value.getLongitude());
            return this;
        }

        getGeoPoint(): api.util.GeoPoint {
            if (api.util.StringHelper.isEmpty(this.geoLocationInput.getValue())) {
                return null;
            }
            return  api.util.GeoPoint.fromString(this.geoLocationInput.getValue());

        }

        getGeoPoint1(): string {
            return this.geoLocationInput.getValue();
        }


    }
}