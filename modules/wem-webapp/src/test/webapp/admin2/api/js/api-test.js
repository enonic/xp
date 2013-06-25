var api_ui;
(function (api_ui) {
    var KeyBindings = (function () {
        function KeyBindings() { }
        KeyBindings.mousetraps = {
        };
        KeyBindings.shelves = [];
        KeyBindings.bindKeys = function bindKeys(bindings) {
            bindings.forEach(function (binding) {
                KeyBindings.bindKey(binding);
            });
        };
        KeyBindings.bindKey = function bindKey(binding) {
            console.log("KeyBindings.bindKey", binding);
            Mousetrap.bind(binding.getCombination(), binding.getCallback(), binding.getAction());
            KeyBindings.mousetraps[binding.getCombination()] = binding;
        };
        KeyBindings.unbindKeys = function unbindKeys(bindings) {
            console.log("KeyBindings.unbindKeys");
            bindings.forEach(function (binding) {
                KeyBindings.unbindKey(binding);
            });
        };
        KeyBindings.unbindKey = function unbindKey(binding) {
            console.log("KeyBindings.unbindKey");
            Mousetrap.unbind(binding.getCombination());
            delete KeyBindings.mousetraps[binding.getCombination()];
        };
        KeyBindings.trigger = function trigger(combination, action) {
            Mousetrap.trigger(combination, action);
        };
        KeyBindings.reset = function reset() {
            console.log("KeyBindings.reset");
            Mousetrap.reset();
            KeyBindings.mousetraps = {
            };
        };
        KeyBindings.shelveBindings = function shelveBindings() {
            console.log("shelveBindings() {");
            console.log("  resetting current");
            for(var key in KeyBindings.mousetraps) {
                console.log("  shelving: " + KeyBindings.mousetraps[key].getCombination());
            }
            Mousetrap.reset();
            KeyBindings.shelves.push(KeyBindings.mousetraps);
            KeyBindings.mousetraps = {
            };
            console.log("}");
        };
        KeyBindings.unshelveBindings = function unshelveBindings() {
            console.log("unshelveBindings() {");
            console.log(" resetting current");
            console.log(" removing last shelf");
            Mousetrap.reset();
            var previousMousetraps = KeyBindings.shelves.pop();
            for(var key in previousMousetraps) {
                var mousetrap = previousMousetraps[key];
                console.log("  binding: " + mousetrap.getCombination());
                Mousetrap.bind(mousetrap.getCombination(), mousetrap.getCallback(), mousetrap.getAction());
            }
            KeyBindings.mousetraps = previousMousetraps;
            console.log("}");
        };
        return KeyBindings;
    })();
    api_ui.KeyBindings = KeyBindings;    
    var KeyBinding = (function () {
        function KeyBinding(combination, callback, action) {
            this.combination = combination;
            this.callback = callback;
            this.action = action;
        }
        KeyBinding.prototype.setCallback = function (value) {
            this.callback = value;
            return this;
        };
        KeyBinding.prototype.setAction = function (value) {
            this.action = value;
            return this;
        };
        KeyBinding.prototype.getCombination = function () {
            return this.combination;
        };
        KeyBinding.prototype.getCallback = function () {
            return this.callback;
        };
        KeyBinding.prototype.getAction = function () {
            return this.action;
        };
        KeyBinding.newKeyBinding = function newKeyBinding(combination) {
            return new KeyBinding(combination);
        };
        return KeyBinding;
    })();
    api_ui.KeyBinding = KeyBinding;    
})(api_ui || (api_ui = {}));
var api_ui;
(function (api_ui) {
    var Action = (function () {
        function Action(label, shortcut) {
            var _this = this;
            this.enabled = true;
            this.executionListeners = [];
            this.propertyChangeListeners = [];
            this.label = label;
            if(shortcut) {
                this.shortcut = new api_ui.KeyBinding(shortcut).setCallback(function () {
                    _this.execute();
                });
            }
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
        Action.prototype.hasShortcut = function () {
            return this.shortcut != null;
        };
        Action.prototype.getShortcut = function () {
            return this.shortcut;
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
        Action.getKeyBindings = function getKeyBindings(actions) {
            var bindings = [];
            actions.forEach(function (action, index, array) {
                if(action.hasShortcut()) {
                    bindings.push(action.getShortcut());
                }
            });
            return bindings;
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
var api_dom;
(function (api_dom) {
    var ElementHelper = (function () {
        function ElementHelper(element) {
            this.el = element;
        }
        ElementHelper.fromName = function fromName(name) {
            return new ElementHelper(document.createElement(name));
        };
        ElementHelper.prototype.getHTMLElement = function () {
            return this.el;
        };
        ElementHelper.prototype.insertBefore = function (newEl, existingEl) {
            this.el.insertBefore(newEl.getHTMLElement(), existingEl ? existingEl.getHTMLElement() : null);
        };
        ElementHelper.prototype.insertBeforeEl = function (existingEl) {
            existingEl.getHTMLElement().parentNode.insertBefore(this.el, existingEl.getHTMLElement());
        };
        ElementHelper.prototype.insertAfterEl = function (existingEl) {
            existingEl.getHTMLElement().parentNode.insertBefore(this.el, existingEl.getHTMLElement().nextSibling);
        };
        ElementHelper.prototype.setDisabled = function (value) {
            this.el.disabled = value;
            return this;
        };
        ElementHelper.prototype.isDisabled = function () {
            return this.el.disabled;
        };
        ElementHelper.prototype.setId = function (value) {
            this.el.id = value;
            return this;
        };
        ElementHelper.prototype.setInnerHtml = function (value) {
            this.el.innerHTML = value;
            return this;
        };
        ElementHelper.prototype.setValue = function (value) {
            this.el.setAttribute("value", value);
            return this;
        };
        ElementHelper.prototype.addClass = function (clsName) {
            if(!this.hasClass(clsName)) {
                if(this.el.className === '') {
                    this.el.className += clsName;
                } else {
                    this.el.className += ' ' + clsName;
                }
            }
        };
        ElementHelper.prototype.hasClass = function (clsName) {
            return this.el.className.match(new RegExp('(\\s|^)' + clsName + '(\\s|$)')) !== null;
        };
        ElementHelper.prototype.removeClass = function (clsName) {
            if(this.hasClass(clsName)) {
                var reg = new RegExp('(\\s|^)' + clsName + '(\\s|$)');
                this.el.className = this.el.className.replace(reg, '');
            }
        };
        ElementHelper.prototype.addEventListener = function (eventName, f) {
            this.el.addEventListener(eventName, f);
        };
        ElementHelper.prototype.removeEventListener = function (eventName, f) {
            this.el.removeEventListener(eventName, f);
        };
        ElementHelper.prototype.appendChild = function (child) {
            this.el.appendChild(child);
            return this;
        };
        ElementHelper.prototype.setData = function (name, value) {
            var any = this.el;
            any._data[name] = value;
            return this;
        };
        ElementHelper.prototype.getData = function (name) {
            var any = this.el;
            return any._data[name];
        };
        ElementHelper.prototype.getDisplay = function () {
            return this.el.style.display;
        };
        ElementHelper.prototype.setDisplay = function (value) {
            this.el.style.display = value;
            return this;
        };
        ElementHelper.prototype.getVisibility = function () {
            return this.el.style.visibility;
        };
        ElementHelper.prototype.setVisibility = function (value) {
            this.el.style.visibility = value;
            return this;
        };
        ElementHelper.prototype.setPosition = function (value) {
            this.el.style.position = value;
            return this;
        };
        ElementHelper.prototype.setWidth = function (value) {
            this.el.style.width = value;
            return this;
        };
        ElementHelper.prototype.getWidth = function () {
            return this.el.offsetWidth;
        };
        ElementHelper.prototype.setHeight = function (value) {
            this.el.style.height = value;
            return this;
        };
        ElementHelper.prototype.getHeight = function () {
            return this.el.offsetHeight;
        };
        ElementHelper.prototype.setTop = function (value) {
            this.el.style.top = value;
            return this;
        };
        ElementHelper.prototype.setLeft = function (value) {
            this.el.style.left = value;
            return this;
        };
        ElementHelper.prototype.setMarginLeft = function (value) {
            this.el.style.marginLeft = value;
            return this;
        };
        ElementHelper.prototype.setMarginRight = function (value) {
            this.el.style.marginRight = value;
            return this;
        };
        ElementHelper.prototype.setMarginTop = function (value) {
            this.el.style.marginTop = value;
            return this;
        };
        ElementHelper.prototype.setMarginBottom = function (value) {
            this.el.style.marginBottom = value;
            return this;
        };
        ElementHelper.prototype.setZindex = function (value) {
            this.el.style.zIndex = value.toString();
            return this;
        };
        ElementHelper.prototype.setBackgroundImage = function (value) {
            this.el.style.backgroundImage = value;
            return this;
        };
        ElementHelper.prototype.remove = function () {
            var parent = this.el.parentElement;
            parent.removeChild(this.el);
        };
        ElementHelper.prototype.getOffset = function () {
            var el = this.el;
            var x = 0, y = 0;
            while(el && !isNaN(el.offsetLeft) && !isNaN(el.offsetTop)) {
                x += el.offsetLeft - el.scrollLeft;
                y += el.offsetTop - el.scrollTop;
                el = el.offsetParent;
            }
            return {
                top: y,
                left: x
            };
        };
        return ElementHelper;
    })();
    api_dom.ElementHelper = ElementHelper;    
})(api_dom || (api_dom = {}));
var api_dom;
(function (api_dom) {
    var Element = (function () {
        function Element(elementName, idPrefix, className, elHelper) {
            if(elHelper == null) {
                this.el = api_dom.ElementHelper.fromName(elementName);
            } else {
                this.el = elHelper;
            }
            if(idPrefix != null) {
                this.id = idPrefix + '-' + (++Element.constructorCounter);
                this.el.setId(this.id);
            }
            if(className != null) {
                this.getHTMLElement().className = className;
            }
        }
        Element.constructorCounter = 0;
        Element.prototype.className = function (value) {
            this.getHTMLElement().className = value;
            return this;
        };
        Element.prototype.show = function () {
            jQuery(this.el.getHTMLElement()).show();
        };
        Element.prototype.hide = function () {
            jQuery(this.el.getHTMLElement()).hide();
        };
        Element.prototype.isVisible = function () {
            var displayed = this.el.getDisplay() != "none";
            var visible = this.el.getVisibility() != "hidden";
            var sized = this.el.getWidth() != 0 || this.el.getHeight() != 0;
            return displayed && visible && sized;
        };
        Element.prototype.empty = function () {
            this.el.setInnerHtml("");
        };
        Element.prototype.getId = function () {
            return this.id;
        };
        Element.prototype.getEl = function () {
            return this.el;
        };
        Element.prototype.getHTMLElement = function () {
            return this.el.getHTMLElement();
        };
        Element.prototype.appendChild = function (child) {
            this.el.appendChild(child.getEl().getHTMLElement());
        };
        Element.prototype.prependChild = function (child) {
            this.el.getHTMLElement().insertBefore(child.getHTMLElement(), this.el.getHTMLElement().firstChild);
        };
        Element.prototype.removeChild = function (child) {
            if(this.el.getHTMLElement().contains(child.getHTMLElement())) {
                this.el.getHTMLElement().removeChild(child.getHTMLElement());
            }
        };
        Element.prototype.insertAfterEl = function (existingEl) {
            this.el.insertAfterEl(existingEl);
        };
        Element.prototype.insertBeforeEl = function (existingEl) {
            this.el.insertBeforeEl(existingEl);
        };
        Element.prototype.removeChildren = function () {
            var htmlEl = this.el.getHTMLElement();
            while(htmlEl.firstChild) {
                htmlEl.removeChild(htmlEl.firstChild);
            }
        };
        return Element;
    })();
    api_dom.Element = Element;    
})(api_dom || (api_dom = {}));
var api_dom;
(function (api_dom) {
    var DivEl = (function (_super) {
        __extends(DivEl, _super);
        function DivEl(idPrefix, className) {
                _super.call(this, "div", idPrefix, className);
        }
        return DivEl;
    })(api_dom.Element);
    api_dom.DivEl = DivEl;    
})(api_dom || (api_dom = {}));
var api_dom;
(function (api_dom) {
    var ButtonEl = (function (_super) {
        __extends(ButtonEl, _super);
        function ButtonEl(idPrefix, className) {
                _super.call(this, "button", idPrefix, className);
        }
        return ButtonEl;
    })(api_dom.Element);
    api_dom.ButtonEl = ButtonEl;    
})(api_dom || (api_dom = {}));
var api_dom;
(function (api_dom) {
    var SpanEl = (function (_super) {
        __extends(SpanEl, _super);
        function SpanEl(idPrefix, className) {
                _super.call(this, 'span', idPrefix, className);
        }
        return SpanEl;
    })(api_dom.Element);
    api_dom.SpanEl = SpanEl;    
})(api_dom || (api_dom = {}));
var api_dom;
(function (api_dom) {
    var UlEl = (function (_super) {
        __extends(UlEl, _super);
        function UlEl(idPrefix, className) {
                _super.call(this, "ul", idPrefix, className);
        }
        return UlEl;
    })(api_dom.Element);
    api_dom.UlEl = UlEl;    
})(api_dom || (api_dom = {}));
var api_dom;
(function (api_dom) {
    var LiEl = (function (_super) {
        __extends(LiEl, _super);
        function LiEl(idPrefix, className) {
                _super.call(this, "li", idPrefix, className);
        }
        return LiEl;
    })(api_dom.Element);
    api_dom.LiEl = LiEl;    
})(api_dom || (api_dom = {}));
var api_ui;
(function (api_ui) {
    var Panel = (function (_super) {
        __extends(Panel, _super);
        function Panel(idPrefix) {
                _super.call(this, idPrefix, "Panel");
        }
        return Panel;
    })(api_dom.DivEl);
    api_ui.Panel = Panel;    
})(api_ui || (api_ui = {}));
var api_ui;
(function (api_ui) {
    var DeckPanel = (function (_super) {
        __extends(DeckPanel, _super);
        function DeckPanel(idPrefix) {
                _super.call(this, idPrefix || "DeckPanel");
            this.panels = [];
            this.panelShown = -1;
        }
        DeckPanel.prototype.isEmpty = function () {
            return this.panels.length == 0;
        };
        DeckPanel.prototype.getSize = function () {
            return this.panels.length;
        };
        DeckPanel.prototype.addPanel = function (panel) {
            panel.hide();
            this.appendChild(panel);
            return this.panels.push(panel) - 1;
        };
        DeckPanel.prototype.getPanel = function (index) {
            return this.panels[index];
        };
        DeckPanel.prototype.getLastPanel = function () {
            return this.isEmpty() ? null : this.panels[this.panels.length - 1];
        };
        DeckPanel.prototype.getPanelShown = function () {
            return this.panels[this.panelShown];
        };
        DeckPanel.prototype.getPanelShownIndex = function () {
            return this.panelShown;
        };
        DeckPanel.prototype.removePanel = function (index) {
            var panelToRemove = this.panels[index];
            panelToRemove.getEl().remove();
            var removingLastPanel = this.panels.length == index + 1;
            var panelToRemoveIsShown = this.isShownPanel(index);
            this.panels.splice(index, 1);
            if(this.isEmpty()) {
                this.panelShown = -1;
            } else if(panelToRemoveIsShown) {
                if(removingLastPanel) {
                    this.getLastPanel().show();
                    this.panelShown = this.panels.length - 1;
                } else {
                    this.panels[index].show();
                }
            }
            return panelToRemove;
        };
        DeckPanel.prototype.isShownPanel = function (panelIndex) {
            return this.panelShown === panelIndex;
        };
        DeckPanel.prototype.showPanel = function (index) {
            for(var i = 0; i < this.panels.length; i++) {
                var panel = this.panels[i];
                if(i === index) {
                    panel.show();
                    this.panelShown = index;
                } else {
                    panel.hide();
                }
            }
        };
        DeckPanel.prototype.getPanels = function () {
            return this.panels;
        };
        return DeckPanel;
    })(api_ui.Panel);
    api_ui.DeckPanel = DeckPanel;    
})(api_ui || (api_ui = {}));
TestCase("DecPanel", {
    "test given DeckPanel with three panels and last panel is shown when last is removed then the second becomes the shown": function () {
        var deckPanel = new api_ui.DeckPanel();
        var panel1 = new api_ui.Panel();
        var panel2 = new api_ui.Panel();
        var panel3 = new api_ui.Panel();
        deckPanel.addPanel(panel1);
        deckPanel.addPanel(panel2);
        deckPanel.addPanel(panel3);
        deckPanel.showPanel(2);
        assertEquals(2, deckPanel.getPanelShownIndex());
        assertEquals(panel3, deckPanel.getPanelShown());
        deckPanel.removePanel(2);
        assertEquals(1, deckPanel.getPanelShownIndex());
        assertEquals(panel2, deckPanel.getPanelShown());
    }
});
var api_ui_tab;
(function (api_ui_tab) {
    var TabMenuItem = (function (_super) {
        __extends(TabMenuItem, _super);
        function TabMenuItem(label) {
            var _this = this;
                _super.call(this, "TabMenuItem", "tab-menu-item");
            this.visible = true;
            this.removable = true;
            this.label = label;
            this.labelEl = new api_dom.SpanEl();
            this.labelEl.getEl().setInnerHtml(label);
            this.appendChild(this.labelEl);
            var removeButton = new api_dom.ButtonEl();
            removeButton.getEl().setInnerHtml("&times;");
            this.appendChild(removeButton);
            this.labelEl.getEl().addEventListener("click", function () {
                _this.tabMenu.handleTabClickedEvent(_this);
            });
            removeButton.getEl().addEventListener("click", function () {
                if(_this.removable) {
                    _this.tabMenu.handleTabRemoveButtonClickedEvent(_this);
                    if(_this.tabMenu.getSize() == 0) {
                        _this.tabMenu.hideMenu();
                    }
                }
            });
        }
        TabMenuItem.prototype.setTabMenu = function (tabMenu) {
            this.tabMenu = tabMenu;
        };
        TabMenuItem.prototype.setTabIndex = function (value) {
            this.tabIndex = value;
        };
        TabMenuItem.prototype.getTabIndex = function () {
            return this.tabIndex;
        };
        TabMenuItem.prototype.getLabel = function () {
            return this.label;
        };
        TabMenuItem.prototype.isVisible = function () {
            return this.visible;
        };
        TabMenuItem.prototype.setVisible = function (value) {
            this.visible = value;
            if(!this.visible) {
                this.remove();
            }
        };
        TabMenuItem.prototype.isActive = function () {
            return this.active;
        };
        TabMenuItem.prototype.setActive = function (value) {
            this.active = value;
            if(this.active) {
                this.getEl().addClass("active");
            } else {
                this.getEl().removeClass("active");
            }
        };
        TabMenuItem.prototype.isRemovable = function () {
            return this.removable;
        };
        TabMenuItem.prototype.setRemovable = function (value) {
            this.removable = value;
        };
        TabMenuItem.prototype.remove = function () {
            if(this.tabMenu) {
                this.tabMenu.removeChild(this);
            }
        };
        return TabMenuItem;
    })(api_dom.LiEl);
    api_ui_tab.TabMenuItem = TabMenuItem;    
})(api_ui_tab || (api_ui_tab = {}));
var api_ui_tab;
(function (api_ui_tab) {
    var TabMenuButton = (function (_super) {
        __extends(TabMenuButton, _super);
        function TabMenuButton(idPrefix) {
                _super.call(this, idPrefix || "TabMenuButton");
            this.labelEl = new api_dom.SpanEl();
            this.appendChild(this.labelEl);
        }
        TabMenuButton.prototype.setTabMenu = function (tabMenu) {
            this.tabMenu = tabMenu;
        };
        TabMenuButton.prototype.setLabel = function (value) {
            this.labelEl.getEl().setInnerHtml(value);
        };
        return TabMenuButton;
    })(api_dom.DivEl);
    api_ui_tab.TabMenuButton = TabMenuButton;    
})(api_ui_tab || (api_ui_tab = {}));
var api_ui_tab;
(function (api_ui_tab) {
    var TabMenu = (function (_super) {
        __extends(TabMenu, _super);
        function TabMenu(idPrefix) {
            var _this = this;
                _super.call(this, idPrefix || "TabMenu");
            this.showingMenuItems = false;
            this.tabs = [];
            this.tabSelectedListeners = [];
            this.tabRemovedListeners = [];
            this.tabMenuButton = this.createTabMenuButton();
            this.tabMenuButton.hide();
            this.tabMenuButton.getEl().addEventListener("click", function () {
                _this.toggleMenu();
            });
            this.appendChild(this.tabMenuButton);
            this.menuEl = this.createMenu();
            this.appendChild(this.menuEl);
            this.initExt();
        }
        TabMenu.prototype.createTabMenuButton = function () {
            return new api_ui_tab.TabMenuButton();
        };
        TabMenu.prototype.createMenu = function () {
            var ulEl = new api_dom.UlEl();
            ulEl.getEl().setZindex(19001);
            ulEl.hide();
            return ulEl;
        };
        TabMenu.prototype.initExt = function () {
            var htmlEl = this.getHTMLElement();
            this.ext = new Ext.Component({
                contentEl: htmlEl
            });
        };
        TabMenu.prototype.toggleMenu = function () {
            if(!this.showingMenuItems) {
                this.showMenu();
            } else {
                this.hideMenu();
            }
        };
        TabMenu.prototype.hideMenu = function () {
            this.menuEl.hide();
            this.showingMenuItems = false;
        };
        TabMenu.prototype.showMenu = function () {
            this.menuEl.show();
            this.showingMenuItems = true;
        };
        TabMenu.prototype.addTab = function (tab) {
            var tabMenuItem = tab;
            tabMenuItem.setTabMenu(this);
            var newLength = this.tabs.push(tabMenuItem);
            tabMenuItem.setTabIndex(newLength - 1);
            if(tab.isVisible()) {
                this.tabMenuButton.setLabel(tab.getLabel());
                this.menuEl.appendChild(tabMenuItem);
                this.tabMenuButton.show();
            }
        };
        TabMenu.prototype.isEmpty = function () {
            return this.tabs.length == 0;
        };
        TabMenu.prototype.getSize = function () {
            return this.tabs.length;
        };
        TabMenu.prototype.countVisible = function () {
            var size = 0;
            this.tabs.forEach(function (tab) {
                if(tab.isVisible()) {
                    size++;
                }
            });
            return size;
        };
        TabMenu.prototype.getSelectedTabIndex = function () {
            return this.selectedTab;
        };
        TabMenu.prototype.getSelectedTab = function () {
            return this.tabs[this.selectedTab];
        };
        TabMenu.prototype.removeTab = function (tab) {
            var tabMenuItem = tab;
            tabMenuItem.getEl().remove();
            var isLastTab = this.isLastTab(tab);
            this.tabs.splice(tab.getTabIndex(), 1);
            if(this.isSelectedTab(tab)) {
                if(this.isEmpty()) {
                    this.selectedTab = -1;
                } else if(tab.getTabIndex() > this.tabs.length - 1) {
                    this.selectedTab = tab.getTabIndex() - 1;
                }
            }
            if(!isLastTab) {
                for(var i = tab.getTabIndex() - 1; i < this.tabs.length; i++) {
                    this.tabs[i].setTabIndex(i);
                }
            }
            if(this.countVisible() == 0) {
                this.tabMenuButton.setLabel("");
                this.tabMenuButton.hide();
                this.hideMenu();
            }
        };
        TabMenu.prototype.isSelectedTab = function (tab) {
            return tab.getTabIndex() == this.selectedTab;
        };
        TabMenu.prototype.isLastTab = function (tab) {
            return tab.getTabIndex() === this.tabs.length;
        };
        TabMenu.prototype.updateActiveTab = function (tabIndex) {
            this.tabs.forEach(function (tab, index) {
                var activate = (tabIndex == index);
                tab.setActive(activate);
            });
        };
        TabMenu.prototype.selectTab = function (tabIndex) {
            var selectedTab = this.tabs[tabIndex];
            this.tabMenuButton.setLabel(selectedTab.getLabel());
            this.selectedTab = tabIndex;
            this.updateActiveTab(tabIndex);
        };
        TabMenu.prototype.getActiveTab = function () {
            return this.getSelectedTab();
        };
        TabMenu.prototype.deselectTab = function () {
            this.tabMenuButton.setLabel("");
            this.selectedTab = -1;
        };
        TabMenu.prototype.addTabSelectedListener = function (listener) {
            this.tabSelectedListeners.push(listener);
        };
        TabMenu.prototype.addTabRemoveListener = function (listener) {
            this.tabRemovedListeners.push(listener);
        };
        TabMenu.prototype.handleTabClickedEvent = function (tabMenuItem) {
            this.hideMenu();
            this.fireTabSelected(tabMenuItem);
        };
        TabMenu.prototype.handleTabRemoveButtonClickedEvent = function (tabMenuItem) {
            if(this.fireTabRemoveEvent(tabMenuItem)) {
                this.removeTab(tabMenuItem);
            }
        };
        TabMenu.prototype.fireTabSelected = function (tab) {
            for(var i = 0; i < this.tabSelectedListeners.length; i++) {
                this.tabSelectedListeners[i](tab);
            }
        };
        TabMenu.prototype.fireTabRemoveEvent = function (tab) {
            for(var i = 0; i < this.tabRemovedListeners.length; i++) {
                if(!this.tabRemovedListeners[i](tab)) {
                    return false;
                }
            }
            return true;
        };
        return TabMenu;
    })(api_dom.DivEl);
    api_ui_tab.TabMenu = TabMenu;    
})(api_ui_tab || (api_ui_tab = {}));
TestCase("TabMenu", {
    "test given TabMenu added to DOM when getElementById then element is returned ": function () {
        var tabMenu = new api_ui_tab.TabMenu();
        var tab1 = new api_ui_tab.TabMenuItem("Tab1");
        var tab2 = new api_ui_tab.TabMenuItem("Tab2");
        tabMenu.addTab(tab1);
        tabMenu.addTab(tab2);
        document.body.appendChild(tabMenu.getHTMLElement());
        var tabMenuEl = document.getElementById(tabMenu.getId());
        assertNotNull(tabMenuEl);
        assertEquals(tabMenu.getId(), tabMenuEl.id);
    },
    "test given TabMenu with two tabs when getSize then two is returned": function () {
        var tabMenu = new api_ui_tab.TabMenu();
        var tab1 = new api_ui_tab.TabMenuItem("Tab1");
        var tab2 = new api_ui_tab.TabMenuItem("Tab2");
        tabMenu.addTab(tab1);
        tabMenu.addTab(tab2);
        assertEquals(2, tabMenu.getSize());
    },
    "test given TabMenu with four tabs when second removed then the indexes of the succeeding tabs is lowered by one": function () {
        var tabMenu = new api_ui_tab.TabMenu();
        var tab1 = new api_ui_tab.TabMenuItem("Tab1");
        var tab2 = new api_ui_tab.TabMenuItem("Tab2");
        var tab3 = new api_ui_tab.TabMenuItem("Tab3");
        var tab4 = new api_ui_tab.TabMenuItem("Tab4");
        tabMenu.addTab(tab1);
        tabMenu.addTab(tab2);
        tabMenu.addTab(tab3);
        tabMenu.addTab(tab4);
        assertEquals(0, tab1.getTabIndex());
        assertEquals(1, tab2.getTabIndex());
        assertEquals(2, tab3.getTabIndex());
        assertEquals(3, tab4.getTabIndex());
        tabMenu.removeTab(tab2);
        assertEquals(0, tab1.getTabIndex());
        assertEquals(1, tab3.getTabIndex());
        assertEquals(2, tab4.getTabIndex());
    },
    "test given TabMenu with three tabs and last tab is selected when last is removed then the second becomes the selected": function () {
        var tabMenu = new api_ui_tab.TabMenu();
        var tab1 = new api_ui_tab.TabMenuItem("Tab1");
        var tab2 = new api_ui_tab.TabMenuItem("Tab2");
        var tab3 = new api_ui_tab.TabMenuItem("Tab3");
        tabMenu.addTab(tab1);
        tabMenu.addTab(tab2);
        tabMenu.addTab(tab3);
        tabMenu.selectTab(2);
        assertEquals(2, tabMenu.getSelectedTabIndex());
        tabMenu.removeTab(tab3);
        assertEquals(1, tabMenu.getSelectedTabIndex());
    }
});
//@ sourceMappingURL=api-test.js.map
