module api.content.form.inputtype.geo {

    import support = api.form.inputtype.support;

    import ValueTypes = api.data.ValueTypes;
    import ValueType = api.data.ValueType;
    import Value = api.data.Value;
    import Property = api.data.Property;

    // TODO: GeoPoint is not dependent on the content domain and should therefore be moved to api.form.inputtype.geo
    export class GeoPoint extends support.BaseInputTypeNotManagingAdd<any,api.util.GeoPoint> {

        constructor(config: api.form.inputtype.InputTypeViewContext<any>) {
            super(config);
        }

        getValueType(): ValueType {
            return ValueTypes.GEO_POINT;
        }

        newInitialValue(): Value {
            return ValueTypes.GEO_POINT.newNullValue();
        }

        createInputOccurrenceElement(index: number, property: Property): api.dom.Element {

            var geoPoint = new api.ui.geo.GeoPoint();

            if (property.hasNonNullValue()) {
                var geoPointValue = property.getGeoPoint();
                if (geoPointValue) {
                    geoPoint.setGeoPoint(geoPointValue);
                }
            }

            geoPoint.onValueChanged((event: api.ui.ValueChangedEvent) => {
                var geoLocation = event.getNewValue();
                var value = ValueTypes.GEO_POINT.newValue(geoLocation);
                property.setValue(value);
            });

            return geoPoint;
        }

        availableSizeChanged() {
        }

        valueBreaksRequiredContract(value: Value): boolean {
            return value.isNull() || !value.getType().equals(ValueTypes.GEO_POINT);
        }
    }

    api.form.inputtype.InputTypeManager.register(new api.Class("GeoPoint", GeoPoint));
}