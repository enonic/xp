var api_ui;
(function (api_ui) {
    var Action = (function () {
        function Action(label) {
            this.enabled = true;
            this.executionListeners = [];
            this.propertyChangeListeners = [];
            this.label = label;
        }
        Action.prototype.getLabel = function () {
            return this.label;
        };
        Action.prototype.setLabel = function (value) {
            if(value !== this.label) {
                this.label = value;
                for(var i in this.propertyChangeListeners) {
                    this.propertyChangeListeners[i](this);
                }
            }
        };
        Action.prototype.isEnabled = function () {
            return this.enabled;
        };
        Action.prototype.setEnabled = function (value) {
            if(value !== this.enabled) {
                this.enabled = value;
                for(var i in this.propertyChangeListeners) {
                    this.propertyChangeListeners[i](this);
                }
            }
        };
        Action.prototype.getIconClass = function () {
            return this.iconClass;
        };
        Action.prototype.setIconClass = function (value) {
            if(value !== this.iconClass) {
                this.iconClass = value;
                for(var i in this.propertyChangeListeners) {
                    this.propertyChangeListeners[i](this);
                }
            }
        };
        Action.prototype.execute = function () {
            if(this.enabled) {
                for(var i in this.executionListeners) {
                    this.executionListeners[i](this);
                }
            }
        };
        Action.prototype.addExecutionListener = function (listener) {
            this.executionListeners.push(listener);
        };
        Action.prototype.addPropertyChangeListener = function (listener) {
            this.propertyChangeListeners.push(listener);
        };
        return Action;
    })();
    api_ui.Action = Action;    
})(api_ui || (api_ui = {}));
TestCase("Action", {
    "test getLabel": function () {
        assertEquals("My action", new api_ui.Action('My action').getLabel());
    },
    "test given setEnabled invoked then addPropertyChangeListener is invoked and action isEnabled is correct": function () {
        var action = new api_ui.Action('My action');
        action.setEnabled(true);
        assertEquals(true, action.isEnabled());
        action.addPropertyChangeListener(function (action) {
            assertEquals(false, action.isEnabled());
        });
        action.setEnabled(false);
    },
    "test given setLabel invoked then addPropertyChangeListener is invoked and action getLabel is correct": function () {
        var action = new api_ui.Action('My action');
        action.addPropertyChangeListener(function (action) {
            assertEquals("Changed label", action.getLabel());
        });
        action.setLabel("Changed label");
    }
});
var api_content_data;
(function (api_content_data) {
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
            var endsWithEndBracket = str.indexOf(']', str.length - ']'.length) !== -1;
            var containsStartBracket = str.indexOf('[') !== -1;
            if(endsWithEndBracket && containsStartBracket) {
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
    api_content_data.DataId = DataId;    
})(api_content_data || (api_content_data = {}));
TestCase("DataId", {
    "test getName": function () {
        assertEquals("myName", new api_content_data.DataId('myName', 0));
    },
    "test getArrayIndex": function () {
        assertEquals(0, new api_content_data.DataId('myName', 0).getArrayIndex());
        assertEquals(1, new api_content_data.DataId('myName', 1).getArrayIndex());
        assertEquals(999, new api_content_data.DataId('myName', 999).getArrayIndex());
    },
    "test toString": function () {
        assertEquals("myName", new api_content_data.DataId('myName', 0).toString());
        assertEquals("myName[1]", new api_content_data.DataId('myName', 1).toString());
        assertEquals("myName[999]", new api_content_data.DataId('myName', 999).toString());
    },
    "test toString when created using from": function () {
        assertEquals("myName", api_content_data.DataId.from('myName[0]').toString());
        assertEquals("myName[1]", api_content_data.DataId.from('myName[1]').toString());
        assertEquals("myName[999]", api_content_data.DataId.from('myName[999]').toString());
    }
});
var api_content_data;
(function (api_content_data) {
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
        Data.prototype.getId = function () {
            return new api_content_data.DataId(this.name, this.arrayIndex);
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
    api_content_data.Data = Data;    
})(api_content_data || (api_content_data = {}));
var __extends = this.__extends || function (d, b) {
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
var api_content_data;
(function (api_content_data) {
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
        Property.prototype.setValue = function (value) {
            this.value = value;
        };
        return Property;
    })(api_content_data.Data);
    api_content_data.Property = Property;    
})(api_content_data || (api_content_data = {}));
var api_content_data;
(function (api_content_data) {
    var DataSet = (function (_super) {
        __extends(DataSet, _super);
        function DataSet(name) {
                _super.call(this, name);
            this.dataById = {
            };
        }
        DataSet.prototype.nameCount = function (name) {
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
            var index = this.nameCount(data.getName());
            data.setArrayIndex(index);
            var dataId = new api_content_data.DataId(data.getName(), index);
            this.dataById[dataId.toString()] = data;
        };
        DataSet.prototype.getData = function (dataId) {
            return this.dataById[api_content_data.DataId.from(dataId).toString()];
        };
        return DataSet;
    })(api_content_data.Data);
    api_content_data.DataSet = DataSet;    
})(api_content_data || (api_content_data = {}));
TestCase("DataSet", {
    "test given a name when getName() then given name is returned": function () {
        var dataSet = new api_content_data.DataSet('mySet');
        assertEquals("mySet", dataSet.getName());
    },
    "test given an existing dataId when getData() then given Data is returned": function () {
        var dataSet = new api_content_data.DataSet('mySet');
        dataSet.addData(new api_content_data.Property('myProp', 'A value', 'String'));
        dataSet.addData(new api_content_data.Property('myOtherProp', 'A value', 'String'));
        assertEquals("myProp", dataSet.getData('myProp').getName());
        assertEquals("myOtherProp", dataSet.getData('myOtherProp').getName());
    },
    "test given a dataId not existing when getData() then no Data is returned": function () {
        var dataSet = new api_content_data.DataSet('mySet');
        dataSet.addData(new api_content_data.Property('myProp', 'A value', 'String'));
        assertEquals(null, dataSet.getData('myNonExistingProp'));
    },
    "test given a Data added to a DataSet when getParent() then the DataSet added to is returned": function () {
        var dataSet = new api_content_data.DataSet('mySet');
        dataSet.addData(new api_content_data.Property('myProp', 'A value', 'String'));
        var data = dataSet.getData('myProp');
        assertEquals(dataSet, data.getParent());
    },
    "test given two data with same name when nameCount then two is returned": function () {
        var dataSet = new api_content_data.DataSet('mySet');
        dataSet.addData(new api_content_data.Property('myProp', 'A', 'String'));
        dataSet.addData(new api_content_data.Property('myProp', 'B', 'String'));
        assertEquals(2, dataSet.nameCount('myProp'));
    },
    "test given Data with arrayIndex one when getData equal DataId then Data with arrayIndex one is returned": function () {
        var dataSet = new api_content_data.DataSet('mySet');
        dataSet.addData(new api_content_data.Property('myProp', 'A', 'String'));
        dataSet.addData(new api_content_data.Property('myProp', 'B', 'String'));
        assertEquals("myProp[1]", dataSet.getData('myProp[1]').getId().toString());
    }
});
TestCase("Property", {
    "test given a name when getName() then given name is returned": function () {
        var property = new api_content_data.Property('myProp', 'A value', 'String');
        assertEquals("myProp", property.getName());
    },
    "test given a value when getValue() then given value is returned": function () {
        var property = new api_content_data.Property('myProp', 'A value', 'String');
        assertEquals("A value", property.getValue());
    },
    "test given a type when getType() then given type is returned": function () {
        var property = new api_content_data.Property('myProp', 'A value', 'String');
        assertEquals("String", property.getType());
    }
});
//@ sourceMappingURL=test.js.map
