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
                Data.prototype.getName = function () {
                    return this.name;
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
                function Property(json) {
                                _super.call(this, json.name);
                    this.value = json.value;
                    this.type = json.type;
                }
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
TestCase("Property", {
    "test given a name when getName() then given name is returned": function () {
        var property = new API.content.data.Property({
            name: 'myProp',
            value: 'A value',
            type: 'String'
        });
        assertEquals("myProp", property.getName());
    },
    "test given a value when getValue() then given value is returned": function () {
        var property = new API.content.data.Property({
            name: 'myProp',
            value: 'A value',
            type: 'String'
        });
        assertEquals("A value", property.getValue());
    },
    "test given a type when getType() then given type is returned": function () {
        var property = new API.content.data.Property({
            name: 'myProp',
            value: 'A value',
            type: 'String'
        });
        assertEquals("String", property.getType());
    }
});
//@ sourceMappingURL=test.js.map
