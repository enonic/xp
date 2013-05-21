var API_content_data;
(function (API_content_data) {
    var DataId = (function () {
        function DataId(name, arrayIndex) {
            this.name = name;
            this.arrayIndex = arrayIndex;
            if(arrayIndex > 0) {
                this.refString = name + '[' + arrayIndex + ']';
            } else {
                this.refString = name;
            }
        }
        DataId.prototype.getName = function () {
            return this.name;
        };
        DataId.prototype.getArrayIndex = function () {
            return this.arrayIndex;
        };
        DataId.prototype.toString = function () {
            return this.refString;
        };
        DataId.from = function from(str) {
            console.log("str:" + str);
            var endsWithEndBracket = str.indexOf(']', str.length - ']'.length) !== -1;
            var containsStartBracket = str.indexOf('[') !== -1;
            if(endsWithEndBracket && containsStartBracket) {
                console.log("str: index there is");
                var firstBracketPos = str.indexOf('[');
                var nameStr = str.substring(0, firstBracketPos);
                var indexStr = str.substring(nameStr.length + 1, (str.length - 1));
                var index = parseInt(indexStr);
                return new DataId(nameStr, index);
            } else {
                return new DataId(str, 0);
            }
        };
        return DataId;
    })();
    API_content_data.DataId = DataId;    
})(API_content_data || (API_content_data = {}));
TestCase("DataId", {
    "test getName": function () {
        assertEquals("myName", new API_content_data.DataId('myName', 0));
    },
    "test getArrayIndex": function () {
        assertEquals(0, new API_content_data.DataId('myName', 0).getArrayIndex());
        assertEquals(1, new API_content_data.DataId('myName', 1).getArrayIndex());
        assertEquals(999, new API_content_data.DataId('myName', 999).getArrayIndex());
    },
    "test toString": function () {
        assertEquals("myName", new API_content_data.DataId('myName', 0).toString());
        assertEquals("myName[1]", new API_content_data.DataId('myName', 1).toString());
        assertEquals("myName[999]", new API_content_data.DataId('myName', 999).toString());
    },
    "test toString when created using from": function () {
        assertEquals("myName", API_content_data.DataId.from('myName[0]').toString());
        assertEquals("myName[1]", API_content_data.DataId.from('myName[1]').toString());
        assertEquals("myName[999]", API_content_data.DataId.from('myName[999]').toString());
    }
});
var API_content_data;
(function (API_content_data) {
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
    API_content_data.Data = Data;    
})(API_content_data || (API_content_data = {}));
var __extends = this.__extends || function (d, b) {
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
var API_content_data;
(function (API_content_data) {
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
    })(API_content_data.Data);
    API_content_data.Property = Property;    
})(API_content_data || (API_content_data = {}));
var API_content_data;
(function (API_content_data) {
    var DataSet = (function (_super) {
        __extends(DataSet, _super);
        function DataSet(name) {
                _super.call(this, name);
            this.dataById = {
            };
        }
        DataSet.prototype.dataCount = function (name) {
            var count = 0;
            for(var i in this.dataById) {
                var data = this.dataById[i];
                if(data.getName() === name) {
                    count++;
                }
            }
            return count;
        };
        DataSet.prototype.addData = function (data) {
            data.setParent(this);
            var index = this.dataCount(data.getName());
            data.setArrayIndex(index);
            var dataId = new API_content_data.DataId(data.getName(), index);
            var dataIdStr = dataId.toString();
            this.dataById[dataIdStr] = data;
        };
        DataSet.prototype.getData = function (dataId) {
            return this.dataById[API_content_data.DataId.from(dataId).toString()];
        };
        return DataSet;
    })(API_content_data.Data);
    API_content_data.DataSet = DataSet;    
})(API_content_data || (API_content_data = {}));
TestCase("DataSet", {
    "test given a name when getName() then given name is returned": function () {
        var dataSet = new API_content_data.DataSet('mySet');
        assertEquals("mySet", dataSet.getName());
    },
    "test given a dataId when getData() then given Data is returned": function () {
        var dataSet = new API_content_data.DataSet('mySet');
        dataSet.addData(new API_content_data.Property('myProp', 'A value', 'String'));
        dataSet.addData(new API_content_data.Property('myOtherProp', 'A value', 'String'));
        assertEquals("myProp", dataSet.getData('myProp').getName());
        assertEquals("myOtherProp", dataSet.getData('myOtherProp').getName());
    },
    "test given a Data added to a DataSet when getParent() then the DataSet added to is returned": function () {
        var dataSet = new API_content_data.DataSet('mySet');
        dataSet.addData(new API_content_data.Property('myProp', 'A value', 'String'));
        var data = dataSet.getData('myProp');
        assertEquals(dataSet, data.getParent());
    },
    "test xxx": function () {
        var dataSet = new API_content_data.DataSet('mySet');
        dataSet.addData(new API_content_data.Property('myProp', 'A', 'String'));
        dataSet.addData(new API_content_data.Property('myProp', 'B', 'String'));
        assertEquals(2, dataSet.dataCount('myProp'));
    }
});
TestCase("Property", {
    "test given a name when getName() then given name is returned": function () {
        var property = new API_content_data.Property('myProp', 'A value', 'String');
        assertEquals("myProp", property.getName());
    },
    "test given a value when getValue() then given value is returned": function () {
        var property = new API_content_data.Property('myProp', 'A value', 'String');
        assertEquals("A value", property.getValue());
    },
    "test given a type when getType() then given type is returned": function () {
        var property = new API_content_data.Property('myProp', 'A value', 'String');
        assertEquals("String", property.getType());
    }
});
//@ sourceMappingURL=test.js.map
