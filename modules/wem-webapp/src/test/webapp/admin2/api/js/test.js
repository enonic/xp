var API;
(function (API) {
    (function (content) {
        (function (data) {
            var Data = (function () {
                function Data(name) {
                    this.name = name;
                }
                Data.prototype.setArrayIndex = function (value) {
                    this.arrayIndex = value;
                };
                Data.prototype.setParent = function (parent) {
                    this.parent = parent;
                };
                Data.prototype.getName = function () {
                    return this.name;
                };
                Data.prototype.getParent = function () {
                    return this.parent;
                };
                Data.prototype.getArrayIndex = function () {
                    return this.arrayIndex;
                };
                return Data;
            })();
            data.Data = Data;            
        })(content.data || (content.data = {}));
        var data = content.data;
    })(API.content || (API.content = {}));
    var content = API.content;
})(API || (API = {}));
var __extends = this.__extends || function (d, b) {
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
var API;
(function (API) {
    (function (content) {
        (function (data) {
            var Property = (function (_super) {
                __extends(Property, _super);
                function Property(name, value, type) {
                                _super.call(this, name);
                    this.value = value;
                    this.type = type;
                }
                Property.from = function from(json) {
                    return new Property(json.name, json.value, json.type);
                };
                Property.prototype.getValue = function () {
                    return this.value;
                };
                Property.prototype.getType = function () {
                    return this.type;
                };
                return Property;
            })(data.Data);
            data.Property = Property;            
        })(content.data || (content.data = {}));
        var data = content.data;
    })(API.content || (API.content = {}));
    var content = API.content;
})(API || (API = {}));
var API;
(function (API) {
    (function (content) {
        (function (data) {
            var DataSet = (function (_super) {
                __extends(DataSet, _super);
                function DataSet(name) {
                                _super.call(this, name);
                    this.dataById = {
                    };
                }
                DataSet.prototype.addData = function (data) {
                    data.setParent(this);
                    this.dataById[data.getName()] = data;
                };
                DataSet.prototype.getData = function (dataId) {
                    return this.dataById[dataId];
                };
                return DataSet;
            })(data.Data);
            data.DataSet = DataSet;            
        })(content.data || (content.data = {}));
        var data = content.data;
    })(API.content || (API.content = {}));
    var content = API.content;
})(API || (API = {}));
TestCase("DataSet", {
    "test given a name when getName() then given name is returned": function () {
        var dataSet = new API.content.data.DataSet('mySet');
        assertEquals("mySet", dataSet.getName());
    },
    "test given a dataId when getData() then given Data is returned": function () {
        var dataSet = new API.content.data.DataSet('mySet');
        dataSet.addData(new API.content.data.Property('myProp', 'A value', 'String'));
        dataSet.addData(new API.content.data.Property('myOtherProp', 'A value', 'String'));
        assertEquals("myProp", dataSet.getData('myProp').getName());
        assertEquals("myOtherProp", dataSet.getData('myOtherProp').getName());
    },
    "test given a Data added to a DataSet when getParent() then the DataSet added to is returned": function () {
        var dataSet = new API.content.data.DataSet('mySet');
        dataSet.addData(new API.content.data.Property('myProp', 'A value', 'String'));
        var data = dataSet.getData('myProp');
        assertEquals(dataSet, data.getParent());
    }
});
TestCase("Property", {
    "test given a name when getName() then given name is returned": function () {
        var property = new API.content.data.Property('myProp', 'A value', 'String');
        assertEquals("myProp", property.getName());
    },
    "test given a value when getValue() then given value is returned": function () {
        var property = new API.content.data.Property('myProp', 'A value', 'String');
        assertEquals("A value", property.getValue());
    },
    "test given a type when getType() then given type is returned": function () {
        var property = new API.content.data.Property('myProp', 'A value', 'String');
        assertEquals("String", property.getType());
    }
});
//@ sourceMappingURL=test.js.map
