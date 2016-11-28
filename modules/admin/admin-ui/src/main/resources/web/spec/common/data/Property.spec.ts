import Property = api.data.Property;
import PropertyBuilder = api.data.PropertyBuilder;
import ValueTypeString = api.data.ValueTypeString;
import ValueTypePropertySet = api.data.ValueTypePropertySet;
import ValueTypeDateTime = api.data.ValueTypeDateTime;
import ValueTypeLocalDate = api.data.ValueTypeLocalDate;
import ValueTypeLocalDateTime = api.data.ValueTypeLocalDateTime;
import ValueTypeLocalTime = api.data.ValueTypeLocalTime;
import ValueTypeGeoPoint = api.data.ValueTypeGeoPoint;
import ValueTypeReference = api.data.ValueTypeReference;
import ValueTypeBinaryReference = api.data.ValueTypeBinaryReference;

describe("api.data.Property", () => {

    let builder: PropertyBuilder, propertyArray: PropertyArray, tree: PropertyTree, value: Value;

    beforeEach(() => {
        builder = Property.create();

        tree = new PropertyTree();

        propertyArray =
            PropertyArray.create().setType(ValueTypes.STRING).setParent(tree.getRoot()).setName("name").build();

        tree.getRoot().addPropertyArray(propertyArray);

        value = new ValueTypeString().newValue("myValue");
    });

    describe("constructor", () => {

        beforeEach(() => {
            spyOn(console, "error").and.stub();
        });

        it("throws an exception if array is null", () => {
            expect(builder.build).toThrowError("array of a Property cannot be null");
        });

        it("throws an exception if name is null", () => {
            builder.setArray(propertyArray);
            expect(() => {
                builder.build();
            }).toThrowError("name of a Property cannot be null");
        });

        it("throws an exception if index is null", () => {
            builder.setArray(propertyArray).setName("name");
            expect(() => {
                builder.build()
            }).toThrowError("index of a Property cannot be null");
        });

        it("throws an exception if value is null", () => {
            builder.setArray(propertyArray).setName("name").setIndex(0);
            expect(() => {
                builder.build()
            }).toThrowError("value of a Property cannot be null");
        });

        it("successfull property creation", () => {
            builder.setArray(propertyArray).setName("name").setIndex(0).setValue(value);
            expect(builder.build()).toBeDefined();
        });
    });

    describe("public methods", () => {
        let property: Property;

        beforeEach(() => {
            property = Property.create().setArray(propertyArray).setName("name").setIndex(0).setValue(value).build();
            propertyArray.addProperty(property);
        });


        describe("getName()", () => {

            it("given a Property with name 'myValue' then 'myValue' is returned", () => {
                expect(property.getName()).toBe("name");
            });
        });

        describe("getIndex()", () => {

            it("given a Property with index 0 then 0 is returned", () => {
                expect(property.getIndex()).toBe(0);
            });

            it("given a Property with index 1 then 1 is returned", () => {
                property = tree.addString("name", "myValue1");
                expect(property.getIndex()).toBe(1);
            });
        });

        describe("getPath()", () => {

            it("given a Property named 'name' which has root as parent then '.name' is returned", () => {
                expect(property.getPath().toString()).toBe(".name");
            });

            it("given a Property named 'name' which has root as parent then '.name' is returned", () => {
                let propertySet = tree.addPropertySet("mySet");
                property = propertySet.setString("name", 0, "myValue");

                expect(property.getPath().toString()).toBe(".mySet.name");
            });
        });

        describe("getParentProperty()", () => {

            it("given a Property having root as parent then null returned", () => {
                expect(property.getParentProperty()).toBeNull();
                expect(property.hasParentProperty()).toBeFalsy();
            });

            it("given a Property having a sub-set as parent then the Property of that sub-set is returned", () => {
                let propertySet = tree.addPropertySet("mySet");
                property = propertySet.setString("myProp", 0, "myValue");
                expect(property.getParentProperty()).toBe(tree.getProperty("mySet"));
                expect(property.hasParentProperty()).toBeTruthy();
            });
        });

        describe("getType()", () => {

            it("given a Property with ValueType String then ValueTypes.STRING is returned", () => {
                expect(property.getType().toString()).toBe(ValueTypes.STRING.toString());
            });
        });

        describe("hasNullValue()", () => {

            it("given a Property with a value then false is returned", () => {
                expect(property.hasNullValue()).toBeFalsy();
            });
        });

        describe("hasNonNullValue()", () => {

            it("when getting a Property with a value then true is returned", () => {
                expect(property.hasNonNullValue()).toBeTruthy();
            });
        });

        describe("getValue()", () => {

            it("given a Property with a string value then the same string value is returned", () => {
                expect(property.getValue().getObject()).toBe("myValue");
            });
        });

        describe("getParent()", () => {

            it("given a Property having root as parent then the PropertyTree.root is returned", () => {
                expect(property.getParent()).toBe(tree.getRoot());
            });

            it("given a Property having a sub-set as parent then that sub-set is returned", () => {
                let propertySet = tree.addPropertySet("mySet");

                property = propertySet.setString("myProp", 0, "myValue");
                expect(property.getParent()).toBe(propertySet);
            });
        });

        describe("setIndex()", () => {
            let listenerSpy;
            beforeEach(() => {
                listenerSpy = spyOn(property, "notifyPropertyIndexChangedEvent");
            });

            it("property index changed event isn't fired if new index  equal to the old one", () => {
                property.setIndex(0);
                expect(listenerSpy).not.toHaveBeenCalled();
            });

            it("property index changed event is fired if new index not equal to the old one", () => {
                property.setIndex(1);
                expect(listenerSpy).toHaveBeenCalledWith(0, 1);
            });
        });

        describe("setValue()", () => {
            let listenerSpy;

            beforeEach(() => {
                spyOn(console, "error").and.stub();
                listenerSpy = spyOn(property, "notifyPropertyIndexChangedEvent");
            });

            it("throws an exception if value is null", () => {
                expect(() => {
                    property.setValue(null);
                }).toThrowError("value of a Property cannot be null");
            });

            it("value changed successfully", () => {
                listenerSpy = spyOn(property, "notifyPropertyValueChangedEvent");

                let newValue = new ValueTypeString().newValue("newValue"),
                    oldValue = property.getValue();

                property.setValue(newValue);

                expect(listenerSpy).toHaveBeenCalledWith(oldValue, newValue);
            });

            describe("property set handling", () => {
                let propertySet: PropertySet, propertySetSpy;

                beforeEach(() => {
                    propertySet = new PropertySet();
                    propertySetSpy = spyOn(propertySet, "setContainerProperty");
                });

                it("register new property set", () => {

                    let propertyArraySpy = spyOn(propertyArray, "registerPropertySetListeners");

                    property.setValue(new Value(propertySet, ValueTypes.DATA));

                    expect(propertySetSpy).toHaveBeenCalledWith(property);
                    expect(propertyArraySpy).toHaveBeenCalledWith(propertySet);
                });

                it("unregister old property set", () => {
                    property.setValue(new Value(propertySet, ValueTypes.DATA));

                    let propertyArraySpy = spyOn(propertyArray, "unregisterPropertySetListeners");

                    property.setValue(new Value(new PropertySet(), ValueTypes.DATA));

                    expect(propertySetSpy).toHaveBeenCalledWith(null);
                    expect(propertyArraySpy).toHaveBeenCalledWith(propertySet);
                });
            });
        });

        describe("convertValueType()", () => {
            it("array's convert values method is called", () => {
                let propertyArraySpy = spyOn(propertyArray, "convertValues").and.stub();
                property.convertValueType(ValueTypes.BOOLEAN);

                expect(propertyArraySpy).toHaveBeenCalledWith(ValueTypes.BOOLEAN);
            });
        });

        describe("detach()", () => {
            it("array's convert values method is called", () => {
                property.detach();

                expect(property.getParent()).toBe(null);
            });
        });

        describe("reset()", () => {
            it("reset property to initial value", () => {
                property.reset();
                expect(property.getValue().toString()).toBe(new ValueTypeString().newNullValue().toString());
            });

            it("reset property set to initial value", () => {
                let propertySet = new PropertySet(),
                    propertySetSpy = spyOn(propertySet, "reset");
                ;

                property.setValue(new Value(propertySet, ValueTypes.DATA));

                property.reset();
                expect(propertySetSpy).toHaveBeenCalled();
            });
        });

        describe("equals()", () => {
            let other: PropertyBuilder;

            beforeEach(() => {
                other = Property.create();
            });

            it("different object type is not equal", () => {
                expect(property.equals(propertyArray)).toBeFalsy();
            });

            it("property with different name is not equal", () => {
                other.setName("other name").setArray(propertyArray).setIndex(property.getIndex()).setValue(property.getValue());

                expect(property.equals(other.build())).toBeFalsy();
            });

            it("property with different index is not equal", () => {
                other.setIndex(4).setArray(propertyArray).setName(property.getName()).setValue(property.getValue());

                expect(property.equals(other.build())).toBeFalsy();
            });

            it("property with different value is not equal", () => {
                other.setValue(new ValueTypeString().newValue("other value")).setArray(propertyArray).setIndex(property.getIndex()).setName(
                    property.getName());

                expect(property.equals(other.build())).toBeFalsy();
            });

            it("the same property is equal", () => {

                expect(property.equals(property)).toBeTruthy();
            });

            it("property with the same data is equal", () => {
                other.setArray(propertyArray).setValue(property.getValue()).setIndex(property.getIndex()).setName(property.getName());

                expect(property.equals(other.build())).toBeTruthy();
            });
        });

        describe("copy()", () => {
            it("copy simple property", () => {
                let destinationArray =
                    PropertyArray.create().setType(ValueTypes.STRING).setParent(tree.getRoot()).setName("destination").build();

                let copy = property.copy(destinationArray);

                expect(property.getName()).toBe(copy.getName());
                expect(property.getValue()).toBe(copy.getValue());
                expect(property.getIndex()).toBe(copy.getIndex());
                expect(destinationArray).toBe(copy['array']);
            });

            it("copy PropertySet property", () => {
                let destinationArray =
                    PropertyArray.create().setType(ValueTypes.DATA).setParent(tree.getRoot()).setName("destination").build();

                let propertySet = new PropertySet();

                property.setValue(new Value(propertySet, ValueTypes.DATA));

                let copy = property.copy(destinationArray);
                expect(copy.getValue().getPropertySet().getTree()).toBe(destinationArray.getTree());
            });
        });

        describe("getString()", () => {
            it("String to String is valid", () => {
                property.setValue(new Value("newValue", ValueTypes.STRING));
                expect(property.getString()).toBe("newValue");
            })
        });

        describe("getPropertySet()", () => {
            it("PropertySet to PropertySet is valid", () => {
                let propertySet = new PropertySet();
                property.setValue(new Value(propertySet, ValueTypes.DATA));
                expect(property.getPropertySet()).toBe(propertySet);
            })
        });


        describe("getLong()", () => {
            it("String to Long is valid", () => {
                property.setValue(new Value("3", ValueTypes.STRING));
                expect(property.getLong()).toBe(3);
            });

            it("Long to Long is valid", () => {
                property.setValue(new Value(3, ValueTypes.LONG));
                expect(property.getLong()).toBe(3);
            });
        });

        describe("getDouble()", () => {
            it("String to Double is valid", () => {
                property.setValue(new Value("3.6", ValueTypes.STRING));
                expect(property.getDouble()).toBe(3.6);
            });

            it("Double to Double is valid", () => {
                property.setValue(new Value(3.6, ValueTypes.DOUBLE));
                expect(property.getDouble()).toBe(3.6);
            });
        });

        describe("getBoolean()", () => {
            it("String to Boolean is valid", () => {
                property.setValue(new Value("true", ValueTypes.STRING));
                expect(property.getBoolean()).toBeTruthy();

                property.setValue(new Value("false", ValueTypes.STRING));
                expect(property.getBoolean()).toBeFalsy();
            });

            it("Boolean to Boolean is valid", () => {
                property.setValue(new Value(true, ValueTypes.BOOLEAN));
                expect(property.getBoolean()).toBeTruthy();
            });
        });

        describe("getDateTime()", () => {
            it("DateTime to DateTime is valid", () => {
                let dateTime = new ValueTypeDateTime().newValue("1997-07-16T19:20+01:00");
                property.setValue(dateTime);
                expect(property.getDateTime().dateToString()).toBe("1997-07-16");
                expect(property.getDateTime().timeToString()).toBe("19:20:00");
            });
        });


        describe("getLocalDateTime()", () => {
            it("LocalDateTime to LocalDateTime is valid", () => {
                let localDateTime = new ValueTypeLocalDateTime().newValue("1997-07-16T19:20:00");
                property.setValue(localDateTime);
                expect(property.getLocalDateTime().getDay()).toBe(16);
                expect(property.getLocalDateTime().getMonth()).toBe(6);
                expect(property.getLocalDateTime().getYear()).toBe(1997);
                expect(property.getLocalDateTime().getHours()).toBe(19);
                expect(property.getLocalDateTime().getMinutes()).toBe(20);
                expect(property.getLocalDateTime().getSeconds()).toBe(0);
            });
        });

        describe("getLocalTime()", () => {
            it("LocalTime to LocalTime is valid", () => {
                let localTime = new ValueTypeLocalTime().newValue("19:20:00");
                property.setValue(localTime);
                expect(property.getLocalTime().getHours()).toBe(19);
                expect(property.getLocalTime().getMinutes()).toBe(20);
                expect(property.getLocalTime().getSeconds()).toBe(0);
            });
        });

        describe("getLocalDate()", () => {
            it("LocalDate to LocalDate is valid", () => {
                let localDate = new ValueTypeLocalDate().newValue("1997-07-16");
                property.setValue(localDate);
                expect(property.getLocalDate().getDay()).toBe(16);
                expect(property.getLocalDate().getMonth()).toBe(6);
                expect(property.getLocalDate().getYear()).toBe(1997);
            });
        });

        describe("getGeoPoint()", () => {
            it("LocalDate to LocalDate is valid", () => {
                let geoPoint = new ValueTypeGeoPoint().newValue("11.1,22.2");
                property.setValue(geoPoint);
                expect(property.getGeoPoint().getLatitude()).toBe(11.1);
                expect(property.getGeoPoint().getLongitude()).toBe(22.2);
            });
        });

        describe("getReference()", () => {
            it("Reference to Reference is valid", () => {
                let ref = new ValueTypeReference().newValue("nodeId");
                property.setValue(ref);
                expect(property.getReference().getNodeId()).toBe("nodeId");
            });
        });

        describe("getBinaryReference()", () => {
            it("BinaryReference to BinaryReference is valid", () => {
                let ref = new ValueTypeBinaryReference().newValue("binaryValue");
                property.setValue(ref);
                expect(property.getBinaryReference().getValue()).toBe("binaryValue");
            });
        });

        describe("static getName()", () => {

            it("throws error if name is null", () => {
                expect(() => Property.checkName(null)).toThrowError("Property name cannot be null");
            });

            it("throws error if name is blank", () => {
                expect(() => Property.checkName("")).toThrowError("Property name cannot be blank");
            });

            it("throws error if name contains '.'", () => {
                expect(() => Property.checkName("na.me")).toThrowError("Property name cannot contain .");
            });

            it("throws error if name contains '[' or ']'", () => {
                expect(() => Property.checkName("[name")).toThrowError("Property name cannot contain [ or ]");
                expect(() => Property.checkName("name]")).toThrowError("Property name cannot contain [ or ]");
                expect(() => Property.checkName("[name]")).toThrowError("Property name cannot contain [ or ]");
            });
        });
    });
});