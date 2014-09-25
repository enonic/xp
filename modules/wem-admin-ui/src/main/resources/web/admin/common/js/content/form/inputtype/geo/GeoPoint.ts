module api.content.form.inputtype.geo {

    import support = api.form.inputtype.support;

    import ValueTypes = api.data.type.ValueTypes;

    export class GeoPoint extends support.BaseInputTypeNotManagingAdd<any,api.util.GeoPoint> {

        constructor(config: api.form.inputtype.InputTypeViewContext<any>) {
            super(config);
        }

        getValueType(): api.data.type.ValueType {
            return ValueTypes.GEO_POINT;
        }

        newInitialValue(): api.util.GeoPoint {
            return null;
        }

        createInputOccurrenceElement(index: number, property: api.data.Property): api.dom.Element {

            var geoPoint = new api.ui.geo.GeoPoint();

            if (property.hasNonNullValue()) {
                var geoPointValue = property.getGeoPoint();
                if (geoPointValue) {
                    geoPoint.setGeoPoint(geoPointValue);
                }
            }
            return geoPoint;
        }

        availableSizeChanged() {
        }

        onOccurrenceValueChanged(element: api.dom.Element, listener: (event: api.form.inputtype.support.ValueChangedEvent) => void) {

            var geoPoint = <api.ui.geo.GeoPoint>element;

            geoPoint.onValueChanged((event: api.ui.ValueChangedEvent) => {
                var geoLocation = event.getNewValue();
                var value = ValueTypes.GEO_POINT.newValue(geoLocation);
                listener(new api.form.inputtype.support.ValueChangedEvent(value));
            });


        }

        getValue(occurrence: api.dom.Element): api.data.Value {
            var geoPointEL: api.ui.geo.GeoPoint = <api.ui.geo.GeoPoint>occurrence;
            var geoPointVo: api.util.GeoPoint = geoPointEL.getGeoPoint();
            return  new api.data.Value(geoPointVo, ValueTypes.GEO_POINT);
        }

        valueBreaksRequiredContract(value: api.data.Value): boolean {
            return value.isNull() || !value.getType().equals(ValueTypes.GEO_POINT);
        }
    }

    api.form.inputtype.InputTypeManager.register(new api.Class("GeoPoint", GeoPoint));
}