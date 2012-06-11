/*

Siesta 1.0.8
Copyright(c) 2009-2012 Bryntum AB
http://bryntum.com/contact
http://bryntum.com/products/siesta/license

*/
;!function () {;
var Joose = {}

// configuration hash

Joose.C             = typeof JOOSE_CFG != 'undefined' ? JOOSE_CFG : {}

Joose.is_IE         = '\v' == 'v'
Joose.is_NodeJS     = Boolean(typeof process != 'undefined' && process.pid)


Joose.top           = Joose.is_NodeJS && global || this

Joose.stub          = function () {
    return function () { throw new Error("Modules can not be instantiated") }
}


Joose.VERSION       = ({ /*PKGVERSION*/VERSION : '3.50.1' }).VERSION


if (typeof module != 'undefined') module.exports = Joose
/*if (!Joose.is_NodeJS) */
this.Joose = Joose


// Static helpers for Arrays
Joose.A = {

    each : function (array, func, scope) {
        scope = scope || this
        
        for (var i = 0, len = array.length; i < len; i++) 
            if (func.call(scope, array[i], i) === false) return false
    },
    
    
    eachR : function (array, func, scope) {
        scope = scope || this

        for (var i = array.length - 1; i >= 0; i--) 
            if (func.call(scope, array[i], i) === false) return false
    },
    
    
    exists : function (array, value) {
        for (var i = 0, len = array.length; i < len; i++) if (array[i] == value) return true
            
        return false
    },
    
    
    map : function (array, func, scope) {
        scope = scope || this
        
        var res = []
        
        for (var i = 0, len = array.length; i < len; i++) 
            res.push( func.call(scope, array[i], i) )
            
        return res
    },
    

    grep : function (array, func) {
        var a = []
        
        Joose.A.each(array, function (t) {
            if (func(t)) a.push(t)
        })
        
        return a
    },
    
    
    remove : function (array, removeEle) {
        var a = []
        
        Joose.A.each(array, function (t) {
            if (t !== removeEle) a.push(t)
        })
        
        return a
    }
    
}

// Static helpers for Strings
Joose.S = {
    
    saneSplit : function (str, delimeter) {
        var res = (str || '').split(delimeter)
        
        if (res.length == 1 && !res[0]) res.shift()
        
        return res
    },
    

    uppercaseFirst : function (string) { 
        return string.substr(0, 1).toUpperCase() + string.substr(1, string.length - 1)
    },
    
    
    strToClass : function (name, top) {
        var current = top || Joose.top
        
        Joose.A.each(name.split('.'), function (segment) {
            if (current) 
                current = current[ segment ]
            else
                return false
        })
        
        return current
    }
}

var baseFunc    = function () {}

var enumProps   = [ 'hasOwnProperty', 'valueOf', 'toString', 'constructor' ]

var manualEnum  = true

for (var i in { toString : 1 }) manualEnum = false


// Static helpers for objects
Joose.O = {

    each : function (object, func, scope) {
        scope = scope || this
        
        for (var i in object) 
            if (func.call(scope, object[i], i) === false) return false
        
        if (manualEnum) 
            return Joose.A.each(enumProps, function (el) {
                
                if (object.hasOwnProperty(el)) return func.call(scope, object[el], el)
            })
    },
    
    
    eachOwn : function (object, func, scope) {
        scope = scope || this
        
        return Joose.O.each(object, function (value, name) {
            if (object.hasOwnProperty(name)) return func.call(scope, value, name)
        }, scope)
    },
    
    
    copy : function (source, target) {
        target = target || {}
        
        Joose.O.each(source, function (value, name) { target[name] = value })
        
        return target
    },
    
    
    copyOwn : function (source, target) {
        target = target || {}
        
        Joose.O.eachOwn(source, function (value, name) { target[name] = value })
        
        return target
    },
    
    
    getMutableCopy : function (object) {
        baseFunc.prototype = object
        
        return new baseFunc()
    },
    
    
    extend : function (target, source) {
        return Joose.O.copy(source, target)
    },
    
    
    isEmpty : function (object) {
        for (var i in object) if (object.hasOwnProperty(i)) return false
        
        return true
    },
    
    
    isInstance: function (obj) {
        return obj && obj.meta && obj.constructor == obj.meta.c
    },
    
    
    isClass : function (obj) {
        return obj && obj.meta && obj.meta.c == obj
    },
    
    
    wantArray : function (obj) {
        if (obj instanceof Array) return obj
        
        return [ obj ]
    },
    
    
    // this was a bug in WebKit, which gives typeof / / == 'function'
    // should be monitored and removed at some point in the future
    isFunction : function (obj) {
        return typeof obj == 'function' && obj.constructor != / /.constructor
    }
}


//initializers

Joose.I = {
    Array       : function () { return [] },
    Object      : function () { return {} },
    Function    : function () { return arguments.callee },
    Now         : function () { return new Date() }
};
Joose.Proto = Joose.stub()

Joose.Proto.Empty = Joose.stub()
    
Joose.Proto.Empty.meta = {};
;(function () {

    Joose.Proto.Object = Joose.stub()
    
    
    var SUPER = function () {
        var self = SUPER.caller
        
        if (self == SUPERARG) self = self.caller
        
        if (!self.SUPER) throw "Invalid call to SUPER"
        
        return self.SUPER[self.methodName].apply(this, arguments)
    }
    
    
    var SUPERARG = function () {
        return this.SUPER.apply(this, arguments[0])
    }
    
    
    
    Joose.Proto.Object.prototype = {
        
        SUPERARG : SUPERARG,
        SUPER : SUPER,
        
        INNER : function () {
            throw "Invalid call to INNER"
        },                
        
        
        BUILD : function (config) {
            return arguments.length == 1 && typeof config == 'object' && config || {}
        },
        
        
        initialize: function () {
        },
        
        
        toString: function () {
            return "a " + this.meta.name
        }
        
    }
        
    Joose.Proto.Object.meta = {
        constructor     : Joose.Proto.Object,
        
        methods         : Joose.O.copy(Joose.Proto.Object.prototype),
        attributes      : {}
    }
    
    Joose.Proto.Object.prototype.meta = Joose.Proto.Object.meta

})();
;(function () {

    Joose.Proto.Class = function () {
        return this.initialize(this.BUILD.apply(this, arguments)) || this
    }
    
    var bootstrap = {
        
        VERSION             : null,
        AUTHORITY           : null,
        
        constructor         : Joose.Proto.Class,
        superClass          : null,
        
        name                : null,
        
        attributes          : null,
        methods             : null,
        
        meta                : null,
        c                   : null,
        
        defaultSuperClass   : Joose.Proto.Object,
        
        
        BUILD : function (name, extend) {
            this.name = name
            
            return { __extend__ : extend || {} }
        },
        
        
        initialize: function (props) {
            var extend      = props.__extend__
            
            this.VERSION    = extend.VERSION
            this.AUTHORITY  = extend.AUTHORITY
            
            delete extend.VERSION
            delete extend.AUTHORITY
            
            this.c = this.extractConstructor(extend)
            
            this.adaptConstructor(this.c)
            
            if (extend.constructorOnly) {
                delete extend.constructorOnly
                return
            }
            
            this.construct(extend)
        },
        
        
        construct : function (extend) {
            if (!this.prepareProps(extend)) return
            
            var superClass = this.superClass = this.extractSuperClass(extend)
            
            this.processSuperClass(superClass)
            
            this.adaptPrototype(this.c.prototype)
            
            this.finalize(extend)
        },
        
        
        finalize : function (extend) {
            this.processStem(extend)
            
            this.extend(extend)
        },
        
        
        //if the extension returns false from this method it should re-enter 'construct'
        prepareProps : function (extend) {
            return true
        },
        
        
        extractConstructor : function (extend) {
            var res = extend.hasOwnProperty('constructor') ? extend.constructor : this.defaultConstructor()
            
            delete extend.constructor
            
            return res
        },
        
        
        extractSuperClass : function (extend) {
            if (extend.hasOwnProperty('isa') && !extend.isa) throw new Error("Attempt to inherit from undefined superclass [" + this.name + "]")
            
            var res = extend.isa || this.defaultSuperClass
            
            delete extend.isa
            
            return res
        },
        
        
        processStem : function () {
            var superMeta       = this.superClass.meta
            
            this.methods        = Joose.O.getMutableCopy(superMeta.methods || {})
            this.attributes     = Joose.O.getMutableCopy(superMeta.attributes || {})
        },
        
        
        initInstance : function (instance, props) {
            Joose.O.copyOwn(props, instance)
        },
        
        
        defaultConstructor: function () {
            return function (arg) {
                var BUILD = this.BUILD
                
                var args = BUILD && BUILD.apply(this, arguments) || arg || {}
                
                var thisMeta    = this.meta
                
                thisMeta.initInstance(this, args)
                
                return thisMeta.hasMethod('initialize') && this.initialize(args) || this
            }
        },
        
        
        processSuperClass: function (superClass) {
            var superProto      = superClass.prototype
            
            //non-Joose superclasses
            if (!superClass.meta) {
                
                var extend = Joose.O.copy(superProto)
                
                extend.isa = Joose.Proto.Empty
                // clear potential value in the `extend.constructor` to prevent it from being modified
                delete extend.constructor
                
                var meta = new this.defaultSuperClass.meta.constructor(null, extend)
                
                superClass.meta = superProto.meta = meta
                
                meta.c = superClass
            }
            
            this.c.prototype    = Joose.O.getMutableCopy(superProto)
            this.c.superClass   = superProto
        },
        
        
        adaptConstructor: function (c) {
            c.meta = this
            
            if (!c.hasOwnProperty('toString')) c.toString = function () { return this.meta.name }
        },
    
        
        adaptPrototype: function (proto) {
            //this will fix weird semantic of native "constructor" property to more intuitive (idea borrowed from Ext)
            proto.constructor   = this.c
            proto.meta          = this
        },
        
        
        addMethod: function (name, func) {
            func.SUPER = this.superClass.prototype
            
            //chrome don't allow to redefine the "name" property
            func.methodName = name
            
            this.methods[name] = func
            this.c.prototype[name] = func
        },
        
        
        addAttribute: function (name, init) {
            this.attributes[name] = init
            this.c.prototype[name] = init
        },
        
        
        removeMethod : function (name) {
            delete this.methods[name]
            delete this.c.prototype[name]
        },
    
        
        removeAttribute: function (name) {
            delete this.attributes[name]
            delete this.c.prototype[name]
        },
        
        
        hasMethod: function (name) { 
            return Boolean(this.methods[name])
        },
        
        
        hasAttribute: function (name) { 
            return this.attributes[name] !== undefined
        },
        
    
        hasOwnMethod: function (name) { 
            return this.hasMethod(name) && this.methods.hasOwnProperty(name)
        },
        
        
        hasOwnAttribute: function (name) { 
            return this.hasAttribute(name) && this.attributes.hasOwnProperty(name)
        },
        
        
        extend : function (props) {
            Joose.O.eachOwn(props, function (value, name) {
                if (name != 'meta' && name != 'constructor') 
                    if (Joose.O.isFunction(value) && !value.meta) 
                        this.addMethod(name, value) 
                    else 
                        this.addAttribute(name, value)
            }, this)
        },
        
        
        subClassOf : function (classObject, extend) {
            return this.subClass(extend, null, classObject)
        },
    
    
        subClass : function (extend, name, classObject) {
            extend      = extend        || {}
            extend.isa  = classObject   || this.c
            
            return new this.constructor(name, extend).c
        },
        
        
        instantiate : function () {
            var f = function () {}
            
            f.prototype = this.c.prototype
            
            var obj = new f()
            
            return this.c.apply(obj, arguments) || obj
        }
    }
    
    //micro bootstraping
    
    Joose.Proto.Class.prototype = Joose.O.getMutableCopy(Joose.Proto.Object.prototype)
    
    Joose.O.extend(Joose.Proto.Class.prototype, bootstrap)
    
    Joose.Proto.Class.prototype.meta = new Joose.Proto.Class('Joose.Proto.Class', bootstrap)
    
    
    
    Joose.Proto.Class.meta.addMethod('isa', function (someClass) {
        var f = function () {}
        
        f.prototype = this.c.prototype
        
        return new f() instanceof someClass
    })
})();
Joose.Managed = Joose.stub()

Joose.Managed.Property = new Joose.Proto.Class('Joose.Managed.Property', {
    
    name            : null,
    
    init            : null,
    value           : null,
    
    definedIn       : null,
    
    
    initialize : function (props) {
        Joose.Managed.Property.superClass.initialize.call(this, props)
        
        this.computeValue()
    },
    
    
    computeValue : function () {
        this.value = this.init
    },    
    
    
    //targetClass is still open at this stage
    preApply : function (targetClass) {
    },
    

    //targetClass is already open at this stage
    postUnApply : function (targetClass) {
    },
    
    
    apply : function (target) {
        target[this.name] = this.value
    },
    
    
    isAppliedTo : function (target) {
        return target[this.name] == this.value
    },
    
    
    unapply : function (from) {
        if (!this.isAppliedTo(from)) throw "Unapply of property [" + this.name + "] from [" + from + "] failed"
        
        delete from[this.name]
    },
    
    
    cloneProps : function () {
        return {
            name        : this.name, 
            init        : this.init,
            definedIn   : this.definedIn
        }
    },

    
    clone : function (name) {
        var props = this.cloneProps()
        
        props.name = name || props.name
        
        return new this.constructor(props)
    }
    
    
}).c;
Joose.Managed.Property.ConflictMarker = new Joose.Proto.Class('Joose.Managed.Property.ConflictMarker', {
    
    isa : Joose.Managed.Property,

    apply : function (target) {
        throw new Error("Attempt to apply ConflictMarker [" + this.name + "] to [" + target + "]")
    }
    
}).c;
Joose.Managed.Property.Requirement = new Joose.Proto.Class('Joose.Managed.Property.Requirement', {
    
    isa : Joose.Managed.Property,

    
    apply : function (target) {
        if (!target.meta.hasMethod(this.name)) 
            throw new Error("Requirement [" + this.name + "], defined in [" + this.definedIn.definedIn.name + "] is not satisfied for class [" + target + "]")
    },
    
    
    unapply : function (from) {
    }
    
}).c;
Joose.Managed.Property.Attribute = new Joose.Proto.Class('Joose.Managed.Property.Attribute', {
    
    isa : Joose.Managed.Property,
    
    slot                : null,
    
    
    initialize : function () {
        Joose.Managed.Property.Attribute.superClass.initialize.apply(this, arguments)
        
        this.slot = this.name
    },
    
    
    apply : function (target) {
        target.prototype[ this.slot ] = this.value
    },
    
    
    isAppliedTo : function (target) {
        return target.prototype[ this.slot ] == this.value
    },
    
    
    unapply : function (from) {
        if (!this.isAppliedTo(from)) throw "Unapply of property [" + this.name + "] from [" + from + "] failed"
        
        delete from.prototype[this.slot]
    },
    
    
    clearValue : function (instance) {
        delete instance[ this.slot ]
    },
    
    
    hasValue : function (instance) {
        return instance.hasOwnProperty(this.slot)
    },
        
        
    getRawValueFrom : function (instance) {
        return instance[ this.slot ]
    },
    
    
    setRawValueTo : function (instance, value) {
        instance[ this.slot ] = value
        
        return this
    }
    
}).c;
Joose.Managed.Property.MethodModifier = new Joose.Proto.Class('Joose.Managed.Property.MethodModifier', {
    
    isa : Joose.Managed.Property,

    
    prepareWrapper : function () {
        throw "Abstract method [prepareWrapper] of " + this + " was called"
    },
    
    
    apply : function (target) {
        var name            = this.name
        var targetProto     = target.prototype
        var isOwn           = targetProto.hasOwnProperty(name)
        var original        = targetProto[name]
        var superProto      = target.meta.superClass.prototype
        
        
        var originalCall = isOwn ? original : function () { 
            return superProto[name].apply(this, arguments) 
        }
        
        var methodWrapper = this.prepareWrapper({
            name            : name,
            modifier        : this.value, 
            
            isOwn           : isOwn,
            originalCall    : originalCall, 
            
            superProto      : superProto,
            
            target          : target
        })
        
        if (isOwn) methodWrapper.__ORIGINAL__ = original
        
        methodWrapper.__CONTAIN__   = this.value
        methodWrapper.__METHOD__    = this
        
        targetProto[name] = methodWrapper
    },
    
    
    isAppliedTo : function (target) {
        var targetCont = target.prototype[this.name]
        
        return targetCont && targetCont.__CONTAIN__ == this.value
    },
    
    
    unapply : function (from) {
        var name = this.name
        var fromProto = from.prototype
        var original = fromProto[name].__ORIGINAL__
        
        if (!this.isAppliedTo(from)) throw "Unapply of method [" + name + "] from class [" + from + "] failed"
        
        //if modifier was applied to own method - restore it
        if (original) 
            fromProto[name] = original
        //otherwise - just delete it, to reveal the inherited method 
        else
            delete fromProto[name]
    }
    
}).c;
Joose.Managed.Property.MethodModifier.Override = new Joose.Proto.Class('Joose.Managed.Property.MethodModifier.Override', {
    
    isa : Joose.Managed.Property.MethodModifier,

    
    prepareWrapper : function (params) {
        
        var modifier        = params.modifier
        var originalCall    = params.originalCall
        var superProto      = params.superProto
        var superMetaConst  = superProto.meta.constructor
        
        //call to Joose.Proto level, require some additional processing
        var isCallToProto = (superMetaConst == Joose.Proto.Class || superMetaConst == Joose.Proto.Object) && !(params.isOwn && originalCall.IS_OVERRIDE) 
        
        var original = originalCall
        
        if (isCallToProto) original = function () {
            var beforeSUPER = this.SUPER
            
            this.SUPER  = superProto.SUPER
            
            var res = originalCall.apply(this, arguments)
            
            this.SUPER = beforeSUPER
            
            return res
        }

        var override = function () {
            
            var beforeSUPER = this.SUPER
            
            this.SUPER  = original
            
            var res = modifier.apply(this, arguments)
            
            this.SUPER = beforeSUPER
            
            return res
        }
        
        override.IS_OVERRIDE = true
        
        return override
    }
    
    
}).c;
Joose.Managed.Property.MethodModifier.Put = new Joose.Proto.Class('Joose.Managed.Property.MethodModifier.Put', {
    
    isa : Joose.Managed.Property.MethodModifier.Override,


    prepareWrapper : function (params) {
        
        if (params.isOwn) throw "Method [" + params.name + "] is applying over something [" + params.originalCall + "] in class [" + params.target + "]"
        
        return Joose.Managed.Property.MethodModifier.Put.superClass.prepareWrapper.call(this, params)
    }
    
    
}).c;
Joose.Managed.Property.MethodModifier.After = new Joose.Proto.Class('Joose.Managed.Property.MethodModifier.After', {
    
    isa : Joose.Managed.Property.MethodModifier,

    
    prepareWrapper : function (params) {
        
        var modifier        = params.modifier
        var originalCall    = params.originalCall
        
        return function () {
            var res = originalCall.apply(this, arguments)
            modifier.apply(this, arguments)
            return res
        }
    }    

    
}).c;
Joose.Managed.Property.MethodModifier.Before = new Joose.Proto.Class('Joose.Managed.Property.MethodModifier.Before', {
    
    isa : Joose.Managed.Property.MethodModifier,

    
    prepareWrapper : function (params) {
        
        var modifier        = params.modifier
        var originalCall    = params.originalCall
        
        return function () {
            modifier.apply(this, arguments)
            return originalCall.apply(this, arguments)
        }
    }
    
}).c;
Joose.Managed.Property.MethodModifier.Around = new Joose.Proto.Class('Joose.Managed.Property.MethodModifier.Around', {
    
    isa : Joose.Managed.Property.MethodModifier,

    prepareWrapper : function (params) {
        
        var modifier        = params.modifier
        var originalCall    = params.originalCall
        
        var me
        
        var bound = function () {
            return originalCall.apply(me, arguments)
        }
            
        return function () {
            me = this
            
            var boundArr = [ bound ]
            boundArr.push.apply(boundArr, arguments)
            
            return modifier.apply(this, boundArr)
        }
    }
    
}).c;
Joose.Managed.Property.MethodModifier.Augment = new Joose.Proto.Class('Joose.Managed.Property.MethodModifier.Augment', {
    
    isa : Joose.Managed.Property.MethodModifier,

    
    prepareWrapper : function (params) {
        
        var AUGMENT = function () {
            
            //populate callstack to the most deep non-augment method
            var callstack = []
            
            var self = AUGMENT
            
            do {
                callstack.push(self.IS_AUGMENT ? self.__CONTAIN__ : self)
                
                self = self.IS_AUGMENT && (self.__ORIGINAL__ || self.SUPER[self.methodName])
            } while (self)
            
            
            //save previous INNER
            var beforeINNER = this.INNER
            
            //create new INNER
            this.INNER = function () {
                var innerCall = callstack.pop()
                
                return innerCall ? innerCall.apply(this, arguments) : undefined
            }
            
            //augment modifier results in hypotetical INNER call of the same method in subclass 
            var res = this.INNER.apply(this, arguments)
            
            //restore previous INNER chain
            this.INNER = beforeINNER
            
            return res
        }
        
        AUGMENT.methodName  = params.name
        AUGMENT.SUPER       = params.superProto
        AUGMENT.IS_AUGMENT  = true
        
        return AUGMENT
    }
    
}).c;
Joose.Managed.PropertySet = new Joose.Proto.Class('Joose.Managed.PropertySet', {
    
    isa                       : Joose.Managed.Property,

    properties                : null,
    
    propertyMetaClass         : Joose.Managed.Property,
    
    
    initialize : function (props) {
        Joose.Managed.PropertySet.superClass.initialize.call(this, props)
        
        //XXX this guards the meta roles :)
        this.properties = props.properties || {}
    },
    
    
    addProperty : function (name, props) {
        var metaClass = props.meta || this.propertyMetaClass
        delete props.meta
        
        props.definedIn     = this
        props.name          = name
        
        return this.properties[name] = new metaClass(props)
    },
    
    
    addPropertyObject : function (object) {
        return this.properties[object.name] = object
    },
    
    
    removeProperty : function (name) {
        var prop = this.properties[name]
        
        delete this.properties[name]
        
        return prop
    },
    
    
    haveProperty : function (name) {
        return this.properties[name] != null
    },
    

    haveOwnProperty : function (name) {
        return this.haveProperty(name) && this.properties.hasOwnProperty(name)
    },
    
    
    getProperty : function (name) {
        return this.properties[name]
    },
    
    
    //includes inherited properties (probably you wants 'eachOwn', which process only "own" (including consumed from Roles) properties) 
    each : function (func, scope) {
        Joose.O.each(this.properties, func, scope || this)
    },
    
    
    eachOwn : function (func, scope) {
        Joose.O.eachOwn(this.properties, func, scope || this)
    },
    
    
    //synonym for each
    eachAll : function (func, scope) {
        this.each(func, scope)
    },
    
    
    cloneProps : function () {
        var props = Joose.Managed.PropertySet.superClass.cloneProps.call(this)
        
        props.propertyMetaClass     = this.propertyMetaClass
        
        return props
    },
    
    
    clone : function (name) {
        var clone = this.cleanClone(name)
        
        clone.properties = Joose.O.copyOwn(this.properties)
        
        return clone
    },
    
    
    cleanClone : function (name) {
        var props = this.cloneProps()
        
        props.name = name || props.name
        
        return new this.constructor(props)
    },
    
    
    alias : function (what) {
        var props = this.properties
        
        Joose.O.each(what, function (aliasName, originalName) {
            var original = props[originalName]
            
            if (original) this.addPropertyObject(original.clone(aliasName))
        }, this)
    },
    
    
    exclude : function (what) {
        var props = this.properties
        
        Joose.A.each(what, function (name) {
            delete props[name]
        })
    },
    
    
    beforeConsumedBy : function () {
    },
    
    
    flattenTo : function (target) {
        var targetProps = target.properties
        
        this.eachOwn(function (property, name) {
            var targetProperty = targetProps[name]
            
            if (targetProperty instanceof Joose.Managed.Property.ConflictMarker) return
            
            if (!targetProps.hasOwnProperty(name) || targetProperty == null) {
                target.addPropertyObject(property)
                return
            }
            
            if (targetProperty == property) return
            
            target.removeProperty(name)
            target.addProperty(name, {
                meta : Joose.Managed.Property.ConflictMarker
            })
        }, this)
    },
    
    
    composeTo : function (target) {
        this.eachOwn(function (property, name) {
            if (!target.haveOwnProperty(name)) target.addPropertyObject(property)
        })
    },
    
    
    composeFrom : function () {
        if (!arguments.length) return
        
        var flattening = this.cleanClone()
        
        Joose.A.each(arguments, function (arg) {
            var isDescriptor    = !(arg instanceof Joose.Managed.PropertySet)
            var propSet         = isDescriptor ? arg.propertySet : arg
            
            propSet.beforeConsumedBy(this, flattening)
            
            if (isDescriptor) {
                if (arg.alias || arg.exclude)   propSet = propSet.clone()
                if (arg.alias)                  propSet.alias(arg.alias)
                if (arg.exclude)                propSet.exclude(arg.exclude)
            }
            
            propSet.flattenTo(flattening)
        }, this)
        
        flattening.composeTo(this)
    },
    
    
    preApply : function (target) {
        this.eachOwn(function (property) {
            property.preApply(target)
        })
    },
    
    
    apply : function (target) {
        this.eachOwn(function (property) {
            property.apply(target)
        })
    },
    
    
    unapply : function (from) {
        this.eachOwn(function (property) {
            property.unapply(from)
        })
    },
    
    
    postUnApply : function (target) {
        this.eachOwn(function (property) {
            property.postUnApply(target)
        })
    }
    
}).c
;
var __ID__ = 1


Joose.Managed.PropertySet.Mutable = new Joose.Proto.Class('Joose.Managed.PropertySet.Mutable', {
    
    isa                 : Joose.Managed.PropertySet,

    ID                  : null,
    
    derivatives         : null,
    
    opened              : null,
    
    composedFrom        : null,
    
    
    initialize : function (props) {
        Joose.Managed.PropertySet.Mutable.superClass.initialize.call(this, props)
        
        //initially opened
        this.opened             = 1
        this.derivatives        = {}
        this.ID                 = __ID__++
        this.composedFrom       = []
    },
    
    
    addComposeInfo : function () {
        this.ensureOpen()
        
        Joose.A.each(arguments, function (arg) {
            this.composedFrom.push(arg)
            
            var propSet = arg instanceof Joose.Managed.PropertySet ? arg : arg.propertySet
                
            propSet.derivatives[this.ID] = this
        }, this)
    },
    
    
    removeComposeInfo : function () {
        this.ensureOpen()
        
        Joose.A.each(arguments, function (arg) {
            
            var i = 0
            
            while (i < this.composedFrom.length) {
                var propSet = this.composedFrom[i]
                propSet = propSet instanceof Joose.Managed.PropertySet ? propSet : propSet.propertySet
                
                if (arg == propSet) {
                    delete propSet.derivatives[this.ID]
                    this.composedFrom.splice(i, 1)
                } else i++
            }
            
        }, this)
    },
    
    
    ensureOpen : function () {
        if (!this.opened) throw "Mutation of closed property set: [" + this.name + "]"
    },
    
    
    addProperty : function (name, props) {
        this.ensureOpen()
        
        return Joose.Managed.PropertySet.Mutable.superClass.addProperty.call(this, name, props)
    },
    

    addPropertyObject : function (object) {
        this.ensureOpen()
        
        return Joose.Managed.PropertySet.Mutable.superClass.addPropertyObject.call(this, object)
    },
    
    
    removeProperty : function (name) {
        this.ensureOpen()
        
        return Joose.Managed.PropertySet.Mutable.superClass.removeProperty.call(this, name)
    },
    
    
    composeFrom : function () {
        this.ensureOpen()
        
        return Joose.Managed.PropertySet.Mutable.superClass.composeFrom.apply(this, this.composedFrom)
    },
    
    
    open : function () {
        this.opened++
        
        if (this.opened == 1) {
        
            Joose.O.each(this.derivatives, function (propSet) {
                propSet.open()
            })
            
            this.deCompose()
        }
    },
    
    
    close : function () {
        if (!this.opened) throw "Unmatched 'close' operation on property set: [" + this.name + "]"
        
        if (this.opened == 1) {
            this.reCompose()
            
            Joose.O.each(this.derivatives, function (propSet) {
                propSet.close()
            })
        }
        this.opened--
    },
    
    
    reCompose : function () {
        this.composeFrom()
    },
    
    
    deCompose : function () {
        this.eachOwn(function (property, name) {
            if (property.definedIn != this) this.removeProperty(name)
        }, this)
    }
    
}).c;
Joose.Managed.StemElement = function () { throw "Modules may not be instantiated." }

Joose.Managed.StemElement.Attributes = new Joose.Proto.Class('Joose.Managed.StemElement.Attributes', {
    
    isa                     : Joose.Managed.PropertySet.Mutable,
    
    propertyMetaClass       : Joose.Managed.Property.Attribute
    
}).c
;
Joose.Managed.StemElement.Methods = new Joose.Proto.Class('Joose.Managed.StemElement.Methods', {
    
    isa : Joose.Managed.PropertySet.Mutable,
    
    propertyMetaClass : Joose.Managed.Property.MethodModifier.Put,

    
    preApply : function () {
    },
    
    
    postUnApply : function () {
    }
    
}).c;
Joose.Managed.StemElement.Requirements = new Joose.Proto.Class('Joose.Managed.StemElement.Requirements', {

    isa                     : Joose.Managed.PropertySet.Mutable,
    
    propertyMetaClass       : Joose.Managed.Property.Requirement,
    
    
    
    alias : function () {
    },
    
    
    exclude : function () {
    },
    
    
    flattenTo : function (target) {
        this.each(function (property, name) {
            if (!target.haveProperty(name)) target.addPropertyObject(property)
        })
    },
    
    
    composeTo : function (target) {
        this.flattenTo(target)
    },
    
    
    preApply : function () {
    },
    
    
    postUnApply : function () {
    }
    
}).c;
Joose.Managed.StemElement.MethodModifiers = new Joose.Proto.Class('Joose.Managed.StemElement.MethodModifiers', {

    isa                     : Joose.Managed.PropertySet.Mutable,
    
    propertyMetaClass       : null,
    
    
    addProperty : function (name, props) {
        var metaClass = props.meta
        delete props.meta
        
        props.definedIn         = this
        props.name              = name
        
        var modifier            = new metaClass(props)
        var properties          = this.properties
        
        if (!properties[name]) properties[ name ] = []
        
        properties[name].push(modifier)
        
        return modifier
    },
    

    addPropertyObject : function (object) {
        var name            = object.name
        var properties      = this.properties
        
        if (!properties[name]) properties[name] = []
        
        properties[name].push(object)
        
        return object
    },
    
    
    //remove only the last modifier
    removeProperty : function (name) {
        if (!this.haveProperty(name)) return undefined
        
        var properties      = this.properties
        var modifier        = properties[ name ].pop()
        
        //if all modifiers were removed - clearing the properties
        if (!properties[name].length) Joose.Managed.StemElement.MethodModifiers.superClass.removeProperty.call(this, name)
        
        return modifier
    },
    
    
    alias : function () {
    },
    
    
    exclude : function () {
    },
    
    
    flattenTo : function (target) {
        var targetProps = target.properties
        
        this.each(function (modifiersArr, name) {
            var targetModifiersArr = targetProps[name]
            
            if (targetModifiersArr == null) targetModifiersArr = targetProps[name] = []
            
            Joose.A.each(modifiersArr, function (modifier) {
                if (!Joose.A.exists(targetModifiersArr, modifier)) targetModifiersArr.push(modifier)
            })
            
        })
    },
    
    
    composeTo : function (target) {
        this.flattenTo(target)
    },

    
    deCompose : function () {
        this.each(function (modifiersArr, name) {
            var i = 0
            
            while (i < modifiersArr.length) 
                if (modifiersArr[i].definedIn != this) 
                    modifiersArr.splice(i, 1)
                else 
                    i++
        })
    },
    
    
    preApply : function (target) {
    },

    
    postUnApply : function (target) {
    },
    
    
    apply : function (target) {
        this.each(function (modifiersArr, name) {
            Joose.A.each(modifiersArr, function (modifier) {
                modifier.apply(target)
            })
        })
    },
    
    
    unapply : function (from) {
        this.each(function (modifiersArr, name) {
            for (var i = modifiersArr.length - 1; i >=0 ; i--) modifiersArr[i].unapply(from)
        })
    }
    
    
    
}).c;
Joose.Managed.PropertySet.Composition = new Joose.Proto.Class('Joose.Managed.PropertySet.Composition', {
    
    isa                         : Joose.Managed.PropertySet.Mutable,
    
    propertyMetaClass           : Joose.Managed.PropertySet.Mutable,
    
    processOrder                : null,

    
    each : function (func, scope) {
        var props   = this.properties
        var scope   = scope || this
        
        Joose.A.each(this.processOrder, function (name) {
            func.call(scope, props[name], name)
        })
    },
    
    
    eachR : function (func, scope) {
        var props   = this.properties
        var scope   = scope || this
        
        Joose.A.eachR(this.processOrder, function (name) {
            func.call(scope, props[name], name)
        })
        
        
//        var props           = this.properties
//        var processOrder    = this.processOrder
//        
//        for(var i = processOrder.length - 1; i >= 0; i--) 
//            func.call(scope || this, props[ processOrder[i] ], processOrder[i])
    },
    
    
    clone : function (name) {
        var clone = this.cleanClone(name)
        
        this.each(function (property) {
            clone.addPropertyObject(property.clone())
        })
        
        return clone
    },
    
    
    alias : function (what) {
        this.each(function (property) {
            property.alias(what)
        })
    },
    
    
    exclude : function (what) {
        this.each(function (property) {
            property.exclude(what)
        })
    },
    
    
    flattenTo : function (target) {
        var targetProps = target.properties
        
        this.each(function (property, name) {
            var subTarget = targetProps[name] || target.addProperty(name, {
                meta : property.constructor
            })
            
            property.flattenTo(subTarget)
        })
    },
    
    
    composeTo : function (target) {
        var targetProps = target.properties
        
        this.each(function (property, name) {
            var subTarget = targetProps[name] || target.addProperty(name, {
                meta : property.constructor
            })
            
            property.composeTo(subTarget)
        })
    },
    
    
    
    deCompose : function () {
        this.eachR(function (property) {
            property.open()
        })
        
        Joose.Managed.PropertySet.Composition.superClass.deCompose.call(this)
    },
    
    
    reCompose : function () {
        Joose.Managed.PropertySet.Composition.superClass.reCompose.call(this)
        
        this.each(function (property) {
            property.close()
        })
    },
    
    
    unapply : function (from) {
        this.eachR(function (property) {
            property.unapply(from)
        })
    }
    
}).c
;
Joose.Managed.Stem = new Joose.Proto.Class('Joose.Managed.Stem', {
    
    isa                  : Joose.Managed.PropertySet.Composition,
    
    targetMeta           : null,
    
    attributesMC         : Joose.Managed.StemElement.Attributes,
    methodsMC            : Joose.Managed.StemElement.Methods,
    requirementsMC       : Joose.Managed.StemElement.Requirements,
    methodsModifiersMC   : Joose.Managed.StemElement.MethodModifiers,
    
    processOrder         : [ 'attributes', 'methods', 'requirements', 'methodsModifiers' ],
    
    
    initialize : function (props) {
        Joose.Managed.Stem.superClass.initialize.call(this, props)
        
        var targetMeta = this.targetMeta
        
        this.addProperty('attributes', {
            meta : this.attributesMC,
            
            //it can be no 'targetMeta' in clones
            properties : targetMeta ? targetMeta.attributes : {}
        })
        
        
        this.addProperty('methods', {
            meta : this.methodsMC,
            
            properties : targetMeta ? targetMeta.methods : {}
        })
        
        
        this.addProperty('requirements', {
            meta : this.requirementsMC
        })
        
        
        this.addProperty('methodsModifiers', {
            meta : this.methodsModifiersMC
        })
    },
    
    
    reCompose : function () {
        var c       = this.targetMeta.c
        
        this.preApply(c)
        
        Joose.Managed.Stem.superClass.reCompose.call(this)
        
        this.apply(c)
    },
    
    
    deCompose : function () {
        var c       = this.targetMeta.c
        
        this.unapply(c)
        
        Joose.Managed.Stem.superClass.deCompose.call(this)
        
        this.postUnApply(c)
    }
    
    
}).c
;
Joose.Managed.Builder = new Joose.Proto.Class('Joose.Managed.Builder', {
    
    targetMeta          : null,
    
    
    _buildStart : function (targetMeta, props) {
        targetMeta.stem.open()
        
        Joose.A.each([ 'trait', 'traits', 'removeTrait', 'removeTraits', 'does', 'doesnot', 'doesnt' ], function (builder) {
            if (props[builder]) {
                this[builder](targetMeta, props[builder])
                delete props[builder]
            }
        }, this)
    },
    
    
    _extend : function (props) {
        if (Joose.O.isEmpty(props)) return
        
        var targetMeta = this.targetMeta
        
        this._buildStart(targetMeta, props)
        
        Joose.O.eachOwn(props, function (value, name) {
            var handler = this[name]
            
            if (!handler) throw new Error("Unknown builder [" + name + "] was used during extending of [" + targetMeta.c + "]")
            
            handler.call(this, targetMeta, value)
        }, this)
        
        this._buildComplete(targetMeta, props)
    },
    

    _buildComplete : function (targetMeta, props) {
        targetMeta.stem.close()
    },
    
    
    methods : function (targetMeta, info) {
        Joose.O.eachOwn(info, function (value, name) {
            targetMeta.addMethod(name, value)
        })
    },
    

    removeMethods : function (targetMeta, info) {
        Joose.A.each(info, function (name) {
            targetMeta.removeMethod(name)
        })
    },
    
    
    have : function (targetMeta, info) {
        Joose.O.eachOwn(info, function (value, name) {
            targetMeta.addAttribute(name, value)
        })
    },
    
    
    havenot : function (targetMeta, info) {
        Joose.A.each(info, function (name) {
            targetMeta.removeAttribute(name)
        })
    },
    

    havent : function (targetMeta, info) {
        this.havenot(targetMeta, info)
    },
    
    
    after : function (targetMeta, info) {
        Joose.O.each(info, function (value, name) {
            targetMeta.addMethodModifier(name, value, Joose.Managed.Property.MethodModifier.After)
        })
    },
    
    
    before : function (targetMeta, info) {
        Joose.O.each(info, function (value, name) {
            targetMeta.addMethodModifier(name, value, Joose.Managed.Property.MethodModifier.Before)
        })
    },
    
    
    override : function (targetMeta, info) {
        Joose.O.each(info, function (value, name) {
            targetMeta.addMethodModifier(name, value, Joose.Managed.Property.MethodModifier.Override)
        })
    },
    
    
    around : function (targetMeta, info) {
        Joose.O.each(info, function (value, name) {
            targetMeta.addMethodModifier(name, value, Joose.Managed.Property.MethodModifier.Around)
        })
    },
    
    
    augment : function (targetMeta, info) {
        Joose.O.each(info, function (value, name) {
            targetMeta.addMethodModifier(name, value, Joose.Managed.Property.MethodModifier.Augment)
        })
    },
    
    
    removeModifier : function (targetMeta, info) {
        Joose.A.each(info, function (name) {
            targetMeta.removeMethodModifier(name)
        })
    },
    
    
    does : function (targetMeta, info) {
        Joose.A.each(Joose.O.wantArray(info), function (desc) {
            targetMeta.addRole(desc)
        })
    },
    

    doesnot : function (targetMeta, info) {
        Joose.A.each(Joose.O.wantArray(info), function (desc) {
            targetMeta.removeRole(desc)
        })
    },
    
    
    doesnt : function (targetMeta, info) {
        this.doesnot(targetMeta, info)
    },
    
    
    trait : function () {
        this.traits.apply(this, arguments)
    },
    
    
    traits : function (targetMeta, info) {
        if (targetMeta.firstPass) return
        
        if (!targetMeta.meta.isDetached) throw "Can't apply trait to not detached class"
        
        targetMeta.meta.extend({
            does : info
        })
    },
    
    
    removeTrait : function () {
        this.removeTraits.apply(this, arguments)
    },
     
    
    removeTraits : function (targetMeta, info) {
        if (!targetMeta.meta.isDetached) throw "Can't remove trait from not detached class"
        
        targetMeta.meta.extend({
            doesnot : info
        })
    }
    
    
    
}).c;
Joose.Managed.Class = new Joose.Proto.Class('Joose.Managed.Class', {
    
    isa                         : Joose.Proto.Class,
    
    stem                        : null,
    stemClass                   : Joose.Managed.Stem,
    stemClassCreated            : false,
    
    builder                     : null,
    builderClass                : Joose.Managed.Builder,
    builderClassCreated         : false,
    
    isDetached                  : false,
    firstPass                   : true,
    
    // a special instance, which, when passed as 1st argument to constructor, signifies that constructor should
    // skips traits processing for this instance
    skipTraitsAnchor            : {},
    
    
    //build for metaclasses - collects traits from roles
    BUILD : function () {
        var sup = Joose.Managed.Class.superClass.BUILD.apply(this, arguments)
        
        var props   = sup.__extend__
        
        var traits = Joose.O.wantArray(props.trait || props.traits || [])
        delete props.trait
        delete props.traits
        
        Joose.A.each(Joose.O.wantArray(props.does || []), function (arg) {
            var role = (arg.meta instanceof Joose.Managed.Class) ? arg : arg.role
            
            if (role.meta.meta.isDetached) traits.push(role.meta.constructor)
        })
        
        if (traits.length) props.traits = traits 
        
        return sup
    },
    
    
    initInstance : function (instance, props) {
        Joose.O.each(this.attributes, function (attribute, name) {
            
            if (attribute instanceof Joose.Managed.Attribute) 
                attribute.initFromConfig(instance, props)
            else 
                if (props.hasOwnProperty(name)) instance[name] = props[name]
        })
    },
    
    
    // we are using the same constructor for usual and meta- classes
    defaultConstructor: function () {
        return function (skipTraitsAnchor, params) {
            
            var thisMeta    = this.meta
            var skipTraits  = skipTraitsAnchor == thisMeta.skipTraitsAnchor
            
            var BUILD       = this.BUILD
            
            var props       = BUILD && BUILD.apply(this, skipTraits ? params : arguments) || (skipTraits ? params[0] : skipTraitsAnchor) || {}
            
            
            // either looking for traits in __extend__ (meta-class) or in usual props (usual class)
            var extend  = props.__extend__ || props
            
            var traits = extend.trait || extend.traits
            
            if (traits || extend.detached) {
                delete extend.trait
                delete extend.traits
                delete extend.detached
                
                if (!skipTraits) {
                    var classWithTrait  = thisMeta.subClass({ does : traits || [] }, thisMeta.name)
                    var meta            = classWithTrait.meta
                    meta.isDetached     = true
                    
                    return meta.instantiate(thisMeta.skipTraitsAnchor, arguments)
                }
            }
            
            thisMeta.initInstance(this, props)
            
            return thisMeta.hasMethod('initialize') && this.initialize(props) || this
        }
    },
    
    
    finalize: function (extend) {
        Joose.Managed.Class.superClass.finalize.call(this, extend)
        
        this.stem.close()
        
        this.afterMutate()
    },
    
    
    processStem : function () {
        Joose.Managed.Class.superClass.processStem.call(this)
        
        this.builder    = new this.builderClass({ targetMeta : this })
        this.stem       = new this.stemClass({ name : this.name, targetMeta : this })
        
        var builderClass = this.getClassInAttribute('builderClass')
        
        if (builderClass) {
            this.builderClassCreated = true
            this.addAttribute('builderClass', this.subClassOf(builderClass))
        }
        
        
        var stemClass = this.getClassInAttribute('stemClass')
        
        if (stemClass) {
            this.stemClassCreated = true
            this.addAttribute('stemClass', this.subClassOf(stemClass))
        }
    },
    
    
    extend : function (props) {
        if (props.builder) {
            this.getBuilderTarget().meta.extend(props.builder)
            delete props.builder
        }
        
        if (props.stem) {
            this.getStemTarget().meta.extend(props.stem)
            delete props.stem
        }
        
        this.builder._extend(props)
        
        this.firstPass = false
        
        if (!this.stem.opened) this.afterMutate()
    },
    
    
    getBuilderTarget : function () {
        var builderClass = this.getClassInAttribute('builderClass')
        if (!builderClass) throw "Attempt to extend a builder on non-meta class"
        
        return builderClass
    },
    

    getStemTarget : function () {
        var stemClass = this.getClassInAttribute('stemClass')
        if (!stemClass) throw "Attempt to extend a stem on non-meta class"
        
        return stemClass
    },
    
    
    getClassInAttribute : function (attributeName) {
        var attrClass = this.getAttribute(attributeName)
        if (attrClass instanceof Joose.Managed.Property.Attribute) attrClass = attrClass.value
        
        return attrClass
    },
    
    
    addMethodModifier: function (name, func, type) {
        var props = {}
        
        props.init = func
        props.meta = type
        
        return this.stem.properties.methodsModifiers.addProperty(name, props)
    },
    
    
    removeMethodModifier: function (name) {
        return this.stem.properties.methodsModifiers.removeProperty(name)
    },
    
    
    addMethod: function (name, func, props) {
        props = props || {}
        props.init = func
        
        return this.stem.properties.methods.addProperty(name, props)
    },
    
    
    addAttribute: function (name, init, props) {
        props = props || {}
        props.init = init
        
        return this.stem.properties.attributes.addProperty(name, props)
    },
    
    
    removeMethod : function (name) {
        return this.stem.properties.methods.removeProperty(name)
    },

    
    removeAttribute: function (name) {
        return this.stem.properties.attributes.removeProperty(name)
    },
    
    
    hasMethod: function (name) {
        return this.stem.properties.methods.haveProperty(name)
    },
    
    
    hasAttribute: function (name) { 
        return this.stem.properties.attributes.haveProperty(name)
    },
    
    
    hasMethodModifiersFor : function (name) {
        return this.stem.properties.methodsModifiers.haveProperty(name)
    },
    
    
    hasOwnMethod: function (name) {
        return this.stem.properties.methods.haveOwnProperty(name)
    },
    
    
    hasOwnAttribute: function (name) { 
        return this.stem.properties.attributes.haveOwnProperty(name)
    },
    

    getMethod : function (name) {
        return this.stem.properties.methods.getProperty(name)
    },
    
    
    getAttribute : function (name) {
        return this.stem.properties.attributes.getProperty(name)
    },
    
    
    eachRole : function (roles, func, scope) {
        Joose.A.each(roles, function (arg, index) {
            var role = (arg.meta instanceof Joose.Managed.Class) ? arg : arg.role
            
            func.call(scope || this, arg, role, index)
        }, this)
    },
    
    
    addRole : function () {
        
        this.eachRole(arguments, function (arg, role) {
            
            this.beforeRoleAdd(role)
            
            var desc = arg
            
            //compose descriptor can contain 'alias' and 'exclude' fields, in this case actual reference should be stored
            //into 'propertySet' field
            if (role != arg) {
                desc.propertySet = role.meta.stem
                delete desc.role
            } else
                desc = desc.meta.stem
            
            this.stem.addComposeInfo(desc)
            
        }, this)
    },
    
    
    beforeRoleAdd : function (role) {
        var roleMeta = role.meta
        
        if (roleMeta.builderClassCreated) this.getBuilderTarget().meta.extend({
            does : [ roleMeta.getBuilderTarget() ]
        })
        
        if (roleMeta.stemClassCreated) this.getStemTarget().meta.extend({
            does : [ roleMeta.getStemTarget() ]
        })
        
        if (roleMeta.meta.isDetached && !this.firstPass) this.builder.traits(this, roleMeta.constructor)
    },
    
    
    beforeRoleRemove : function (role) {
        var roleMeta = role.meta
        
        if (roleMeta.builderClassCreated) this.getBuilderTarget().meta.extend({
            doesnt : [ roleMeta.getBuilderTarget() ]
        })
        
        if (roleMeta.stemClassCreated) this.getStemTarget().meta.extend({
            doesnt : [ roleMeta.getStemTarget() ]
        })
        
        if (roleMeta.meta.isDetached && !this.firstPass) this.builder.removeTraits(this, roleMeta.constructor)
    },
    
    
    removeRole : function () {
        this.eachRole(arguments, function (arg, role) {
            this.beforeRoleRemove(role)
            
            this.stem.removeComposeInfo(role.meta.stem)
        }, this)
    },
    
    
    getRoles : function () {
        
        return Joose.A.map(this.stem.composedFrom, function (composeDesc) {
            //compose descriptor can contain 'alias' and 'exclude' fields, in this case actual reference is stored
            //into 'propertySet' field
            if (!(composeDesc instanceof Joose.Managed.PropertySet)) return composeDesc.propertySet
            
            return composeDesc.targetMeta.c
        })
    },
    
    
    does : function (role) {
        var myRoles = this.getRoles()
        
        for (var i = 0; i < myRoles.length; i++) if (role == myRoles[i]) return true
        for (var i = 0; i < myRoles.length; i++) if (myRoles[i].meta.does(role)) return true
        
        var superMeta = this.superClass.meta
        
        // considering the case of inheriting from non-Joose classes
        if (this.superClass != Joose.Proto.Empty && superMeta && superMeta.meta && superMeta.meta.hasMethod('does')) return superMeta.does(role)
        
        return false
    },
    
    
    getMethods : function () {
        return this.stem.properties.methods
    },
    
    
    getAttributes : function () {
        return this.stem.properties.attributes
    },
    
    
    afterMutate : function () {
    },
    
    
    getCurrentMethod : function () {
        for (var wrapper = arguments.callee.caller, count = 0; wrapper && count < 5; wrapper = wrapper.caller, count++)
            if (wrapper.__METHOD__) return wrapper.__METHOD__
        
        return null
    }
    
    
}).c;
Joose.Managed.Role = new Joose.Managed.Class('Joose.Managed.Role', {
    
    isa                         : Joose.Managed.Class,
    
    have : {
        defaultSuperClass       : Joose.Proto.Empty,
        
        builderRole             : null,
        stemRole                : null
    },
    
    
    methods : {
        
        defaultConstructor : function () {
            return function () {
                throw new Error("Roles cant be instantiated")
            }
        },
        

        processSuperClass : function () {
            if (this.superClass != this.defaultSuperClass) throw new Error("Roles can't inherit from anything")
        },
        
        
        getBuilderTarget : function () {
            if (!this.builderRole) {
                this.builderRole = new this.constructor().c
                this.builderClassCreated = true
            }
            
            return this.builderRole
        },
        
    
        getStemTarget : function () {
            if (!this.stemRole) {
                this.stemRole = new this.constructor().c
                this.stemClassCreated = true
            }
            
            return this.stemRole
        },
        
    
        addRequirement : function (methodName) {
            this.stem.properties.requirements.addProperty(methodName, {})
        }
        
    },
    

    stem : {
        methods : {
            
            apply : function () {
            },
            
            
            unapply : function () {
            }
        }
    },
    
    
    builder : {
        methods : {
            requires : function (targetClassMeta, info) {
                Joose.A.each(Joose.O.wantArray(info), function (methodName) {
                    targetClassMeta.addRequirement(methodName)
                }, this)
            }
        }
    }
    
}).c;
Joose.Managed.Attribute = new Joose.Managed.Class('Joose.Managed.Attribute', {
    
    isa : Joose.Managed.Property.Attribute,
    
    have : {
        is              : null,
        
        builder         : null,
        
        isPrivate       : false,
        
        role            : null,
        
        publicName      : null,
        setterName      : null,
        getterName      : null,
        
        //indicates the logical readableness/writeableness of the attribute
        readable        : false,
        writeable       : false,
        
        //indicates the physical presense of the accessor (may be absent for "combined" accessors for example)
        hasGetter       : false,
        hasSetter       : false,
        
        required        : false,
        
        canInlineSetRaw : true,
        canInlineGetRaw : true
    },
    
    
    after : {
        initialize : function () {
            var name = this.name
            
            this.publicName = name.replace(/^_+/, '')
            
            this.slot = this.isPrivate ? '$$' + name : name
            
            this.setterName = this.setterName || this.getSetterName()
            this.getterName = this.getterName || this.getGetterName()
            
            this.readable  = this.hasGetter = /^r/i.test(this.is)
            this.writeable = this.hasSetter = /^.w/i.test(this.is)
        }
    },
    
    
    override : {
        
        computeValue : function () {
            var init    = this.init
            
            if (Joose.O.isClass(init) || !Joose.O.isFunction(init)) this.SUPER()
        },
        
        
        preApply : function (targetClass) {
            targetClass.meta.extend({
                methods : this.getAccessorsFor(targetClass)
            })
        },
        
        
        postUnApply : function (from) {
            from.meta.extend({
                removeMethods : this.getAccessorsFrom(from)
            })
        }
        
    },
    
    
    methods : {
        
        getAccessorsFor : function (targetClass) {
            var targetMeta = targetClass.meta
            var setterName = this.setterName
            var getterName = this.getterName
            
            var methods = {}
            
            if (this.hasSetter && !targetMeta.hasMethod(setterName)) {
                methods[setterName] = this.getSetter()
                methods[setterName].ACCESSOR_FROM = this
            }
            
            if (this.hasGetter && !targetMeta.hasMethod(getterName)) {
                methods[getterName] = this.getGetter()
                methods[getterName].ACCESSOR_FROM = this
            }
            
            return methods
        },
        
        
        getAccessorsFrom : function (from) {
            var targetMeta = from.meta
            var setterName = this.setterName
            var getterName = this.getterName
            
            var setter = this.hasSetter && targetMeta.getMethod(setterName)
            var getter = this.hasGetter && targetMeta.getMethod(getterName)
            
            var removeMethods = []
            
            if (setter && setter.value.ACCESSOR_FROM == this) removeMethods.push(setterName)
            if (getter && getter.value.ACCESSOR_FROM == this) removeMethods.push(getterName)
            
            return removeMethods
        },
        
        
        getGetterName : function () {
            return 'get' + Joose.S.uppercaseFirst(this.publicName)
        },


        getSetterName : function () {
            return 'set' + Joose.S.uppercaseFirst(this.publicName)
        },
        
        
        getSetter : function () {
            var me      = this
            var slot    = me.slot
            
            if (me.canInlineSetRaw)
                return function (value) {
                    this[ slot ] = value
                    
                    return this
                }
            else
                return function () {
                    return me.setRawValueTo.apply(this, arguments)
                }
        },
        
        
        getGetter : function () {
            var me      = this
            var slot    = me.slot
            
            if (me.canInlineGetRaw)
                return function (value) {
                    return this[ slot ]
                }
            else
                return function () {
                    return me.getRawValueFrom.apply(this, arguments)
                }
        },
        
        
        getValueFrom : function (instance) {
            var getterName      = this.getterName
            
            if (this.readable && instance.meta.hasMethod(getterName)) return instance[ getterName ]()
            
            return this.getRawValueFrom(instance)
        },
        
        
        setValueTo : function (instance, value) {
            var setterName      = this.setterName
            
            if (this.writeable && instance.meta.hasMethod(setterName)) 
                instance[ setterName ](value)
            else
                this.setRawValueTo(instance, value)
        },
        
        
        initFromConfig : function (instance, config) {
            var name            = this.name
            
            var value, isSet = false
            
            if (config.hasOwnProperty(name)) {
                value = config[name]
                isSet = true
            } else {
                var init    = this.init
                
                // simple function (not class) has been used as "init" value
                if (Joose.O.isFunction(init) && !Joose.O.isClass(init)) {
                    
                    value = init.call(instance, config, name)
                    
                    isSet = true
                    
                } else if (this.builder) {
                    
                    value = instance[ this.builder.replace(/^this\./, '') ](config, name)
                    isSet = true
                }
            }
            
            if (isSet)
                this.setRawValueTo(instance, value)
            else 
                if (this.required) throw new Error("Required attribute [" + name + "] is missed during initialization of " + instance)
        }
    }

}).c
;
Joose.Managed.Attribute.Builder = new Joose.Managed.Role('Joose.Managed.Attribute.Builder', {
    
    
    have : {
        defaultAttributeClass : Joose.Managed.Attribute
    },
    
    builder : {
        
        methods : {
            
            has : function (targetClassMeta, info) {
                Joose.O.eachOwn(info, function (props, name) {
                    if (typeof props != 'object' || props == null || props.constructor == / /.constructor) props = { init : props }
                    
                    props.meta = props.meta || targetClassMeta.defaultAttributeClass
                    
                    if (/^__/.test(name)) {
                        name = name.replace(/^_+/, '')
                        
                        props.isPrivate = true
                    }
                    
                    targetClassMeta.addAttribute(name, props.init, props)
                }, this)
            },
            
            
            hasnot : function (targetClassMeta, info) {
                this.havenot(targetClassMeta, info)
            },
            
            
            hasnt : function (targetClassMeta, info) {
                this.hasnot(targetClassMeta, info)
            }
        }
            
    }
    
}).c
;
Joose.Managed.My = new Joose.Managed.Role('Joose.Managed.My', {
    
    have : {
        myClass                         : null,
        
        needToReAlias                   : false
    },
    
    
    methods : {
        createMy : function (extend) {
            var thisMeta = this.meta
            var isRole = this instanceof Joose.Managed.Role
            
            var myExtend = extend.my || {}
            delete extend.my
            
            // Symbiont will generally have the same meta class as its hoster, excepting the cases, when the superclass also have the symbiont. 
            // In such cases, the meta class for symbiont will be inherited (unless explicitly specified)
            
            var superClassMy    = this.superClass.meta.myClass
            
            if (!isRole && !myExtend.isa && superClassMy) myExtend.isa = superClassMy
            

            if (!myExtend.meta && !myExtend.isa) myExtend.meta = this.constructor
            
            var createdClass    = this.myClass = Class(myExtend)
            
            var c               = this.c
            
            c.prototype.my      = c.my = isRole ? createdClass : new createdClass({ HOST : c })
            
            this.needToReAlias = true
        },
        
        
        aliasStaticMethods : function () {
            this.needToReAlias = false
            
            var c           = this.c
            var myProto     = this.myClass.prototype
            
            Joose.O.eachOwn(c, function (property, name) {
                if (property.IS_ALIAS) delete c[ name ] 
            })
            
            this.myClass.meta.stem.properties.methods.each(function (method, name) {
                
                if (!c[ name ])
                    (c[ name ] = function () {
                        return myProto[ name ].apply(c.my, arguments)
                    }).IS_ALIAS = true
            })
        }
    },
    
    
    override : {
        
        extend : function (props) {
            var myClass = this.myClass
            
            if (!myClass && this.superClass.meta.myClass) this.createMy(props)
            
            if (props.my) {
                if (!myClass) 
                    this.createMy(props)
                else {
                    this.needToReAlias = true
                    
                    myClass.meta.extend(props.my)
                    delete props.my
                }
            }
            
            this.SUPER(props)
            
            if (this.needToReAlias && !(this instanceof Joose.Managed.Role)) this.aliasStaticMethods()
        }  
    },
    
    
    before : {
        
        addRole : function () {
            var myStem
            
            Joose.A.each(arguments, function (arg) {
                
                if (!arg) throw new Error("Attempt to consume an undefined Role into [" + this.name + "]")
                
                //instanceof Class to allow treat classes as roles
                var role = (arg.meta instanceof Joose.Managed.Class) ? arg : arg.role
                
                if (role.meta.meta.hasAttribute('myClass') && role.meta.myClass) {
                    
                    if (!this.myClass) {
                        this.createMy({
                            my : {
                                does : role.meta.myClass
                            }
                        })
                        return
                    }
                    
                    myStem = this.myClass.meta.stem
                    if (!myStem.opened) myStem.open()
                    
                    myStem.addComposeInfo(role.my.meta.stem)
                }
            }, this)
            
            if (myStem) {
                myStem.close()
                
                this.needToReAlias = true
            }
        },
        
        
        removeRole : function () {
            if (!this.myClass) return
            
            var myStem = this.myClass.meta.stem
            myStem.open()
            
            Joose.A.each(arguments, function (role) {
                if (role.meta.meta.hasAttribute('myClass') && role.meta.myClass) {
                    myStem.removeComposeInfo(role.my.meta.stem)
                    
                    this.needToReAlias = true
                }
            }, this)
            
            myStem.close()
        }
        
    }
    
}).c;
Joose.Namespace = Joose.stub()

Joose.Namespace.Able = new Joose.Managed.Role('Joose.Namespace.Able', {

    have : {
        bodyFunc                : null
    },
    
    
    before : {
        extend : function (extend) {
            if (extend.body) {
                this.bodyFunc = extend.body
                delete extend.body
            }
        }
    },
    
    
    after: {
        
        afterMutate : function () {
            var bodyFunc = this.bodyFunc
            delete this.bodyFunc
            
            if (bodyFunc) Joose.Namespace.Manager.my.executeIn(this.c, bodyFunc)
        }
    }
    
}).c;
Joose.Managed.Bootstrap = new Joose.Managed.Role('Joose.Managed.Bootstrap', {
    
    does   : [ Joose.Namespace.Able, Joose.Managed.My, Joose.Managed.Attribute.Builder ]
    
}).c
;
Joose.Meta = Joose.stub()


Joose.Meta.Object = new Joose.Proto.Class('Joose.Meta.Object', {
    
    isa             : Joose.Proto.Object
    
}).c


;
Joose.Meta.Class = new Joose.Managed.Class('Joose.Meta.Class', {
    
    isa                         : Joose.Managed.Class,
    
    does                        : Joose.Managed.Bootstrap,
    
    have : {
        defaultSuperClass       : Joose.Meta.Object
    }
    
}).c

;
Joose.Meta.Role = new Joose.Meta.Class('Joose.Meta.Role', {
    
    isa                         : Joose.Managed.Role,
    
    does                        : Joose.Managed.Bootstrap
    
}).c;
Joose.Namespace.Keeper = new Joose.Meta.Class('Joose.Namespace.Keeper', {
    
    isa         : Joose.Meta.Class,
    
    have        : {
        externalConstructor             : null
    },
    
    
    methods: {
        
        defaultConstructor: function () {
            
            return function () {
                //constructors should assume that meta is attached to 'arguments.callee' (not to 'this') 
                var thisMeta = arguments.callee.meta
                
                if (thisMeta instanceof Joose.Namespace.Keeper) throw new Error("Module [" + thisMeta.c + "] may not be instantiated. Forgot to 'use' the class with the same name?")
                
                var externalConstructor = thisMeta.externalConstructor
                
                if (typeof externalConstructor == 'function') {
                    
                    externalConstructor.meta = thisMeta
                    
                    return externalConstructor.apply(this, arguments)
                }
                
                throw "NamespaceKeeper of [" + thisMeta.name + "] was planted incorrectly."
            }
        },
        
        
        //withClass should be not constructed yet on this stage (see Joose.Proto.Class.construct)
        //it should be on the 'constructorOnly' life stage (should already have constructor)
        plant: function (withClass) {
            var keeper = this.c
            
            keeper.meta = withClass.meta
            
            keeper.meta.c = keeper
            keeper.meta.externalConstructor = withClass
        }
    }
    
}).c


;
Joose.Namespace.Manager = new Joose.Managed.Class('Joose.Namespace.Manager', {
    
    have : {
        current     : null
    },
    
    
    methods : {
        
        initialize : function () {
            this.current    = [ Joose.top ]
        },
        
        
        getCurrent: function () {
            return this.current[0]
        },
        
        
        executeIn : function (ns, func) {
            var current = this.current
            
            current.unshift(ns)
            var res = func.call(ns, ns)
            current.shift()
            
            return res
        },
        
        
        earlyCreate : function (name, metaClass, props) {
            props.constructorOnly = true
            
            return new metaClass(name, props).c
        },
        
        
        //this function establishing the full "namespace chain" (including the last element)
        create : function (nsName, metaClass, extend) {
            
            //if no name provided, then we creating an anonymous class, so just skip all the namespace manipulations
            if (!nsName) return new metaClass(nsName, extend).c
            
            var me = this
            
            if (/^\./.test(nsName)) return this.executeIn(Joose.top, function () {
                return me.create(nsName.replace(/^\./, ''), metaClass, extend)
            })
            
            var props   = extend || {}
            
            var parts   = Joose.S.saneSplit(nsName, '.')
            var object  = this.getCurrent()
            var soFar   = object == Joose.top ? [] : Joose.S.saneSplit(object.meta.name, '.')
            
            for (var i = 0; i < parts.length; i++) {
                var part        = parts[i]
                var isLast      = i == parts.length - 1
                
                if (part == "meta" || part == "my" || !part) throw "Module name [" + nsName + "] may not include a part called 'meta' or 'my' or empty part."
                
                var cur =   object[part]
                
                soFar.push(part)
                
                var soFarName       = soFar.join(".")
                var needFinalize    = false
                var nsKeeper
                
                // if the namespace segment is empty
                if (typeof cur == "undefined") {
                    if (isLast) {
                        // perform "early create" which just fills the namespace segment with right constructor
                        // this allows us to have a right constructor in the namespace segment when the `body` will be called
                        nsKeeper        = this.earlyCreate(soFarName, metaClass, props)
                        needFinalize    = true
                    } else
                        nsKeeper        = new Joose.Namespace.Keeper(soFarName).c
                    
                    object[part] = nsKeeper
                    
                    cur = nsKeeper
                    
                } else if (isLast && cur && cur.meta) {
                    
                    var currentMeta = cur.meta
                    
                    if (metaClass == Joose.Namespace.Keeper)
                        //`Module` over something case - extend the original
                        currentMeta.extend(props)
                    else {
                        
                        if (currentMeta instanceof Joose.Namespace.Keeper) {
                            
                            currentMeta.plant(this.earlyCreate(soFarName, metaClass, props))
                            
                            needFinalize = true
                        } else
                            throw new Error("Double declaration of [" + soFarName + "]")
                    }
                    
                } else 
                    if (isLast && !(cur && cur.meta && cur.meta.meta)) throw "Trying to setup module " + soFarName + " failed. There is already something: " + cur

                // hook to allow embedd resource into meta
                if (isLast) this.prepareMeta(cur.meta)
                    
                if (needFinalize) cur.meta.construct(props)
                    
                object = cur
            }
            
            return object
        },
        
        
        prepareMeta : function () {
        },
        
        
        prepareProperties : function (name, props, defaultMeta, callback) {
            if (name && typeof name != 'string') {
                props   = name
                name    = null
            }
            
            var meta
            
            if (props && props.meta) {
                meta = props.meta
                delete props.meta
            }
            
            if (!meta)
                if (props && typeof props.isa == 'function' && props.isa.meta)
                    meta = props.isa.meta.constructor
                else
                    meta = defaultMeta
            
            return callback.call(this, name, meta, props)
        },
        
        
        getDefaultHelperFor : function (metaClass) {
            var me = this
            
            return function (name, props) {
                return me.prepareProperties(name, props, metaClass, function (name, meta, props) {
                    return me.create(name, meta, props)
                })
            }
        },
        
        
        register : function (helperName, metaClass, func) {
            var me = this
            
            if (this.meta.hasMethod(helperName)) {
                
                var helper = function () {
                    return me[ helperName ].apply(me, arguments)
                }
                
                if (!Joose.top[ helperName ])   Joose.top[ helperName ]         = helper
                if (!Joose[ helperName ])       Joose[ helperName ]             = helper
                
                if (Joose.is_NodeJS && typeof exports != 'undefined')            exports[ helperName ]    = helper
                
            } else {
                var methods = {}
                
                methods[ helperName ] = func || this.getDefaultHelperFor(metaClass)
                
                this.meta.extend({
                    methods : methods
                })
                
                this.register(helperName)
            }
        },
        
        
        Module : function (name, props) {
            return this.prepareProperties(name, props, Joose.Namespace.Keeper, function (name, meta, props) {
                if (typeof props == 'function') props = { body : props }    
                
                return this.create(name, meta, props)
            })
        }
    }
    
}).c

Joose.Namespace.Manager.my = new Joose.Namespace.Manager()

Joose.Namespace.Manager.my.register('Class', Joose.Meta.Class)
Joose.Namespace.Manager.my.register('Role', Joose.Meta.Role)
Joose.Namespace.Manager.my.register('Module')


// for the rest of the package
var Class       = Joose.Class
var Role        = Joose.Role
;
Role('Joose.Attribute.Delegate', {
    
    have : {
        handles : null
    },
    
    
    override : {
        
        eachDelegate : function (handles, func, scope) {
            if (typeof handles == 'string') return func.call(scope, handles, handles)
            
            if (handles instanceof Array)
                return Joose.A.each(handles, function (delegateTo) {
                    
                    func.call(scope, delegateTo, delegateTo)
                })
                
            if (handles === Object(handles))
                Joose.O.eachOwn(handles, function (delegateTo, handleAs) {
                    
                    func.call(scope, handleAs, delegateTo)
                })
        },
        
        
        getAccessorsFor : function (targetClass) {
            var targetMeta  = targetClass.meta
            var methods     = this.SUPER(targetClass)
            
            var me      = this
            
            this.eachDelegate(this.handles, function (handleAs, delegateTo) {
                
                if (!targetMeta.hasMethod(handleAs)) {
                    var handler = methods[ handleAs ] = function () {
                        var attrValue = me.getValueFrom(this)
                        
                        return attrValue[ delegateTo ].apply(attrValue, arguments)
                    }
                    
                    handler.ACCESSOR_FROM = me
                }
            })
            
            return methods
        },
        
        
        getAccessorsFrom : function (from) {
            var methods = this.SUPER(from)
            
            var me          = this
            var targetMeta  = from.meta
            
            this.eachDelegate(this.handles, function (handleAs) {
                
                var handler = targetMeta.getMethod(handleAs)
                
                if (handler && handler.value.ACCESSOR_FROM == me) methods.push(handleAs)
            })
            
            return methods
        }
    }
})

;
Role('Joose.Attribute.Trigger', {
    
    have : {
        trigger        : null
    }, 

    
    after : {
        initialize : function() {
            if (this.trigger) {
                if (!this.writeable) throw new Error("Can't use `trigger` for read-only attributes")
                
                this.hasSetter = true
            }
        }
    },
    
    
    override : {
        
        getSetter : function() {
            var original    = this.SUPER()
            var trigger     = this.trigger
            
            if (!trigger) return original
            
            var me      = this
            var init    = Joose.O.isFunction(me.init) ? null : me.init
            
            return function () {
                var oldValue    = me.hasValue(this) ? me.getValueFrom(this) : init
                
                var res         = original.apply(this, arguments)
                
                trigger.call(this, me.getValueFrom(this), oldValue)
                
                return res
            }
        }
    }
})    

;
Role('Joose.Attribute.Lazy', {
    
    
    have : {
        lazy        : null
    }, 
    
    
    before : {
        computeValue : function () {
            if (typeof this.init == 'function' && this.lazy) {
                this.lazy = this.init    
                delete this.init    
            }
        }
    },
    
    
    after : {
        initialize : function () {
            if (this.lazy) this.readable = this.hasGetter = true
        }
    },
    
    
    override : {
        
        getGetter : function () {
            var original    = this.SUPER()
            var lazy        = this.lazy
            
            if (!lazy) return original
            
            var me      = this    
            
            return function () {
                if (!me.hasValue(this)) {
                    var initializer = typeof lazy == 'function' ? lazy : this[ lazy.replace(/^this\./, '') ]
                    
                    me.setValueTo(this, initializer.apply(this, arguments))
                }
                
                return original.call(this)    
            }
        }
    }
})

;
Role('Joose.Attribute.Accessor.Combined', {
    
    
    have : {
        isCombined        : false
    }, 
    
    
    after : {
        initialize : function() {
            this.isCombined = this.isCombined || /..c/i.test(this.is)
            
            if (this.isCombined) {
                this.slot = '$$' + this.name
                
                this.hasGetter = true
                this.hasSetter = false
                
                this.setterName = this.getterName = this.publicName
            }
        }
    },
    
    
    override : {
        
        getGetter : function() {
            var getter    = this.SUPER()
            
            if (!this.isCombined) return getter
            
            var setter    = this.getSetter()
            
            var me = this
            
            return function () {
                
                if (!arguments.length) {
                    if (me.readable) return getter.call(this)
                    throw new Error("Call to getter of unreadable attribute: [" + me.name + "]")
                }
                
                if (me.writeable) return setter.apply(this, arguments)
                
                throw new Error("Call to setter of read-only attribute: [" + me.name + "]")    
            }
        }
    }
    
})

;
Joose.Managed.Attribute.meta.extend({
    does : [ Joose.Attribute.Delegate, Joose.Attribute.Trigger, Joose.Attribute.Lazy, Joose.Attribute.Accessor.Combined ]
})            

;
Role('Joose.Meta.Singleton', {
    
    has : {
        forceInstance           : Joose.I.Object,
        instance                : null
    },
    
    
    
    override : {
        
        defaultConstructor : function () {
            var meta        = this
            var previous    = this.SUPER()
            
            this.adaptConstructor(previous)
            
            return function (forceInstance, params) {
                if (forceInstance == meta.forceInstance) return previous.apply(this, params) || this
                
                var instance = meta.instance
                
                if (instance) {
                    if (meta.hasMethod('configure')) instance.configure.apply(instance, arguments)
                } else
                    meta.instance = new meta.c(meta.forceInstance, arguments)
                    
                return meta.instance
            }
        }        
    }
    

})


Joose.Namespace.Manager.my.register('Singleton', Class({
    isa     : Joose.Meta.Class,
    meta    : Joose.Meta.Class,
    
    does    : Joose.Meta.Singleton
}))
;
;
}();;
;
Class('Scope.Provider', {
    
    /*PKGVERSION*/VERSION : 0.12,
    
    has     : {
        scope               : null,
        
        seedingCode         : null,
        seedingScript       : null,
        
        preload             : {
            is      : 'ro',
            init    : Joose.I.Array
        },
        
        cleanupCallback     : null
    },
    
        
    methods : {
        
        isCSS : function (url) {
            return /\.css(\?.*)?$/i.test(url)
        },
        
        
        isAlreadySetUp : function () {
            return Boolean(this.scope)
        },
        
        
        addPreload : function (preloadDesc) {
            if (this.isAlreadySetUp()) throw new Error("Can't use `addPreload` - scope is already setup. Use `runCode/runScript` instead")
            
            if (typeof preloadDesc == 'string')
                
                if (this.isCSS(preloadDesc)) 
                    preloadDesc = {
                        type        : 'css',
                        url         : preloadDesc
                    }
                else
                    preloadDesc = {
                        type        : 'js',
                        url         : preloadDesc
                    }
            else
            
                if (preloadDesc.text) 
                    preloadDesc = {
                        type        : 'js',
                        content     : preloadDesc.text
                    }
                    
            if (!preloadDesc.type) throw new Error("Preload descriptor must have the `type` property")
                
            this.preload.push(preloadDesc)
        },
        
        
        addOnErrorHandler : function (handler, callback) {
            throw "Abstract method `addOnErrorHandler` of Scope.Provider called"
        },
        
        
        create : function () {
            throw "Abstract method `create` of Scope.Provider called"
        },
        
        
        setup : function (callback) {
            throw "Abstract method `setup` of Scope.Provider called"
        },
        
        
        cleanup : function () {
            throw "Abstract method `cleanup` of Scope.Provider called"
        },
        
        
        runCode : function (text, callback) {
            throw "Abstract method `runCode` of Scope.Provider called"
        },
        
        
        runScript : function (url, callback) {
            throw "Abstract method `runScript` of Scope.Provider called"
        }
    }
})


Scope.Provider.__ONLOAD__   = {}
Scope.Provider.__ONERROR__  = {};
Role('Scope.Provider.Role.WithDOM', {
    
    requires    : [ 'getDocument', 'create', 'getPreload', 'isAlreadySetUp' ],
    
    has : {
        useStrictMode   : true,
        sourceURL       : null,
        
        minViewportSize : null,
        
        parentWindow    : function () { return window },
        scopeId         : function () { return Math.round(Math.random() * 1e10) },
        
        //                init function
        attachToOnError : function () {
            
            // returns the value of the attribute
            return function (window, scopeId, handler) {
                
                var prevHandler         = window.onerror
                if (prevHandler && prevHandler.__SP_MANAGED__) return
                
                window.onerror = function (message, url, lineNumber) {
                    // prevent recursive calls if other authors politely did not overwrite the handler and will call it
                    if (handler.__CALLING__) return
                    
                    handler.__CALLING__ = true
                    
                    prevHandler && prevHandler.apply(this, arguments)
                
                    handler.apply(this, arguments)
                    
                    handler.__CALLING__ = false
                    
                    // in FF/IE need to return `true` to prevent default action
                    return window.WebKitPoint ? false : true 
                }
                
                window.onerror.__SP_MANAGED__ = true
            } 
        },
        
        // this is a "cached" onerror handler - a handler which was provided before the scope
        // has started the creation process - should be installed ASAP in the creation process
        // to allow catching of the exceptions in the scope with `sourceURL` 
        cachedOnError   : null
    },
    
    
    before : {
        
        cleanup : function () {
            this.scope.onerror = null
            
            var scopeProvider = this.parentWindow.Scope.Provider
            
            delete scopeProvider.__ONLOAD__[ this.scopeId ]
            delete scopeProvider.__ONERROR__[ this.scopeId ]
        }
    },
    
        
    methods : {
        
        getHead : function () {
            return this.getDocument().getElementsByTagName('head')[ 0 ]
        },
        
        
        installOnErrorHandler : function (handler) {
            if (!this.isAlreadySetUp()) throw "Scope should be already set up"
            
            this.attachToOnError(this.scope, this.scopeId, handler)
        },
        
        
        addOnErrorHandler : function (handler) {
            handler.__SP_MANAGED__  = true
            
            if (this.cachedOnError && this.cachedOnError != handler) throw "Can only install one on error handler" 
            this.cachedOnError      = handler
            
            var scopeId     = this.scopeId
            
            this.parentWindow.Scope.Provider.__ONERROR__[ scopeId ] = handler
            
            var attachToOnError = ';(' + this.attachToOnError.toString() + ')(window, ' + scopeId + ', (window.opener || window.parent).Scope.Provider.__ONERROR__[ ' + scopeId + ' ]);'
            
            if (this.isAlreadySetUp()) 
                this.runCode(attachToOnError)
            else {
                // this is a fallback - run the "attachToOnError" from inside of scope
                this.getPreload().unshift({
                    type        : 'js',
                    content     : attachToOnError,
                    unordered   : true
                })
            }
        },
        
        
        addSeedingToPreload : function () {
            var preload             = this.getPreload()
                
            if (this.seedingCode) preload.unshift({
                type        : 'js',
                content     : this.seedingCode
            })
            
            if (this.seedingScript) preload.push({
                type        : 'js',
                url         : this.seedingScript
            })
        },
        
        
        setup : function (callback) {
            var isIE                = 'v' == '\v' || Boolean(this.parentWindow.msWriteProfilerMark)
//            var isOpera             = Object.prototype.toString.call(this.parentWindow.opera) == '[object Opera]'
            var hasInlineScript     = false
            
            Joose.A.each(this.getPreload(), function (preloadDesc) {
                // IE will execute the inline scripts ASAP, this might be not what we want (inline script might be need executed only after some url script)
                // its however ok in some cases (like adding `onerror` handler
                // such inline scripts should be marked with `unordered` - true
                if (preloadDesc.type == 'js' && preloadDesc.content && !preloadDesc.unordered) {
                    hasInlineScript = true
                    
                    return false
                } 
            })
            
            if (this.sourceURL || isIE && hasInlineScript) {
                this.addSeedingToPreload()
                
                this.setupIncrementally(callback)
                
            } else {
                // for sane browsers just add the seeding code and seeding script to preloads
                if (!isIE) this.addSeedingToPreload()
                
                // seeding scripts are included only for sane browsers (not IE)
                this.setupWithDocWrite(callback, isIE)
            }
        },
        
        
        setupWithDocWrite : function (callback, needToSeed) {
            var html        = []
            var me          = this
            
            Joose.A.each(this.getPreload(), function (preloadDesc) {
                
                if (preloadDesc.type == 'js') 
                    html.push(me.getScriptTagString(preloadDesc.url, preloadDesc.content))
                    
                else if (preloadDesc.type == 'css') 
                    html.push(me.getLinkTagString(preloadDesc.url, preloadDesc.content))
                
                else throw "Incorrect preload descriptor " + preloadDesc
            })
            
            // no need to wait for DOM ready - we'll overwrite it anyway
            this.create()
            
            var scopeId              = this.scopeId
            
            this.parentWindow.Scope.Provider.__ONLOAD__[ scopeId ]    = function () {

                var cont = function () { callback && callback(me) }
                
                // sane browsers - seeding code and script has been already added
                if (!needToSeed) { cont(); return }
                
                // our beloved IE - manually seeding the scope
                
                if (me.seedingCode) me.runCode(me.seedingCode)
                
                if (me.seedingScript) 
                    me.runScript(me.seedingScript, cont)
                else
                    cont()
            }
            
            var doc             = this.getDocument()
            
            doc.open()
            
            doc.write([
                this.useStrictMode ? '<!DOCTYPE html>' : '',
                '<html style="width: 100%; height: 100%; margin : 0; padding : 0;">',
                    '<head>',
                        html.join(''),
                    '</head>',
    
                    '<body style="margin : 0; padding : 0; width: 100%; height: 100%" onload="(window.opener || window.parent).Scope.Provider.__ONLOAD__[' + scopeId + ']()">',
                    '</body>',
                '</html>'
            ].join(''))
            
            doc.close()
            
            // Chrome (Webkit?) will clear the `onerror` after "doc.open()/.close()" so need to re-install it
            if (me.cachedOnError) me.installOnErrorHandler(me.cachedOnError)
        },
        
        
        setupIncrementally : function (callback) {
            var me      = this
            
            // here the "onerror" should be included early in the "preloads" 
            this.create(function () {
                
                var loadScripts     = function (preloads, callback) {
                    
                    var cont = function () { loadScripts(preloads, callback) }
                    
                    if (!preloads.length) 
                        callback && callback()
                    else {
                        var preloadDesc     = preloads.shift()
                        
                        if (preloadDesc.url) 
                            me.runScript(preloadDesc.url, cont)
                        else 
                            if (preloadDesc.type == 'js')
                                me.runCode(preloadDesc.content, cont)
                            else {
                                me.addStyleTag(preloadDesc.content)
                                
                                cont()
                            }
                    }
                }
                
                loadScripts(me.getPreload().slice(), callback)
            })
        },        
        
        
        getScriptTagString : function (url, text) {
            var res = '<script type="text/javascript"'
            
            if (url) 
                res     += ' src="' + url + '"></script>'
            else
                res     += '>' + text.replace(/<\/script>/gi, '\\x3C/script>') + '</script>'
                
            return res
        },
        
        
        getLinkTagString : function (url, text) {
            if (url) return '<link href="' + url + '" rel="stylesheet" type="text/css" />'
            
            if (text) return '<style>' + text + '</style>'
        },
        
        

        loadCSS : function (url, callback) {
            var doc         = this.getDocument()
            var link        = doc.createElement('link')
            
            link.type       = 'text/css'
            link.rel        = 'stylesheet'
            link.href       = url
        
            this.getHead().appendChild(link)
            
            var hasContinued    = false
            
            var cont            = function () {
                // just in case some crazy JS engine calls `onerror` even after node removal
                if (hasContinued) return
                hasContinued    = true
                clearTimeout(forcedTimeout)
                
                if (callback) callback()
                
                doc.body.removeChild(img)
            }
            
            var forcedTimeout   = setTimeout(cont, 3000)
        
            var img             = doc.createElement('img')
            
            img.onerror         = cont
        
            doc.body.appendChild(img)
            
            img.src             = url
        },
        
        
        runCode : function (text, callback) {
            this.getHead().appendChild(this.createScriptTag(text))
            
            callback && callback()
        },
        
        
        runScript : function (url, callback) {
            if (this.isCSS(url))
                this.loadCSS(url, callback)
            else
                this.getHead().appendChild(this.createScriptTag(null, url, callback))
        },
        
        
        createScriptTag : function (text, url, callback) {
            var node = this.getDocument().createElement("script")
            
            node.setAttribute("type", "text/javascript")
            
            if (url) node.setAttribute("src", url)
            
            if (text) node.text = text
            
            if (callback) node.onload = node.onreadystatechange = function() {
                if (!node.readyState || node.readyState == "loaded" || node.readyState == "complete" || node.readyState == 4 && node.status == 200) {
                    node.onload = node.onreadystatechange = null
                    
                    //surely for IE6..
                    if ('v' == '\v') 
                        setTimeout(callback, 0)
                    else
                        callback()
                }
            }
            
            return node
        },
        
        
        addStyleTag : function (text) {
            var document    = this.getDocument()
            var node        = document.createElement('style')
            
            node.setAttribute("type", "text/css")
            
            var head = document.getElementsByTagName('head')[0]
            head.appendChild(node)
            
            if (node.styleSheet) {   // IE
                node.styleSheet.cssText = text
            } else {                // the world
                node.appendChild(document.createTextNode(text))
            }
        }        
    }
})


/**

Name
====

Scope.Provider.Role.WithDOM - role for scope provider, which uses `script` tag for running the code.


SYNOPSIS
========

        Class('Scope.Provider.IFrame', {
            
            isa     : Scope.Provider,
            
            does    : Scope.Provider.Role.WithDOM,
            
            ...
        })

DESCRIPTION
===========

`Scope.Provider.Role.WithDOM` requires the implementation of the `getDocument` method, which should return the
document into which the `script` tags will be created.

In return, this role provides the implementation of `runCode` and `runScript`.




GETTING HELP
============

This extension is supported via github issues tracker: <http://github.com/SamuraiJack/Scope-Provider/issues>

For general Joose questions you can also visit [#joose](http://webchat.freenode.net/?randomnick=1&channels=joose&prompt=1) 
on irc.freenode.org or the forum at: <http://joose.it/forum>
 


SEE ALSO
========

Web page of this module: <http://github.com/SamuraiJack/Scope-Provider/>

General documentation for Joose: <http://joose.github.com/Joose>


BUGS
====

All complex software has bugs lurking in it, and this module is no exception.

Please report any bugs through the web interface at <http://github.com/SamuraiJack/Scope-Provider/issues>



AUTHORS
=======

Nickolay Platonov <nplatonov@cpan.org>





COPYRIGHT AND LICENSE
=====================

This software is Copyright (c) 2010 by Nickolay Platonov <nplatonov@cpan.org>.

This is free software, licensed under:

  The GNU Lesser General Public License, Version 3, June 2007

*/;
Class('Scope.Provider.IFrame', {
    
    isa     : Scope.Provider,
    
    does    : Scope.Provider.Role.WithDOM,
    
    
    have : {
        iframe          : null,
        
        parentEl        : null
    },
    

    methods : {
        
        getDocument : function () {
            return this.iframe.contentWindow.document
        },
        
        
        create : function (onLoadCallback) {
            var me      = this
            var self    = { self : this }
            var doc     = this.parentWindow.document
            var iframe  = this.iframe = doc.createElement('iframe')
            
            var minViewportSize     = this.minViewportSize
            
            iframe.style.width      = (minViewportSize && minViewportSize.width || 1024) + 'px'
            iframe.style.height     = (minViewportSize && minViewportSize.height || 768) + 'px'
            iframe.setAttribute('frameborder', 0)

            var ignoreOnLoad        = false    
            
            var callback = function () {
                if (ignoreOnLoad) return
                
                if (iframe.detachEvent) 
                    iframe.detachEvent('onload', callback)
                else
                    iframe.onload = null
                
                onLoadCallback && onLoadCallback(me)
            }
            
            if (iframe.attachEvent) 
                iframe.attachEvent('onload', callback)
            else
                iframe.onload = callback
            
            iframe.src = this.sourceURL || 'about:blank'
            
            ;(this.parentEl || doc.body).appendChild(iframe)
            
            var scope   = this.scope = iframe.contentWindow
            var doc     = this.getDocument()
            
            // dances with tambourine around the IE, somehow fixes the cross-domain limits
            if ('v' == '\v' || Boolean(this.parentWindow.msWriteProfilerMark)) {
                // only ignore the 1st call to callback when there is a `sourceURL` config
                // which will later be assigned to `iframe.src` and will trigger a new iframe loading
                if (this.sourceURL) ignoreOnLoad = true
                
                doc.open()
                doc.write('')
                doc.close()
                
                ignoreOnLoad = false
                
                iframe.onreadystatechange = function () {
                    if (iframe.readyState == 'complete') iframe.onreadystatechange = null
                    
                    // trying to add the "early" onerror handler on each "readyState" change
                    // for some mystical reasons can't use `me.installOnErrorHandler` need to inline the call
                    if (me.cachedOnError) me.attachToOnError(scope, me.scopeId, me.cachedOnError)
                }
                
                if (this.sourceURL) iframe.src = this.sourceURL
            }
            
            // trying to add the "early" onerror handler - installing it in this stage will only work in FF 
            // (other browsers will clear on varios stages)
            if (me.cachedOnError) me.installOnErrorHandler(me.cachedOnError)
        },
        
        
        cleanup : function () {
            var iframe      = this.iframe
            var win         = this.scope
            var me          = this
            
            iframe.style.display    = 'none'
            
            var onUnloadChecker = function () {
                if (!window.onunload) window.onunload = function () { return 'something' }
            }
            
            // add the `onunload` handler if there's no any - attempting to prevent browser from caching the iframe
            // trying to create the handler from inside of the scope
            this.runCode(';(' + onUnloadChecker.toString() + ')();')

            this.iframe     = null
            this.scope      = null

            // wait for 1000ms to allow time for possible `setTimeout` in the scope of iframe
            setTimeout(function () {
                
                // chaging the page, triggering `onunload` and hopefully preventing browser from caching the content of iframe
                iframe.src              = 'javascript:false'
                
                // wait again before removing iframe from the DOM, as recommended by some online sources
                setTimeout(function () {
                    ;(me.parentEl || me.parentWindow.document.body).removeChild(iframe)
                    
                    iframe  = null
                    win     = null
                    
                    if (me.cleanupCallback) me.cleanupCallback()
                    
                }, 1000)
            }, 1000)
        }
    }
})

/**

Name
====

Scope.Provider.IFrame - scope provider, which uses the iframe.


SYNOPSIS
========

        var provider = new Scope.Provider.IFrame()
        
        provider.setup(function () {
        
            if (provider.scope.SOME_GLOBAL == 'some_value') {
                ...
            }
            
            provider.runCode(text, callback)
            
            ...
            
            provider.runScript(url, callback)
            
            ...
            
            provider.cleanup()        
        })


DESCRIPTION
===========

`Scope.Provider.IFrame` is an implementation of the scope provider, which uses the iframe, 
to create a new scope.


ISA
===

[Scope.Provider](../Provider.html)


DOES
====

[Scope.Provider.Role.WithDOM](Role/WithDOM.html)



GETTING HELP
============

This extension is supported via github issues tracker: <http://github.com/SamuraiJack/Scope-Provider/issues>

You can also ask questions at IRC channel : [#joose](http://webchat.freenode.net/?randomnick=1&channels=joose&prompt=1)
 
Or the mailing list: <http://groups.google.com/group/joose-js>
 


SEE ALSO
========

Web page of this module: <http://github.com/SamuraiJack/Scope-Provider/>

General documentation for Joose: <http://joose.github.com/Joose>


BUGS
====

All complex software has bugs lurking in it, and this module is no exception.

Please report any bugs through the web interface at <http://github.com/SamuraiJack/Scope-Provider/issues>



AUTHORS
=======

Nickolay Platonov <nplatonov@cpan.org>





COPYRIGHT AND LICENSE
=====================

This software is Copyright (c) 2010 by Nickolay Platonov <nplatonov@cpan.org>.

This is free software, licensed under:

  The GNU Lesser General Public License, Version 3, June 2007

*/;
Class('Scope.Provider.Window', {
    
    isa     : Scope.Provider,

    does    : Scope.Provider.Role.WithDOM,
    
    
    has     : {
        popupWindow     : null
    },
    

    methods : {
        
        create : function (onLoadCallback) {
            var popup   = this.scope = this.popupWindow = this.parentWindow.open(this.sourceURL || 'about:blank', '_blank', "width=800,height=600")
            
            if (!popup) {
                alert('Please enable popups for the host with this test suite running: ' + this.parentWindow.location.host)
                throw 'Please enable popups for the host with this test suite running: ' + this.parentWindow.location.host
            }
            
            var isIE = 'v' == '\v' || Boolean(this.parentWindow.msWriteProfilerMark)
            
            // dances with tambourine around the IE
            if (isIE && !this.sourceURL) {
                var doc = this.getDocument()
                
                doc.open()
                doc.write('')
                doc.close()
            }
            
            // trying to add the "early" onerror handler - will probably only work in FF
            if (this.cachedOnError) this.installOnErrorHandler(this.cachedOnError)
            
            /*!
             * contentloaded.js
             *
             * Author: Diego Perini (diego.perini at gmail.com)
             * Summary: cross-browser wrapper for DOMContentLoaded
             * Updated: 20101020
             * License: MIT
             * Version: 1.2
             *
             * URL:
             * http://javascript.nwbox.com/ContentLoaded/
             * http://javascript.nwbox.com/ContentLoaded/MIT-LICENSE
             *
             */
            
            // @win window reference
            // @fn function reference
            var contentLoaded = function (win, fn) {
            
                var done = false, top = true,
            
                doc = win.document, root = doc.documentElement,
            
                add = doc.addEventListener ? 'addEventListener' : 'attachEvent',
                rem = doc.addEventListener ? 'removeEventListener' : 'detachEvent',
                pre = doc.addEventListener ? '' : 'on',
            
                init = function(e) {
                    if (e.type == 'readystatechange' && doc.readyState != 'complete') return;
                    
                    (e.type == 'load' ? win : doc)[rem](pre + e.type, init, false);
                    
                    if (!done && (done = true)) fn.call(win, e.type || e);
                },
            
                poll = function() {
                    try { root.doScroll('left'); } catch(e) { setTimeout(poll, 50); return; }
                    
                    init('poll');
                };
            
                if (doc.readyState == 'complete') 
                    fn.call(win, 'lazy');
                else {
                    if (doc.createEventObject && root.doScroll) {
                        try { top = !win.frameElement; } catch(e) { }
                        if (top) poll();
                    }
                    doc[add](pre + 'DOMContentLoaded', init, false);
                    doc[add](pre + 'readystatechange', init, false);
                    win[add](pre + 'load', init, false);
                }
            }
            
            contentLoaded(popup, onLoadCallback || function () {})
        },
        
        
        getDocument : function () {
            return this.popupWindow.document
        },
        
        
        cleanup : function () {
            this.popupWindow.close()
            
            this.popupWindow = null
            
            if (this.cleanupCallback) this.cleanupCallback()
        }
    }
})

/**

Name
====

Scope.Provider.Window - scope provider, which uses the popup browser window.


SYNOPSIS
========

        var provider = new Scope.Provider.Window()
        
        provider.setup(function () {
        
            if (provider.scope.SOME_GLOBAL == 'some_value') {
                ...
            }
            
            provider.runCode(text, callback)
            
            ...
            
            provider.runScript(url, callback)
            
            ...
            
            provider.cleanup()        
        })


DESCRIPTION
===========

`Scope.Provider.Window` is an implementation of the scope provider, which uses the popup browser window, 
to create a new scope.


ISA
===

[Scope.Provider](../Provider.html)


DOES
====

[Scope.Provider.Role.WithDOM](Role/WithDOM.html)



GETTING HELP
============

This extension is supported via github issues tracker: <http://github.com/SamuraiJack/Scope-Provider/issues>

You can also ask questions at IRC channel : [#joose](http://webchat.freenode.net/?randomnick=1&channels=joose&prompt=1)
 
Or the mailing list: <http://groups.google.com/group/joose-js>
 


SEE ALSO
========

Web page of this module: <http://github.com/SamuraiJack/Scope-Provider/>

General documentation for Joose: <http://joose.github.com/Joose>


BUGS
====

All complex software has bugs lurking in it, and this module is no exception.

Please report any bugs through the web interface at <http://github.com/SamuraiJack/Scope-Provider/issues>



AUTHORS
=======

Nickolay Platonov <nplatonov@cpan.org>





COPYRIGHT AND LICENSE
=====================

This software is Copyright (c) 2010 by Nickolay Platonov <nplatonov@cpan.org>.

This is free software, licensed under:

  The GNU Lesser General Public License, Version 3, June 2007

*/;
Class('Scope.Provider.NodeJS', {
    
    isa     : Scope.Provider,

    
    has     : {
        sourceURL       : null
    },
    

    methods : {
        
        compile : function (module, content, filename) {
            var Module    = require('module')
            var path      = require('path')
            
            var self      = module;
            // remove shebang
            content       = content.replace(/^\#\!.*/, '');
        
            var modRequire     = function (path) {
                return self.require(path);
            }
        
            modRequire.resolve = function(request) {
                return Module._resolveFilename(request, self)[1];
            };
        
            Object.defineProperty(modRequire, 'paths', { get: function() {
                throw new Error('modRequire.paths is removed. Use ' +
                            'node_modules folders, or the NODE_PATH '+
                            'environment variable instead.');
            }});
        
            modRequire.main = process.mainModule;
        
            // Enable support to add extra extension types
            modRequire.extensions = Module._extensions;
            modRequire.registerExtension = function() {
                throw new Error('modRequire.registerExtension() removed. Use ' +
                            'modRequire.extensions instead.');
            };
        
            modRequire.cache = Module._cache;
        
            var dirname = path.dirname(filename);
        
            // create wrapper function
            var wrapper = Module.wrap(content);
            
            var compiledWrapper = require('vm').runInContext(wrapper, this.scope, filename);
            
            return compiledWrapper.apply(self.exports, [self.exports, modRequire, self, filename, dirname]);
        },        
        
        
        addOnErrorHandler : function (handler, callback) {
        },

        
        create : function (callback) {
            var vm          = require('vm')
            var sandbox     = {}

            Joose.O.extend(sandbox, {
//                __PROVIDER__    : true,
                
                process         : process,
                
                global          : sandbox,
                root            : root,
                
                setTimeout      : setTimeout,
                clearTimeout    : clearTimeout,
                setInterval     : setInterval,
                clearInterval   : clearInterval
//                ,
//                
//                __filename      : __filename,
//                __dirname       : __dirname,
//                module          : module
            })
            
            var scope       = this.scope    = vm.createContext(sandbox)
            
            callback && callback()
        },
        
        
        setup : function (callback) {
            this.create()
            
            var me      = this
            
            if (this.seedingCode) require('vm').runInContext(this.seedingCode, this.scope)
            
            Joose.A.each(this.getPreload(), function (preloadDesc) {
                
                if (preloadDesc.type == 'js')
                    if (preloadDesc.url)
                        me.runScript(preloadDesc.url)
                    else
                        me.runCode(preloadDesc.content)
            })
            
            if (this.seedingScript) {
                var Module          = require('module')
                var path            = require('path')
                
                var module          = new Module('./' + this.sourceURL, require.main)
                
                var filename        = module.filename = path.join(path.dirname(require.main.filename), this.sourceURL)
                
                var content         = require('fs').readFileSync(filename, 'utf8')
                // Remove byte order marker. This catches EF BB BF (the UTF-8 BOM)
                // because the buffer-to-string conversion in `fs.readFileSync()`
                // translates it to FEFF, the UTF-16 BOM.
                if (content.charCodeAt(0) === 0xFEFF) content = content.slice(1)
  
                this.compile(module, content, filename)
            }
            
            callback && callback()
        },
        
        
        runCode : function (text, callback) {
            var res = require('vm').runInContext(text, this.scope)
            
            callback && callback(res)
            
            return res
        },
        
        
        runScript : function (url, callback) {
            var content = require('fs').readFileSync(url, 'utf8')
            
            var res = require('vm').runInContext(content, this.scope, url)
            
            callback && callback(res)
            
            return res
        },
        
        
        cleanup : function () {
            if (this.cleanupCallback) this.cleanupCallback()
        }
    }
})


/**

Name
====

Scope.Provider.NodeJS - scope provider, which uses the `Script.runInNewContext` call of the NodeJS.


SYNOPSIS
========

        var provider = new Scope.Provider.NodeJS()
        
        provider.setup(function () {
        
            if (provider.scope.SOME_GLOBAL == 'some_value') {
                ...
            }
            
            provider.runCode(text, callback)
            
            ...
            
            provider.runScript(url, callback)
            
            ...
            
            provider.cleanup()        
        })


DESCRIPTION
===========

`Scope.Provider.NodeJS` is an implementation of the scope provider, 
which uses the `Script.runInNewContext` call of the NodeJS platform.


ISA
===

[Scope.Provider](../Provider.html)



GETTING HELP
============

This extension is supported via github issues tracker: <http://github.com/SamuraiJack/Scope-Provider/issues>

You can also ask questions at IRC channel : [#joose](http://webchat.freenode.net/?randomnick=1&channels=joose&prompt=1)
 
Or the mailing list: <http://groups.google.com/group/joose-js>
 


SEE ALSO
========

Web page of this module: <http://github.com/SamuraiJack/Scope-Provider/>

General documentation for Joose: <http://joose.github.com/Joose>


BUGS
====

All complex software has bugs lurking in it, and this module is no exception.

Please report any bugs through the web interface at <http://github.com/SamuraiJack/Scope-Provider/issues>



AUTHORS
=======

Nickolay Platonov <nplatonov@cpan.org>





COPYRIGHT AND LICENSE
=====================

This software is Copyright (c) 2010 by Nickolay Platonov <nplatonov@cpan.org>.

This is free software, licensed under:

  The GNU Lesser General Public License, Version 3, June 2007

*/;
;
Class('JooseX.Observable.Event', {
    
    has : {
        name        : { required : true },
        args        : { required : true },
        
        source      : { required : true },
        
        splat       : null,
        current     : null,
        
        bubbling    : true
    },
    
        
    methods : {
        
        stopPropagation : function () {
            this.bubbling = false
        }
    }
})


;
Class('JooseX.Observable.Listener', {

    has : {
        channel     : { required : true },
        eventName   : { required : true },
        
        func        : { required : true },
        scope       : null,
        
        single          : false,
        
        buffer          : null,
        bufferMax       : null,
        
        bufferStartedAt : null,
        bufferTimeout   : null,
        
        delayTimeout    : null,
        
        delay           : null
    },
    
        
    methods : {
        
        activate : function (event, args) {
            var me      = this
            
            if (me.buffer != null) {
                
                if (me.bufferMax != null)
                    if (!me.bufferStartedAt) 
                        me.bufferStartedAt = new Date()
                    else
                        if (new Date - me.bufferStartedAt > me.bufferMax) return
                
                        
                if (me.bufferTimeout) clearTimeout(me.bufferTimeout)
                
                me.bufferTimeout = setTimeout(function () {
                    
                    delete me.bufferStartedAt
                    delete me.bufferTimeout
                    
                    me.doActivate(event, args)
                    
                }, me.buffer)
                
                return
            }
            
            if (me.delay != null) {
                
                me.delayTimeout = setTimeout(function () {
                    
                    delete me.delayTimeout
                    
                    me.doActivate(event, args)
                    
                }, me.delay)
                
                return
            }
            
            return me.doActivate(event, args)
        },
        
        
        doActivate : function (event, args) {
            if (this.single) this.remove()
            
            return this.func.apply(this.scope || event.source, [ event ].concat(args) ) !== false
        },
        
        
        cancel  : function () {
            if (this.buffer) {
                clearTimeout(this.bufferTimeout)
                
                delete this.bufferTimeout
                delete this.bufferStartedAt
            }
            
            if (this.delay) clearTimeout(this.delayTimeout)
        },
        
        
        remove : function () {
            this.channel.removeListener(this)
        }
    }
})


;
Class('JooseX.Observable.Channel', {
    
    has : {
        channels    : Joose.I.Object,
        
        listeners   : Joose.I.Object
    },
    
        
    methods : {
        
        // (!) segments array will be destroyed in this method
        getListenersFor : function (segments, name, activators) {
            var listeners = this.listeners
            
            if (listeners[ '**' ]) {
                
                var splat       = segments.concat(name)
                
                Joose.A.each(listeners[ '**' ], function (listener) {
                    activators.push({
                        listener    : listener,
                        splat       : splat
                    })
                })
            }
            
            if (segments.length) {
                var next = this.getSingleChannel(segments.shift(), true)
                
                if (next) next.getListenersFor(segments, name, activators)
            } else {
                
                if (listeners[ '*' ])
                    Joose.A.each(listeners[ '*' ], function (listener) {
                        
                        activators.push({
                            listener    : listener,
                            splat       : name
                        })
                    })
                
                if (listeners[ name ])  
                    Joose.A.each(listeners[ name ], function (listener) {
                        
                        activators.push({
                            listener    : listener
                        })
                    })
            }
        },
        
        
        hasListenerFor : function (segments, name) {
            var listeners = this.listeners
            
            if (listeners[ '**' ] && listeners[ '**' ].length) return true
            
            if (segments.length)  {
                var next = this.getSingleChannel(segments.shift(), true)
                
                if (next) return next.hasListenerFor(segments, name)
                
            } else {
                
                if (listeners[ '*' ] && listeners[ '*' ].length) return true
                
                if (listeners[ name ] && listeners[ name ].length) return true  
            }
            
            return false
        },
        
        
        addListener : function (listener) {
            var eventName   = listener.eventName
            var listeners   = this.listeners
            
            listeners[ eventName ] = listeners[ eventName ] || []
            
            listeners[ eventName ].push(listener)
        },
        
        
        removeListener : function (listenerToRemove) {
            var eventListeners      = this.listeners[ listenerToRemove.eventName ]
            
            Joose.A.each(eventListeners, function (listener, index) {
                
                if (listener == listenerToRemove) {
                    
                    eventListeners.splice(index, 1)
                    
                    return false
                }
            })
        },
        
        
        removeListenerByHandler : function (eventName, func, scope) {
            var eventListeners      = this.listeners[ eventName ]
            
            Joose.A.each(eventListeners, function (listener, index) {
                
                if (listener.func == func && listener.scope == scope) {
                    
                    eventListeners.splice(index, 1)
                    
                    return false
                }
            })
        },
        
        
        getSingleChannel : function (name, doNotCreate) {
            var channels    = this.channels
            
            if (channels[ name ]) return channels[ name ]
            
            if (doNotCreate) return null
            
            return channels[ name ] = new JooseX.Observable.Channel()
        },
        
        
        // (!) segments array will be destroyed in this method
        getChannel : function (segments, doNotCreate) {
            if (!segments.length) return this
            
            var next    = this.getSingleChannel(segments.shift(), doNotCreate)
            
            if (doNotCreate && !next) return null
            
            return next.getChannel(segments, doNotCreate)
        }
    }
})


;
Role('JooseX.Observable', {
    
    /*PKGVERSION*/VERSION : 0.04,
    
//    use : [ 
//        'JooseX.Observable.Channel',    
//        'JooseX.Observable.Listener', 
//        'JooseX.Observable.Event'    
//    ],
    
    
//    trait   : 'JooseX.Observable.Meta',
    
    
    has : {
        rootChannel             : {
            is          : 'rw',
            init        : function () { return new JooseX.Observable.Channel() }
        },
        
        suspendCounter          : 0
    },
    
        
    methods : {
        
        getBubbleTarget : function () {
        },
        
        
        parseEventPath : function (path) {
            var channels    = path.split('/')
            var eventName   = channels.pop()
            
            if (channels.length && !channels[ 0 ]) channels.shift()
            
            return {
                channels        : channels,
                eventName       : eventName
            }
        },
        
        
        on : function (path, func, scope, options) {
            if (!func) throw "Not valid listener function provided when subsribing on event: " + path
            
            var parsed      = this.parseEventPath(path)
            var channel     = this.getRootChannel().getChannel(parsed.channels)
            
            var listener    = new JooseX.Observable.Listener(Joose.O.extend(options || {}, {
                channel     : channel,
                eventName   : parsed.eventName,
                
                func        : func,
                scope       : scope
            }))
            
            channel.addListener(listener)
            
            return listener
        },
        
        
        un : function (path, func, scope) {
            
            if (path instanceof JooseX.Observable.Listener) {
                
                path.remove()
                
                return
            }
            
            var parsed      = this.parseEventPath(path)
            var channel     = this.getRootChannel().getChannel(parsed.channels, true)
            
            if (channel) channel.removeListenerByHandler(parsed.eventName, func, scope)
        },
        
        
        emit : function () {
            return this.fireEvent.apply(this, arguments)
        },
        
        
        fireEvent : function (path) {
            if (this.suspendCounter) return
            
            var args        = Array.prototype.slice.call(arguments, 1)

            var event       = new JooseX.Observable.Event({
                name        : path,
                args        : args,
                
                source      : this
            }) 
            
            return this.propagateEvent(event, path, args)
        },
        
        
        propagateEvent : function (event, path, args) {
            if (this.suspendCounter) return
            
            var parsed      = this.parseEventPath(path)
            var eventName   = parsed.eventName
            
            if (!eventName == '*' || eventName == '**') throw new Error("Can't fire an empty event or event with `*`, `**` names ")
            
            var activators  = []
            
            this.getRootChannel().getListenersFor(parsed.channels, eventName, activators)
            
            var res             = true
            
            event.current       = this
            
            if (activators.length) Joose.A.each(activators, function (activator) {
                event.splat = activator.splat
                
                res = activator.listener.activate(event, args) !== false && res
            })
            
            if (event.bubbling) {
                
                var further = this.getBubbleTarget()
                
                if (further) res = further.propagateEvent(event, path, args) !== false && res
            } 
                
            return res
        },
        
        
        hasListenerFor : function (path) {
            var parsed      = this.parseEventPath(path)
            
            return this.getRootChannel().hasListenerFor(parsed.channels, parsed.eventName)
        },
        
        
        purgeListeners  : function () {
            this.rootChannel = new JooseX.Observable.Channel()
        },
        
        
        suspendEvents : function () {
            this.suspendCounter++
        },
        
        
        resumeEvents : function () {
            this.suspendCounter--
            
            if (this.suspendCounter < 0) this.suspendCounter = 0
        }
    }
});
;
/*
    http://www.JSON.org/json2.js
    2011-02-23

    Public Domain.

    NO WARRANTY EXPRESSED OR IMPLIED. USE AT YOUR OWN RISK.

    See http://www.JSON.org/js.html


    This code should be minified before deployment.
    See http://javascript.crockford.com/jsmin.html

    USE YOUR OWN COPY. IT IS EXTREMELY UNWISE TO LOAD CODE FROM SERVERS YOU DO
    NOT CONTROL.


    This file creates a global JSON object containing two methods: stringify
    and parse.

        JSON.stringify(value, replacer, space)
            value       any JavaScript value, usually an object or array.

            replacer    an optional parameter that determines how object
                        values are stringified for objects. It can be a
                        function or an array of strings.

            space       an optional parameter that specifies the indentation
                        of nested structures. If it is omitted, the text will
                        be packed without extra whitespace. If it is a number,
                        it will specify the number of spaces to indent at each
                        level. If it is a string (such as '\t' or '&nbsp;'),
                        it contains the characters used to indent at each level.

            This method produces a JSON text from a JavaScript value.

            When an object value is found, if the object contains a toJSON
            method, its toJSON method will be called and the result will be
            stringified. A toJSON method does not serialize: it returns the
            value represented by the name/value pair that should be serialized,
            or undefined if nothing should be serialized. The toJSON method
            will be passed the key associated with the value, and this will be
            bound to the value

            For example, this would serialize Dates as ISO strings.

                Date.prototype.toJSON = function (key) {
                    function f(n) {
                        // Format integers to have at least two digits.
                        return n < 10 ? '0' + n : n;
                    }

                    return this.getUTCFullYear()   + '-' +
                         f(this.getUTCMonth() + 1) + '-' +
                         f(this.getUTCDate())      + 'T' +
                         f(this.getUTCHours())     + ':' +
                         f(this.getUTCMinutes())   + ':' +
                         f(this.getUTCSeconds())   + 'Z';
                };

            You can provide an optional replacer method. It will be passed the
            key and value of each member, with this bound to the containing
            object. The value that is returned from your method will be
            serialized. If your method returns undefined, then the member will
            be excluded from the serialization.

            If the replacer parameter is an array of strings, then it will be
            used to select the members to be serialized. It filters the results
            such that only members with keys listed in the replacer array are
            stringified.

            Values that do not have JSON representations, such as undefined or
            functions, will not be serialized. Such values in objects will be
            dropped; in arrays they will be replaced with null. You can use
            a replacer function to replace those with JSON values.
            JSON.stringify(undefined) returns undefined.

            The optional space parameter produces a stringification of the
            value that is filled with line breaks and indentation to make it
            easier to read.

            If the space parameter is a non-empty string, then that string will
            be used for indentation. If the space parameter is a number, then
            the indentation will be that many spaces.

            Example:

            text = JSON.stringify(['e', {pluribus: 'unum'}]);
            // text is '["e",{"pluribus":"unum"}]'


            text = JSON.stringify(['e', {pluribus: 'unum'}], null, '\t');
            // text is '[\n\t"e",\n\t{\n\t\t"pluribus": "unum"\n\t}\n]'

            text = JSON.stringify([new Date()], function (key, value) {
                return this[key] instanceof Date ?
                    'Date(' + this[key] + ')' : value;
            });
            // text is '["Date(---current time---)"]'


        JSON.parse(text, reviver)
            This method parses a JSON text to produce an object or array.
            It can throw a SyntaxError exception.

            The optional reviver parameter is a function that can filter and
            transform the results. It receives each of the keys and values,
            and its return value is used instead of the original value.
            If it returns what it received, then the structure is not modified.
            If it returns undefined then the member is deleted.

            Example:

            // Parse the text. Values that look like ISO date strings will
            // be converted to Date objects.

            myData = JSON.parse(text, function (key, value) {
                var a;
                if (typeof value === 'string') {
                    a =
/^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2}(?:\.\d*)?)Z$/.exec(value);
                    if (a) {
                        return new Date(Date.UTC(+a[1], +a[2] - 1, +a[3], +a[4],
                            +a[5], +a[6]));
                    }
                }
                return value;
            });

            myData = JSON.parse('["Date(09/09/2001)"]', function (key, value) {
                var d;
                if (typeof value === 'string' &&
                        value.slice(0, 5) === 'Date(' &&
                        value.slice(-1) === ')') {
                    d = new Date(value.slice(5, -1));
                    if (d) {
                        return d;
                    }
                }
                return value;
            });


    This is a reference implementation. You are free to copy, modify, or
    redistribute.
*/

/*jslint evil: true, strict: false, regexp: false */

/*members "", "\b", "\t", "\n", "\f", "\r", "\"", JSON, "\\", apply,
    call, charCodeAt, getUTCDate, getUTCFullYear, getUTCHours,
    getUTCMinutes, getUTCMonth, getUTCSeconds, hasOwnProperty, join,
    lastIndex, length, parse, prototype, push, replace, slice, stringify,
    test, toJSON, toString, valueOf
*/


// Create a JSON object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

var JSON;
if (!JSON) {
    JSON = {};
}

(function () {
    "use strict";

    function f(n) {
        // Format integers to have at least two digits.
        return n < 10 ? '0' + n : n;
    }

    if (typeof Date.prototype.toJSON !== 'function') {

        Date.prototype.toJSON = function (key) {

            return isFinite(this.valueOf()) ?
                this.getUTCFullYear()     + '-' +
                f(this.getUTCMonth() + 1) + '-' +
                f(this.getUTCDate())      + 'T' +
                f(this.getUTCHours())     + ':' +
                f(this.getUTCMinutes())   + ':' +
                f(this.getUTCSeconds())   + 'Z' : null;
        };

        String.prototype.toJSON      =
            Number.prototype.toJSON  =
            Boolean.prototype.toJSON = function (key) {
                return this.valueOf();
            };
    }

    var cx = /[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,
        escapable = /[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,
        gap,
        indent,
        meta = {    // table of character substitutions
            '\b': '\\b',
            '\t': '\\t',
            '\n': '\\n',
            '\f': '\\f',
            '\r': '\\r',
            '"' : '\\"',
            '\\': '\\\\'
        },
        rep;


    function quote(string) {

// If the string contains no control characters, no quote characters, and no
// backslash characters, then we can safely slap some quotes around it.
// Otherwise we must also replace the offending characters with safe escape
// sequences.

        escapable.lastIndex = 0;
        return escapable.test(string) ? '"' + string.replace(escapable, function (a) {
            var c = meta[a];
            return typeof c === 'string' ? c :
                '\\u' + ('0000' + a.charCodeAt(0).toString(16)).slice(-4);
        }) + '"' : '"' + string + '"';
    }


    function str(key, holder) {

// Produce a string from holder[key].

        var i,          // The loop counter.
            k,          // The member key.
            v,          // The member value.
            length,
            mind = gap,
            partial,
            value = holder[key];

// If the value has a toJSON method, call it to obtain a replacement value.

        if (value && typeof value === 'object' &&
                typeof value.toJSON === 'function') {
            value = value.toJSON(key);
        }

// If we were called with a replacer function, then call the replacer to
// obtain a replacement value.

        if (typeof rep === 'function') {
            value = rep.call(holder, key, value);
        }

// What happens next depends on the value's type.

        switch (typeof value) {
        case 'string':
            return quote(value);

        case 'number':

// JSON numbers must be finite. Encode non-finite numbers as null.

            return isFinite(value) ? String(value) : 'null';

        case 'boolean':
        case 'null':

// If the value is a boolean or null, convert it to a string. Note:
// typeof null does not produce 'null'. The case is included here in
// the remote chance that this gets fixed someday.

            return String(value);

// If the type is 'object', we might be dealing with an object or an array or
// null.

        case 'object':

// Due to a specification blunder in ECMAScript, typeof null is 'object',
// so watch out for that case.

            if (!value) {
                return 'null';
            }

// Make an array to hold the partial results of stringifying this object value.

            gap += indent;
            partial = [];

// Is the value an array?

            if (Object.prototype.toString.apply(value) === '[object Array]') {

// The value is an array. Stringify every element. Use null as a placeholder
// for non-JSON values.

                length = value.length;
                for (i = 0; i < length; i += 1) {
                    partial[i] = str(i, value) || 'null';
                }

// Join all of the elements together, separated with commas, and wrap them in
// brackets.

                v = partial.length === 0 ? '[]' : gap ?
                    '[\n' + gap + partial.join(',\n' + gap) + '\n' + mind + ']' :
                    '[' + partial.join(',') + ']';
                gap = mind;
                return v;
            }

// If the replacer is an array, use it to select the members to be stringified.

            if (rep && typeof rep === 'object') {
                length = rep.length;
                for (i = 0; i < length; i += 1) {
                    if (typeof rep[i] === 'string') {
                        k = rep[i];
                        v = str(k, value);
                        if (v) {
                            partial.push(quote(k) + (gap ? ': ' : ':') + v);
                        }
                    }
                }
            } else {

// Otherwise, iterate through all of the keys in the object.

                for (k in value) {
                    if (Object.prototype.hasOwnProperty.call(value, k)) {
                        v = str(k, value);
                        if (v) {
                            partial.push(quote(k) + (gap ? ': ' : ':') + v);
                        }
                    }
                }
            }

// Join all of the member texts together, separated with commas,
// and wrap them in braces.

            v = partial.length === 0 ? '{}' : gap ?
                '{\n' + gap + partial.join(',\n' + gap) + '\n' + mind + '}' :
                '{' + partial.join(',') + '}';
            gap = mind;
            return v;
        }
    }

// If the JSON object does not yet have a stringify method, give it one.

    if (typeof JSON.stringify !== 'function') {
        JSON.stringify = function (value, replacer, space) {

// The stringify method takes a value and an optional replacer, and an optional
// space parameter, and returns a JSON text. The replacer can be a function
// that can replace values, or an array of strings that will select the keys.
// A default replacer method can be provided. Use of the space parameter can
// produce text that is more easily readable.

            var i;
            gap = '';
            indent = '';

// If the space parameter is a number, make an indent string containing that
// many spaces.

            if (typeof space === 'number') {
                for (i = 0; i < space; i += 1) {
                    indent += ' ';
                }

// If the space parameter is a string, it will be used as the indent string.

            } else if (typeof space === 'string') {
                indent = space;
            }

// If there is a replacer, it must be a function or an array.
// Otherwise, throw an error.

            rep = replacer;
            if (replacer && typeof replacer !== 'function' &&
                    (typeof replacer !== 'object' ||
                    typeof replacer.length !== 'number')) {
                throw new Error('JSON.stringify');
            }

// Make a fake root object containing our value under the key of ''.
// Return the result of stringifying the value.

            return str('', {'': value});
        };
    }


// If the JSON object does not yet have a parse method, give it one.

    if (typeof JSON.parse !== 'function') {
        JSON.parse = function (text, reviver) {

// The parse method takes a text and an optional reviver function, and returns
// a JavaScript value if the text is a valid JSON text.

            var j;

            function walk(holder, key) {

// The walk method is used to recursively walk the resulting structure so
// that modifications can be made.

                var k, v, value = holder[key];
                if (value && typeof value === 'object') {
                    for (k in value) {
                        if (Object.prototype.hasOwnProperty.call(value, k)) {
                            v = walk(value, k);
                            if (v !== undefined) {
                                value[k] = v;
                            } else {
                                delete value[k];
                            }
                        }
                    }
                }
                return reviver.call(holder, key, value);
            }


// Parsing happens in four stages. In the first stage, we replace certain
// Unicode characters with escape sequences. JavaScript handles many characters
// incorrectly, either silently deleting them, or treating them as line endings.

            text = String(text);
            cx.lastIndex = 0;
            if (cx.test(text)) {
                text = text.replace(cx, function (a) {
                    return '\\u' +
                        ('0000' + a.charCodeAt(0).toString(16)).slice(-4);
                });
            }

// In the second stage, we run the text against regular expressions that look
// for non-JSON patterns. We are especially concerned with '()' and 'new'
// because they can cause invocation, and '=' because it can cause mutation.
// But just to be safe, we want to reject all unexpected forms.

// We split the second stage into 4 regexp operations in order to work around
// crippling inefficiencies in IE's and Safari's regexp engines. First we
// replace the JSON backslash pairs with '@' (a non-JSON character). Second, we
// replace all simple value tokens with ']' characters. Third, we delete all
// open brackets that follow a colon or comma or that begin the text. Finally,
// we look to see that the remaining characters are only whitespace or ']' or
// ',' or ':' or '{' or '}'. If that is so, then the text is safe for eval.

            if (/^[\],:{}\s]*$/
                    .test(text.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g, '@')
                        .replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, ']')
                        .replace(/(?:^|:|,)(?:\s*\[)+/g, ''))) {

// In the third stage we use the eval function to compile the text into a
// JavaScript structure. The '{' operator is subject to a syntactic ambiguity
// in JavaScript: it can begin a block or an object literal. We wrap the text
// in parens to eliminate the ambiguity.

                j = eval('(' + text + ')');

// In the optional fourth stage, we recursively walk the new structure, passing
// each name/value pair to a reviver function for possible transformation.

                return typeof reviver === 'function' ?
                    walk({'': j}, '') : j;
            }

// If the text is not JSON parseable, then a SyntaxError is thrown.

            throw new SyntaxError('JSON.parse');
        };
    }
}());
;
!function () {
    
    var REF      = 1    

    Class('Data.Visitor2', {
        
        has : {
            seenPlaceholder : {
                init        : {}
            },
            
            outOfDepthPlaceholder : {
                init        : {}
            },
            
            seen            : Joose.I.Object,
            
            maxDepth        : null
        },
            
        methods : {
            
            getClassNameFor : function (object) {
                if (Joose.O.isInstance(object))      return object.meta.name
                
                return Object.prototype.toString.call(object).replace(/^\[object /, '').replace(/\]$/, '')
            },
            
            
            getRefAdr : function () {
                return REF++
            },
            
            
            assignRefAdrTo : function (object) {
                if (!object.__REFADR__) 
                    if (Object.defineProperty)
                        Object.defineProperty(object, '__REFADR__', { value : REF++ })
                    else
                        object.__REFADR__ = REF++
                
                return object.__REFADR__
            },
                
                
            isSeen : function (object) {
                return object.__REFADR__ && this.seen.hasOwnProperty(object.__REFADR__)
            },
            
            
            markSeenAs : function (object, result) {
                return this.seen[ object.__REFADR__ ] = result
            },
            
            
            hasSeenResultFor : function (object) {
                var ref = object.__REFADR__
                
                return this.seen.hasOwnProperty(ref) && this.seen[ ref ] != this.seenPlaceholder
            },
            
            
            visit : function (value, depth) {
                // will be false for NaN values
                if (depth > this.maxDepth)
                    return this.visitOutOfDepthValue(value, depth + 1)
                else
                    if (Object(value) === value)
                        if (this.isSeen(value)) 
                            return this.visitSeen(value, depth + 1)
                        else                        
                            return this.visitNotSeen(value, depth + 1)
                    else
                        return this.visitValue(value, depth + 1)
            },
            
            
            visitOutOfDepthValue : function (value, depth) {
                return this.outOfDepthPlaceholder
            },
            
            
            visitValue : function (value, depth) {
                return value
            },
            
            
            visitSeen : function (value, depth) {
                return this.seen[ value.__REFADR__ ]
            },
            
            
            getInitialSeenMarker : function (object, depth) {
                return this.seenPlaceholder
            },
            
            
            visitNotSeen : function (object, depth) {
                this.assignRefAdrTo(object)
                
                this.markSeenAs(object, this.getInitialSeenMarker(object, depth))
    
                
                if (Joose.O.isInstance(object)) return this.markSeenAs(object, this.visitJooseInstance(object, depth))
                
                
                var methodName = 'visit' + this.getClassNameFor(object)
                
                if (!this.meta.hasMethod(methodName)) methodName = 'visitObject' 
                
                return this.markSeenAs(object, this[ methodName ](object, depth))
            },
            
            
            visitArray  : function (array, depth) {
                Joose.A.each(array, function (value, index) {
                    
                    this.visitArrayEntry(value, index, array, depth)
                    
                }, this)
                
                return array
            },
            
            
            visitArrayEntry  : function (entry, index, array, depth) {
                return this.visit(entry, depth)
            },
            
            
            visitObject : function (object, depth) {
                
                Joose.O.eachOwn(object, function (value, key) {
                    
                    if (key != '__REFADR__') {
                        this.visitObjectKey(key, value, object, depth)
                        this.visitObjectValue(value, key, object, depth)
                    }
                    
                }, this)
                
                return object
            },
            
            
            visitJooseInstance : function (value, depth) {
                return this.visitObject(value, depth)
            },
            
            
            visitObjectKey : function (key, value, object, depth) {
                return this.visitValue(key, depth)
            },
            
            
            visitObjectValue : function (value, key, object, depth) {
                return this.visit(value, depth)
            }
        },
        
        
        my : {
            
            has : {
                HOST        : null
            },
            
            
            methods : {
                
                visit : function (value, maxDepth) {
                    var visitor     = new this.HOST({
                        maxDepth        : maxDepth || Infinity
                    })
                    
                    return visitor.visit(value, 0)
                }
            }
        }
    })    
    
}()


;
;
Class('Siesta.Util.Serializer', {
    
    isa : Data.Visitor2,
    
    has     : {
        result                  : Joose.I.Array,
        manualEnum              : function () {
            for (var i in { toString : 1 }) return false
            
            return true
        }
    },
    
    
    methods : {
        
        assignRefAdrTo : function (object) {
            try {
                return this.SUPER(object)
            } catch (e) {
                if (!object.__REFADR__) object.__REFADR__ = this.getRefAdr()
            }
            
            return object.__REFADR__
        },
        
        
        write : function (str) {
            this.result.push(str)
        },
        
        
        visitOutOfDepthValue : function (value, depth) {
            this.write('...')
        },
        
        
        visitValue : function (value) {
            if (value == null)
                // `null` and `undefined`
                this.write(value + '')
            else
                this.write(typeof value == 'string' ? '"' + value.replace(/"/g, '\\"').replace(/\n/g, '\\n') + '"' : value + '')
        },
        
        
        visitObjectKey : function (key, value, object) {
            this.write('"' + key + '": ')
        },
        
        
        getClassNameFor : function (object) {
            if (object.nodeType != null && object.nodeName != null && object.tagName) return 'DOMElement'
            
            // trying to detect and not dive into global window
            if (object.document != null && object.location != null && object.location.href != null) return 'Window'
            
            return this.SUPER(object)
        },
        
        
        visitSeen : function (value, depth) {
            this.write('[Circular]')
        },
        
        
        visitRegExp : function (value, depth) {
            this.write(value + '')
        },
        
        
        visitDate : function (value, depth) {
            this.write('"' + value + '"')
        },
        

        // safer alternative to parent's implementation of `visitObject` - some host objects has no "hasOwnProperty" method
        visitObject : function (object, depth) {
            for (var key in object) {
                if (key != '__REFADR__' && (!object.hasOwnProperty || object.hasOwnProperty(key))) {
                    var value   = object[ key ]
                    
                    this.visitObjectKey(key, value, object, depth)
                    this.visitObjectValue(value, key, object, depth)
                }
            }

            var me  = this
            
            if (this.manualEnum) 
                Joose.A.each([ 'hasOwnProperty', 'valueOf', 'toString', 'constructor' ], function (key) {
                    if (object.hasOwnProperty && object.hasOwnProperty(key)) {
                        var value   = object[ key ]
                        
                        me.visitObjectKey(key, value, object, depth)
                        me.visitObjectValue(value, key, object, depth)
                    }
                })
            
            return object
        },
        
        
        visitDOMElement : function (object, depth) {
            var output  = '&lt;' + object.tagName
            
            if (object.id) output += ' id="' + object.id + '"'
            if (object.className) output += ' class="' + object.className + '"'
            
            this.write(output + '&gt;')
        },
        
        
        visitDOMStringMap : function () {
            this.write('[DOMStringMap]')
        },
        
        
        // the Object.prototype.toString.call(window) for FF
        visitWindow : function () {
            this.write('[window]')
        },
        
        
        // window.location type in FF
        visitLocation : function () {
            this.write('[window.location]')
        }
    },
    
    
    before : {
        visitObject : function () {
            this.write('{')
        },
        
        
        visitArray : function () {
            this.write('[')
        }
    },
    
    
    after : {
        visitObject : function () {
            var result = this.result
            
            if (result[ result.length - 1 ] == ', ') result.pop()
            
            this.write('}')
        },
        
        
        visitArray : function () {
            var result = this.result
            
            if (result[ result.length - 1 ] == ', ') result.pop()
            
            this.write(']')
        },
        
        
        visitObjectValue : function () {
            this.write(', ')
        },
        
        
        visitArrayEntry : function () {
            this.write(', ')
        }
    },
    
    
    my : {
        
        has : {
            HOST        : null
        },
        
        
        methods : {
            
            stringify : function (value, maxDepth) {
                var visitor     = new this.HOST({
                    maxDepth        : maxDepth || 4
                })
                
                visitor.visit(value, 0)
                
                return visitor.result.join('')
            }
        }
    }
})
;
Class('Siesta.Util.Queue', {
    
    has     : {
        // array of Objects, each containing arbitrary data about queue step. Possibly keys:
        // `processor` - an individual processor function for this step
        // can also be provided for whole queue
        // will receive the: (stepData, index, queue)
        // `isAsync` - when provided, the `next` function will be also embedded,
        // which should be called manually
        // `interval` - the delay after step (except for asynchronous)
        steps                   : Joose.I.Array,
        
        interval                : 100,
        callbackDelay           : 0,
        // setTimeout
        deferer                 : { required : true },
        // clearTimeout - only required when "abort" is planned / possible
        deferClearer            : null,
        
        processor               : null,
        processorScope          : null,
        
        currentTimeout          : null,
        callback                : null,
        scope                   : null,
        isAborted               : false,
        
        observeTest             : null
    },
    
    
    methods : {
        
        // step is an object with
        // { 
        //      processor : func, 
        //      processorScope : obj,
        //      next : func (in case of async step, will be populated by queue)
        // }
        
        addStep : function (stepData) {
            this.addSyncStep(stepData)
        },
        
        
        addSyncStep : function (stepData) {
            this.steps.push(stepData)
        },
        
        
        addAsyncStep : function (stepData) {
            stepData.isAsync = true
            
            this.steps.push(stepData)
        },
        
        
        run : function (callback, scope) {
            this.callback   = callback
            this.scope      = scope
            
            // abort the queue, if the provided test instance has finalized (probably because of exception)
            this.observeTest && this.observeTest.on('testfinalize', function () { this.abort(true) }, this, { single : true })
            
            this.doSteps(this.steps.slice(), callback, scope)
        },
        
        
        abort : function (ignoreCallback) {
            this.isAborted      = true
            
            var deferClearer    = this.deferClearer
            
            if (!deferClearer) throw "Need `deferClearer` to be able to `abort` the queue"
            
            deferClearer(this.currentTimeout)
            
            if (!ignoreCallback) this.callback.call(this.scope || this)
        },
        
        
        doSteps : function (steps, callback, scope) {
            this.currentTimeout = null
            
            var me          = this
            var deferer     = this.deferer
            var step        = steps.shift()
            
            if (step) {
                var processor       = step.processor || this.processor
                var processorScope  = step.processorScope || this.processorScope
                
                var index           = this.steps.length - steps.length - 1
                
                if (!processor) throw new Error("No process function found for step: " + index)
                
                if (step.isAsync) {
                    var next = step.next = function () {
                        me.doSteps(steps, callback, scope)
                    }
                    
                    // processor should call `next` to continue
                    processor.call(processorScope || me, step, index, this, next)
                } else {
                    
                    processor.call(processorScope || me, step, index, this)
                    
                    if (this.isAborted) return
                    
                    var interval = step.interval || me.interval
                    
                    if (interval) 
                        this.currentTimeout = deferer(function () {
                            me.doSteps(steps, callback, scope)    
                        }, interval)
                    else
                        me.doSteps(steps, callback, scope)
                }
                
                
            } else {
                if (callback)
                    if (this.callbackDelay)
                        deferer(function () {
                            callback.call(scope || this)
                        }, this.callbackDelay)
                    else
                        callback.call(scope || this)
            }
        }
    }
})
;
Class('Siesta.Util.XMLNode', {
    
    has     : {
        children        : Joose.I.Array,
        
        tag             : { required : true },
        attributes      : Joose.I.Object,
        
        textContent     : null,
        
        escapeTable     : {
            
            init    : {
                '&'     : '&amp;', 
                '<'     : '&lt;', 
                '>'     : '&gt;', 
                '"'     : '&quot;'
            }
        }
        
    },
    
    
    methods : {
        
        escapeXml : function (s) {
            var me = this
            
            return typeof s != 'string' ? s : s.replace(/[&<>"]/g, function (match) {
                return me.escapeTable[ match ]
            })
        },
        
        
        toString : function () {
            var me                  = this
            var childrenContent     = []
            
            Joose.A.each(this.children, function (child) {
                childrenContent.push(child.toString())
            })
            
            var attributesContent       = []
            
            Joose.O.each(this.attributes, function (value, name) {
                attributesContent.push(name + '="' + me.escapeXml(value) + '"')
            })
            
            // to have predictable order of attributes in tests
            attributesContent.sort()
            
            attributesContent.unshift(this.tag)
            
            
            return '<' + attributesContent.join(' ') + '>' + (this.textContent != null ? this.escapeXml(this.textContent) : '') + childrenContent.join('') + '</' + this.tag + '>' 
        },
        
        
        appendChild : function (child) {
            if (child instanceof Siesta.Util.XMLNode)
                child.parent    = this
            else
                child           = new Siesta.Util.XMLNode(Joose.O.extend(child, { parent : this }))
                
            this.children.push(child)
            
            return child
        },
        
        
        setAttribute : function (name, value) {
            this.attributes[ name ] = value
        }
    }
})
;
Class('Siesta.Content.Resource', {
    
    has : {
        url             : null,
        
        content         : null
    },
    
    
    methods : {
        
        asHTML : function () {
            throw "Abstract method called"
        },
        
        
        asDescriptor : function () {
            throw "Abstract method called"
        },
        
        
        // todo should check same-origin 
        canCache : function () {
        }
        
    }
        
})
//eof Siesta.Result

;
Class('Siesta.Content.Resource.CSS', {
    
    isa     : Siesta.Content.Resource,
    
    has     : {
    },
    
    
    methods : {
        
        asHTML : function () {
        },
        
        
        asDescriptor : function () {
            var res = {
                type        : 'css'
            }
            
            if (this.url)       res.url         = this.url
            if (this.content)   res.contnet     = this.content
            
            return res
        }
    }
        
})
//eof Siesta.Result

;
Class('Siesta.Content.Resource.JavaScript', {
    
    isa     : Siesta.Content.Resource,
    
    has     : {
    },
    
    
    methods : {
        
        asHTML : function () {
        },
        
        
        asDescriptor : function () {
            var res = {
                type        : 'js'
            }
            
            if (this.url)       res.url         = this.url
            if (this.content)   res.content     = this.content
            
            return res
        }
    }
        
})
//eof Siesta.Result

;
Class('Siesta.Content.Preset', {
    
    has : {
        preload                 : Joose.I.Array,
        
        resources               : Joose.I.Array
    },
    
    
    methods : {
        
        initialize : function () {
            var me              = this
            
            Joose.A.each(this.preload, function (preloadDesc) {
                
                me.addResource(preloadDesc)
            })
        },
        
        
        isCSS : function (url) {
            return /\.css(\?.*)?$/i.test(url)
        },
        
        
        getResourceFromDescriptor : function (desc) {
            if (typeof desc == 'string')
            
                if (this.isCSS(desc))
                    return new Siesta.Content.Resource.CSS({
                        url         : desc
                    })
                else
                    return new Siesta.Content.Resource.JavaScript({
                        url         : desc
                    })
                    
            else if (desc.text) 
                return new Siesta.Content.Resource.JavaScript({
                    content         : desc.text
                })
                    
            else if (desc.type == 'css') 
                return new Siesta.Content.Resource.CSS({
                    content         : desc.content
                })
                
            else if (desc.type == 'js') 
                return new Siesta.Content.Resource.JavaScript({
                    content         : desc.content
                })
                
            else 
                throw "Incorrect preload descriptor:" + desc 
        },
        
        
        addResource : function (desc) {
            var resource    = (desc instanceof Siesta.Content.Resource) && desc || this.getResourceFromDescriptor(desc)
            
            this.resources.push(resource)
            
            return resource
        },
        
        
        eachResource : function (func, scope) {
            return Joose.A.each(this.resources, func, scope || this)
        },
        
        
        // deprecated - seems preset don't need to know about scope providers
        prepareScope : function (scopeProvider, contentManager) {
            
            this.eachResource(function (resource) {
                
                if (contentManager.hasContentOf(resource))
                    scopeProvider.addPreload({
                        type        : (resource instanceof Siesta.Content.Resource.CSS) ? 'css' : 'js', 
                        content     : contentManager.getContentOf(resource)
                    })
                else 
                    scopeProvider.addPreload(resource.asDescriptor())
            })
        }
    }
        
})

;
Class('Siesta.Content.Manager', {
    
    has : {
        disabled        : false,
        
        presets         : {
            require     : true
        },
        
        urls            : Joose.I.Object
    },
    
    
    methods : {
        
        cache : function (callback, errback, ignoreErrors) {
            if (this.disabled) {
                callback && callback()
                
                return
            }
            
            var urls    = this.urls
            var me      = this
            
            Joose.A.each(this.presets, function (preset) {
                
                preset.eachResource(function (resource) {
                    
                    if (resource.url) urls[ resource.url ] = null
                })
            })
            
            var loadCount   = 0
            var errorCount  = 0
            
            var total       = 0
            Joose.O.each(urls, function () { total++ })
            
            if (total) 
                Joose.O.each(urls, function (value, url) {
                    
                    me.load(url, function (content) {
                        if (errorCount) return
                        
                        urls[ url ] = content
                        
                        if (++loadCount == total) callback && callback()
                    
                    }, ignoreErrors ? function () {
                        
                        if (++loadCount == total) callback && callback()
                        
                    } : function () {
                        errorCount++
                        
                        errback && errback(url)
                    })
                })
            else
                callback && callback()
        },
        
        
        load : function (url, callback, errback) {
            throw "abstract method `load` called"
        },
        
        
        hasContentOf : function (url) {
            if (url instanceof Siesta.Content.Resource) url = url.url
            
            return typeof this.urls[ url ] == 'string'
        },
        
        
        getContentOf : function (url) {
            if (url instanceof Siesta.Content.Resource) url = url.url
            
            return this.urls[ url ]
        }
    }
})

;
;
Class('Siesta.Result', {
    
    has : {
        description : null
    }
        
})
//eof Siesta.Result

;
Class('Siesta.Result.Diagnostic', {
    
    isa : Siesta.Result,
    
    has : {
        isSimulatedEvent : false,

        // Used by simulated events
        sourceX      : null,
        sourceY      : null,
        type         : null
    },

    methods : {
        
        toString : function () {
            var message = '# ' + this.description;
            return message;
        }
    }    
});

;
Class('Siesta.Result.Assertion', {
    
    isa : Siesta.Result,
    

    has : {
        name        : null,
        
        passed      : null,
        
        annotation  : null,
        
        index       : null,
        
        isSkipped   : false,
        isTodo      : false
    },
    
    
    methods : {
        
        toString : function () {
            var text = (this.isTodo ? 'TODO: ' : '') + (this.passed ? 'ok ' : 'fail ') + this.index + ' - ' + this.description
            
            if (this.annotation) text += '\n' + this.annotation
            
            return text
        }
    }
})

;
/**
@class Siesta.Test.Function

This is a mixin, with helper methods for testing functionality relating to DOM elements. This mixin is consumed by {@link Siesta.Test}

*/
Role('Siesta.Test.Function', {
    
    methods : {
         /**
         * This assertion passes if the function is called at least one time during the test life span.
         * 
         * @param {Function/String} fn The function itself or the name of the function on the host object (2nd argument)
         * @param {Object} host The "owner" of the method
         * @param {String} desc The description of the assertion.
         */
        isCalled : function(fn, obj, desc) {
            this.isCalledNTimes(fn, obj, 1, desc, true);
        },

        /**
         * This assertion passes if the function is called exactly (n) times during the test life span.
         * 
         * @param {Function/String} fn The function itself or the name of the function on the host object (2nd argument)
         * @param {Object} host The "owner" of the method
         * @param {Number} n The expected number of calls
         * @param {String} desc The description of the assertion.
         */
        isCalledNTimes : function(fn, obj, n, desc, isGreaterEqual) {
            var me      = this,
                prop    = typeof fn === "string" ? fn : me.getPropertyName(obj, fn);
            desc = desc ? (desc + ' ') : '';

            this.on('beforetestfinalizeearly', function () {
                if (counter === n || (isGreaterEqual && counter > n)) {
                    me.pass(desc || (prop + ' method was called exactly ' + n + ' times'));
                } else {
                    me.fail(desc || prop, {
                        assertionName       : 'isCalledNTimes ' + prop, 
                        got                 : counter, 
                        need                : n ,
                        needDesc            : ("Need " + (isGreaterEqual ? 'at least ' : 'exactly '))
                    });
                }
            });

            var counter = 0;
            fn = obj[prop];
            obj[prop] = function () { counter++; fn.apply(obj, arguments); };
        },

        /**
         * This assertion passes if the function is not called during the test life span.
         * 
         * @param {Function/String} fn The function itself or the name of the function on the host object (2nd argument)
         * @param {Object} host The "owner" of the method
         * @param {Number} n The expected number of calls
         * @param {String} desc The description of the assertion.
         */
        isntCalled : function(fn, obj, desc) {
            this.isCalledNTimes(fn, obj, 0, desc);
        },

        getPropertyName : function(host, obj) {
            for (var o in host) {
                if (host[o] === obj) return o;
            }
        }
    }
});
;
/**
@class Siesta.Test.Date

A mixin with the additinal assertions for dates. Being consumed by {@link Siesta.Test}

*/
Role('Siesta.Test.Date', {
    
    methods : {
        
        isDateEq: function (got, expectedDate, description) {
            this.isDateEqual.apply(this, arguments);
        },

        
        /**
         * This assertion passes when the 2 provided dates are equal and fails otherwise.
         * 
         * It has a synonym: `isDateEq`
         * 
         * @param {Date} got The 1st date to compare
         * @param {Date} expectedDate The 2nd date to compare
         * @param {String} description The description of the assertion
         */
        isDateEqual: function (got, expectedDate, description) {
            if (got - expectedDate === 0) {
                this.pass(description);
            } else {
                this.fail(description, {
                    assertionName   : 'isDateEqual',
                    
                    got         : got ? got.toString() : '',
                    gotDesc     : 'Got',
                    
                    need        : expectedDate.toString()
                });
            }
        }
    }
});
;
/**
@class Siesta.Test.More

A mixin with additional generic assertion methods, which can work cross-platform between browsers and NodeJS. 
Is being consumed by {@link Siesta.Test}, so all of them are available in all tests. 

*/
Role('Siesta.Test.More', {
    
    requires        : [ 'isFailed', 'typeOf', 'on' ],
    
    
    has : {
        autoCheckGlobals        : false,
        expectedGlobals         : Joose.I.Array,

        disableGlobalsCheck     : false,
        
        browserGlobals : { 
            init : [
                'console',
                'getInterface',
                'ExtBox1',
                '__IE_DEVTOOLBAR_CONSOLE_COMMAND_LINE',
                'seleniumAlert',
                'onload',
                'onerror', 
                'StartTest',
                // will be reported in IE8 after overriding
                'setTimeout',
                'clearTimeout'
            ]
        },
        
        /**
         * @cfg {Number} waitForTimeout Default timeout for `waitFor` (in milliseconds). Default value is 10000. 
         */
        waitForTimeout          : 10000,
        
        waitForPollInterval     : 100
    },
    
    
    methods : {
        
        /**
         * This assertion passes, when the comparison of 1st with 2nd, using `>` operator will return `true` and fails otherwise. 
         * 
         * @param {Number} value1 The 1st value to compare
         * @param {Number} value2 The 2nd value to compare
         * @param {String} desc The description of the assertion
         */
        isGreater : function (value1, value2, desc) {
            if (value1 > value2)
                this.pass(desc)
            else
                this.fail(desc, {
                    assertionName   : 'isGreater',
                    
                    got         : value1,
                    need        : value2,
                    
                    needDesc    : "Need, greater than"
                })
        },
        
        
        /**
         * This assertion passes, when the comparison of 1st with 2nd, using `<` operator will return `true` and fails otherwise. 
         * 
         * @param {Number} value1 The 1st value to compare
         * @param {Number} value2 The 2nd value to compare
         * @param {String} desc The description of the assertion
         */
        isLess : function (value1, value2, desc) {
            if (value1 < value2)
                this.pass(desc)
            else
                this.fail(desc, {
                    assertionName   : 'isLess',
                    
                    got         : value1,
                    need        : value2,
                    
                    needDesc    : "Need, less than"
                })
        },
        

        isGE : function () {
            this.isGreaterOrEqual.apply(this, arguments)
        },
        
        /**
         * This assertion passes, when the comparison of 1st with 2nd, using `>=` operator will return `true` and fails otherwise. 
         * 
         * It has a synonym - `isGE`.
         * 
         * @param {Number} value1 The 1st value to compare
         * @param {Number} value2 The 2nd value to compare
         * @param {String} desc The description of the assertion
         */
        isGreaterOrEqual : function (value1, value2, desc) {
            if (value1 >= value2)
                this.pass(desc)
            else
                this.fail(desc, {
                    assertionName   : 'isGreaterOrEqual',
                    
                    got         : value1,
                    need        : value2,
                    
                    needDesc    : "Need, greater or equal to"
                })
        },
        

        
        isLE : function () {
            this.isLessOrEqual.apply(this, arguments)
        },
        
        /**
         * This assertion passes, when the comparison of 1st with 2nd, using `<=` operator will return `true` and fails otherwise. 
         * 
         * It has a synonym - `isLE`.
         * 
         * @param {Number} value1 The 1st value to compare
         * @param {Number} value2 The 2nd value to compare
         * @param {String} desc The description of the assertion
         */
        isLessOrEqual : function (value1, value2, desc) {
            if (value1 <= value2)
                this.pass(desc)
            else
                this.fail(desc, {
                    assertionName   : 'isLessOrEqual',
                    
                    got         : value1,
                    need        : value2,
                    
                    needDesc    : "Need, less or equal to"
                })
        },
        
        
        /**
         * This assertion suppose to compare the numeric values. It passes when the passed values are approximately the same (the difference 
         * is withing a threshold). A threshold can be provided explicitly (when assertion is called with 4 arguments), 
         * or it will be set to 5% from the 1st value (when calling assertion with 3 arguments).
         * 
         * @param {Number} value1 The 1st value to compare
         * @param {Number} value2 The 2nd value to compare
         * @param {Number} threshHold The maximum allowed difference between values. This argument can be omited. 
         * @param {String} desc The description of the assertion
         */
        isApprox : function (value1, value2, threshHold, desc) {
            if (arguments.length == 3) {
                desc        = threshHold
                threshHold  = Math.abs(value1 * 0.05)
            }
            
            if (Math.abs(value2 - value1) < threshHold)
                this.pass(desc, value2 == value1 ? 'Exact match' : 'Match within treshhold: ' + threshHold)
            else
                this.fail(desc, {
                    assertionName       : 'isApprox', 
                    got                 : value1, 
                    need                : value2, 
                    needDesc            : 'Need approx',
                    annotation          : 'Threshold is: ' + threshHold
                })
        },
        
        
        /**
         * This assertion passes when the passed `string` matches to a regular expression `regex`. When `regex` is a string, 
         * assertion will check that it is a substring of `string`
         * 
         * @param {String} string The string to check for "likeness"
         * @param {String/RegExp} regex The regex against which to test the string, can be also a plain string
         * @param {String} desc The description of the assertion
         */
        like : function (string, regex, desc) {
            if (this.typeOf(regex) == "RegExp")
            
                if (string.match(regex))
                    this.pass(desc)
                else
                    this.fail(desc, {
                        assertionName       : 'like', 
                        got                 : string, 
                        need                : regex, 
                        needDesc            : 'Need string matching'
                    })
            else
             
                if (string.indexOf(regex) != -1)
                    this.pass(desc)
                else
                    this.fail(desc, {
                        assertionName       : 'like', 
                        got                 : string, 
                        need                : regex, 
                        needDesc            : 'Need string containing'
                    })
        },
        
        /**
         * This method is the opposite of 'like', it adds failed assertion, when the string matches the passed regex.
         * 
         * @param {String} string The string to check for "unlikeness"
         * @param {String/RegExp} regex The regex against which to test the string, can be also a plain string
         * @param {String} desc The description of the assertion
         */
        unlike : function(string, regex, desc) {
            if (this.typeOf(regex) == "RegExp")
            
                if (!string.match(regex))
                    this.pass(desc)
                else
                    this.fail(desc, {
                        assertionName       : 'unlike', 
                        got                 : string, 
                        need                : regex, 
                        needDesc            : 'Need string not matching'
                    })
            else
             
                if (string.indexOf(regex) == -1)
                    this.pass(desc)
                else
                    this.fail(desc, {
                        assertionName       : 'unlike', 
                        got                 : string, 
                        need                : regex, 
                        needDesc            : 'Need string not containing'
                    })
        },
        
        
        "throws" : function () {
            this.throwsOk.apply(this, arguments)
        },
        
        throws_ok : function () {
            this.throwsOk.apply(this, arguments)
        },
        
        /**
         * This assertion passes, when the `func` function throws the exception during executing, and the
         * stringified exception passes the 'like' assertion (with 'expected' parameter).
         * 
         * It has synonyms - `throws_ok` and `throws`.
         * 
         * @param {Function} func The function which supposed to throw an exception
         * @param {String/RegExp} expected The regex against which to test the stringified exception, can be also a plain string
         * @param {String} desc The description of the assertion
         */
        throwsOk : function (func, expected, desc) {
            if (this.typeOf(func) != 'Function') throw new Error('throws_ok accepts a function as 1st argument')
            
            var e = this.global.StartTest.exceptionCatcher(func)
            
            // assuming no one will throw undefined exception..
            if (e === undefined) {
                this.fail(desc, {
                    assertionName       : 'throws_ok', 
                    annotation          : 'Function did not throw the exception'
                })
                
                return
            }
            
            if (e instanceof this.global.StartTest.testError)
                //IE uses non-standard 'description' property for error msg
                e = e.message || e.description
                
            e = '' + e
                
            if (this.typeOf(expected) == "RegExp")
            
                if (e.match(expected))
                    this.pass(desc)
                else
                    this.fail(desc, {
                        assertionName       : 'throws_ok', 
                        got                 : e, 
                        gotDesc             : 'Exception stringifies to',
                        need                : expected, 
                        needDesc            : 'Need string matching'
                    })
            else
             
                if (e.indexOf(expected) != -1)
                    this.pass(desc)
                else
                    this.fail(desc, {
                        assertionName       : 'throws_ok', 
                        got                 : e, 
                        gotDesc             : 'Exception stringifies to',
                        need                : expected, 
                        needDesc            : 'Need string containing'
                    })
        },
        
        
        
        lives_ok : function () {
            this.livesOk.apply(this, arguments)
        },
        
        lives : function () {
            this.livesOk.apply(this, arguments)
        },
        
        /**
         * This assertion passes, when the supplied `func` function doesn't throw the exception during execution.
         * 
         * This method has a synonyms: `lives_ok` and `lives`
         * 
         * @param {Function} func The function which supposed to not throw an exception
         * @param {String} desc The description of the assertion
         */
        livesOk : function (func, desc) {
            if (this.typeOf(func) != 'Function') {
                func = [ desc, desc = func][ 0 ]
            }
            
            var e = this.global.StartTest.exceptionCatcher(func)
            
            if (e === undefined) 
                this.pass(desc)
            else
                this.fail(desc, {
                    assertionName       : 'lives_ok', 
                    annotation          : 'Function threw an exception: ' + e
                })
        },
        
        
        isa_ok : function (value, className, desc) {
            this.isaOk(value, className, desc)
        },
        
        /**
         * This assertion passes, when the supplied `value` is the instance of the `className`. The check is performed with
         * `instanceof` operator. The `className` parameter can be supplied as class constructor or as string, representing the class
         * name. In the latter case the `class` will eval'ed to receive the class constructor.
         * 
         * This method has a synonym: isa_ok
         * 
         * @param {Mixed} value The value to check for 'isa' relationship
         * @param {Class/String} className The class to check for 'isa' relationship with `value`
         * @param {String} desc The description of the assertion
         */
        isaOk : function (value, className, desc) {
            try {
                if (this.typeOf(className) == 'String') className = eval(className)
            } catch (e) {
                this.fail(desc, {
                    assertionName       : 'isa_ok', 
                    annotation          : "Exception [" + e + "] caught, while evaluating the class name [" + className + "]"
                })
                
                return
            }
            
            if (value instanceof className) 
                this.pass(desc)
            else
                this.fail(desc, {
                    assertionName       : 'isa_ok', 
                    got                 : value, 
                    need                : String(className), 
                    needDesc            : 'Need, instance of'
                })
        },
        
        
//        isString : function () {
//        },
        
//        isObject : function () {
//        },
        
//        isArray : function () {
//        },

//        isNumber : function () {
//        },

//        isBoolean : function () {
//        },

//        isDate : function () {
//        },

//        isRegExp : function () {
//        },
        
        
        countKeys : function (object) {
            var counter = 0
            
            Joose.O.eachOwn(object, function () {
                counter++
            })
            
            return counter
        },
        
        
        /**
         * This method performs a deep comparison of the passed JSON objects. Objects must not contain cyclic references.
         * You can use this method in your own assertions.
         * 
         * @param {Object} obj1 The 1st object to compare
         * @param {Object} obj2 The 2nd object to compare
         * @param {Boolean} strict When passed the `true` value, the comparison of the primitive values will be performed with the 
         * `===` operator (so [ 1 ] and [ "1" ] object will be different).
         * @return {Boolean} `true` if the passed objects are equal
         */
        compareObjects : function (obj1, obj2, strict) {
            if (strict) {
                if (obj1 === obj2) return true
            } else 
                if (obj1 == obj2) return true
                
            
            var type1 = this.typeOf(obj1)
            var type2 = this.typeOf(obj2)
            
            if (type1 != type2) return false
            
            if (type1 == 'Array')
                if (obj1.length != obj2.length) 
                    return false
                else {
                    for (var i = 0; i < obj1.length; i++)
                        if (!this.compareObjects(obj1[ i ], obj2[ i ], strict)) return false
                    
                    return true
                }
            
            var me = this
                
            if (type1 == 'Object')
                if (this.countKeys(obj1) != this.countKeys(obj2)) 
                    return false
                else {
                    var res = Joose.O.eachOwn(obj1, function (value, name) {
                        
                        if (!me.compareObjects(value, obj2[ name ], strict)) return false
                    })
                    
                    return res === false ? false : true
                }
                
            if (type1 == 'Date') return !Boolean(obj1 - obj2)
        }, 
        
        
        is_deeply : function (obj1, obj2, desc) {
            this.isDeeply.apply(this, arguments)
        },
        
        /**
         * This assertion passes when in-depth comparison of 1st and 2nd arguments (which are assumed to be JSON objects) shows that they are equal.
         * Comparison is performed with '==' operator, so `[ 1 ]` and `[ "1" ] objects will be equal. The objects should not contain cyclic references.
         * 
         * This method has a synonym: `is_deeply`
         * 
         * @param {Object} obj1 The 1st object to compare
         * @param {Object} obj2 The 2nd object to compare
         * @param {String} desc The description of the assertion
         */
        isDeeply : function (obj1, obj2, desc) {
            if (this.compareObjects(obj1, obj2))
                this.pass(desc)
            else
                this.fail(desc, {
                    assertionName       : 'isDeeply', 
                    got                 : obj1, 
                    need                : obj2 
                })
        },
        
        
        /**
         * This assertion passes when in-depth comparison of 1st and 2nd arguments (which are assumed to be JSON objects) shows that they are equal.
         * Comparison is performed with '===' operator, so `[ 1 ]` and `[ "1" ] objects will be different. The objects should not contain cyclic references.
         * 
         * @param {Object} obj1 The 1st object to compare
         * @param {Object} obj2 The 2nd object to compare
         * @param {String} desc The description of the assertion
         */
        isDeeplyStrict : function (obj1, obj2, desc) {
            if (this.compareObjects(obj1, obj2, true))
                this.pass(desc)
            else
                this.fail(desc, {
                    assertionName       : 'isDeeplyStrict', 
                    got                 : obj1, 
                    need                : obj2 
                })
        },
        
        expectGlobal : function () {
            this.expectGlobals.apply(this, arguments)
        },
        
        /**
         * This method accepts a variable number of names of expected properties in the global scope. When verifying the globals with {@link #verifyGlobals}
         * assertions, the expected gloabls will not be counted as failed assertions.
         * 
         * This method has a synonym with singular name: `expectGlobal`
         * 
         * @param {String} name1 The name of global property
         * @param {String} name2 The name of global property
         * @param {String} nameN The name of global property
         */
        expectGlobals : function () {
            this.expectedGlobals.push.apply(this.expectedGlobals, arguments)
        },
        
        /**
         * This method accepts a variable number of names of expected properties in the global scope and then performs a globals check. 
         *
         * It will scan all globals properties in the scope of test and compare them with the list of expected globals. Expected globals can be provided with:
         * {@link #expectGlobals} method or {@link Siesta.Harness#expectedGlobals expectedGlobals} configuration option of harness.
         * 
         * You can enable this assertion to automatically happen at the end of each test, using {@link Siesta.Harness#autoCheckGlobals autoCheckGlobals} option of the harness.
         * 
         * @param {String} name1 The name of global property
         * @param {String} name2 The name of global property
         * @param {String} nameN The name of global property
         */
        verifyGlobals : function () {
            if (this.disableGlobalsCheck) {
                this.diag('Testing leakage of global variables is not supported on this platform')
                
                return
            }
            
            this.expectGlobals.apply(this, arguments)
            
            var me                  = this
            var expectedGlobals     = {}
            var failed              = false
            
            Joose.A.each(this.expectedGlobals.concat(this.browserGlobals), function (name) { expectedGlobals[ name ] = true })
            
            this.diag('Global variables')
            
            for (var name in this.global) {
                
                if (!expectedGlobals[ name ]) {
                    me.fail('Unexpected global found', 'Global name: ' + name)
                    
                    failed      = true
                }
            }
            
            if (!failed) this.pass('No unexpected global variables found')
        },

        /**
         * Waits for passed checker method to return true (or any non-false value, like for example DOM element or array), and calls the callback when this happens.
         * As an additional feature, the callback will receive the result from the checker method as the 1st argument.  
         * 
         * @param {Function/Number} method Either a function which should return true when a certain condition has been fulfilled, or a number of ms to wait before calling the callback.
         * @param {Function} callback A function to call when the condition has been met. Will receive a result from checker function.
         * @param {Object} scope The scope for the callback
         * @param {Int} timeout The maximum amount of time (in milliseconds) to wait for the condition to be fulfilled. Defaults to the {@link Siesta.Test.ExtJS#waitForTimeout} value. 
         */
        waitFor : function (method, callback, scope, timeout)  {
            var async       = this.beginAsync(),
                me          = this;
                
            
            var originalSetTimeout      = me.originalSetTimeout;
            var originalClearTimeout    = me.originalClearTimeout;
            
            var pollTimeout
            
            // stop polling, if this test instance has finalized (probably because of exception)
            this.on('testfinalize', function () {
                originalClearTimeout(pollTimeout)
            }, null, { single : true })

            if (this.typeOf(method) == 'Number') {
                pollTimeout = originalSetTimeout(function() { me.endAsync(async); callback.call(scope || me); }, method);
            } else {

                timeout         = timeout || this.waitForTimeout
            
                var startDate           = new Date()
            
                var pollFunc    = function () {
                    if (new Date() - startDate > timeout) {
                    
                        me.fail('Timeout while waiting for condition', {
                            assertionName       : 'waitFor',
                            annotation          : 'Condition was not fullfilled during ' + timeout + 'ms'
                        })
                    
                        return
                    }
                
                    try {
                        var result = method.call(scope || me);
                    } catch (e) {
                        me.endAsync(async);
                    
                        me.fail('waitFor checker threw an exception', {
                            assertionName       : 'waitFor',
                            got                 : e.toString(),
                            gotDesc             : "Exception"
                        })
                    
                        return
                    }
                
                    if (result != null && result !== false) {
                        me.endAsync(async);
                    
                        callback.call(scope || me, result);
                    } else 
                        pollTimeout = originalSetTimeout(pollFunc, me.waitForPollInterval)
                }
            
                pollFunc()
            }
        },
        
        
        /**
         * This method accept either variable number of arguments (steps) or the array of them. Each step should be either a function or configuration object for test actions. 
         * These functions / actions will be executed in order.
         * 
         * If step is a function, as the 1st argument, it will receive a callback to call when the step is completed. As the 2nd and further arguments, the step function will receive the
         * arguments passed to the previous callback.
         * 
         * The last step will receive a no-op callback, which can be ignored or still called.  
         * 
         * If a step is presented with action configuration object, then the callback will be called by the action class automatically. Configuration object should contain the "action" property,
         * specifying the action class and some other config options (depending from the action class). 
         * 
         * Its better to see how it works on the example. For example, when using using only functions:
         
    t.chain(
        // function receives a callback as 1st argument
        function (next) {
            // we pass that callback to the "click" method
            t.click(buttonEl, next)
        },
        function (next) {
            t.type(fieldEl, 'Something', next)
        },
        function (next) {
            t.is(fieldEl.value == 'Something', 'Correct value in the field')
            
            // call the callback with some arguments
            next('foo', 'bar')  
        }, 
        // those arguments are now available as arguments of next step
        function (next, value1, value2) {
            t.is(value1, 'foo', 'The arguments for the callback are translated to the arguments of the step')
            t.is(value2, 'bar', 'The arguments for the callback are translated to the arguments of the step')
        }
    )

         * 
         * The same example, using action configuration objects for first 2 steps:
         
    t.chain(
        {
            action      : 'click',
            target      : buttonEl
        },
        {
            action      : 'type',
            target      : fieldEl,
            text        : 'Something'
        },
        function (next) {
            t.is(fieldEl.value == 'Something', 'Correct value in the field')
            
            next('foo', 'bar')  
        }, 
        ...
    )
    
         *  
         *  For the list of available actions please refer to the classes in the Siesta.Test.Action namespace. Please note, that each step is expected to complete within the {@link Siesta.Harness#defaultTimeout} time.
         *  
         *  @param {Function/Object/Array} step1 The function to execute or action configuration, or the array of such
         *  @param {Function/Object} step2 The function to execute or action configuration
         *  @param {Function/Object} stepN The function to execute or action configuration
         */
        chain : function () {
            var me          = this
            
            var queue       = new Siesta.Util.Queue({
                deferer         : this.originalSetTimeout,
                deferClearer    : this.originalClearTimeout,
                
                interval        : this.actionDelay,
                
                observeTest     : this
            })
            
            // inline any arrays in the arguments into one array
            var steps   = Array.prototype.concat.apply([], arguments)
            
            var len     = steps.length
            var args    = []
            
            Joose.A.each(steps, function (step, index) {
                
                var isLast      = index == len - 1
                
                queue.addAsyncStep({
                    processor : function (data) {
                        var async       = me.beginAsync()
                        
                        var nextFunc    = function () {
                            me.endAsync(async)
                            
                            args    = Array.prototype.slice.call(arguments)
                            
                            data.next()
                        }
                        
                        if (me.typeOf(step) == 'Function') {
                            // if the last step is a function - then provide "null" as the "next" callback for it
                            args.unshift(isLast ? function () {} : nextFunc)
                            
                            step.apply(me, args)
                            
                            // and finalize the async frame manually, as the "nextFunc" for last step will never be called
                            isLast && me.endAsync(async)
                            
                        } else {
                            if (!step.args) step.args   = args
                            
                            step.next       = nextFunc
                            step.test       = me
                            
                            var action      = Siesta.Test.ActionRegistry.create(step)
                            
                            action.process()
                        }
                    } 
                })
            })
            
            queue.run()
        }
    },
    
    
    after : {
        
        initialize        : function () {
            
            this.on('beforetestfinalize', function () {
                
                if (this.autoCheckGlobals && !this.isFailed()) this.verifyGlobals()
                
            }, this)
        }
    }
        
})
//eof Siesta.Test.More
;
/**
@class Siesta.Test
@mixin Siesta.Test.More
@mixin Siesta.Test.Date 
@mixin Siesta.Test.Function 

`Siesta.Test` is a base testing class in Siesta hierarchy. Its not supposed to be created manually, instead, the harness will create it for you.

This file is a reference only, for a getting start guide and manual, please refer to <a href="#!/guide/siesta_getting_started">Getting Started Guide</a>.

Please note: Each test will be run in **its own**, completely **isolated** and **clean** global scope. **There is no need to cleanup anything**.

SYNOPSIS
========

    StartTest(function(t) {
        t.diag("Sanity")
        
        t.ok($, 'jQuery is here')
        
        t.ok(Your.Project, 'My project is here')
        t.ok(Your.Project.Util, '.. indeed')
        
        setTimeout(function () {
        
            t.ok(true, "True is ok")
        
        }, 500)
    })    


*/

Class('Siesta.Test', {
    
    does        : [ 
        Siesta.Test.More,
        Siesta.Test.Date,
        Siesta.Test.Function,
        JooseX.Observable
    ],
    
    
    has        : {
        url                 : { required : true },
        urlExtractRegex     : {
            is      : 'rwc',
            lazy    : function () {
                return new RegExp(this.url.replace(/([.*+?^${}()|[\]\/\\])/g, "\\$1") + ':(\\d+)')
            }
        },
        
        assertPlanned       : null,
        assertCount         : 0,
        
        results             : Joose.I.Array,
        
        run                 : { required : true },
        
        harness             : { required : true },
        
        // indicates that test has threw an exception (not related to failed assertions)
        failed              : false,
        failedException     : null,
        
        startDate           : null,
        endDate             : null,
        
        contentManager      : null,
        
        // the scope provider for the context of the test page
        scopeProvider       : null,
        // the context of the test page
        global              : { required : true },
        
        // the scope provider for the context of the test script
        // usually the same as the `scopeProvider`, but may be different in case of using `separateContext` option
        scriptScopeProvider : null,
        
        transparentEx       : false,
        
        needDone            : true,
        isDone              : false,
        
        defaultTimeout      : 15000,
        
        timeoutsCount       : 1,
        timeoutIds          : Joose.I.Object,
        idsToIndex          : Joose.I.Object,
        waitTitles          : Joose.I.Object,
        
        
        // indicates that test function has completed the execution (test may be still running due to async)
        processed           : false,
        
        callback            : null,
        
        // Nbr of exceptions detected while running the test
        nbrExceptions       : 0,
        testEndReported     : false,
        
        // only used for testing itself, otherwise should be always `true`
        needToCleanup           : true,
        
        overrideSetTimeout      : true,
        
        originalSetTimeout      : { required : true },
        originalClearTimeout    : { required : true }
    },
    
    
    methods : {
        
        toString : function() {
            return this.url
        },
        
        
        // deprecated
        plan : function (value) {
            if (this.assertPlanned != null) throw new Error("Test plan can't be changed")
            
            this.assertPlanned = value
        },
        
        
        addResult : function (result) {
            // check for class name for cross-context instances (happens during self-testing)
            var isAssertion = (result instanceof Siesta.Result.Assertion) || result.meta.name == 'Siesta.Result.Assertion'
            
            // only allow to add diagnostic results and todo results after the end of test
            // and only if "needDone" is enabled
            if (isAssertion && (this.isDone || this.isFinished()) && !result.isTodo)
                if (!this.testEndReported) {
                    this.testEndReported = true
                    
                    this.fail("Adding assertions after the test has finished.")
                }
            
            if (isAssertion) result.index = ++this.assertCount
            
            this.results.push(result)
            
            this.harness.onTestUpdate(this, result)
            
            /**
             * This event is fired when the individual test case receives new result (assertion or diagnostic message). 
             * 
             * This event bubbles up to the {@link Siesta.Harness harness}, you can observe it on harness as well. 
             * 
             * @event testupdate
             * @member Siesta.Test
             * @param {JooseX.Observable.Event} event The event instance
             * @param {Siesta.Test} test The test instance that just has started
             * @param {Siesta.Result} result The new result. Instance of Siesta.Result.Assertion or Siesta.Result.Diagnostic classes
             */
            this.fireEvent('testupdate', this, result)
        },
        

        /**
         * This method output the diagnostic message.  
         * @param {String} desc The text of diagnostic message
         */
        diag : function (desc) {
            this.addResult(new Siesta.Result.Diagnostic({
                description : desc
            }))
        },
        
        
        /**
         * This method add the passed assertion to this test.
         * 
         * @param {String} desc The description of the assertion
         * @param {String} annotation The additional description how exactly this assertion passes. Will be shown with monospace font.
         */
        pass : function (desc, annotation) {
            this.addResult(new Siesta.Result.Assertion({
                passed      : true,
                
                annotation  : annotation,
                description : desc
            }))
        },
        
        
        /**
         * This method returns a result of `Object.prototype.toString` applied to the passed argument. The `[object` and trailing `]` are trimmed. 
         * 
         * @param {Mixed} object
         * @return {String} The name of the "type" for this object.
         */
        typeOf : function (object) {
            return Object.prototype.toString.call(object).replace(/^\[object /, '').replace(/\]$/, '')
        },
        
        /**
         * This method add the failed assertion to this test.
         * 
         * @param {String} desc The description of the assertion
         * @param {String/Object} annotation The additional description how exactly this assertion fails. Will be shown with monospace font.
         * 
         * Can be either string or an object with the following properties. In the latter case a string will be constructed from the properties of the object.
         * 
         * - `assertionName` - the name of assertion, will be shown in the 1st line, along with originating source line (in FF and Chrome only)
         * - `got` - an arbitrary JavaScript object, when provided will be shown on the next line
         * - `need` - an arbitrary JavaScript object, when provided will be shown on the next line
         * - `gotDesc` - a prompt for "got", default value is "Got", but can be for example: "We have" 
         * - `needDesc` - a prompt for "need", default value is "Need", but can be for example: "We need"
         * - `annotation` - A text to append on the last line, can contain some additional explanations
         * 
         *  The "got" and "need" values will be stringified to the "not quite JSON" notation. Notably the points of circular references will be 
         *  marked with `[Circular]` marks and the values at 4th (and following) level of depth will be marked with triple points: `[ [ [ ... ] ] ]`  
         */
        fail : function (desc, annotation) {
            if (annotation && this.typeOf(annotation) != 'String') {
                var strings             = []
                
                var params              = annotation
                var annotation          = params.annotation
                var assertionName       = params.assertionName
                var hasGot              = params.hasOwnProperty('got')
                var hasNeed             = params.hasOwnProperty('need')
                var gotDesc             = params.gotDesc || 'Got'
                var needDesc            = params.needDesc || 'Need'
                var sourceLine          = this.getSourceLine()
                
                if (assertionName || sourceLine) strings.push(
                    'Failed assertion ' + (assertionName ? '[' + assertionName + '] ' : '') + this.formatSourceLine(sourceLine)
                )
                
                if (hasGot && hasNeed) {
                    var max         = Math.max(gotDesc.length, needDesc.length)
                    
                    gotDesc         = this.appendSpaces(gotDesc, max - gotDesc.length + 1)
                    needDesc        = this.appendSpaces(needDesc, max - needDesc.length + 1)
                }
                
                if (hasGot)     strings.push(gotDesc   + ': ' + Siesta.Util.Serializer.stringify(params.got))
                if (hasNeed)    strings.push(needDesc  + ': ' + Siesta.Util.Serializer.stringify(params.need))
                
                if (annotation) strings.push(annotation)
                
                annotation      = strings.join('\n')
            }
            
            this.addResult(new Siesta.Result.Assertion({
                name        : assertionName,
                passed      : false,
                
                annotation  : annotation,
                description : desc
            }))
        },
        
        
        getSource : function () {
            return this.contentManager.getContentOf(this.url)
        },
        
        
        getSourceLine : function () {
            try {
                throw new Error()
            } catch (e) {
                if (e.stack) {
                    var match       = e.stack.match(this.urlExtractRegex())
                    
                    if (match) return match[ 1 ]
                }
                
                // TODO
//                if (typeof console != 'udefined' && console.trace) {
//                    var trace = console.trace()
//                }
                
                return null
            }
        },
        
        
        getStackTrace : function (e) {
            if (Object(e) !== e)    return null
            if (!e.stack)           return null
            
            var text            = e.stack
            var traceLineRegex  = /\((.*?)\)@(.*?):(\d+)/g;
            var match
            
            var result      = []
            
            for (var i = 0; match = traceLineRegex.exec(text); i++)
                if (i)
                    // other lines
                    result.push('at line ' + match[ 3 ] + ' of ' + match[ 2 ])
                else
                    // first line
                    result.push(match[ 1 ] + ' at line ' + match[ 3 ] + ' of ' + match[ 2 ])
                
            if (!result.length) return null
            
            return result
        },
        
        
        formatSourceLine : function (sourceLine) {
            return sourceLine ? 'at line ' + sourceLine + ' of ' + this.url : ''
        },
        
        
        appendSpaces : function (str, num) {
            var spaces      = ''
            
            while (num--) spaces += ' '
            
            return str + spaces
        },
        
        
        eachAssertion : function (func, scope) {
            scope       = scope || this
            
            var index   = 0
            
            Joose.A.each(this.results, function (result) {
                // check for class name for cross-context instances (happens during self-testing)
                if ((result instanceof Siesta.Result.Assertion) || result.meta.name == 'Siesta.Result.Assertion') func.call(scope, result, index++)
            })
        },
        
        
        /**
         * This assertion passes when the supplied `value` evalutes to `true` and fails otherwise.
         *  
         * @param {Mixed} value The value, indicating wheter assertions passes or fails
         * @param {String} desc The description of the assertion
         */
        ok : function (value, desc) {
            if (value) 
                this.pass(desc)
            else 
                this.fail(desc, {
                    assertionName       : 'ok', 
                    got                 : value, 
                    annotation          : 'Need "truthy" value'
                })
        },
        
        
        notok : function () {
            this.notOk.apply(this, arguments)
        },
        
        /**
         * This assertion passes when the supplied `value` evalutes to `false` and fails otherwise.
         * 
         * It has a synonym - `notok`.
         *  
         * @param {Mixed} value The value, indicating wheter assertions passes or fails
         * @param {String} desc The description of the assertion
         */
        notOk : function (value, desc) {
            if (!value) 
                this.pass(desc)
            else 
                this.fail(desc, {
                    assertionName       : 'notOk', 
                    got                 : value, 
                    annotation          : 'Need "falsy" value'
                })
        },
        
        
        /**
         * This assertion passes when the comparison of 1st and 2nd arguments with `==` operator returns true and fails otherwise.
         * 
         * @param {Mixed} got The value "we have" - will be shown as "Got:" in case of failure
         * @param {Mixed} expected The value "we expect" - will be shown as "Need:" in case of failure
         * @param {String} desc The description of the assertion
         */
        is : function (got, expected, desc) {
            if (got instanceof this.global.Date) {
                this.isDateEqual(got, expected, desc);
            } else if (got == expected)
                this.pass(desc)
            else
                this.fail(desc, {
                    assertionName       : 'is', 
                    got                 : got, 
                    need                : expected 
                })
        },
        

        
        isnot : function () {
            this.isNot.apply(this, arguments)
        },

        isnt : function () {
            this.isNot.apply(this, arguments)
        },
        
        
        /**
         * This assertion passes when the comparison of 1st and 2nd arguments with `!=` operator returns true and fails otherwise.
         * It has synonyms - `isnot` and `isnt`.
         * 
         * @param {Mixed} got The value "we have" - will be shown as "Got:" in case of failure
         * @param {Mixed} expected The value "we expect" - will be shown as "Need:" in case of failure
         * @param {String} desc The description of the assertion
         */
        isNot : function (got, expected, desc) {
            if (got != expected)
                this.pass(desc)
            else
                this.fail(desc, {
                    assertionName       : 'isnt', 
                    got                 : got, 
                    need                : expected,
                    needDesc            : 'Need, not'
                })
        },
        

        /**
         * This assertion passes when the comparison of 1st and 2nd arguments with `===` operator returns true and fails otherwise.
         * 
         * @param {Mixed} got The value "we have" - will be shown as "Got:" in case of failure
         * @param {Mixed} expected The value "we expect" - will be shown as "Need:" in case of failure
         * @param {String} desc The description of the assertion
         */
        isStrict : function (got, expected, desc) {
            if (got === expected)
                this.pass(desc)
            else
                this.fail(desc, {
                    assertionName       : 'isStrict', 
                    got                 : got, 
                    need                : expected,
                    needDesc            : 'Need strictly'
                })
        },

        
        isntStrict : function () {
            this.isNotStrict.apply(this, arguments)
        },
        
        /**
         * This assertion passes when the comparison of 1st and 2nd arguments with `!==` operator returns true and fails otherwise.
         * It has synonyms - `isntStrict`.
         * 
         * @param {Mixed} got The value "we have" - will be shown as "Got:" in case of failure
         * @param {Mixed} expected The value "we expect" - will be shown as "Need:" in case of failure
         * @param {String} desc The description of the assertion
         */
        isNotStrict : function (got, expected, desc) {
            if (got !== expected)
                this.pass(desc)
            else
                this.fail(desc, {
                    assertionName       : 'isntStrict', 
                    got                 : got, 
                    need                : expected,
                    needDesc            : 'Need, strictly not'
                })
        },
        
        
        /**
         * This method starts the "asynchronous frame". The test will wait for all asynchronous frames to complete before it will finalize.
         * The frame can be finished with the {@link #endWait} call. Unlike the {@link #beginAsync}, this method requires you to provide
         * the unique id for the asynchronous frame. 
         * 
         * For example:
         * 
         *      t.wait("require")
         *      
         *      Ext.require('Some.Class', function () {
         *      
         *          t.ok(Some.Class, 'Some class was loaded')
         *          
         *          t.endWait("require")
         *      })
         * 
         * 
         * @param {String} title The unique id for the asynchronous frame.
         * @param {String} howLong The maximum time (in ms) to wait until force the finalization of this async frame. Optional. Default time is 15000 ms.
         */
        wait : function (title, howLong) {
            if (this.waitTitles.hasOwnProperty(title)) throw new Error("Already doing a `wait` with title [" + title + "]")
            
            return this.waitTitles[ title ] = this.beginAsync(howLong)
        },
        
        
        /**
         * This method finalize the "asynchronous frame" started with {@link #wait}.
         * 
         * @param {String} title The id of frame to finalize, which was previously passed to {@link #wait} method
         */
        endWait : function (title) {
            if (!this.waitTitles.hasOwnProperty(title)) throw new Error("There were no call to `wait` with title [" + title + "]")
            
            this.endAsync(this.waitTitles[ title ])
            
            delete this.waitTitles[ title ]
        },
        
        
        
        /**
         * This method starts the "asynchronous frame". The test will wait for all asynchronous frames to complete before it will finalize.
         * The frame can be finished with the {@link #endAsync} call.
         * 
         * For example:
         * 
         *      var async = t.beginAsync()
         *      
         *      Ext.require('Some.Class', function () {
         *      
         *          t.ok(Some.Class, 'Some class was loaded')
         *          
         *          t.endAsync(async)
         *      })
         * 
         * 
         * @param {Number} time The maximum time (in ms) to wait until force the finalization of this async frame. Optional. Default time is 15000 ms.
         * @return {Object} The frame object, which can be used in {@link #endAsync} call
         */
        beginAsync : function (time) {
            var me                      = this
            var originalSetTimeout      = this.originalSetTimeout
            
            // in NodeJS `setTimeout` returns an object and not a simple ID, so we try hard to store that object under unique index
            // also using `setTimeout` from the scope of test - as timeouts in different scopes in browsers are mis-synchronized
            // can't just use `this.originalSetTimeout` because of scoping issues
            var timeoutId = originalSetTimeout(function () {
                me.endAsync(index)
            }, time || this.defaultTimeout)
            
            var index = this.timeoutsCount++
            
            this.timeoutIds[ index ] = timeoutId
            
            return index
        },
        
        
        /**
         * This method finalize the "asynchronous frame" started with {@link #beginAsync}.
         * 
         * @param {Object} frame The frame to finalize (returned by {@link #beginAsync} method
         */
        endAsync : function (index) {
            var originalSetTimeout      = this.originalSetTimeout
            var originalClearTimeout    = this.originalClearTimeout || this.global.clearTimeout
            var counter = 0
            
            if (index == null) Joose.O.each(this.timeoutIds, function (timeoutId, indx) {
                index = indx
                if (counter++) throw new Error("Calls to endAsync without argument should only be performed if you have single beginAsync statement") 
            })
            
            var timeoutId               = this.timeoutIds[ index ]
            
            // need to call in this way for IE < 9
            originalClearTimeout(timeoutId)
            delete this.timeoutIds[ index ]
            
            var me = this
            
            if (this.processed && !this.isFinished())
                // to allow potential call to `done` after `endAsync`
                originalSetTimeout(function () {
                    me.finalize()
                }, 1)
        },
        
        
        clearTimeouts : function () {
            var me                      = this
            var originalClearTimeout    = this.originalClearTimeout
            
            Joose.O.each(this.timeoutIds, function (value, id) {
                originalClearTimeout(value)
            })
            
            this.timeoutIds = {}
        },
        
        
        // deprecated
        skipIf : function (condition, why, code, howMany) {
            howMany = howMany || 1
            
            if (condition) {
                
                for (var i = 1; i <= howMany; i++) this.addResult(new Siesta.Result.Assertion({
                    passed      : true,
                    isSkipped   : true,
                    
                    description : 'SKIPPED: ' + why
                }))    
                
            } else
                code()
        },
        
        
        // deprecated
        skip : function (why, code, howMany) {
            this.skipIf(true, why, code, howMany)
        },
        
        
        /**
         * With this method you can mark a group of assertions as "todo", assuming they most probably will fail, 
         * but its still worth to try run them.
         * The supplied `code` function will be run, it will receive a new test instance as the 1st argument,
         * which should be used for assertions checks (and not the primary test instance, received from `StartTest`).
         * 
         * Assertions, failed inside the `code` block will be still treated by harness as "green".
         * Assertions, passed inside the `code` block will be treated by harness as bonus ones and highlighted.
         *
         * See also {@link Siesta.Test.ExtJS#knownBugIn} method.
         *
         * For example:

    t.todo('Scheduled for 4.1.x release', function (todo) {
    
        var treePanel    = new Ext.tree.Panel()
    
        todo.is(treePanel.getView().store, treePanel.store, 'NodeStore and TreeStore have been merged and there's only 1 store now);
    })

         * @param {String} why The reason/description for the todo
         * @param {Function} code A function, wrapping the "todo" assertions. This function will receive a special test class instance
         * which should be used for assertions checks
         */
        todo : function (why, code) {
            if (this.typeOf(why) == 'Function') why = [ code, code = why ][ 0 ]
            
            var todo  = new this.constructor({
                trait       : Siesta.Test.Todo,
                
                parent      : this,
                
                global      : this.global,
                url         : this.url,
                harness     : this.harness,
                run         : function () {},
                
                overrideSetTimeout      : false,
                originalSetTimeout      : this.originalSetTimeout,
                originalClearTimeout    : this.originalClearTimeout
            })
            
            var exception = this.global.StartTest.exceptionCatcher(function(){
                code(todo)
            })
            
            if (exception !== undefined) this.diag("TODO section threw an exception: [" + exception + "]")
        },
        
        
        failWithException : function (e) {
            this.failed             = true
            this.failedException    = e
            
            this.harness.onTestFail(this, e, this.getStackTrace(e))
            
            /**
             * This event is fired when the individual test case has threw an exception. 
             * 
             * This event bubbles up to the {@link Siesta.Harness harness}, you can observe it on harness as well.
             * 
             * @event testfailedwithexception
             * @member Siesta.Test
             * @param {JooseX.Observable.Event} event The event instance
             * @param {Siesta.Test} test The test instance that just has threw an exception
             * @param {Object} exception The exception thrown
             */
            this.fireEvent('testfailedwithexception', this, e);
            
            this.finalize(true)
        },
        
        
        start : function (alreadyFailedWithException, startNote) {
            if (this.startDate) {
                throw 'Test has already been started';
            }
            this.startDate  = new Date()
            
            this.harness.onTestStart(this)
            
            /**
             * This event is fired when the individual test case starts. 
             * 
             * This event bubbles up to the {@link Siesta.Harness harness}, you can observe it on harness as well. 
             * 
             * @event teststart
             * @member Siesta.Test
             * @param {JooseX.Observable.Event} event The event instance
             * @param {Siesta.Test} test The test instance that just has started
             */
            this.fireEvent('teststart', this);
            
            if (alreadyFailedWithException) {
                this.failWithException(alreadyFailedWithException)
                
                return
            }
            
            if (startNote) this.diag(startNote)
            
            var me                      = this
            var global                  = this.global
            
            var originalSetTimeout      = this.originalSetTimeout
            var originalClearTimeout    = this.originalClearTimeout
            
            // this.overrideSetTimeout
            if (this.overrideSetTimeout) {
                // see http://www.adequatelygood.com/2011/4/Replacing-setTimeout-Globally
                this.scopeProvider.runCode('var setTimeout, clearTimeout;')
                
                global.setTimeout = function (func, delay) {
                    
                    var index = me.timeoutsCount++
                    
                    // in NodeJS `setTimeout` returns an object and not a simple ID, so we try hard to store that object under unique index
                    // also using `setTimeout` from the scope of test - as timeouts in different scopes in browsers are mis-synchronized
                    var timeoutId = originalSetTimeout(function () {
                        originalClearTimeout(timeoutId)
                        delete me.timeoutIds[ index ]
                        
                        // if the test func has been executed, but the test was not finalized yet - then we should try to finalize it
                        if (me.processed && !me.isFinished())
                            // we are doing that after slight delay, potentially allowing to setup some other async frames in the "func" below
                            originalSetTimeout(function () {
                                me.finalize()
                            }, 1)
                        
                        func()
                        
                    }, delay)
    
                    // in NodeJS saves the index of the timeout descriptor to the descriptor
                    if (typeof timeoutId == 'object') 
                        timeoutId.__index = index
                    else
                        // in browser (where `timeoutId` is a number) - to the `idsToIndex` hash
                        me.idsToIndex[ timeoutId ] = index
                    
                    return me.timeoutIds[ index ] = timeoutId
                }
                
                global.clearTimeout = function (id) {
                    if (id == null) return
                    
                    var index
                    
                    // in NodeJS `setTimeout` returns an object and not a simple ID
                    if (typeof id == 'object') {
                        index       = id.__index
                        if (me.timeoutIds[ index ] != id) throw "Incorrect state"
                    } else {
                        index       = me.idsToIndex[ id ]
                        
                        delete me.idsToIndex[ id ]
                    }
                    
                    originalClearTimeout(id)
                    
                    if (index != null) delete me.timeoutIds[ index ]
                    
                    // if the test func has been executed, but the test was not finalized yet - then we should try to finalize it
                    if (me.processed && !me.isFinished())
                        // we are doing that after slight delay, potentially allowing to setup some other async frames after the "clearTimeout" will complete
                        originalSetTimeout(function () {
                            me.finalize()
                        }, 1)
                }
            }
            // eof this.overrideSetTimeout
            
            // we only don't need to cleanup up when doing a self-testing
            if (this.needToCleanup) this.scopeProvider.cleanupCallback = function () {
                if (me.overrideSetTimeout) {
                    global.setTimeout       = originalSetTimeout
                    global.clearTimeout     = originalClearTimeout
                }
                
                originalSetTimeout          = me.originalSetTimeout       = null
                originalClearTimeout        = me.originalClearTimeout     = null
                
                me.global                   = global = null
            }
            
            var run     = this.run
            
            if (this.transparentEx)
                run(me)
            else 
                var e = global.StartTest.exceptionCatcher(function(){
                    run(me)
                })
            
            if (e) {
                this.failWithException(e)
                
                return
            } 
            
            this.finalize()
        },
        
        
        finalize : function (force) {
            if (this.isFinished()) return
            
            this.processed = true
            
            if (force) this.clearTimeouts()
            
            if (!Joose.O.isEmpty(this.timeoutIds)) return
            
            if (!this.needDone && !this.isDone) {
                this.fireEvent('beforetestfinalizeearly')
                
                /**
                 * This event is fired before the individual test case ends (no any corresponded harness actions will be run yet).
                 * 
                 * This event bubbles up to the {@link Siesta.Harness harness}, you can observe it on harness as well.
                 * 
                 * @event beforetestfinalize
                 * @member Siesta.Test
                 * @param {JooseX.Observable.Event} event The event instance
                 * @param {Siesta.Test} test The test instance that is about to finalize
                 */
                this.fireEvent('beforetestfinalize');
            }
            
            this.endDate = new Date()

            this.harness.onTestEnd(this)
            
            /**
             * This event is fired when the individual test case ends (either because it has completed correctly and threw an exception).
             * 
             * This event bubbles up to the {@link Siesta.Harness harness}, you can observe it on harness as well.
             * 
             * @event testfinalize
             * @member Siesta.Test
             * @param {JooseX.Observable.Event} event The event instance
             * @param {Siesta.Test} test The test instance that just has completed
             */
            this.fireEvent('testfinalize', this);
            
            this.callback && this.callback()
            
//            // attempting to clear all references to scope, but with delay, to allow
//            // other potentially delayed actions to access `global` 
//            var me = this
//            
//            var originalSetTimeout          = me.originalSetTimeout
//            
//            // setTimeout from the scope of harness
//            originalSetTimeout(function () {
//                if (me.overrideSetTimeout) {
//                    me.global.setTimeout    = me.originalSetTimeout
//                    me.global.clearTimeout  = me.originalClearTimeout
//                }
//                
//                originalSetTimeout          = me.originalSetTimeout       = null
//                me.originalClearTimeout     = null
//                
//                me.global                   = null
//            }, 700)
        },
        
        
        getSummaryMessage : function (lineBreaks) {
            var res = []
            
            var passCount       = this.getPassCount()
            var failCount       = this.getFailCount()
            var assertPlanned   = this.assertPlanned
            var total           = failCount + passCount
            
            res.push('Passed: ' + passCount)
            res.push('Failed: ' + failCount)
            
            if (!this.failed) {
                // there was a t.plan() call
                if (assertPlanned != null) {
                    if (total < assertPlanned) 
                        res.push('Looks like you planned ' + assertPlanned + ' tests, but ran only ' + total)
                        
                    if (total > assertPlanned) 
                        res.push('Looks like you planned ' + assertPlanned + ' tests, but ran ' +  (total - assertPlanned) + ' extra tests, ' + total + ' total.')
                    
                    if (total == assertPlanned && !failCount) res.push('All tests passed')
                } else {
                    if (!this.isDoneCorrectly()) res.push('Test has completed, but there were no `t.done()` call. Add it at the bottom, or use `t.beginAsync()` for asynchronous code')
                    
                    if (this.isDoneCorrectly() && !failCount) res.push('All tests passed')
                }
                
            } else {
                var stack = this.getStackTrace(this.failedException)
                if (stack)
                    res.push.apply(res, [ 'Test suite threw an exception: ' + this.failedException].concat(stack))
                else
                    res.push('Test suite threw an exception: ' + this.failedException)
            }
            
            return res.join(lineBreaks || '\n')
        },
        
        
        /**
         * This method indicates that test has completed at the expected point and no more assertions are planned. Adding assertions after the call to `done`
         * will add a failing assertion "Adding assertion after test completion".
         * 
         * @param {Number} delay Optional. When provided, the test will not complete right away, but will wait for `delay` milliseconds for additional assertions. 
         */
        done : function (delay) {
            var me      = this
            
            if (delay) {
                var async = this.beginAsync()
                
                var originalSetTimeout = this.originalSetTimeout
                
                originalSetTimeout(function () {
                    
                    me.endAsync(async)
                    me.done() 
                
                }, delay)
                
            } else {
                this.fireEvent('beforetestfinalizeearly')
                this.fireEvent('beforetestfinalize');
                
                this.isDone = true
                
                if (this.processed) this.finalize()
            }
        },
        
        // `isDoneCorrectly` means that either test does not need the call to `done`
        // or the call to `done` has been already made
        isDoneCorrectly : function () {
            return !this.needDone || this.isDone
        },
        
        
        getPassCount : function () {
            var passCount = 0
            
            this.eachAssertion(function (assertion) {
                if (assertion.passed && !assertion.isTodo) passCount++
            })
            
            return passCount
        },

        getTodoPassCount : function () {
            var todoCount = 0;
            
            this.eachAssertion(function (assertion) {
                if (assertion.isTodo && assertion.passed) todoCount++;
            });
            
            return todoCount;
        },

        getTodoFailCount : function () {
            var todoCount = 0;
            
            this.eachAssertion(function (assertion) {
                if (assertion.isTodo && !assertion.passed) todoCount++;
            });
            
            return todoCount;
        },
        
        
        getFailCount : function () {
            var failCount = 0
            
            this.eachAssertion(function (assertion) {
                if (!assertion.passed && !assertion.isTodo) failCount++
            })
            
            return failCount
        },
        
        
        isPassed : function () {
            var passCount       = this.getPassCount()
            var failCount       = this.getFailCount()
            var assertPlanned   = this.assertPlanned
            
            return this.isFinished() && !this.failed && !failCount && (
                assertPlanned != null && passCount == assertPlanned
                    ||
                assertPlanned == null && this.isDoneCorrectly()
            )
        },
        
        
        isFailed : function () {
            var passCount       = this.getPassCount()
            var failCount       = this.getFailCount()
            var assertPlanned   = this.assertPlanned
            
            return this.failed || failCount || (
            
                this.isFinished() && ( 
                    assertPlanned != null && passCount != assertPlanned
                        ||
                    assertPlanned == null && !this.isDoneCorrectly()
                )
            )
        },
        
        
        isFailedWithException : function () {
            return this.failed
        },
        
        
        isStarted : function () {
            return this.startDate != null
        },
        
        
        isFinished : function () {
            return this.endDate != null
        },
        
        
        getDuration : function () {
            return this.endDate - this.startDate
        },
        
        getBubbleTarget : function () {
            return this.harness;
        }
    }
        
})
//eof Siesta.Test;
Role('Siesta.Test.Todo', {
    
    has : {
        parent              : null
    },
    
    
    methods : {
        
        addResult : function (result) {
            if (result instanceof Siesta.Result.Assertion) result.isTodo = true
            
            this.parent.addResult(result)
        },
        
        
        beginAsync : function (time) {
            return this.parent.beginAsync(time)
        },
        
        
        endAsync : function (index) {
            return this.parent.endAsync(index)
        }
        
    }
        
})
//eof Siesta.Test
;
/**
@class Siesta.Test.Action

*/
Class('Siesta.Test.ActionRegistry', {
    
    my : {
    
        has : {
            actionClasses       : Joose.I.Object
        },
    
        
        methods : {
            
            registerAction : function (name, constructor) {
                this.actionClasses[ name.toLowerCase() ] = constructor
            },

            
            getActionClass : function (name) {
                return this.actionClasses[ name.toLowerCase() ]
            },
            
            
            create : function (obj) {
                if (!obj.action) throw "Need to pass `action` property for step config"
                
                var actionClass = this.getActionClass(obj.action)
                
                return new actionClass(obj)
            }
        }
    }
});
;
/**
@class Siesta.Test.Action

*/
Class('Siesta.Test.Action', {
    
    has : {
        args                : null, 
        
        test                : { required : true },
        next                : { required : true },
        
        requiredTestMethod  : null
    },

    
    methods : {
        
        initialize : function () {
            var requiredTestMethod  = this.requiredTestMethod
            
            // additional sanity check
            if (requiredTestMethod && !this.test[ requiredTestMethod ]) 
                throw new Error("Action [" + this + "] requires `" + requiredTestMethod + "` method in your test class") 
        },
        
        
        process : function () {
            this.next()
        }
    }
});
;
/**

@class Siesta.Test.Action.Done
@extends Siesta.Test.Action

This action can be included in the `t.chain` call with "done" shortcut:

    t.chain(
        {
            action      : 'done'
        }
    )

This action will just call the {@link Siesta.Test#done done} method of the test.

*/
Class('Siesta.Test.Action.Done', {
    
    isa         : Siesta.Test.Action,
    
    has : {
        /**
         * @cfg {Number} delay
         * 
         * An optional `delay` argument for {@link Siesta.Test#done done} call.
         */
        delay  :        null
    },

    
    methods : {
        
        process : function () {
            this.test.done(this.delay)
        }
    }
});


Siesta.Test.ActionRegistry.registerAction('done', Siesta.Test.Action.Done);
/**

@class Siesta.Test.Action.Wait
@extends Siesta.Test.Action

This action can be included in the `t.chain` call with "wait" or "delay" shortcuts:

    t.chain(
        {
            action      : 'wait',   // or "delay"
            delay       : 1000      // 1 second
        }
    )

This action will just wait the time specified - 1s by default, and continue. 

*/
Class('Siesta.Test.Action.Wait', {
    
    isa         : Siesta.Test.Action,
    
    has : {
        /**
         * @cfg {Number} delay
         * 
         * A number of milliseconds to wait before continuing.
         */
        delay  :        1000
    },

    
    methods : {
        
        process : function () {
            var originalSetTimeout      = this.test.originalSetTimeout
            
            originalSetTimeout(this.next, this.delay)
        }
    }
});


Siesta.Test.ActionRegistry.registerAction('wait', Siesta.Test.Action.Wait)
Siesta.Test.ActionRegistry.registerAction('delay', Siesta.Test.Action.Wait);
/**

@class Siesta.Harness

`Siesta.Harness` is an abstract base harness class in Siesta hierarchy. This class provides no UI, 
you should use one of it subclasses, for example {@link Siesta.Harness.Browser}

This file is a reference only, for a getting start guide and manual, please refer to <a href="#!/guide/siesta_getting_started">Getting Started Guide</a>.


Synopsys
========

    var Harness,
        isNode        = typeof process != 'undefined' && process.pid
    
    if (isNode) {
        Harness = require('siesta');
    } else {
        Harness = Siesta.Harness.Browser;
    }
        
    
    Harness.configure({
        title     : 'Awesome Test Suite',
        
        transparentEx       : true,
        
        autoCheckGlobals    : true,
        expectedGlobals     : [
            'Ext',
            'Sch'
        ],
        
        preload : [
            "http://cdn.sencha.io/ext-4.0.2a/ext-all-debug.js",
            "../awesome-project-all.js",
            {
                text    : "console.log('preload completed')"
            }
        ]
    })
    
    
    Harness.start(
        // simple string - url relative to harness file
        'sanity.t.js',
        
        // test file descriptor with own configuration options
        {
            url     : 'basic.t.js',
            
            // replace `preload` option of harness
            preload : [
                "http://cdn.sencha.io/ext-4.0.6/ext-all-debug.js",
                "../awesome-project-all.js"
            ]
        },
        
        // groups ("folders") of test files (possibly with own options)
        {
            group       : 'Sanity',
            
            autoCheckGlobals    : false,
            
            items       : [
                'data/crud.t.js',
                ...
            ]
        },
        ...
    )


*/


Class('Siesta.Harness', {
    
    does        : [
        JooseX.Observable
    ],
    
    has : {
        /**
         * @cfg {String} title The title of the test suite. Can contain HTML.
         */
        title               : null,
        
        /**
         * @cfg {Class} testClass The test class which will be used for creating test instances, defaults to {@link Siesta.Test}.
         * You can subclass {@link Siesta.Test} and provide a new class. 
         * 
         * This option can be also specified in the test file descriptor. 
         */
        testClass           : Siesta.Test,
        contentManagerClass : Siesta.Content.Manager,
        
        testsByURL          : Joose.I.Object,
        
        // fields of test descriptor:
        // - id - either `url` or wbs + group - computed
        // - url
        // - isMissing - true if test file is missing
        // - testConfig - config object provided to the StartTest
        // - index - (in the group) computed
        // - scopeProvider
        // - scopeProviderConfig
        // - preload
        // - alsoPreload
        // - parent - parent descriptor (or harness for top-most ones) - computed
        // - preset - computed by harness - instance of Siesta.Content.Preset
        // - forceDOMVisible - true to show the <iframe> on top of all others when running this test
        //                     (required for IE when using "document.getElementFromPoint()") 
        // OR - object 
        // - group - group name
        // - items - array of test descriptors
        // - expanded - initial state of the group (true by default)
        descriptors         : Joose.I.Array,
        descriptorsById     : Joose.I.Object,
        
        scopesByURL         : Joose.I.Object,
        
        /**
         * @cfg {Boolean} transparentEx When set to `true` harness will not try to catch any exception, thrown from the test code.
         * This is very useful for debugging - you can for example use the "break on error" option in Firebug.
         * But, using this option may naturally lead to unhandled exceptions, which may leave the harness in incosistent state - 
         * refresh the browser page in such case.
         *  
         * Defaults to `false` - harness will do its best to detect any exception thrown from the test code.
         * 
         * This option can be also specified in the test file descriptor. 
         */
        transparentEx       : false,
        
        scopeProviderConfig     : null,
        scopeProvider           : null,
        
        /**
         * @cfg {String} runCore Either `parallel` or `sequential`. Indicates how the individual tests should be run - several at once or one-by-one.
         * Default value is "parallel". You do not need to change this option usually.
         */
        runCore                 : 'parallel',
        
        /**
         * @cfg {Number} maxThreads The maximum number of tests running at the same time. Only applicable for `parallel` run-core.
         */
        maxThreads              : 4,
        
        /**
         * @cfg {Boolean} autoCheckGlobals When set to `true`, harness will automatically issue an {@link Siesta.Test#verifyGlobals} assertion at the end of each test,
         * so you won't have to manually specify it each time. The assertion will be triggered only if test completed successfully. Default value is `false`.
         * See also {@link #expectedGlobals} configuration option and {@link Siesta.Test#expectGlobals} method.
         * 
         * This option will be always disabled in Opera, since every DOM element with `id` is being added as a global symbol in it.
         * 
         * This option can be also specified in the test file descriptor.
         */
        autoCheckGlobals        : false,
        
        disableGlobalsCheck     : false,
        
        /**
         * @cfg {Array} expectedGlobals An array of properties names which are likely to present in the scope of each test. There is no need to provide the name
         * of built-in globals - harness will automatically scan them from the empty context. Only provide the names of global properties which will be created
         * by your preload code.
         * 
         * For example
         * 
    Harness.configure({
        title               : 'Ext Scheduler Test Suite',
        
        autoCheckGlobals    : true,
        expectedGlobals     : [
            'Ext',
            'MyProject',
            'SomeExternalLibrary'
        ],
        ...
    })
            
         * This option can be also specified in the test file descriptor.
         */
        expectedGlobals         : Joose.I.Array,
        // will be populated by `setup` 
        cleanScopeGlobals       : Joose.I.Array,
        
        /**
         * @cfg {Array} preload The array which contains the *preload descriptors* describing which files/code should be preloaded into the scope of each test.
         * 
         * Preload descriptor can be:
         * 
         * - a string, containing an url to load (cross-domain urls are ok, if url ends with ".css" it will be loaded as CSS)
         * - an object `{ type : 'css/js', url : '...' }` allowing to specify the CSS files with different extension
         * - an object `{ type : 'css/js', content : '...' }` allowing to specify the inline content for script / style
         * - an object `{ text : '...' }` which is a shortcut for `{ type : 'js', content : '...' }`
         * 
         * For example:
         * 
    Harness.configure({
        title           : 'Ext Scheduler Test Suite',
        
        preload         : [
            'http://cdn.sencha.io/ext-4.0.2a/resources/css/ext-all.css',
            'http://cdn.sencha.io/ext-4.0.2a/ext-all-debug.js',
            {
                text    : 'MySpecialGlobalFunc = function () { if (typeof console != "undefined") ... }'
            }
        ],
        ...
    })
            
         * This option can be also specified in the test file descriptor.
         */
        preload                 : Joose.I.Array,
        
        /**
         * @cfg {Array} alsoPreload The array with preload descriptors describing which files/code should be preloaded **additionally**.
         * 
         * This option can be **only** specified in the test file descriptor and not for groups.
         */
        
        /**
         * @cfg {Object} listeners The object which keys corresponds to event names and values - to event handlers. If provided, the special key "scope" will be treated as the 
         * scope for all event handlers, otherwise the harness itself will be used as scope.
         * 
         * Note, that the events from individual {@link Siesta.Test test cases} instances will bubble up to the harness - you can listen to all of them in one place: 
         * 

    Harness.configure({
        title     : 'Awesome Test Suite',
        
        preload : [
            'http://cdn.sencha.io/ext-4.0.7-gpl/resources/css/ext-all.css',
            'http://cdn.sencha.io/ext-4.0.7-gpl/ext-all-debug.js',
            
            'preload.js'
        ],
        
        listeners : {
            testsuitestart      : function (event, harness) {
                log('Test suite is starting: ' + harness.title)
            },
            testsuiteend        : function (event, harness) {
                log('Test suite is finishing: ' + harness.title)
            },
            teststart           : function (event, test) {
                log('Test case is starting: ' + test.url)
            },
            testupdate          : function (event, test, result) {
                log('Test case [' + test.url + '] has been updated: ' + result.description + (result.annotation ? ', ' + result.annotation : ''))
            },
            testfailedwithexception : function (event, test) {
                log('Test case [' + test.url + '] has failed with exception: ' + test.failedException)
            },
            testfinalize        : function (event, test) {
                log('Test case [' + test.url + '] has completed')
            }
        }
    })

         */
        
        
        /**
         * @cfg {Boolean} cachePreload When set to `true`, harness will cache the content of the preload files and provide it for each test, instead of loading it 
         * from network each time. This option may give a slight speedup in tests execution (especially when running the suite from the remote server), but see the 
         * caveats below. Default value is `false`.
         * 
         * Caveats: this option doesn't work very well for CSS (due to broken relative urls for images). Also its not "debugging-friendly" - as you will not be able 
         * to setup breakpoints for cached code. 
         */
        cachePreload            : false,
        
        mainPreset              : null,
        
        verbosity               : 0,
        
        /**
         * @cfg {Boolean} keepResults When set to `true`, harness will not cleanup the context of the test immediately. Instead it will do so, only when
         * the test will run again. This will allow you for example to examine the DOM of tests. Default value is `true` 
         */
        keepResults             : true,
        
        /**
         * @cfg {Boolean} overrideSetTimeout When set to `false`, the tests will not override "setTimeout" from the context of each test
         * for asynchronous code tracking. User will need to use `beginAsync/endAsync` calls to indicate that test is still running.
         * 
         * This option can be also specified in the test file descriptor.
         */
        overrideSetTimeout      : true,
        
        /**
         * @cfg {Boolean} needDone When set to `true`, the tests will must indicate that that they have reached the correct exit point with `t.done()` call, 
         * after which, adding any assertions is not allowed. Using this option will ensure that test did not exit prematurely with some exception silently caught.
         * 
         * This option can be also specified in the test file descriptor.
         */
        needDone                : false,
        
        needToStop              : false,
        
        // the default timeout for tests will be increased when launching more than this number of files
        increaseTimeoutThreshold    : 8,
        
        // the start and end dates for the most recent `launch` method
        startDate               : null,
        endDate                 : null,
        
        /**
         * @cfg {Number} waitForTimeout Default timeout for `waitFor` (in milliseconds). Default value is 10000.
         * 
         * This option can be also specified in the test file descriptor.
         */
        waitForTimeout          : 10000,
        
        /**
         * @cfg {Number} defaultTimeout Default timeout for `beginAsync` operation (in milliseconds). Default value is 15000.
         * 
         * This option can be also specified in the test file descriptor.
         */
        defaultTimeout          : 15000
    },
    
    
    methods : {
        
        onTestUpdate : function (test, result) {
        },
        
        
        onTestFail : function (test, exception, stack) {
        },
        
        
        onTestStart : function (test) {
        },
        
        
        onTestEnd : function (test) {
        },
        
        
        onTestSuiteStart : function (descriptors, contentManager) {
            this.startDate  = new Date()
            
            /**
             * This event is fired when the test suite starts. Note, that when running the test suite in the browsers, this event can be fired several times
             * (for each group of tests you've launched).  
             * 
             * You can subscribe to it, using regular ExtJS syntax:
             * 
             *      Harness.on('testsuitestart', function (event, harness) {}, scope, { single : true })
             * 
             * See also the "/examples/events"
             * 
             * @event testsuitestart
             * @member Siesta.Harness
             * @param {JooseX.Observable.Event} event The event instance
             * @param {Siesta.Harness} harness The harness that just has started
             */
            this.fireEvent('testsuitestart', this)
        },
        
        
        onTestSuiteEnd : function () {
            this.endDate    = new Date()
            
            /**
             * This event is fired when the test suite ends. Note, that when running the test suite in the browsers, this event can be fired several times
             * (for each group of tests you've launched).  
             * 
             * @event testsuiteend
             * @member Siesta.Harness
             * @param {JooseX.Observable.Event} event The event instance
             * @param {Siesta.Harness} harness The harness that just has ended
             */
            this.fireEvent('testsuiteend', this)
        },
        
        
        onBeforeScopePreload : function (scopeProvider, url) {
        },
        
        
        onCachingError : function (descriptors, contentManager) {
        },
        
        
        /**
         * This method configures the harness instance. It just copies the passed configuration option into harness instance.
         *
         * @param {Object} config - configuration options (values of attributes for this class)
         */
        configure : function (config) {
            Joose.O.copy(config, this)
            
            var me      = this
            
            if (config.listeners) Joose.O.each(config.listeners, function (value, name) {
                if (name == 'scope') return
                
                me.on(name, value, config.scope || me)
            })
        },
        
        
        // backward compat
        processPreloadArray : function (preload) {
            var me = this
            
            Joose.A.each(preload, function (url, index) {
                
                // do not process { text : "" } preload descriptors
                if (Object(url) === url) return 
                
                preload[ index ] = me.normalizeURL(url)
            })
            
            return preload
        },
        
        
        populateCleanScopeGlobals : function (scopeProvider, callback) {
            var scopeProviderClass  = eval(scopeProvider)
            var cleanScope          = new scopeProviderClass()
            
            var cleanScopeGlobals   = this.cleanScopeGlobals        
            
            // we can also use "create" and not "setup" here
            // create will only create the iframe (in browsers) and will not try to update its content
            // the latter crashes IE8
            cleanScope.setup(function () {
                
                for (var name in cleanScope.scope) cleanScopeGlobals.push(name)
                
                callback()
                
                // this setTimeout seems to stop the spinning loading indicator in FF
                // accorting to https://github.com/3rd-Eden/Socket.IO/commit/bad600fb1fb70238f42767c56f01256470fa3c15
                // it only works *after* onload (this callback will be called *in* onload)
                
                setTimeout(function () {
                    // will remove the iframe (in case of browser harness) from DOM and stop loading indicator
                    cleanScope.cleanup()    
                }, 0)
            })
        },
        
        
        setup : function (callback) {
            this.populateCleanScopeGlobals(this.scopeProvider, callback)
        },
        
        /**
         * This method will launch a test suite. It accepts a variable number of *test file descriptors*. A test file descritor is one of the following:
         * 
         * - a string, containing a test file url
         * - an object containing the `url` property `{ url : '...', option1 : 'value1', option2 : 'value2' }`. The `url` property should point to the test file.
         * Other properties can contain values of some configuration options of the harness (marked accordingly). In this case, they will **override** the corresponding values,
         * provided to harness or parent descriptor. 
         * - an object `{ group : 'groupName', items : [], expanded : true, option1 : 'value1' }` specifying the folder of test files. The `items` property can contain an array of test file descriptors.
         * Other properties will override the applicable harness options **for all child descriptors**.
         * 
         * Groups (folder) may contain nested groups. Number of nesting levels is not limited.
         * 
         * For example, one may easily have a special group of test files, having its own preload configuration (for example for testing on-demand loading). In the same
         * time some test in that group may have its own preload, overriding others.

    Harness.configure({
        title           : 'Ext Scheduler Test Suite',
        preload         : [
            'http://cdn.sencha.io/ext-4.0.2a/resources/css/ext-all.css',
            'http://cdn.sencha.io/ext-4.0.2a/ext-all-debug.js',
            '../awesome-app-all-debug.js'
        ],
        ...
    })
    
    Harness.start(
        // regular file
        'data/crud.t.js',
        // a group with own "preload" config for its items
        {
            group       : 'On-demand loading',
            
            preload     : [
                'http://cdn.sencha.io/ext-4.0.2a/resources/css/ext-all.css',
                'http://cdn.sencha.io/ext-4.0.2a/ext-all-debug.js',
            ],
            items       : [
                'ondemand/sanity.t.js',
                'ondemand/special-test.t.js',
                // a test descriptor with its own "preload" config (have the most priority)
                {
                    url         : 'ondemand/4-0-6-compat.t.js',
                    preload     : [
                        'http://cdn.sencha.io/ext-4.0.6/resources/css/ext-all.css',
                        'http://cdn.sencha.io/ext-4.0.6/ext-all-debug.js',
                    ]
                },
                // sub-group
                {
                    group       : 'Sub-group',
                    items       : [
                        ...
                    ]
                }
            ]
        },
        ...
    )

         * Additionally, you can provide a test descriptor in the test file itself, adding it as the 1st or 2nd argument for `StartTest` call:  
         * 
    StartTest({
        autoCheckGlobals    : false,
        alsoPreload         : [ 'some_additional_preload.js' ]
    }, function (t) {
        ...
    }) 
         * 
         * Values from this object takes the highest priority and will override any other configuration.
         * 
         * @param {Mixed} descriptor1
         * @param {Mixed} descriptor2
         * @param {Mixed} descriptorN
         */
        start : function () {
            // a bit hackish - used by Selenium reporter..
            var me = __ACTIVE_HARNESS__ = this
            
            this.mainPreset = new Siesta.Content.Preset({
                preload     : this.processPreloadArray(this.preload)
            })
            
            var descriptors = this.descriptors = Joose.A.map(arguments, function (desc, index) {
                return me.normalizeDescriptor(desc, me, index)
            })
            
            this.setup(function () {
                me.launch(descriptors)
            })
        },

        
        launch : function (descriptors, callback, errback) {
            var me                      = this
            
            //console.time('launch')
            //console.time('launch-till-preload')
            //console.time('launch-after-preload')
            
            this.needToStop             = false
            
            // no folders, only leafs
            var flattenDescriptors      = this.flattenDescriptors(descriptors)
            var testScriptsPreset       = new Siesta.Content.Preset()
            var presets                 = [ testScriptsPreset, this.mainPreset ]
            
            Joose.A.each(flattenDescriptors, function (desc) { 
                if (desc.preset != me.mainPreset) presets.push(desc.preset)
                
                testScriptsPreset.addResource(desc.url)
            })
            
            // cache either everything (this.cachePreload) or only the test files (to be able to show missing files / show content) 
            var contentManager  = new this.contentManagerClass({
                presets         : [ testScriptsPreset ].concat(this.cachePreload ? presets : [])
            })
            
            var options         = {
                increaseTimeout     : this.runCore == 'parallel' && flattenDescriptors.length > this.increaseTimeoutThreshold
            }
            
            //console.time('caching')
            
            contentManager.cache(function () {
                
                //console.timeEnd('caching')
                
                Joose.A.each(flattenDescriptors, function (desc) { 
                    if (contentManager.hasContentOf(desc.url)) {
                        var testConfig  = desc.testConfig = Siesta.getConfigForTestScript(contentManager.getContentOf(desc.url))
                        
                        // if testConfig contains the "preload" or "alsoPreload" key - then we need to update the preset of the descriptor
                        if (testConfig && (testConfig.preload || testConfig.alsoPreload)) desc.preset = me.getDescriptorPreset(desc)
                    } else 
                        desc.isMissing = true
                        
                    me.normalizeScopeProvider(desc)
                })
                
                
                me.onTestSuiteStart(descriptors, contentManager)
                
                me.runCoreGeneral(flattenDescriptors, contentManager, options, function () {
                    me.onTestSuiteEnd(descriptors, contentManager)
                    
                    callback && callback(descriptors)
                })
                
            }, function () {}, true)
        },
        
        
        flattenDescriptors : function (descriptors, includeFolders) {
            var flatten     = []
            var me          = this
            
            Joose.A.each(descriptors, function (descriptor) {
                
                if (descriptor.group) {
                    
                    if (includeFolders) flatten.push(descriptor)
                    
                    flatten.push.apply(flatten, me.flattenDescriptors(descriptor.items, includeFolders))
                    
                } else
                    flatten.push(descriptor)
            })
            
            return flatten
        },
        

        getDescriptorConfig : function (descriptor, configName, doNotLookAtRoot) {
            var testConfig  = descriptor.testConfig
            
            if (testConfig && testConfig.hasOwnProperty(configName))    return testConfig[ configName ]
            if (descriptor.hasOwnProperty(configName))                  return descriptor[ configName ]
            
            var parent  = descriptor.parent
            
            if (parent) {
                if (parent == this)
                    if (doNotLookAtRoot) 
                        return undefined
                    else
                        return this[ configName ]
                
                return this.getDescriptorConfig(parent, configName, doNotLookAtRoot)
            }
            
            return undefined
        },
        
        
        getDescriptorPreset : function (desc) {
            var preload                 = this.getDescriptorConfig(desc, 'preload', true)
            
            if (preload || desc.alsoPreload)
                return new Siesta.Content.Preset({
                    preload     : this.processPreloadArray((preload || this.preload).concat(desc.alsoPreload || []))
                })
                
            return this.mainPreset
        },
        
        
        normalizeScopeProvider : function (desc) {
            var scopeProvider = this.getDescriptorConfig(desc, 'scopeProvider')
            
            if (scopeProvider) {
                var match 
                
                if (match = /^=(.+)/.exec(scopeProvider))
                    scopeProvider = match[ 1 ]
                else 
                    scopeProvider = scopeProvider.replace(/^(Scope\.Provider\.)?/, 'Scope.Provider.')
            }
            
            desc.scopeProvider          = scopeProvider
            desc.scopeProviderConfig    = this.getDescriptorConfig(desc, 'scopeProviderConfig') 
        },
        
        
        normalizeDescriptor : function (desc, parent, index, level) {
            if (typeof desc == 'string') desc = { url : desc }
            
            level       = level || 0
            
            var me      = this
            
            desc.parent = parent
            
            // folder
            if (desc.group) {
                
                desc.id     = parent == this ? 'testFolder-' + level + '-' + index : parent.id + '/' + level + '-' + index
                desc.items  = Joose.A.map(desc.items || [], function (subDesc, index) {
                    return me.normalizeDescriptor(subDesc, desc, index, level + 1)
                })
                
            } else {
                // leaf case
                desc.id                     = desc.url
                desc.preset                 = this.getDescriptorPreset(desc)
                
                // the only thing left to normlize in the descriptor is now "scopeProvider"
                // we postpone this normalization to the moment after loading of the test files, 
                // since they can also contain "scopeProvider"-related configs
                // see "normalizeScopeProvider"
            }
            
            this.descriptorsById[ desc.id ] = desc
            
            return desc
        },
        
        
        runCoreGeneral : function (descriptors, contentManager, options, callback) {
            var runCoreMethod   = 'runCore' + Joose.S.uppercaseFirst(this.runCore)
            
            if (typeof this[ runCoreMethod ] != 'function') throw new Error("Invalid `runCore` specified: [" + this.runCore + "]")
            
            this[ runCoreMethod ](descriptors, contentManager, options, callback)
        },
        
        
        runCoreParallel : function (descriptors, contentManager, options, callback) {
            var me              = this
            var processedNum    = 0
            var count           = descriptors.length
            
            var exitLoop        = false
            
            if (!count) callback()
            
            var launch  = function (descriptors) {
                var desc = descriptors.shift()
                
                if (!desc) return
                
                me.processURL(desc, desc.index, contentManager, options, function () {
                    processedNum++
                    
                    // set the internal closure `exitLoop` to stop launching new branches
                    // on the 1st encountering of `me.needToStop` flag
                    if (me.needToStop || exitLoop) {
                        exitLoop = true
                        
                        callback()
                        
                        return
                    }
                    
                    if (processedNum == count) 
                        callback()
                    else
                        launch(descriptors)
                })
            }
            
            for (var i = 1; i <= this.maxThreads; i++) launch(descriptors)
        },
        
        
        runCoreSequential : function (descriptors, contentManager, options, callback) {
            if (descriptors.length && !this.needToStop) {
                var desc = descriptors.shift()
                
                var me = this
                
                this.processURL(desc, desc.index, contentManager, options, function () {
                    
                    me.runCoreSequential(descriptors, contentManager, options, callback)
                })
                
            } else
                callback()
        },
        
        
        getSeedingCode : function (desc) {
            return 'StartTest = function () { StartTest.args = arguments };' +
                      // for older IE - the try/catch should be from the same context as the exception
                      'StartTest.exceptionCatcher = function (func) { var ex; try { func() } catch (e) { ex = e; }; return ex; };' +
                      'StartTest.testError = Error;'
        },
        
        
        getScopeProviderConfigFor : function (desc) {
            var config          = Joose.O.copy(desc.scopeProviderConfig || {})
            
            config.seedingCode  = this.getSeedingCode()
            
            return config
        },
        
        
        setupScope : function (desc) {
            var url                 = desc.url
            var scopeProvideClass   = eval(desc.scopeProvider)
            
            this.cleanupScopeForURL(url)
            
            return this.scopesByURL[ url ] = new scopeProvideClass(this.getScopeProviderConfigFor(desc))
        },
        
        
        cleanupScopeForURL : function (url) {
            var scopeProvider = this.scopesByURL[ url ]
            
            if (scopeProvider) {
                delete this.scopesByURL[ url ]
                
                scopeProvider.cleanup()
            }
        },


        // should prepare the "seedingScript" - include it to the `scopeProvider`
        prepareScopeSeeding : function (scopeProvider, desc, contentManager) {
            if (this.cachePreload && contentManager.hasContentOf(desc.url))
                scopeProvider.addPreload({
                    type        : 'js', 
                    content     : contentManager.getContentOf(desc.url)
                })
            else
                scopeProvider.seedingScript = this.resolveURL(desc.url, scopeProvider, desc)
        },

        
        // should normalize non-standard urls (like specifying Class.Name in preload)
        // such behavior is not documented and generally deprecated
        normalizeURL : function (url) {
            return url
        },
            
            
        resolveURL : function (url, scopeProvider, desc) {
            return url
        },
        
        
        processURL : function (desc, index, contentManager, urlOptions, callback) {
            var me      = this
            var url     = desc.url
            
            if (desc.isMissing) {
                callback()
                
                return
            }
            
            // a magical shared object, which will contains the `test` property with test instance, once the test will be created
            var testHolder      = {}
            // an array of errors occured during preload phase
            var preloadErrors   = []    
            
            var scopeProvider   = this.setupScope(desc)
            var transparentEx   = this.getDescriptorConfig(desc, 'transparentEx')
            
            var onErrorHandler  = function (msg, url, lineNumber) {
                var test = testHolder.test
                
                if (test && test.isStarted()) {
                    test.nbrExceptions++;
                    test.failWithException(msg + ' ' + url + ' ' + lineNumber)
                }
                else {
                    preloadErrors.push(msg + ' ' + url + ' ' + lineNumber)
                }
            }
            
            // trying to setup the `onerror` handler as early as possible - to detect each and every exception from the test
            if (!transparentEx) scopeProvider.addOnErrorHandler(onErrorHandler)
            
//            scopeProvider.addPreload({
//                type        : 'js', 
//                content     : 'console.time("scope-onload")'
//            })
            
            desc.preset.eachResource(function (resource) {
                
                if (me.cachePreload && contentManager.hasContentOf(resource))
                    scopeProvider.addPreload({
                        type        : (resource instanceof Siesta.Content.Resource.CSS) ? 'css' : 'js', 
                        content     : contentManager.getContentOf(resource)
                    })
                else {
                    var resourceDesc    = resource.asDescriptor()
                    
                    if (resourceDesc.url) resourceDesc.url = me.resolveURL(resourceDesc.url, scopeProvider, desc)
                    
                    scopeProvider.addPreload(resourceDesc)
                }
            })

            
            me.prepareScopeSeeding(scopeProvider, desc, contentManager)
            
            this.onBeforeScopePreload(scopeProvider, url)
            
            //console.timeEnd('launch-till-preload')
            
            //console.time('preload')
            
//            scopeProvider.addPreload({
//                type        : 'js', 
//                content     : 'console.timeEnd("scope-onload")'
//            })
            
            scopeProvider.setup(function () {
                var startTestAnchor     = scopeProvider.scope.StartTest
                
                var args                = startTestAnchor && startTestAnchor.args
                
                // pick either 1st or 2nd argument depending which one is a function 
                var runFunc             = args && (typeof args[ 0 ] == 'function' && args[ 0 ] || args[ 1 ])
                
                me.launchTest({
                    testHolder          : testHolder,
                    desc                : desc,
                    scopeProvider       : scopeProvider,
                    contentManager      : contentManager,
                    urlOptions          : urlOptions,
                    preloadErrors       : preloadErrors,
                    onErrorHandler      : onErrorHandler,
                    
                    runFunc             : runFunc
                }, callback)
            });
        },
        
        
        launchTest : function (options, callback) {
            var scopeProvider   = options.scopeProvider
            var desc            = options.desc
//            desc, scopeProvider, contentManager, options, preloadErrors, onErrorHandler, callback
            
            //console.timeEnd('preload')
            //console.timeEnd('launch-after-preload')
            var me              = this
            var url             = desc.url
            var testClass       = me.getDescriptorConfig(desc, 'testClass')
        
            // after the scope setup, the `onerror` handler might be cleared - installing it again
            if (!this.getDescriptorConfig(desc, 'transparentEx')) scopeProvider.addOnErrorHandler(options.onErrorHandler)
            
            var testConfig      = me.getNewTestConfiguration(desc, scopeProvider, options.contentManager, options.urlOptions, options.runFunc)
            
            testConfig.callback = function () {
                if (!me.keepResults) me.cleanupScopeForURL(url)
        
                // a slight delay before launching new test to possibly give browser some time to render UI
                callback && setTimeout(callback, 10)
            }
            
            var test            = options.testHolder.test = me.testsByURL[ url ] = new testClass(testConfig)
            
            scopeProvider.scope.setTimeout(function() {
                //console.timeEnd('launch')
                
                // start the test after slight delay - to run it already *after* onload (in browsers)
                test.start(options.preloadErrors[ 0 ])
            }, 10);
        },
        
        
        getNewTestConfiguration : function (desc, scopeProvider, contentManager, options, runFunc) {
            var scope           = scopeProvider.scope
            
            var config          = {
                url                 : desc.url,
            
                harness             : this,
                run                 : runFunc,
            
                expectedGlobals     : this.cleanScopeGlobals.concat(this.getDescriptorConfig(desc, 'expectedGlobals')),
                autoCheckGlobals    : this.getDescriptorConfig(desc, 'autoCheckGlobals'),
                disableGlobalsCheck : this.disableGlobalsCheck,
            
                global              : scope,
                scopeProvider       : scopeProvider,
                
                contentManager      : contentManager,
                
                transparentEx       : this.getDescriptorConfig(desc, 'transparentEx'),
                needDone            : this.getDescriptorConfig(desc, 'needDone'),
                
                overrideSetTimeout      : this.getDescriptorConfig(desc, 'overrideSetTimeout'),
                originalSetTimeout      : scope.setTimeout,
                originalClearTimeout    : scope.clearTimeout,
                
                defaultTimeout          : this.getDescriptorConfig(desc, 'defaultTimeout') * (options.increaseTimeout ? 2 : 1),
                waitForTimeout          : this.getDescriptorConfig(desc, 'waitForTimeout') * (options.increaseTimeout ? 3 : 1)
            }
            
            // potentially not safe
            if (desc.testConfig) Joose.O.extend(config, desc.testConfig)
            
            return config
        },
        
        
        getScriptDescriptor : function (id) {
            return this.descriptorsById[ id ]
        },
        
        
        allPassed : function () {
            var allPassed       = true
            var me              = this
            
            Joose.A.each(this.flattenDescriptors(this.descriptors), function (descriptor) {
                var test    = me.testsByURL[ descriptor.url ]
                
                // ignore missing tests (could be skipped by test filtering
                if (!test) return
                
                if (descriptor.isMissing) { allPassed = false; return false }
                
                allPassed = allPassed && test.isPassed()
            })
            
            return allPassed
        },
        
        
        generateReport : function (options) {
            // a string? 
            if (Object(options) !== options) options = { format : options || 'JSON' }
            
            var methodName  = 'generate' + options.format + 'Report'
            
            if (!this[ methodName ]) throw "Can't generate report - missing the `" + methodName + "` method"
            
            return this[ methodName ](options)
        }
    }
    // eof methods
})
//eof Siesta.Harness
;
;
/*!
* jQuery JavaScript Library v1.6.2
* http://jquery.com/
*
* Copyright 2011, John Resig
* Dual licensed under the MIT or GPL Version 2 licenses.
* http://jquery.org/license
*
* Includes Sizzle.js
* http://sizzlejs.com/
* Copyright 2011, The Dojo Foundation
* Released under the MIT, BSD, and GPL Licenses.
*
* Date: Thu Jun 30 14:16:56 2011 -0400
*/
(function (window, undefined) {

    // Use the correct document accordingly with window argument (sandbox)
    var document = window.document,
	navigator = window.navigator,
	location = window.location;
    var jQuery = (function () {

        // Define a local copy of jQuery
        var jQuery = function (selector, context) {
            // The jQuery object is actually just the init constructor 'enhanced'
            return new jQuery.fn.init(selector, context, rootjQuery);
        },

        // Map over jQuery in case of overwrite
	_jQuery = window.jQuery,

        // Map over the $ in case of overwrite
	_$ = window.$,

        // A central reference to the root jQuery(document)
	rootjQuery,

        // A simple way to check for HTML strings or ID strings
        // (both of which we optimize for)
	quickExpr = /^(?:[^<]*(<[\w\W]+>)[^>]*$|#([\w\-]*)$)/,

        // Check if a string has a non-whitespace character in it
	rnotwhite = /\S/,

        // Used for trimming whitespace
	trimLeft = /^\s+/,
	trimRight = /\s+$/,

        // Check for digits
	rdigit = /\d/,

        // Match a standalone tag
	rsingleTag = /^<(\w+)\s*\/?>(?:<\/\1>)?$/,

        // JSON RegExp
	rvalidchars = /^[\],:{}\s]*$/,
	rvalidescape = /\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g,
	rvalidtokens = /"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g,
	rvalidbraces = /(?:^|:|,)(?:\s*\[)+/g,

        // Useragent RegExp
	rwebkit = /(webkit)[ \/]([\w.]+)/,
	ropera = /(opera)(?:.*version)?[ \/]([\w.]+)/,
	rmsie = /(msie) ([\w.]+)/,
	rmozilla = /(mozilla)(?:.*? rv:([\w.]+))?/,

        // Matches dashed string for camelizing
	rdashAlpha = /-([a-z])/ig,

        // Used by jQuery.camelCase as callback to replace()
	fcamelCase = function (all, letter) {
	    return letter.toUpperCase();
	},

        // Keep a UserAgent string for use with jQuery.browser
	userAgent = navigator.userAgent,

        // For matching the engine and version of the browser
	browserMatch,

        // The deferred used on DOM ready
	readyList,

        // The ready event handler
	DOMContentLoaded,

        // Save a reference to some core methods
	toString = Object.prototype.toString,
	hasOwn = Object.prototype.hasOwnProperty,
	push = Array.prototype.push,
	slice = Array.prototype.slice,
	trim = String.prototype.trim,
	indexOf = Array.prototype.indexOf,

        // [[Class]] -> type pairs
	class2type = {};

        jQuery.fn = jQuery.prototype = {
            constructor: jQuery,
            init: function (selector, context, rootjQuery) {
                var match, elem, ret, doc;

                // Handle $(""), $(null), or $(undefined)
                if (!selector) {
                    return this;
                }

                // Handle $(DOMElement)
                if (selector.nodeType) {
                    this.context = this[0] = selector;
                    this.length = 1;
                    return this;
                }

                // The body element only exists once, optimize finding it
                if (selector === "body" && !context && document.body) {
                    this.context = document;
                    this[0] = document.body;
                    this.selector = selector;
                    this.length = 1;
                    return this;
                }

                // Handle HTML strings
                if (typeof selector === "string") {
                    // Are we dealing with HTML string or an ID?
                    if (selector.charAt(0) === "<" && selector.charAt(selector.length - 1) === ">" && selector.length >= 3) {
                        // Assume that strings that start and end with <> are HTML and skip the regex check
                        match = [null, selector, null];

                    } else {
                        match = quickExpr.exec(selector);
                    }

                    // Verify a match, and that no context was specified for #id
                    if (match && (match[1] || !context)) {

                        // HANDLE: $(html) -> $(array)
                        if (match[1]) {
                            context = context instanceof jQuery ? context[0] : context;
                            doc = (context ? context.ownerDocument || context : document);

                            // If a single string is passed in and it's a single tag
                            // just do a createElement and skip the rest
                            ret = rsingleTag.exec(selector);

                            if (ret) {
                                if (jQuery.isPlainObject(context)) {
                                    selector = [document.createElement(ret[1])];
                                    jQuery.fn.attr.call(selector, context, true);

                                } else {
                                    selector = [doc.createElement(ret[1])];
                                }

                            } else {
                                ret = jQuery.buildFragment([match[1]], [doc]);
                                selector = (ret.cacheable ? jQuery.clone(ret.fragment) : ret.fragment).childNodes;
                            }

                            return jQuery.merge(this, selector);

                            // HANDLE: $("#id")
                        } else {
                            elem = document.getElementById(match[2]);

                            // Check parentNode to catch when Blackberry 4.6 returns
                            // nodes that are no longer in the document #6963
                            if (elem && elem.parentNode) {
                                // Handle the case where IE and Opera return items
                                // by name instead of ID
                                if (elem.id !== match[2]) {
                                    return rootjQuery.find(selector);
                                }

                                // Otherwise, we inject the element directly into the jQuery object
                                this.length = 1;
                                this[0] = elem;
                            }

                            this.context = document;
                            this.selector = selector;
                            return this;
                        }

                        // HANDLE: $(expr, $(...))
                    } else if (!context || context.jquery) {
                        return (context || rootjQuery).find(selector);

                        // HANDLE: $(expr, context)
                        // (which is just equivalent to: $(context).find(expr)
                    } else {
                        return this.constructor(context).find(selector);
                    }

                    // HANDLE: $(function)
                    // Shortcut for document ready
                } else if (jQuery.isFunction(selector)) {
                    return rootjQuery.ready(selector);
                }

                if (selector.selector !== undefined) {
                    this.selector = selector.selector;
                    this.context = selector.context;
                }

                return jQuery.makeArray(selector, this);
            },

            // Start with an empty selector
            selector: "",

            // The current version of jQuery being used
            jquery: "1.6.2",

            // The default length of a jQuery object is 0
            length: 0,

            // The number of elements contained in the matched element set
            size: function () {
                return this.length;
            },

            toArray: function () {
                return slice.call(this, 0);
            },

            // Get the Nth element in the matched element set OR
            // Get the whole matched element set as a clean array
            get: function (num) {
                return num == null ?

                // Return a 'clean' array
			this.toArray() :

                // Return just the object
			(num < 0 ? this[this.length + num] : this[num]);
            },

            // Take an array of elements and push it onto the stack
            // (returning the new matched element set)
            pushStack: function (elems, name, selector) {
                // Build a new jQuery matched element set
                var ret = this.constructor();

                if (jQuery.isArray(elems)) {
                    push.apply(ret, elems);

                } else {
                    jQuery.merge(ret, elems);
                }

                // Add the old object onto the stack (as a reference)
                ret.prevObject = this;

                ret.context = this.context;

                if (name === "find") {
                    ret.selector = this.selector + (this.selector ? " " : "") + selector;
                } else if (name) {
                    ret.selector = this.selector + "." + name + "(" + selector + ")";
                }

                // Return the newly-formed element set
                return ret;
            },

            // Execute a callback for every element in the matched set.
            // (You can seed the arguments with an array of args, but this is
            // only used internally.)
            each: function (callback, args) {
                return jQuery.each(this, callback, args);
            },

            ready: function (fn) {
                // Attach the listeners
                jQuery.bindReady();

                // Add the callback
                readyList.done(fn);

                return this;
            },

            eq: function (i) {
                return i === -1 ?
			this.slice(i) :
			this.slice(i, +i + 1);
            },

            first: function () {
                return this.eq(0);
            },

            last: function () {
                return this.eq(-1);
            },

            slice: function () {
                return this.pushStack(slice.apply(this, arguments),
			"slice", slice.call(arguments).join(","));
            },

            map: function (callback) {
                return this.pushStack(jQuery.map(this, function (elem, i) {
                    return callback.call(elem, i, elem);
                }));
            },

            end: function () {
                return this.prevObject || this.constructor(null);
            },

            // For internal use only.
            // Behaves like an Array's method, not like a jQuery method.
            push: push,
            sort: [].sort,
            splice: [].splice
        };

        // Give the init function the jQuery prototype for later instantiation
        jQuery.fn.init.prototype = jQuery.fn;

        jQuery.extend = jQuery.fn.extend = function () {
            var options, name, src, copy, copyIsArray, clone,
		target = arguments[0] || {},
		i = 1,
		length = arguments.length,
		deep = false;

            // Handle a deep copy situation
            if (typeof target === "boolean") {
                deep = target;
                target = arguments[1] || {};
                // skip the boolean and the target
                i = 2;
            }

            // Handle case when target is a string or something (possible in deep copy)
            if (typeof target !== "object" && !jQuery.isFunction(target)) {
                target = {};
            }

            // extend jQuery itself if only one argument is passed
            if (length === i) {
                target = this;
                --i;
            }

            for (; i < length; i++) {
                // Only deal with non-null/undefined values
                if ((options = arguments[i]) != null) {
                    // Extend the base object
                    for (name in options) {
                        src = target[name];
                        copy = options[name];

                        // Prevent never-ending loop
                        if (target === copy) {
                            continue;
                        }

                        // Recurse if we're merging plain objects or arrays
                        if (deep && copy && (jQuery.isPlainObject(copy) || (copyIsArray = jQuery.isArray(copy)))) {
                            if (copyIsArray) {
                                copyIsArray = false;
                                clone = src && jQuery.isArray(src) ? src : [];

                            } else {
                                clone = src && jQuery.isPlainObject(src) ? src : {};
                            }

                            // Never move original objects, clone them
                            target[name] = jQuery.extend(deep, clone, copy);

                            // Don't bring in undefined values
                        } else if (copy !== undefined) {
                            target[name] = copy;
                        }
                    }
                }
            }

            // Return the modified object
            return target;
        };

        jQuery.extend({
            noConflict: function (deep) {
                if (window.$ === jQuery) {
                    window.$ = _$;
                }

                if (deep && window.jQuery === jQuery) {
                    window.jQuery = _jQuery;
                }

                return jQuery;
            },

            // Is the DOM ready to be used? Set to true once it occurs.
            isReady: false,

            // A counter to track how many items to wait for before
            // the ready event fires. See #6781
            readyWait: 1,

            // Hold (or release) the ready event
            holdReady: function (hold) {
                if (hold) {
                    jQuery.readyWait++;
                } else {
                    jQuery.ready(true);
                }
            },

            // Handle when the DOM is ready
            ready: function (wait) {
                // Either a released hold or an DOMready/load event and not yet ready
                if ((wait === true && ! --jQuery.readyWait) || (wait !== true && !jQuery.isReady)) {
                    // Make sure body exists, at least, in case IE gets a little overzealous (ticket #5443).
                    if (!document.body) {
                        return setTimeout(jQuery.ready, 1);
                    }

                    // Remember that the DOM is ready
                    jQuery.isReady = true;

                    // If a normal DOM Ready event fired, decrement, and wait if need be
                    if (wait !== true && --jQuery.readyWait > 0) {
                        return;
                    }

                    // If there are functions bound, to execute
                    readyList.resolveWith(document, [jQuery]);

                    // Trigger any bound ready events
                    if (jQuery.fn.trigger) {
                        jQuery(document).trigger("ready").unbind("ready");
                    }
                }
            },

            bindReady: function () {
                if (readyList) {
                    return;
                }

                readyList = jQuery._Deferred();

                // Catch cases where $(document).ready() is called after the
                // browser event has already occurred.
                if (document.readyState === "complete") {
                    // Handle it asynchronously to allow scripts the opportunity to delay ready
                    return setTimeout(jQuery.ready, 1);
                }

                // Mozilla, Opera and webkit nightlies currently support this event
                if (document.addEventListener) {
                    // Use the handy event callback
                    document.addEventListener("DOMContentLoaded", DOMContentLoaded, false);

                    // A fallback to window.onload, that will always work
                    window.addEventListener("load", jQuery.ready, false);

                    // If IE event model is used
                } else if (document.attachEvent) {
                    // ensure firing before onload,
                    // maybe late but safe also for iframes
                    document.attachEvent("onreadystatechange", DOMContentLoaded);

                    // A fallback to window.onload, that will always work
                    window.attachEvent("onload", jQuery.ready);

                    // If IE and not a frame
                    // continually check to see if the document is ready
                    var toplevel = false;

                    try {
                        toplevel = window.frameElement == null;
                    } catch (e) { }

                    if (document.documentElement.doScroll && toplevel) {
                        doScrollCheck();
                    }
                }
            },

            // See test/unit/core.js for details concerning isFunction.
            // Since version 1.3, DOM methods and functions like alert
            // aren't supported. They return false on IE (#2968).
            isFunction: function (obj) {
                return jQuery.type(obj) === "function";
            },

            isArray: Array.isArray || function (obj) {
                return jQuery.type(obj) === "array";
            },

            // A crude way of determining if an object is a window
            isWindow: function (obj) {
                return obj && typeof obj === "object" && "setInterval" in obj;
            },

            isNaN: function (obj) {
                return obj == null || !rdigit.test(obj) || isNaN(obj);
            },

            type: function (obj) {
                return obj == null ?
			String(obj) :
			class2type[toString.call(obj)] || "object";
            },

            isPlainObject: function (obj) {
                // Must be an Object.
                // Because of IE, we also have to check the presence of the constructor property.
                // Make sure that DOM nodes and window objects don't pass through, as well
                if (!obj || jQuery.type(obj) !== "object" || obj.nodeType || jQuery.isWindow(obj)) {
                    return false;
                }

                // Not own constructor property must be Object
                if (obj.constructor &&
			!hasOwn.call(obj, "constructor") &&
			!hasOwn.call(obj.constructor.prototype, "isPrototypeOf")) {
                    return false;
                }

                // Own properties are enumerated firstly, so to speed up,
                // if last one is own, then all properties are own.

                var key;
                for (key in obj) { }

                return key === undefined || hasOwn.call(obj, key);
            },

            isEmptyObject: function (obj) {
                for (var name in obj) {
                    return false;
                }
                return true;
            },

            error: function (msg) {
                throw msg;
            },

            parseJSON: function (data) {
                if (typeof data !== "string" || !data) {
                    return null;
                }

                // Make sure leading/trailing whitespace is removed (IE can't handle it)
                data = jQuery.trim(data);

                // Attempt to parse using the native JSON parser first
                if (window.JSON && window.JSON.parse) {
                    return window.JSON.parse(data);
                }

                // Make sure the incoming data is actual JSON
                // Logic borrowed from http://json.org/json2.js
                if (rvalidchars.test(data.replace(rvalidescape, "@")
			.replace(rvalidtokens, "]")
			.replace(rvalidbraces, ""))) {

                    return (new Function("return " + data))();

                }
                jQuery.error("Invalid JSON: " + data);
            },

            // Cross-browser xml parsing
            // (xml & tmp used internally)
            parseXML: function (data, xml, tmp) {

                if (window.DOMParser) { // Standard
                    tmp = new DOMParser();
                    xml = tmp.parseFromString(data, "text/xml");
                } else { // IE
                    xml = new ActiveXObject("Microsoft.XMLDOM");
                    xml.async = "false";
                    xml.loadXML(data);
                }

                tmp = xml.documentElement;

                if (!tmp || !tmp.nodeName || tmp.nodeName === "parsererror") {
                    jQuery.error("Invalid XML: " + data);
                }

                return xml;
            },

            noop: function () { },

            // Evaluates a script in a global context
            // Workarounds based on findings by Jim Driscoll
            // http://weblogs.java.net/blog/driscoll/archive/2009/09/08/eval-javascript-global-context
            globalEval: function (data) {
                if (data && rnotwhite.test(data)) {
                    // We use execScript on Internet Explorer
                    // We use an anonymous function so that context is window
                    // rather than jQuery in Firefox
                    (window.execScript || function (data) {
                        window["eval"].call(window, data);
                    })(data);
                }
            },

            // Converts a dashed string to camelCased string;
            // Used by both the css and data modules
            camelCase: function (string) {
                return string.replace(rdashAlpha, fcamelCase);
            },

            nodeName: function (elem, name) {
                return elem.nodeName && elem.nodeName.toUpperCase() === name.toUpperCase();
            },

            // args is for internal usage only
            each: function (object, callback, args) {
                var name, i = 0,
			length = object.length,
			isObj = length === undefined || jQuery.isFunction(object);

                if (args) {
                    if (isObj) {
                        for (name in object) {
                            if (callback.apply(object[name], args) === false) {
                                break;
                            }
                        }
                    } else {
                        for (; i < length; ) {
                            if (callback.apply(object[i++], args) === false) {
                                break;
                            }
                        }
                    }

                    // A special, fast, case for the most common use of each
                } else {
                    if (isObj) {
                        for (name in object) {
                            if (callback.call(object[name], name, object[name]) === false) {
                                break;
                            }
                        }
                    } else {
                        for (; i < length; ) {
                            if (callback.call(object[i], i, object[i++]) === false) {
                                break;
                            }
                        }
                    }
                }

                return object;
            },

            // Use native String.trim function wherever possible
            trim: trim ?
		function (text) {
		    return text == null ?
				"" :
				trim.call(text);
		} :

            // Otherwise use our own trimming functionality
		function (text) {
		    return text == null ?
				"" :
				text.toString().replace(trimLeft, "").replace(trimRight, "");
		},

            // results is for internal usage only
            makeArray: function (array, results) {
                var ret = results || [];

                if (array != null) {
                    // The window, strings (and functions) also have 'length'
                    // The extra typeof function check is to prevent crashes
                    // in Safari 2 (See: #3039)
                    // Tweaked logic slightly to handle Blackberry 4.7 RegExp issues #6930
                    var type = jQuery.type(array);

                    if (array.length == null || type === "string" || type === "function" || type === "regexp" || jQuery.isWindow(array)) {
                        push.call(ret, array);
                    } else {
                        jQuery.merge(ret, array);
                    }
                }

                return ret;
            },

            inArray: function (elem, array) {

                if (indexOf) {
                    return indexOf.call(array, elem);
                }

                for (var i = 0, length = array.length; i < length; i++) {
                    if (array[i] === elem) {
                        return i;
                    }
                }

                return -1;
            },

            merge: function (first, second) {
                var i = first.length,
			j = 0;

                if (typeof second.length === "number") {
                    for (var l = second.length; j < l; j++) {
                        first[i++] = second[j];
                    }

                } else {
                    while (second[j] !== undefined) {
                        first[i++] = second[j++];
                    }
                }

                first.length = i;

                return first;
            },

            grep: function (elems, callback, inv) {
                var ret = [], retVal;
                inv = !!inv;

                // Go through the array, only saving the items
                // that pass the validator function
                for (var i = 0, length = elems.length; i < length; i++) {
                    retVal = !!callback(elems[i], i);
                    if (inv !== retVal) {
                        ret.push(elems[i]);
                    }
                }

                return ret;
            },

            // arg is for internal usage only
            map: function (elems, callback, arg) {
                var value, key, ret = [],
			i = 0,
			length = elems.length,
                // jquery objects are treated as arrays
			isArray = elems instanceof jQuery || length !== undefined && typeof length === "number" && ((length > 0 && elems[0] && elems[length - 1]) || length === 0 || jQuery.isArray(elems));

                // Go through the array, translating each of the items to their
                if (isArray) {
                    for (; i < length; i++) {
                        value = callback(elems[i], i, arg);

                        if (value != null) {
                            ret[ret.length] = value;
                        }
                    }

                    // Go through every key on the object,
                } else {
                    for (key in elems) {
                        value = callback(elems[key], key, arg);

                        if (value != null) {
                            ret[ret.length] = value;
                        }
                    }
                }

                // Flatten any nested arrays
                return ret.concat.apply([], ret);
            },

            // A global GUID counter for objects
            guid: 1,

            // Bind a function to a context, optionally partially applying any
            // arguments.
            proxy: function (fn, context) {
                if (typeof context === "string") {
                    var tmp = fn[context];
                    context = fn;
                    fn = tmp;
                }

                // Quick check to determine if target is callable, in the spec
                // this throws a TypeError, but we will just return undefined.
                if (!jQuery.isFunction(fn)) {
                    return undefined;
                }

                // Simulated bind
                var args = slice.call(arguments, 2),
			proxy = function () {
			    return fn.apply(context, args.concat(slice.call(arguments)));
			};

                // Set the guid of unique handler to the same of original handler, so it can be removed
                proxy.guid = fn.guid = fn.guid || proxy.guid || jQuery.guid++;

                return proxy;
            },

            // Mutifunctional method to get and set values to a collection
            // The value/s can optionally be executed if it's a function
            access: function (elems, key, value, exec, fn, pass) {
                var length = elems.length;

                // Setting many attributes
                if (typeof key === "object") {
                    for (var k in key) {
                        jQuery.access(elems, k, key[k], exec, fn, value);
                    }
                    return elems;
                }

                // Setting one attribute
                if (value !== undefined) {
                    // Optionally, function values get executed if exec is true
                    exec = !pass && exec && jQuery.isFunction(value);

                    for (var i = 0; i < length; i++) {
                        fn(elems[i], key, exec ? value.call(elems[i], i, fn(elems[i], key)) : value, pass);
                    }

                    return elems;
                }

                // Getting an attribute
                return length ? fn(elems[0], key) : undefined;
            },

            now: function () {
                return (new Date()).getTime();
            },

            // Use of jQuery.browser is frowned upon.
            // More details: http://docs.jquery.com/Utilities/jQuery.browser
            uaMatch: function (ua) {
                ua = ua.toLowerCase();

                var match = rwebkit.exec(ua) ||
			ropera.exec(ua) ||
			rmsie.exec(ua) ||
			ua.indexOf("compatible") < 0 && rmozilla.exec(ua) ||
			[];

                return { browser: match[1] || "", version: match[2] || "0" };
            },

            sub: function () {
                function jQuerySub(selector, context) {
                    return new jQuerySub.fn.init(selector, context);
                }
                jQuery.extend(true, jQuerySub, this);
                jQuerySub.superclass = this;
                jQuerySub.fn = jQuerySub.prototype = this();
                jQuerySub.fn.constructor = jQuerySub;
                jQuerySub.sub = this.sub;
                jQuerySub.fn.init = function init(selector, context) {
                    if (context && context instanceof jQuery && !(context instanceof jQuerySub)) {
                        context = jQuerySub(context);
                    }

                    return jQuery.fn.init.call(this, selector, context, rootjQuerySub);
                };
                jQuerySub.fn.init.prototype = jQuerySub.fn;
                var rootjQuerySub = jQuerySub(document);
                return jQuerySub;
            },

            browser: {}
        });

        // Populate the class2type map
        jQuery.each("Boolean Number String Function Array Date RegExp Object".split(" "), function (i, name) {
            class2type["[object " + name + "]"] = name.toLowerCase();
        });

        browserMatch = jQuery.uaMatch(userAgent);
        if (browserMatch.browser) {
            jQuery.browser[browserMatch.browser] = true;
            jQuery.browser.version = browserMatch.version;
        }

        // Deprecated, use jQuery.browser.webkit instead
        if (jQuery.browser.webkit) {
            jQuery.browser.safari = true;
        }

        // IE doesn't match non-breaking spaces with \s
        if (rnotwhite.test("\xA0")) {
            trimLeft = /^[\s\xA0]+/;
            trimRight = /[\s\xA0]+$/;
        }

        // All jQuery objects should point back to these
        rootjQuery = jQuery(document);

        // Cleanup functions for the document ready method
        if (document.addEventListener) {
            DOMContentLoaded = function () {
                document.removeEventListener("DOMContentLoaded", DOMContentLoaded, false);
                jQuery.ready();
            };

        } else if (document.attachEvent) {
            DOMContentLoaded = function () {
                // Make sure body exists, at least, in case IE gets a little overzealous (ticket #5443).
                if (document.readyState === "complete") {
                    document.detachEvent("onreadystatechange", DOMContentLoaded);
                    jQuery.ready();
                }
            };
        }

        // The DOM ready check for Internet Explorer
        function doScrollCheck() {
            if (jQuery.isReady) {
                return;
            }

            try {
                // If IE is used, use the trick by Diego Perini
                // http://javascript.nwbox.com/IEContentLoaded/
                document.documentElement.doScroll("left");
            } catch (e) {
                setTimeout(doScrollCheck, 1);
                return;
            }

            // and execute any waiting functions
            jQuery.ready();
        }

        return jQuery;

    })();


    var // Promise methods
	promiseMethods = "done fail isResolved isRejected promise then always pipe".split(" "),
    // Static reference to slice
	sliceDeferred = [].slice;

    jQuery.extend({
        // Create a simple deferred (one callbacks list)
        _Deferred: function () {
            var // callbacks list
			callbacks = [],
            // stored [ context , args ]
			fired,
            // to avoid firing when already doing so
			firing,
            // flag to know if the deferred has been cancelled
			cancelled,
            // the deferred itself
			deferred = {

			    // done( f1, f2, ...)
			    done: function () {
			        if (!cancelled) {
			            var args = arguments,
							i,
							length,
							elem,
							type,
							_fired;
			            if (fired) {
			                _fired = fired;
			                fired = 0;
			            }
			            for (i = 0, length = args.length; i < length; i++) {
			                elem = args[i];
			                type = jQuery.type(elem);
			                if (type === "array") {
			                    deferred.done.apply(deferred, elem);
			                } else if (type === "function") {
			                    callbacks.push(elem);
			                }
			            }
			            if (_fired) {
			                deferred.resolveWith(_fired[0], _fired[1]);
			            }
			        }
			        return this;
			    },

			    // resolve with given context and args
			    resolveWith: function (context, args) {
			        if (!cancelled && !fired && !firing) {
			            // make sure args are available (#8421)
			            args = args || [];
			            firing = 1;
			            try {
			                while (callbacks[0]) {
			                    callbacks.shift().apply(context, args);
			                }
			            }
			            finally {
			                fired = [context, args];
			                firing = 0;
			            }
			        }
			        return this;
			    },

			    // resolve with this as context and given arguments
			    resolve: function () {
			        deferred.resolveWith(this, arguments);
			        return this;
			    },

			    // Has this deferred been resolved?
			    isResolved: function () {
			        return !!(firing || fired);
			    },

			    // Cancel
			    cancel: function () {
			        cancelled = 1;
			        callbacks = [];
			        return this;
			    }
			};

            return deferred;
        },

        // Full fledged deferred (two callbacks list)
        Deferred: function (func) {
            var deferred = jQuery._Deferred(),
			failDeferred = jQuery._Deferred(),
			promise;
            // Add errorDeferred methods, then and promise
            jQuery.extend(deferred, {
                then: function (doneCallbacks, failCallbacks) {
                    deferred.done(doneCallbacks).fail(failCallbacks);
                    return this;
                },
                always: function () {
                    return deferred.done.apply(deferred, arguments).fail.apply(this, arguments);
                },
                fail: failDeferred.done,
                rejectWith: failDeferred.resolveWith,
                reject: failDeferred.resolve,
                isRejected: failDeferred.isResolved,
                pipe: function (fnDone, fnFail) {
                    return jQuery.Deferred(function (newDefer) {
                        jQuery.each({
                            done: [fnDone, "resolve"],
                            fail: [fnFail, "reject"]
                        }, function (handler, data) {
                            var fn = data[0],
							action = data[1],
							returned;
                            if (jQuery.isFunction(fn)) {
                                deferred[handler](function () {
                                    returned = fn.apply(this, arguments);
                                    if (returned && jQuery.isFunction(returned.promise)) {
                                        returned.promise().then(newDefer.resolve, newDefer.reject);
                                    } else {
                                        newDefer[action](returned);
                                    }
                                });
                            } else {
                                deferred[handler](newDefer[action]);
                            }
                        });
                    }).promise();
                },
                // Get a promise for this deferred
                // If obj is provided, the promise aspect is added to the object
                promise: function (obj) {
                    if (obj == null) {
                        if (promise) {
                            return promise;
                        }
                        promise = obj = {};
                    }
                    var i = promiseMethods.length;
                    while (i--) {
                        obj[promiseMethods[i]] = deferred[promiseMethods[i]];
                    }
                    return obj;
                }
            });
            // Make sure only one callback list will be used
            deferred.done(failDeferred.cancel).fail(deferred.cancel);
            // Unexpose cancel
            delete deferred.cancel;
            // Call given func if any
            if (func) {
                func.call(deferred, deferred);
            }
            return deferred;
        },

        // Deferred helper
        when: function (firstParam) {
            var args = arguments,
			i = 0,
			length = args.length,
			count = length,
			deferred = length <= 1 && firstParam && jQuery.isFunction(firstParam.promise) ?
				firstParam :
				jQuery.Deferred();
            function resolveFunc(i) {
                return function (value) {
                    args[i] = arguments.length > 1 ? sliceDeferred.call(arguments, 0) : value;
                    if (!(--count)) {
                        // Strange bug in FF4:
                        // Values changed onto the arguments object sometimes end up as undefined values
                        // outside the $.when method. Cloning the object into a fresh array solves the issue
                        deferred.resolveWith(deferred, sliceDeferred.call(args, 0));
                    }
                };
            }
            if (length > 1) {
                for (; i < length; i++) {
                    if (args[i] && jQuery.isFunction(args[i].promise)) {
                        args[i].promise().then(resolveFunc(i), deferred.reject);
                    } else {
                        --count;
                    }
                }
                if (!count) {
                    deferred.resolveWith(deferred, args);
                }
            } else if (deferred !== firstParam) {
                deferred.resolveWith(deferred, length ? [firstParam] : []);
            }
            return deferred.promise();
        }
    });



    jQuery.support = (function () {

        var div = document.createElement("div"),
		documentElement = document.documentElement,
		all,
		a,
		select,
		opt,
		input,
		marginDiv,
		support,
		fragment,
		body,
		testElementParent,
		testElement,
		testElementStyle,
		tds,
		events,
		eventName,
		i,
		isSupported;

        // Preliminary tests
        div.setAttribute("className", "t");
        div.innerHTML = "   <link/><table></table><a href='/a' style='top:1px;float:left;opacity:.55;'>a</a><input type='checkbox'/>";

        all = div.getElementsByTagName("*");
        a = div.getElementsByTagName("a")[0];

        // Can't get basic test support
        if (!all || !all.length || !a) {
            return {};
        }

        // First batch of supports tests
        select = document.createElement("select");
        opt = select.appendChild(document.createElement("option"));
        input = div.getElementsByTagName("input")[0];

        support = {
            // IE strips leading whitespace when .innerHTML is used
            leadingWhitespace: (div.firstChild.nodeType === 3),

            // Make sure that tbody elements aren't automatically inserted
            // IE will insert them into empty tables
            tbody: !div.getElementsByTagName("tbody").length,

            // Make sure that link elements get serialized correctly by innerHTML
            // This requires a wrapper element in IE
            htmlSerialize: !!div.getElementsByTagName("link").length,

            // Get the style information from getAttribute
            // (IE uses .cssText instead)
            style: /top/.test(a.getAttribute("style")),

            // Make sure that URLs aren't manipulated
            // (IE normalizes it by default)
            hrefNormalized: (a.getAttribute("href") === "/a"),

            // Make sure that element opacity exists
            // (IE uses filter instead)
            // Use a regex to work around a WebKit issue. See #5145
            opacity: /^0.55$/.test(a.style.opacity),

            // Verify style float existence
            // (IE uses styleFloat instead of cssFloat)
            cssFloat: !!a.style.cssFloat,

            // Make sure that if no value is specified for a checkbox
            // that it defaults to "on".
            // (WebKit defaults to "" instead)
            checkOn: (input.value === "on"),

            // Make sure that a selected-by-default option has a working selected property.
            // (WebKit defaults to false instead of true, IE too, if it's in an optgroup)
            optSelected: opt.selected,

            // Test setAttribute on camelCase class. If it works, we need attrFixes when doing get/setAttribute (ie6/7)
            getSetAttribute: div.className !== "t",

            // Will be defined later
            submitBubbles: true,
            changeBubbles: true,
            focusinBubbles: false,
            deleteExpando: true,
            noCloneEvent: true,
            inlineBlockNeedsLayout: false,
            shrinkWrapBlocks: false,
            reliableMarginRight: true
        };

        // Make sure checked status is properly cloned
        input.checked = true;
        support.noCloneChecked = input.cloneNode(true).checked;

        // Make sure that the options inside disabled selects aren't marked as disabled
        // (WebKit marks them as disabled)
        select.disabled = true;
        support.optDisabled = !opt.disabled;

        // Test to see if it's possible to delete an expando from an element
        // Fails in Internet Explorer
        try {
            delete div.test;
        } catch (e) {
            support.deleteExpando = false;
        }

        if (!div.addEventListener && div.attachEvent && div.fireEvent) {
            div.attachEvent("onclick", function () {
                // Cloning a node shouldn't copy over any
                // bound event handlers (IE does this)
                support.noCloneEvent = false;
            });
            div.cloneNode(true).fireEvent("onclick");
        }

        // Check if a radio maintains it's value
        // after being appended to the DOM
        input = document.createElement("input");
        input.value = "t";
        input.setAttribute("type", "radio");
        support.radioValue = input.value === "t";

        input.setAttribute("checked", "checked");
        div.appendChild(input);
        fragment = document.createDocumentFragment();
        fragment.appendChild(div.firstChild);

        // WebKit doesn't clone checked state correctly in fragments
        support.checkClone = fragment.cloneNode(true).cloneNode(true).lastChild.checked;

        div.innerHTML = "";

        // Figure out if the W3C box model works as expected
        div.style.width = div.style.paddingLeft = "1px";

        body = document.getElementsByTagName("body")[0];
        // We use our own, invisible, body unless the body is already present
        // in which case we use a div (#9239)
        testElement = document.createElement(body ? "div" : "body");
        testElementStyle = {
            visibility: "hidden",
            width: 0,
            height: 0,
            border: 0,
            margin: 0
        };
        if (body) {
            jQuery.extend(testElementStyle, {
                position: "absolute",
                left: -1000,
                top: -1000
            });
        }
        for (i in testElementStyle) {
            testElement.style[i] = testElementStyle[i];
        }
        testElement.appendChild(div);
        testElementParent = body || documentElement;
        testElementParent.insertBefore(testElement, testElementParent.firstChild);

        // Check if a disconnected checkbox will retain its checked
        // value of true after appended to the DOM (IE6/7)
        support.appendChecked = input.checked;

        support.boxModel = div.offsetWidth === 2;

        if ("zoom" in div.style) {
            // Check if natively block-level elements act like inline-block
            // elements when setting their display to 'inline' and giving
            // them layout
            // (IE < 8 does this)
            div.style.display = "inline";
            div.style.zoom = 1;
            support.inlineBlockNeedsLayout = (div.offsetWidth === 2);

            // Check if elements with layout shrink-wrap their children
            // (IE 6 does this)
            div.style.display = "";
            div.innerHTML = "<div style='width:4px;'></div>";
            support.shrinkWrapBlocks = (div.offsetWidth !== 2);
        }

        div.innerHTML = "<table><tr><td style='padding:0;border:0;display:none'></td><td>t</td></tr></table>";
        tds = div.getElementsByTagName("td");

        // Check if table cells still have offsetWidth/Height when they are set
        // to display:none and there are still other visible table cells in a
        // table row; if so, offsetWidth/Height are not reliable for use when
        // determining if an element has been hidden directly using
        // display:none (it is still safe to use offsets if a parent element is
        // hidden; don safety goggles and see bug #4512 for more information).
        // (only IE 8 fails this test)
        isSupported = (tds[0].offsetHeight === 0);

        tds[0].style.display = "";
        tds[1].style.display = "none";

        // Check if empty table cells still have offsetWidth/Height
        // (IE < 8 fail this test)
        support.reliableHiddenOffsets = isSupported && (tds[0].offsetHeight === 0);
        div.innerHTML = "";

        // Check if div with explicit width and no margin-right incorrectly
        // gets computed margin-right based on width of container. For more
        // info see bug #3333
        // Fails in WebKit before Feb 2011 nightlies
        // WebKit Bug 13343 - getComputedStyle returns wrong value for margin-right
        if (document.defaultView && document.defaultView.getComputedStyle) {
            marginDiv = document.createElement("div");
            marginDiv.style.width = "0";
            marginDiv.style.marginRight = "0";
            div.appendChild(marginDiv);
            support.reliableMarginRight =
			(parseInt((document.defaultView.getComputedStyle(marginDiv, null) || { marginRight: 0 }).marginRight, 10) || 0) === 0;
        }

        // Remove the body element we added
        testElement.innerHTML = "";
        testElementParent.removeChild(testElement);

        // Technique from Juriy Zaytsev
        // http://thinkweb2.com/projects/prototype/detecting-event-support-without-browser-sniffing/
        // We only care about the case where non-standard event systems
        // are used, namely in IE. Short-circuiting here helps us to
        // avoid an eval call (in setAttribute) which can cause CSP
        // to go haywire. See: https://developer.mozilla.org/en/Security/CSP
        if (div.attachEvent) {
            for (i in {
                submit: 1,
                change: 1,
                focusin: 1
            }) {
                eventName = "on" + i;
                isSupported = (eventName in div);
                if (!isSupported) {
                    div.setAttribute(eventName, "return;");
                    isSupported = (typeof div[eventName] === "function");
                }
                support[i + "Bubbles"] = isSupported;
            }
        }

        // Null connected elements to avoid leaks in IE
        testElement = fragment = select = opt = body = marginDiv = div = input = null;

        return support;
    })();

    // Keep track of boxModel
    jQuery.boxModel = jQuery.support.boxModel;




    var rbrace = /^(?:\{.*\}|\[.*\])$/,
	rmultiDash = /([a-z])([A-Z])/g;

    jQuery.extend({
        cache: {},

        // Please use with caution
        uuid: 0,

        // Unique for each copy of jQuery on the page
        // Non-digits removed to match rinlinejQuery
        expando: "jQuery" + (jQuery.fn.jquery + Math.random()).replace(/\D/g, ""),

        // The following elements throw uncatchable exceptions if you
        // attempt to add expando properties to them.
        noData: {
            "embed": true,
            // Ban all objects except for Flash (which handle expandos)
            "object": "clsid:D27CDB6E-AE6D-11cf-96B8-444553540000",
            "applet": true
        },

        hasData: function (elem) {
            elem = elem.nodeType ? jQuery.cache[elem[jQuery.expando]] : elem[jQuery.expando];

            return !!elem && !isEmptyDataObject(elem);
        },

        data: function (elem, name, data, pvt /* Internal Use Only */) {
            if (!jQuery.acceptData(elem)) {
                return;
            }

            var internalKey = jQuery.expando, getByName = typeof name === "string", thisCache,

            // We have to handle DOM nodes and JS objects differently because IE6-7
            // can't GC object references properly across the DOM-JS boundary
			isNode = elem.nodeType,

            // Only DOM nodes need the global jQuery cache; JS object data is
            // attached directly to the object so GC can occur automatically
			cache = isNode ? jQuery.cache : elem,

            // Only defining an ID for JS objects if its cache already exists allows
            // the code to shortcut on the same path as a DOM node with no cache
			id = isNode ? elem[jQuery.expando] : elem[jQuery.expando] && jQuery.expando;

            // Avoid doing any more work than we need to when trying to get data on an
            // object that has no data at all
            if ((!id || (pvt && id && !cache[id][internalKey])) && getByName && data === undefined) {
                return;
            }

            if (!id) {
                // Only DOM nodes need a new unique ID for each element since their data
                // ends up in the global cache
                if (isNode) {
                    elem[jQuery.expando] = id = ++jQuery.uuid;
                } else {
                    id = jQuery.expando;
                }
            }

            if (!cache[id]) {
                cache[id] = {};

                // TODO: This is a hack for 1.5 ONLY. Avoids exposing jQuery
                // metadata on plain JS objects when the object is serialized using
                // JSON.stringify
                if (!isNode) {
                    cache[id].toJSON = jQuery.noop;
                }
            }

            // An object can be passed to jQuery.data instead of a key/value pair; this gets
            // shallow copied over onto the existing cache
            if (typeof name === "object" || typeof name === "function") {
                if (pvt) {
                    cache[id][internalKey] = jQuery.extend(cache[id][internalKey], name);
                } else {
                    cache[id] = jQuery.extend(cache[id], name);
                }
            }

            thisCache = cache[id];

            // Internal jQuery data is stored in a separate object inside the object's data
            // cache in order to avoid key collisions between internal data and user-defined
            // data
            if (pvt) {
                if (!thisCache[internalKey]) {
                    thisCache[internalKey] = {};
                }

                thisCache = thisCache[internalKey];
            }

            if (data !== undefined) {
                thisCache[jQuery.camelCase(name)] = data;
            }

            // TODO: This is a hack for 1.5 ONLY. It will be removed in 1.6. Users should
            // not attempt to inspect the internal events object using jQuery.data, as this
            // internal data object is undocumented and subject to change.
            if (name === "events" && !thisCache[name]) {
                return thisCache[internalKey] && thisCache[internalKey].events;
            }

            return getByName ?
            // Check for both converted-to-camel and non-converted data property names
			thisCache[jQuery.camelCase(name)] || thisCache[name] :
			thisCache;
        },

        removeData: function (elem, name, pvt /* Internal Use Only */) {
            if (!jQuery.acceptData(elem)) {
                return;
            }

            var internalKey = jQuery.expando, isNode = elem.nodeType,

            // See jQuery.data for more information
			cache = isNode ? jQuery.cache : elem,

            // See jQuery.data for more information
			id = isNode ? elem[jQuery.expando] : jQuery.expando;

            // If there is already no cache entry for this object, there is no
            // purpose in continuing
            if (!cache[id]) {
                return;
            }

            if (name) {
                var thisCache = pvt ? cache[id][internalKey] : cache[id];

                if (thisCache) {
                    delete thisCache[name];

                    // If there is no data left in the cache, we want to continue
                    // and let the cache object itself get destroyed
                    if (!isEmptyDataObject(thisCache)) {
                        return;
                    }
                }
            }

            // See jQuery.data for more information
            if (pvt) {
                delete cache[id][internalKey];

                // Don't destroy the parent cache unless the internal data object
                // had been the only thing left in it
                if (!isEmptyDataObject(cache[id])) {
                    return;
                }
            }

            var internalCache = cache[id][internalKey];

            // Browsers that fail expando deletion also refuse to delete expandos on
            // the window, but it will allow it on all other JS objects; other browsers
            // don't care
            if (jQuery.support.deleteExpando || cache != window) {
                delete cache[id];
            } else {
                cache[id] = null;
            }

            // We destroyed the entire user cache at once because it's faster than
            // iterating through each key, but we need to continue to persist internal
            // data if it existed
            if (internalCache) {
                cache[id] = {};
                // TODO: This is a hack for 1.5 ONLY. Avoids exposing jQuery
                // metadata on plain JS objects when the object is serialized using
                // JSON.stringify
                if (!isNode) {
                    cache[id].toJSON = jQuery.noop;
                }

                cache[id][internalKey] = internalCache;

                // Otherwise, we need to eliminate the expando on the node to avoid
                // false lookups in the cache for entries that no longer exist
            } else if (isNode) {
                // IE does not allow us to delete expando properties from nodes,
                // nor does it have a removeAttribute function on Document nodes;
                // we must handle all of these cases
                if (jQuery.support.deleteExpando) {
                    delete elem[jQuery.expando];
                } else if (elem.removeAttribute) {
                    elem.removeAttribute(jQuery.expando);
                } else {
                    elem[jQuery.expando] = null;
                }
            }
        },

        // For internal use only.
        _data: function (elem, name, data) {
            return jQuery.data(elem, name, data, true);
        },

        // A method for determining if a DOM node can handle the data expando
        acceptData: function (elem) {
            if (elem.nodeName) {
                var match = jQuery.noData[elem.nodeName.toLowerCase()];

                if (match) {
                    return !(match === true || elem.getAttribute("classid") !== match);
                }
            }

            return true;
        }
    });

    jQuery.fn.extend({
        data: function (key, value) {
            var data = null;

            if (typeof key === "undefined") {
                if (this.length) {
                    data = jQuery.data(this[0]);

                    if (this[0].nodeType === 1) {
                        var attr = this[0].attributes, name;
                        for (var i = 0, l = attr.length; i < l; i++) {
                            name = attr[i].name;

                            if (name.indexOf("data-") === 0) {
                                name = jQuery.camelCase(name.substring(5));

                                dataAttr(this[0], name, data[name]);
                            }
                        }
                    }
                }

                return data;

            } else if (typeof key === "object") {
                return this.each(function () {
                    jQuery.data(this, key);
                });
            }

            var parts = key.split(".");
            parts[1] = parts[1] ? "." + parts[1] : "";

            if (value === undefined) {
                data = this.triggerHandler("getData" + parts[1] + "!", [parts[0]]);

                // Try to fetch any internally stored data first
                if (data === undefined && this.length) {
                    data = jQuery.data(this[0], key);
                    data = dataAttr(this[0], key, data);
                }

                return data === undefined && parts[1] ?
				this.data(parts[0]) :
				data;

            } else {
                return this.each(function () {
                    var $this = jQuery(this),
					args = [parts[0], value];

                    $this.triggerHandler("setData" + parts[1] + "!", args);
                    jQuery.data(this, key, value);
                    $this.triggerHandler("changeData" + parts[1] + "!", args);
                });
            }
        },

        removeData: function (key) {
            return this.each(function () {
                jQuery.removeData(this, key);
            });
        }
    });

    function dataAttr(elem, key, data) {
        // If nothing was found internally, try to fetch any
        // data from the HTML5 data-* attribute
        if (data === undefined && elem.nodeType === 1) {
            var name = "data-" + key.replace(rmultiDash, "$1-$2").toLowerCase();

            data = elem.getAttribute(name);

            if (typeof data === "string") {
                try {
                    data = data === "true" ? true :
				data === "false" ? false :
				data === "null" ? null :
				!jQuery.isNaN(data) ? parseFloat(data) :
					rbrace.test(data) ? jQuery.parseJSON(data) :
					data;
                } catch (e) { }

                // Make sure we set the data so it isn't changed later
                jQuery.data(elem, key, data);

            } else {
                data = undefined;
            }
        }

        return data;
    }

    // TODO: This is a hack for 1.5 ONLY to allow objects with a single toJSON
    // property to be considered empty objects; this property always exists in
    // order to make sure JSON.stringify does not expose internal metadata
    function isEmptyDataObject(obj) {
        for (var name in obj) {
            if (name !== "toJSON") {
                return false;
            }
        }

        return true;
    }




    function handleQueueMarkDefer(elem, type, src) {
        var deferDataKey = type + "defer",
		queueDataKey = type + "queue",
		markDataKey = type + "mark",
		defer = jQuery.data(elem, deferDataKey, undefined, true);
        if (defer &&
		(src === "queue" || !jQuery.data(elem, queueDataKey, undefined, true)) &&
		(src === "mark" || !jQuery.data(elem, markDataKey, undefined, true))) {
            // Give room for hard-coded callbacks to fire first
            // and eventually mark/queue something else on the element
            setTimeout(function () {
                if (!jQuery.data(elem, queueDataKey, undefined, true) &&
				!jQuery.data(elem, markDataKey, undefined, true)) {
                    jQuery.removeData(elem, deferDataKey, true);
                    defer.resolve();
                }
            }, 0);
        }
    }

    jQuery.extend({

        _mark: function (elem, type) {
            if (elem) {
                type = (type || "fx") + "mark";
                jQuery.data(elem, type, (jQuery.data(elem, type, undefined, true) || 0) + 1, true);
            }
        },

        _unmark: function (force, elem, type) {
            if (force !== true) {
                type = elem;
                elem = force;
                force = false;
            }
            if (elem) {
                type = type || "fx";
                var key = type + "mark",
				count = force ? 0 : ((jQuery.data(elem, key, undefined, true) || 1) - 1);
                if (count) {
                    jQuery.data(elem, key, count, true);
                } else {
                    jQuery.removeData(elem, key, true);
                    handleQueueMarkDefer(elem, type, "mark");
                }
            }
        },

        queue: function (elem, type, data) {
            if (elem) {
                type = (type || "fx") + "queue";
                var q = jQuery.data(elem, type, undefined, true);
                // Speed up dequeue by getting out quickly if this is just a lookup
                if (data) {
                    if (!q || jQuery.isArray(data)) {
                        q = jQuery.data(elem, type, jQuery.makeArray(data), true);
                    } else {
                        q.push(data);
                    }
                }
                return q || [];
            }
        },

        dequeue: function (elem, type) {
            type = type || "fx";

            var queue = jQuery.queue(elem, type),
			fn = queue.shift(),
			defer;

            // If the fx queue is dequeued, always remove the progress sentinel
            if (fn === "inprogress") {
                fn = queue.shift();
            }

            if (fn) {
                // Add a progress sentinel to prevent the fx queue from being
                // automatically dequeued
                if (type === "fx") {
                    queue.unshift("inprogress");
                }

                fn.call(elem, function () {
                    jQuery.dequeue(elem, type);
                });
            }

            if (!queue.length) {
                jQuery.removeData(elem, type + "queue", true);
                handleQueueMarkDefer(elem, type, "queue");
            }
        }
    });

    jQuery.fn.extend({
        queue: function (type, data) {
            if (typeof type !== "string") {
                data = type;
                type = "fx";
            }

            if (data === undefined) {
                return jQuery.queue(this[0], type);
            }
            return this.each(function () {
                var queue = jQuery.queue(this, type, data);

                if (type === "fx" && queue[0] !== "inprogress") {
                    jQuery.dequeue(this, type);
                }
            });
        },
        dequeue: function (type) {
            return this.each(function () {
                jQuery.dequeue(this, type);
            });
        },
        // Based off of the plugin by Clint Helfers, with permission.
        // http://blindsignals.com/index.php/2009/07/jquery-delay/
        delay: function (time, type) {
            time = jQuery.fx ? jQuery.fx.speeds[time] || time : time;
            type = type || "fx";

            return this.queue(type, function () {
                var elem = this;
                setTimeout(function () {
                    jQuery.dequeue(elem, type);
                }, time);
            });
        },
        clearQueue: function (type) {
            return this.queue(type || "fx", []);
        },
        // Get a promise resolved when queues of a certain type
        // are emptied (fx is the type by default)
        promise: function (type, object) {
            if (typeof type !== "string") {
                object = type;
                type = undefined;
            }
            type = type || "fx";
            var defer = jQuery.Deferred(),
			elements = this,
			i = elements.length,
			count = 1,
			deferDataKey = type + "defer",
			queueDataKey = type + "queue",
			markDataKey = type + "mark",
			tmp;
            function resolve() {
                if (!(--count)) {
                    defer.resolveWith(elements, [elements]);
                }
            }
            while (i--) {
                if ((tmp = jQuery.data(elements[i], deferDataKey, undefined, true) ||
					(jQuery.data(elements[i], queueDataKey, undefined, true) ||
						jQuery.data(elements[i], markDataKey, undefined, true)) &&
					jQuery.data(elements[i], deferDataKey, jQuery._Deferred(), true))) {
                    count++;
                    tmp.done(resolve);
                }
            }
            resolve();
            return defer.promise();
        }
    });




    var rclass = /[\n\t\r]/g,
	rspace = /\s+/,
	rreturn = /\r/g,
	rtype = /^(?:button|input)$/i,
	rfocusable = /^(?:button|input|object|select|textarea)$/i,
	rclickable = /^a(?:rea)?$/i,
	rboolean = /^(?:autofocus|autoplay|async|checked|controls|defer|disabled|hidden|loop|multiple|open|readonly|required|scoped|selected)$/i,
	rinvalidChar = /\:|^on/,
	formHook, boolHook;

    jQuery.fn.extend({
        attr: function (name, value) {
            return jQuery.access(this, name, value, true, jQuery.attr);
        },

        removeAttr: function (name) {
            return this.each(function () {
                jQuery.removeAttr(this, name);
            });
        },

        prop: function (name, value) {
            return jQuery.access(this, name, value, true, jQuery.prop);
        },

        removeProp: function (name) {
            name = jQuery.propFix[name] || name;
            return this.each(function () {
                // try/catch handles cases where IE balks (such as removing a property on window)
                try {
                    this[name] = undefined;
                    delete this[name];
                } catch (e) { }
            });
        },

        addClass: function (value) {
            var classNames, i, l, elem,
			setClass, c, cl;

            if (jQuery.isFunction(value)) {
                return this.each(function (j) {
                    jQuery(this).addClass(value.call(this, j, this.className));
                });
            }

            if (value && typeof value === "string") {
                classNames = value.split(rspace);

                for (i = 0, l = this.length; i < l; i++) {
                    elem = this[i];

                    if (elem.nodeType === 1) {
                        if (!elem.className && classNames.length === 1) {
                            elem.className = value;

                        } else {
                            setClass = " " + elem.className + " ";

                            for (c = 0, cl = classNames.length; c < cl; c++) {
                                if (! ~setClass.indexOf(" " + classNames[c] + " ")) {
                                    setClass += classNames[c] + " ";
                                }
                            }
                            elem.className = jQuery.trim(setClass);
                        }
                    }
                }
            }

            return this;
        },

        removeClass: function (value) {
            var classNames, i, l, elem, className, c, cl;

            if (jQuery.isFunction(value)) {
                return this.each(function (j) {
                    jQuery(this).removeClass(value.call(this, j, this.className));
                });
            }

            if ((value && typeof value === "string") || value === undefined) {
                classNames = (value || "").split(rspace);

                for (i = 0, l = this.length; i < l; i++) {
                    elem = this[i];

                    if (elem.nodeType === 1 && elem.className) {
                        if (value) {
                            className = (" " + elem.className + " ").replace(rclass, " ");
                            for (c = 0, cl = classNames.length; c < cl; c++) {
                                className = className.replace(" " + classNames[c] + " ", " ");
                            }
                            elem.className = jQuery.trim(className);

                        } else {
                            elem.className = "";
                        }
                    }
                }
            }

            return this;
        },

        toggleClass: function (value, stateVal) {
            var type = typeof value,
			isBool = typeof stateVal === "boolean";

            if (jQuery.isFunction(value)) {
                return this.each(function (i) {
                    jQuery(this).toggleClass(value.call(this, i, this.className, stateVal), stateVal);
                });
            }

            return this.each(function () {
                if (type === "string") {
                    // toggle individual class names
                    var className,
					i = 0,
					self = jQuery(this),
					state = stateVal,
					classNames = value.split(rspace);

                    while ((className = classNames[i++])) {
                        // check each className given, space seperated list
                        state = isBool ? state : !self.hasClass(className);
                        self[state ? "addClass" : "removeClass"](className);
                    }

                } else if (type === "undefined" || type === "boolean") {
                    if (this.className) {
                        // store className if set
                        jQuery._data(this, "__className__", this.className);
                    }

                    // toggle whole className
                    this.className = this.className || value === false ? "" : jQuery._data(this, "__className__") || "";
                }
            });
        },

        hasClass: function (selector) {
            var className = " " + selector + " ";
            for (var i = 0, l = this.length; i < l; i++) {
                if ((" " + this[i].className + " ").replace(rclass, " ").indexOf(className) > -1) {
                    return true;
                }
            }

            return false;
        },

        val: function (value) {
            var hooks, ret,
			elem = this[0];

            if (!arguments.length) {
                if (elem) {
                    hooks = jQuery.valHooks[elem.nodeName.toLowerCase()] || jQuery.valHooks[elem.type];

                    if (hooks && "get" in hooks && (ret = hooks.get(elem, "value")) !== undefined) {
                        return ret;
                    }

                    ret = elem.value;

                    return typeof ret === "string" ?
                    // handle most common string cases
					ret.replace(rreturn, "") :
                    // handle cases where value is null/undef or number
					ret == null ? "" : ret;
                }

                return undefined;
            }

            var isFunction = jQuery.isFunction(value);

            return this.each(function (i) {
                var self = jQuery(this), val;

                if (this.nodeType !== 1) {
                    return;
                }

                if (isFunction) {
                    val = value.call(this, i, self.val());
                } else {
                    val = value;
                }

                // Treat null/undefined as ""; convert numbers to string
                if (val == null) {
                    val = "";
                } else if (typeof val === "number") {
                    val += "";
                } else if (jQuery.isArray(val)) {
                    val = jQuery.map(val, function (value) {
                        return value == null ? "" : value + "";
                    });
                }

                hooks = jQuery.valHooks[this.nodeName.toLowerCase()] || jQuery.valHooks[this.type];

                // If set returns undefined, fall back to normal setting
                if (!hooks || !("set" in hooks) || hooks.set(this, val, "value") === undefined) {
                    this.value = val;
                }
            });
        }
    });

    jQuery.extend({
        valHooks: {
            option: {
                get: function (elem) {
                    // attributes.value is undefined in Blackberry 4.7 but
                    // uses .value. See #6932
                    var val = elem.attributes.value;
                    return !val || val.specified ? elem.value : elem.text;
                }
            },
            select: {
                get: function (elem) {
                    var value,
					index = elem.selectedIndex,
					values = [],
					options = elem.options,
					one = elem.type === "select-one";

                    // Nothing was selected
                    if (index < 0) {
                        return null;
                    }

                    // Loop through all the selected options
                    for (var i = one ? index : 0, max = one ? index + 1 : options.length; i < max; i++) {
                        var option = options[i];

                        // Don't return options that are disabled or in a disabled optgroup
                        if (option.selected && (jQuery.support.optDisabled ? !option.disabled : option.getAttribute("disabled") === null) &&
							(!option.parentNode.disabled || !jQuery.nodeName(option.parentNode, "optgroup"))) {

                            // Get the specific value for the option
                            value = jQuery(option).val();

                            // We don't need an array for one selects
                            if (one) {
                                return value;
                            }

                            // Multi-Selects return an array
                            values.push(value);
                        }
                    }

                    // Fixes Bug #2551 -- select.val() broken in IE after form.reset()
                    if (one && !values.length && options.length) {
                        return jQuery(options[index]).val();
                    }

                    return values;
                },

                set: function (elem, value) {
                    var values = jQuery.makeArray(value);

                    jQuery(elem).find("option").each(function () {
                        this.selected = jQuery.inArray(jQuery(this).val(), values) >= 0;
                    });

                    if (!values.length) {
                        elem.selectedIndex = -1;
                    }
                    return values;
                }
            }
        },

        attrFn: {
            val: true,
            css: true,
            html: true,
            text: true,
            data: true,
            width: true,
            height: true,
            offset: true
        },

        attrFix: {
            // Always normalize to ensure hook usage
            tabindex: "tabIndex"
        },

        attr: function (elem, name, value, pass) {
            var nType = elem.nodeType;

            // don't get/set attributes on text, comment and attribute nodes
            if (!elem || nType === 3 || nType === 8 || nType === 2) {
                return undefined;
            }

            if (pass && name in jQuery.attrFn) {
                return jQuery(elem)[name](value);
            }

            // Fallback to prop when attributes are not supported
            if (!("getAttribute" in elem)) {
                return jQuery.prop(elem, name, value);
            }

            var ret, hooks,
			notxml = nType !== 1 || !jQuery.isXMLDoc(elem);

            // Normalize the name if needed
            if (notxml) {
                name = jQuery.attrFix[name] || name;

                hooks = jQuery.attrHooks[name];

                if (!hooks) {
                    // Use boolHook for boolean attributes
                    if (rboolean.test(name)) {

                        hooks = boolHook;

                        // Use formHook for forms and if the name contains certain characters
                    } else if (formHook && name !== "className" &&
					(jQuery.nodeName(elem, "form") || rinvalidChar.test(name))) {

                        hooks = formHook;
                    }
                }
            }

            if (value !== undefined) {

                if (value === null) {
                    jQuery.removeAttr(elem, name);
                    return undefined;

                } else if (hooks && "set" in hooks && notxml && (ret = hooks.set(elem, value, name)) !== undefined) {
                    return ret;

                } else {
                    elem.setAttribute(name, "" + value);
                    return value;
                }

            } else if (hooks && "get" in hooks && notxml && (ret = hooks.get(elem, name)) !== null) {
                return ret;

            } else {

                ret = elem.getAttribute(name);

                // Non-existent attributes return null, we normalize to undefined
                return ret === null ?
				undefined :
				ret;
            }
        },

        removeAttr: function (elem, name) {
            var propName;
            if (elem.nodeType === 1) {
                name = jQuery.attrFix[name] || name;

                if (jQuery.support.getSetAttribute) {
                    // Use removeAttribute in browsers that support it
                    elem.removeAttribute(name);
                } else {
                    jQuery.attr(elem, name, "");
                    elem.removeAttributeNode(elem.getAttributeNode(name));
                }

                // Set corresponding property to false for boolean attributes
                if (rboolean.test(name) && (propName = jQuery.propFix[name] || name) in elem) {
                    elem[propName] = false;
                }
            }
        },

        attrHooks: {
            type: {
                set: function (elem, value) {
                    // We can't allow the type property to be changed (since it causes problems in IE)
                    if (rtype.test(elem.nodeName) && elem.parentNode) {
                        jQuery.error("type property can't be changed");
                    } else if (!jQuery.support.radioValue && value === "radio" && jQuery.nodeName(elem, "input")) {
                        // Setting the type on a radio button after the value resets the value in IE6-9
                        // Reset value to it's default in case type is set after value
                        // This is for element creation
                        var val = elem.value;
                        elem.setAttribute("type", value);
                        if (val) {
                            elem.value = val;
                        }
                        return value;
                    }
                }
            },
            tabIndex: {
                get: function (elem) {
                    // elem.tabIndex doesn't always return the correct value when it hasn't been explicitly set
                    // http://fluidproject.org/blog/2008/01/09/getting-setting-and-removing-tabindex-values-with-javascript/
                    var attributeNode = elem.getAttributeNode("tabIndex");

                    return attributeNode && attributeNode.specified ?
					parseInt(attributeNode.value, 10) :
					rfocusable.test(elem.nodeName) || rclickable.test(elem.nodeName) && elem.href ?
						0 :
						undefined;
                }
            },
            // Use the value property for back compat
            // Use the formHook for button elements in IE6/7 (#1954)
            value: {
                get: function (elem, name) {
                    if (formHook && jQuery.nodeName(elem, "button")) {
                        return formHook.get(elem, name);
                    }
                    return name in elem ?
					elem.value :
					null;
                },
                set: function (elem, value, name) {
                    if (formHook && jQuery.nodeName(elem, "button")) {
                        return formHook.set(elem, value, name);
                    }
                    // Does not return so that setAttribute is also used
                    elem.value = value;
                }
            }
        },

        propFix: {
            tabindex: "tabIndex",
            readonly: "readOnly",
            "for": "htmlFor",
            "class": "className",
            maxlength: "maxLength",
            cellspacing: "cellSpacing",
            cellpadding: "cellPadding",
            rowspan: "rowSpan",
            colspan: "colSpan",
            usemap: "useMap",
            frameborder: "frameBorder",
            contenteditable: "contentEditable"
        },

        prop: function (elem, name, value) {
            var nType = elem.nodeType;

            // don't get/set properties on text, comment and attribute nodes
            if (!elem || nType === 3 || nType === 8 || nType === 2) {
                return undefined;
            }

            var ret, hooks,
			notxml = nType !== 1 || !jQuery.isXMLDoc(elem);

            if (notxml) {
                // Fix name and attach hooks
                name = jQuery.propFix[name] || name;
                hooks = jQuery.propHooks[name];
            }

            if (value !== undefined) {
                if (hooks && "set" in hooks && (ret = hooks.set(elem, value, name)) !== undefined) {
                    return ret;

                } else {
                    return (elem[name] = value);
                }

            } else {
                if (hooks && "get" in hooks && (ret = hooks.get(elem, name)) !== undefined) {
                    return ret;

                } else {
                    return elem[name];
                }
            }
        },

        propHooks: {}
    });

    // Hook for boolean attributes
    boolHook = {
        get: function (elem, name) {
            // Align boolean attributes with corresponding properties
            return jQuery.prop(elem, name) ?
			name.toLowerCase() :
			undefined;
        },
        set: function (elem, value, name) {
            var propName;
            if (value === false) {
                // Remove boolean attributes when set to false
                jQuery.removeAttr(elem, name);
            } else {
                // value is true since we know at this point it's type boolean and not false
                // Set boolean attributes to the same name and set the DOM property
                propName = jQuery.propFix[name] || name;
                if (propName in elem) {
                    // Only set the IDL specifically if it already exists on the element
                    elem[propName] = true;
                }

                elem.setAttribute(name, name.toLowerCase());
            }
            return name;
        }
    };

    // IE6/7 do not support getting/setting some attributes with get/setAttribute
    if (!jQuery.support.getSetAttribute) {

        // propFix is more comprehensive and contains all fixes
        jQuery.attrFix = jQuery.propFix;

        // Use this for any attribute on a form in IE6/7
        formHook = jQuery.attrHooks.name = jQuery.attrHooks.title = jQuery.valHooks.button = {
            get: function (elem, name) {
                var ret;
                ret = elem.getAttributeNode(name);
                // Return undefined if nodeValue is empty string
                return ret && ret.nodeValue !== "" ?
				ret.nodeValue :
				undefined;
            },
            set: function (elem, value, name) {
                // Check form objects in IE (multiple bugs related)
                // Only use nodeValue if the attribute node exists on the form
                var ret = elem.getAttributeNode(name);
                if (ret) {
                    ret.nodeValue = value;
                    return value;
                }
            }
        };

        // Set width and height to auto instead of 0 on empty string( Bug #8150 )
        // This is for removals
        jQuery.each(["width", "height"], function (i, name) {
            jQuery.attrHooks[name] = jQuery.extend(jQuery.attrHooks[name], {
                set: function (elem, value) {
                    if (value === "") {
                        elem.setAttribute(name, "auto");
                        return value;
                    }
                }
            });
        });
    }


    // Some attributes require a special call on IE
    if (!jQuery.support.hrefNormalized) {
        jQuery.each(["href", "src", "width", "height"], function (i, name) {
            jQuery.attrHooks[name] = jQuery.extend(jQuery.attrHooks[name], {
                get: function (elem) {
                    var ret = elem.getAttribute(name, 2);
                    return ret === null ? undefined : ret;
                }
            });
        });
    }

    if (!jQuery.support.style) {
        jQuery.attrHooks.style = {
            get: function (elem) {
                // Return undefined in the case of empty string
                // Normalize to lowercase since IE uppercases css property names
                return elem.style.cssText.toLowerCase() || undefined;
            },
            set: function (elem, value) {
                return (elem.style.cssText = "" + value);
            }
        };
    }

    // Safari mis-reports the default selected property of an option
    // Accessing the parent's selectedIndex property fixes it
    if (!jQuery.support.optSelected) {
        jQuery.propHooks.selected = jQuery.extend(jQuery.propHooks.selected, {
            get: function (elem) {
                var parent = elem.parentNode;

                if (parent) {
                    parent.selectedIndex;

                    // Make sure that it also works with optgroups, see #5701
                    if (parent.parentNode) {
                        parent.parentNode.selectedIndex;
                    }
                }
            }
        });
    }

    // Radios and checkboxes getter/setter
    if (!jQuery.support.checkOn) {
        jQuery.each(["radio", "checkbox"], function () {
            jQuery.valHooks[this] = {
                get: function (elem) {
                    // Handle the case where in Webkit "" is returned instead of "on" if a value isn't specified
                    return elem.getAttribute("value") === null ? "on" : elem.value;
                }
            };
        });
    }
    jQuery.each(["radio", "checkbox"], function () {
        jQuery.valHooks[this] = jQuery.extend(jQuery.valHooks[this], {
            set: function (elem, value) {
                if (jQuery.isArray(value)) {
                    return (elem.checked = jQuery.inArray(jQuery(elem).val(), value) >= 0);
                }
            }
        });
    });




    var rnamespaces = /\.(.*)$/,
	rformElems = /^(?:textarea|input|select)$/i,
	rperiod = /\./g,
	rspaces = / /g,
	rescape = /[^\w\s.|`]/g,
	fcleanup = function (nm) {
	    return nm.replace(rescape, "\\$&");
	};

    /*
    * A number of helper functions used for managing events.
    * Many of the ideas behind this code originated from
    * Dean Edwards' addEvent library.
    */
    jQuery.event = {

        // Bind an event to an element
        // Original by Dean Edwards
        add: function (elem, types, handler, data) {
            if (elem.nodeType === 3 || elem.nodeType === 8) {
                return;
            }

            if (handler === false) {
                handler = returnFalse;
            } else if (!handler) {
                // Fixes bug #7229. Fix recommended by jdalton
                return;
            }

            var handleObjIn, handleObj;

            if (handler.handler) {
                handleObjIn = handler;
                handler = handleObjIn.handler;
            }

            // Make sure that the function being executed has a unique ID
            if (!handler.guid) {
                handler.guid = jQuery.guid++;
            }

            // Init the element's event structure
            var elemData = jQuery._data(elem);

            // If no elemData is found then we must be trying to bind to one of the
            // banned noData elements
            if (!elemData) {
                return;
            }

            var events = elemData.events,
			eventHandle = elemData.handle;

            if (!events) {
                elemData.events = events = {};
            }

            if (!eventHandle) {
                elemData.handle = eventHandle = function (e) {
                    // Discard the second event of a jQuery.event.trigger() and
                    // when an event is called after a page has unloaded
                    return typeof jQuery !== "undefined" && (!e || jQuery.event.triggered !== e.type) ?
					jQuery.event.handle.apply(eventHandle.elem, arguments) :
					undefined;
                };
            }

            // Add elem as a property of the handle function
            // This is to prevent a memory leak with non-native events in IE.
            eventHandle.elem = elem;

            // Handle multiple events separated by a space
            // jQuery(...).bind("mouseover mouseout", fn);
            types = types.split(" ");

            var type, i = 0, namespaces;

            while ((type = types[i++])) {
                handleObj = handleObjIn ?
				jQuery.extend({}, handleObjIn) :
				{ handler: handler, data: data };

                // Namespaced event handlers
                if (type.indexOf(".") > -1) {
                    namespaces = type.split(".");
                    type = namespaces.shift();
                    handleObj.namespace = namespaces.slice(0).sort().join(".");

                } else {
                    namespaces = [];
                    handleObj.namespace = "";
                }

                handleObj.type = type;
                if (!handleObj.guid) {
                    handleObj.guid = handler.guid;
                }

                // Get the current list of functions bound to this event
                var handlers = events[type],
				special = jQuery.event.special[type] || {};

                // Init the event handler queue
                if (!handlers) {
                    handlers = events[type] = [];

                    // Check for a special event handler
                    // Only use addEventListener/attachEvent if the special
                    // events handler returns false
                    if (!special.setup || special.setup.call(elem, data, namespaces, eventHandle) === false) {
                        // Bind the global event handler to the element
                        if (elem.addEventListener) {
                            elem.addEventListener(type, eventHandle, false);

                        } else if (elem.attachEvent) {
                            elem.attachEvent("on" + type, eventHandle);
                        }
                    }
                }

                if (special.add) {
                    special.add.call(elem, handleObj);

                    if (!handleObj.handler.guid) {
                        handleObj.handler.guid = handler.guid;
                    }
                }

                // Add the function to the element's handler list
                handlers.push(handleObj);

                // Keep track of which events have been used, for event optimization
                jQuery.event.global[type] = true;
            }

            // Nullify elem to prevent memory leaks in IE
            elem = null;
        },

        global: {},

        // Detach an event or set of events from an element
        remove: function (elem, types, handler, pos) {
            // don't do events on text and comment nodes
            if (elem.nodeType === 3 || elem.nodeType === 8) {
                return;
            }

            if (handler === false) {
                handler = returnFalse;
            }

            var ret, type, fn, j, i = 0, all, namespaces, namespace, special, eventType, handleObj, origType,
			elemData = jQuery.hasData(elem) && jQuery._data(elem),
			events = elemData && elemData.events;

            if (!elemData || !events) {
                return;
            }

            // types is actually an event object here
            if (types && types.type) {
                handler = types.handler;
                types = types.type;
            }

            // Unbind all events for the element
            if (!types || typeof types === "string" && types.charAt(0) === ".") {
                types = types || "";

                for (type in events) {
                    jQuery.event.remove(elem, type + types);
                }

                return;
            }

            // Handle multiple events separated by a space
            // jQuery(...).unbind("mouseover mouseout", fn);
            types = types.split(" ");

            while ((type = types[i++])) {
                origType = type;
                handleObj = null;
                all = type.indexOf(".") < 0;
                namespaces = [];

                if (!all) {
                    // Namespaced event handlers
                    namespaces = type.split(".");
                    type = namespaces.shift();

                    namespace = new RegExp("(^|\\.)" +
					jQuery.map(namespaces.slice(0).sort(), fcleanup).join("\\.(?:.*\\.)?") + "(\\.|$)");
                }

                eventType = events[type];

                if (!eventType) {
                    continue;
                }

                if (!handler) {
                    for (j = 0; j < eventType.length; j++) {
                        handleObj = eventType[j];

                        if (all || namespace.test(handleObj.namespace)) {
                            jQuery.event.remove(elem, origType, handleObj.handler, j);
                            eventType.splice(j--, 1);
                        }
                    }

                    continue;
                }

                special = jQuery.event.special[type] || {};

                for (j = pos || 0; j < eventType.length; j++) {
                    handleObj = eventType[j];

                    if (handler.guid === handleObj.guid) {
                        // remove the given handler for the given type
                        if (all || namespace.test(handleObj.namespace)) {
                            if (pos == null) {
                                eventType.splice(j--, 1);
                            }

                            if (special.remove) {
                                special.remove.call(elem, handleObj);
                            }
                        }

                        if (pos != null) {
                            break;
                        }
                    }
                }

                // remove generic event handler if no more handlers exist
                if (eventType.length === 0 || pos != null && eventType.length === 1) {
                    if (!special.teardown || special.teardown.call(elem, namespaces) === false) {
                        jQuery.removeEvent(elem, type, elemData.handle);
                    }

                    ret = null;
                    delete events[type];
                }
            }

            // Remove the expando if it's no longer used
            if (jQuery.isEmptyObject(events)) {
                var handle = elemData.handle;
                if (handle) {
                    handle.elem = null;
                }

                delete elemData.events;
                delete elemData.handle;

                if (jQuery.isEmptyObject(elemData)) {
                    jQuery.removeData(elem, undefined, true);
                }
            }
        },

        // Events that are safe to short-circuit if no handlers are attached.
        // Native DOM events should not be added, they may have inline handlers.
        customEvent: {
            "getData": true,
            "setData": true,
            "changeData": true
        },

        trigger: function (event, data, elem, onlyHandlers) {
            // Event object or event type
            var type = event.type || event,
			namespaces = [],
			exclusive;

            if (type.indexOf("!") >= 0) {
                // Exclusive events trigger only for the exact event (no namespaces)
                type = type.slice(0, -1);
                exclusive = true;
            }

            if (type.indexOf(".") >= 0) {
                // Namespaced trigger; create a regexp to match event type in handle()
                namespaces = type.split(".");
                type = namespaces.shift();
                namespaces.sort();
            }

            if ((!elem || jQuery.event.customEvent[type]) && !jQuery.event.global[type]) {
                // No jQuery handlers for this event type, and it can't have inline handlers
                return;
            }

            // Caller can pass in an Event, Object, or just an event type string
            event = typeof event === "object" ?
            // jQuery.Event object
			event[jQuery.expando] ? event :
            // Object literal
			new jQuery.Event(type, event) :
            // Just the event type (string)
			new jQuery.Event(type);

            event.type = type;
            event.exclusive = exclusive;
            event.namespace = namespaces.join(".");
            event.namespace_re = new RegExp("(^|\\.)" + namespaces.join("\\.(?:.*\\.)?") + "(\\.|$)");

            // triggerHandler() and global events don't bubble or run the default action
            if (onlyHandlers || !elem) {
                event.preventDefault();
                event.stopPropagation();
            }

            // Handle a global trigger
            if (!elem) {
                // TODO: Stop taunting the data cache; remove global events and always attach to document
                jQuery.each(jQuery.cache, function () {
                    // internalKey variable is just used to make it easier to find
                    // and potentially change this stuff later; currently it just
                    // points to jQuery.expando
                    var internalKey = jQuery.expando,
					internalCache = this[internalKey];
                    if (internalCache && internalCache.events && internalCache.events[type]) {
                        jQuery.event.trigger(event, data, internalCache.handle.elem);
                    }
                });
                return;
            }

            // Don't do events on text and comment nodes
            if (elem.nodeType === 3 || elem.nodeType === 8) {
                return;
            }

            // Clean up the event in case it is being reused
            event.result = undefined;
            event.target = elem;

            // Clone any incoming data and prepend the event, creating the handler arg list
            data = data != null ? jQuery.makeArray(data) : [];
            data.unshift(event);

            var cur = elem,
            // IE doesn't like method names with a colon (#3533, #8272)
			ontype = type.indexOf(":") < 0 ? "on" + type : "";

            // Fire event on the current element, then bubble up the DOM tree
            do {
                var handle = jQuery._data(cur, "handle");

                event.currentTarget = cur;
                if (handle) {
                    handle.apply(cur, data);
                }

                // Trigger an inline bound script
                if (ontype && jQuery.acceptData(cur) && cur[ontype] && cur[ontype].apply(cur, data) === false) {
                    event.result = false;
                    event.preventDefault();
                }

                // Bubble up to document, then to window
                cur = cur.parentNode || cur.ownerDocument || cur === event.target.ownerDocument && window;
            } while (cur && !event.isPropagationStopped());

            // If nobody prevented the default action, do it now
            if (!event.isDefaultPrevented()) {
                var old,
				special = jQuery.event.special[type] || {};

                if ((!special._default || special._default.call(elem.ownerDocument, event) === false) &&
				!(type === "click" && jQuery.nodeName(elem, "a")) && jQuery.acceptData(elem)) {

                    // Call a native DOM method on the target with the same name name as the event.
                    // Can't use an .isFunction)() check here because IE6/7 fails that test.
                    // IE<9 dies on focus to hidden element (#1486), may want to revisit a try/catch.
                    try {
                        if (ontype && elem[type]) {
                            // Don't re-trigger an onFOO event when we call its FOO() method
                            old = elem[ontype];

                            if (old) {
                                elem[ontype] = null;
                            }

                            jQuery.event.triggered = type;
                            elem[type]();
                        }
                    } catch (ieError) { }

                    if (old) {
                        elem[ontype] = old;
                    }

                    jQuery.event.triggered = undefined;
                }
            }

            return event.result;
        },

        handle: function (event) {
            event = jQuery.event.fix(event || window.event);
            // Snapshot the handlers list since a called handler may add/remove events.
            var handlers = ((jQuery._data(this, "events") || {})[event.type] || []).slice(0),
			run_all = !event.exclusive && !event.namespace,
			args = Array.prototype.slice.call(arguments, 0);

            // Use the fix-ed Event rather than the (read-only) native event
            args[0] = event;
            event.currentTarget = this;

            for (var j = 0, l = handlers.length; j < l; j++) {
                var handleObj = handlers[j];

                // Triggered event must 1) be non-exclusive and have no namespace, or
                // 2) have namespace(s) a subset or equal to those in the bound event.
                if (run_all || event.namespace_re.test(handleObj.namespace)) {
                    // Pass in a reference to the handler function itself
                    // So that we can later remove it
                    event.handler = handleObj.handler;
                    event.data = handleObj.data;
                    event.handleObj = handleObj;

                    var ret = handleObj.handler.apply(this, args);

                    if (ret !== undefined) {
                        event.result = ret;
                        if (ret === false) {
                            event.preventDefault();
                            event.stopPropagation();
                        }
                    }

                    if (event.isImmediatePropagationStopped()) {
                        break;
                    }
                }
            }
            return event.result;
        },

        props: "altKey attrChange attrName bubbles button cancelable charCode clientX clientY ctrlKey currentTarget data detail eventPhase fromElement handler keyCode layerX layerY metaKey newValue offsetX offsetY pageX pageY prevValue relatedNode relatedTarget screenX screenY shiftKey srcElement target toElement view wheelDelta which".split(" "),

        fix: function (event) {
            if (event[jQuery.expando]) {
                return event;
            }

            // store a copy of the original event object
            // and "clone" to set read-only properties
            var originalEvent = event;
            event = jQuery.Event(originalEvent);

            for (var i = this.props.length, prop; i; ) {
                prop = this.props[--i];
                event[prop] = originalEvent[prop];
            }

            // Fix target property, if necessary
            if (!event.target) {
                // Fixes #1925 where srcElement might not be defined either
                event.target = event.srcElement || document;
            }

            // check if target is a textnode (safari)
            if (event.target.nodeType === 3) {
                event.target = event.target.parentNode;
            }

            // Add relatedTarget, if necessary
            if (!event.relatedTarget && event.fromElement) {
                event.relatedTarget = event.fromElement === event.target ? event.toElement : event.fromElement;
            }

            // Calculate pageX/Y if missing and clientX/Y available
            if (event.pageX == null && event.clientX != null) {
                var eventDocument = event.target.ownerDocument || document,
				doc = eventDocument.documentElement,
				body = eventDocument.body;

                event.pageX = event.clientX + (doc && doc.scrollLeft || body && body.scrollLeft || 0) - (doc && doc.clientLeft || body && body.clientLeft || 0);
                event.pageY = event.clientY + (doc && doc.scrollTop || body && body.scrollTop || 0) - (doc && doc.clientTop || body && body.clientTop || 0);
            }

            // Add which for key events
            if (event.which == null && (event.charCode != null || event.keyCode != null)) {
                event.which = event.charCode != null ? event.charCode : event.keyCode;
            }

            // Add metaKey to non-Mac browsers (use ctrl for PC's and Meta for Macs)
            if (!event.metaKey && event.ctrlKey) {
                event.metaKey = event.ctrlKey;
            }

            // Add which for click: 1 === left; 2 === middle; 3 === right
            // Note: button is not normalized, so don't use it
            if (!event.which && event.button !== undefined) {
                event.which = (event.button & 1 ? 1 : (event.button & 2 ? 3 : (event.button & 4 ? 2 : 0)));
            }

            return event;
        },

        // Deprecated, use jQuery.guid instead
        guid: 1E8,

        // Deprecated, use jQuery.proxy instead
        proxy: jQuery.proxy,

        special: {
            ready: {
                // Make sure the ready event is setup
                setup: jQuery.bindReady,
                teardown: jQuery.noop
            },

            live: {
                add: function (handleObj) {
                    jQuery.event.add(this,
					liveConvert(handleObj.origType, handleObj.selector),
					jQuery.extend({}, handleObj, { handler: liveHandler, guid: handleObj.handler.guid }));
                },

                remove: function (handleObj) {
                    jQuery.event.remove(this, liveConvert(handleObj.origType, handleObj.selector), handleObj);
                }
            },

            beforeunload: {
                setup: function (data, namespaces, eventHandle) {
                    // We only want to do this special case on windows
                    if (jQuery.isWindow(this)) {
                        this.onbeforeunload = eventHandle;
                    }
                },

                teardown: function (namespaces, eventHandle) {
                    if (this.onbeforeunload === eventHandle) {
                        this.onbeforeunload = null;
                    }
                }
            }
        }
    };

    jQuery.removeEvent = document.removeEventListener ?
	function (elem, type, handle) {
	    if (elem.removeEventListener) {
	        elem.removeEventListener(type, handle, false);
	    }
	} :
	function (elem, type, handle) {
	    if (elem.detachEvent) {
	        elem.detachEvent("on" + type, handle);
	    }
	};

    jQuery.Event = function (src, props) {
        // Allow instantiation without the 'new' keyword
        if (!this.preventDefault) {
            return new jQuery.Event(src, props);
        }

        // Event object
        if (src && src.type) {
            this.originalEvent = src;
            this.type = src.type;

            // Events bubbling up the document may have been marked as prevented
            // by a handler lower down the tree; reflect the correct value.
            this.isDefaultPrevented = (src.defaultPrevented || src.returnValue === false ||
			src.getPreventDefault && src.getPreventDefault()) ? returnTrue : returnFalse;

            // Event type
        } else {
            this.type = src;
        }

        // Put explicitly provided properties onto the event object
        if (props) {
            jQuery.extend(this, props);
        }

        // timeStamp is buggy for some events on Firefox(#3843)
        // So we won't rely on the native value
        this.timeStamp = jQuery.now();

        // Mark it as fixed
        this[jQuery.expando] = true;
    };

    function returnFalse() {
        return false;
    }
    function returnTrue() {
        return true;
    }

    // jQuery.Event is based on DOM3 Events as specified by the ECMAScript Language Binding
    // http://www.w3.org/TR/2003/WD-DOM-Level-3-Events-20030331/ecma-script-binding.html
    jQuery.Event.prototype = {
        preventDefault: function () {
            this.isDefaultPrevented = returnTrue;

            var e = this.originalEvent;
            if (!e) {
                return;
            }

            // if preventDefault exists run it on the original event
            if (e.preventDefault) {
                e.preventDefault();

                // otherwise set the returnValue property of the original event to false (IE)
            } else {
                e.returnValue = false;
            }
        },
        stopPropagation: function () {
            this.isPropagationStopped = returnTrue;

            var e = this.originalEvent;
            if (!e) {
                return;
            }
            // if stopPropagation exists run it on the original event
            if (e.stopPropagation) {
                e.stopPropagation();
            }
            // otherwise set the cancelBubble property of the original event to true (IE)
            e.cancelBubble = true;
        },
        stopImmediatePropagation: function () {
            this.isImmediatePropagationStopped = returnTrue;
            this.stopPropagation();
        },
        isDefaultPrevented: returnFalse,
        isPropagationStopped: returnFalse,
        isImmediatePropagationStopped: returnFalse
    };

    // Checks if an event happened on an element within another element
    // Used in jQuery.event.special.mouseenter and mouseleave handlers
    var withinElement = function (event) {

        // Check if mouse(over|out) are still within the same parent element
        var related = event.relatedTarget,
		inside = false,
		eventType = event.type;

        event.type = event.data;

        if (related !== this) {

            if (related) {
                inside = jQuery.contains(this, related);
            }

            if (!inside) {

                jQuery.event.handle.apply(this, arguments);

                event.type = eventType;
            }
        }
    },

    // In case of event delegation, we only need to rename the event.type,
    // liveHandler will take care of the rest.
delegate = function (event) {
    event.type = event.data;
    jQuery.event.handle.apply(this, arguments);
};

    // Create mouseenter and mouseleave events
    jQuery.each({
        mouseenter: "mouseover",
        mouseleave: "mouseout"
    }, function (orig, fix) {
        jQuery.event.special[orig] = {
            setup: function (data) {
                jQuery.event.add(this, fix, data && data.selector ? delegate : withinElement, orig);
            },
            teardown: function (data) {
                jQuery.event.remove(this, fix, data && data.selector ? delegate : withinElement);
            }
        };
    });

    // submit delegation
    if (!jQuery.support.submitBubbles) {

        jQuery.event.special.submit = {
            setup: function (data, namespaces) {
                if (!jQuery.nodeName(this, "form")) {
                    jQuery.event.add(this, "click.specialSubmit", function (e) {
                        var elem = e.target,
						type = elem.type;

                        if ((type === "submit" || type === "image") && jQuery(elem).closest("form").length) {
                            trigger("submit", this, arguments);
                        }
                    });

                    jQuery.event.add(this, "keypress.specialSubmit", function (e) {
                        var elem = e.target,
						type = elem.type;

                        if ((type === "text" || type === "password") && jQuery(elem).closest("form").length && e.keyCode === 13) {
                            trigger("submit", this, arguments);
                        }
                    });

                } else {
                    return false;
                }
            },

            teardown: function (namespaces) {
                jQuery.event.remove(this, ".specialSubmit");
            }
        };

    }

    // change delegation, happens here so we have bind.
    if (!jQuery.support.changeBubbles) {

        var changeFilters,

	getVal = function (elem) {
	    var type = elem.type, val = elem.value;

	    if (type === "radio" || type === "checkbox") {
	        val = elem.checked;

	    } else if (type === "select-multiple") {
	        val = elem.selectedIndex > -1 ?
				jQuery.map(elem.options, function (elem) {
				    return elem.selected;
				}).join("-") :
				"";

	    } else if (jQuery.nodeName(elem, "select")) {
	        val = elem.selectedIndex;
	    }

	    return val;
	},

	testChange = function testChange(e) {
	    var elem = e.target, data, val;

	    if (!rformElems.test(elem.nodeName) || elem.readOnly) {
	        return;
	    }

	    data = jQuery._data(elem, "_change_data");
	    val = getVal(elem);

	    // the current data will be also retrieved by beforeactivate
	    if (e.type !== "focusout" || elem.type !== "radio") {
	        jQuery._data(elem, "_change_data", val);
	    }

	    if (data === undefined || val === data) {
	        return;
	    }

	    if (data != null || val) {
	        e.type = "change";
	        e.liveFired = undefined;
	        jQuery.event.trigger(e, arguments[1], elem);
	    }
	};

        jQuery.event.special.change = {
            filters: {
                focusout: testChange,

                beforedeactivate: testChange,

                click: function (e) {
                    var elem = e.target, type = jQuery.nodeName(elem, "input") ? elem.type : "";

                    if (type === "radio" || type === "checkbox" || jQuery.nodeName(elem, "select")) {
                        testChange.call(this, e);
                    }
                },

                // Change has to be called before submit
                // Keydown will be called before keypress, which is used in submit-event delegation
                keydown: function (e) {
                    var elem = e.target, type = jQuery.nodeName(elem, "input") ? elem.type : "";

                    if ((e.keyCode === 13 && !jQuery.nodeName(elem, "textarea")) ||
					(e.keyCode === 32 && (type === "checkbox" || type === "radio")) ||
					type === "select-multiple") {
                        testChange.call(this, e);
                    }
                },

                // Beforeactivate happens also before the previous element is blurred
                // with this event you can't trigger a change event, but you can store
                // information
                beforeactivate: function (e) {
                    var elem = e.target;
                    jQuery._data(elem, "_change_data", getVal(elem));
                }
            },

            setup: function (data, namespaces) {
                if (this.type === "file") {
                    return false;
                }

                for (var type in changeFilters) {
                    jQuery.event.add(this, type + ".specialChange", changeFilters[type]);
                }

                return rformElems.test(this.nodeName);
            },

            teardown: function (namespaces) {
                jQuery.event.remove(this, ".specialChange");

                return rformElems.test(this.nodeName);
            }
        };

        changeFilters = jQuery.event.special.change.filters;

        // Handle when the input is .focus()'d
        changeFilters.focus = changeFilters.beforeactivate;
    }

    function trigger(type, elem, args) {
        // Piggyback on a donor event to simulate a different one.
        // Fake originalEvent to avoid donor's stopPropagation, but if the
        // simulated event prevents default then we do the same on the donor.
        // Don't pass args or remember liveFired; they apply to the donor event.
        var event = jQuery.extend({}, args[0]);
        event.type = type;
        event.originalEvent = {};
        event.liveFired = undefined;
        jQuery.event.handle.call(elem, event);
        if (event.isDefaultPrevented()) {
            args[0].preventDefault();
        }
    }

    // Create "bubbling" focus and blur events
    if (!jQuery.support.focusinBubbles) {
        jQuery.each({ focus: "focusin", blur: "focusout" }, function (orig, fix) {

            // Attach a single capturing handler while someone wants focusin/focusout
            var attaches = 0;

            jQuery.event.special[fix] = {
                setup: function () {
                    if (attaches++ === 0) {
                        document.addEventListener(orig, handler, true);
                    }
                },
                teardown: function () {
                    if (--attaches === 0) {
                        document.removeEventListener(orig, handler, true);
                    }
                }
            };

            function handler(donor) {
                // Donor event is always a native one; fix it and switch its type.
                // Let focusin/out handler cancel the donor focus/blur event.
                var e = jQuery.event.fix(donor);
                e.type = fix;
                e.originalEvent = {};
                jQuery.event.trigger(e, null, e.target);
                if (e.isDefaultPrevented()) {
                    donor.preventDefault();
                }
            }
        });
    }

    jQuery.each(["bind", "one"], function (i, name) {
        jQuery.fn[name] = function (type, data, fn) {
            var handler;

            // Handle object literals
            if (typeof type === "object") {
                for (var key in type) {
                    this[name](key, data, type[key], fn);
                }
                return this;
            }

            if (arguments.length === 2 || data === false) {
                fn = data;
                data = undefined;
            }

            if (name === "one") {
                handler = function (event) {
                    jQuery(this).unbind(event, handler);
                    return fn.apply(this, arguments);
                };
                handler.guid = fn.guid || jQuery.guid++;
            } else {
                handler = fn;
            }

            if (type === "unload" && name !== "one") {
                this.one(type, data, fn);

            } else {
                for (var i = 0, l = this.length; i < l; i++) {
                    jQuery.event.add(this[i], type, handler, data);
                }
            }

            return this;
        };
    });

    jQuery.fn.extend({
        unbind: function (type, fn) {
            // Handle object literals
            if (typeof type === "object" && !type.preventDefault) {
                for (var key in type) {
                    this.unbind(key, type[key]);
                }

            } else {
                for (var i = 0, l = this.length; i < l; i++) {
                    jQuery.event.remove(this[i], type, fn);
                }
            }

            return this;
        },

        delegate: function (selector, types, data, fn) {
            return this.live(types, data, fn, selector);
        },

        undelegate: function (selector, types, fn) {
            if (arguments.length === 0) {
                return this.unbind("live");

            } else {
                return this.die(types, null, fn, selector);
            }
        },

        trigger: function (type, data) {
            return this.each(function () {
                jQuery.event.trigger(type, data, this);
            });
        },

        triggerHandler: function (type, data) {
            if (this[0]) {
                return jQuery.event.trigger(type, data, this[0], true);
            }
        },

        toggle: function (fn) {
            // Save reference to arguments for access in closure
            var args = arguments,
			guid = fn.guid || jQuery.guid++,
			i = 0,
			toggler = function (event) {
			    // Figure out which function to execute
			    var lastToggle = (jQuery.data(this, "lastToggle" + fn.guid) || 0) % i;
			    jQuery.data(this, "lastToggle" + fn.guid, lastToggle + 1);

			    // Make sure that clicks stop
			    event.preventDefault();

			    // and execute the function
			    return args[lastToggle].apply(this, arguments) || false;
			};

            // link all the functions, so any of them can unbind this click handler
            toggler.guid = guid;
            while (i < args.length) {
                args[i++].guid = guid;
            }

            return this.click(toggler);
        },

        hover: function (fnOver, fnOut) {
            return this.mouseenter(fnOver).mouseleave(fnOut || fnOver);
        }
    });

    var liveMap = {
        focus: "focusin",
        blur: "focusout",
        mouseenter: "mouseover",
        mouseleave: "mouseout"
    };

    jQuery.each(["live", "die"], function (i, name) {
        jQuery.fn[name] = function (types, data, fn, origSelector /* Internal Use Only */) {
            var type, i = 0, match, namespaces, preType,
			selector = origSelector || this.selector,
			context = origSelector ? this : jQuery(this.context);

            if (typeof types === "object" && !types.preventDefault) {
                for (var key in types) {
                    context[name](key, data, types[key], selector);
                }

                return this;
            }

            if (name === "die" && !types &&
					origSelector && origSelector.charAt(0) === ".") {

                context.unbind(origSelector);

                return this;
            }

            if (data === false || jQuery.isFunction(data)) {
                fn = data || returnFalse;
                data = undefined;
            }

            types = (types || "").split(" ");

            while ((type = types[i++]) != null) {
                match = rnamespaces.exec(type);
                namespaces = "";

                if (match) {
                    namespaces = match[0];
                    type = type.replace(rnamespaces, "");
                }

                if (type === "hover") {
                    types.push("mouseenter" + namespaces, "mouseleave" + namespaces);
                    continue;
                }

                preType = type;

                if (liveMap[type]) {
                    types.push(liveMap[type] + namespaces);
                    type = type + namespaces;

                } else {
                    type = (liveMap[type] || type) + namespaces;
                }

                if (name === "live") {
                    // bind live handler
                    for (var j = 0, l = context.length; j < l; j++) {
                        jQuery.event.add(context[j], "live." + liveConvert(type, selector),
						{ data: data, selector: selector, handler: fn, origType: type, origHandler: fn, preType: preType });
                    }

                } else {
                    // unbind live handler
                    context.unbind("live." + liveConvert(type, selector), fn);
                }
            }

            return this;
        };
    });

    function liveHandler(event) {
        var stop, maxLevel, related, match, handleObj, elem, j, i, l, data, close, namespace, ret,
		elems = [],
		selectors = [],
		events = jQuery._data(this, "events");

        // Make sure we avoid non-left-click bubbling in Firefox (#3861) and disabled elements in IE (#6911)
        if (event.liveFired === this || !events || !events.live || event.target.disabled || event.button && event.type === "click") {
            return;
        }

        if (event.namespace) {
            namespace = new RegExp("(^|\\.)" + event.namespace.split(".").join("\\.(?:.*\\.)?") + "(\\.|$)");
        }

        event.liveFired = this;

        var live = events.live.slice(0);

        for (j = 0; j < live.length; j++) {
            handleObj = live[j];

            if (handleObj.origType.replace(rnamespaces, "") === event.type) {
                selectors.push(handleObj.selector);

            } else {
                live.splice(j--, 1);
            }
        }

        match = jQuery(event.target).closest(selectors, event.currentTarget);

        for (i = 0, l = match.length; i < l; i++) {
            close = match[i];

            for (j = 0; j < live.length; j++) {
                handleObj = live[j];

                if (close.selector === handleObj.selector && (!namespace || namespace.test(handleObj.namespace)) && !close.elem.disabled) {
                    elem = close.elem;
                    related = null;

                    // Those two events require additional checking
                    if (handleObj.preType === "mouseenter" || handleObj.preType === "mouseleave") {
                        event.type = handleObj.preType;
                        related = jQuery(event.relatedTarget).closest(handleObj.selector)[0];

                        // Make sure not to accidentally match a child element with the same selector
                        if (related && jQuery.contains(elem, related)) {
                            related = elem;
                        }
                    }

                    if (!related || related !== elem) {
                        elems.push({ elem: elem, handleObj: handleObj, level: close.level });
                    }
                }
            }
        }

        for (i = 0, l = elems.length; i < l; i++) {
            match = elems[i];

            if (maxLevel && match.level > maxLevel) {
                break;
            }

            event.currentTarget = match.elem;
            event.data = match.handleObj.data;
            event.handleObj = match.handleObj;

            ret = match.handleObj.origHandler.apply(match.elem, arguments);

            if (ret === false || event.isPropagationStopped()) {
                maxLevel = match.level;

                if (ret === false) {
                    stop = false;
                }
                if (event.isImmediatePropagationStopped()) {
                    break;
                }
            }
        }

        return stop;
    }

    function liveConvert(type, selector) {
        return (type && type !== "*" ? type + "." : "") + selector.replace(rperiod, "`").replace(rspaces, "&");
    }

    jQuery.each(("blur focus focusin focusout load resize scroll unload click dblclick " +
	"mousedown mouseup mousemove mouseover mouseout mouseenter mouseleave " +
	"change select submit keydown keypress keyup error").split(" "), function (i, name) {

	    // Handle event binding
	    jQuery.fn[name] = function (data, fn) {
	        if (fn == null) {
	            fn = data;
	            data = null;
	        }

	        return arguments.length > 0 ?
			this.bind(name, data, fn) :
			this.trigger(name);
	    };

	    if (jQuery.attrFn) {
	        jQuery.attrFn[name] = true;
	    }
	});



    /*!
    * Sizzle CSS Selector Engine
    *  Copyright 2011, The Dojo Foundation
    *  Released under the MIT, BSD, and GPL Licenses.
    *  More information: http://sizzlejs.com/
    */
    (function () {

        var chunker = /((?:\((?:\([^()]+\)|[^()]+)+\)|\[(?:\[[^\[\]]*\]|['"][^'"]*['"]|[^\[\]'"]+)+\]|\\.|[^ >+~,(\[\\]+)+|[>+~])(\s*,\s*)?((?:.|\r|\n)*)/g,
	done = 0,
	toString = Object.prototype.toString,
	hasDuplicate = false,
	baseHasDuplicate = true,
	rBackslash = /\\/g,
	rNonWord = /\W/;

        // Here we check if the JavaScript engine is using some sort of
        // optimization where it does not always call our comparision
        // function. If that is the case, discard the hasDuplicate value.
        //   Thus far that includes Google Chrome.
        [0, 0].sort(function () {
            baseHasDuplicate = false;
            return 0;
        });

        var Sizzle = function (selector, context, results, seed) {
            results = results || [];
            context = context || document;

            var origContext = context;

            if (context.nodeType !== 1 && context.nodeType !== 9) {
                return [];
            }

            if (!selector || typeof selector !== "string") {
                return results;
            }

            var m, set, checkSet, extra, ret, cur, pop, i,
		prune = true,
		contextXML = Sizzle.isXML(context),
		parts = [],
		soFar = selector;

            // Reset the position of the chunker regexp (start from head)
            do {
                chunker.exec("");
                m = chunker.exec(soFar);

                if (m) {
                    soFar = m[3];

                    parts.push(m[1]);

                    if (m[2]) {
                        extra = m[3];
                        break;
                    }
                }
            } while (m);

            if (parts.length > 1 && origPOS.exec(selector)) {

                if (parts.length === 2 && Expr.relative[parts[0]]) {
                    set = posProcess(parts[0] + parts[1], context);

                } else {
                    set = Expr.relative[parts[0]] ?
				[context] :
				Sizzle(parts.shift(), context);

                    while (parts.length) {
                        selector = parts.shift();

                        if (Expr.relative[selector]) {
                            selector += parts.shift();
                        }

                        set = posProcess(selector, set);
                    }
                }

            } else {
                // Take a shortcut and set the context if the root selector is an ID
                // (but not if it'll be faster if the inner selector is an ID)
                if (!seed && parts.length > 1 && context.nodeType === 9 && !contextXML &&
				Expr.match.ID.test(parts[0]) && !Expr.match.ID.test(parts[parts.length - 1])) {

                    ret = Sizzle.find(parts.shift(), context, contextXML);
                    context = ret.expr ?
				Sizzle.filter(ret.expr, ret.set)[0] :
				ret.set[0];
                }

                if (context) {
                    ret = seed ?
				{ expr: parts.pop(), set: makeArray(seed)} :
				Sizzle.find(parts.pop(), parts.length === 1 && (parts[0] === "~" || parts[0] === "+") && context.parentNode ? context.parentNode : context, contextXML);

                    set = ret.expr ?
				Sizzle.filter(ret.expr, ret.set) :
				ret.set;

                    if (parts.length > 0) {
                        checkSet = makeArray(set);

                    } else {
                        prune = false;
                    }

                    while (parts.length) {
                        cur = parts.pop();
                        pop = cur;

                        if (!Expr.relative[cur]) {
                            cur = "";
                        } else {
                            pop = parts.pop();
                        }

                        if (pop == null) {
                            pop = context;
                        }

                        Expr.relative[cur](checkSet, pop, contextXML);
                    }

                } else {
                    checkSet = parts = [];
                }
            }

            if (!checkSet) {
                checkSet = set;
            }

            if (!checkSet) {
                Sizzle.error(cur || selector);
            }

            if (toString.call(checkSet) === "[object Array]") {
                if (!prune) {
                    results.push.apply(results, checkSet);

                } else if (context && context.nodeType === 1) {
                    for (i = 0; checkSet[i] != null; i++) {
                        if (checkSet[i] && (checkSet[i] === true || checkSet[i].nodeType === 1 && Sizzle.contains(context, checkSet[i]))) {
                            results.push(set[i]);
                        }
                    }

                } else {
                    for (i = 0; checkSet[i] != null; i++) {
                        if (checkSet[i] && checkSet[i].nodeType === 1) {
                            results.push(set[i]);
                        }
                    }
                }

            } else {
                makeArray(checkSet, results);
            }

            if (extra) {
                Sizzle(extra, origContext, results, seed);
                Sizzle.uniqueSort(results);
            }

            return results;
        };

        Sizzle.uniqueSort = function (results) {
            if (sortOrder) {
                hasDuplicate = baseHasDuplicate;
                results.sort(sortOrder);

                if (hasDuplicate) {
                    for (var i = 1; i < results.length; i++) {
                        if (results[i] === results[i - 1]) {
                            results.splice(i--, 1);
                        }
                    }
                }
            }

            return results;
        };

        Sizzle.matches = function (expr, set) {
            return Sizzle(expr, null, null, set);
        };

        Sizzle.matchesSelector = function (node, expr) {
            return Sizzle(expr, null, null, [node]).length > 0;
        };

        Sizzle.find = function (expr, context, isXML) {
            var set;

            if (!expr) {
                return [];
            }

            for (var i = 0, l = Expr.order.length; i < l; i++) {
                var match,
			type = Expr.order[i];

                if ((match = Expr.leftMatch[type].exec(expr))) {
                    var left = match[1];
                    match.splice(1, 1);

                    if (left.substr(left.length - 1) !== "\\") {
                        match[1] = (match[1] || "").replace(rBackslash, "");
                        set = Expr.find[type](match, context, isXML);

                        if (set != null) {
                            expr = expr.replace(Expr.match[type], "");
                            break;
                        }
                    }
                }
            }

            if (!set) {
                set = typeof context.getElementsByTagName !== "undefined" ?
			context.getElementsByTagName("*") :
			[];
            }

            return { set: set, expr: expr };
        };

        Sizzle.filter = function (expr, set, inplace, not) {
            var match, anyFound,
		old = expr,
		result = [],
		curLoop = set,
		isXMLFilter = set && set[0] && Sizzle.isXML(set[0]);

            while (expr && set.length) {
                for (var type in Expr.filter) {
                    if ((match = Expr.leftMatch[type].exec(expr)) != null && match[2]) {
                        var found, item,
					filter = Expr.filter[type],
					left = match[1];

                        anyFound = false;

                        match.splice(1, 1);

                        if (left.substr(left.length - 1) === "\\") {
                            continue;
                        }

                        if (curLoop === result) {
                            result = [];
                        }

                        if (Expr.preFilter[type]) {
                            match = Expr.preFilter[type](match, curLoop, inplace, result, not, isXMLFilter);

                            if (!match) {
                                anyFound = found = true;

                            } else if (match === true) {
                                continue;
                            }
                        }

                        if (match) {
                            for (var i = 0; (item = curLoop[i]) != null; i++) {
                                if (item) {
                                    found = filter(item, match, i, curLoop);
                                    var pass = not ^ !!found;

                                    if (inplace && found != null) {
                                        if (pass) {
                                            anyFound = true;

                                        } else {
                                            curLoop[i] = false;
                                        }

                                    } else if (pass) {
                                        result.push(item);
                                        anyFound = true;
                                    }
                                }
                            }
                        }

                        if (found !== undefined) {
                            if (!inplace) {
                                curLoop = result;
                            }

                            expr = expr.replace(Expr.match[type], "");

                            if (!anyFound) {
                                return [];
                            }

                            break;
                        }
                    }
                }

                // Improper expression
                if (expr === old) {
                    if (anyFound == null) {
                        Sizzle.error(expr);

                    } else {
                        break;
                    }
                }

                old = expr;
            }

            return curLoop;
        };

        Sizzle.error = function (msg) {
            throw "Syntax error, unrecognized expression: " + msg;
        };

        var Expr = Sizzle.selectors = {
            order: ["ID", "NAME", "TAG"],

            match: {
                ID: /#((?:[\w\u00c0-\uFFFF\-]|\\.)+)/,
                CLASS: /\.((?:[\w\u00c0-\uFFFF\-]|\\.)+)/,
                NAME: /\[name=['"]*((?:[\w\u00c0-\uFFFF\-]|\\.)+)['"]*\]/,
                ATTR: /\[\s*((?:[\w\u00c0-\uFFFF\-]|\\.)+)\s*(?:(\S?=)\s*(?:(['"])(.*?)\3|(#?(?:[\w\u00c0-\uFFFF\-]|\\.)*)|)|)\s*\]/,
                TAG: /^((?:[\w\u00c0-\uFFFF\*\-]|\\.)+)/,
                CHILD: /:(only|nth|last|first)-child(?:\(\s*(even|odd|(?:[+\-]?\d+|(?:[+\-]?\d*)?n\s*(?:[+\-]\s*\d+)?))\s*\))?/,
                POS: /:(nth|eq|gt|lt|first|last|even|odd)(?:\((\d*)\))?(?=[^\-]|$)/,
                PSEUDO: /:((?:[\w\u00c0-\uFFFF\-]|\\.)+)(?:\((['"]?)((?:\([^\)]+\)|[^\(\)]*)+)\2\))?/
            },

            leftMatch: {},

            attrMap: {
                "class": "className",
                "for": "htmlFor"
            },

            attrHandle: {
                href: function (elem) {
                    return elem.getAttribute("href");
                },
                type: function (elem) {
                    return elem.getAttribute("type");
                }
            },

            relative: {
                "+": function (checkSet, part) {
                    var isPartStr = typeof part === "string",
				isTag = isPartStr && !rNonWord.test(part),
				isPartStrNotTag = isPartStr && !isTag;

                    if (isTag) {
                        part = part.toLowerCase();
                    }

                    for (var i = 0, l = checkSet.length, elem; i < l; i++) {
                        if ((elem = checkSet[i])) {
                            while ((elem = elem.previousSibling) && elem.nodeType !== 1) { }

                            checkSet[i] = isPartStrNotTag || elem && elem.nodeName.toLowerCase() === part ?
						elem || false :
						elem === part;
                        }
                    }

                    if (isPartStrNotTag) {
                        Sizzle.filter(part, checkSet, true);
                    }
                },

                ">": function (checkSet, part) {
                    var elem,
				isPartStr = typeof part === "string",
				i = 0,
				l = checkSet.length;

                    if (isPartStr && !rNonWord.test(part)) {
                        part = part.toLowerCase();

                        for (; i < l; i++) {
                            elem = checkSet[i];

                            if (elem) {
                                var parent = elem.parentNode;
                                checkSet[i] = parent.nodeName.toLowerCase() === part ? parent : false;
                            }
                        }

                    } else {
                        for (; i < l; i++) {
                            elem = checkSet[i];

                            if (elem) {
                                checkSet[i] = isPartStr ?
							elem.parentNode :
							elem.parentNode === part;
                            }
                        }

                        if (isPartStr) {
                            Sizzle.filter(part, checkSet, true);
                        }
                    }
                },

                "": function (checkSet, part, isXML) {
                    var nodeCheck,
				doneName = done++,
				checkFn = dirCheck;

                    if (typeof part === "string" && !rNonWord.test(part)) {
                        part = part.toLowerCase();
                        nodeCheck = part;
                        checkFn = dirNodeCheck;
                    }

                    checkFn("parentNode", part, doneName, checkSet, nodeCheck, isXML);
                },

                "~": function (checkSet, part, isXML) {
                    var nodeCheck,
				doneName = done++,
				checkFn = dirCheck;

                    if (typeof part === "string" && !rNonWord.test(part)) {
                        part = part.toLowerCase();
                        nodeCheck = part;
                        checkFn = dirNodeCheck;
                    }

                    checkFn("previousSibling", part, doneName, checkSet, nodeCheck, isXML);
                }
            },

            find: {
                ID: function (match, context, isXML) {
                    if (typeof context.getElementById !== "undefined" && !isXML) {
                        var m = context.getElementById(match[1]);
                        // Check parentNode to catch when Blackberry 4.6 returns
                        // nodes that are no longer in the document #6963
                        return m && m.parentNode ? [m] : [];
                    }
                },

                NAME: function (match, context) {
                    if (typeof context.getElementsByName !== "undefined") {
                        var ret = [],
					results = context.getElementsByName(match[1]);

                        for (var i = 0, l = results.length; i < l; i++) {
                            if (results[i].getAttribute("name") === match[1]) {
                                ret.push(results[i]);
                            }
                        }

                        return ret.length === 0 ? null : ret;
                    }
                },

                TAG: function (match, context) {
                    if (typeof context.getElementsByTagName !== "undefined") {
                        return context.getElementsByTagName(match[1]);
                    }
                }
            },
            preFilter: {
                CLASS: function (match, curLoop, inplace, result, not, isXML) {
                    match = " " + match[1].replace(rBackslash, "") + " ";

                    if (isXML) {
                        return match;
                    }

                    for (var i = 0, elem; (elem = curLoop[i]) != null; i++) {
                        if (elem) {
                            if (not ^ (elem.className && (" " + elem.className + " ").replace(/[\t\n\r]/g, " ").indexOf(match) >= 0)) {
                                if (!inplace) {
                                    result.push(elem);
                                }

                            } else if (inplace) {
                                curLoop[i] = false;
                            }
                        }
                    }

                    return false;
                },

                ID: function (match) {
                    return match[1].replace(rBackslash, "");
                },

                TAG: function (match, curLoop) {
                    return match[1].replace(rBackslash, "").toLowerCase();
                },

                CHILD: function (match) {
                    if (match[1] === "nth") {
                        if (!match[2]) {
                            Sizzle.error(match[0]);
                        }

                        match[2] = match[2].replace(/^\+|\s*/g, '');

                        // parse equations like 'even', 'odd', '5', '2n', '3n+2', '4n-1', '-n+6'
                        var test = /(-?)(\d*)(?:n([+\-]?\d*))?/.exec(
					match[2] === "even" && "2n" || match[2] === "odd" && "2n+1" ||
					!/\D/.test(match[2]) && "0n+" + match[2] || match[2]);

                        // calculate the numbers (first)n+(last) including if they are negative
                        match[2] = (test[1] + (test[2] || 1)) - 0;
                        match[3] = test[3] - 0;
                    }
                    else if (match[2]) {
                        Sizzle.error(match[0]);
                    }

                    // TODO: Move to normal caching system
                    match[0] = done++;

                    return match;
                },

                ATTR: function (match, curLoop, inplace, result, not, isXML) {
                    var name = match[1] = match[1].replace(rBackslash, "");

                    if (!isXML && Expr.attrMap[name]) {
                        match[1] = Expr.attrMap[name];
                    }

                    // Handle if an un-quoted value was used
                    match[4] = (match[4] || match[5] || "").replace(rBackslash, "");

                    if (match[2] === "~=") {
                        match[4] = " " + match[4] + " ";
                    }

                    return match;
                },

                PSEUDO: function (match, curLoop, inplace, result, not) {
                    if (match[1] === "not") {
                        // If we're dealing with a complex expression, or a simple one
                        if ((chunker.exec(match[3]) || "").length > 1 || /^\w/.test(match[3])) {
                            match[3] = Sizzle(match[3], null, null, curLoop);

                        } else {
                            var ret = Sizzle.filter(match[3], curLoop, inplace, true ^ not);

                            if (!inplace) {
                                result.push.apply(result, ret);
                            }

                            return false;
                        }

                    } else if (Expr.match.POS.test(match[0]) || Expr.match.CHILD.test(match[0])) {
                        return true;
                    }

                    return match;
                },

                POS: function (match) {
                    match.unshift(true);

                    return match;
                }
            },

            filters: {
                enabled: function (elem) {
                    return elem.disabled === false && elem.type !== "hidden";
                },

                disabled: function (elem) {
                    return elem.disabled === true;
                },

                checked: function (elem) {
                    return elem.checked === true;
                },

                selected: function (elem) {
                    // Accessing this property makes selected-by-default
                    // options in Safari work properly
                    if (elem.parentNode) {
                        elem.parentNode.selectedIndex;
                    }

                    return elem.selected === true;
                },

                parent: function (elem) {
                    return !!elem.firstChild;
                },

                empty: function (elem) {
                    return !elem.firstChild;
                },

                has: function (elem, i, match) {
                    return !!Sizzle(match[3], elem).length;
                },

                header: function (elem) {
                    return (/h\d/i).test(elem.nodeName);
                },

                text: function (elem) {
                    var attr = elem.getAttribute("type"), type = elem.type;
                    // IE6 and 7 will map elem.type to 'text' for new HTML5 types (search, etc) 
                    // use getAttribute instead to test this case
                    return elem.nodeName.toLowerCase() === "input" && "text" === type && (attr === type || attr === null);
                },

                radio: function (elem) {
                    return elem.nodeName.toLowerCase() === "input" && "radio" === elem.type;
                },

                checkbox: function (elem) {
                    return elem.nodeName.toLowerCase() === "input" && "checkbox" === elem.type;
                },

                file: function (elem) {
                    return elem.nodeName.toLowerCase() === "input" && "file" === elem.type;
                },

                password: function (elem) {
                    return elem.nodeName.toLowerCase() === "input" && "password" === elem.type;
                },

                submit: function (elem) {
                    var name = elem.nodeName.toLowerCase();
                    return (name === "input" || name === "button") && "submit" === elem.type;
                },

                image: function (elem) {
                    return elem.nodeName.toLowerCase() === "input" && "image" === elem.type;
                },

                reset: function (elem) {
                    var name = elem.nodeName.toLowerCase();
                    return (name === "input" || name === "button") && "reset" === elem.type;
                },

                button: function (elem) {
                    var name = elem.nodeName.toLowerCase();
                    return name === "input" && "button" === elem.type || name === "button";
                },

                input: function (elem) {
                    return (/input|select|textarea|button/i).test(elem.nodeName);
                },

                focus: function (elem) {
                    return elem === elem.ownerDocument.activeElement;
                }
            },
            setFilters: {
                first: function (elem, i) {
                    return i === 0;
                },

                last: function (elem, i, match, array) {
                    return i === array.length - 1;
                },

                even: function (elem, i) {
                    return i % 2 === 0;
                },

                odd: function (elem, i) {
                    return i % 2 === 1;
                },

                lt: function (elem, i, match) {
                    return i < match[3] - 0;
                },

                gt: function (elem, i, match) {
                    return i > match[3] - 0;
                },

                nth: function (elem, i, match) {
                    return match[3] - 0 === i;
                },

                eq: function (elem, i, match) {
                    return match[3] - 0 === i;
                }
            },
            filter: {
                PSEUDO: function (elem, match, i, array) {
                    var name = match[1],
				filter = Expr.filters[name];

                    if (filter) {
                        return filter(elem, i, match, array);

                    } else if (name === "contains") {
                        return (elem.textContent || elem.innerText || Sizzle.getText([elem]) || "").indexOf(match[3]) >= 0;

                    } else if (name === "not") {
                        var not = match[3];

                        for (var j = 0, l = not.length; j < l; j++) {
                            if (not[j] === elem) {
                                return false;
                            }
                        }

                        return true;

                    } else {
                        Sizzle.error(name);
                    }
                },

                CHILD: function (elem, match) {
                    var type = match[1],
				node = elem;

                    switch (type) {
                        case "only":
                        case "first":
                            while ((node = node.previousSibling)) {
                                if (node.nodeType === 1) {
                                    return false;
                                }
                            }

                            if (type === "first") {
                                return true;
                            }

                            node = elem;

                        case "last":
                            while ((node = node.nextSibling)) {
                                if (node.nodeType === 1) {
                                    return false;
                                }
                            }

                            return true;

                        case "nth":
                            var first = match[2],
						last = match[3];

                            if (first === 1 && last === 0) {
                                return true;
                            }

                            var doneName = match[0],
						parent = elem.parentNode;

                            if (parent && (parent.sizcache !== doneName || !elem.nodeIndex)) {
                                var count = 0;

                                for (node = parent.firstChild; node; node = node.nextSibling) {
                                    if (node.nodeType === 1) {
                                        node.nodeIndex = ++count;
                                    }
                                }

                                parent.sizcache = doneName;
                            }

                            var diff = elem.nodeIndex - last;

                            if (first === 0) {
                                return diff === 0;

                            } else {
                                return (diff % first === 0 && diff / first >= 0);
                            }
                    }
                },

                ID: function (elem, match) {
                    return elem.nodeType === 1 && elem.getAttribute("id") === match;
                },

                TAG: function (elem, match) {
                    return (match === "*" && elem.nodeType === 1) || elem.nodeName.toLowerCase() === match;
                },

                CLASS: function (elem, match) {
                    return (" " + (elem.className || elem.getAttribute("class")) + " ")
				.indexOf(match) > -1;
                },

                ATTR: function (elem, match) {
                    var name = match[1],
				result = Expr.attrHandle[name] ?
					Expr.attrHandle[name](elem) :
					elem[name] != null ?
						elem[name] :
						elem.getAttribute(name),
				value = result + "",
				type = match[2],
				check = match[4];

                    return result == null ?
				type === "!=" :
				type === "=" ?
				value === check :
				type === "*=" ?
				value.indexOf(check) >= 0 :
				type === "~=" ?
				(" " + value + " ").indexOf(check) >= 0 :
				!check ?
				value && result !== false :
				type === "!=" ?
				value !== check :
				type === "^=" ?
				value.indexOf(check) === 0 :
				type === "$=" ?
				value.substr(value.length - check.length) === check :
				type === "|=" ?
				value === check || value.substr(0, check.length + 1) === check + "-" :
				false;
                },

                POS: function (elem, match, i, array) {
                    var name = match[2],
				filter = Expr.setFilters[name];

                    if (filter) {
                        return filter(elem, i, match, array);
                    }
                }
            }
        };

        var origPOS = Expr.match.POS,
	fescape = function (all, num) {
	    return "\\" + (num - 0 + 1);
	};

        for (var type in Expr.match) {
            Expr.match[type] = new RegExp(Expr.match[type].source + (/(?![^\[]*\])(?![^\(]*\))/.source));
            Expr.leftMatch[type] = new RegExp(/(^(?:.|\r|\n)*?)/.source + Expr.match[type].source.replace(/\\(\d+)/g, fescape));
        }

        var makeArray = function (array, results) {
            array = Array.prototype.slice.call(array, 0);

            if (results) {
                results.push.apply(results, array);
                return results;
            }

            return array;
        };

        // Perform a simple check to determine if the browser is capable of
        // converting a NodeList to an array using builtin methods.
        // Also verifies that the returned array holds DOM nodes
        // (which is not the case in the Blackberry browser)
        try {
            Array.prototype.slice.call(document.documentElement.childNodes, 0)[0].nodeType;

            // Provide a fallback method if it does not work
        } catch (e) {
            makeArray = function (array, results) {
                var i = 0,
			ret = results || [];

                if (toString.call(array) === "[object Array]") {
                    Array.prototype.push.apply(ret, array);

                } else {
                    if (typeof array.length === "number") {
                        for (var l = array.length; i < l; i++) {
                            ret.push(array[i]);
                        }

                    } else {
                        for (; array[i]; i++) {
                            ret.push(array[i]);
                        }
                    }
                }

                return ret;
            };
        }

        var sortOrder, siblingCheck;

        if (document.documentElement.compareDocumentPosition) {
            sortOrder = function (a, b) {
                if (a === b) {
                    hasDuplicate = true;
                    return 0;
                }

                if (!a.compareDocumentPosition || !b.compareDocumentPosition) {
                    return a.compareDocumentPosition ? -1 : 1;
                }

                return a.compareDocumentPosition(b) & 4 ? -1 : 1;
            };

        } else {
            sortOrder = function (a, b) {
                // The nodes are identical, we can exit early
                if (a === b) {
                    hasDuplicate = true;
                    return 0;

                    // Fallback to using sourceIndex (in IE) if it's available on both nodes
                } else if (a.sourceIndex && b.sourceIndex) {
                    return a.sourceIndex - b.sourceIndex;
                }

                var al, bl,
			ap = [],
			bp = [],
			aup = a.parentNode,
			bup = b.parentNode,
			cur = aup;

                // If the nodes are siblings (or identical) we can do a quick check
                if (aup === bup) {
                    return siblingCheck(a, b);

                    // If no parents were found then the nodes are disconnected
                } else if (!aup) {
                    return -1;

                } else if (!bup) {
                    return 1;
                }

                // Otherwise they're somewhere else in the tree so we need
                // to build up a full list of the parentNodes for comparison
                while (cur) {
                    ap.unshift(cur);
                    cur = cur.parentNode;
                }

                cur = bup;

                while (cur) {
                    bp.unshift(cur);
                    cur = cur.parentNode;
                }

                al = ap.length;
                bl = bp.length;

                // Start walking down the tree looking for a discrepancy
                for (var i = 0; i < al && i < bl; i++) {
                    if (ap[i] !== bp[i]) {
                        return siblingCheck(ap[i], bp[i]);
                    }
                }

                // We ended someplace up the tree so do a sibling check
                return i === al ?
			siblingCheck(a, bp[i], -1) :
			siblingCheck(ap[i], b, 1);
            };

            siblingCheck = function (a, b, ret) {
                if (a === b) {
                    return ret;
                }

                var cur = a.nextSibling;

                while (cur) {
                    if (cur === b) {
                        return -1;
                    }

                    cur = cur.nextSibling;
                }

                return 1;
            };
        }

        // Utility function for retreiving the text value of an array of DOM nodes
        Sizzle.getText = function (elems) {
            var ret = "", elem;

            for (var i = 0; elems[i]; i++) {
                elem = elems[i];

                // Get the text from text nodes and CDATA nodes
                if (elem.nodeType === 3 || elem.nodeType === 4) {
                    ret += elem.nodeValue;

                    // Traverse everything else, except comment nodes
                } else if (elem.nodeType !== 8) {
                    ret += Sizzle.getText(elem.childNodes);
                }
            }

            return ret;
        };

        // Check to see if the browser returns elements by name when
        // querying by getElementById (and provide a workaround)
        (function () {
            // We're going to inject a fake input element with a specified name
            var form = document.createElement("div"),
		id = "script" + (new Date()).getTime(),
		root = document.documentElement;

            form.innerHTML = "<a name='" + id + "'/>";

            // Inject it into the root element, check its status, and remove it quickly
            root.insertBefore(form, root.firstChild);

            // The workaround has to do additional checks after a getElementById
            // Which slows things down for other browsers (hence the branching)
            if (document.getElementById(id)) {
                Expr.find.ID = function (match, context, isXML) {
                    if (typeof context.getElementById !== "undefined" && !isXML) {
                        var m = context.getElementById(match[1]);

                        return m ?
					m.id === match[1] || typeof m.getAttributeNode !== "undefined" && m.getAttributeNode("id").nodeValue === match[1] ?
						[m] :
						undefined :
					[];
                    }
                };

                Expr.filter.ID = function (elem, match) {
                    var node = typeof elem.getAttributeNode !== "undefined" && elem.getAttributeNode("id");

                    return elem.nodeType === 1 && node && node.nodeValue === match;
                };
            }

            root.removeChild(form);

            // release memory in IE
            root = form = null;
        })();

        (function () {
            // Check to see if the browser returns only elements
            // when doing getElementsByTagName("*")

            // Create a fake element
            var div = document.createElement("div");
            div.appendChild(document.createComment(""));

            // Make sure no comments are found
            if (div.getElementsByTagName("*").length > 0) {
                Expr.find.TAG = function (match, context) {
                    var results = context.getElementsByTagName(match[1]);

                    // Filter out possible comments
                    if (match[1] === "*") {
                        var tmp = [];

                        for (var i = 0; results[i]; i++) {
                            if (results[i].nodeType === 1) {
                                tmp.push(results[i]);
                            }
                        }

                        results = tmp;
                    }

                    return results;
                };
            }

            // Check to see if an attribute returns normalized href attributes
            div.innerHTML = "<a href='#'></a>";

            if (div.firstChild && typeof div.firstChild.getAttribute !== "undefined" &&
			div.firstChild.getAttribute("href") !== "#") {

                Expr.attrHandle.href = function (elem) {
                    return elem.getAttribute("href", 2);
                };
            }

            // release memory in IE
            div = null;
        })();

        if (document.querySelectorAll) {
            (function () {
                var oldSizzle = Sizzle,
			div = document.createElement("div"),
			id = "__sizzle__";

                div.innerHTML = "<p class='TEST'></p>";

                // Safari can't handle uppercase or unicode characters when
                // in quirks mode.
                if (div.querySelectorAll && div.querySelectorAll(".TEST").length === 0) {
                    return;
                }

                Sizzle = function (query, context, extra, seed) {
                    context = context || document;

                    // Only use querySelectorAll on non-XML documents
                    // (ID selectors don't work in non-HTML documents)
                    if (!seed && !Sizzle.isXML(context)) {
                        // See if we find a selector to speed up
                        var match = /^(\w+$)|^\.([\w\-]+$)|^#([\w\-]+$)/.exec(query);

                        if (match && (context.nodeType === 1 || context.nodeType === 9)) {
                            // Speed-up: Sizzle("TAG")
                            if (match[1]) {
                                return makeArray(context.getElementsByTagName(query), extra);

                                // Speed-up: Sizzle(".CLASS")
                            } else if (match[2] && Expr.find.CLASS && context.getElementsByClassName) {
                                return makeArray(context.getElementsByClassName(match[2]), extra);
                            }
                        }

                        if (context.nodeType === 9) {
                            // Speed-up: Sizzle("body")
                            // The body element only exists once, optimize finding it
                            if (query === "body" && context.body) {
                                return makeArray([context.body], extra);

                                // Speed-up: Sizzle("#ID")
                            } else if (match && match[3]) {
                                var elem = context.getElementById(match[3]);

                                // Check parentNode to catch when Blackberry 4.6 returns
                                // nodes that are no longer in the document #6963
                                if (elem && elem.parentNode) {
                                    // Handle the case where IE and Opera return items
                                    // by name instead of ID
                                    if (elem.id === match[3]) {
                                        return makeArray([elem], extra);
                                    }

                                } else {
                                    return makeArray([], extra);
                                }
                            }

                            try {
                                return makeArray(context.querySelectorAll(query), extra);
                            } catch (qsaError) { }

                            // qSA works strangely on Element-rooted queries
                            // We can work around this by specifying an extra ID on the root
                            // and working up from there (Thanks to Andrew Dupont for the technique)
                            // IE 8 doesn't work on object elements
                        } else if (context.nodeType === 1 && context.nodeName.toLowerCase() !== "object") {
                            var oldContext = context,
						old = context.getAttribute("id"),
						nid = old || id,
						hasParent = context.parentNode,
						relativeHierarchySelector = /^\s*[+~]/.test(query);

                            if (!old) {
                                context.setAttribute("id", nid);
                            } else {
                                nid = nid.replace(/'/g, "\\$&");
                            }
                            if (relativeHierarchySelector && hasParent) {
                                context = context.parentNode;
                            }

                            try {
                                if (!relativeHierarchySelector || hasParent) {
                                    return makeArray(context.querySelectorAll("[id='" + nid + "'] " + query), extra);
                                }

                            } catch (pseudoError) {
                            } finally {
                                if (!old) {
                                    oldContext.removeAttribute("id");
                                }
                            }
                        }
                    }

                    return oldSizzle(query, context, extra, seed);
                };

                for (var prop in oldSizzle) {
                    Sizzle[prop] = oldSizzle[prop];
                }

                // release memory in IE
                div = null;
            })();
        }

        (function () {
            var html = document.documentElement,
		matches = html.matchesSelector || html.mozMatchesSelector || html.webkitMatchesSelector || html.msMatchesSelector;

            if (matches) {
                // Check to see if it's possible to do matchesSelector
                // on a disconnected node (IE 9 fails this)
                var disconnectedMatch = !matches.call(document.createElement("div"), "div"),
			pseudoWorks = false;

                try {
                    // This should fail with an exception
                    // Gecko does not error, returns false instead
                    matches.call(document.documentElement, "[test!='']:sizzle");

                } catch (pseudoError) {
                    pseudoWorks = true;
                }

                Sizzle.matchesSelector = function (node, expr) {
                    // Make sure that attribute selectors are quoted
                    expr = expr.replace(/\=\s*([^'"\]]*)\s*\]/g, "='$1']");

                    if (!Sizzle.isXML(node)) {
                        try {
                            if (pseudoWorks || !Expr.match.PSEUDO.test(expr) && !/!=/.test(expr)) {
                                var ret = matches.call(node, expr);

                                // IE 9's matchesSelector returns false on disconnected nodes
                                if (ret || !disconnectedMatch ||
                                // As well, disconnected nodes are said to be in a document
                                // fragment in IE 9, so check for that
								node.document && node.document.nodeType !== 11) {
                                    return ret;
                                }
                            }
                        } catch (e) { }
                    }

                    return Sizzle(expr, null, null, [node]).length > 0;
                };
            }
        })();

        (function () {
            var div = document.createElement("div");

            div.innerHTML = "<div class='test e'></div><div class='test'></div>";

            // Opera can't find a second classname (in 9.6)
            // Also, make sure that getElementsByClassName actually exists
            if (!div.getElementsByClassName || div.getElementsByClassName("e").length === 0) {
                return;
            }

            // Safari caches class attributes, doesn't catch changes (in 3.2)
            div.lastChild.className = "e";

            if (div.getElementsByClassName("e").length === 1) {
                return;
            }

            Expr.order.splice(1, 0, "CLASS");
            Expr.find.CLASS = function (match, context, isXML) {
                if (typeof context.getElementsByClassName !== "undefined" && !isXML) {
                    return context.getElementsByClassName(match[1]);
                }
            };

            // release memory in IE
            div = null;
        })();

        function dirNodeCheck(dir, cur, doneName, checkSet, nodeCheck, isXML) {
            for (var i = 0, l = checkSet.length; i < l; i++) {
                var elem = checkSet[i];

                if (elem) {
                    var match = false;

                    elem = elem[dir];

                    while (elem) {
                        if (elem.sizcache === doneName) {
                            match = checkSet[elem.sizset];
                            break;
                        }

                        if (elem.nodeType === 1 && !isXML) {
                            elem.sizcache = doneName;
                            elem.sizset = i;
                        }

                        if (elem.nodeName.toLowerCase() === cur) {
                            match = elem;
                            break;
                        }

                        elem = elem[dir];
                    }

                    checkSet[i] = match;
                }
            }
        }

        function dirCheck(dir, cur, doneName, checkSet, nodeCheck, isXML) {
            for (var i = 0, l = checkSet.length; i < l; i++) {
                var elem = checkSet[i];

                if (elem) {
                    var match = false;

                    elem = elem[dir];

                    while (elem) {
                        if (elem.sizcache === doneName) {
                            match = checkSet[elem.sizset];
                            break;
                        }

                        if (elem.nodeType === 1) {
                            if (!isXML) {
                                elem.sizcache = doneName;
                                elem.sizset = i;
                            }

                            if (typeof cur !== "string") {
                                if (elem === cur) {
                                    match = true;
                                    break;
                                }

                            } else if (Sizzle.filter(cur, [elem]).length > 0) {
                                match = elem;
                                break;
                            }
                        }

                        elem = elem[dir];
                    }

                    checkSet[i] = match;
                }
            }
        }

        if (document.documentElement.contains) {
            Sizzle.contains = function (a, b) {
                return a !== b && (a.contains ? a.contains(b) : true);
            };

        } else if (document.documentElement.compareDocumentPosition) {
            Sizzle.contains = function (a, b) {
                return !!(a.compareDocumentPosition(b) & 16);
            };

        } else {
            Sizzle.contains = function () {
                return false;
            };
        }

        Sizzle.isXML = function (elem) {
            // documentElement is verified for cases where it doesn't yet exist
            // (such as loading iframes in IE - #4833) 
            var documentElement = (elem ? elem.ownerDocument || elem : 0).documentElement;

            return documentElement ? documentElement.nodeName !== "HTML" : false;
        };

        var posProcess = function (selector, context) {
            var match,
		tmpSet = [],
		later = "",
		root = context.nodeType ? [context] : context;

            // Position selectors must be done after the filter
            // And so must :not(positional) so we move all PSEUDOs to the end
            while ((match = Expr.match.PSEUDO.exec(selector))) {
                later += match[0];
                selector = selector.replace(Expr.match.PSEUDO, "");
            }

            selector = Expr.relative[selector] ? selector + "*" : selector;

            for (var i = 0, l = root.length; i < l; i++) {
                Sizzle(selector, root[i], tmpSet);
            }

            return Sizzle.filter(later, tmpSet);
        };

        // EXPOSE
        jQuery.find = Sizzle;
        jQuery.expr = Sizzle.selectors;
        jQuery.expr[":"] = jQuery.expr.filters;
        jQuery.unique = Sizzle.uniqueSort;
        jQuery.text = Sizzle.getText;
        jQuery.isXMLDoc = Sizzle.isXML;
        jQuery.contains = Sizzle.contains;


    })();


    var runtil = /Until$/,
	rparentsprev = /^(?:parents|prevUntil|prevAll)/,
    // Note: This RegExp should be improved, or likely pulled from Sizzle
	rmultiselector = /,/,
	isSimple = /^.[^:#\[\.,]*$/,
	slice = Array.prototype.slice,
	POS = jQuery.expr.match.POS,
    // methods guaranteed to produce a unique set when starting from a unique set
	guaranteedUnique = {
	    children: true,
	    contents: true,
	    next: true,
	    prev: true
	};

    jQuery.fn.extend({
        find: function (selector) {
            var self = this,
			i, l;

            if (typeof selector !== "string") {
                return jQuery(selector).filter(function () {
                    for (i = 0, l = self.length; i < l; i++) {
                        if (jQuery.contains(self[i], this)) {
                            return true;
                        }
                    }
                });
            }

            var ret = this.pushStack("", "find", selector),
			length, n, r;

            for (i = 0, l = this.length; i < l; i++) {
                length = ret.length;
                jQuery.find(selector, this[i], ret);

                if (i > 0) {
                    // Make sure that the results are unique
                    for (n = length; n < ret.length; n++) {
                        for (r = 0; r < length; r++) {
                            if (ret[r] === ret[n]) {
                                ret.splice(n--, 1);
                                break;
                            }
                        }
                    }
                }
            }

            return ret;
        },

        has: function (target) {
            var targets = jQuery(target);
            return this.filter(function () {
                for (var i = 0, l = targets.length; i < l; i++) {
                    if (jQuery.contains(this, targets[i])) {
                        return true;
                    }
                }
            });
        },

        not: function (selector) {
            return this.pushStack(winnow(this, selector, false), "not", selector);
        },

        filter: function (selector) {
            return this.pushStack(winnow(this, selector, true), "filter", selector);
        },

        is: function (selector) {
            return !!selector && (typeof selector === "string" ?
			jQuery.filter(selector, this).length > 0 :
			this.filter(selector).length > 0);
        },

        closest: function (selectors, context) {
            var ret = [], i, l, cur = this[0];

            // Array
            if (jQuery.isArray(selectors)) {
                var match, selector,
				matches = {},
				level = 1;

                if (cur && selectors.length) {
                    for (i = 0, l = selectors.length; i < l; i++) {
                        selector = selectors[i];

                        if (!matches[selector]) {
                            matches[selector] = POS.test(selector) ?
							jQuery(selector, context || this.context) :
							selector;
                        }
                    }

                    while (cur && cur.ownerDocument && cur !== context) {
                        for (selector in matches) {
                            match = matches[selector];

                            if (match.jquery ? match.index(cur) > -1 : jQuery(cur).is(match)) {
                                ret.push({ selector: selector, elem: cur, level: level });
                            }
                        }

                        cur = cur.parentNode;
                        level++;
                    }
                }

                return ret;
            }

            // String
            var pos = POS.test(selectors) || typeof selectors !== "string" ?
				jQuery(selectors, context || this.context) :
				0;

            for (i = 0, l = this.length; i < l; i++) {
                cur = this[i];

                while (cur) {
                    if (pos ? pos.index(cur) > -1 : jQuery.find.matchesSelector(cur, selectors)) {
                        ret.push(cur);
                        break;

                    } else {
                        cur = cur.parentNode;
                        if (!cur || !cur.ownerDocument || cur === context || cur.nodeType === 11) {
                            break;
                        }
                    }
                }
            }

            ret = ret.length > 1 ? jQuery.unique(ret) : ret;

            return this.pushStack(ret, "closest", selectors);
        },

        // Determine the position of an element within
        // the matched set of elements
        index: function (elem) {
            if (!elem || typeof elem === "string") {
                return jQuery.inArray(this[0],
                // If it receives a string, the selector is used
                // If it receives nothing, the siblings are used
				elem ? jQuery(elem) : this.parent().children());
            }
            // Locate the position of the desired element
            return jQuery.inArray(
            // If it receives a jQuery object, the first element is used
			elem.jquery ? elem[0] : elem, this);
        },

        add: function (selector, context) {
            var set = typeof selector === "string" ?
				jQuery(selector, context) :
				jQuery.makeArray(selector && selector.nodeType ? [selector] : selector),
			all = jQuery.merge(this.get(), set);

            return this.pushStack(isDisconnected(set[0]) || isDisconnected(all[0]) ?
			all :
			jQuery.unique(all));
        },

        andSelf: function () {
            return this.add(this.prevObject);
        }
    });

    // A painfully simple check to see if an element is disconnected
    // from a document (should be improved, where feasible).
    function isDisconnected(node) {
        return !node || !node.parentNode || node.parentNode.nodeType === 11;
    }

    jQuery.each({
        parent: function (elem) {
            var parent = elem.parentNode;
            return parent && parent.nodeType !== 11 ? parent : null;
        },
        parents: function (elem) {
            return jQuery.dir(elem, "parentNode");
        },
        parentsUntil: function (elem, i, until) {
            return jQuery.dir(elem, "parentNode", until);
        },
        next: function (elem) {
            return jQuery.nth(elem, 2, "nextSibling");
        },
        prev: function (elem) {
            return jQuery.nth(elem, 2, "previousSibling");
        },
        nextAll: function (elem) {
            return jQuery.dir(elem, "nextSibling");
        },
        prevAll: function (elem) {
            return jQuery.dir(elem, "previousSibling");
        },
        nextUntil: function (elem, i, until) {
            return jQuery.dir(elem, "nextSibling", until);
        },
        prevUntil: function (elem, i, until) {
            return jQuery.dir(elem, "previousSibling", until);
        },
        siblings: function (elem) {
            return jQuery.sibling(elem.parentNode.firstChild, elem);
        },
        children: function (elem) {
            return jQuery.sibling(elem.firstChild);
        },
        contents: function (elem) {
            return jQuery.nodeName(elem, "iframe") ?
			elem.contentDocument || elem.contentWindow.document :
			jQuery.makeArray(elem.childNodes);
        }
    }, function (name, fn) {
        jQuery.fn[name] = function (until, selector) {
            var ret = jQuery.map(this, fn, until),
            // The variable 'args' was introduced in
            // https://github.com/jquery/jquery/commit/52a0238
            // to work around a bug in Chrome 10 (Dev) and should be removed when the bug is fixed.
            // http://code.google.com/p/v8/issues/detail?id=1050
			args = slice.call(arguments);

            if (!runtil.test(name)) {
                selector = until;
            }

            if (selector && typeof selector === "string") {
                ret = jQuery.filter(selector, ret);
            }

            ret = this.length > 1 && !guaranteedUnique[name] ? jQuery.unique(ret) : ret;

            if ((this.length > 1 || rmultiselector.test(selector)) && rparentsprev.test(name)) {
                ret = ret.reverse();
            }

            return this.pushStack(ret, name, args.join(","));
        };
    });

    jQuery.extend({
        filter: function (expr, elems, not) {
            if (not) {
                expr = ":not(" + expr + ")";
            }

            return elems.length === 1 ?
			jQuery.find.matchesSelector(elems[0], expr) ? [elems[0]] : [] :
			jQuery.find.matches(expr, elems);
        },

        dir: function (elem, dir, until) {
            var matched = [],
			cur = elem[dir];

            while (cur && cur.nodeType !== 9 && (until === undefined || cur.nodeType !== 1 || !jQuery(cur).is(until))) {
                if (cur.nodeType === 1) {
                    matched.push(cur);
                }
                cur = cur[dir];
            }
            return matched;
        },

        nth: function (cur, result, dir, elem) {
            result = result || 1;
            var num = 0;

            for (; cur; cur = cur[dir]) {
                if (cur.nodeType === 1 && ++num === result) {
                    break;
                }
            }

            return cur;
        },

        sibling: function (n, elem) {
            var r = [];

            for (; n; n = n.nextSibling) {
                if (n.nodeType === 1 && n !== elem) {
                    r.push(n);
                }
            }

            return r;
        }
    });

    // Implement the identical functionality for filter and not
    function winnow(elements, qualifier, keep) {

        // Can't pass null or undefined to indexOf in Firefox 4
        // Set to 0 to skip string check
        qualifier = qualifier || 0;

        if (jQuery.isFunction(qualifier)) {
            return jQuery.grep(elements, function (elem, i) {
                var retVal = !!qualifier.call(elem, i, elem);
                return retVal === keep;
            });

        } else if (qualifier.nodeType) {
            return jQuery.grep(elements, function (elem, i) {
                return (elem === qualifier) === keep;
            });

        } else if (typeof qualifier === "string") {
            var filtered = jQuery.grep(elements, function (elem) {
                return elem.nodeType === 1;
            });

            if (isSimple.test(qualifier)) {
                return jQuery.filter(qualifier, filtered, !keep);
            } else {
                qualifier = jQuery.filter(qualifier, filtered);
            }
        }

        return jQuery.grep(elements, function (elem, i) {
            return (jQuery.inArray(elem, qualifier) >= 0) === keep;
        });
    }




    var rinlinejQuery = / jQuery\d+="(?:\d+|null)"/g,
	rleadingWhitespace = /^\s+/,
	rxhtmlTag = /<(?!area|br|col|embed|hr|img|input|link|meta|param)(([\w:]+)[^>]*)\/>/ig,
	rtagName = /<([\w:]+)/,
	rtbody = /<tbody/i,
	rhtml = /<|&#?\w+;/,
	rnocache = /<(?:script|object|embed|option|style)/i,
    // checked="checked" or checked
	rchecked = /checked\s*(?:[^=]|=\s*.checked.)/i,
	rscriptType = /\/(java|ecma)script/i,
	rcleanScript = /^\s*<!(?:\[CDATA\[|\-\-)/,
	wrapMap = {
	    option: [1, "<select multiple='multiple'>", "</select>"],
	    legend: [1, "<fieldset>", "</fieldset>"],
	    thead: [1, "<table>", "</table>"],
	    tr: [2, "<table><tbody>", "</tbody></table>"],
	    td: [3, "<table><tbody><tr>", "</tr></tbody></table>"],
	    col: [2, "<table><tbody></tbody><colgroup>", "</colgroup></table>"],
	    area: [1, "<map>", "</map>"],
	    _default: [0, "", ""]
	};

    wrapMap.optgroup = wrapMap.option;
    wrapMap.tbody = wrapMap.tfoot = wrapMap.colgroup = wrapMap.caption = wrapMap.thead;
    wrapMap.th = wrapMap.td;

    // IE can't serialize <link> and <script> tags normally
    if (!jQuery.support.htmlSerialize) {
        wrapMap._default = [1, "div<div>", "</div>"];
    }

    jQuery.fn.extend({
        text: function (text) {
            if (jQuery.isFunction(text)) {
                return this.each(function (i) {
                    var self = jQuery(this);

                    self.text(text.call(this, i, self.text()));
                });
            }

            if (typeof text !== "object" && text !== undefined) {
                return this.empty().append((this[0] && this[0].ownerDocument || document).createTextNode(text));
            }

            return jQuery.text(this);
        },

        wrapAll: function (html) {
            if (jQuery.isFunction(html)) {
                return this.each(function (i) {
                    jQuery(this).wrapAll(html.call(this, i));
                });
            }

            if (this[0]) {
                // The elements to wrap the target around
                var wrap = jQuery(html, this[0].ownerDocument).eq(0).clone(true);

                if (this[0].parentNode) {
                    wrap.insertBefore(this[0]);
                }

                wrap.map(function () {
                    var elem = this;

                    while (elem.firstChild && elem.firstChild.nodeType === 1) {
                        elem = elem.firstChild;
                    }

                    return elem;
                }).append(this);
            }

            return this;
        },

        wrapInner: function (html) {
            if (jQuery.isFunction(html)) {
                return this.each(function (i) {
                    jQuery(this).wrapInner(html.call(this, i));
                });
            }

            return this.each(function () {
                var self = jQuery(this),
				contents = self.contents();

                if (contents.length) {
                    contents.wrapAll(html);

                } else {
                    self.append(html);
                }
            });
        },

        wrap: function (html) {
            return this.each(function () {
                jQuery(this).wrapAll(html);
            });
        },

        unwrap: function () {
            return this.parent().each(function () {
                if (!jQuery.nodeName(this, "body")) {
                    jQuery(this).replaceWith(this.childNodes);
                }
            }).end();
        },

        append: function () {
            return this.domManip(arguments, true, function (elem) {
                if (this.nodeType === 1) {
                    this.appendChild(elem);
                }
            });
        },

        prepend: function () {
            return this.domManip(arguments, true, function (elem) {
                if (this.nodeType === 1) {
                    this.insertBefore(elem, this.firstChild);
                }
            });
        },

        before: function () {
            if (this[0] && this[0].parentNode) {
                return this.domManip(arguments, false, function (elem) {
                    this.parentNode.insertBefore(elem, this);
                });
            } else if (arguments.length) {
                var set = jQuery(arguments[0]);
                set.push.apply(set, this.toArray());
                return this.pushStack(set, "before", arguments);
            }
        },

        after: function () {
            if (this[0] && this[0].parentNode) {
                return this.domManip(arguments, false, function (elem) {
                    this.parentNode.insertBefore(elem, this.nextSibling);
                });
            } else if (arguments.length) {
                var set = this.pushStack(this, "after", arguments);
                set.push.apply(set, jQuery(arguments[0]).toArray());
                return set;
            }
        },

        // keepData is for internal use only--do not document
        remove: function (selector, keepData) {
            for (var i = 0, elem; (elem = this[i]) != null; i++) {
                if (!selector || jQuery.filter(selector, [elem]).length) {
                    if (!keepData && elem.nodeType === 1) {
                        jQuery.cleanData(elem.getElementsByTagName("*"));
                        jQuery.cleanData([elem]);
                    }

                    if (elem.parentNode) {
                        elem.parentNode.removeChild(elem);
                    }
                }
            }

            return this;
        },

        empty: function () {
            for (var i = 0, elem; (elem = this[i]) != null; i++) {
                // Remove element nodes and prevent memory leaks
                if (elem.nodeType === 1) {
                    jQuery.cleanData(elem.getElementsByTagName("*"));
                }

                // Remove any remaining nodes
                while (elem.firstChild) {
                    elem.removeChild(elem.firstChild);
                }
            }

            return this;
        },

        clone: function (dataAndEvents, deepDataAndEvents) {
            dataAndEvents = dataAndEvents == null ? false : dataAndEvents;
            deepDataAndEvents = deepDataAndEvents == null ? dataAndEvents : deepDataAndEvents;

            return this.map(function () {
                return jQuery.clone(this, dataAndEvents, deepDataAndEvents);
            });
        },

        html: function (value) {
            if (value === undefined) {
                return this[0] && this[0].nodeType === 1 ?
				this[0].innerHTML.replace(rinlinejQuery, "") :
				null;

                // See if we can take a shortcut and just use innerHTML
            } else if (typeof value === "string" && !rnocache.test(value) &&
			(jQuery.support.leadingWhitespace || !rleadingWhitespace.test(value)) &&
			!wrapMap[(rtagName.exec(value) || ["", ""])[1].toLowerCase()]) {

                value = value.replace(rxhtmlTag, "<$1></$2>");

                try {
                    for (var i = 0, l = this.length; i < l; i++) {
                        // Remove element nodes and prevent memory leaks
                        if (this[i].nodeType === 1) {
                            jQuery.cleanData(this[i].getElementsByTagName("*"));
                            this[i].innerHTML = value;
                        }
                    }

                    // If using innerHTML throws an exception, use the fallback method
                } catch (e) {
                    this.empty().append(value);
                }

            } else if (jQuery.isFunction(value)) {
                this.each(function (i) {
                    var self = jQuery(this);

                    self.html(value.call(this, i, self.html()));
                });

            } else {
                this.empty().append(value);
            }

            return this;
        },

        replaceWith: function (value) {
            if (this[0] && this[0].parentNode) {
                // Make sure that the elements are removed from the DOM before they are inserted
                // this can help fix replacing a parent with child elements
                if (jQuery.isFunction(value)) {
                    return this.each(function (i) {
                        var self = jQuery(this), old = self.html();
                        self.replaceWith(value.call(this, i, old));
                    });
                }

                if (typeof value !== "string") {
                    value = jQuery(value).detach();
                }

                return this.each(function () {
                    var next = this.nextSibling,
					parent = this.parentNode;

                    jQuery(this).remove();

                    if (next) {
                        jQuery(next).before(value);
                    } else {
                        jQuery(parent).append(value);
                    }
                });
            } else {
                return this.length ?
				this.pushStack(jQuery(jQuery.isFunction(value) ? value() : value), "replaceWith", value) :
				this;
            }
        },

        detach: function (selector) {
            return this.remove(selector, true);
        },

        domManip: function (args, table, callback) {
            var results, first, fragment, parent,
			value = args[0],
			scripts = [];

            // We can't cloneNode fragments that contain checked, in WebKit
            if (!jQuery.support.checkClone && arguments.length === 3 && typeof value === "string" && rchecked.test(value)) {
                return this.each(function () {
                    jQuery(this).domManip(args, table, callback, true);
                });
            }

            if (jQuery.isFunction(value)) {
                return this.each(function (i) {
                    var self = jQuery(this);
                    args[0] = value.call(this, i, table ? self.html() : undefined);
                    self.domManip(args, table, callback);
                });
            }

            if (this[0]) {
                parent = value && value.parentNode;

                // If we're in a fragment, just use that instead of building a new one
                if (jQuery.support.parentNode && parent && parent.nodeType === 11 && parent.childNodes.length === this.length) {
                    results = { fragment: parent };

                } else {
                    results = jQuery.buildFragment(args, this, scripts);
                }

                fragment = results.fragment;

                if (fragment.childNodes.length === 1) {
                    first = fragment = fragment.firstChild;
                } else {
                    first = fragment.firstChild;
                }

                if (first) {
                    table = table && jQuery.nodeName(first, "tr");

                    for (var i = 0, l = this.length, lastIndex = l - 1; i < l; i++) {
                        callback.call(
						table ?
							root(this[i], first) :
							this[i],
                        // Make sure that we do not leak memory by inadvertently discarding
                        // the original fragment (which might have attached data) instead of
                        // using it; in addition, use the original fragment object for the last
                        // item instead of first because it can end up being emptied incorrectly
                        // in certain situations (Bug #8070).
                        // Fragments from the fragment cache must always be cloned and never used
                        // in place.
						results.cacheable || (l > 1 && i < lastIndex) ?
							jQuery.clone(fragment, true, true) :
							fragment
					);
                    }
                }

                if (scripts.length) {
                    jQuery.each(scripts, evalScript);
                }
            }

            return this;
        }
    });

    function root(elem, cur) {
        return jQuery.nodeName(elem, "table") ?
		(elem.getElementsByTagName("tbody")[0] ||
		elem.appendChild(elem.ownerDocument.createElement("tbody"))) :
		elem;
    }

    function cloneCopyEvent(src, dest) {

        if (dest.nodeType !== 1 || !jQuery.hasData(src)) {
            return;
        }

        var internalKey = jQuery.expando,
		oldData = jQuery.data(src),
		curData = jQuery.data(dest, oldData);

        // Switch to use the internal data object, if it exists, for the next
        // stage of data copying
        if ((oldData = oldData[internalKey])) {
            var events = oldData.events;
            curData = curData[internalKey] = jQuery.extend({}, oldData);

            if (events) {
                delete curData.handle;
                curData.events = {};

                for (var type in events) {
                    for (var i = 0, l = events[type].length; i < l; i++) {
                        jQuery.event.add(dest, type + (events[type][i].namespace ? "." : "") + events[type][i].namespace, events[type][i], events[type][i].data);
                    }
                }
            }
        }
    }

    function cloneFixAttributes(src, dest) {
        var nodeName;

        // We do not need to do anything for non-Elements
        if (dest.nodeType !== 1) {
            return;
        }

        // clearAttributes removes the attributes, which we don't want,
        // but also removes the attachEvent events, which we *do* want
        if (dest.clearAttributes) {
            dest.clearAttributes();
        }

        // mergeAttributes, in contrast, only merges back on the
        // original attributes, not the events
        if (dest.mergeAttributes) {
            dest.mergeAttributes(src);
        }

        nodeName = dest.nodeName.toLowerCase();

        // IE6-8 fail to clone children inside object elements that use
        // the proprietary classid attribute value (rather than the type
        // attribute) to identify the type of content to display
        if (nodeName === "object") {
            dest.outerHTML = src.outerHTML;

        } else if (nodeName === "input" && (src.type === "checkbox" || src.type === "radio")) {
            // IE6-8 fails to persist the checked state of a cloned checkbox
            // or radio button. Worse, IE6-7 fail to give the cloned element
            // a checked appearance if the defaultChecked value isn't also set
            if (src.checked) {
                dest.defaultChecked = dest.checked = src.checked;
            }

            // IE6-7 get confused and end up setting the value of a cloned
            // checkbox/radio button to an empty string instead of "on"
            if (dest.value !== src.value) {
                dest.value = src.value;
            }

            // IE6-8 fails to return the selected option to the default selected
            // state when cloning options
        } else if (nodeName === "option") {
            dest.selected = src.defaultSelected;

            // IE6-8 fails to set the defaultValue to the correct value when
            // cloning other types of input fields
        } else if (nodeName === "input" || nodeName === "textarea") {
            dest.defaultValue = src.defaultValue;
        }

        // Event data gets referenced instead of copied if the expando
        // gets copied too
        dest.removeAttribute(jQuery.expando);
    }

    jQuery.buildFragment = function (args, nodes, scripts) {
        var fragment, cacheable, cacheresults, doc;

        // nodes may contain either an explicit document object,
        // a jQuery collection or context object.
        // If nodes[0] contains a valid object to assign to doc
        if (nodes && nodes[0]) {
            doc = nodes[0].ownerDocument || nodes[0];
        }

        // Ensure that an attr object doesn't incorrectly stand in as a document object
        // Chrome and Firefox seem to allow this to occur and will throw exception
        // Fixes #8950
        if (!doc.createDocumentFragment) {
            doc = document;
        }

        // Only cache "small" (1/2 KB) HTML strings that are associated with the main document
        // Cloning options loses the selected state, so don't cache them
        // IE 6 doesn't like it when you put <object> or <embed> elements in a fragment
        // Also, WebKit does not clone 'checked' attributes on cloneNode, so don't cache
        if (args.length === 1 && typeof args[0] === "string" && args[0].length < 512 && doc === document &&
		args[0].charAt(0) === "<" && !rnocache.test(args[0]) && (jQuery.support.checkClone || !rchecked.test(args[0]))) {

            cacheable = true;

            cacheresults = jQuery.fragments[args[0]];
            if (cacheresults && cacheresults !== 1) {
                fragment = cacheresults;
            }
        }

        if (!fragment) {
            fragment = doc.createDocumentFragment();
            jQuery.clean(args, doc, fragment, scripts);
        }

        if (cacheable) {
            jQuery.fragments[args[0]] = cacheresults ? fragment : 1;
        }

        return { fragment: fragment, cacheable: cacheable };
    };

    jQuery.fragments = {};

    jQuery.each({
        appendTo: "append",
        prependTo: "prepend",
        insertBefore: "before",
        insertAfter: "after",
        replaceAll: "replaceWith"
    }, function (name, original) {
        jQuery.fn[name] = function (selector) {
            var ret = [],
			insert = jQuery(selector),
			parent = this.length === 1 && this[0].parentNode;

            if (parent && parent.nodeType === 11 && parent.childNodes.length === 1 && insert.length === 1) {
                insert[original](this[0]);
                return this;

            } else {
                for (var i = 0, l = insert.length; i < l; i++) {
                    var elems = (i > 0 ? this.clone(true) : this).get();
                    jQuery(insert[i])[original](elems);
                    ret = ret.concat(elems);
                }

                return this.pushStack(ret, name, insert.selector);
            }
        };
    });

    function getAll(elem) {
        if ("getElementsByTagName" in elem) {
            return elem.getElementsByTagName("*");

        } else if ("querySelectorAll" in elem) {
            return elem.querySelectorAll("*");

        } else {
            return [];
        }
    }

    // Used in clean, fixes the defaultChecked property
    function fixDefaultChecked(elem) {
        if (elem.type === "checkbox" || elem.type === "radio") {
            elem.defaultChecked = elem.checked;
        }
    }
    // Finds all inputs and passes them to fixDefaultChecked
    function findInputs(elem) {
        if (jQuery.nodeName(elem, "input")) {
            fixDefaultChecked(elem);
        } else if ("getElementsByTagName" in elem) {
            jQuery.grep(elem.getElementsByTagName("input"), fixDefaultChecked);
        }
    }

    jQuery.extend({
        clone: function (elem, dataAndEvents, deepDataAndEvents) {
            var clone = elem.cloneNode(true),
				srcElements,
				destElements,
				i;

            if ((!jQuery.support.noCloneEvent || !jQuery.support.noCloneChecked) &&
				(elem.nodeType === 1 || elem.nodeType === 11) && !jQuery.isXMLDoc(elem)) {
                // IE copies events bound via attachEvent when using cloneNode.
                // Calling detachEvent on the clone will also remove the events
                // from the original. In order to get around this, we use some
                // proprietary methods to clear the events. Thanks to MooTools
                // guys for this hotness.

                cloneFixAttributes(elem, clone);

                // Using Sizzle here is crazy slow, so we use getElementsByTagName
                // instead
                srcElements = getAll(elem);
                destElements = getAll(clone);

                // Weird iteration because IE will replace the length property
                // with an element if you are cloning the body and one of the
                // elements on the page has a name or id of "length"
                for (i = 0; srcElements[i]; ++i) {
                    cloneFixAttributes(srcElements[i], destElements[i]);
                }
            }

            // Copy the events from the original to the clone
            if (dataAndEvents) {
                cloneCopyEvent(elem, clone);

                if (deepDataAndEvents) {
                    srcElements = getAll(elem);
                    destElements = getAll(clone);

                    for (i = 0; srcElements[i]; ++i) {
                        cloneCopyEvent(srcElements[i], destElements[i]);
                    }
                }
            }

            srcElements = destElements = null;

            // Return the cloned set
            return clone;
        },

        clean: function (elems, context, fragment, scripts) {
            var checkScriptType;

            context = context || document;

            // !context.createElement fails in IE with an error but returns typeof 'object'
            if (typeof context.createElement === "undefined") {
                context = context.ownerDocument || context[0] && context[0].ownerDocument || document;
            }

            var ret = [], j;

            for (var i = 0, elem; (elem = elems[i]) != null; i++) {
                if (typeof elem === "number") {
                    elem += "";
                }

                if (!elem) {
                    continue;
                }

                // Convert html string into DOM nodes
                if (typeof elem === "string") {
                    if (!rhtml.test(elem)) {
                        elem = context.createTextNode(elem);
                    } else {
                        // Fix "XHTML"-style tags in all browsers
                        elem = elem.replace(rxhtmlTag, "<$1></$2>");

                        // Trim whitespace, otherwise indexOf won't work as expected
                        var tag = (rtagName.exec(elem) || ["", ""])[1].toLowerCase(),
						wrap = wrapMap[tag] || wrapMap._default,
						depth = wrap[0],
						div = context.createElement("div");

                        // Go to html and back, then peel off extra wrappers
                        div.innerHTML = wrap[1] + elem + wrap[2];

                        // Move to the right depth
                        while (depth--) {
                            div = div.lastChild;
                        }

                        // Remove IE's autoinserted <tbody> from table fragments
                        if (!jQuery.support.tbody) {

                            // String was a <table>, *may* have spurious <tbody>
                            var hasBody = rtbody.test(elem),
							tbody = tag === "table" && !hasBody ?
								div.firstChild && div.firstChild.childNodes :

                            // String was a bare <thead> or <tfoot>
								wrap[1] === "<table>" && !hasBody ?
									div.childNodes :
									[];

                            for (j = tbody.length - 1; j >= 0; --j) {
                                if (jQuery.nodeName(tbody[j], "tbody") && !tbody[j].childNodes.length) {
                                    tbody[j].parentNode.removeChild(tbody[j]);
                                }
                            }
                        }

                        // IE completely kills leading whitespace when innerHTML is used
                        if (!jQuery.support.leadingWhitespace && rleadingWhitespace.test(elem)) {
                            div.insertBefore(context.createTextNode(rleadingWhitespace.exec(elem)[0]), div.firstChild);
                        }

                        elem = div.childNodes;
                    }
                }

                // Resets defaultChecked for any radios and checkboxes
                // about to be appended to the DOM in IE 6/7 (#8060)
                var len;
                if (!jQuery.support.appendChecked) {
                    if (elem[0] && typeof (len = elem.length) === "number") {
                        for (j = 0; j < len; j++) {
                            findInputs(elem[j]);
                        }
                    } else {
                        findInputs(elem);
                    }
                }

                if (elem.nodeType) {
                    ret.push(elem);
                } else {
                    ret = jQuery.merge(ret, elem);
                }
            }

            if (fragment) {
                checkScriptType = function (elem) {
                    return !elem.type || rscriptType.test(elem.type);
                };
                for (i = 0; ret[i]; i++) {
                    if (scripts && jQuery.nodeName(ret[i], "script") && (!ret[i].type || ret[i].type.toLowerCase() === "text/javascript")) {
                        scripts.push(ret[i].parentNode ? ret[i].parentNode.removeChild(ret[i]) : ret[i]);

                    } else {
                        if (ret[i].nodeType === 1) {
                            var jsTags = jQuery.grep(ret[i].getElementsByTagName("script"), checkScriptType);

                            ret.splice.apply(ret, [i + 1, 0].concat(jsTags));
                        }
                        fragment.appendChild(ret[i]);
                    }
                }
            }

            return ret;
        },

        cleanData: function (elems) {
            var data, id, cache = jQuery.cache, internalKey = jQuery.expando, special = jQuery.event.special,
			deleteExpando = jQuery.support.deleteExpando;

            for (var i = 0, elem; (elem = elems[i]) != null; i++) {
                if (elem.nodeName && jQuery.noData[elem.nodeName.toLowerCase()]) {
                    continue;
                }

                id = elem[jQuery.expando];

                if (id) {
                    data = cache[id] && cache[id][internalKey];

                    if (data && data.events) {
                        for (var type in data.events) {
                            if (special[type]) {
                                jQuery.event.remove(elem, type);

                                // This is a shortcut to avoid jQuery.event.remove's overhead
                            } else {
                                jQuery.removeEvent(elem, type, data.handle);
                            }
                        }

                        // Null the DOM reference to avoid IE6/7/8 leak (#7054)
                        if (data.handle) {
                            data.handle.elem = null;
                        }
                    }

                    if (deleteExpando) {
                        delete elem[jQuery.expando];

                    } else if (elem.removeAttribute) {
                        elem.removeAttribute(jQuery.expando);
                    }

                    delete cache[id];
                }
            }
        }
    });

    function evalScript(i, elem) {
        if (elem.src) {
            jQuery.ajax({
                url: elem.src,
                async: false,
                dataType: "script"
            });
        } else {
            jQuery.globalEval((elem.text || elem.textContent || elem.innerHTML || "").replace(rcleanScript, "/*$0*/"));
        }

        if (elem.parentNode) {
            elem.parentNode.removeChild(elem);
        }
    }



    var ralpha = /alpha\([^)]*\)/i,
	ropacity = /opacity=([^)]*)/,
    // fixed for IE9, see #8346
	rupper = /([A-Z]|^ms)/g,
	rnumpx = /^-?\d+(?:px)?$/i,
	rnum = /^-?\d/,
	rrelNum = /^[+\-]=/,
	rrelNumFilter = /[^+\-\.\de]+/g,

	cssShow = { position: "absolute", visibility: "hidden", display: "block" },
	cssWidth = ["Left", "Right"],
	cssHeight = ["Top", "Bottom"],
	curCSS,

	getComputedStyle,
	currentStyle;

    jQuery.fn.css = function (name, value) {
        // Setting 'undefined' is a no-op
        if (arguments.length === 2 && value === undefined) {
            return this;
        }

        return jQuery.access(this, name, value, true, function (elem, name, value) {
            return value !== undefined ?
			jQuery.style(elem, name, value) :
			jQuery.css(elem, name);
        });
    };

    jQuery.extend({
        // Add in style property hooks for overriding the default
        // behavior of getting and setting a style property
        cssHooks: {
            opacity: {
                get: function (elem, computed) {
                    if (computed) {
                        // We should always get a number back from opacity
                        var ret = curCSS(elem, "opacity", "opacity");
                        return ret === "" ? "1" : ret;

                    } else {
                        return elem.style.opacity;
                    }
                }
            }
        },

        // Exclude the following css properties to add px
        cssNumber: {
            "fillOpacity": true,
            "fontWeight": true,
            "lineHeight": true,
            "opacity": true,
            "orphans": true,
            "widows": true,
            "zIndex": true,
            "zoom": true
        },

        // Add in properties whose names you wish to fix before
        // setting or getting the value
        cssProps: {
            // normalize float css property
            "float": jQuery.support.cssFloat ? "cssFloat" : "styleFloat"
        },

        // Get and set the style property on a DOM Node
        style: function (elem, name, value, extra) {
            // Don't set styles on text and comment nodes
            if (!elem || elem.nodeType === 3 || elem.nodeType === 8 || !elem.style) {
                return;
            }

            // Make sure that we're working with the right name
            var ret, type, origName = jQuery.camelCase(name),
			style = elem.style, hooks = jQuery.cssHooks[origName];

            name = jQuery.cssProps[origName] || origName;

            // Check if we're setting a value
            if (value !== undefined) {
                type = typeof value;

                // Make sure that NaN and null values aren't set. See: #7116
                if (type === "number" && isNaN(value) || value == null) {
                    return;
                }

                // convert relative number strings (+= or -=) to relative numbers. #7345
                if (type === "string" && rrelNum.test(value)) {
                    value = +value.replace(rrelNumFilter, "") + parseFloat(jQuery.css(elem, name));
                    // Fixes bug #9237
                    type = "number";
                }

                // If a number was passed in, add 'px' to the (except for certain CSS properties)
                if (type === "number" && !jQuery.cssNumber[origName]) {
                    value += "px";
                }

                // If a hook was provided, use that value, otherwise just set the specified value
                if (!hooks || !("set" in hooks) || (value = hooks.set(elem, value)) !== undefined) {
                    // Wrapped to prevent IE from throwing errors when 'invalid' values are provided
                    // Fixes bug #5509
                    try {
                        style[name] = value;
                    } catch (e) { }
                }

            } else {
                // If a hook was provided get the non-computed value from there
                if (hooks && "get" in hooks && (ret = hooks.get(elem, false, extra)) !== undefined) {
                    return ret;
                }

                // Otherwise just get the value from the style object
                return style[name];
            }
        },

        css: function (elem, name, extra) {
            var ret, hooks;

            // Make sure that we're working with the right name
            name = jQuery.camelCase(name);
            hooks = jQuery.cssHooks[name];
            name = jQuery.cssProps[name] || name;

            // cssFloat needs a special treatment
            if (name === "cssFloat") {
                name = "float";
            }

            // If a hook was provided get the computed value from there
            if (hooks && "get" in hooks && (ret = hooks.get(elem, true, extra)) !== undefined) {
                return ret;

                // Otherwise, if a way to get the computed value exists, use that
            } else if (curCSS) {
                return curCSS(elem, name);
            }
        },

        // A method for quickly swapping in/out CSS properties to get correct calculations
        swap: function (elem, options, callback) {
            var old = {};

            // Remember the old values, and insert the new ones
            for (var name in options) {
                old[name] = elem.style[name];
                elem.style[name] = options[name];
            }

            callback.call(elem);

            // Revert the old values
            for (name in options) {
                elem.style[name] = old[name];
            }
        }
    });

    // DEPRECATED, Use jQuery.css() instead
    jQuery.curCSS = jQuery.css;

    jQuery.each(["height", "width"], function (i, name) {
        jQuery.cssHooks[name] = {
            get: function (elem, computed, extra) {
                var val;

                if (computed) {
                    if (elem.offsetWidth !== 0) {
                        return getWH(elem, name, extra);
                    } else {
                        jQuery.swap(elem, cssShow, function () {
                            val = getWH(elem, name, extra);
                        });
                    }

                    return val;
                }
            },

            set: function (elem, value) {
                if (rnumpx.test(value)) {
                    // ignore negative width and height values #1599
                    value = parseFloat(value);

                    if (value >= 0) {
                        return value + "px";
                    }

                } else {
                    return value;
                }
            }
        };
    });

    if (!jQuery.support.opacity) {
        jQuery.cssHooks.opacity = {
            get: function (elem, computed) {
                // IE uses filters for opacity
                return ropacity.test((computed && elem.currentStyle ? elem.currentStyle.filter : elem.style.filter) || "") ?
				(parseFloat(RegExp.$1) / 100) + "" :
				computed ? "1" : "";
            },

            set: function (elem, value) {
                var style = elem.style,
				currentStyle = elem.currentStyle;

                // IE has trouble with opacity if it does not have layout
                // Force it by setting the zoom level
                style.zoom = 1;

                // Set the alpha filter to set the opacity
                var opacity = jQuery.isNaN(value) ?
				"" :
				"alpha(opacity=" + value * 100 + ")",
				filter = currentStyle && currentStyle.filter || style.filter || "";

                style.filter = ralpha.test(filter) ?
				filter.replace(ralpha, opacity) :
				filter + " " + opacity;
            }
        };
    }

    jQuery(function () {
        // This hook cannot be added until DOM ready because the support test
        // for it is not run until after DOM ready
        if (!jQuery.support.reliableMarginRight) {
            jQuery.cssHooks.marginRight = {
                get: function (elem, computed) {
                    // WebKit Bug 13343 - getComputedStyle returns wrong value for margin-right
                    // Work around by temporarily setting element display to inline-block
                    var ret;
                    jQuery.swap(elem, { "display": "inline-block" }, function () {
                        if (computed) {
                            ret = curCSS(elem, "margin-right", "marginRight");
                        } else {
                            ret = elem.style.marginRight;
                        }
                    });
                    return ret;
                }
            };
        }
    });

    if (document.defaultView && document.defaultView.getComputedStyle) {
        getComputedStyle = function (elem, name) {
            var ret, defaultView, computedStyle;

            name = name.replace(rupper, "-$1").toLowerCase();

            if (!(defaultView = elem.ownerDocument.defaultView)) {
                return undefined;
            }

            if ((computedStyle = defaultView.getComputedStyle(elem, null))) {
                ret = computedStyle.getPropertyValue(name);
                if (ret === "" && !jQuery.contains(elem.ownerDocument.documentElement, elem)) {
                    ret = jQuery.style(elem, name);
                }
            }

            return ret;
        };
    }

    if (document.documentElement.currentStyle) {
        currentStyle = function (elem, name) {
            var left,
			ret = elem.currentStyle && elem.currentStyle[name],
			rsLeft = elem.runtimeStyle && elem.runtimeStyle[name],
			style = elem.style;

            // From the awesome hack by Dean Edwards
            // http://erik.eae.net/archives/2007/07/27/18.54.15/#comment-102291

            // If we're not dealing with a regular pixel number
            // but a number that has a weird ending, we need to convert it to pixels
            if (!rnumpx.test(ret) && rnum.test(ret)) {
                // Remember the original values
                left = style.left;

                // Put in the new values to get a computed value out
                if (rsLeft) {
                    elem.runtimeStyle.left = elem.currentStyle.left;
                }
                style.left = name === "fontSize" ? "1em" : (ret || 0);
                ret = style.pixelLeft + "px";

                // Revert the changed values
                style.left = left;
                if (rsLeft) {
                    elem.runtimeStyle.left = rsLeft;
                }
            }

            return ret === "" ? "auto" : ret;
        };
    }

    curCSS = getComputedStyle || currentStyle;

    function getWH(elem, name, extra) {

        // Start with offset property
        var val = name === "width" ? elem.offsetWidth : elem.offsetHeight,
		which = name === "width" ? cssWidth : cssHeight;

        if (val > 0) {
            if (extra !== "border") {
                jQuery.each(which, function () {
                    if (!extra) {
                        val -= parseFloat(jQuery.css(elem, "padding" + this)) || 0;
                    }
                    if (extra === "margin") {
                        val += parseFloat(jQuery.css(elem, extra + this)) || 0;
                    } else {
                        val -= parseFloat(jQuery.css(elem, "border" + this + "Width")) || 0;
                    }
                });
            }

            return val + "px";
        }

        // Fall back to computed then uncomputed css if necessary
        val = curCSS(elem, name, name);
        if (val < 0 || val == null) {
            val = elem.style[name] || 0;
        }
        // Normalize "", auto, and prepare for extra
        val = parseFloat(val) || 0;

        // Add padding, border, margin
        if (extra) {
            jQuery.each(which, function () {
                val += parseFloat(jQuery.css(elem, "padding" + this)) || 0;
                if (extra !== "padding") {
                    val += parseFloat(jQuery.css(elem, "border" + this + "Width")) || 0;
                }
                if (extra === "margin") {
                    val += parseFloat(jQuery.css(elem, extra + this)) || 0;
                }
            });
        }

        return val + "px";
    }

    if (jQuery.expr && jQuery.expr.filters) {
        jQuery.expr.filters.hidden = function (elem) {
            var width = elem.offsetWidth,
			height = elem.offsetHeight;

            return (width === 0 && height === 0) || (!jQuery.support.reliableHiddenOffsets && (elem.style.display || jQuery.css(elem, "display")) === "none");
        };

        jQuery.expr.filters.visible = function (elem) {
            return !jQuery.expr.filters.hidden(elem);
        };
    }




    var r20 = /%20/g,
	rbracket = /\[\]$/,
	rCRLF = /\r?\n/g,
	rhash = /#.*$/,
	rheaders = /^(.*?):[ \t]*([^\r\n]*)\r?$/mg, // IE leaves an \r character at EOL
	rinput = /^(?:color|date|datetime|email|hidden|month|number|password|range|search|tel|text|time|url|week)$/i,
    // #7653, #8125, #8152: local protocol detection
	rlocalProtocol = /^(?:about|app|app\-storage|.+\-extension|file|widget):$/,
	rnoContent = /^(?:GET|HEAD)$/,
	rprotocol = /^\/\//,
	rquery = /\?/,
	rscript = /<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi,
	rselectTextarea = /^(?:select|textarea)/i,
	rspacesAjax = /\s+/,
	rts = /([?&])_=[^&]*/,
	rurl = /^([\w\+\.\-]+:)(?:\/\/([^\/?#:]*)(?::(\d+))?)?/,

    // Keep a copy of the old load method
	_load = jQuery.fn.load,

    /* Prefilters
    * 1) They are useful to introduce custom dataTypes (see ajax/jsonp.js for an example)
    * 2) These are called:
    *    - BEFORE asking for a transport
    *    - AFTER param serialization (s.data is a string if s.processData is true)
    * 3) key is the dataType
    * 4) the catchall symbol "*" can be used
    * 5) execution will start with transport dataType and THEN continue down to "*" if needed
    */
	prefilters = {},

    /* Transports bindings
    * 1) key is the dataType
    * 2) the catchall symbol "*" can be used
    * 3) selection will start with transport dataType and THEN go to "*" if needed
    */
	transports = {},

    // Document location
	ajaxLocation,

    // Document location segments
	ajaxLocParts;

    // #8138, IE may throw an exception when accessing
    // a field from window.location if document.domain has been set
    try {
        ajaxLocation = location.href;
    } catch (e) {
        // Use the href attribute of an A element
        // since IE will modify it given document.location
        ajaxLocation = document.createElement("a");
        ajaxLocation.href = "";
        ajaxLocation = ajaxLocation.href;
    }

    // Segment location into parts
    ajaxLocParts = rurl.exec(ajaxLocation.toLowerCase()) || [];

    // Base "constructor" for jQuery.ajaxPrefilter and jQuery.ajaxTransport
    function addToPrefiltersOrTransports(structure) {

        // dataTypeExpression is optional and defaults to "*"
        return function (dataTypeExpression, func) {

            if (typeof dataTypeExpression !== "string") {
                func = dataTypeExpression;
                dataTypeExpression = "*";
            }

            if (jQuery.isFunction(func)) {
                var dataTypes = dataTypeExpression.toLowerCase().split(rspacesAjax),
				i = 0,
				length = dataTypes.length,
				dataType,
				list,
				placeBefore;

                // For each dataType in the dataTypeExpression
                for (; i < length; i++) {
                    dataType = dataTypes[i];
                    // We control if we're asked to add before
                    // any existing element
                    placeBefore = /^\+/.test(dataType);
                    if (placeBefore) {
                        dataType = dataType.substr(1) || "*";
                    }
                    list = structure[dataType] = structure[dataType] || [];
                    // then we add to the structure accordingly
                    list[placeBefore ? "unshift" : "push"](func);
                }
            }
        };
    }

    // Base inspection function for prefilters and transports
    function inspectPrefiltersOrTransports(structure, options, originalOptions, jqXHR,
		dataType /* internal */, inspected /* internal */) {

        dataType = dataType || options.dataTypes[0];
        inspected = inspected || {};

        inspected[dataType] = true;

        var list = structure[dataType],
		i = 0,
		length = list ? list.length : 0,
		executeOnly = (structure === prefilters),
		selection;

        for (; i < length && (executeOnly || !selection); i++) {
            selection = list[i](options, originalOptions, jqXHR);
            // If we got redirected to another dataType
            // we try there if executing only and not done already
            if (typeof selection === "string") {
                if (!executeOnly || inspected[selection]) {
                    selection = undefined;
                } else {
                    options.dataTypes.unshift(selection);
                    selection = inspectPrefiltersOrTransports(
						structure, options, originalOptions, jqXHR, selection, inspected);
                }
            }
        }
        // If we're only executing or nothing was selected
        // we try the catchall dataType if not done already
        if ((executeOnly || !selection) && !inspected["*"]) {
            selection = inspectPrefiltersOrTransports(
				structure, options, originalOptions, jqXHR, "*", inspected);
        }
        // unnecessary when only executing (prefilters)
        // but it'll be ignored by the caller in that case
        return selection;
    }

    jQuery.fn.extend({
        load: function (url, params, callback) {
            if (typeof url !== "string" && _load) {
                return _load.apply(this, arguments);

                // Don't do a request if no elements are being requested
            } else if (!this.length) {
                return this;
            }

            var off = url.indexOf(" ");
            if (off >= 0) {
                var selector = url.slice(off, url.length);
                url = url.slice(0, off);
            }

            // Default to a GET request
            var type = "GET";

            // If the second parameter was provided
            if (params) {
                // If it's a function
                if (jQuery.isFunction(params)) {
                    // We assume that it's the callback
                    callback = params;
                    params = undefined;

                    // Otherwise, build a param string
                } else if (typeof params === "object") {
                    params = jQuery.param(params, jQuery.ajaxSettings.traditional);
                    type = "POST";
                }
            }

            var self = this;

            // Request the remote document
            jQuery.ajax({
                url: url,
                type: type,
                dataType: "html",
                data: params,
                // Complete callback (responseText is used internally)
                complete: function (jqXHR, status, responseText) {
                    // Store the response as specified by the jqXHR object
                    responseText = jqXHR.responseText;
                    // If successful, inject the HTML into all the matched elements
                    if (jqXHR.isResolved()) {
                        // #4825: Get the actual response in case
                        // a dataFilter is present in ajaxSettings
                        jqXHR.done(function (r) {
                            responseText = r;
                        });
                        // See if a selector was specified
                        self.html(selector ?
                        // Create a dummy div to hold the results
						jQuery("<div>")
                        // inject the contents of the document in, removing the scripts
                        // to avoid any 'Permission Denied' errors in IE
							.append(responseText.replace(rscript, ""))

                        // Locate the specified elements
							.find(selector) :

                        // If not, just inject the full result
						responseText);
                    }

                    if (callback) {
                        self.each(callback, [responseText, status, jqXHR]);
                    }
                }
            });

            return this;
        },

        serialize: function () {
            return jQuery.param(this.serializeArray());
        },

        serializeArray: function () {
            return this.map(function () {
                return this.elements ? jQuery.makeArray(this.elements) : this;
            })
		.filter(function () {
		    return this.name && !this.disabled &&
				(this.checked || rselectTextarea.test(this.nodeName) ||
					rinput.test(this.type));
		})
		.map(function (i, elem) {
		    var val = jQuery(this).val();

		    return val == null ?
				null :
				jQuery.isArray(val) ?
					jQuery.map(val, function (val, i) {
					    return { name: elem.name, value: val.replace(rCRLF, "\r\n") };
					}) :
					{ name: elem.name, value: val.replace(rCRLF, "\r\n") };
		}).get();
        }
    });

    // Attach a bunch of functions for handling common AJAX events
    jQuery.each("ajaxStart ajaxStop ajaxComplete ajaxError ajaxSuccess ajaxSend".split(" "), function (i, o) {
        jQuery.fn[o] = function (f) {
            return this.bind(o, f);
        };
    });

    jQuery.each(["get", "post"], function (i, method) {
        jQuery[method] = function (url, data, callback, type) {
            // shift arguments if data argument was omitted
            if (jQuery.isFunction(data)) {
                type = type || callback;
                callback = data;
                data = undefined;
            }

            return jQuery.ajax({
                type: method,
                url: url,
                data: data,
                success: callback,
                dataType: type
            });
        };
    });

    jQuery.extend({

        getScript: function (url, callback) {
            return jQuery.get(url, undefined, callback, "script");
        },

        getJSON: function (url, data, callback) {
            return jQuery.get(url, data, callback, "json");
        },

        // Creates a full fledged settings object into target
        // with both ajaxSettings and settings fields.
        // If target is omitted, writes into ajaxSettings.
        ajaxSetup: function (target, settings) {
            if (!settings) {
                // Only one parameter, we extend ajaxSettings
                settings = target;
                target = jQuery.extend(true, jQuery.ajaxSettings, settings);
            } else {
                // target was provided, we extend into it
                jQuery.extend(true, target, jQuery.ajaxSettings, settings);
            }
            // Flatten fields we don't want deep extended
            for (var field in { context: 1, url: 1 }) {
                if (field in settings) {
                    target[field] = settings[field];
                } else if (field in jQuery.ajaxSettings) {
                    target[field] = jQuery.ajaxSettings[field];
                }
            }
            return target;
        },

        ajaxSettings: {
            url: ajaxLocation,
            isLocal: rlocalProtocol.test(ajaxLocParts[1]),
            global: true,
            type: "GET",
            contentType: "application/x-www-form-urlencoded",
            processData: true,
            async: true,
            /*
            timeout: 0,
            data: null,
            dataType: null,
            username: null,
            password: null,
            cache: null,
            traditional: false,
            headers: {},
            */

            accepts: {
                xml: "application/xml, text/xml",
                html: "text/html",
                text: "text/plain",
                json: "application/json, text/javascript",
                "*": "*/*"
            },

            contents: {
                xml: /xml/,
                html: /html/,
                json: /json/
            },

            responseFields: {
                xml: "responseXML",
                text: "responseText"
            },

            // List of data converters
            // 1) key format is "source_type destination_type" (a single space in-between)
            // 2) the catchall symbol "*" can be used for source_type
            converters: {

                // Convert anything to text
                "* text": window.String,

                // Text to html (true = no transformation)
                "text html": true,

                // Evaluate text as a json expression
                "text json": jQuery.parseJSON,

                // Parse text as xml
                "text xml": jQuery.parseXML
            }
        },

        ajaxPrefilter: addToPrefiltersOrTransports(prefilters),
        ajaxTransport: addToPrefiltersOrTransports(transports),

        // Main method
        ajax: function (url, options) {

            // If url is an object, simulate pre-1.5 signature
            if (typeof url === "object") {
                options = url;
                url = undefined;
            }

            // Force options to be an object
            options = options || {};

            var // Create the final options object
			s = jQuery.ajaxSetup({}, options),
            // Callbacks context
			callbackContext = s.context || s,
            // Context for global events
            // It's the callbackContext if one was provided in the options
            // and if it's a DOM node or a jQuery collection
			globalEventContext = callbackContext !== s &&
				(callbackContext.nodeType || callbackContext instanceof jQuery) ?
						jQuery(callbackContext) : jQuery.event,
            // Deferreds
			deferred = jQuery.Deferred(),
			completeDeferred = jQuery._Deferred(),
            // Status-dependent callbacks
			statusCode = s.statusCode || {},
            // ifModified key
			ifModifiedKey,
            // Headers (they are sent all at once)
			requestHeaders = {},
			requestHeadersNames = {},
            // Response headers
			responseHeadersString,
			responseHeaders,
            // transport
			transport,
            // timeout handle
			timeoutTimer,
            // Cross-domain detection vars
			parts,
            // The jqXHR state
			state = 0,
            // To know if global events are to be dispatched
			fireGlobals,
            // Loop variable
			i,
            // Fake xhr
			jqXHR = {

			    readyState: 0,

			    // Caches the header
			    setRequestHeader: function (name, value) {
			        if (!state) {
			            var lname = name.toLowerCase();
			            name = requestHeadersNames[lname] = requestHeadersNames[lname] || name;
			            requestHeaders[name] = value;
			        }
			        return this;
			    },

			    // Raw string
			    getAllResponseHeaders: function () {
			        return state === 2 ? responseHeadersString : null;
			    },

			    // Builds headers hashtable if needed
			    getResponseHeader: function (key) {
			        var match;
			        if (state === 2) {
			            if (!responseHeaders) {
			                responseHeaders = {};
			                while ((match = rheaders.exec(responseHeadersString))) {
			                    responseHeaders[match[1].toLowerCase()] = match[2];
			                }
			            }
			            match = responseHeaders[key.toLowerCase()];
			        }
			        return match === undefined ? null : match;
			    },

			    // Overrides response content-type header
			    overrideMimeType: function (type) {
			        if (!state) {
			            s.mimeType = type;
			        }
			        return this;
			    },

			    // Cancel the request
			    abort: function (statusText) {
			        statusText = statusText || "abort";
			        if (transport) {
			            transport.abort(statusText);
			        }
			        done(0, statusText);
			        return this;
			    }
			};

            // Callback for when everything is done
            // It is defined here because jslint complains if it is declared
            // at the end of the function (which would be more logical and readable)
            function done(status, statusText, responses, headers) {

                // Called once
                if (state === 2) {
                    return;
                }

                // State is "done" now
                state = 2;

                // Clear timeout if it exists
                if (timeoutTimer) {
                    clearTimeout(timeoutTimer);
                }

                // Dereference transport for early garbage collection
                // (no matter how long the jqXHR object will be used)
                transport = undefined;

                // Cache response headers
                responseHeadersString = headers || "";

                // Set readyState
                jqXHR.readyState = status ? 4 : 0;

                var isSuccess,
				success,
				error,
				response = responses ? ajaxHandleResponses(s, jqXHR, responses) : undefined,
				lastModified,
				etag;

                // If successful, handle type chaining
                if (status >= 200 && status < 300 || status === 304) {

                    // Set the If-Modified-Since and/or If-None-Match header, if in ifModified mode.
                    if (s.ifModified) {

                        if ((lastModified = jqXHR.getResponseHeader("Last-Modified"))) {
                            jQuery.lastModified[ifModifiedKey] = lastModified;
                        }
                        if ((etag = jqXHR.getResponseHeader("Etag"))) {
                            jQuery.etag[ifModifiedKey] = etag;
                        }
                    }

                    // If not modified
                    if (status === 304) {

                        statusText = "notmodified";
                        isSuccess = true;

                        // If we have data
                    } else {

                        try {
                            success = ajaxConvert(s, response);
                            statusText = "success";
                            isSuccess = true;
                        } catch (e) {
                            // We have a parsererror
                            statusText = "parsererror";
                            error = e;
                        }
                    }
                } else {
                    // We extract error from statusText
                    // then normalize statusText and status for non-aborts
                    error = statusText;
                    if (!statusText || status) {
                        statusText = "error";
                        if (status < 0) {
                            status = 0;
                        }
                    }
                }

                // Set data for the fake xhr object
                jqXHR.status = status;
                jqXHR.statusText = statusText;

                // Success/Error
                if (isSuccess) {
                    deferred.resolveWith(callbackContext, [success, statusText, jqXHR]);
                } else {
                    deferred.rejectWith(callbackContext, [jqXHR, statusText, error]);
                }

                // Status-dependent callbacks
                jqXHR.statusCode(statusCode);
                statusCode = undefined;

                if (fireGlobals) {
                    globalEventContext.trigger("ajax" + (isSuccess ? "Success" : "Error"),
						[jqXHR, s, isSuccess ? success : error]);
                }

                // Complete
                completeDeferred.resolveWith(callbackContext, [jqXHR, statusText]);

                if (fireGlobals) {
                    globalEventContext.trigger("ajaxComplete", [jqXHR, s]);
                    // Handle the global AJAX counter
                    if (!(--jQuery.active)) {
                        jQuery.event.trigger("ajaxStop");
                    }
                }
            }

            // Attach deferreds
            deferred.promise(jqXHR);
            jqXHR.success = jqXHR.done;
            jqXHR.error = jqXHR.fail;
            jqXHR.complete = completeDeferred.done;

            // Status-dependent callbacks
            jqXHR.statusCode = function (map) {
                if (map) {
                    var tmp;
                    if (state < 2) {
                        for (tmp in map) {
                            statusCode[tmp] = [statusCode[tmp], map[tmp]];
                        }
                    } else {
                        tmp = map[jqXHR.status];
                        jqXHR.then(tmp, tmp);
                    }
                }
                return this;
            };

            // Remove hash character (#7531: and string promotion)
            // Add protocol if not provided (#5866: IE7 issue with protocol-less urls)
            // We also use the url parameter if available
            s.url = ((url || s.url) + "").replace(rhash, "").replace(rprotocol, ajaxLocParts[1] + "//");

            // Extract dataTypes list
            s.dataTypes = jQuery.trim(s.dataType || "*").toLowerCase().split(rspacesAjax);

            // Determine if a cross-domain request is in order
            if (s.crossDomain == null) {
                parts = rurl.exec(s.url.toLowerCase());
                s.crossDomain = !!(parts &&
				(parts[1] != ajaxLocParts[1] || parts[2] != ajaxLocParts[2] ||
					(parts[3] || (parts[1] === "http:" ? 80 : 443)) !=
						(ajaxLocParts[3] || (ajaxLocParts[1] === "http:" ? 80 : 443)))
			);
            }

            // Convert data if not already a string
            if (s.data && s.processData && typeof s.data !== "string") {
                s.data = jQuery.param(s.data, s.traditional);
            }

            // Apply prefilters
            inspectPrefiltersOrTransports(prefilters, s, options, jqXHR);

            // If request was aborted inside a prefiler, stop there
            if (state === 2) {
                return false;
            }

            // We can fire global events as of now if asked to
            fireGlobals = s.global;

            // Uppercase the type
            s.type = s.type.toUpperCase();

            // Determine if request has content
            s.hasContent = !rnoContent.test(s.type);

            // Watch for a new set of requests
            if (fireGlobals && jQuery.active++ === 0) {
                jQuery.event.trigger("ajaxStart");
            }

            // More options handling for requests with no content
            if (!s.hasContent) {

                // If data is available, append data to url
                if (s.data) {
                    s.url += (rquery.test(s.url) ? "&" : "?") + s.data;
                }

                // Get ifModifiedKey before adding the anti-cache parameter
                ifModifiedKey = s.url;

                // Add anti-cache in url if needed
                if (s.cache === false) {

                    var ts = jQuery.now(),
                    // try replacing _= if it is there
					ret = s.url.replace(rts, "$1_=" + ts);

                    // if nothing was replaced, add timestamp to the end
                    s.url = ret + ((ret === s.url) ? (rquery.test(s.url) ? "&" : "?") + "_=" + ts : "");
                }
            }

            // Set the correct header, if data is being sent
            if (s.data && s.hasContent && s.contentType !== false || options.contentType) {
                jqXHR.setRequestHeader("Content-Type", s.contentType);
            }

            // Set the If-Modified-Since and/or If-None-Match header, if in ifModified mode.
            if (s.ifModified) {
                ifModifiedKey = ifModifiedKey || s.url;
                if (jQuery.lastModified[ifModifiedKey]) {
                    jqXHR.setRequestHeader("If-Modified-Since", jQuery.lastModified[ifModifiedKey]);
                }
                if (jQuery.etag[ifModifiedKey]) {
                    jqXHR.setRequestHeader("If-None-Match", jQuery.etag[ifModifiedKey]);
                }
            }

            // Set the Accepts header for the server, depending on the dataType
            jqXHR.setRequestHeader(
			"Accept",
			s.dataTypes[0] && s.accepts[s.dataTypes[0]] ?
				s.accepts[s.dataTypes[0]] + (s.dataTypes[0] !== "*" ? ", */*; q=0.01" : "") :
				s.accepts["*"]
		);

            // Check for headers option
            for (i in s.headers) {
                jqXHR.setRequestHeader(i, s.headers[i]);
            }

            // Allow custom headers/mimetypes and early abort
            if (s.beforeSend && (s.beforeSend.call(callbackContext, jqXHR, s) === false || state === 2)) {
                // Abort if not done already
                jqXHR.abort();
                return false;

            }

            // Install callbacks on deferreds
            for (i in { success: 1, error: 1, complete: 1 }) {
                jqXHR[i](s[i]);
            }

            // Get transport
            transport = inspectPrefiltersOrTransports(transports, s, options, jqXHR);

            // If no transport, we auto-abort
            if (!transport) {
                done(-1, "No Transport");
            } else {
                jqXHR.readyState = 1;
                // Send global event
                if (fireGlobals) {
                    globalEventContext.trigger("ajaxSend", [jqXHR, s]);
                }
                // Timeout
                if (s.async && s.timeout > 0) {
                    timeoutTimer = setTimeout(function () {
                        jqXHR.abort("timeout");
                    }, s.timeout);
                }

                try {
                    state = 1;
                    transport.send(requestHeaders, done);
                } catch (e) {
                    // Propagate exception as error if not done
                    if (status < 2) {
                        done(-1, e);
                        // Simply rethrow otherwise
                    } else {
                        jQuery.error(e);
                    }
                }
            }

            return jqXHR;
        },

        // Serialize an array of form elements or a set of
        // key/values into a query string
        param: function (a, traditional) {
            var s = [],
			add = function (key, value) {
			    // If value is a function, invoke it and return its value
			    value = jQuery.isFunction(value) ? value() : value;
			    s[s.length] = encodeURIComponent(key) + "=" + encodeURIComponent(value);
			};

            // Set traditional to true for jQuery <= 1.3.2 behavior.
            if (traditional === undefined) {
                traditional = jQuery.ajaxSettings.traditional;
            }

            // If an array was passed in, assume that it is an array of form elements.
            if (jQuery.isArray(a) || (a.jquery && !jQuery.isPlainObject(a))) {
                // Serialize the form elements
                jQuery.each(a, function () {
                    add(this.name, this.value);
                });

            } else {
                // If traditional, encode the "old" way (the way 1.3.2 or older
                // did it), otherwise encode params recursively.
                for (var prefix in a) {
                    buildParams(prefix, a[prefix], traditional, add);
                }
            }

            // Return the resulting serialization
            return s.join("&").replace(r20, "+");
        }
    });

    function buildParams(prefix, obj, traditional, add) {
        if (jQuery.isArray(obj)) {
            // Serialize array item.
            jQuery.each(obj, function (i, v) {
                if (traditional || rbracket.test(prefix)) {
                    // Treat each array item as a scalar.
                    add(prefix, v);

                } else {
                    // If array item is non-scalar (array or object), encode its
                    // numeric index to resolve deserialization ambiguity issues.
                    // Note that rack (as of 1.0.0) can't currently deserialize
                    // nested arrays properly, and attempting to do so may cause
                    // a server error. Possible fixes are to modify rack's
                    // deserialization algorithm or to provide an option or flag
                    // to force array serialization to be shallow.
                    buildParams(prefix + "[" + (typeof v === "object" || jQuery.isArray(v) ? i : "") + "]", v, traditional, add);
                }
            });

        } else if (!traditional && obj != null && typeof obj === "object") {
            // Serialize object item.
            for (var name in obj) {
                buildParams(prefix + "[" + name + "]", obj[name], traditional, add);
            }

        } else {
            // Serialize scalar item.
            add(prefix, obj);
        }
    }

    // This is still on the jQuery object... for now
    // Want to move this to jQuery.ajax some day
    jQuery.extend({

        // Counter for holding the number of active queries
        active: 0,

        // Last-Modified header cache for next request
        lastModified: {},
        etag: {}

    });

    /* Handles responses to an ajax request:
    * - sets all responseXXX fields accordingly
    * - finds the right dataType (mediates between content-type and expected dataType)
    * - returns the corresponding response
    */
    function ajaxHandleResponses(s, jqXHR, responses) {

        var contents = s.contents,
		dataTypes = s.dataTypes,
		responseFields = s.responseFields,
		ct,
		type,
		finalDataType,
		firstDataType;

        // Fill responseXXX fields
        for (type in responseFields) {
            if (type in responses) {
                jqXHR[responseFields[type]] = responses[type];
            }
        }

        // Remove auto dataType and get content-type in the process
        while (dataTypes[0] === "*") {
            dataTypes.shift();
            if (ct === undefined) {
                ct = s.mimeType || jqXHR.getResponseHeader("content-type");
            }
        }

        // Check if we're dealing with a known content-type
        if (ct) {
            for (type in contents) {
                if (contents[type] && contents[type].test(ct)) {
                    dataTypes.unshift(type);
                    break;
                }
            }
        }

        // Check to see if we have a response for the expected dataType
        if (dataTypes[0] in responses) {
            finalDataType = dataTypes[0];
        } else {
            // Try convertible dataTypes
            for (type in responses) {
                if (!dataTypes[0] || s.converters[type + " " + dataTypes[0]]) {
                    finalDataType = type;
                    break;
                }
                if (!firstDataType) {
                    firstDataType = type;
                }
            }
            // Or just use first one
            finalDataType = finalDataType || firstDataType;
        }

        // If we found a dataType
        // We add the dataType to the list if needed
        // and return the corresponding response
        if (finalDataType) {
            if (finalDataType !== dataTypes[0]) {
                dataTypes.unshift(finalDataType);
            }
            return responses[finalDataType];
        }
    }

    // Chain conversions given the request and the original response
    function ajaxConvert(s, response) {

        // Apply the dataFilter if provided
        if (s.dataFilter) {
            response = s.dataFilter(response, s.dataType);
        }

        var dataTypes = s.dataTypes,
		converters = {},
		i,
		key,
		length = dataTypes.length,
		tmp,
        // Current and previous dataTypes
		current = dataTypes[0],
		prev,
        // Conversion expression
		conversion,
        // Conversion function
		conv,
        // Conversion functions (transitive conversion)
		conv1,
		conv2;

        // For each dataType in the chain
        for (i = 1; i < length; i++) {

            // Create converters map
            // with lowercased keys
            if (i === 1) {
                for (key in s.converters) {
                    if (typeof key === "string") {
                        converters[key.toLowerCase()] = s.converters[key];
                    }
                }
            }

            // Get the dataTypes
            prev = current;
            current = dataTypes[i];

            // If current is auto dataType, update it to prev
            if (current === "*") {
                current = prev;
                // If no auto and dataTypes are actually different
            } else if (prev !== "*" && prev !== current) {

                // Get the converter
                conversion = prev + " " + current;
                conv = converters[conversion] || converters["* " + current];

                // If there is no direct converter, search transitively
                if (!conv) {
                    conv2 = undefined;
                    for (conv1 in converters) {
                        tmp = conv1.split(" ");
                        if (tmp[0] === prev || tmp[0] === "*") {
                            conv2 = converters[tmp[1] + " " + current];
                            if (conv2) {
                                conv1 = converters[conv1];
                                if (conv1 === true) {
                                    conv = conv2;
                                } else if (conv2 === true) {
                                    conv = conv1;
                                }
                                break;
                            }
                        }
                    }
                }
                // If we found no converter, dispatch an error
                if (!(conv || conv2)) {
                    jQuery.error("No conversion from " + conversion.replace(" ", " to "));
                }
                // If found converter is not an equivalence
                if (conv !== true) {
                    // Convert with 1 or 2 converters accordingly
                    response = conv ? conv(response) : conv2(conv1(response));
                }
            }
        }
        return response;
    }




    var jsc = jQuery.now(),
	jsre = /(\=)\?(&|$)|\?\?/i;

    // Default jsonp settings
    jQuery.ajaxSetup({
        jsonp: "callback",
        jsonpCallback: function () {
            return jQuery.expando + "_" + (jsc++);
        }
    });

    // Detect, normalize options and install callbacks for jsonp requests
    jQuery.ajaxPrefilter("json jsonp", function (s, originalSettings, jqXHR) {

        var inspectData = s.contentType === "application/x-www-form-urlencoded" &&
		(typeof s.data === "string");

        if (s.dataTypes[0] === "jsonp" ||
		s.jsonp !== false && (jsre.test(s.url) ||
				inspectData && jsre.test(s.data))) {

            var responseContainer,
			jsonpCallback = s.jsonpCallback =
				jQuery.isFunction(s.jsonpCallback) ? s.jsonpCallback() : s.jsonpCallback,
			previous = window[jsonpCallback],
			url = s.url,
			data = s.data,
			replace = "$1" + jsonpCallback + "$2";

            if (s.jsonp !== false) {
                url = url.replace(jsre, replace);
                if (s.url === url) {
                    if (inspectData) {
                        data = data.replace(jsre, replace);
                    }
                    if (s.data === data) {
                        // Add callback manually
                        url += (/\?/.test(url) ? "&" : "?") + s.jsonp + "=" + jsonpCallback;
                    }
                }
            }

            s.url = url;
            s.data = data;

            // Install callback
            window[jsonpCallback] = function (response) {
                responseContainer = [response];
            };

            // Clean-up function
            jqXHR.always(function () {
                // Set callback back to previous value
                window[jsonpCallback] = previous;
                // Call if it was a function and we have a response
                if (responseContainer && jQuery.isFunction(previous)) {
                    window[jsonpCallback](responseContainer[0]);
                }
            });

            // Use data converter to retrieve json after script execution
            s.converters["script json"] = function () {
                if (!responseContainer) {
                    jQuery.error(jsonpCallback + " was not called");
                }
                return responseContainer[0];
            };

            // force json dataType
            s.dataTypes[0] = "json";

            // Delegate to script
            return "script";
        }
    });




    // Install script dataType
    jQuery.ajaxSetup({
        accepts: {
            script: "text/javascript, application/javascript, application/ecmascript, application/x-ecmascript"
        },
        contents: {
            script: /javascript|ecmascript/
        },
        converters: {
            "text script": function (text) {
                jQuery.globalEval(text);
                return text;
            }
        }
    });

    // Handle cache's special case and global
    jQuery.ajaxPrefilter("script", function (s) {
        if (s.cache === undefined) {
            s.cache = false;
        }
        if (s.crossDomain) {
            s.type = "GET";
            s.global = false;
        }
    });

    // Bind script tag hack transport
    jQuery.ajaxTransport("script", function (s) {

        // This transport only deals with cross domain requests
        if (s.crossDomain) {

            var script,
			head = document.head || document.getElementsByTagName("head")[0] || document.documentElement;

            return {

                send: function (_, callback) {

                    script = document.createElement("script");

                    script.async = "async";

                    if (s.scriptCharset) {
                        script.charset = s.scriptCharset;
                    }

                    script.src = s.url;

                    // Attach handlers for all browsers
                    script.onload = script.onreadystatechange = function (_, isAbort) {

                        if (isAbort || !script.readyState || /loaded|complete/.test(script.readyState)) {

                            // Handle memory leak in IE
                            script.onload = script.onreadystatechange = null;

                            // Remove the script
                            if (head && script.parentNode) {
                                head.removeChild(script);
                            }

                            // Dereference the script
                            script = undefined;

                            // Callback if not abort
                            if (!isAbort) {
                                callback(200, "success");
                            }
                        }
                    };
                    // Use insertBefore instead of appendChild  to circumvent an IE6 bug.
                    // This arises when a base node is used (#2709 and #4378).
                    head.insertBefore(script, head.firstChild);
                },

                abort: function () {
                    if (script) {
                        script.onload(0, 1);
                    }
                }
            };
        }
    });




    var // #5280: Internet Explorer will keep connections alive if we don't abort on unload
	xhrOnUnloadAbort = window.ActiveXObject ? function () {
	    // Abort all pending requests
	    for (var key in xhrCallbacks) {
	        xhrCallbacks[key](0, 1);
	    }
	} : false,
	xhrId = 0,
	xhrCallbacks;

    // Functions to create xhrs
    function createStandardXHR() {
        try {
            return new window.XMLHttpRequest();
        } catch (e) { }
    }

    function createActiveXHR() {
        try {
            return new window.ActiveXObject("Microsoft.XMLHTTP");
        } catch (e) { }
    }

    // Create the request object
    // (This is still attached to ajaxSettings for backward compatibility)
    jQuery.ajaxSettings.xhr = window.ActiveXObject ?
    /* Microsoft failed to properly
    * implement the XMLHttpRequest in IE7 (can't request local files),
    * so we use the ActiveXObject when it is available
    * Additionally XMLHttpRequest can be disabled in IE7/IE8 so
    * we need a fallback.
    */
	function () {
	    return !this.isLocal && createStandardXHR() || createActiveXHR();
	} :
    // For all other browsers, use the standard XMLHttpRequest object
	createStandardXHR;

    // Determine support properties
    (function (xhr) {
        jQuery.extend(jQuery.support, {
            ajax: !!xhr,
            cors: !!xhr && ("withCredentials" in xhr)
        });
    })(jQuery.ajaxSettings.xhr());

    // Create transport if the browser can provide an xhr
    if (jQuery.support.ajax) {

        jQuery.ajaxTransport(function (s) {
            // Cross domain only allowed if supported through XMLHttpRequest
            if (!s.crossDomain || jQuery.support.cors) {

                var callback;

                return {
                    send: function (headers, complete) {

                        // Get a new xhr
                        var xhr = s.xhr(),
						handle,
						i;

                        // Open the socket
                        // Passing null username, generates a login popup on Opera (#2865)
                        if (s.username) {
                            xhr.open(s.type, s.url, s.async, s.username, s.password);
                        } else {
                            xhr.open(s.type, s.url, s.async);
                        }

                        // Apply custom fields if provided
                        if (s.xhrFields) {
                            for (i in s.xhrFields) {
                                xhr[i] = s.xhrFields[i];
                            }
                        }

                        // Override mime type if needed
                        if (s.mimeType && xhr.overrideMimeType) {
                            xhr.overrideMimeType(s.mimeType);
                        }

                        // X-Requested-With header
                        // For cross-domain requests, seeing as conditions for a preflight are
                        // akin to a jigsaw puzzle, we simply never set it to be sure.
                        // (it can always be set on a per-request basis or even using ajaxSetup)
                        // For same-domain requests, won't change header if already provided.
                        if (!s.crossDomain && !headers["X-Requested-With"]) {
                            headers["X-Requested-With"] = "XMLHttpRequest";
                        }

                        // Need an extra try/catch for cross domain requests in Firefox 3
                        try {
                            for (i in headers) {
                                xhr.setRequestHeader(i, headers[i]);
                            }
                        } catch (_) { }

                        // Do send the request
                        // This may raise an exception which is actually
                        // handled in jQuery.ajax (so no try/catch here)
                        xhr.send((s.hasContent && s.data) || null);

                        // Listener
                        callback = function (_, isAbort) {

                            var status,
							statusText,
							responseHeaders,
							responses,
							xml;

                            // Firefox throws exceptions when accessing properties
                            // of an xhr when a network error occured
                            // http://helpful.knobs-dials.com/index.php/Component_returned_failure_code:_0x80040111_(NS_ERROR_NOT_AVAILABLE)
                            try {

                                // Was never called and is aborted or complete
                                if (callback && (isAbort || xhr.readyState === 4)) {

                                    // Only called once
                                    callback = undefined;

                                    // Do not keep as active anymore
                                    if (handle) {
                                        xhr.onreadystatechange = jQuery.noop;
                                        if (xhrOnUnloadAbort) {
                                            delete xhrCallbacks[handle];
                                        }
                                    }

                                    // If it's an abort
                                    if (isAbort) {
                                        // Abort it manually if needed
                                        if (xhr.readyState !== 4) {
                                            xhr.abort();
                                        }
                                    } else {
                                        status = xhr.status;
                                        responseHeaders = xhr.getAllResponseHeaders();
                                        responses = {};
                                        xml = xhr.responseXML;

                                        // Construct response list
                                        if (xml && xml.documentElement /* #4958 */) {
                                            responses.xml = xml;
                                        }
                                        responses.text = xhr.responseText;

                                        // Firefox throws an exception when accessing
                                        // statusText for faulty cross-domain requests
                                        try {
                                            statusText = xhr.statusText;
                                        } catch (e) {
                                            // We normalize with Webkit giving an empty statusText
                                            statusText = "";
                                        }

                                        // Filter status for non standard behaviors

                                        // If the request is local and we have data: assume a success
                                        // (success with no data won't get notified, that's the best we
                                        // can do given current implementations)
                                        if (!status && s.isLocal && !s.crossDomain) {
                                            status = responses.text ? 200 : 404;
                                            // IE - #1450: sometimes returns 1223 when it should be 204
                                        } else if (status === 1223) {
                                            status = 204;
                                        }
                                    }
                                }
                            } catch (firefoxAccessException) {
                                if (!isAbort) {
                                    complete(-1, firefoxAccessException);
                                }
                            }

                            // Call complete if needed
                            if (responses) {
                                complete(status, statusText, responses, responseHeaders);
                            }
                        };

                        // if we're in sync mode or it's in cache
                        // and has been retrieved directly (IE6 & IE7)
                        // we need to manually fire the callback
                        if (!s.async || xhr.readyState === 4) {
                            callback();
                        } else {
                            handle = ++xhrId;
                            if (xhrOnUnloadAbort) {
                                // Create the active xhrs callbacks list if needed
                                // and attach the unload handler
                                if (!xhrCallbacks) {
                                    xhrCallbacks = {};
                                    jQuery(window).unload(xhrOnUnloadAbort);
                                }
                                // Add to list of active xhrs callbacks
                                xhrCallbacks[handle] = callback;
                            }
                            xhr.onreadystatechange = callback;
                        }
                    },

                    abort: function () {
                        if (callback) {
                            callback(0, 1);
                        }
                    }
                };
            }
        });
    }




    var elemdisplay = {},
	iframe, iframeDoc,
	rfxtypes = /^(?:toggle|show|hide)$/,
	rfxnum = /^([+\-]=)?([\d+.\-]+)([a-z%]*)$/i,
	timerId,
	fxAttrs = [
    // height animations
		["height", "marginTop", "marginBottom", "paddingTop", "paddingBottom"],
    // width animations
		["width", "marginLeft", "marginRight", "paddingLeft", "paddingRight"],
    // opacity animations
		["opacity"]
	],
	fxNow,
	requestAnimationFrame = window.webkitRequestAnimationFrame ||
		window.mozRequestAnimationFrame ||
		window.oRequestAnimationFrame;

    jQuery.fn.extend({
        show: function (speed, easing, callback) {
            var elem, display;

            if (speed || speed === 0) {
                return this.animate(genFx("show", 3), speed, easing, callback);

            } else {
                for (var i = 0, j = this.length; i < j; i++) {
                    elem = this[i];

                    if (elem.style) {
                        display = elem.style.display;

                        // Reset the inline display of this element to learn if it is
                        // being hidden by cascaded rules or not
                        if (!jQuery._data(elem, "olddisplay") && display === "none") {
                            display = elem.style.display = "";
                        }

                        // Set elements which have been overridden with display: none
                        // in a stylesheet to whatever the default browser style is
                        // for such an element
                        if (display === "" && jQuery.css(elem, "display") === "none") {
                            jQuery._data(elem, "olddisplay", defaultDisplay(elem.nodeName));
                        }
                    }
                }

                // Set the display of most of the elements in a second loop
                // to avoid the constant reflow
                for (i = 0; i < j; i++) {
                    elem = this[i];

                    if (elem.style) {
                        display = elem.style.display;

                        if (display === "" || display === "none") {
                            elem.style.display = jQuery._data(elem, "olddisplay") || "";
                        }
                    }
                }

                return this;
            }
        },

        hide: function (speed, easing, callback) {
            if (speed || speed === 0) {
                return this.animate(genFx("hide", 3), speed, easing, callback);

            } else {
                for (var i = 0, j = this.length; i < j; i++) {
                    if (this[i].style) {
                        var display = jQuery.css(this[i], "display");

                        if (display !== "none" && !jQuery._data(this[i], "olddisplay")) {
                            jQuery._data(this[i], "olddisplay", display);
                        }
                    }
                }

                // Set the display of the elements in a second loop
                // to avoid the constant reflow
                for (i = 0; i < j; i++) {
                    if (this[i].style) {
                        this[i].style.display = "none";
                    }
                }

                return this;
            }
        },

        // Save the old toggle function
        _toggle: jQuery.fn.toggle,

        toggle: function (fn, fn2, callback) {
            var bool = typeof fn === "boolean";

            if (jQuery.isFunction(fn) && jQuery.isFunction(fn2)) {
                this._toggle.apply(this, arguments);

            } else if (fn == null || bool) {
                this.each(function () {
                    var state = bool ? fn : jQuery(this).is(":hidden");
                    jQuery(this)[state ? "show" : "hide"]();
                });

            } else {
                this.animate(genFx("toggle", 3), fn, fn2, callback);
            }

            return this;
        },

        fadeTo: function (speed, to, easing, callback) {
            return this.filter(":hidden").css("opacity", 0).show().end()
					.animate({ opacity: to }, speed, easing, callback);
        },

        animate: function (prop, speed, easing, callback) {
            var optall = jQuery.speed(speed, easing, callback);

            if (jQuery.isEmptyObject(prop)) {
                return this.each(optall.complete, [false]);
            }

            // Do not change referenced properties as per-property easing will be lost
            prop = jQuery.extend({}, prop);

            return this[optall.queue === false ? "each" : "queue"](function () {
                // XXX 'this' does not always have a nodeName when running the
                // test suite

                if (optall.queue === false) {
                    jQuery._mark(this);
                }

                var opt = jQuery.extend({}, optall),
				isElement = this.nodeType === 1,
				hidden = isElement && jQuery(this).is(":hidden"),
				name, val, p,
				display, e,
				parts, start, end, unit;

                // will store per property easing and be used to determine when an animation is complete
                opt.animatedProperties = {};

                for (p in prop) {

                    // property name normalization
                    name = jQuery.camelCase(p);
                    if (p !== name) {
                        prop[name] = prop[p];
                        delete prop[p];
                    }

                    val = prop[name];

                    // easing resolution: per property > opt.specialEasing > opt.easing > 'swing' (default)
                    if (jQuery.isArray(val)) {
                        opt.animatedProperties[name] = val[1];
                        val = prop[name] = val[0];
                    } else {
                        opt.animatedProperties[name] = opt.specialEasing && opt.specialEasing[name] || opt.easing || 'swing';
                    }

                    if (val === "hide" && hidden || val === "show" && !hidden) {
                        return opt.complete.call(this);
                    }

                    if (isElement && (name === "height" || name === "width")) {
                        // Make sure that nothing sneaks out
                        // Record all 3 overflow attributes because IE does not
                        // change the overflow attribute when overflowX and
                        // overflowY are set to the same value
                        opt.overflow = [this.style.overflow, this.style.overflowX, this.style.overflowY];

                        // Set display property to inline-block for height/width
                        // animations on inline elements that are having width/height
                        // animated
                        if (jQuery.css(this, "display") === "inline" &&
							jQuery.css(this, "float") === "none") {
                            if (!jQuery.support.inlineBlockNeedsLayout) {
                                this.style.display = "inline-block";

                            } else {
                                display = defaultDisplay(this.nodeName);

                                // inline-level elements accept inline-block;
                                // block-level elements need to be inline with layout
                                if (display === "inline") {
                                    this.style.display = "inline-block";

                                } else {
                                    this.style.display = "inline";
                                    this.style.zoom = 1;
                                }
                            }
                        }
                    }
                }

                if (opt.overflow != null) {
                    this.style.overflow = "hidden";
                }

                for (p in prop) {
                    e = new jQuery.fx(this, opt, p);
                    val = prop[p];

                    if (rfxtypes.test(val)) {
                        e[val === "toggle" ? hidden ? "show" : "hide" : val]();

                    } else {
                        parts = rfxnum.exec(val);
                        start = e.cur();

                        if (parts) {
                            end = parseFloat(parts[2]);
                            unit = parts[3] || (jQuery.cssNumber[p] ? "" : "px");

                            // We need to compute starting value
                            if (unit !== "px") {
                                jQuery.style(this, p, (end || 1) + unit);
                                start = ((end || 1) / e.cur()) * start;
                                jQuery.style(this, p, start + unit);
                            }

                            // If a +=/-= token was provided, we're doing a relative animation
                            if (parts[1]) {
                                end = ((parts[1] === "-=" ? -1 : 1) * end) + start;
                            }

                            e.custom(start, end, unit);

                        } else {
                            e.custom(start, val, "");
                        }
                    }
                }

                // For JS strict compliance
                return true;
            });
        },

        stop: function (clearQueue, gotoEnd) {
            if (clearQueue) {
                this.queue([]);
            }

            this.each(function () {
                var timers = jQuery.timers,
				i = timers.length;
                // clear marker counters if we know they won't be
                if (!gotoEnd) {
                    jQuery._unmark(true, this);
                }
                while (i--) {
                    if (timers[i].elem === this) {
                        if (gotoEnd) {
                            // force the next step to be the last
                            timers[i](true);
                        }

                        timers.splice(i, 1);
                    }
                }
            });

            // start the next in the queue if the last step wasn't forced
            if (!gotoEnd) {
                this.dequeue();
            }

            return this;
        }

    });

    // Animations created synchronously will run synchronously
    function createFxNow() {
        setTimeout(clearFxNow, 0);
        return (fxNow = jQuery.now());
    }

    function clearFxNow() {
        fxNow = undefined;
    }

    // Generate parameters to create a standard animation
    function genFx(type, num) {
        var obj = {};

        jQuery.each(fxAttrs.concat.apply([], fxAttrs.slice(0, num)), function () {
            obj[this] = type;
        });

        return obj;
    }

    // Generate shortcuts for custom animations
    jQuery.each({
        slideDown: genFx("show", 1),
        slideUp: genFx("hide", 1),
        slideToggle: genFx("toggle", 1),
        fadeIn: { opacity: "show" },
        fadeOut: { opacity: "hide" },
        fadeToggle: { opacity: "toggle" }
    }, function (name, props) {
        jQuery.fn[name] = function (speed, easing, callback) {
            return this.animate(props, speed, easing, callback);
        };
    });

    jQuery.extend({
        speed: function (speed, easing, fn) {
            var opt = speed && typeof speed === "object" ? jQuery.extend({}, speed) : {
                complete: fn || !fn && easing ||
				jQuery.isFunction(speed) && speed,
                duration: speed,
                easing: fn && easing || easing && !jQuery.isFunction(easing) && easing
            };

            opt.duration = jQuery.fx.off ? 0 : typeof opt.duration === "number" ? opt.duration :
			opt.duration in jQuery.fx.speeds ? jQuery.fx.speeds[opt.duration] : jQuery.fx.speeds._default;

            // Queueing
            opt.old = opt.complete;
            opt.complete = function (noUnmark) {
                if (jQuery.isFunction(opt.old)) {
                    opt.old.call(this);
                }

                if (opt.queue !== false) {
                    jQuery.dequeue(this);
                } else if (noUnmark !== false) {
                    jQuery._unmark(this);
                }
            };

            return opt;
        },

        easing: {
            linear: function (p, n, firstNum, diff) {
                return firstNum + diff * p;
            },
            swing: function (p, n, firstNum, diff) {
                return ((-Math.cos(p * Math.PI) / 2) + 0.5) * diff + firstNum;
            }
        },

        timers: [],

        fx: function (elem, options, prop) {
            this.options = options;
            this.elem = elem;
            this.prop = prop;

            options.orig = options.orig || {};
        }

    });

    jQuery.fx.prototype = {
        // Simple function for setting a style value
        update: function () {
            if (this.options.step) {
                this.options.step.call(this.elem, this.now, this);
            }

            (jQuery.fx.step[this.prop] || jQuery.fx.step._default)(this);
        },

        // Get the current size
        cur: function () {
            if (this.elem[this.prop] != null && (!this.elem.style || this.elem.style[this.prop] == null)) {
                return this.elem[this.prop];
            }

            var parsed,
			r = jQuery.css(this.elem, this.prop);
            // Empty strings, null, undefined and "auto" are converted to 0,
            // complex values such as "rotate(1rad)" are returned as is,
            // simple values such as "10px" are parsed to Float.
            return isNaN(parsed = parseFloat(r)) ? !r || r === "auto" ? 0 : r : parsed;
        },

        // Start an animation from one number to another
        custom: function (from, to, unit) {
            var self = this,
			fx = jQuery.fx,
			raf;

            this.startTime = fxNow || createFxNow();
            this.start = from;
            this.end = to;
            this.unit = unit || this.unit || (jQuery.cssNumber[this.prop] ? "" : "px");
            this.now = this.start;
            this.pos = this.state = 0;

            function t(gotoEnd) {
                return self.step(gotoEnd);
            }

            t.elem = this.elem;

            if (t() && jQuery.timers.push(t) && !timerId) {
                // Use requestAnimationFrame instead of setInterval if available
                if (requestAnimationFrame) {
                    timerId = true;
                    raf = function () {
                        // When timerId gets set to null at any point, this stops
                        if (timerId) {
                            requestAnimationFrame(raf);
                            fx.tick();
                        }
                    };
                    requestAnimationFrame(raf);
                } else {
                    timerId = setInterval(fx.tick, fx.interval);
                }
            }
        },

        // Simple 'show' function
        show: function () {
            // Remember where we started, so that we can go back to it later
            this.options.orig[this.prop] = jQuery.style(this.elem, this.prop);
            this.options.show = true;

            // Begin the animation
            // Make sure that we start at a small width/height to avoid any
            // flash of content
            this.custom(this.prop === "width" || this.prop === "height" ? 1 : 0, this.cur());

            // Start by showing the element
            jQuery(this.elem).show();
        },

        // Simple 'hide' function
        hide: function () {
            // Remember where we started, so that we can go back to it later
            this.options.orig[this.prop] = jQuery.style(this.elem, this.prop);
            this.options.hide = true;

            // Begin the animation
            this.custom(this.cur(), 0);
        },

        // Each step of an animation
        step: function (gotoEnd) {
            var t = fxNow || createFxNow(),
			done = true,
			elem = this.elem,
			options = this.options,
			i, n;

            if (gotoEnd || t >= options.duration + this.startTime) {
                this.now = this.end;
                this.pos = this.state = 1;
                this.update();

                options.animatedProperties[this.prop] = true;

                for (i in options.animatedProperties) {
                    if (options.animatedProperties[i] !== true) {
                        done = false;
                    }
                }

                if (done) {
                    // Reset the overflow
                    if (options.overflow != null && !jQuery.support.shrinkWrapBlocks) {

                        jQuery.each(["", "X", "Y"], function (index, value) {
                            elem.style["overflow" + value] = options.overflow[index];
                        });
                    }

                    // Hide the element if the "hide" operation was done
                    if (options.hide) {
                        jQuery(elem).hide();
                    }

                    // Reset the properties, if the item has been hidden or shown
                    if (options.hide || options.show) {
                        for (var p in options.animatedProperties) {
                            jQuery.style(elem, p, options.orig[p]);
                        }
                    }

                    // Execute the complete function
                    options.complete.call(elem);
                }

                return false;

            } else {
                // classical easing cannot be used with an Infinity duration
                if (options.duration == Infinity) {
                    this.now = t;
                } else {
                    n = t - this.startTime;
                    this.state = n / options.duration;

                    // Perform the easing function, defaults to swing
                    this.pos = jQuery.easing[options.animatedProperties[this.prop]](this.state, n, 0, 1, options.duration);
                    this.now = this.start + ((this.end - this.start) * this.pos);
                }
                // Perform the next step of the animation
                this.update();
            }

            return true;
        }
    };

    jQuery.extend(jQuery.fx, {
        tick: function () {
            for (var timers = jQuery.timers, i = 0; i < timers.length; ++i) {
                if (!timers[i]()) {
                    timers.splice(i--, 1);
                }
            }

            if (!timers.length) {
                jQuery.fx.stop();
            }
        },

        interval: 13,

        stop: function () {
            clearInterval(timerId);
            timerId = null;
        },

        speeds: {
            slow: 600,
            fast: 200,
            // Default speed
            _default: 400
        },

        step: {
            opacity: function (fx) {
                jQuery.style(fx.elem, "opacity", fx.now);
            },

            _default: function (fx) {
                if (fx.elem.style && fx.elem.style[fx.prop] != null) {
                    fx.elem.style[fx.prop] = (fx.prop === "width" || fx.prop === "height" ? Math.max(0, fx.now) : fx.now) + fx.unit;
                } else {
                    fx.elem[fx.prop] = fx.now;
                }
            }
        }
    });

    if (jQuery.expr && jQuery.expr.filters) {
        jQuery.expr.filters.animated = function (elem) {
            return jQuery.grep(jQuery.timers, function (fn) {
                return elem === fn.elem;
            }).length;
        };
    }

    // Try to restore the default display value of an element
    function defaultDisplay(nodeName) {

        if (!elemdisplay[nodeName]) {

            var body = document.body,
			elem = jQuery("<" + nodeName + ">").appendTo(body),
			display = elem.css("display");

            elem.remove();

            // If the simple way fails,
            // get element's real default display by attaching it to a temp iframe
            if (display === "none" || display === "") {
                // No iframe to use yet, so create it
                if (!iframe) {
                    iframe = document.createElement("iframe");
                    iframe.frameBorder = iframe.width = iframe.height = 0;
                }

                body.appendChild(iframe);

                // Create a cacheable copy of the iframe document on first call.
                // IE and Opera will allow us to reuse the iframeDoc without re-writing the fake HTML
                // document to it; WebKit & Firefox won't allow reusing the iframe document.
                if (!iframeDoc || !iframe.createElement) {
                    iframeDoc = (iframe.contentWindow || iframe.contentDocument).document;
                    iframeDoc.write((document.compatMode === "CSS1Compat" ? "<!doctype html>" : "") + "<html><body>");
                    iframeDoc.close();
                }

                elem = iframeDoc.createElement(nodeName);

                iframeDoc.body.appendChild(elem);

                display = jQuery.css(elem, "display");

                body.removeChild(iframe);
            }

            // Store the correct default display
            elemdisplay[nodeName] = display;
        }

        return elemdisplay[nodeName];
    }




    var rtable = /^t(?:able|d|h)$/i,
	rroot = /^(?:body|html)$/i;

    if ("getBoundingClientRect" in document.documentElement) {
        jQuery.fn.offset = function (options) {
            var elem = this[0], box;

            if (options) {
                return this.each(function (i) {
                    jQuery.offset.setOffset(this, options, i);
                });
            }

            if (!elem || !elem.ownerDocument) {
                return null;
            }

            if (elem === elem.ownerDocument.body) {
                return jQuery.offset.bodyOffset(elem);
            }

            try {
                box = elem.getBoundingClientRect();
            } catch (e) { }

            var doc = elem.ownerDocument,
			docElem = doc.documentElement;

            // Make sure we're not dealing with a disconnected DOM node
            if (!box || !jQuery.contains(docElem, elem)) {
                return box ? { top: box.top, left: box.left} : { top: 0, left: 0 };
            }

            var body = doc.body,
			win = getWindow(doc),
			clientTop = docElem.clientTop || body.clientTop || 0,
			clientLeft = docElem.clientLeft || body.clientLeft || 0,
			scrollTop = win.pageYOffset || jQuery.support.boxModel && docElem.scrollTop || body.scrollTop,
			scrollLeft = win.pageXOffset || jQuery.support.boxModel && docElem.scrollLeft || body.scrollLeft,
			top = box.top + scrollTop - clientTop,
			left = box.left + scrollLeft - clientLeft;

            return { top: top, left: left };
        };

    } else {
        jQuery.fn.offset = function (options) {
            var elem = this[0];

            if (options) {
                return this.each(function (i) {
                    jQuery.offset.setOffset(this, options, i);
                });
            }

            if (!elem || !elem.ownerDocument) {
                return null;
            }

            if (elem === elem.ownerDocument.body) {
                return jQuery.offset.bodyOffset(elem);
            }

            jQuery.offset.initialize();

            var computedStyle,
			offsetParent = elem.offsetParent,
			prevOffsetParent = elem,
			doc = elem.ownerDocument,
			docElem = doc.documentElement,
			body = doc.body,
			defaultView = doc.defaultView,
			prevComputedStyle = defaultView ? defaultView.getComputedStyle(elem, null) : elem.currentStyle,
			top = elem.offsetTop,
			left = elem.offsetLeft;

            while ((elem = elem.parentNode) && elem !== body && elem !== docElem) {
                if (jQuery.offset.supportsFixedPosition && prevComputedStyle.position === "fixed") {
                    break;
                }

                computedStyle = defaultView ? defaultView.getComputedStyle(elem, null) : elem.currentStyle;
                top -= elem.scrollTop;
                left -= elem.scrollLeft;

                if (elem === offsetParent) {
                    top += elem.offsetTop;
                    left += elem.offsetLeft;

                    if (jQuery.offset.doesNotAddBorder && !(jQuery.offset.doesAddBorderForTableAndCells && rtable.test(elem.nodeName))) {
                        top += parseFloat(computedStyle.borderTopWidth) || 0;
                        left += parseFloat(computedStyle.borderLeftWidth) || 0;
                    }

                    prevOffsetParent = offsetParent;
                    offsetParent = elem.offsetParent;
                }

                if (jQuery.offset.subtractsBorderForOverflowNotVisible && computedStyle.overflow !== "visible") {
                    top += parseFloat(computedStyle.borderTopWidth) || 0;
                    left += parseFloat(computedStyle.borderLeftWidth) || 0;
                }

                prevComputedStyle = computedStyle;
            }

            if (prevComputedStyle.position === "relative" || prevComputedStyle.position === "static") {
                top += body.offsetTop;
                left += body.offsetLeft;
            }

            if (jQuery.offset.supportsFixedPosition && prevComputedStyle.position === "fixed") {
                top += Math.max(docElem.scrollTop, body.scrollTop);
                left += Math.max(docElem.scrollLeft, body.scrollLeft);
            }

            return { top: top, left: left };
        };
    }

    jQuery.offset = {
        initialize: function () {
            var body = document.body, container = document.createElement("div"), innerDiv, checkDiv, table, td, bodyMarginTop = parseFloat(jQuery.css(body, "marginTop")) || 0,
			html = "<div style='position:absolute;top:0;left:0;margin:0;border:5px solid #000;padding:0;width:1px;height:1px;'><div></div></div><table style='position:absolute;top:0;left:0;margin:0;border:5px solid #000;padding:0;width:1px;height:1px;' cellpadding='0' cellspacing='0'><tr><td></td></tr></table>";

            jQuery.extend(container.style, { position: "absolute", top: 0, left: 0, margin: 0, border: 0, width: "1px", height: "1px", visibility: "hidden" });

            container.innerHTML = html;
            body.insertBefore(container, body.firstChild);
            innerDiv = container.firstChild;
            checkDiv = innerDiv.firstChild;
            td = innerDiv.nextSibling.firstChild.firstChild;

            this.doesNotAddBorder = (checkDiv.offsetTop !== 5);
            this.doesAddBorderForTableAndCells = (td.offsetTop === 5);

            checkDiv.style.position = "fixed";
            checkDiv.style.top = "20px";

            // safari subtracts parent border width here which is 5px
            this.supportsFixedPosition = (checkDiv.offsetTop === 20 || checkDiv.offsetTop === 15);
            checkDiv.style.position = checkDiv.style.top = "";

            innerDiv.style.overflow = "hidden";
            innerDiv.style.position = "relative";

            this.subtractsBorderForOverflowNotVisible = (checkDiv.offsetTop === -5);

            this.doesNotIncludeMarginInBodyOffset = (body.offsetTop !== bodyMarginTop);

            body.removeChild(container);
            jQuery.offset.initialize = jQuery.noop;
        },

        bodyOffset: function (body) {
            var top = body.offsetTop,
			left = body.offsetLeft;

            jQuery.offset.initialize();

            if (jQuery.offset.doesNotIncludeMarginInBodyOffset) {
                top += parseFloat(jQuery.css(body, "marginTop")) || 0;
                left += parseFloat(jQuery.css(body, "marginLeft")) || 0;
            }

            return { top: top, left: left };
        },

        setOffset: function (elem, options, i) {
            var position = jQuery.css(elem, "position");

            // set position first, in-case top/left are set even on static elem
            if (position === "static") {
                elem.style.position = "relative";
            }

            var curElem = jQuery(elem),
			curOffset = curElem.offset(),
			curCSSTop = jQuery.css(elem, "top"),
			curCSSLeft = jQuery.css(elem, "left"),
			calculatePosition = (position === "absolute" || position === "fixed") && jQuery.inArray("auto", [curCSSTop, curCSSLeft]) > -1,
			props = {}, curPosition = {}, curTop, curLeft;

            // need to be able to calculate position if either top or left is auto and position is either absolute or fixed
            if (calculatePosition) {
                curPosition = curElem.position();
                curTop = curPosition.top;
                curLeft = curPosition.left;
            } else {
                curTop = parseFloat(curCSSTop) || 0;
                curLeft = parseFloat(curCSSLeft) || 0;
            }

            if (jQuery.isFunction(options)) {
                options = options.call(elem, i, curOffset);
            }

            if (options.top != null) {
                props.top = (options.top - curOffset.top) + curTop;
            }
            if (options.left != null) {
                props.left = (options.left - curOffset.left) + curLeft;
            }

            if ("using" in options) {
                options.using.call(elem, props);
            } else {
                curElem.css(props);
            }
        }
    };


    jQuery.fn.extend({
        position: function () {
            if (!this[0]) {
                return null;
            }

            var elem = this[0],

            // Get *real* offsetParent
		offsetParent = this.offsetParent(),

            // Get correct offsets
		offset = this.offset(),
		parentOffset = rroot.test(offsetParent[0].nodeName) ? { top: 0, left: 0} : offsetParent.offset();

            // Subtract element margins
            // note: when an element has margin: auto the offsetLeft and marginLeft
            // are the same in Safari causing offset.left to incorrectly be 0
            offset.top -= parseFloat(jQuery.css(elem, "marginTop")) || 0;
            offset.left -= parseFloat(jQuery.css(elem, "marginLeft")) || 0;

            // Add offsetParent borders
            parentOffset.top += parseFloat(jQuery.css(offsetParent[0], "borderTopWidth")) || 0;
            parentOffset.left += parseFloat(jQuery.css(offsetParent[0], "borderLeftWidth")) || 0;

            // Subtract the two offsets
            return {
                top: offset.top - parentOffset.top,
                left: offset.left - parentOffset.left
            };
        },

        offsetParent: function () {
            return this.map(function () {
                var offsetParent = this.offsetParent || document.body;
                while (offsetParent && (!rroot.test(offsetParent.nodeName) && jQuery.css(offsetParent, "position") === "static")) {
                    offsetParent = offsetParent.offsetParent;
                }
                return offsetParent;
            });
        }
    });


    // Create scrollLeft and scrollTop methods
    jQuery.each(["Left", "Top"], function (i, name) {
        var method = "scroll" + name;

        jQuery.fn[method] = function (val) {
            var elem, win;

            if (val === undefined) {
                elem = this[0];

                if (!elem) {
                    return null;
                }

                win = getWindow(elem);

                // Return the scroll offset
                return win ? ("pageXOffset" in win) ? win[i ? "pageYOffset" : "pageXOffset"] :
				jQuery.support.boxModel && win.document.documentElement[method] ||
					win.document.body[method] :
				elem[method];
            }

            // Set the scroll offset
            return this.each(function () {
                win = getWindow(this);

                if (win) {
                    win.scrollTo(
					!i ? val : jQuery(win).scrollLeft(),
					 i ? val : jQuery(win).scrollTop()
				);

                } else {
                    this[method] = val;
                }
            });
        };
    });

    function getWindow(elem) {
        return jQuery.isWindow(elem) ?
		elem :
		elem.nodeType === 9 ?
			elem.defaultView || elem.parentWindow :
			false;
    }




    // Create width, height, innerHeight, innerWidth, outerHeight and outerWidth methods
    jQuery.each(["Height", "Width"], function (i, name) {

        var type = name.toLowerCase();

        // innerHeight and innerWidth
        jQuery.fn["inner" + name] = function () {
            var elem = this[0];
            return elem && elem.style ?
			parseFloat(jQuery.css(elem, type, "padding")) :
			null;
        };

        // outerHeight and outerWidth
        jQuery.fn["outer" + name] = function (margin) {
            var elem = this[0];
            return elem && elem.style ?
			parseFloat(jQuery.css(elem, type, margin ? "margin" : "border")) :
			null;
        };

        jQuery.fn[type] = function (size) {
            // Get window width or height
            var elem = this[0];
            if (!elem) {
                return size == null ? null : this;
            }

            if (jQuery.isFunction(size)) {
                return this.each(function (i) {
                    var self = jQuery(this);
                    self[type](size.call(this, i, self[type]()));
                });
            }

            if (jQuery.isWindow(elem)) {
                // Everyone else use document.documentElement or document.body depending on Quirks vs Standards mode
                // 3rd condition allows Nokia support, as it supports the docElem prop but not CSS1Compat
                var docElemProp = elem.document.documentElement["client" + name];
                return elem.document.compatMode === "CSS1Compat" && docElemProp ||
				elem.document.body["client" + name] || docElemProp;

                // Get document width or height
            } else if (elem.nodeType === 9) {
                // Either scroll[Width/Height] or offset[Width/Height], whichever is greater
                return Math.max(
				elem.documentElement["client" + name],
				elem.body["scroll" + name], elem.documentElement["scroll" + name],
				elem.body["offset" + name], elem.documentElement["offset" + name]
			);

                // Get or set width or height on the element
            } else if (size === undefined) {
                var orig = jQuery.css(elem, type),
				ret = parseFloat(orig);

                return jQuery.isNaN(ret) ? orig : ret;

                // Set the width or height on the element (default to pixels if value is unitless)
            } else {
                return this.css(type, typeof size === "string" ? size : size + "px");
            }
        };

    });


    // Expose jQuery to the global object
    window.jQuery = window.$ = jQuery;

    // @PATCH
    window.$.rebindWindowContext = function (win) {
        window = win;
        document = win.document;
        rootjQuery = $(document);
        return function (selector, context) {
            // The jQuery object is actually just the init constructor 'enhanced'
            return new jQuery.fn.init(selector, context, rootjQuery);
        }
    };
})(window);
;
Class("JooseX.SimpleRequest", {

    have : {
    	req : null
	},

    
    methods: {
    	
        initialize: function () {
            if (window.XMLHttpRequest)
                this.req = new XMLHttpRequest()
            else
                this.req = new ActiveXObject("Microsoft.XMLHTTP")
        },
        
        
        getText: function (urlOrOptions, async, callback, scope) {
            var req = this.req
            
            var headers
            var url
            
            if (typeof urlOrOptions != 'string') {
                headers = urlOrOptions.headers
                url = urlOrOptions.url
                async = async || urlOrOptions.async
                callback = callback || urlOrOptions.callback
                scope = scope || urlOrOptions.scope
            } else url = urlOrOptions
            
            req.open('GET', url, async || false)
            
            if (headers) Joose.O.eachOwn(headers, function (value, name) {
                req.setRequestHeader(name, value)
            })
            
            try {
                req.onreadystatechange = function (event) {  
                    if (async && req.readyState == 4) {  
                        // status is set to 0 for failed cross-domain requests.. 
                        if (req.status == 200 /*|| req.status == 0*/) callback.call(scope || this, true, req.responseText)
                        else callback.call(scope || this, false, "File not found: " + url)
                    }  
                };  
                req.send(null)
            } catch (e) {
                throw "File not found: " + url
            }
            
            if (!async)
                if (req.status == 200 || req.status == 0) return req.responseText; else throw "File not found: " + url
            
            return null
        }
    }
})
;
/**
 * SyntaxHighlighter
 * http://alexgorbatchev.com/SyntaxHighlighter
 *
 * SyntaxHighlighter is donationware. If you are using it, please donate.
 * http://alexgorbatchev.com/SyntaxHighlighter/donate.html
 *
 * @version
 * 3.0.83 (July 02 2010)
 * 
 * @copyright
 * Copyright (C) 2004-2010 Alex Gorbatchev.
 *
 * @license
 * Dual licensed under the MIT and GPL licenses.
 */
eval(function(p,a,c,k,e,d){e=function(c){return(c<a?'':e(parseInt(c/a)))+((c=c%a)>35?String.fromCharCode(c+29):c.toString(36))};if(!''.replace(/^/,String)){while(c--){d[e(c)]=k[c]||e(c)}k=[function(e){return d[e]}];e=function(){return'\\w+'};c=1};while(c--){if(k[c]){p=p.replace(new RegExp('\\b'+e(c)+'\\b','g'),k[c])}}return p}('K M;I(M)1S 2U("2a\'t 4k M 4K 2g 3l 4G 4H");(6(){6 r(f,e){I(!M.1R(f))1S 3m("3s 15 4R");K a=f.1w;f=M(f.1m,t(f)+(e||""));I(a)f.1w={1m:a.1m,19:a.19?a.19.1a(0):N};H f}6 t(f){H(f.1J?"g":"")+(f.4s?"i":"")+(f.4p?"m":"")+(f.4v?"x":"")+(f.3n?"y":"")}6 B(f,e,a,b){K c=u.L,d,h,g;v=R;5K{O(;c--;){g=u[c];I(a&g.3r&&(!g.2p||g.2p.W(b))){g.2q.12=e;I((h=g.2q.X(f))&&h.P===e){d={3k:g.2b.W(b,h,a),1C:h};1N}}}}5v(i){1S i}5q{v=11}H d}6 p(f,e,a){I(3b.Z.1i)H f.1i(e,a);O(a=a||0;a<f.L;a++)I(f[a]===e)H a;H-1}M=6(f,e){K a=[],b=M.1B,c=0,d,h;I(M.1R(f)){I(e!==1d)1S 3m("2a\'t 5r 5I 5F 5B 5C 15 5E 5p");H r(f)}I(v)1S 2U("2a\'t W 3l M 59 5m 5g 5x 5i");e=e||"";O(d={2N:11,19:[],2K:6(g){H e.1i(g)>-1},3d:6(g){e+=g}};c<f.L;)I(h=B(f,c,b,d)){a.U(h.3k);c+=h.1C[0].L||1}Y I(h=n.X.W(z[b],f.1a(c))){a.U(h[0]);c+=h[0].L}Y{h=f.3a(c);I(h==="[")b=M.2I;Y I(h==="]")b=M.1B;a.U(h);c++}a=15(a.1K(""),n.Q.W(e,w,""));a.1w={1m:f,19:d.2N?d.19:N};H a};M.3v="1.5.0";M.2I=1;M.1B=2;K C=/\\$(?:(\\d\\d?|[$&`\'])|{([$\\w]+)})/g,w=/[^5h]+|([\\s\\S])(?=[\\s\\S]*\\1)/g,A=/^(?:[?*+]|{\\d+(?:,\\d*)?})\\??/,v=11,u=[],n={X:15.Z.X,1A:15.Z.1A,1C:1r.Z.1C,Q:1r.Z.Q,1e:1r.Z.1e},x=n.X.W(/()??/,"")[1]===1d,D=6(){K f=/^/g;n.1A.W(f,"");H!f.12}(),y=6(){K f=/x/g;n.Q.W("x",f,"");H!f.12}(),E=15.Z.3n!==1d,z={};z[M.2I]=/^(?:\\\\(?:[0-3][0-7]{0,2}|[4-7][0-7]?|x[\\29-26-f]{2}|u[\\29-26-f]{4}|c[A-3o-z]|[\\s\\S]))/;z[M.1B]=/^(?:\\\\(?:0(?:[0-3][0-7]{0,2}|[4-7][0-7]?)?|[1-9]\\d*|x[\\29-26-f]{2}|u[\\29-26-f]{4}|c[A-3o-z]|[\\s\\S])|\\(\\?[:=!]|[?*+]\\?|{\\d+(?:,\\d*)?}\\??)/;M.1h=6(f,e,a,b){u.U({2q:r(f,"g"+(E?"y":"")),2b:e,3r:a||M.1B,2p:b||N})};M.2n=6(f,e){K a=f+"/"+(e||"");H M.2n[a]||(M.2n[a]=M(f,e))};M.3c=6(f){H r(f,"g")};M.5l=6(f){H f.Q(/[-[\\]{}()*+?.,\\\\^$|#\\s]/g,"\\\\$&")};M.5e=6(f,e,a,b){e=r(e,"g"+(b&&E?"y":""));e.12=a=a||0;f=e.X(f);H b?f&&f.P===a?f:N:f};M.3q=6(){M.1h=6(){1S 2U("2a\'t 55 1h 54 3q")}};M.1R=6(f){H 53.Z.1q.W(f)==="[2m 15]"};M.3p=6(f,e,a,b){O(K c=r(e,"g"),d=-1,h;h=c.X(f);){a.W(b,h,++d,f,c);c.12===h.P&&c.12++}I(e.1J)e.12=0};M.57=6(f,e){H 6 a(b,c){K d=e[c].1I?e[c]:{1I:e[c]},h=r(d.1I,"g"),g=[],i;O(i=0;i<b.L;i++)M.3p(b[i],h,6(k){g.U(d.3j?k[d.3j]||"":k[0])});H c===e.L-1||!g.L?g:a(g,c+1)}([f],0)};15.Z.1p=6(f,e){H J.X(e[0])};15.Z.W=6(f,e){H J.X(e)};15.Z.X=6(f){K e=n.X.1p(J,14),a;I(e){I(!x&&e.L>1&&p(e,"")>-1){a=15(J.1m,n.Q.W(t(J),"g",""));n.Q.W(f.1a(e.P),a,6(){O(K c=1;c<14.L-2;c++)I(14[c]===1d)e[c]=1d})}I(J.1w&&J.1w.19)O(K b=1;b<e.L;b++)I(a=J.1w.19[b-1])e[a]=e[b];!D&&J.1J&&!e[0].L&&J.12>e.P&&J.12--}H e};I(!D)15.Z.1A=6(f){(f=n.X.W(J,f))&&J.1J&&!f[0].L&&J.12>f.P&&J.12--;H!!f};1r.Z.1C=6(f){M.1R(f)||(f=15(f));I(f.1J){K e=n.1C.1p(J,14);f.12=0;H e}H f.X(J)};1r.Z.Q=6(f,e){K a=M.1R(f),b,c;I(a&&1j e.58()==="3f"&&e.1i("${")===-1&&y)H n.Q.1p(J,14);I(a){I(f.1w)b=f.1w.19}Y f+="";I(1j e==="6")c=n.Q.W(J,f,6(){I(b){14[0]=1f 1r(14[0]);O(K d=0;d<b.L;d++)I(b[d])14[0][b[d]]=14[d+1]}I(a&&f.1J)f.12=14[14.L-2]+14[0].L;H e.1p(N,14)});Y{c=J+"";c=n.Q.W(c,f,6(){K d=14;H n.Q.W(e,C,6(h,g,i){I(g)5b(g){24"$":H"$";24"&":H d[0];24"`":H d[d.L-1].1a(0,d[d.L-2]);24"\'":H d[d.L-1].1a(d[d.L-2]+d[0].L);5a:i="";g=+g;I(!g)H h;O(;g>d.L-3;){i=1r.Z.1a.W(g,-1)+i;g=1Q.3i(g/10)}H(g?d[g]||"":"$")+i}Y{g=+i;I(g<=d.L-3)H d[g];g=b?p(b,i):-1;H g>-1?d[g+1]:h}})})}I(a&&f.1J)f.12=0;H c};1r.Z.1e=6(f,e){I(!M.1R(f))H n.1e.1p(J,14);K a=J+"",b=[],c=0,d,h;I(e===1d||+e<0)e=5D;Y{e=1Q.3i(+e);I(!e)H[]}O(f=M.3c(f);d=f.X(a);){I(f.12>c){b.U(a.1a(c,d.P));d.L>1&&d.P<a.L&&3b.Z.U.1p(b,d.1a(1));h=d[0].L;c=f.12;I(b.L>=e)1N}f.12===d.P&&f.12++}I(c===a.L){I(!n.1A.W(f,"")||h)b.U("")}Y b.U(a.1a(c));H b.L>e?b.1a(0,e):b};M.1h(/\\(\\?#[^)]*\\)/,6(f){H n.1A.W(A,f.2S.1a(f.P+f[0].L))?"":"(?:)"});M.1h(/\\((?!\\?)/,6(){J.19.U(N);H"("});M.1h(/\\(\\?<([$\\w]+)>/,6(f){J.19.U(f[1]);J.2N=R;H"("});M.1h(/\\\\k<([\\w$]+)>/,6(f){K e=p(J.19,f[1]);H e>-1?"\\\\"+(e+1)+(3R(f.2S.3a(f.P+f[0].L))?"":"(?:)"):f[0]});M.1h(/\\[\\^?]/,6(f){H f[0]==="[]"?"\\\\b\\\\B":"[\\\\s\\\\S]"});M.1h(/^\\(\\?([5A]+)\\)/,6(f){J.3d(f[1]);H""});M.1h(/(?:\\s+|#.*)+/,6(f){H n.1A.W(A,f.2S.1a(f.P+f[0].L))?"":"(?:)"},M.1B,6(){H J.2K("x")});M.1h(/\\./,6(){H"[\\\\s\\\\S]"},M.1B,6(){H J.2K("s")})})();1j 2e!="1d"&&(2e.M=M);K 1v=6(){6 r(a,b){a.1l.1i(b)!=-1||(a.1l+=" "+b)}6 t(a){H a.1i("3e")==0?a:"3e"+a}6 B(a){H e.1Y.2A[t(a)]}6 p(a,b,c){I(a==N)H N;K d=c!=R?a.3G:[a.2G],h={"#":"1c",".":"1l"}[b.1o(0,1)]||"3h",g,i;g=h!="3h"?b.1o(1):b.5u();I((a[h]||"").1i(g)!=-1)H a;O(a=0;d&&a<d.L&&i==N;a++)i=p(d[a],b,c);H i}6 C(a,b){K c={},d;O(d 2g a)c[d]=a[d];O(d 2g b)c[d]=b[d];H c}6 w(a,b,c,d){6 h(g){g=g||1P.5y;I(!g.1F){g.1F=g.52;g.3N=6(){J.5w=11}}c.W(d||1P,g)}a.3g?a.3g("4U"+b,h):a.4y(b,h,11)}6 A(a,b){K c=e.1Y.2j,d=N;I(c==N){c={};O(K h 2g e.1U){K g=e.1U[h];d=g.4x;I(d!=N){g.1V=h.4w();O(g=0;g<d.L;g++)c[d[g]]=h}}e.1Y.2j=c}d=e.1U[c[a]];d==N&&b!=11&&1P.1X(e.13.1x.1X+(e.13.1x.3E+a));H d}6 v(a,b){O(K c=a.1e("\\n"),d=0;d<c.L;d++)c[d]=b(c[d],d);H c.1K("\\n")}6 u(a,b){I(a==N||a.L==0||a=="\\n")H a;a=a.Q(/</g,"&1y;");a=a.Q(/ {2,}/g,6(c){O(K d="",h=0;h<c.L-1;h++)d+=e.13.1W;H d+" "});I(b!=N)a=v(a,6(c){I(c.L==0)H"";K d="";c=c.Q(/^(&2s;| )+/,6(h){d=h;H""});I(c.L==0)H d;H d+\'<17 1g="\'+b+\'">\'+c+"</17>"});H a}6 n(a,b){a.1e("\\n");O(K c="",d=0;d<50;d++)c+="                    ";H a=v(a,6(h){I(h.1i("\\t")==-1)H h;O(K g=0;(g=h.1i("\\t"))!=-1;)h=h.1o(0,g)+c.1o(0,b-g%b)+h.1o(g+1,h.L);H h})}6 x(a){H a.Q(/^\\s+|\\s+$/g,"")}6 D(a,b){I(a.P<b.P)H-1;Y I(a.P>b.P)H 1;Y I(a.L<b.L)H-1;Y I(a.L>b.L)H 1;H 0}6 y(a,b){6 c(k){H k[0]}O(K d=N,h=[],g=b.2D?b.2D:c;(d=b.1I.X(a))!=N;){K i=g(d,b);I(1j i=="3f")i=[1f e.2L(i,d.P,b.23)];h=h.1O(i)}H h}6 E(a){K b=/(.*)((&1G;|&1y;).*)/;H a.Q(e.3A.3M,6(c){K d="",h=N;I(h=b.X(c)){c=h[1];d=h[2]}H\'<a 2h="\'+c+\'">\'+c+"</a>"+d})}6 z(){O(K a=1E.36("1k"),b=[],c=0;c<a.L;c++)a[c].3s=="20"&&b.U(a[c]);H b}6 f(a){a=a.1F;K b=p(a,".20",R);a=p(a,".3O",R);K c=1E.4i("3t");I(!(!a||!b||p(a,"3t"))){B(b.1c);r(b,"1m");O(K d=a.3G,h=[],g=0;g<d.L;g++)h.U(d[g].4z||d[g].4A);h=h.1K("\\r");c.39(1E.4D(h));a.39(c);c.2C();c.4C();w(c,"4u",6(){c.2G.4E(c);b.1l=b.1l.Q("1m","")})}}I(1j 3F!="1d"&&1j M=="1d")M=3F("M").M;K e={2v:{"1g-27":"","2i-1s":1,"2z-1s-2t":11,1M:N,1t:N,"42-45":R,"43-22":4,1u:R,16:R,"3V-17":R,2l:11,"41-40":R,2k:11,"1z-1k":11},13:{1W:"&2s;",2M:R,46:11,44:11,34:"4n",1x:{21:"4o 1m",2P:"?",1X:"1v\\n\\n",3E:"4r\'t 4t 1D O: ",4g:"4m 4B\'t 51 O 1z-1k 4F: ",37:\'<!4T 1z 4S "-//4V//3H 4W 1.0 4Z//4Y" "1Z://2y.3L.3K/4X/3I/3H/3I-4P.4J"><1z 4I="1Z://2y.3L.3K/4L/5L"><3J><4N 1Z-4M="5G-5M" 6K="2O/1z; 6J=6I-8" /><1t>6L 1v</1t></3J><3B 1L="25-6M:6Q,6P,6O,6N-6F;6y-2f:#6x;2f:#6w;25-22:6v;2O-3D:3C;"><T 1L="2O-3D:3C;3w-32:1.6z;"><T 1L="25-22:6A-6E;">1v</T><T 1L="25-22:.6C;3w-6B:6R;"><T>3v 3.0.76 (72 73 3x)</T><T><a 2h="1Z://3u.2w/1v" 1F="38" 1L="2f:#3y">1Z://3u.2w/1v</a></T><T>70 17 6U 71.</T><T>6T 6X-3x 6Y 6D.</T></T><T>6t 61 60 J 1k, 5Z <a 2h="6u://2y.62.2w/63-66/65?64=5X-5W&5P=5O" 1L="2f:#3y">5R</a> 5V <2R/>5U 5T 5S!</T></T></3B></1z>\'}},1Y:{2j:N,2A:{}},1U:{},3A:{6n:/\\/\\*[\\s\\S]*?\\*\\//2c,6m:/\\/\\/.*$/2c,6l:/#.*$/2c,6k:/"([^\\\\"\\n]|\\\\.)*"/g,6o:/\'([^\\\\\'\\n]|\\\\.)*\'/g,6p:1f M(\'"([^\\\\\\\\"]|\\\\\\\\.)*"\',"3z"),6s:1f M("\'([^\\\\\\\\\']|\\\\\\\\.)*\'","3z"),6q:/(&1y;|<)!--[\\s\\S]*?--(&1G;|>)/2c,3M:/\\w+:\\/\\/[\\w-.\\/?%&=:@;]*/g,6a:{18:/(&1y;|<)\\?=?/g,1b:/\\?(&1G;|>)/g},69:{18:/(&1y;|<)%=?/g,1b:/%(&1G;|>)/g},6d:{18:/(&1y;|<)\\s*1k.*?(&1G;|>)/2T,1b:/(&1y;|<)\\/\\s*1k\\s*(&1G;|>)/2T}},16:{1H:6(a){6 b(i,k){H e.16.2o(i,k,e.13.1x[k])}O(K c=\'<T 1g="16">\',d=e.16.2x,h=d.2X,g=0;g<h.L;g++)c+=(d[h[g]].1H||b)(a,h[g]);c+="</T>";H c},2o:6(a,b,c){H\'<2W><a 2h="#" 1g="6e 6h\'+b+" "+b+\'">\'+c+"</a></2W>"},2b:6(a){K b=a.1F,c=b.1l||"";b=B(p(b,".20",R).1c);K d=6(h){H(h=15(h+"6f(\\\\w+)").X(c))?h[1]:N}("6g");b&&d&&e.16.2x[d].2B(b);a.3N()},2x:{2X:["21","2P"],21:{1H:6(a){I(a.V("2l")!=R)H"";K b=a.V("1t");H e.16.2o(a,"21",b?b:e.13.1x.21)},2B:6(a){a=1E.6j(t(a.1c));a.1l=a.1l.Q("47","")}},2P:{2B:6(){K a="68=0";a+=", 18="+(31.30-33)/2+", 32="+(31.2Z-2Y)/2+", 30=33, 2Z=2Y";a=a.Q(/^,/,"");a=1P.6Z("","38",a);a.2C();K b=a.1E;b.6W(e.13.1x.37);b.6V();a.2C()}}}},35:6(a,b){K c;I(b)c=[b];Y{c=1E.36(e.13.34);O(K d=[],h=0;h<c.L;h++)d.U(c[h]);c=d}c=c;d=[];I(e.13.2M)c=c.1O(z());I(c.L===0)H d;O(h=0;h<c.L;h++){O(K g=c[h],i=a,k=c[h].1l,j=3W 0,l={},m=1f M("^\\\\[(?<2V>(.*?))\\\\]$"),s=1f M("(?<27>[\\\\w-]+)\\\\s*:\\\\s*(?<1T>[\\\\w-%#]+|\\\\[.*?\\\\]|\\".*?\\"|\'.*?\')\\\\s*;?","g");(j=s.X(k))!=N;){K o=j.1T.Q(/^[\'"]|[\'"]$/g,"");I(o!=N&&m.1A(o)){o=m.X(o);o=o.2V.L>0?o.2V.1e(/\\s*,\\s*/):[]}l[j.27]=o}g={1F:g,1n:C(i,l)};g.1n.1D!=N&&d.U(g)}H d},1M:6(a,b){K c=J.35(a,b),d=N,h=e.13;I(c.L!==0)O(K g=0;g<c.L;g++){b=c[g];K i=b.1F,k=b.1n,j=k.1D,l;I(j!=N){I(k["1z-1k"]=="R"||e.2v["1z-1k"]==R){d=1f e.4l(j);j="4O"}Y I(d=A(j))d=1f d;Y 6H;l=i.3X;I(h.2M){l=l;K m=x(l),s=11;I(m.1i("<![6G[")==0){m=m.4h(9);s=R}K o=m.L;I(m.1i("]]\\>")==o-3){m=m.4h(0,o-3);s=R}l=s?m:l}I((i.1t||"")!="")k.1t=i.1t;k.1D=j;d.2Q(k);b=d.2F(l);I((i.1c||"")!="")b.1c=i.1c;i.2G.74(b,i)}}},2E:6(a){w(1P,"4k",6(){e.1M(a)})}};e.2E=e.2E;e.1M=e.1M;e.2L=6(a,b,c){J.1T=a;J.P=b;J.L=a.L;J.23=c;J.1V=N};e.2L.Z.1q=6(){H J.1T};e.4l=6(a){6 b(j,l){O(K m=0;m<j.L;m++)j[m].P+=l}K c=A(a),d,h=1f e.1U.5Y,g=J,i="2F 1H 2Q".1e(" ");I(c!=N){d=1f c;O(K k=0;k<i.L;k++)(6(){K j=i[k];g[j]=6(){H h[j].1p(h,14)}})();d.28==N?1P.1X(e.13.1x.1X+(e.13.1x.4g+a)):h.2J.U({1I:d.28.17,2D:6(j){O(K l=j.17,m=[],s=d.2J,o=j.P+j.18.L,F=d.28,q,G=0;G<s.L;G++){q=y(l,s[G]);b(q,o);m=m.1O(q)}I(F.18!=N&&j.18!=N){q=y(j.18,F.18);b(q,j.P);m=m.1O(q)}I(F.1b!=N&&j.1b!=N){q=y(j.1b,F.1b);b(q,j.P+j[0].5Q(j.1b));m=m.1O(q)}O(j=0;j<m.L;j++)m[j].1V=c.1V;H m}})}};e.4j=6(){};e.4j.Z={V:6(a,b){K c=J.1n[a];c=c==N?b:c;K d={"R":R,"11":11}[c];H d==N?c:d},3Y:6(a){H 1E.4i(a)},4c:6(a,b){K c=[];I(a!=N)O(K d=0;d<a.L;d++)I(1j a[d]=="2m")c=c.1O(y(b,a[d]));H J.4e(c.6b(D))},4e:6(a){O(K b=0;b<a.L;b++)I(a[b]!==N)O(K c=a[b],d=c.P+c.L,h=b+1;h<a.L&&a[b]!==N;h++){K g=a[h];I(g!==N)I(g.P>d)1N;Y I(g.P==c.P&&g.L>c.L)a[b]=N;Y I(g.P>=c.P&&g.P<d)a[h]=N}H a},4d:6(a){K b=[],c=2u(J.V("2i-1s"));v(a,6(d,h){b.U(h+c)});H b},3U:6(a){K b=J.V("1M",[]);I(1j b!="2m"&&b.U==N)b=[b];a:{a=a.1q();K c=3W 0;O(c=c=1Q.6c(c||0,0);c<b.L;c++)I(b[c]==a){b=c;1N a}b=-1}H b!=-1},2r:6(a,b,c){a=["1s","6i"+b,"P"+a,"6r"+(b%2==0?1:2).1q()];J.3U(b)&&a.U("67");b==0&&a.U("1N");H\'<T 1g="\'+a.1K(" ")+\'">\'+c+"</T>"},3Q:6(a,b){K c="",d=a.1e("\\n").L,h=2u(J.V("2i-1s")),g=J.V("2z-1s-2t");I(g==R)g=(h+d-1).1q().L;Y I(3R(g)==R)g=0;O(K i=0;i<d;i++){K k=b?b[i]:h+i,j;I(k==0)j=e.13.1W;Y{j=g;O(K l=k.1q();l.L<j;)l="0"+l;j=l}a=j;c+=J.2r(i,k,a)}H c},49:6(a,b){a=x(a);K c=a.1e("\\n");J.V("2z-1s-2t");K d=2u(J.V("2i-1s"));a="";O(K h=J.V("1D"),g=0;g<c.L;g++){K i=c[g],k=/^(&2s;|\\s)+/.X(i),j=N,l=b?b[g]:d+g;I(k!=N){j=k[0].1q();i=i.1o(j.L);j=j.Q(" ",e.13.1W)}i=x(i);I(i.L==0)i=e.13.1W;a+=J.2r(g,l,(j!=N?\'<17 1g="\'+h+\' 5N">\'+j+"</17>":"")+i)}H a},4f:6(a){H a?"<4a>"+a+"</4a>":""},4b:6(a,b){6 c(l){H(l=l?l.1V||g:g)?l+" ":""}O(K d=0,h="",g=J.V("1D",""),i=0;i<b.L;i++){K k=b[i],j;I(!(k===N||k.L===0)){j=c(k);h+=u(a.1o(d,k.P-d),j+"48")+u(k.1T,j+k.23);d=k.P+k.L+(k.75||0)}}h+=u(a.1o(d),c()+"48");H h},1H:6(a){K b="",c=["20"],d;I(J.V("2k")==R)J.1n.16=J.1n.1u=11;1l="20";J.V("2l")==R&&c.U("47");I((1u=J.V("1u"))==11)c.U("6S");c.U(J.V("1g-27"));c.U(J.V("1D"));a=a.Q(/^[ ]*[\\n]+|[\\n]*[ ]*$/g,"").Q(/\\r/g," ");b=J.V("43-22");I(J.V("42-45")==R)a=n(a,b);Y{O(K h="",g=0;g<b;g++)h+=" ";a=a.Q(/\\t/g,h)}a=a;a:{b=a=a;h=/<2R\\s*\\/?>|&1y;2R\\s*\\/?&1G;/2T;I(e.13.46==R)b=b.Q(h,"\\n");I(e.13.44==R)b=b.Q(h,"");b=b.1e("\\n");h=/^\\s*/;g=4Q;O(K i=0;i<b.L&&g>0;i++){K k=b[i];I(x(k).L!=0){k=h.X(k);I(k==N){a=a;1N a}g=1Q.4q(k[0].L,g)}}I(g>0)O(i=0;i<b.L;i++)b[i]=b[i].1o(g);a=b.1K("\\n")}I(1u)d=J.4d(a);b=J.4c(J.2J,a);b=J.4b(a,b);b=J.49(b,d);I(J.V("41-40"))b=E(b);1j 2H!="1d"&&2H.3S&&2H.3S.1C(/5s/)&&c.U("5t");H b=\'<T 1c="\'+t(J.1c)+\'" 1g="\'+c.1K(" ")+\'">\'+(J.V("16")?e.16.1H(J):"")+\'<3Z 5z="0" 5H="0" 5J="0">\'+J.4f(J.V("1t"))+"<3T><3P>"+(1u?\'<2d 1g="1u">\'+J.3Q(a)+"</2d>":"")+\'<2d 1g="17"><T 1g="3O">\'+b+"</T></2d></3P></3T></3Z></T>"},2F:6(a){I(a===N)a="";J.17=a;K b=J.3Y("T");b.3X=J.1H(a);J.V("16")&&w(p(b,".16"),"5c",e.16.2b);J.V("3V-17")&&w(p(b,".17"),"56",f);H b},2Q:6(a){J.1c=""+1Q.5d(1Q.5n()*5k).1q();e.1Y.2A[t(J.1c)]=J;J.1n=C(e.2v,a||{});I(J.V("2k")==R)J.1n.16=J.1n.1u=11},5j:6(a){a=a.Q(/^\\s+|\\s+$/g,"").Q(/\\s+/g,"|");H"\\\\b(?:"+a+")\\\\b"},5f:6(a){J.28={18:{1I:a.18,23:"1k"},1b:{1I:a.1b,23:"1k"},17:1f M("(?<18>"+a.18.1m+")(?<17>.*?)(?<1b>"+a.1b.1m+")","5o")}}};H e}();1j 2e!="1d"&&(2e.1v=1v);',62,441,'||||||function|||||||||||||||||||||||||||||||||||||return|if|this|var|length|XRegExp|null|for|index|replace|true||div|push|getParam|call|exec|else|prototype||false|lastIndex|config|arguments|RegExp|toolbar|code|left|captureNames|slice|right|id|undefined|split|new|class|addToken|indexOf|typeof|script|className|source|params|substr|apply|toString|String|line|title|gutter|SyntaxHighlighter|_xregexp|strings|lt|html|test|OUTSIDE_CLASS|match|brush|document|target|gt|getHtml|regex|global|join|style|highlight|break|concat|window|Math|isRegExp|throw|value|brushes|brushName|space|alert|vars|http|syntaxhighlighter|expandSource|size|css|case|font|Fa|name|htmlScript|dA|can|handler|gm|td|exports|color|in|href|first|discoveredBrushes|light|collapse|object|cache|getButtonHtml|trigger|pattern|getLineHtml|nbsp|numbers|parseInt|defaults|com|items|www|pad|highlighters|execute|focus|func|all|getDiv|parentNode|navigator|INSIDE_CLASS|regexList|hasFlag|Match|useScriptTags|hasNamedCapture|text|help|init|br|input|gi|Error|values|span|list|250|height|width|screen|top|500|tagName|findElements|getElementsByTagName|aboutDialog|_blank|appendChild|charAt|Array|copyAsGlobal|setFlag|highlighter_|string|attachEvent|nodeName|floor|backref|output|the|TypeError|sticky|Za|iterate|freezeTokens|scope|type|textarea|alexgorbatchev|version|margin|2010|005896|gs|regexLib|body|center|align|noBrush|require|childNodes|DTD|xhtml1|head|org|w3|url|preventDefault|container|tr|getLineNumbersHtml|isNaN|userAgent|tbody|isLineHighlighted|quick|void|innerHTML|create|table|links|auto|smart|tab|stripBrs|tabs|bloggerMode|collapsed|plain|getCodeLinesHtml|caption|getMatchesHtml|findMatches|figureOutLineNumbers|removeNestedMatches|getTitleHtml|brushNotHtmlScript|substring|createElement|Highlighter|load|HtmlScript|Brush|pre|expand|multiline|min|Can|ignoreCase|find|blur|extended|toLowerCase|aliases|addEventListener|innerText|textContent|wasn|select|createTextNode|removeChild|option|same|frame|xmlns|dtd|twice|1999|equiv|meta|htmlscript|transitional|1E3|expected|PUBLIC|DOCTYPE|on|W3C|XHTML|TR|EN|Transitional||configured|srcElement|Object|after|run|dblclick|matchChain|valueOf|constructor|default|switch|click|round|execAt|forHtmlScript|token|gimy|functions|getKeywords|1E6|escape|within|random|sgi|another|finally|supply|MSIE|ie|toUpperCase|catch|returnValue|definition|event|border|imsx|constructing|one|Infinity|from|when|Content|cellpadding|flags|cellspacing|try|xhtml|Type|spaces|2930402|hosted_button_id|lastIndexOf|donate|active|development|keep|to|xclick|_s|Xml|please|like|you|paypal|cgi|cmd|webscr|bin|highlighted|scrollbars|aspScriptTags|phpScriptTags|sort|max|scriptScriptTags|toolbar_item|_|command|command_|number|getElementById|doubleQuotedString|singleLinePerlComments|singleLineCComments|multiLineCComments|singleQuotedString|multiLineDoubleQuotedString|xmlComments|alt|multiLineSingleQuotedString|If|https|1em|000|fff|background|5em|xx|bottom|75em|Gorbatchev|large|serif|CDATA|continue|utf|charset|content|About|family|sans|Helvetica|Arial|Geneva|3em|nogutter|Copyright|syntax|close|write|2004|Alex|open|JavaScript|highlighter|July|02|replaceChild|offset|83'.split('|'),0,{}))
;
/**
 * SyntaxHighlighter
 * http://alexgorbatchev.com/SyntaxHighlighter
 *
 * SyntaxHighlighter is donationware. If you are using it, please donate.
 * http://alexgorbatchev.com/SyntaxHighlighter/donate.html
 *
 * @version
 * 3.0.83 (July 02 2010)
 * 
 * @copyright
 * Copyright (C) 2004-2010 Alex Gorbatchev.
 *
 * @license
 * Dual licensed under the MIT and GPL licenses.
 */
eval(function(p,a,c,k,e,d){e=function(c){return(c<a?'':e(parseInt(c/a)))+((c=c%a)>35?String.fromCharCode(c+29):c.toString(36))};if(!''.replace(/^/,String)){while(c--){d[e(c)]=k[c]||e(c)}k=[function(e){return d[e]}];e=function(){return'\\w+'};c=1};while(c--){if(k[c]){p=p.replace(new RegExp('\\b'+e(c)+'\\b','g'),k[c])}}return p}('(2(){1 h=5;h.I=2(){2 n(c,a){4(1 d=0;d<c.9;d++)i[c[d]]=a}2 o(c){1 a=r.H("J"),d=3;a.K=c;a.M="L/t";a.G="t";a.u=a.v=2(){6(!d&&(!8.7||8.7=="F"||8.7=="z")){d=q;e[c]=q;a:{4(1 p y e)6(e[p]==3)B a;j&&5.C(k)}a.u=a.v=x;a.D.O(a)}};r.N.R(a)}1 f=Q,l=h.P(),i={},e={},j=3,k=x,b;5.T=2(c){k=c;j=q};4(b=0;b<f.9;b++){1 m=f[b].w?f[b]:f[b].S(/\\s+/),g=m.w();n(m,g)}4(b=0;b<l.9;b++)6(g=i[l[b].E.A]){e[g]=3;o(g)}}})();',56,56,'|var|function|false|for|SyntaxHighlighter|if|readyState|this|length|||||||||||||||||true|document||javascript|onload|onreadystatechange|pop|null|in|complete|brush|break|highlight|parentNode|params|loaded|language|createElement|autoloader|script|src|text|type|body|removeChild|findElements|arguments|appendChild|split|all'.split('|'),0,{}))
;
/**
 * SyntaxHighlighter
 * http://alexgorbatchev.com/SyntaxHighlighter
 *
 * SyntaxHighlighter is donationware. If you are using it, please donate.
 * http://alexgorbatchev.com/SyntaxHighlighter/donate.html
 *
 * @version
 * 3.0.83 (July 02 2010)
 * 
 * @copyright
 * Copyright (C) 2004-2010 Alex Gorbatchev.
 *
 * @license
 * Dual licensed under the MIT and GPL licenses.
 */
;(function()
{
	// CommonJS
	typeof(require) != 'undefined' ? SyntaxHighlighter = require('shCore').SyntaxHighlighter : null;

	function Brush()
	{
		var keywords =	'break case catch continue ' +
						'default delete do else false  ' +
						'for function if in instanceof ' +
						'new null return super switch ' +
						'this throw true try typeof var while with'
						;

		var r = SyntaxHighlighter.regexLib;
		
		this.regexList = [
			{ regex: r.multiLineDoubleQuotedString,					css: 'string' },			// double quoted strings
			{ regex: r.multiLineSingleQuotedString,					css: 'string' },			// single quoted strings
			{ regex: r.singleLineCComments,							css: 'comments' },			// one line comments
			{ regex: r.multiLineCComments,							css: 'comments' },			// multiline comments
			{ regex: /\s*#.*/gm,									css: 'preprocessor' },		// preprocessor tags like #region and #endregion
			{ regex: new RegExp(this.getKeywords(keywords), 'gm'),	css: 'keyword' }			// keywords
			];
	
		this.forHtmlScript(r.scriptScriptTags);
	};

	Brush.prototype	= new SyntaxHighlighter.Highlighter();
	Brush.aliases	= ['js', 'jscript', 'javascript'];

	SyntaxHighlighter.brushes.JScript = Brush;

	// CommonJS
	typeof(exports) != 'undefined' ? exports.Brush = Brush : null;
})();
;
/**
@class Siesta.Test.Action.Role.HasTarget

This is a mixing, allowing the action to have "target" attribute, also aliased as "el"

*/
Role('Siesta.Test.Action.Role.HasTarget', {
    
        
    has : {
        /**
         * @cfg {Object/Function} target 
         * 
         * A target for action. The actual value varies depending from the action, but usually it will be a DOM element.
         * 
         * **Important.** If the function is provided for this config, it will be called and returning value used as actual target. 
         * This is useful, since sometimes target for the action depends from the previous step and 
         * is not yet available during `t.chain` call. 
         * 
         * For example, you want to click on the button which opens a window and then click on something in the window. Compare:
         * 

    t.chain(
        // clicking on button opens the window
        {
            action      : 'click',
            target      : buttonComp        
        },
        // FRAGILE: `windowComp` could not be rendered yet - `buttonComp` is not yet clicked!
        {
            action      : 'click',
            target      : windowComp.el.down('.clickArea')  
        }
        
        // MORE ROBUST: taking the "el" right before this action starts
        {
            action      : 'click',
            target      : function () {
                return windowComp.el.down('.clickArea')
            } 
        }
    )
         * 
         * This config option can also be provided as "el" 
         */
        target              : { required : true }
        
        /**
         * @cfg {Object} el 
         * 
         * An alias for {@link #target} 
         */
    },

    
    methods : {
        
        BUILD : function (config) {
            // allow "el" as synonym for "target"
            if (config.el && !config.target) config.target = config.el
            
            return config
        },
        

        getTarget : function () {
            if (this.test.typeOf(this.target) == 'Function')
                return this.target.call(this.test, this)
            else
                return this.target
        }
    }
});
;
/**

@class Siesta.Test.Action.Click
@extends Siesta.Test.Action
@mixin Siesta.Test.Action.Role.HasTarget

This action can be included in the `t.chain` call with "click" shortcut:

    t.chain(
        {
            action      : 'click',
            target      : someDOMElement
        }
    )

This action will perform a {@link Siesta.Test.Browser#click click} on the provided {@link #target}. 
Target can be a DOM element or, in case you are using the Siesta.Test.ExtJS class - an instance of Ext.Component 

*/
Class('Siesta.Test.Action.Click', {
    
    isa         : Siesta.Test.Action,
    
    does        : Siesta.Test.Action.Role.HasTarget,
        
    has : {
        requiredTestMethod  : 'click'
    },

    
    methods : {
        
        process : function () {
            this.test.click(this.getTarget(), this.next)
        }
    }
});


Siesta.Test.ActionRegistry.registerAction('click', Siesta.Test.Action.Click);
/**

@class Siesta.Test.Action.DoubleClick
@extends Siesta.Test.Action
@mixin Siesta.Test.Action.Role.HasTarget

This action will perform a {@link Siesta.Test.Browser#doubleClick double click} on the provided {@link #target}. 
Target can be a DOM element or, in case you are using the Siesta.Test.ExtJS class - an instance of Ext.Component 

This action can be included in the `t.chain` call with "doubleclick" or "doubleClick" shortcuts:

    t.chain(
        {
            action      : 'click',
            target      : someDOMElement
        }
    )


*/
Class('Siesta.Test.Action.DoubleClick', {
    
    isa         : Siesta.Test.Action,
    
    does        : Siesta.Test.Action.Role.HasTarget,
        
    has : {
        requiredTestMethod  : 'doubleClick'
    },

    
    methods : {
        
        process : function () {
            this.test.rightClick(this.getTarget(), this.next)
        }
    }
});


Siesta.Test.ActionRegistry.registerAction('doubleclick', Siesta.Test.Action.DoubleClick)
;
/**

@class Siesta.Test.Action.RightClick
@extends Siesta.Test.Action
@mixin Siesta.Test.Action.Role.HasTarget

This action will perform a {@link Siesta.Test.Browser#rightClick right click} on the provided {@link #target}. 
Target can be a DOM element or, in case you are using the Siesta.Test.ExtJS class - an instance of Ext.Component 

This action can be included in the `t.chain` call with "rightclick" or "rightClick" shortcuts:

    t.chain(
        {
            action      : 'rightclick',
            target      : someDOMElement
        }
    )


*/
Class('Siesta.Test.Action.RightClick', {
    
    isa         : Siesta.Test.Action,
    
    does        : Siesta.Test.Action.Role.HasTarget,
        
    has : {
        requiredTestMethod  : 'rightClick'
    },

    
    methods : {
        
        process : function () {
            this.test.rightClick(this.getTarget(), this.next)
        }
    }
});


Siesta.Test.ActionRegistry.registerAction('rightclick', Siesta.Test.Action.RightClick)
;
/**

@class Siesta.Test.Action.Type
@extends Siesta.Test.Action
@mixin Siesta.Test.Action.Role.HasTarget

This action will {@link Siesta.Test.Browser#type type} provided {@link #text} in the provided {@link #target}. 
Target can be a DOM element or, in case you are using the Siesta.Test.ExtJS class - an instance of Ext.Component (field component for example). 

This action can be included in the `t.chain` call with "type" shortcut:

    t.chain(
        {
            action      : 'type',
            target      : someDOMElement,
            text        : 'Some text'
        }
    )


*/
Class('Siesta.Test.Action.Type', {
    
    isa         : Siesta.Test.Action,
    
    does        : Siesta.Test.Action.Role.HasTarget,
        
    has : {
        requiredTestMethod  : 'type',
        
        /**
         * @cfg {String} text
         * 
         * A text to type into target
         */
        text                : ''
    },

    
    methods : {
        
        process : function () {
            this.test.type(this.getTarget(), this.text, this.next)
        }
    }
});


Siesta.Test.ActionRegistry.registerAction('type', Siesta.Test.Action.Type);
/**

@class Siesta.Test.Action.Drag
@extends Siesta.Test.Action

This action can be included in the `t.chain` call with the "drag" shortcut:

    t.chain(
        {
            action      : 'drag',
            target      : someDOMElementOrArray,
            to          : someDOMElementOrArray
        },
        {
            action      : 'drag',
            target      : someDOMElementOrArray,
            by          : [ 10, 10 ]
        }
    )

This action will perform a {@link Siesta.Test.Browser#dragTo dragTo} or {@link Siesta.Test.Browser#dragBy dragBy} actions on the provided {@link #target}. 
Target can be a DOM element or, in case you are using the Siesta.Test.ExtJS class - an instance of Ext.Component 

*/
Class('Siesta.Test.Action.Drag', {
    
    isa         : Siesta.Test.Action,
    
    does        : Siesta.Test.Action.Role.HasTarget,
    
    has : {
        requiredTestMethod  : 'dragTo',
        
        /**
         * @cfg {Array/HTMLElement/Function} target
         * 
         * The initial point of dragging operation. Can be provided as the DOM element, the array with screen coordinates: `[ x, y ]`, or the function
         * returning one of those.
         */
         
        /**
         * @cfg {Array/HTMLElement/Function} source
         * 
         * Alias for {@link #target}. This may sound confusing, but "target" of "drag" action is its "source" in the same time.   
         */
         
        
        /**
         * @cfg {Array/HTMLElement/Function} to 
         * 
         * The target point of dragging operation. Can be provided as the DOM element, the array with screen coordinates: `[ x, y ]`, or the function
         * returning one of those.
         * 
         * Exactly one of the `to` and `by` configuration options should be provided for this action.
         */
        to                  : null,
        
        /**
         * @cfg {Array/Function} by 
         * 
         * The delta for dragging operation. Should be provided as the array with delta value for each coordinate: `[ dX, dY ]` or the function returning such.
         * 
         * Exactly one of the `to` and `by` configuration options should be provided for this action.
         */
        by                  : null,
        
        
        /**
         * @cfg {Boolean} dragOnly
         * 
         * True to skip the mouseup and not finish the drop operation (one can start another drag operation, emulating the pause during drag-n-drop).
         */
        dragOnly        : false,
        
        options         : null
    },

    
    override : {
        BUILD : function (config) {
            // allow "source" as synonym for "target"
            // sounds weird, but "target" in action domain means source point for dragging 
            if (config.source && !config.target) config.target = config.source
            
            return this.SUPER(config)
        }
    },
    
    
    methods : {
        
        initialize : function () {
            this.SUPER()
            
            if (!this.to && !this.by)   throw 'Either "to" or "by" configuration option is required for "drag" step' 
            if (this.to && this.by)     throw 'Exactly one of "to" or "by" configuration options is required for "drag" step, not both'
        },
        
        
        getTo : function () {
            if (this.test.typeOf(this.to) == 'Function')
                return this.to.call(this.test, this)
            else
                return this.to
        },
        
        
        getBy : function () {
            if (this.test.typeOf(this.by) == 'Function')
                return this.by.call(this.test, this)
            else
                return this.by
        },

        
        process : function () {
            if (this.to)
                this.test.dragTo(this.getTarget(), this.getTo(), this.next, null, this.options, this.dragOnly)
            else
                this.test.dragBy(this.getTarget(), this.getBy(), this.next, null, this.options, this.dragOnly)
        }
    }
});


Siesta.Test.ActionRegistry.registerAction('drag', Siesta.Test.Action.Drag);
/**
@class Siesta.Test.Simulate.Mouse

This is a mixin, providing the mouse events simulation functionality.
*/

//        Copyright (c) 2011 John Resig, http://jquery.com/

//        Permission is hereby granted, free of charge, to any person obtaining
//        a copy of this software and associated documentation files (the
//        "Software"), to deal in the Software without restriction, including
//        without limitation the rights to use, copy, modify, merge, publish,
//        distribute, sublicense, and/or sell copies of the Software, and to
//        permit persons to whom the Software is furnished to do so, subject to
//        the following conditions:

//        The above copyright notice and this permission notice shall be
//        included in all copies or substantial portions of the Software.

//        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
//        EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
//        MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
//        NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
//        LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
//        OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
//        WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


Role('Siesta.Test.Simulate.Mouse', {
    
    requires        : [ 'simulateEvent', 'getSimulateEventsWith' ],    
    
    has: {
        /**
         *  @cfg {Int} dragDelay The delay between individual drag events (mousemove)
         */
        dragDelay               : 25,

        /**
         *  @cfg {Boolean} moveCursorBetweenPoints True to move the mouse cursor between for example two clicks on separate elements (for better visual experience)
         */
        moveCursorBetweenPoints : true,

        /**
         *  @cfg {Int} dragPrecision Defines how precisely to follow the path between two points when simulating a drag. 2 indicates every other point will be used.
                                     (low value = slow dragging, high value = fast dragging)
        */
        dragPrecision           : $.browser.msie ? 10 : 5,
        
        currentPosition : {
           init : function () { return [0, 0]; }
        },

        overEls : {
           init : function () { return []; }
        }
    },


    methods: {
        // private
        createMouseEvent: function (type, options, el) {
            var event;
            
            options = $.extend({
                bubbles     : type !== 'mouseenter' && type !== 'mouseleave', 
                cancelable  : type != "mousemove", 
                view        : this.global, 
                detail      : 0,

                screenX     : 0,
                screenY     : 0,

                ctrlKey     : false,
                altKey      : false,
                shiftKey    : false,
                metaKey     : false,

                button          : 0,
                relatedTarget   : undefined

            }, options);

            if (!("clientX" in options) || !("clientY" in options)) {
                var center = this.findCenter(el);

                options = $.extend({
                    clientX: center[0],
                    clientY: center[1]
                }, options);
            }

            var doc = this.global.document;

            // use W3C standard when available and allowed by "simulateEventsWith" option
            if (doc.createEvent && this.getSimulateEventsWith() == 'dispatchEvent') {
                event = doc.createEvent("MouseEvents");

                event.initMouseEvent(
                    type, options.bubbles, options.cancelable, options.view, options.detail,
                    options.screenX, options.screenY, options.clientX, options.clientY,
                    options.ctrlKey, options.altKey, options.shiftKey, options.metaKey,
                    options.button, options.relatedTarget || doc.body.parentNode
                );
                
                
            } else if (doc.createEventObject) {
                event = doc.createEventObject();

                $.extend(event, options);

                event.button = { 0: 1, 1: 4, 2: 2 }[event.button] || event.button;
            }

            // Mouse over is used in some certain edge cases which interfer with this tracking
            if (type !== 'mouseover' && type !== 'mouseout') {
                this.currentPosition = [options.clientX, options.clientY];
            }
            return event;
        },
        
        /**
        * This method will simulate a mouse move to an xy-coordinate or an element (the center of it)
        * @param {HTMLElement/Array} target Either an element, or [x,y] as the target point
        * @param {Function} callback (optional) To run this method async, provide a callback method to be called after the operation is completed.
        * @param {Object} scope (optional) the scope for the callback
        */
        moveMouseTo : function(target, callback, scope) {
            // Normalize target
            if (!this.isArray(target)) {
                target = this.detectCenter(target);
            }
            this.moveMouse(this.currentPosition, target, callback, scope);
        },

        /**
        * This method will simulate a mouse move by an x a y delta amount
        * @param {Array} delta The delta x and y distance to move, e.g. [20, 20] for 20px down/right, or [0, 10] for just 10px down.
        * @param {Function} callback (optional) To run this method async, provide a callback method to be called after the operation is completed.
        * @param {Object} scope (optional) the scope for the callback
        */
        moveMouseBy : function(delta, callback, scope) {
            // Normalize target
            var target = [this.currentPosition[0] + delta[0], this.currentPosition[1] + delta[1]];

            this.moveMouse(this.currentPosition, target, callback, scope);
        },

        // private
        moveMouse : function(xy, xy2, callback, scope, precision, async, options) {
            var a           = this.beginAsync(),
                document    = this.global.document,
                me          = this,
                lastOverEl,
                overEls     = this.overEls;
            
            precision = (precision || me.dragPrecision);
            options = options || {};
            
            var path        = this.getPathBetweenPoints(xy, xy2).concat([xy2]);

            var queue       = new Siesta.Util.Queue({
                deferer         : this.originalSetTimeout,
                deferClearer    : this.originalClearTimeout,
                
                interval        : async !== false ? this.dragDelay : 0,
                callbackDelay   : async !== false ? 50 : 0,
                
                observeTest     : this,
                
                processor   : function (data, index) {
                    var fromIndex = data.sourceIndex,
                        toIndex = data.targetIndex;

                    for (var j = fromIndex; j <= toIndex; j++) {
                        var point       = path[j];
                        var targetEl    = document.elementFromPoint(point[0], point[1]) || document.body;
                        
                        if (targetEl !== lastOverEl) {
                            if (Siesta.supports.mouseEnterLeave) {
                                for (var i = overEls.length - 1; i >= 0; i--) {
                                    var el = overEls[i];
                                    if (el !== targetEl && me.$(el).has(targetEl).length === 0) {
                                        me.simulateEvent(el, "mouseleave", $.extend({ clientX: point[0], clientY: point[1], relatedTarget : targetEl}, options));
                                        overEls.splice(i, 1);
                                    }
                                }
                            }
                            if (lastOverEl) {
                                me.simulateEvent(lastOverEl, "mouseout", $.extend({ clientX: point[0], clientY: point[1], relatedTarget : targetEl}, options));
                            }
                            if (Siesta.supports.mouseEnterLeave && jQuery.inArray(targetEl, overEls) < 0) {
                                me.simulateEvent(targetEl, "mouseenter", $.extend({ clientX: point[0], clientY: point[1], relatedTarget : lastOverEl}, options));
                            
                                overEls.push(targetEl);
                            }
                            me.simulateEvent(targetEl, "mouseover", $.extend({ clientX: point[0], clientY: point[1], relatedTarget : lastOverEl}, options));
                            lastOverEl = targetEl;
                        }
                     
                        me.simulateEvent(targetEl, "mousemove", $.extend({ clientX: point[0], clientY: point[1]}, options), j < toIndex);
                    }
                }
            });
            
            for (var i = 0, l = path.length; i < l; i += precision) {
                queue.addStep({
                    sourceIndex       : i,
                    targetIndex       : Math.min(i + precision, path.length - 1)
                });
            }

            queue.run(function () {
                me.endAsync(a);
                
                callback && callback.call(scope || me);
            })
        },
        
        
        normalizeTarget : function (el) {
            var document    = this.global.document;
            var xy
            
            el              = el || this.currentPosition;
            
            if (this.isArray(el)) {
                xy          = el;
                el          = document.elementFromPoint(xy[0], xy[1]) || document.body;
                options     = { clientX : xy[0], clientY : xy[1] };
            } else {
                xy          = this.detectCenter(el);
                el          = document.elementFromPoint(xy[0], xy[1]) || document.body;
                options     = { clientX : xy[0], clientY : xy[1] };
            }
            
            return {
                el          : el,
                xy          : xy,
                options     : { clientX : xy[0], clientY : xy[1] }
            }
        },
        

        genericMouseClick : function (el, callback, scope, clickMethod) {
            if (jQuery.isFunction(el)) {
                scope       = callback;
                callback    = el; 
                el          = null;
            } 
            
            var data        = this.normalizeTarget(el)

            // the asynchronous case
            if (this.moveCursorBetweenPoints && callback) {
                this.syncCursor(data.xy, this[ clickMethod ], [ data.el, callback, scope, data.options ]);
            } else {
                this[ clickMethod ](data.el, callback, scope, data.options);
            }
        },
        
        
        /**
         * This method will simulate a mouse click in the center of the specified DOM/Ext element.
         * 
         * Note, that it will first calculate the centeral point of the specified element and then 
         * will pick the top-most DOM element from that point. For example, if you will provide a grid row as the `el`,
         * then click will happen on top of the central cell, and then will bubble to the row itself.
         * In most cases this is the desired behavior.  
         * 
         * The following events will be fired, in order:  `mouseover`, `mousedown`, `mouseup`, `click`
         * 
         * Example:
         * 
         *      t.click(t.getFirstRow(grid), function () { ... })
         * 
         * The 1st argument for this method can be omitted. In this case, Siesta will use the current cursor position:
         * 
         *      t.click(function () { ... })
         *   
         * @param {HTMLElement/Array} (optional) el The element to click, or the array with XY coordinates 
         * @param {Function} callback (optional) A function to call when the condition has been met.
         * @param {Object} scope (optional) The scope for the callback 
         */
        click: function (el, callback, scope) {
            this.genericMouseClick(el, callback, scope, 'simulateMouseClick')
        },

        
        /**
         * This method will simulate a mouse right click in the center of the specified DOM/Ext element.
         * 
         * Note, that it will first calculate the centeral point of the specified element and then 
         * will pick the top-most DOM element from that point. For example, if you will provide a grid row as the `el`,
         * then click will happen on top of the central cell, and then will bubble to the row itself.
         * In most cases this is the desired behavior.  
         * 
         * The following events will be fired, in order:  `mouseover`, `mousedown`, `mouseup`, `contextmenu`
         * 
         * Example:
         * 
         *      t.click(t.getFirstRow(grid), function () { ... })
         * 
         * The 1st argument for this method can be omitted. In this case, Siesta will use the current cursor position:
         * 
         *      t.click(function () { ... })
         *   
         * @param {HTMLElement/Array} (optional) el The element to click, or the array with XY coordinates 
         * @param {Function} callback (optional) A function to call when the condition has been met.
         * @param {Object} scope (optional) The scope for the callback 
         */
        rightClick: function (el, callback, scope) {
            this.genericMouseClick(el, callback, scope, 'simulateRightClick')
        },

        
        /**
         * This method will simulate a mouse double click in the center of the specified DOM/Ext element.
         * 
         * Note, that it will first calculate the centeral point of the specified element and then 
         * will pick the top-most DOM element from that point. For example, if you will provide a grid row as the `el`,
         * then click will happen on top of the central cell, and then will bubble to the row itself.
         * In most cases this is the desired behavior.  
         * 
         * The following events will be fired, in order:  `mouseover`, `mousedown`, `mouseup`, `click`, `mousedown`, `mouseup`, `click`, `dblclick`
         * 
         * Example:
         * 
         *      t.click(t.getFirstRow(grid), function () { ... })
         * 
         * The 1st argument for this method can be omitted. In this case, Siesta will use the current cursor position:
         * 
         *      t.click(function () { ... })
         *   
         * @param {HTMLElement/Array} (optional) el The element to click, or the array with XY coordinates 
         * @param {Function} callback (optional) A function to call when the condition has been met.
         * @param {Object} scope (optional) The scope for the callback 
         */
        doubleClick: function (el, callback, scope) {
            this.genericMouseClick(el, callback, scope, 'simulateDoubleClick')
        },

        /**
         * This method will simulate a mousedown event in the center of the specified DOM element.
         * @param {HTMLElement} el
         * @param {Object} options any extra options used to configure the DOM event
         */
        mouseDown: function (el, options) {
            if (!el) {
                el = this.getElementAtCursor();
                options = $.extend({ clientX: this.currentPosition[0], clientY: this.currentPosition[1]}, options);
            }
            this.simulateEvent(el, 'mousedown', options);
        },

         /**
         * This method will simulate a mousedown event in the center of the specified DOM element.
         * @param {HTMLElement} el
         * @param {Object} options any extra options used to configure the DOM event
         */
        mouseUp: function (el, options) {
            if (!el) {
                el = this.getElementAtCursor();
                options = $.extend({ clientX: this.currentPosition[0], clientY: this.currentPosition[1]}, options);
            }
            this.simulateEvent(el, 'mouseup', options);
        },

        /**
         * This method will simulate a mouseover event in the center of the specified DOM element.
         * @param {HTMLElement} el
         * @param {Object} options any extra options used to configure the DOM event
         */
        mouseOver: function (el, options) {
            if (!el) {
                el = this.getElementAtCursor();
                options = $.extend({ clientX: this.currentPosition[0], clientY: this.currentPosition[1]}, options);
            }
            this.simulateEvent(el, 'mouseover', options);
        },

        /**
         * This method will simulate a mouseout event in the center of the specified DOM element.
         * @param {HTMLElement} el
         * @param {Object} options any extra options used to configure the DOM event
         */        
        mouseOut: function (el, options) {
            if (!el) {
                el = this.getElementAtCursor();
                options = $.extend({ clientX: this.currentPosition[0], clientY: this.currentPosition[1]}, options);
            }
            this.simulateEvent(el, 'mouseout', options);
        },

        // private
        simulateRightClick: function (el, callback, scope, options) {
            var me          = this;
            
            var queue       = new Siesta.Util.Queue({
                deferer         : this.originalSetTimeout,
                deferClearer    : this.originalClearTimeout,
                
                interval        : callback ? 10 : 0,
                callbackDelay   : me.afterActionDelay,
                
                observeTest     : this,
                
                processor   : function (data) {
                    me.simulateEvent.apply(me, data);
                }
            })
            
            queue.addStep([ el, "mouseover", options, true ])
            queue.addStep([ el, "mousedown", options, false ])
            queue.addStep([ el, "mouseup", options, true ])
            
            queue.addStep({
                processor       : function () {
                    me.simulateEvent(el, "contextmenu", options, false);
//  do we need to focus the element on the right click?                  
//                    try { el.focus() } catch (e) {}
                }
            })
            
            var async   = me.beginAsync();
            
            queue.run(function () {
                me.endAsync(async);
                
                callback && callback.call(scope || me);  
            })
        }, 

        // private
        simulateMouseClick: function (el, callback, scope, options) {
            var me          = this;
            
            var queue       = new Siesta.Util.Queue({
                deferer         : this.originalSetTimeout,
                deferClearer    : this.originalClearTimeout,
                
                interval        : callback ? 10 : 0,
                callbackDelay   : me.afterActionDelay,
                
                observeTest     : this,
                
                processor   : function (data) {
                    me.simulateEvent.apply(me, data);
                }
            })
            
            queue.addStep([ el, "mouseover", options, true ])
            queue.addStep([ el, "mousedown", options, false ])
            queue.addStep([ el, "mouseup", options, true ])
            
            queue.addStep({
                processor       : function () {
                    me.simulateEvent(el, "click", options, false);
                    
                    try { el.focus() } catch (e) {}
                }
            })
            
            var async   = me.beginAsync();
            
            queue.run(function () {
                me.endAsync(async);
                
                callback && callback.call(scope || me);  
            })
        },

        // private
        simulateDoubleClick: function (el, callback, scope, options) {
            var me          = this;
            
            var queue       = new Siesta.Util.Queue({
                deferer         : this.originalSetTimeout,
                deferClearer    : this.originalClearTimeout,
                
                interval        : callback ? 10 : 0,
                callbackDelay   : me.afterActionDelay,
                
                observeTest     : this,
                
                processor   : function (data) {
                    me.simulateEvent.apply(me, data);
                }
            })
            
            queue.addStep([ el, "mouseover", options, true ])
            queue.addStep([ el, "mousedown", options, false ])
            queue.addStep([ el, "mouseup", options, true ])
            queue.addStep([ el, "click", options, true ])
            queue.addStep([ el, "mousedown", options, false ])
            queue.addStep([ el, "mouseup", options, true ])
            queue.addStep([ el, "click", options, true ])
            
            queue.addStep({
                processor       : function () {
                    me.simulateEvent(el, "dblclick", options, false);
                    
                    try { el.focus() } catch (e) {}
                }
            })
            
            var async   = me.beginAsync();
            
            queue.run(function () {
                me.endAsync(async);
                
                callback && callback.call(scope || me);  
            })
        }, 

        // private
        syncCursor : function(toXY, callback, args) {
            var me          = this
            var fromXY      = this.currentPosition;
            
            if (toXY[0] !== fromXY[0] || toXY[1] !== fromXY[1]) {
                var async = this.beginAsync();
                
                this.moveMouse(fromXY, toXY, function() { 
                    me.endAsync(async); 
                    callback && callback.apply(me, args); 
                });
            } else 
                // already aligned
                callback && callback.apply(this, args);
        },


        /**
        * This method will simulate a drag and drop operation between either two points or two DOM elements.
        * The following events will be fired, in order:  `mouseover`, `mousedown`, `mousemove` (along the mouse path), `mouseup`
        * 
        * This method is deprecated in favor of {@link #dragTo} and {@link #dragBy} methods
        *   
        * @param {HTMLElement/Array} source Either an element, or [x,y] as the drag starting point
        * @param {HTMLElement/Array} target (optional) Either an element, or [x,y] as the drag end point
        * @param {Array} delta (optional) the amount to drag from the source coordinate, expressed as [x,y]. [50, 10] will drag 50px to the right and 10px down.
        * @param {Function} callback (optional) To run this method async, provide a callback method to be called after the drag operation is completed.
        * @param {Object} scope (optional) the scope for the callback
        * @param {Object} options any extra options used to configure the DOM event
        */
        drag: function (source, target, delta, callback, scope, options) {
            if (!source) {
                throw 'No drag source defined';
            }

            if (target) {
                this.dragTo(source, target, callback, scope, options);
            } else {
                this.dragBy(source, delta, callback, scope, options);
            }
        },

        /**
        * This method will simulate a drag and drop operation between either two points or two DOM elements.
        * The following events will be fired, in order:  `mouseover`, `mousedown`, `mousemove` (along the mouse path), `mouseup`
        *   
        * @param {HTMLElement/Array} source Either an element, or [x,y] as the drag starting point
        * @param {HTMLElement/Array} target Either an element, or [x,y] as the drag end point
        * @param {Function} callback (optional) To run this method async, provide a callback method to be called after the drag operation is completed.
        * @param {Object} scope (optional) the scope for the callback
        * @param {Object} options any extra options used to configure the DOM event
        * @param {Boolean} dragOnly true to skip the mouseup and not finish the drop operation.
        */
        dragTo : function(source, target, callback, scope, options, dragOnly) {
            if (!source) {
                throw 'No drag source defined';
            }
            if (!target) {
                throw 'No drag target defined';
            }
            var sourceXY, targetXY;
            
            options = options || {};
            
            // Normalize source
            if (this.isArray(source)) {
                sourceXY = source;
            } else {
                sourceXY = this.detectCenter(source);
            }

            // Normalize target
            if (this.isArray(target)) {
                targetXY = target;
            } else {
                targetXY = this.findCenter(target);
            }

            var args = [sourceXY, targetXY, callback, scope, options, dragOnly];
            
            if (this.moveCursorBetweenPoints && callback) {
                this.syncCursor(sourceXY, this.simulateDrag, args);
            } else {
                this.simulateDrag.apply(this, args)
            }
        },

        /**
        * This method will simulate a drag and drop operation from a point (or DOM element) and move by a delta.
        * The following events will be fired, in order:  `mouseover`, `mousedown`, `mousemove` (along the mouse path), `mouseup`
        *   
        * @param {HTMLElement/Array} source Either an element, or [x,y] as the drag starting point
        * @param {Array} delta The amount to drag from the source coordinate, expressed as [x,y]. E.g. [50, 10] will drag 50px to the right and 10px down.
        * @param {Function} callback (optional) To run this method async, provide a callback method to be called after the drag operation is completed.
        * @param {Object} scope (optional) the scope for the callback
        * @param {Object} options any extra options used to configure the DOM event
        * @param {Boolean} dragOnly true to skip the mouseup and not finish the drop operation.
        */
        dragBy : function(source, delta, callback, scope, options, dragOnly) {
            if (!source) {
                throw 'No drag source defined';
            }
            if (!delta) {
                throw 'No drag delta defined';
            }
            var sourceXY, targetXY;

            // Normalize source
            if (this.isArray(source)) {
                sourceXY = source;
            } else {
                sourceXY = this.detectCenter(source);
            }
            targetXY = [sourceXY[0] + delta[0], sourceXY[1] + delta[1]];
            
            var args = [sourceXY, targetXY, callback, scope, options, dragOnly];
            
            if (this.moveCursorBetweenPoints && callback) {
                this.syncCursor(sourceXY, this.simulateDrag, args);
            } else {
                this.simulateDrag.apply(this, args)
            }
        },
        
        // private
        simulateDrag: function (sourceXY, targetXY, callback, scope, options, dragOnly) {
            var global = this.global,
                document = global.document,
                source,
                target;
            
            options = options || {};

            source = document.elementFromPoint(sourceXY[0], sourceXY[1]) || document.body;
            target = document.elementFromPoint(targetXY[0], targetXY[1]) || document.body;
            
            var me          = this;
            
            var queue       = new Siesta.Util.Queue({
                deferer         : this.originalSetTimeout,
                deferClearer    : this.originalClearTimeout,
                
                interval        : me.dragDelay,
                callbackDelay   : me.afterActionDelay,
                
                observeTest     : this
            });
            
            queue.addStep({
                processor : function () {
                    me.simulateEvent(source, "mouseover", $.extend({ clientX: sourceXY[0], clientY: sourceXY[1]}, options));
                }
            });
            
            queue.addStep({
                processor : function () {
                    // Fetch source el again since the mouseover might trigger another element to go visible.
                    source = document.elementFromPoint(sourceXY[0], sourceXY[1]) || document.body;
                    me.simulateEvent(source, "mouseover", $.extend({ clientX: sourceXY[0], clientY: sourceXY[1]}, options));
                }
            });
            
            queue.addStep({
                processor : function () {
                    me.simulateEvent(source, "mousedown", $.extend({ clientX: sourceXY[0], clientY: sourceXY[1]}, options));
                }
            });
            
            queue.addStep({
                isAsync     : true,
                
                processor   : function (data) {
                    me.moveMouse(sourceXY, targetXY, data.next, this, null, true, options);
                }
            });
            
            var el;
            
            queue.addStep({
                processor : function () {
                    el = document.elementFromPoint(targetXY[0], targetXY[1]) || document.body;
                    me.simulateEvent(el, 'mouseover', $.extend({ clientX: targetXY[0], clientY: targetXY[1] }, options)); 
                }
            });
            
            if (!dragOnly) {
                queue.addStep({
                    processor : function () {
                        me.simulateEvent(el, 'mouseup', $.extend({ clientX: targetXY[0], clientY: targetXY[1] }, options)); 
                    }
                });
            
//            queue.addStep({
//                processor : function () {
//                    if (me.simulateEvent(el, 'click', $.extend({ clientX: targetXY[0], clientY: targetXY[1] }, options)); 
//                }
//            });
            }
            
            
            var async       = this.beginAsync();
            
            queue.run(function () {
                me.endAsync(async)
                
                callback && callback.call(scope || me)
            });
        },
        
        detectCenter : function(el) {
            var hidden = !this.isElementVisible(el);

            // Trigger mouseover in case source is hidden, possibly shown only when hovering over it (its x/y cannot be determined if display:none)
            if (hidden) {
                this.simulateEvent(el, "mouseover", { clientX: 0, clientY: 0});
            }
            var center = this.findCenter(el);
            if (hidden) {
                this.simulateEvent(el, "mouseout", { clientX: 0, clientY: 0});
            }

            return center;
        },

        getElementAtCursor : function() {
            var xy          = this.currentPosition,
                document    = this.global.document;
            
            return document.elementFromPoint(xy[0], xy[1]) || document.body;
        }
    }
});

;
/**
@class Siesta.Test.Simulate.KeyCodes
@singleton

This is a singleton class, containing the mnemonical names for various advanced key codes. You can use this names in the {@link Siesta.Test.Browser#type} method, like this:

        t.type(el, 'Foo bar[ENTER]', function () {
            ...
        })
        
Below is the full list:

    - `BACKSPACE`

    - `TAB`

    - `RETURN`
    - `ENTER`

    - `SHIFT`
    - `CTRL`
    - `ALT`

    - `PAUSE-BREAK`
    - `CAPS`
    - `ESCAPE`
    - `NUM-LOCK`
    - `SCROLL-LOCK`
    - `PRINT`

    - `PAGE-UP`
    - `PAGE-DOWN`
    - `END`
    - `HOME`
    - `LEFT`
    - `UP`
    - `RIGHT`
    - `DOWN`
    - `INSERT`
    - `DELETE`


    - `NUM0`
    - `NUM1`
    - `NUM2`
    - `NUM3`
    - `NUM4`
    - `NUM5`
    - `NUM6`
    - `NUM7`
    - `NUM8`
    - `NUM9`

    - `F1`
    - `F2`
    - `F3`
    - `F4`
    - `F5`
    - `F6`
    - `F7`
    - `F8`
    - `F9`
    - `F10`
    - `F11`
    - `F12`
  
*/
Singleton('Siesta.Test.Simulate.KeyCodes', {

    methods : {
        isNav : function(k) {
            var keys        = this.keys
            
            return (k >= 33 && k <= 40) ||
                    k == keys.RETURN ||
                    k == keys.TAB ||
                    k == keys.ESCAPE;
        }, 

        isSpecial : function(k) {

            return (k === this.keys.BACKSPACE) ||
                   (k >= 16 && k <= 20) ||
                   (k >= 44 && k <= 46);
        }
    },

    has : {
        // FROM Syn library by JupiterJS, MIT License. www.jupiterjs.com

        // key codes
        keys : {
            
            init : {
                //backspace
                '\b': 8,
                'BACKSPACE': 8,
        
                //tab
                '\t': 9,
                'TAB': 9,
        
                //enter
                '\r': 13,
                'RETURN': 13,
                'ENTER': 13,
        
                //special
                'SHIFT': 16,
                'CTRL': 17,
                'ALT': 18,
        
                //weird
                'PAUSE-BREAK': 19,
                'CAPS': 20,
                'ESCAPE': 27,
                'NUM-LOCK': 144,
                'SCROLL-LOCK': 145,
                'PRINT': 44,
        
                //navigation
                'PAGE-UP': 33,
                'PAGE-DOWN': 34,
                'END': 35,
                'HOME': 36,
                'LEFT': 37,
                'UP': 38,
                'RIGHT': 39,
                'DOWN': 40,
                'INSERT': 45,
                'DELETE': 46,
        
                //normal characters
                ' ': 32,
                '0': 48,
                '1': 49,
                '2': 50,
                '3': 51,
                '4': 52,
                '5': 53,
                '6': 54,
                '7': 55,
                '8': 56,
                '9': 57,
                'A': 65,
                'B': 66,
                'C': 67,
                'D': 68,
                'E': 69,
                'F': 70,
                'G': 71,
                'H': 72,
                'I': 73,
                'J': 74,
                'K': 75,
                'L': 76,
                'M': 77,
                'N': 78,
                'O': 79,
                'P': 80,
                'Q': 81,
                'R': 82,
                'S': 83,
                'T': 84,
                'U': 85,
                'V': 86,
                'W': 87,
                'X': 88,
                'Y': 89,
                'Z': 90,
        
                //NORMAL-CHARACTERS, NUMPAD
                'NUM0': 96,
                'NUM1': 97,
                'NUM2': 98,
                'NUM3': 99,
                'NUM4': 100,
                'NUM5': 101,
                'NUM6': 102,
                'NUM7': 103,
                'NUM8': 104,
                'NUM9': 105,
                '*': 106,
                '+': 107,
                '-': 109,
                '.': 110,
        
                //normal-characters, others
                '/': 111,
                ';': 186,
                '=': 187,
                ',': 188,
                '-': 189,
                '.': 190,
                '/': 191,
                '`': 192,
                '[': 219,
                '\\': 220,
                ']': 221,
                "'": 222,
        
                'F1': 112,
                'F2': 113,
                'F3': 114,
                'F4': 115,
                'F5': 116,
                'F6': 117,
                'F7': 118,
                'F8': 119,
                'F9': 120,
                'F10': 121,
                'F11': 122,
                'F12': 123
            } 
        }
        // eof key codes
    }
});;
/**
@class Siesta.Test.Simulate.Keyboard

This is a mixin, providing the keyboard events simulation functionality.

  
*/

//        Copyright (c) 2011 John Resig, http://jquery.com/

//        Permission is hereby granted, free of charge, to any person obtaining
//        a copy of this software and associated documentation files (the
//        "Software"), to deal in the Software without restriction, including
//        without limitation the rights to use, copy, modify, merge, publish,
//        distribute, sublicense, and/or sell copies of the Software, and to
//        permit persons to whom the Software is furnished to do so, subject to
//        the following conditions:

//        The above copyright notice and this permission notice shall be
//        included in all copies or substantial portions of the Software.

//        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
//        EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
//        MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
//        NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
//        LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
//        OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
//        WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


Role('Siesta.Test.Simulate.Keyboard', {
    
    requires        : [ 'simulateEvent', 'getSimulateEventsWith' ],

    methods: {
        // private
        createKeyboardEvent: function (type, options, el) {
            var evt;

            var e = $.extend({ bubbles: true, cancelable: true, view: this.global,
                ctrlKey: false, altKey: false, shiftKey: false, metaKey: false,
                keyCode: 0, charCode: 0
            }, options);

            var doc = this.global.document;

            // use W3C standard when available and allowed by "simulateEventsWith" option
            if (doc.createEvent && this.getSimulateEventsWith() == 'dispatchEvent') {
                try {
                    evt = doc.createEvent("KeyEvents");
                    evt.initKeyEvent(type, e.bubbles, e.cancelable, e.view, e.ctrlKey, e.altKey, e.shiftKey, e.metaKey, e.keyCode, e.charCode);
                } catch (err) {
                    evt = doc.createEvent("Events");
                    evt.initEvent(type, e.bubbles, e.cancelable);
                    $.extend(evt, { view: e.view,
                        ctrlKey: e.ctrlKey, altKey: e.altKey, shiftKey: e.shiftKey, metaKey: e.metaKey,
                        keyCode: e.keyCode, charCode: e.charCode
                    });
                }
            } else if (doc.createEventObject) {
                evt = doc.createEventObject();
                $.extend(evt, e);
            }
            if ($.browser.msie || $.browser.opera) {
                evt.keyCode = (e.charCode > 0) ? e.charCode : e.keyCode;
                evt.charCode = undefined;
            }
            return evt;
        },

        // private
        createTextEvent: function (type, options, el) {
            var doc = this.global.document;
            var event = null;

            // only for Webkit for now
            if (doc.createEvent && !$.browser.msie) {
                try {
                    event = doc.createEvent('TextEvent');

                    if (event && event.initTextEvent) {
                        event.initTextEvent('textInput', true, true, this.global, options.text, 0);
                        return event;
                    }
                }
                catch(e) {}
            }

            return null;
        },


        /**
        * This method will simulate text typing, on a specified DOM element. Simulation of certain advanced keys is supported.
        * You can include the name of such key in the square brackets into the 2nd argument. See {@link Siesta.Test.Simulate.KeyCodes} for a list 
        * of key names.
        * 
        * For example:
        * 

    t.type(el, 'Foo bar[ENTER]', function () {
        ...
    })
        *  
        * The following events will be fired, in order: `keydown`, `keypress`, `keyup`
        *   
        * @param {HTMLElement} el The element to type into
        * @param {String} text the text to type, possible containig the names of special keys in square brackets.
        * @param {Function} callback (optional) To run this method async, provide a callback method to be called after the type operation is completed.
        * @param {Object} scope (optional) the scope for the callback
        */
        type: function (el, text, callback, scope) {
            el              = this.normalizeElement(el);
            
            var me          = this

            // Extract normal chars, or special keys in brackets such as [TAB], [RIGHT] or [ENTER]			
            var keys        = (text + '').match(/\[([^\])]+\])|([^\[])/g);
            
            var queue       = new Siesta.Util.Queue({
                
                deferer         : this.originalSetTimeout,
                deferClearer    : this.originalClearTimeout,
                
                interval        : this.actionDelay,
                callbackDelay   : this.afterActionDelay,
                
                observeTest     : this,
                
                processor       : function (data, index) {
                    me.keyPress(el, data.key)
                }
            })

            jQuery.each(keys, function (index, key) {
                queue.addStep({
                    key     : key.length == 1 ? key : key.substring(1, key.length - 1)
                })
            });
            
            var async       = this.beginAsync();
            
            queue.run(function () {
                me.endAsync(async)
                
                callback && callback.call(scope || me)
            })
        },

        /**
        * @param {HTMLElement} el
        * @param {String} key
        * @param {Object} options any extra options used to configure the DOM event
        * 
        * This method will simluate the key press, translated to the specified DOM element.
        * The following events will be fired, in order: `keydown`, `keypress`, `textInput`(webkit only currently), `keyup`  
        */
        keyPress: function (el, key, options) {
            el = this.normalizeElement(el);
            var KeyCodes = Siesta.Test.Simulate.KeyCodes().keys,
                keyCode,
                charCode;

            options = options || {};
            
            options.readableKey = key;
            keyCode = KeyCodes[key.toUpperCase()] || 0;

            if(this.isReadableKey(keyCode)) {
                charCode = key.charCodeAt(0);
            } else {
                charCode = 0;
            } 

            var me = this,
                originalLength = -1,
                isReadableKey = this.isReadableKey(keyCode),
                isTextInput = el.nodeName.toLowerCase() === 'textarea' || 
                              (el.nodeName.toLowerCase() === 'input' && (el.type.toLowerCase() === 'password' || el.type.toLowerCase() === 'text'));
            
            if (isTextInput) {
                originalLength = el.value.length;
            }
            
            me.simulateEvent(el, 'keydown', $.extend({ charCode : 0, keyCode : keyCode }, options), true);
            me.simulateEvent(el, 'keypress', $.extend({ charCode : charCode, keyCode : this.isReadableKey(keyCode) ? 0 : keyCode }, options), false);
            
            if (isTextInput) {
                if (isReadableKey) {
                    // TODO check first if textInput event is supported
                    me.simulateEvent(el, 'textInput', { text: options.readableKey }, true);
                
                    // If the entered char had no impact on the textfield - manually put it there
                    if (!Siesta.supports.canSimulateKeyCharacters || (originalLength === el.value.length)) {
                        el.value = el.value + options.readableKey;
                    }
                }
                
                // Manually delete one char off the end if backspace simulation is not supported by the browser
                if (keyCode === KeyCodes.BACKSPACE && !Siesta.supports.canSimulateBackspace && el.value.length > 0) {
                    el.value = el.value.substring(0, el.value.length - 1);
                }
            }
            
            if (keyCode === KeyCodes.ENTER && !Siesta.supports.enterOnAnchorTriggersClick) {
                me.simulateEvent(el, 'click');
            }
            me.simulateEvent(el, 'keyup', $.extend({ charCode : 0, keyCode : keyCode }, options), true);
        },

        // private
        isReadableKey: function (keyCode) {
            var KC = Siesta.Test.Simulate.KeyCodes();

            return !KC.isNav(keyCode) && !KC.isSpecial(keyCode);
        }
    }
});


;
/**
@class Siesta.Test.Simulate.Event
@mixin Siesta.Test.Simulate.Mouse
@mixin Siesta.Test.Simulate.Keyboard

This is a mixin providing events simulation functionality.

*/

Role('Siesta.Test.Simulate.Event', {
    
    does : [
        Siesta.Test.Simulate.Mouse,
        Siesta.Test.Simulate.Keyboard
    ],

    has: {
        actionDelay             : 100,
        afterActionDelay        : 100,
        suppressEventsLog       : true,
        
        /**
         * @cfg {String} simulateEventsWith
         * 
         * This option is IE9-strict mode (and probably above) specific. It specifies, which events simulation function Siesta should use. 
         * The choice is between 'dispatchEvent' (W3C standard) and 'fireEvent' (MS interface) - both are available in IE9 strict mode
         * and both activates different event listeners. See this blog post for detailed explanations: 
         * <http://www.digitalenginesoftware.com/blog/archives/76-DOM-Event-Model-Compatibility-or-Why-fireEvent-Doesnt-Trigger-addEventListener.html>
         * 
         * Valid values are "dispatchEvent" and "fireEvent".
         * 
         * The framework specific adapters chooses the most appropriate value automatically (unless explicitly configured). 
         */
        simulateEventsWith      : {
            is          : 'rw',
            init        : 'dispatchEvent'
        }
    },

    methods: {
        
        /**
         * This method will simulate an event triggered by the passed element. If no coordinates are supplied in the options object, the center of the element
         * will be used. 
         * @param {HTMLElement} el
         * @param {String} type The type of event (e.g. 'mouseover', 'click', 'keypress')
         * @param {Object} the options for the event. See http://developer.mozilla.org/en/DOM/event for reference.
         * @param {Boolean} suppressLog true to not include this simulated event in the assertion grid.
         */
        simulateEvent: function (el, type, options, suppressLog) {
            var global = this.global;
            options = options || {};

            el = this.normalizeElement(el);
            var evt = this.createEvent(type, options, el);
            
            if (evt) {
                evt.synthetic = true;
                this.dispatchEvent(el, type, evt);

                // Let the outside world know that an event was simulated
                if (!suppressLog) {
                    this.fireEvent('eventsimulated', this, el, type, evt);

                    if (!this.suppressEventsLog) {
                        this.logEvent(el, type, evt, options);
                    }
                }
            }

            return evt;
        },

        createEvent: function (type, options, el) {
            if (/^text(Input)$/.test(type)) {
                return this.createTextEvent(type, options, el);
            }
            if (/^mouse(over|out|down|up|move|enter|leave)|contextmenu|(dbl)?click$/.test(type)) {
                return this.createMouseEvent(type, options, el);
            }
            if (/^key(up|down|press)$/.test(type)) {
                return this.createKeyboardEvent(type, options, el);
            }
            if (/^touch/.test(type)) {
                return this.createTouchEvent(type, options, el);
            }
            throw 'Event type: ' + type + ' not supported';
        },

        
        dispatchEvent: function (el, type, evt) {
            // use W3C standard when available and allowed by "simulateEventsWith" option            
            if (el.dispatchEvent && this.getSimulateEventsWith() == 'dispatchEvent') {
                el.dispatchEvent(evt);
            } else if (el.fireEvent) {
                // IE 6,7,8 can't dispatch many events cleanly - throws exceptions
                try {
                    // this is the serios nominant to the best-IE-bug-ever prize and its IE7 specific
                    // accessing the "scrollLeft" property on document or body triggers the synchronous(!) "resize" event on window
                    // ExtJS uses singleton for Ext.EventObj and its "target" property gets overwritten with "null"
                    // thus consequent event handlers fails
                    // doing an access to that property to cache it
                    var doc     = this.global.document.documentElement;
                    var body    = this.global.document.body;
                    
                    var xxx     = doc && doc.scrollLeft || body && body.scrollLeft || 0;
                    
                    el.fireEvent('on' + type, evt);
                } catch (e) {
                }
            } else
                throw "Can't dispatch event: " + type
            
            return evt;
        },

        // TODO, this method should not contain ugly HTML UI stuff.
        logEvent: function (el, type, evt, options) {
            var x = evt.clientX,
                y = evt.clientY,
                tag = el.nodeName,
                eventData = {
                    type: type,
                    sourceX: x,
                    sourceY: y,
                    isSimulatedEvent: true,
                    sourceNode: el,
                    readableSource : tag + (el.id ? ('[id="' + el.id + '"]') : '') + (el.className ? ('[cls="' + el.className + '"]') : '')
                };

            if (type.match('touch|mouse|click|contextmenu')) {
                eventData.description = "<span class=\"simulated-event-name\">" + type + "</span>" + ' fired by ' + eventData.readableSource + ' at [' + x + ', ' + y + ']';
                eventData.button = evt.button;
            } else {
                eventData.keyCode = options.keyCode;
                eventData.key = String.fromCharCode(options.keyCode);
                
                eventData.description = "<span class=\"simulated-event-name\">" + type + "</span> [" + options.readableKey + '] fired by ' + eventData.readableSource;
            }

            this.addResult(new Siesta.Result.Diagnostic(eventData));
        }
    }
});
;
/**
@class Siesta.Test.ExtJS.Store

This is a mixin, with helper methods for testing functionality relating to Ext.data.Store class. This mixin is being consumed by {@link Siesta.Test.ExtJS}

*/
Role('Siesta.Test.ExtJS.Store', {
    
    methods : {
        
        /**
         * Waits until all the passed stores have been loaded (fires the "load" event) and calls the provided callback.
         * 
         * This method accepts either variable number of arguments:
         *
         *      t.waitForStoresToLoad(store1, store2, function () { ... })
         * or array of stores:
         * 
         *      t.waitForStoresToLoad([ store1, store2 ], function () { ... })
         * 
         * @param {Ext.data.AbstractStore} store1 The store to load.
         * @param {Ext.data.AbstractStore} store2 The store to load.
         * @param {Ext.data.AbstractStore} storeN The store to load.
         * @param {Function} callback A function to call when the condition has been met.
         */        
        waitForStoresToLoad: function () {
            var Ext         = this.getExt();
            var args        = Ext.Array.flatten(Array.prototype.slice.call(arguments))
            
            var callback
            
            if (this.typeOf(args[ args.length - 1 ]) == 'Function') callback = args.pop()

            var me          = this;
            var loaded      = 0;
            var storesNum   = args.length;

            var async       = this.beginAsync();

            Joose.A.each(args, function (store) {
                if (!store.proxy) {
                    storesNum--;
                    return;
                }

                store.on('load', function () {

                    store.proxy.un('exception', exceptionFailure);

                    if (++loaded == storesNum) {
                        me.pass("All stores loaded correctly");

                        callback.apply(me, args);

                        me.endAsync(async);
                    }
                }, null, { single : true });

                var exceptionFailure = function () {
                    me.endAsync(async);
                    
                    me.fail("Failed to load the store", "Store [READ] URL: " + store.proxy.url);
                };

                store.proxy.on('exception', exceptionFailure);
            });
        },

        /**
         * This method is a wrapper around {@link #waitForStoresToLoad} method - it waits for the provided stores to fire the "load" event.
         * In addition to {@link #waitForStoresToLoad} this method also calls the `load` method of each passed store.
         * 
         * This method accepts either variable number of arguments:
         *
         *      t.loadStoresAndThen(store1, store2, function () { ... })
         * or array of stores:
         * 
         *      t.loadStoresAndThen([ store1, store2 ], function () { ... })
         * 
         * @param {Ext.data.AbstractStore} store1 The store to load.
         * @param {Ext.data.AbstractStore} store2 The store to load.
         * @param {Ext.data.AbstractStore} storeN The store to load.
         * @param {Function} callback A function to call when the condition has been met.
         */  
        loadStoresAndThen: function () {
            var Ext = this.getExt();
            this.waitForStoresToLoad.apply(this, arguments);
            
            var args                = Ext.Array.flatten(Array.prototype.slice.call(arguments))
            
            if (this.typeOf(args[ args.length - 1 ]) == 'Function') args.pop()

            Joose.A.each(args, function (store) {
                if (store.proxy && store.load) {
                    store.load();
                }
            });
        }
    }
});
;
/**
@class Siesta.Test.ExtJS.Observable

This is a mixin, with helper methods for testing functionality relating to Ext.util.Observable class. This mixin is being consumed by {@link Siesta.Test.ExtJS}

*/
Role('Siesta.Test.ExtJS.Observable', {
    
    methods : {
        
        /**
         * This assertion can be expressed as the following statement: When calling the passed 
         * function `func`, the passed `observable` will fire the `event` `n` times, during the
         * following `timeOut` milliseconds. 
         * 
         * @param {Ext.util.Observable} observable The observable instance  
         * @param {String} event The name of event
         * @param {Number} n The expected number of events
         * @param {Number} timeOut The number of milliseconds to wait for events to be fired
         * @param {Function} func The function which should fire the events to detect
         * @param {String} desc The description of the assertion.
         * @param {Function} callback Optional. A callback to call after the assertion was checked. 
         */
         firesOk: function (observable, event, n, timeOut, func, desc, callback) {
            var me      = this;
            var async   = this.beginAsync(timeOut + 100);
            
            var originalSetTimeout = this.originalSetTimeout;

            originalSetTimeout(function () {

                me.endAsync(async);

                observable.un(event, countFunc);

                if (counter == n)
                    me.pass('Exactly ' + n + " '" + event + "' events have been fired");
                else
                    me.fail(n + " '" + event + "' events were expected, but " + counter + ' were fired');

                callback && callback();

            }, timeOut);


            var counter = 0;

            var countFunc = function () { counter++; };

            observable.on(event, countFunc);

            func();
        },
        
        
        /**
         * This assertion passes if the observable fires the specified event exactly (n) times during the test execution.
         * 
         * @param {Ext.util.Observable} observable The observable instance  
         * @param {String} event The name of event
         * @param {Number} n The expected number of events to be fired
         * @param {String} desc The description of the assertion.
         */
        willFireNTimes: function (observable, event, n, desc, isGreaterEqual) {
            var me      = this;
            desc = desc ? (desc + ' ') : '';

            this.on('beforetestfinalizeearly', function () {
                observable.un(event, countFunc);

                if (counter === n || (isGreaterEqual && counter > n)) {
                    me.pass(desc + 'Exactly ' + n + " '" + event + "' events have been fired");
                } else {
                    me.fail(desc + n + " '" + event + "' events were expected, but " + counter + ' were fired');
                }
            });

            var counter = 0,
                countFunc = function () { counter++; };

            observable.on(event, countFunc);
        },

        /**
         * This assertion passes if the observable does not fire the specified event through the duration of the entire test.
         * 
         * @param {Ext.util.Observable} observable The observable instance  
         * @param {String} event The name of event
         * @param {String} desc The description of the assertion.
         */
        wontFire : function(observable, event, desc) {
            this.willFireNTimes(observable, event, 0, desc);
        },

        /**
         * This assertion passes if the observable does not fire the specified event through the duration of the entire test.
         * 
         * @param {Ext.util.Observable} observable The observable instance  
         * @param {String} event The name of event
         * @param {Number} n The minimum number of events to be fired
         * @param {String} desc The description of the assertion.
         */
        firesAtLeastNTimes : function(observable, event, n, desc) {
            this.willFireNTimes(observable, event, n, desc, true);
        },
        
        
        /**
         * This method will wait till the first `event`, fired on the provided `observable` and then will call the provided callback.
         * 
         * @param {Ext.util.Observable} observable The observable to wait on
         * @param {} event The name of the event to wait for
         * @param {} callback The callback to call 
         * @param {} scope The scope for the callback
         * @param {} timeout The maximum amount of time to wait for the condition to be fulfilled. Defaults to the {@link Siesta.Test.ExtJS#waitForTimeout} value.
         */
        waitForEvent : function (observable, event, callback, scope, timeout) {
            var eventFired      = false
            
            observable.on(event, function () { eventFired = true }, null, { single : true })
            
            this.waitFor(
                function () { return eventFired },
                callback,
                scope,
                timeout
            );
        },
        
        
        /**
         * This method checks if the provided `observable` has a listener for the `eventName`
         * 
         * @param {Ext.util.Observable} observable
         * @param {} eventName
         */
        hasListener : function (observable, eventName, description) {
            if (!observable || !observable.hasListener) {
                this.fail(description, {
                    assertionName       : 'hasListener',
                    annotation          : '1st argument for `t.hasListener` should be an observable instance'
                })
                
                return
            }
            
            if (observable.hasListener(eventName))
                this.pass(description)
            else
                this.fail(description, {
                    assertionName       : 'hasListener',
                    annotation          : 'Provided observable has no listeners for event: ' + eventName
                })
        }
    }
});
;
/**
@class Siesta.Test.ExtJS.Component

This is a mixin, with helper methods for testing functionality relating to Ext.Component. This mixin is being consumed by {@link Siesta.Test.ExtJS}. 

*/
Role('Siesta.Test.ExtJS.Component', {
    
    requires        : [ 'waitFor' ],
    
    methods : {
        
        /**
         * Waits until the main element of the passed component is the 'top' element in the DOM. The callback will receive the passed component instance.
         * 
         * @param {Ext.Component} component The component to look for.
         * @param {Function} callback The callback to call after the component becomes visible
         * @param {Object} scope The scope for the callback
         * @param {Int} timeout The maximum amount of time to wait for the condition to be fulfilled. Defaults to the {@link Siesta.Test.ExtJS#waitForTimeout} value. 
         */        
        waitForComponentVisible : function(component, callback, scope, timeout) {
            var Ext = this.getExt();
            if (!(component instanceof Ext.Component)) {
                throw 'Expected an Ext.Component, got: ' + component;
            }
            
            var me = this;

            this.waitFor(
                function() { return component.el && me.elementIsTop(component.el, true) && component; }, 
                callback,
                scope, 
                timeout
            );
        },
        
        
        /**
         * Waits until the main element of the passed component is not visible. The callback will receive the passed component instance.
         * 
         * @param {Ext.Component} component The component to look for.
         * @param {Function} callback The callback to call after the component becomes not visible
         * @param {Object} scope The scope for the callback
         * @param {Int} timeout The maximum amount of time to wait for the condition to be fulfilled. Defaults to the {@link Siesta.Test.ExtJS#waitForTimeout} value. 
         */        
        waitForComponentNotVisible : function(component, callback, scope, timeout) {
            var Ext = this.getExt();
            if (!(component instanceof Ext.Component)) {
                throw 'Expected an Ext.Component, got: ' + component;
            }
            
            var me = this;

            this.waitFor(
                function() { return component.el && !me.isElementVisible(component.el) && component; }, 
                callback,
                scope, 
                timeout
            );
        },
        

        /**
         * Waits until Ext.ComponentQuery detects the passed query parameter. The callback will receive the result of the query.
         * 
         * The "root" argument of this method can be omitted.
         * 
         * @param {String} query The component query phrase
         * @param {Ext.Container} root The container to start a component query from. Optional
         * @param {Function} callback The callback to call after the xtype has been found
         * @param {Object} scope The scope for the callback
         * @param {Int} timeout The maximum amount of time to wait for the condition to be fulfilled. Defaults to the {@link Siesta.Test.ExtJS#waitForTimeout} value. 
         */        
        waitForComponentQuery : function(query, root, callback, scope, timeout) {
            // no `root` supplied
            if (this.typeOf(root) == 'Function') {
                timeout     = scope
                scope       = callback
                callback    = root
                root        = this.getExt().ComponentQuery
            }
            
            this.waitFor(
                function() { 
                    var result = root.query(query);
                    return result.length > 0 ? result : false; 
                }, 
                callback,
                scope, 
                timeout
            );
        },

        /**
         * Shorthand alias for {@link #waitForComponentQuery}
         * 
         * @param {String} query The component query phrase
         * @param {Ext.Container} root The container to start a component query from
         * @param {Function} callback The callback to call after the xtype has been found
         * @param {Object} scope The scope for the callback
         * @param {Int} timeout The maximum amount of time to wait for the condition to be fulfilled. Defaults to the {@link Siesta.Test.ExtJS#waitForTimeout} value. 
         */   
        waitForCQ : function () {
            this.waitForComponentQuery.apply(this, arguments);
        },
        
        
        /**
         * Alias for {@link #waitForComponentQueryNotFound}
         * 
         * @param {String} query
         * @param {Function} callback
         * @param {Object} scope
         * @param {Number} timeout
         */
        waitForCQNotFound: function () {
            this.waitForComponentQueryNotFound.apply(this, arguments);
        },

        
        /**
         * Waits until Ext.ComponentQuery from the passed query parameter is no longer found, and then calls the callback supplied.
         *
         * The "root" argument of this method can be omitted.
         *
         * @param {String} query The component query selector
         * @param {Ext.Container} root The container to start a component query from. Optional
         * @param {Function} callback The callback to call after the xtype has been found
         * @param {Object} scope The scope for the callback
         * @param {Int} timeout The maximum amount of time to wait for the condition to be fulfilled. Defaults to the {@link Siesta.Test.ExtJS#waitForTimeout} value.
         */     
        waitForComponentQueryNotFound: function(query, root, callback, scope, timeout){
            // no `root` supplied
            if (this.typeOf(root) == 'Function') {
                timeout     = scope
                scope       = callback
                callback    = root
                root        = this.getExt().ComponentQuery
            }
           
            this.waitFor(
                function() {
                    var result = root.query(query);
                    return result.length === 0 && result;
                },
                callback,
                scope,
                timeout
            );
        },
        

        /**
         * Alias for {@link #waitForComponentQueryVisible}
         * 
         * @param {String} query
         * @param {Function} callback
         * @param {Object} scope
         * @param {Number} timeout
         */
        waitForCQVisible: function () {
            this.waitForComponentQueryVisible.apply(this, arguments);
        },
        
        
        /**
         * Waits until all results of the Ext.ComponentQuery are detected and visible.
         * 
         * The "root" argument of this method can be omitted.
         *
         * @param {String} query The component query selector
         * @param {Ext.Container} root The container to start a component query from. Optional
         * @param {Function} callback The callback to call after the xtype has been found
         * @param {Object} scope The scope for the callback
         * @param {Int} timeout The maximum amount of time to wait for the condition to be fulfilled. Defaults to the {@link Siesta.Test.ExtJS#waitForTimeout} value.
         */
        waitForComponentQueryVisible: function(query, root, callback, scope, timeout){
            var me      = this,
                Ext     = me.getExt();
                
            // no `root` supplied
            if (this.typeOf(root) == 'Function') {
                timeout     = scope
                scope       = callback
                callback    = root
                root        = Ext.ComponentQuery
            }

            me.waitFor(
                function() {
                    var result      = root.query(query),
                        allVisible  = true;
                   
                    if (result.length > 0){
                        Ext.Array.each(result,function(c){
                            if (!me.isElementVisible(c)){
                                allVisible = false;
                                return false;
                            }
                        });
                        return allVisible && result;
                    } else {
                        return false;
                    }
                },
                callback,
                scope,
                timeout
            );
        },
        
        
        /**
         * Waits until the a component with the specified xtype can be detected by a simple ComponentQuery.
         * 
         * The "root" argument of this method can be omitted.
         * 
         * @param {String} xtype The component xtype to look for.
         * @param {Ext.Container} root The container to start a component query from. Optional
         * @param {Function} callback The callback to call after the xtype has been found
         * @param {Object} scope The scope for the callback
         * @param {Int} timeout The maximum amount of time to wait for the condition to be fulfilled. Defaults to the {@link Siesta.Test.ExtJS#waitForTimeout} value. 
         */        
        waitForXType : function(xtype, root, callback, scope, timeout) {
            this.waitForComponentQuery(xtype, root, callback, scope, timeout);
        },

        /**
         * Waits until the a component with the specified xtype can be detected by a simple ComponentQuery.
         * 
         * @param {String} component The class name to wait for.
         * @param {Boolean} rendered true to also wait for the component to be rendered
         * @param {Function} callback The callback to call after the component has been found
         * @param {Object} scope The scope for the callback
         * @param {Int} timeout The maximum amount of time to wait for the condition to be fulfilled. Defaults to the {@link Siesta.Test.ExtJS#waitForTimeout} value. 
         */        
        waitForComponent : function(component, rendered, callback, scope, timeout) {
            var Ext = this.getExt();
            if (Ext.isString(component)) {
                xtype = Ext.ClassManager.get(component).xtype;
            } else {
                xtype = component.xtype;
            }
            
            if (rendered) {
                xtype = xtype + '[rendered]';
            }

            this.waitForXType(xtype, callback, scope, timeout);
        },

        /**
         * This assertion passes when the passed width and height matches the result of component.getSize()
         * 
         * @param {Ext.Component} component The component to query.
         * @param {Int} width
         * @param {Int} height
         * @param {String} description The description of the assertion
         */
        hasSize : function(component, width, height, description) {
            this.isDeeply(component.getSize(), { width : width, height : height }, description);
        },

         /**
         * This assertion passes when the passed x and y matches the result of component.getPosition()
         * 
         * @param {Ext.Component} component The component to query.
         * @param {Int} x
         * @param {Int} y
         * @param {String} description The description of the assertion
         */
        hasPosition : function(component, x, y, description) {
            this.isDeeply(component.getPosition(), [x, y], description);
        }
    }
});
;
/**
@class Siesta.Test.ExtJS.Grid

This is a mixin, with helper methods for testing functionality relating to ExtJS grids. This mixin is being consumed by {@link Siesta.Test.ExtJS}

*/
Role('Siesta.Test.ExtJS.Grid', {
    
    requires        : [ 'waitFor', 'pass', 'fail', 'typeOf' ],
    
    
    methods : {
        /**
         * Waits for the rows of a gridpanel or tree panel to render and then calls the supplied callback. Please note, that if the store of the grid has no records,
         * the condition for this waiter will never be fullfilled.
         * 
         * @param {Ext.panel.Table} panel The table panel
         * @param {Function} callback A function to call when the condition has been met.
         * @param {Object} scope The scope for the callback
         * @param {Int} timeout The maximum amount of time to wait for the condition to be fulfilled. Defaults to the {@link Siesta.Test.ExtJS#waitForTimeout} value. 
         */
        waitForRowsVisible : function(panel, callback, scope, timeout) {
            this.waitFor(
                function() { return !!panel.getView().getNode(0) }, 
                callback,
                scope, 
                timeout
            );
        },

        /**
         * Utility method which returns the first grid row element.
         * 
         * @param {Ext.panel.Table} panel The panel
         * @return {Ext.Element} The element of first row of grid.
         */
        getFirstRow : function(grid) {
            var Ext = this.getExt();
            return Ext.get(grid.getView().getNode(0));
        },

        /**
         * Utility method which returns the first grid cell element.
         * 
         * @param {Ext.panel.Table} panel The panel
         * 
         * @return {HTMLElement} The element of first cell of grid.
         */
        getFirstCell : function(panel) {
            return this.getCell(panel, 0, 0);
        },

        /**
         * Utility method which returns a grid row element.
         * 
         * @param {Ext.panel.Table} panel The panel
         * @param {Int} index The row index
         */
        getRow : function(grid, index) {
            var Ext = this.global.Ext;
            return Ext.get(grid.getView().getNode(index));
        },

        /**
         * Utility method which returns the cell at the supplied row and col position.
         * 
         * @param {Ext.panel.Table} panel The panel
         * @param {Int} row The row index
         * @param {Int} column The column index
         * 
         * @return {HTMLElement} The element of the grid cell at specified position.
         */
        getCell : function(grid, row, col) {
            return grid.getView().getCellByPosition({ row : row, column : col });
        },

        /**
         * Utility method which returns the last cell for the supplied row.
         * 
         * @param {Ext.panel.Table} panel The panel
         * @param {Int} row The row index
         * 
         * @return {HTMLElement} The element of the grid cell at specified position.
         */
        getLastCellInRow : function(grid, row) {
            return grid.getView().getCellByPosition({ row : row, column : grid.headerCt.getColumnCount() - 1});
        },

        /**
         * This assertion passes if the passed string is found in the passed grid's cell element.
         * 
         * @param {Ext.panel.Table} panel The panel to query
         * @param {Int} row The row index
         * @param {Int} column The column index
         * @param {String/RegExp} string The string to find or RegExp to match
         * @param {Description} description The description for the assertion
         */
        matchGridCellContent : function(grid, rowIndex, colIndex, string, description) {
            var view = grid.getView(),
                cell = view.getCellByPosition({ row : rowIndex, column : colIndex }).child('div');

            var isRegExp    = this.typeOf(string) == 'RegExp';
            var content     = cell.dom.innerHTML;
                
            if (isRegExp && string.test(content) || content.match(string)) {
                this.pass(description);
            } else {
                this.fail(description, {
                    assertionName   : 'matchGridCellContent',
                    
                    got         : cell.dom.innerHTML,
                    gotDesc     : 'Cell content',
                    
                    need        : string,
                    needDesc    : 'String matching',
                    
                    annotation  : 'Row index: ' + rowIndex + ', column index: ' + colIndex
                });
            }
        }
    }
});
;
/**
@class Siesta.Test.ExtJS.DataView

This is a mixin, with helper methods for testing functionality relating to ExtJS dataviews. This mixin is being consumed by {@link Siesta.Test.ExtJS}

*/
Role('Siesta.Test.ExtJS.DataView', {
    
    requires        : [ 'waitFor', 'getExt' ],
    
    
    methods : {
        /**
         * Waits for the items of a dataview to render and then calls the supplied callback.
         * @param {Ext.view.View} view The view
         * @param {Function} callback A function to call when the condition has been met.
         * @param {Object} scope The scope for the callback
         * @param {Int} timeout The maximum amount of time to wait for the condition to be fulfilled. Defaults to the {@link Siesta.Test.ExtJS#waitForTimeout} value. 
         */
        waitForViewRendered : function(view, callback, scope, timeout) {
            var hasItems    = view.store.getCount() > 0

            this.waitFor(
                function() { return hasItems ? !!view.getNode(0) : view.rendered }, 
                callback,
                scope, 
                timeout
            );
        },

        /**
         * Utility method which returns the first view element.
         * 
         * @param {Ext.panel.Table} panel The panel
         * @return {Ext.Element} The first element of the view
         */
        getFirstItem : function(view) {
            var Ext = this.getExt();
            return Ext.get(view.getNode(0));
        }
    }
});
;
/**
@class Siesta.Test.ExtJS.Element

This is a mixin, with helper methods for testing functionality relating to DOM elements. This mixin is consumed by {@link Siesta.Test.ExtJS}

*/
Role('Siesta.Test.ExtJS.Element', {
    
    methods : {
        /**
         * Passes if the passed element has the expected region.
         * 
         * @param {Ext.Element} el The element
         * @param {Ext.util.Region} region The region to compare to.
         * @param {String} description The description of the assertion
         */
        hasRegion : function(el, region, description) {
            var elRegion = el.getRegion();

            this.is(elRegion["top"], region["top"], description + ' (top)');
            this.is(elRegion["right"], region["right"], description + ' (right)');
            this.is(elRegion["bottom"], region["bottom"], description + ' (bottom)');
            this.is(elRegion["left"], region["left"], description + ' (left)');
        }
    }
});
;
/**
@class Siesta.Test.ExtJS.FormField

This is a mixin, with helper methods for testing functionality relating to Ext.form.Field class. This mixin is being consumed by {@link Siesta.Test.ExtJS}

*/
Role('Siesta.Test.ExtJS.FormField', {
    
    methods : {
        /**
         * Passes if the passed Field has the expected value.
         * 
         * @param {Ext.Element} field The field
         * @param {Ext.util.Region} region The region to compare to.
         * @param {String} description The description of the assertion
         */
        hasValue : function(field, value, description) {
            this.is(field.getValue(), value, description);
        }
    }
});
;
/**

@class Siesta.Test.TextSelection

This is a mixin providing text selection functionality.

*/
Role('Siesta.Test.TextSelection', {
    
    methods : {
        /**
         * Utility method which returns the selected text in the passed element or in the document
         * @param {Ext.Element/HTMLElement} The element
         * @return {String} The selected text
         */
        getSelectedText : function (el){
            el = this.normalizeElement(el);
            
            if ('selectionStart' in el) {
                return el.value.substring(el.selectionStart, el.selectionEnd);
            }

            var win = this.global,
                doc = win.document;
            
            if(win.getSelection){ 
		        return win.getSelection().toString(); 
            } 
            else if(doc.getSelection){ 
                return doc.getSelection(); 
            } 
            else if(doc.selection){ 
                return doc.selection.createRange().text; 
            } 
        },

        /**
         * Utility method which selects text in the passed element
         * @param {Ext.Element/HTMLElement} The element
         * @param {Int} start (optional) The selection start index
         * @param {Int} end (optional) The selection end index
         */
        selectText : function(el, start, end){
            el = this.normalizeElement(el);

            var v = el.value,
                doFocus = true;

            if (v.length > 0) {
                start = start === undefined ? 0 : start;
                end = end === undefined ? v.length : end;
                if (el.setSelectionRange) {
                    el.setSelectionRange(start, end);
                }
                else if(el.createTextRange) {
                    var R = el.createTextRange();
                    R.moveStart('character', start);
                    R.moveEnd('character', end - v.length);
                    R.select();
                }
                doFocus = $.browser.mozilla || $.browser.opera;
            }
            if (doFocus) {
                el.focus();
            }
        }
    }
})
;
/**
@class Siesta.Test.Element

This is a mixin, with helper methods for testing functionality relating to DOM elements. This mixin is consumed by {@link Siesta.Test}

*/
Role('Siesta.Test.Element', {
    
    requires    : [
        'chain'
    ],
    
    methods : {
        // Normalizes the element to an HTML element. Every 'framework layer' will need to provide its own implementation
        normalizeElement : function(domNode) {
            return domNode;
        },
        
        /**
         * Utility method which returns the center if a passed element.
         * @param {HTMLElement} el The element to find the center of.
         * @return {Object} The object with `x` and `y` properties
         */
        findCenter: function (target) {
            var el          = this.$( this.normalizeElement(target) ),
                offset      = el.offset(),
                doc         = this.$( this.global.document );
            return [
                offset.left + el.outerWidth()  / 2 - doc.scrollLeft(),
                offset.top  + el.outerHeight() / 2 - doc.scrollTop()
            ]
        },


        /**
         * Returns true if the element is visible.
         * @param {HTMLElement} el The element 
         * @return {Boolean}
         */
        isElementVisible : function(el) {
            el = this.normalizeElement(el);
            return this.$(el).is(':visible');
        },

        /**
         * Passes if the innerHTML of the passed element contains the text passed
         * 
         * @param {HTMLElement} el The element to query
         * @param {String} text The text to match 
         * @param {Description} description The description for the assertion
         */
        contentLike : function(el, text, description) {
            el = this.normalizeElement(el);

            this.like(el.innerHTML, text, description);
        },

        /**
         * Passes if the innerHTML of the passed element does not contain the text passed
         * 
         * @param {HTMLElement} el The element to query
         * @param {String} text The text to match 
         * @param {Description} description The description for the assertion
         */
        contentNotLike : function(el, text, description) {
            el = this.normalizeElement(el);

            this.unlike(el.innerHTML, text, description);
        },

        /**
         * Passes if the innerHTML of the passed element contains the text passed
         * 
         * @param {HTMLElement} el The element to query
         * @param {String} text The text to match 
         * @param {Function} callback The callback to call after the CSS selector has been found
         * @param {Object} scope The scope for the callback
         * @param {Int} timeout The maximum amount of time to wait for the condition to be fulfilled. Defaults to the {@link Siesta.Test.ExtJS#waitForTimeout} value. 
         */
        waitForContentLike : function(el, text, callback, scope, timeout) {
            el = this.normalizeElement(el);

            this.waitFor(
                function() { return el.innerHTML.match(text); }, 
                callback,
                scope, 
                timeout
            );
        },

        /**
         * Performs clicks, double clicks, right clicks and drags at random coordinates within the passed element.
         * 
         * @param {HTMLElement} el The element to upon which to unleash the "monkey".
         * @param {Int} nbrInteractions The number of random interactions to perform. 
         * @param {Description} description The description for the assertion
         * @param {Function} callback The callback to call after the CSS selector has been found
         * @param {Object} scope The scope for the callback
         */
        monkeyTest : function(el, nbrInteractions, description, callback, scope) {
            el = this.normalizeElement(el) || this.global.document.body;
            nbrInteractions = nbrInteractions || 30;

            var me          = this,
                offset      = me.$(el).offset(),
                right       = offset.left + me.$(el).width(),
                bottom      = offset.top + me.$(el).height();

            var queue       = new Siesta.Util.Queue({
                deferer         : me.originalSetTimeout,
                deferClearer    : me.originalClearTimeout,
                
                interval        : 50,
                
                observeTest     : this,
                
                processor   : function (data) {
                    if (me.nbrExceptions || me.failed)
                        // do not continue if the test has detected an exception thrown
                        queue.abort()
                    else
                        data.action(data)
                }
            });
            
            for (var i = 0; i < nbrInteractions; i++) {
                var xy = [me.randomBetween(offset.left, right), me.randomBetween(offset.top, bottom)];

                switch (i % 4) {
                    case 0:
                        queue.addAsyncStep({
                            action          : function (data) {
                                me.click(data.xy, data.next)
                            },
                            xy              : xy
                        });
                    break;

                    case 1:
                        queue.addAsyncStep({
                            action          : function (data) {
                                me.doubleClick(data.xy, data.next)
                            },
                            xy              : xy
                        });
                    break;

                    case 2:
                        queue.addAsyncStep({
                            action          : function (data) {
                                me.rightClick(data.xy, data.next)
                            },
                            xy              : xy
                        });
                    break;

                    default:
                        queue.addAsyncStep({
                            action          : function (data) {
                                me.drag(
                                    data.xy, 
                                    [ me.randomBetween(offset.left, right), me.randomBetween(offset.top, bottom) ],
                                    null,
                                    data.next
                                )
                            },
                            xy              : xy
                        });
                    break;
                }
            }
            
            var checkerActivated    = false
            
            var assertionChecker    = function () {
                checkerActivated    = true
                
                me.is(me.nbrExceptions, 0, description || '0 exceptions thrown during monkey test');
            }
            
            this.on('beforetestfinalizeearly', assertionChecker) 

            var async       = me.beginAsync();
            
            queue.run(function () {
                me.endAsync(async);
                
                if (!checkerActivated) {
                    me.un('beforetestfinalizeearly', assertionChecker)
                    
                    assertionChecker()
                }
                
                callback && callback.call(scope || me);
            });
        },

        /**
         * Passes if the element has the supplied CSS classname 
         * 
         * @param {HTMLElement} el The element to query
         * @param {String} cls The class name to check for
         * @param {Description} description The description for the assertion
         */
        hasCls : function (el, cls, description) {
            el = this.normalizeElement(el);
            
            if (this.$(el).hasClass(cls)) {
                this.pass(description);
            } else {
                this.fail(description, {
                    assertionName   : 'hasCls',
                    
                    got         : el.className,
                    gotDesc     : 'Classes of element',
                    need        : cls,
                    needDesc    : 'Need CSS class'
                })
            }
        },
        
        
        /**
         * Passes if the element does not have the supplied CSS classname 
         * 
         * @param {HTMLElement} el The element to query
         * @param {String} cls The class name to check for
         * @param {Description} description The description for the assertion
         */
        hasNotCls : function (el, cls, description) {
            el = this.normalizeElement(el);
            
            if (!this.$(el).hasClass(cls)) {
                this.pass(description);
            } else {
                this.fail(description, {
                    assertionName   : 'hasNotCls',
                    got         : el.className,
                    gotDesc     : 'Classes of element',
                    annotation  : 'Element has the class [' + cls + ']'
                })
            }
        },

        /**
         * Passes if the element does not have the supplied style value
         * 
         * @param {HTMLElement} el The element to query
         * @param {String} property The style property to check for
         * @param {String} value The style value to check for
         * @param {Description} description The description for the assertion
         */
        hasStyle : function (el, property, value, description) {
            el = this.normalizeElement(el);
            
            if (this.$(el).css(property) === value) {
                this.pass(description);
            } else {
                this.fail(description, {
                    assertionName   : 'hasStyle',
                    got         : this.$(el).css(property),
                    gotDesc     : 'Styles of element',
                    need        : value,
                    needDesc    : 'Need style'
                });
            }
        },
        
        
        /**
         * Passes if the element does not have the supplied style value
         * 
         * @param {HTMLElement} el The element to query
         * @param {String} property The style property to check for
         * @param {String} value The style value to check for
         * @param {Description} description The description for the assertion
         */
        hasNotStyle : function (el, property, value, description) {
            el = this.normalizeElement(el);
            
            if (this.$(el).css(property) !== value) {
                this.pass(description);
            } else {
                this.fail(description, {
                    assertionName   : 'hasNotStyle',
                    got         : el.style.toString(),
                    gotDesc     : 'Style of element',
                    annotation  : 'Element has the style [' + property + ']'
                });
            }
        },
        
        /**
         * Waits for a certain CSS selector to be found at the passed XY coordinate, and calls the callback when found. 
         * The callback will receive the element from the passed XY coordinates.
         * 
         * @param {Array} xy The x and y coordinates to query
         * @param {String} selector The CSS selector to check for
         * @param {Function} callback The callback to call after the CSS selector has been found
         * @param {Object} scope The scope for the callback
         * @param {Int} timeout The maximum amount of time to wait for the condition to be fulfilled. Defaults to the {@link Siesta.Test#waitForTimeout} value. 
         */
        waitForSelectorAt : function(xy, selector, callback, scope, timeout) {
            var doc = this.global.document;

            this.waitFor(
                function() { var el = doc.elementFromPoint(xy[0], xy[1]); return el && this.$(el).is(selector); }, 
                function() { callback.call(scope || this, doc.elementFromPoint(xy[0], xy[1])); },
                scope, 
                timeout
            );
        },

        /**
         * Waits for a certain CSS selector to be found in the DOM, and then calls the callback supplied.
         * The callback will receive the results of jQuery selector.
         * 
         * @param {String} selector The CSS selector to check for
         * @param {HTMLElement} root (optional) The root element in which to detect the selector.
         * @param {Function} callback The callback to call after the CSS selector has been found
         * @param {Object} scope The scope for the callback
         * @param {Int} timeout The maximum amount of time to wait for the condition to be fulfilled. Defaults to the {@link Siesta.Test#waitForTimeout} value. 
         */
        waitForSelector : function(selector, root, callback, scope, timeout) {

            if (jQuery.isFunction(root)) {
                timeout = scope;
                scope = callback;
                callback = root;
                root = null;
            } 

            if (root) root  = this.normalizeElement(root);

            this.waitFor(
                function() { return this.$(selector, root).length > 0; }, 
                function() { callback.call(scope || this, this.$(selector, root)); },
                scope, 
                timeout
            );
        },
        
        
        /**
         * Waits till all the CSS selectors from the provided array to be found in the DOM, and then calls the callback supplied.
         * 
         * @param {String[]} selectors The array of CSS selectors to check for
         * @param {HTMLElement} root (optional) The root element in which to detect the selector.
         * @param {Function} callback The callback to call after the CSS selector has been found
         * @param {Object} scope The scope for the callback
         * @param {Int} timeout The maximum amount of time to wait for the condition to be fulfilled. Defaults to the {@link Siesta.Test#waitForTimeout} value. 
         */
        waitForSelectors : function(selectors, root, callback, scope, timeout) {

            if (jQuery.isFunction(root)) {
                timeout     = scope;
                scope       = callback;
                callback    = root;
                root        = null;
            } 

            if (root) root  = this.normalizeElement(root);
            
            var me          = this

            this.waitFor(
                function () {
                    var allPresent  = true
                    
                    Joose.A.each(selectors, function (selector) {
                        if (me.$(selector, root).length == 0) {
                            allPresent = false
                            // stop iteration
                            return false
                        }
                    })
                    
                    return allPresent
                }, 
                callback,
                scope, 
                timeout
            );
        },
        
        

        /**
         * Waits for a certain CSS selector to not be found in the DOM, and then calls the callback supplied.
         * 
         * @param {String} selector The CSS selector to check for
         * @param {HTMLElement} root (optional) The root element in which to detect the selector.
         * @param {Function} callback The callback to call after the CSS selector has been found
         * @param {Object} scope The scope for the callback
         * @param {Int} timeout The maximum amount of time to wait for the condition to be fulfilled. Defaults to the {@link Siesta.Test#waitForTimeout} value. 
         */
        waitForSelectorNotFound : function(selector, root, callback, scope, timeout) {

            if (jQuery.isFunction(root)) {
                timeout = scope;
                scope = callback;
                callback = root;
                root = null;
            } 

            if (root) root  = this.normalizeElement(root);

            this.waitFor(
                function() { return this.$(selector, root).length === 0; }, 
                callback,
                scope, 
                timeout
            );
        },
        
        
        /**
         * Waits until the passed element becomes "visible" in the DOM and calls the provided callback.
         * Please note, that "visible" means element will just have a DOM node, and still may be hidden by another visible element.
         * 
         * The callback will receive the passed element as the 1st argument.
         * 
         * See also {@link #waitForElementTop} method.
         * 
         * @param {HTMLElement} el The element to look for.
         * @param {Function} callback The callback to call after the CSS selector has been found
         * @param {Object} scope The scope for the callback
         * @param {Int} timeout The maximum amount of time to wait for the condition to be fulfilled. Defaults to the {@link Siesta.Test.ExtJS#waitForTimeout} value. 
         */
        waitForElementVisible : function(el, callback, scope, timeout) {
            var me = this;

            this.waitFor(
                function() { return me.isElementVisible(el) && el; }, 
                callback,
                scope, 
                timeout
            );
        },

        /**
         * Waits until the passed element is becomes not "visible" in the DOM and call the provided callback.
         * Please note, that "visible" means element will just have a DOM node, and still may be hidden by another visible element.
         * 
         * The callback will receive the passed element as the 1st argument.
         * 
         * See also {@link #waitForElementNotTop} method.
         * 
         * @param {HTMLElement} el The element to look for.
         * @param {Function} callback The callback to call after the CSS selector has been found
         * @param {Object} scope The scope for the callback
         * @param {Int} timeout The maximum amount of time to wait for the condition to be fulfilled. Defaults to the {@link Siesta.Test.ExtJS#waitForTimeout} value. 
         */
        waitForElementNotVisible : function(el, callback, scope, timeout) {
            var me = this;

            this.waitFor(
                function() {return !me.isElementVisible(el) && el; }, 
                callback,
                scope, 
                timeout
            );
        },
        
        
        /**
         * Waits until the passed element is the 'top' element in the DOM and call the provided callback.
         * 
         * The callback will receive the passed element as the 1st argument.
         * 
         * @param {HTMLElement} el The element to look for.
         * @param {Function} callback The callback to call 
         * @param {Object} scope The scope for the callback
         * @param {Int} timeout The maximum amount of time to wait for the condition to be fulfilled. Defaults to the {@link Siesta.Test.ExtJS#waitForTimeout} value. 
         */
        waitForElementTop : function(el, callback, scope, timeout) {
            var me = this;

            this.waitFor(
                function() { return me.elementIsTop(el, true) && el; }, 
                callback,
                scope, 
                timeout
            );
        },

        /**
         * Waits until the passed element is not the 'top' element in the DOM and calls the provided callback with the element found.
         * 
         * The callback will receive the actual top element.
         * 
         * @param {HTMLElement} el The element to look for.
         * @param {Function} callback The callback to call
         * @param {Object} scope The scope for the callback
         * @param {Int} timeout The maximum amount of time to wait for the condition to be fulfilled. Defaults to the {@link Siesta.Test.ExtJS#waitForTimeout} value. 
         */
        waitForElementNotTop : function(el, callback, scope, timeout) {
            el = this.normalizeElement(el);
            
            var me = this,
                doc = me.global.document;

            me.waitFor(
                function() {    
                    if (!me.elementIsTop(el, true)) {
                        var center = me.findCenter(el);
                        return doc.elementFromPoint(center[0], center[1]);
                    }        
                }, 
                callback,
                scope, 
                timeout
            );
        },

        /**
         * Passes if the element is visible.
         * @param {HTMLElement} el The element 
         * @param {Description} description The description for the assertion
         */
        elementIsVisible : function(el, description) {
            el = this.normalizeElement(el);
            this.ok(this.isElementVisible(el), description);
        },

        /**
         * Passes if the element is not visible.
         * @param {HTMLElement} el The element 
         * @param {Description} description The description for the assertion
         */
        elementIsNotVisible : function(el, description) {
            el = this.normalizeElement(el);
            this.notOk(this.isElementVisible(el), description);
        },

        /**
         * Utility method which checks if the passed method is the 'top' element at its position.
         * 
         * @param {HTMLElement} el The element to look for.
         * @param {Boolean} allowChildren true to also include child nodes. False to strictly check for the passed element.
         * @return {Boolean} true if the element is the top element.
         */
        elementIsTop : function(el, allowChildren, strict) {
            el = this.normalizeElement(el);
            
            var center  = this.findCenter(el),
                foundEl = this.global.document.elementFromPoint(center[0], center[1]);
            
            return foundEl && (foundEl === el || (allowChildren && this.$(foundEl).closest(el).length > 0));
        },
        
        /**
         * Passes if the element is found at the supplied xy coordinates.
         * 
         * @param {HTMLElement} el The element to query
         * @param {Array} xy The xy coordinate to query.
         * @param {Boolean} allowChildren true to also include child nodes. False to strictly check for the passed element.
         * @param {Description} description The description for the assertion
         */
        elementIsAt : function(el, xy, allowChildren, description) {
            el = this.normalizeElement(el);
            
            var foundEl = this.global.document.elementFromPoint(xy[0], xy[1]);
            
            if (!foundEl) {
                this.fail(description, {
                    assertionName       : 'elementIsAt',
                    got                 : { x: xy[0], y : xy[1] },
                    gotDesc             : 'Postion',
                    annotation          : 'No element found at the specified position'
                });
            } else if (allowChildren) {
                if (foundEl === el || $(foundEl).closest(el)) {
                    this.pass(description);
                } else {
                    this.fail(description, {
                        assertionName   : 'elementIsAt',
                        got             : foundEl,
                        gotDesc         : 'Top element',
                        need            : el,
                        needDesc        : 'Need exactly this or its child',
                        annotation      : 'Passed element is not the top-most one and not the child of one'
                    });
                }
            } else {
                if (foundEl.dom === el.dom) {
                    this.pass(description);
                } else {
                    this.fail(description, {
                        assertionName   : 'elementIsAt',
                        got             : foundEl,
                        gotDesc         : 'Top element',
                        need            : el,
                        needDesc        : 'Should be',
                        annotation      : 'Passed element is not the top-most one'
                    });
                }
            }
        },

        /**
         * Passes if the element is the top element (using its center xy coordinates).
         * 
         * @param {HTMLElement} el The element to look for.
         * @param {Boolean} allowChildren true to also include child nodes. False to strictly check for the passed element.
         * @param {Description} description The description for the assertion
         * @param {Boolean} strict true to check all four corners of the element. False to only check at element center.
         */
        elementIsTopElement : function(el, allowChildren, description, strict) {
            el = this.normalizeElement(el);

            if (strict) {
                var o = this.$(el).offset();
                var region  = { 
                    top : o.top,
                    right : o.left + this.$(el).outerWidth(),
                    bottom : o.top + this.$(el).outerHeight(),
                    left : o.left
                };

                this.elementIsAt(el, [region.left+1, region.top+1], allowChildren, description + ' (t-l)');
                this.elementIsAt(el, [region.left+1, region.bottom-1], allowChildren, description + ' (b-l)');
                this.elementIsAt(el, [region.right-1, region.top+1], allowChildren, description + ' (t-r)');
                this.elementIsAt(el, [region.right-1, region.bottom-1], allowChildren, description + ' (b-r)');
            } else {
                this.elementIsAt(el, this.findCenter(el), allowChildren, description);
            }
        },
        
        /**
         * Passes if the element is not the top element (using its center xy coordinates).
         * 
         * @param {HTMLElement} el The element to look for.
         * @param {Boolean} allowChildren true to also include child nodes. False to strictly check for the passed element.
         * @param {Description} description The description for the assertion
         */
        elementIsNotTopElement : function(el, allowChildren, description) {
            el = this.normalizeElement(el);
            var center = this.findCenter(el);
            
            var foundEl = this.global.document.elementFromPoint(center[0], center[1]);
            
            if (!foundEl) {
                this.pass(description);
                
                return
            }
            
            if (allowChildren) {
                this.ok(foundEl !== el && $(foundEl).closest(el).length === 0, description);
            } else {
                this.isnt(foundEl, el, description);
            }
        },

        /**
         * Passes if the element is found at the supplied xy coordinates.
         * 
         * @param {String} selector The selector to query for
         * @param {Array} xy The xy coordinate to query.
         * @param {Boolean} allowChildren true to also include child nodes. False to strictly check for the passed element.
         * @param {Description} description The description for the assertion
         */
        selectorIsAt : function(selector, xy, description) {
            var doc = this.global.document;

            var foundEl = this.$(doc.elementFromPoint(xy[0], xy[1]) || doc.body);
            
            if (!foundEl) {
                this.fail(description, {
                    assertionName       : 'selectorIsAt',
                    got                 : { x: xy[0], y : xy[1] },
                    gotDesc             : 'Postion',
                    annotation          : 'No element matching the passed selector found at the specified position'
                });
            }

            if (foundEl.has(selector).length > 0 || foundEl.closest(selector).length > 0) {
                this.pass(description);
            } else {
                this.fail(description, {
                    assertionName   : 'selectorIsAt',
                    annotation      : 'Passed selector does not match DOM content at xy position'
                });
            }
        },

        /**
         * Passes if the selector is found in the DOM
         * 
         * @param {String} selector The selector to query for
         * @param {Description} description The description for the assertion
         */
        selectorExists : function(selector, description) {
            if (this.$(selector).length <= 0) {
                this.fail(description, 'No element matching the passed selector found: ' + selector);
            } else {
                this.pass(description);
            } 
        },

        /**
         * Passes if the selector is not found in the DOM
         * 
         * @param {String} selector The selector to query for
         * @param {Description} description The description for the assertion
         */
        selectorNotExists : function(selector, description) {
            if (this.$(selector).length > 0) {
                this.fail(description, 'Elements found matching the passed selector: ' + selector);
            } else {
                this.pass(description);
            } 
        },

        /**
         * Waits until the `scrollLeft` property of the element has changed. 
         * 
         * The callback will receive the new `scrollLeft` value.
         * 
         * @param {HTMLElement} el The element
         * @param {Function} callback The callback to call
         * @param {Object} scope The scope for the callback
         * @param {Int} timeout The maximum amount of time to wait for the condition to be fulfilled. Defaults to the {@link Siesta.Test.ExtJS#waitForTimeout} value. 
         */
        waitForScrollLeftChange : function(el, callback, scope, timeout) {
            el = this.normalizeElement(el);
            var original = el.scrollLeft;

            this.waitFor(
                function() { if (el.scrollLeft !== original) return el.scrollLeft; }, 
                callback,
                scope,
                timeout
            );
        },

        /**
         * Waits until the scrollTop property of the element has changed
         * 
         * The callback will receive the new `scrollTop` value.
         * 
         * @param {HTMLElement} el The element
         * @param {Function} callback The callback to call
         * @param {Object} scope The scope for the callback
         * @param {Int} timeout The maximum amount of time to wait for the condition to be fulfilled. Defaults to the {@link Siesta.Test.ExtJS#waitForTimeout} value. 
         */
        waitForScrollTopChange : function(el, callback, scope, timeout) {
            el = this.normalizeElement(el);
            var original = el.scrollTop;

            this.waitFor(
                function() { if (el.scrollTop !== original) return el.scrollTop; }, 
                callback,
                scope, 
                timeout
            );
        },
        
        
        /**
         * This method accepts an array of the DOM elements and performs a mouse click on them, in order. After that, it calls the provided callback:
         * 
       
       t.chainClick([ el1, el2 ], function () {
            ...
       })
       
         * the elements can be also provided inline, w/o wrapping array:
       
       t.chainClick(el1, el2, function () {
            ...
       })
       
       
         * 
         * @param {Array[HTMLElement]} elements The array of elements to click
         * @param {Function} callback The function to call after clicking all elements
         */
        chainClick : function () {
            var args        = Array.prototype.slice.call(arguments)
            var callback
            
            if (this.typeOf(args[ args.length - 1 ]) == 'Function') callback = args.pop()
            
            // poor-man Array.flatten, with only 1 level of nesting support
            args            = Array.prototype.concat.apply([], args)
            
            var steps       = []
            
            Joose.A.each(args, function (arg) {
                steps.push({
                    action      : 'click',
                    target      : arg
                })
            })
            
            if (callback) steps.push(callback)
            
            this.chain.apply(this, steps)
        }
    }
});
;
/**
 * 
@class Siesta.Test.Browser
@extends Siesta.Test
@mixin Siesta.Test.Simulate.Event
@mixin Siesta.Test.TextSelection 


A base class for testing a generic browser functionality. It has various DOM-related assertions, and is not optimized for any framework.

*/
Class('Siesta.Test.Browser', {
    
    isa         : Siesta.Test,
        
    does        :  [ 
        Siesta.Test.Simulate.Event,
        Siesta.Test.Element,
        Siesta.Test.TextSelection
    ],

    methods : { 
        $ : function () {
            var local$ = $.rebindWindowContext(this.global);
            return local$.apply(this.global, arguments);
        },

        // private
        getPathBetweenPoints: function (from, to) {
            if (typeof from[0] !== 'number' || typeof from[1] !== 'number' || typeof to[0] !== 'number' || typeof to[1] !== 'number') {
                throw 'Incorrect arguments passed to getPathBetweenPoints';
            }

            var stops = [],
                x0 = Math.floor(from[0]),
                x1 = Math.floor(to[0]),
                y0 = Math.floor(from[1]),
                y1 = Math.floor(to[1]),
                dx = Math.abs(x1 - x0),
                dy = Math.abs(y1 - y0),
                sx, sy, err, e2;

            if (x0 < x1) {
                sx = 1;
            } else {
                sx = -1;
            }

            if (y0 < y1) {
                sy = 1;
            } else {
                sy = -1;
            }
            err = dx - dy;
            
            while (x0 !== x1 || y0 !== y1) {
                e2 = 2 * err;
                if (e2 > -dy) {
                    err = err - dy;
                    x0 = x0 + sx;
                }

                if (e2 < dx) {
                    err = err + dx;
                    y0 = y0 + sy;
                }
                stops.push([x0, y0]);
            }

            stops.push(to);
            return stops;
        },

        randomBetween : function (min, max) {
            return Math.round(min + (Math.random()*(max - min)));
        },

        
        // private
        isArray : function(a) {
            return a && (a instanceof Array || a instanceof this.global.Array);
        },
        
        
        /**
         * This method will return the top-most DOM element at the specified coordinates from the test page.
         * 
         * @param {Number} x The X coordinate
         * @param {Number} y The Y coordinate
         * @return {HTMLElement} The top-most element at the specified position on the test page
         */
        elementFromPoint : function (x, y) {
            return this.global.document.elementFromPoint(x, y)
        }
    }
});
;
/**
 * 
@class Siesta.Test.ExtJS
@extends Siesta.Test.Browser
@mixin Siesta.Test.ExtJS.Observable
@mixin Siesta.Test.ExtJS.FormField
@mixin Siesta.Test.ExtJS.Component
@mixin Siesta.Test.ExtJS.Element 
@mixin Siesta.Test.ExtJS.Store 
@mixin Siesta.Test.ExtJS.DataView
@mixin Siesta.Test.ExtJS.Grid


A base class for testing browser and ExtJS applications. It inherit from {@link Siesta.Test.Browser} 
and adds various ExtJS specific assertions.

In various places where the {@link Siesta.Test.Browser} accepts the DOM element as the argument (for example, `type/click/drag` etc), 
this class will allow you to accept the instance of `Ext.Component`. In such case the `getEl` method of the component will be used to
convert the component to DOM element. 

This file is a reference only, for a getting start guide and manual, please refer to <a href="#!/guide/siesta_getting_started">Getting Started Guide</a>.

*/
Class('Siesta.Test.ExtJS', {
    
    isa         : Siesta.Test.Browser,
        
    does        :  [ 
        Siesta.Test.ExtJS.Component, 
        Siesta.Test.ExtJS.Element, 
        Siesta.Test.ExtJS.FormField, 
        Siesta.Test.ExtJS.Observable, 
        Siesta.Test.ExtJS.Store, 
        Siesta.Test.ExtJS.Grid,
        Siesta.Test.ExtJS.DataView
    ],
    
    has : {
        waitForExtReady         : true,
        waitForAppReady         : false,
        loaderPath              : null,
        
        simulateEventsWith      : {
            is      : 'rw',
            init    : function () {
                var div = document.createElement('div')
                
                return div.attachEvent ? 'fireEvent' : 'dispatchEvent'
            }
        }
    },

    methods : {
        
        initialize : function() {
            // Since this test is preloading Ext JS, we should let Siesta know what to 'expect'
            this.expectGlobals('Ext', 'id');
            this.SUPER();
        },
        
        
        start : function (alreadyFailedWithException, startNote) {
            var me      = this;
            var sup     = this.SUPER;
            var Ext     = this.getExt();
            
            if (this.loaderPath && Ext) {
                Ext.Loader.setPath(this.loaderPath);
            }
            
            if (Ext && (this.waitForExtReady || this.waitForAppReady)) {
                var errorMessage    = this.waitForExtReady 
                        ? 
                    "Ext.onReady took longer than 10 seconds - some dependency can't be loaded? Check the `Net` tab in Firebug"
                        :
                    "Waiting for MVC application launch took longer than 10 seconds - no MVC application on test page? You may need to disable the `waitForAppReady` config option."
                
                var hasTimedOut     = false
                
                var timeout         = setTimeout(function () {
                    hasTimedOut     = true
                    
                    sup.call(me, alreadyFailedWithException, errorMessage)
                    
                }, 10000)
                
                var continuation    = function () {
                    clearTimeout(timeout)
                    
                    if (!hasTimedOut) sup.call(me, alreadyFailedWithException, startNote)
                }
                
                // this flag will explain to Ext, that DOM ready event has already happened
                // Ext fails to set this flag if it was loaded dynamically, already after DOM ready
                // the test will start only after DOM ready anyway, so we just set this flag  
                Ext.isReady         = true

                var canWaitForApp   = Boolean(Ext.ClassManager.get('Ext.app.Application'))
                
                if (this.waitForAppReady && canWaitForApp) {
                    Ext.util.Observable.observe(Ext.app.Application, {
                        launch      : continuation,
                        
                        single      : true,
                        delay       : 100
                    })
                    
                    return
                }
                
                if (this.waitForExtReady || (this.waitForAppReady && !canWaitForApp)) 
                    // we still wrap the start of the test with the Ext.onReady, but we are waiting not for DOM loading but for dependencies loading
                    Ext.onReady(continuation)
                    
            } else
                this.SUPERARG(arguments)
        },

        /**
         * This method returns the `Ext` object from the scope of the test. When creating your own assertions for Ext JS code, you need
         * to make sure you are using this method to get the `Ext` instance. Otherwise, you'll be using the same "top-level" `Ext`
         * instance, used by the harness for its UI. 
         * 
         * For example:
         * 
         *      elementHasProvidedCssClass : function (el, cls, desc) {
         *          var Ext     = this.getExt();
         *          
         *          if (Ext.fly(el).hasCls(cls)) {
         *              this.pass(desc);
         *          } else {
         *              this.fail(desc);
         *          }
         *      }
         *   
         * @return {Object} The `Ext` object from the scope of test
         */
        getExt : function () {
            return this.global.Ext
        },
        
        
        /**
         * The alias for {@link #getExt}
         * @method
         */
        Ext : function () {
            return this.global.Ext
        },
        

        // Accept Ext.Element and Ext.Component
        normalizeElement : function(el) {
            if (!el) return null
            
            var Ext = this.getExt();
            return el instanceof Ext.Component ? el.getEl().dom : (el.dom ? el.dom : el);
        },

        
        /**
        * This method wraps the {@link Siesta.Test.Browser#type} method with additional check:
        * if the 1st passed parameter is an instance of `Ext.form.Field`, then the typing will occur
        * into its `inputEl` property. Please see {@link Siesta.Test.Browser#type} description
        * for the list of supported advanced key codes.
        * 
        * @param {Ext.form.Field/HTMLElement} el The element or Component to type into
        * @param {String} text the text to type.
        * @param {Function} callback (optional) To run this method async, provide a callback method to be called after the type operation is completed.
        * @param {Object} scope (optional) the scope for the callback
        */
        type: function (el, text, callback, scope) {
            var Ext = this.getExt();
            if (el instanceof Ext.form.Field) {
                el = el.inputEl;
            }
            this.SUPER(el, text, callback, scope);
        },

        /**
         * This method wraps the {@link Siesta.Test.Browser#click} method with additional check:
         * if the 1st passed parameter is an instance of `Ext.form.Field`, then click will occur
         * in the center of its `inputEl`. If its an instance of `Ext.Component` - in the center
         * of the component's element.
         * 
         * The following events will be fired, in order:  `mouseover`, `mousedown`, `mouseup`, `click`
         * 
         * See also {@link Siesta.Test.Browser#click}.
         *   
         * @param {Ext.Component/HTMLElement} el The element or Component to click
         * @param {Function} callback (optional) A function to call when the condition has been met.
         * @param {Object} scope (optional) The scope for the callback 
         */
        click: function (el, callback, scope) {
            var Ext = this.getExt();
            if (el instanceof Ext.form.Field && el.inputEl) {
                el = el.inputEl;
            } else if (el instanceof Ext.Component) {
                var center = this.findCenter(el);
                el = this.elementFromPoint(center[0], center[1]);
            }
            this.SUPER(el, callback, scope);
        },

        /**
         * This method will simulate a drag and drop operation between either two points, two DOM elements or two `Ext.Component`s.
         * The following events will be fired, in order:  `mouseover`, `mousedown`, `mousemove` (along the mouse path), `mouseup`
         * 
         * This method is deprecated in favor of {@link #dragTo} and {@link #dragBy} methods
         *   
         * @param {Ext.Component/HTMLElement/Array} source Either an element, or [x,y] as the drag starting point
         * @param {Ext.Component/HTMLElement/Array} arget (optional) Either an element, or [x,y] as the drag end point
         * @param {Array} delta (optional) the amount to drag from the source coordinate, expressed as [x,y]. [50, 10] will drag 50px to the right and 10px down.
         * @param {Function} callback (optional) To run this method async, provide a callback method to be called after the drag operation is completed.
         * @param {Object} scope (optional) the scope for the callback
         * @param {Object} options any extra options used to configure the DOM event
        */
        drag: function (source, target, delta, callback, scope, options) {
            var Ext = this.getExt();
            if (source instanceof Ext.Component) {
                var center = this.findCenter(source.el);
                source = this.elementFromPoint(center[0], center[1]);
            }

            if (target instanceof Ext.Component) {
                var center = this.findCenter(target.el);
                target = this.elementFromPoint(center[0], center[1]);
            }
            this.SUPER(source, target, delta, callback, scope, options);
        },

        /**
        * This method will simulate a drag and drop operation between either two points or two DOM elements.
        * The following events will be fired, in order:  `mouseover`, `mousedown`, `mousemove` (along the mouse path), `mouseup`
        *   
        * @param {Ext.Component/HTMLElement/Array} source Either an element, or [x,y] as the drag starting point
        * @param {Ext.Component/HTMLElement/Array} target Either an element, or [x,y] as the drag end point
        * @param {Function} callback (optional) To run this method async, provide a callback method to be called after the drag operation is completed.
        * @param {Object} scope (optional) the scope for the callback
        * @param {Object} options any extra options used to configure the DOM event
        * @param {Boolean} dragOnly true to skip the mouseup and not finish the drop operation.
        */
        dragTo : function(source, target, callback, scope, options, dragOnly) {
            var Ext = this.getExt();
            if (source instanceof Ext.Component) {
                var center = this.findCenter(source.el);
                source = this.elementFromPoint(center[0], center[1]);
            }

            if (target instanceof Ext.Component) {
                var center = this.findCenter(target.el);
                target = this.elementFromPoint(center[0], center[1]);
            }
            this.SUPER(source, target, callback, scope, options, dragOnly);
        },

        /**
        * This method will simulate a drag and drop operation from a point (or DOM element) and move by a delta.
        * The following events will be fired, in order:  `mouseover`, `mousedown`, `mousemove` (along the mouse path), `mouseup`
        *   
        * @param {Ext.Component/HTMLElement/Array} source Either an element, or [x,y] as the drag starting point
        * @param {Array} delta The amount to drag from the source coordinate, expressed as [x,y]. E.g. [50, 10] will drag 50px to the right and 10px down.
        * @param {Function} callback (optional) To run this method async, provide a callback method to be called after the drag operation is completed.
        * @param {Object} scope (optional) the scope for the callback
        * @param {Object} options any extra options used to configure the DOM event
        * @param {Boolean} dragOnly true to skip the mouseup and not finish the drop operation.
        */
        dragBy : function(source, delta, callback, scope, options, dragOnly) {
            var Ext = this.getExt();
            if (source instanceof Ext.Component) {
                var center = this.findCenter(source.el);
                source = this.elementFromPoint(center[0], center[1]);
            }
            this.SUPER(source, delta, callback, scope, options, dragOnly);
        },

        /**
        * This method will simulate a mouse move to an Ext JS component, xy-coordinate or an element (the center of it)
        * @param {Ext.Component/HTMLElement/Array} target Either an element, or [x,y] as the target point
        * @param {Function} callback (optional) To run this method async, provide a callback method to be called after the operation is completed.
        * @param {Object} scope (optional) the scope for the callback
        */
        moveMouseTo : function(target, callback, scope) {
            var Ext = this.getExt();
            if (target instanceof Ext.Component) {
                var center = this.findCenter(target.el);
                target = this.elementFromPoint(center[0], center[1]);
            }
            this.SUPER(target, callback, scope);
        },

         /**
         * This method allow assertions to fail silently for tests executed in versions of Ext JS up to a certain release. When you try to run this test on a newer
         * version of Ext JS and it fails, it will fail properly and force you to re-investigate. If it passes in the newer version, you should remove the 
         * use of this method.
         * 
         * See also {@link Siesta.Test#todo}
         *   
         * @param {String} frameworkVersion The Ext JS framework version, e.g. '4.0.7'
         * @param {Function} fn The method covering the broken functionality
         * @param {String} reason The reason or explanation of the bug
        */
        knownBugIn : function(frameworkVersion, fn, reason) {
            var Ext = this.getExt();
            
            if (Ext.versions.extjs.isGreaterThan(frameworkVersion)) {
                fn.call(this.global, this);
            } else {
                this.todo(reason, fn);
            }
        },
        
        
         /**
         * This method will load the specified classes with `Ext.require()` and call the provided callback. Additionally it will check that all classes have been loaded.
         * 
         * This method accepts either variable number of arguments:
         *
         *      t.requireOk('Some.Class1', 'Some.Class2', function () { ... })
         * or array of class names:
         * 
         *      t.requireOk([ 'Some.Class1', 'Some.Class2' ], function () { ... })
         * 
         * @param {String} className1 The name of the class to `require`
         * @param {String} className2 The name of the class to `require`
         * @param {String} classNameN The name of the class to `require`
         * @param {Function} fn The callback. Will be called even if the loading of some classes have failed.
        */
        requireOk : function () {
            var me                  = this
            var global              = this.global
            var Ext                 = this.getExt()
            var args                = Ext.Array.flatten(Array.prototype.slice.call(arguments))
            
            var callback
            
            if (this.typeOf(args[ args.length - 1 ]) == 'Function') callback = args.pop()
            
            
            // what to do when loading completed or timed-out
            var continuation    = function () {
                me.endAsync(async)
                
                Joose.A.each(args, function (className) {
                    var cls     = Ext.ClassManager.get(className)
                    
                    //                       normal class                         singleton
                    if (cls && (me.typeOf(cls) == 'Function' || me.typeOf(cls.self) == 'Function'))
                        me.pass("Class: " + className + " was loaded")
                    else
                        me.fail("Class: " + className + " was loaded")
                })
                
                callback && callback()
            }
            
            var timeout         = Ext.isIE ? 120000 : 30000,
                async           = this.beginAsync(timeout + 100)
            
            var hasTimedOut     = false
            
            var timeoutId       = global.setTimeout(function () {
                hasTimedOut     = true
                continuation()
            }, timeout)
            
            Ext.Loader.setConfig({ enabled : true });

            Ext.require(args, function () {
                global.clearTimeout(timeoutId)
                
                if (!hasTimedOut) continuation() 
            })
        },
        
        /**
         * This method is a simple wrapper around the {@link #chainClick} - it performs a component query for provided `selector` starting from the `root` container
         * and then clicks on all found components, in order:
         * 

    // click all buttons in the `panel`
    t.clickComponentQuery('button', panel, function () {})
    
         * 
         * The 2nd argument for this method can be omitted and method can be called with 2 arguments only. In this case a global component query will be performed:
         *

    // click all buttons in the application
    t.clickComponentQuery('button', function () {})
    
         * 
         * @param {String} selector The selector to perform a component query with
         * @param {Ext.Container} root The optional root container to start a query from.
         * @param {Function} callback The callback to call, after clicking all the found components
         */
        clickComponentQuery : function (selector, root, callback) {
            
            if (arguments.length == 2 && this.typeOf(arguments[ 1 ] == 'Function')) {
                callback    = root
                root        = this.Ext().ComponentQuery
            }
            
            if (arguments.length == 1) {
                root        = this.Ext().ComponentQuery
            }
            
            var result      = root.query(selector)
            
            this.chainClick(result, function () { callback && callback.call(this, result) })
        },
        
        
        /**
         * An alias for {@link #clickComponentQuery}.
         * 
         * @param {String} selector The selector to perform a component query with
         * @param {Ext.Container} root The optional root container to start a query from.
         * @param {Function} callback The callback to call, after clicking all the found components
         */
        clickCQ : function () {
            this.clickComponentQuery.apply(this, arguments)
        }
    }
})
;
/**
 * 
@class Siesta.Test.jQuery
@extends Siesta.Test.Browser

A base class for testing jQuery applications. It inherit from {@link Siesta.Test.Browser} and adds various jQuery specific assertions.

This file is a reference only, for a getting start guide and manual, please refer to <a href="#!/guide/siesta_getting_started">Getting Started Guide</a>.

*/
Class('Siesta.Test.jQuery', {
    
    isa         : Siesta.Test.Browser,
        
    methods : {
        
        initialize : function() {
            // Since this test is preloading jQuery, we should let Siesta know what to 'expect'
            this.expectGlobals('$', 'jQuery');
            this.SUPER();
        },
     

        /**
         * This method returns the jQuery object from the scope of the test. When creating your own assertions for jQuery code, you need
         * to make sure you are using this method to get the `jQuery` instance. 
         * @return {Object} The `$` object from the scope of test
         */
        get$ : function () {
            return this.global.$;
        },

        normalizeElement : function(el) {
            return el.jquery ? el.get(0) : el;
        },

        drag: function (source, target, delta, callback, scope, options) {
            if (source && source.jquery) {
                source = source[0];
            }

            if (target && target.jquery) {
                target = target[0];
            }

            this.SUPER(source, target, delta, callback, scope, options);
        }
    }
})
;
Class('Siesta.Content.Manager.Browser', {
    
    isa     : Siesta.Content.Manager,
    
    has : {
//        baseUrl             : window.location.href.replace(/\?.*$/,'').replace(/\/[^/]*$/, '/'),
//        baseHost            : window.location.host,
//        baseProtocol        : window.location.protocol
    },
    
    
    methods : {
        
        // TODO check that URL can be actully fetched with XHR (same origin)
        load : function (url, onsuccess, onerror) {
            var req = new JooseX.SimpleRequest()
            
            try {
                req.getText(url, true, function (success, text) {
                    
                    if (!success) { 
                        onerror(this + " not found") 
                        return 
                    }
                    
                    onsuccess(text)
                })
            } catch (e) {
                onerror(e)
            }
        }
    }
})

;
/**
@class Siesta.Harness.Browser
@extends Siesta.Harness 

Class, representing the browser harness. This class provides a web-based UI and defines some additional configuration options.

The default value of the `testClass` configuration option in this class is {@link Siesta.Test.Browser}, which contains
only generic browser-related assertions. So, use this harness class, when testing a generic web page.

This file is for reference only, for a getting start guide and manual, please refer to <a href="#!/guide/siesta_getting_started">Getting Started Guide</a>.


Synopsys
========

    var Harness = Siesta.Harness.Browser;
    
    Harness.configure({
        title     : 'Awesome Test Suite',
        
        transparentEx       : true,
        
        autoCheckGlobals    : true,
        expectedGlobals     : [
            'Ext',
            'Sch'
        ],
        
        preload : [
            "http://cdn.sencha.io/ext-4.0.2a/ext-all-debug.js",
            "../awesome-project-all.js",
            {
                text    : "console.log('preload completed')"
            }
        ]
    })
    
    
    Harness.start(
        // simple string - url relative to harness file
        'sanity.t.js',
        
        // test file descriptor with own configuration options
        {
            url     : 'basic.t.js',
            
            // replace `preload` option of harness
            preload : [
                "http://cdn.sencha.io/ext-4.0.6/ext-all-debug.js",
                "../awesome-project-all.js"
            ]
        },
        
        // groups ("folders") of test files (possibly with own options)
        {
            group       : 'Sanity',
            
            autoCheckGlobals    : false,
            
            items       : [
                'data/crud.t.js',
                ...
            ]
        },
        ...
    )



*/

Class('Siesta.Harness.Browser', {
    
    // static
    my : {
        isa         : Siesta.Harness,
        
        has : {
            /**
             * @cfg {Class} testClass The test class which will be used for creating test instances, defaults to {@link Siesta.Test.Browser}.
             * You can subclass {@link Siesta.Test.Browser} and provide a new class. 
             * 
             * This option can be also specified in the test file descriptor. 
             */
            testClass           : Siesta.Test.Browser,
            
            viewportClass       : "Siesta.Harness.Browser.UI.Viewport",

            viewport            : null,
            
            /**
             * @cfg {Boolean} autoRun When set to `true`, harness will automatically launch the execution either of the checked test files or the whole suite.
             * Default value is `false`
             */
            autoRun             : false,
            
            /**
             * @cfg {Boolean} viewDOM When set to `true`, harness will expand the panel with the `<iframe>` of the test file, so you can examine the content of DOM.
             * Default value is `false`
             */
            viewDOM             : false,
            
            /**
             * @cfg {Boolean} speedRun When set to `true`, harness will reduce the quality or completely remove the visual effects for events simulation,
             * improving the speed of test. Default value is `true`.
             * 
             * This option can be also specified in the test file descriptor.
             */
            speedRun            : true,

            /**
             * @cfg {Boolean} breakOnFail When set to `true`, harness will not start launching any further tests after detecting a failed assertion.
             * improving the speed of test. Default value is `false`.   
             */
            breakOnFail         : false,

            contentManagerClass : Siesta.Content.Manager.Browser,
            scopeProvider       : 'Scope.Provider.IFrame',
            
            /**
             * @cfg {Boolean} disableCaching When set to `true`, harness will prevent the browser caching of files being preloaded and the test files, by appending
             * a query string to it.
             * Note, that in this case, debuggers may not understand that you are actually loading the same file, and breakpoints may not work. Default value is `false`
             */
            disableCaching      : false,
            
            baseUrl             : window.location.href.replace(/\?.*$/,'').replace(/\/[^/]*$/, '/'),
            baseHost            : window.location.host,
            baseProtocol        : window.location.protocol,
            
            /**
             * @cfg {Boolean} forceDOMVisible When set to `true` the tests will be ran in "fullscreen" mode, with their iframes on top of all other elements.
             * This is required in IE if your test includes interaction with the DOM, for example when using `document.getElementFromPoint()` method (it will not work unless the element
             * is visible).
             * 
             * This option can be also specified in the test file descriptor (usually you will create a group of "rendering" tests). Usually its only relevant for IE, 
             * so the usage may looks like:
             *
             * This option is enabled by default in IE and disabled in all other browsers.
             * 
    Harness.start(
        {
            group       : 'Rendering',
            
            forceDOMVisible    : $.browser.msie,
            
            items       : [
                'rendering/01_grid.t.js',
                ...
            ]
        },
        ...
    )
            
             */
            forceDOMVisible     : $.browser.msie,
            
            
            /**
             * @cfg {String} hostPageUrl The url of the html page which will be used for tests. 
             * 
             * **Note**, that {@link #preload} option will still be in effect, so make sure you don't double load your dependencies.
             * 
             * This option can be also specified in the test file descriptor.
             * 
             * For example, to define that test should be executed on the page generated by some php script:
    Harness.start(
        {
            hostPageUrl     : '../my_php_script?page=home',     // url of the html page for test
            url             : '020_home_page_drag_n_drop.t.js'  // url of the js file, containing actual test code
        },
        ...
    )
             *  
             * 
             */
            hostPageUrl         : null,
            
            
            /**
             * @cfg {Boolean} useStrictMode When set to `false` the test scopes will be created w/o strict mode `DOCTYPE`. Default value is `true`.
             * This option is not applicable for tests with `hostPageUrl` option. 
             * 
             * This option can be also specified in the test file descriptor.
             */
            useStrictMode       : true,
            
            
            /**
             * @cfg {String} runCore Either `parallel` or `sequential`. Indicates how the individual tests should be run - several at once or one-by-one.
             * 
             * Default value is "parallel", except of IE 6, 7, 8 where its set to `sequential`. You do not need to change this option usually.
             */
            runCore                 : 'parallel',
            
            
            /**
             * @cfg {Boolean} suppressEventsLog When set to `true` the harness will not create entries for events simulation in log. 
             * 
             * Default value is `true`
             * 
             * This option can be also specified in the test file descriptor.
             */
            suppressEventsLog   : true,
            
            
            /**
             * @cfg {String} simulateEventsWith
             * 
             * This option is IE9-strict mode (and probably above) specific. It specifies, which events simulation function Siesta should use. 
             * The choice is between 'dispatchEvent' (W3C standard) and 'fireEvent' (MS interface) - both are available in IE9 strict mode
             * and each activates different set of event listeners. See this blog post for detailed explanations: 
             * <http://www.digitalenginesoftware.com/blog/archives/76-DOM-Event-Model-Compatibility-or-Why-fireEvent-Doesnt-Trigger-addEventListener.html>
             * 
             * Valid values are "dispatchEvent" and "fireEvent".
             * 
             * The framework specific adapters (like {@link Siesta.Test.ExtJS} and like {@link Siesta.Test.jQuery}) chooses the most appropriate value 
             * automatically (unless explicitly configured). 
             */
            simulateEventsWith  : {
                is      : 'rw',
                init    : 'dispatchEvent'
            },
            
            // the currently "forced" (by the "forceDOMVisible" option) iframe 
            currentlyForcedIFrame       : null,
            testOfForcedIFrame          : null,
            
            // docs are below to disable the "defaults" auto-detection
            minViewportSize     : {
                init    : {
                    width       : 1024,
                    height      : 768
                }
            },
            
            /**
             * @cfg {Object} minViewportSize 
             * 
             * Minimal size of the test's viewport (usually size of the iframe with the test page). This option is useful, when your tests expects page
             * to have certain minimal size. However it may cause the misplacement of the whole harness UI in Chrome, in case when minimal size is bigger than
             * place for iframe.  
             * 
             * Should be an object with the following properties:
             * 
             * @cfg {Number} minViewportSize.width Minimal width, default value is 1024
             * @cfg {Number} minViewportSize.height Minimal height, default value is 768
             * 
             * **This is option is only active when {@link #maintainMinViewportSize} is enabled.**
             * 
             * This option can also be specified in the test file descriptor.
             */
            
            /**
             * @cfg {Boolean} maintainMinViewportSize
             * 
             * Enabling this option will cause Siesta to honor the {@link #minViewportSize} configuration option.
             * 
             * This option can also be specified in the test file descriptor.
             */
            maintainMinViewportSize     : false
        },
        
        
        after : {
            
            onBeforeScopePreload : function (scopeProvider, url) {
                if (this.viewport) this.viewport.onBeforeScopePreload(scopeProvider, url)
            },
            
            
            onTestSuiteStart : function (descriptors, contentManager) {
                if (this.viewport) this.viewport.onTestSuiteStart(descriptors, contentManager)
            },
            
            
            onTestSuiteEnd : function (descriptors, contentManager) {
                if (this.viewport) this.viewport.onTestSuiteEnd(descriptors, contentManager)
                
                // remove the links to forced iframe / test in hope to ease the memory pressure
                delete this.currentlyForcedIFrame
                delete this.testOfForcedIFrame
            },
            
            
            onTestStart : function (test) {
                if (this.viewport) this.viewport.onTestStart(test)
                
                var iframe
                
                if (iframe = this.testHasForcedIframe(test)) {
                    if (this.currentlyForcedIFrame) this.hideForcedIFrame(this.currentlyForcedIFrame)
                
                    this.showForcedIFrame(iframe, test)
                
                    this.currentlyForcedIFrame  = iframe
                    this.testOfForcedIFrame     = test
                }        
            },
            
            
            onTestUpdate : function (test, result) {
                if (this.viewport) this.viewport.onTestUpdate(test, result)
            },
            
            
            onTestEnd : function (test) {
                var iframe
                
                if (iframe = this.testHasForcedIframe(test)) {
                
                    this.hideForcedIFrame(iframe)
                
                    this.currentlyForcedIFrame  = null
                    this.testOfForcedIFrame     = null
                }
                
                if (this.viewport) this.viewport.onTestEnd(test)
            },
            
            
            onTestFail : function (test, exception, stack) {
                if (this.viewport) this.viewport.onTestFail(test, exception, stack)
            }
        },
        
        
        methods : {
            
            isAutomated : function () {
                return false
            },
            
            
            start : function () {
                // Opera's global variables handling is weird
                if ($.browser.opera) {
                    this.autoCheckGlobals = false;
                }
                
                if ($.browser.msie && $.browser.version !== "9.0") {
                    if (!this.hasOwnProperty('runCore'))            this.runCore            = 'sequential'
                    if (!this.hasOwnProperty('suppressEventsLog'))  this.suppressEventsLog  = true
                }
                
                this.SUPERARG(arguments)
            },
            
            
            launch : function () {
                var me = this
                
                
                if (!this.viewport && !this.isAutomated()) 
                    Ext.onReady(function () {
                        Ext.QuickTips.init();
                        
                        me.viewport = Ext.create(me.viewportClass, {
                            title           : me.title,
                            harness         : me
                        })
                    }) 
                else
                    this.SUPERARG(arguments)
            },
            
            
            populateCleanScopeGlobals : function (scopeProvider, callback) {
                if ($.browser.msie && Number(/^(\d+)/.exec($.browser.version)[ 1 ]) < 9) {
                    // do nothing for IE < 9 - testing leakage of globals is not supported
                    // also IE8 often crashes on this stage
                    this.disableGlobalsCheck = true
                    
                    callback()
                    
                    return
                }
                
                // always populate the globals from IFrame (even if user specified the Window provider)
                this.SUPER('Scope.Provider.IFrame', callback)
            },
            
            
            setup : function (callback) {
                var me      = this
                var sup     = this.SUPER

                // delay the super setup until dom ready
                if (!this.isAutomated()) {
                    Ext.onReady(function () {
                        Siesta.supports.init();
                    
                        sup.call(me, callback);
                    });
                } else {
                    $(function () {
                        Siesta.supports.init();
                    
                        sup.call(me, callback);
                    });
                }
            },
            
            
            getScopeProviderConfigFor : function (desc) {
                var config                      = this.SUPER(desc)
                
                config.sourceURL                = config.sourceURL || this.getDescriptorConfig(desc, 'hostPageUrl')
                config.minViewportSize          = config.minViewportSize || this.getDescriptorConfig(desc, 'minViewportSize')
                config.maintainMinViewportSize  = config.maintainMinViewportSize || this.getDescriptorConfig(desc, 'maintainMinViewportSize')
                
                if (!config.hasOwnProperty('useStrictMode')) config.useStrictMode = this.getDescriptorConfig(desc, 'useStrictMode')
                
                return config
            },
            
            
            getNewTestConfiguration : function (desc, scopeProvider, contentManager, options, runFunc) {
                var config          = this.SUPERARG(arguments)
                
                if (this.getDescriptorConfig(desc, 'speedRun')) {
                    
                    Joose.O.extend(config, {
                        actionDelay         : 1,
                        dragPrecision       : 20,
                        dragDelay           : 10
                    })
                }
                
                config.suppressEventsLog    = this.getDescriptorConfig(desc, 'suppressEventsLog')
                
                if (this.hasOwnProperty('simulateEventsWith')) config.simulateEventsWith = this.simulateEventsWith
                
                return config
            },
            
            
            runCoreGeneral : function (descriptors, contentManager, options, callback) {
                
                if (this.runCore == 'parallel') {
                    
                    var me                  = this
                    var canRunParallel      = []
                    var mustRunSequential   = []
                    
                    Joose.A.each(descriptors, function (desc) {
                        
                        if (me.getDescriptorConfig(desc, 'forceDOMVisible'))
                            mustRunSequential.push(desc)
                        else
                            canRunParallel.push(desc)
                    })
                    
                    this.runCoreParallel(canRunParallel, contentManager, options, function () {
                        
                        setTimeout(function () {
                            
                            me.runCoreSequential(mustRunSequential, contentManager, options, callback)
                            
                        }, 100)
                    })
                
                } else
                    this.SUPERARG(arguments)
            },
            
            
            normalizeURL : function (url) {
                // ref to JSAN module - DEPRECATED
                if (/^jsan:/.test(url)) url = '/jsan/' + url.replace(/^jsan:/, '').replace(/\./g, '/') + '.js'
                
                // ref to lib in current dist (no `/` and trailing `.js`) 
                if (!/\.js$/.test(url) && !/\//.test(url)) url = '../lib/' + url.replace(/\./g, '/') + '.js'
                
                return url
            },
            
            
            resolveURL : function (url, scopeProvider, desc) {
                
                // if the `scopeProvider` is provided and it has a sourceURL - then absolutize the preloads relative to that url
                if (scopeProvider && scopeProvider.sourceURL) url = this.absolutizeURL(url)
                
                if (this.disableCaching)
                    // if there's a ?param string in url - append new param
                    if (/\?./.test(url))
                        url += '&disableCaching=' + new Date().getTime()
                    else
                        if (!/\?$/.test(url)) 
                            url += '?disableCaching=' + new Date().getTime()
                
                // otherwise assumed to be a raw filename, relative or absolute
                return url
            },
            
            
            absolutizeURL : function (url, baseUrl) {
                // if the url is already absolute - just return it (perhaps with some normalization - 2nd case)
                if (/^http/.test(url))  return url
                if (/^\//.test(url))    return this.baseProtocol + '//' + this.baseHost + url
                
                baseUrl             = baseUrl || this.baseUrl
                
                // strip the potential query and filename from baseURL, leaving only the "directory" part
                baseUrl             = baseUrl.replace(/\?.*$/,'').replace(/\/[^/]*$/, '/')
                
                // first absolutize the base url relative the harness page (which will be always global, so it won't recurse)
                var absBaseUrl      = this.absolutizeURL(baseUrl, this.baseUrl)
                
                // add a trailing "/" if missing
                absBaseUrl          = absBaseUrl.replace(/\/?$/, '/')
                
                return absBaseUrl + url
            },
            
            
            // encapsulates the dirty-ness of the "forcedIframe" logic
            testHasForcedIframe : function (test) {
                if (this.getDescriptorConfig(this.getScriptDescriptor(test.url), 'forceDOMVisible') && (test.scopeProvider instanceof Scope.Provider.IFrame))
                    return test.scopeProvider.iframe
                else
                    return null
            },
            
            
            showForcedIFrame : function (iframe, test) {
                var desc                    = this.getScriptDescriptor(test.url)

                var minViewportWidth        = 0
                var minViewportHeight       = 0
                
                if (this.getDescriptorConfig(desc, 'maintainMinViewportSize')) {
                    var minViewportSize     = this.getDescriptorConfig(desc, 'minViewportSize')
                    
                    minViewportWidth        = minViewportSize && minViewportSize.width || 1024
                    minViewportHeight       = minViewportSize && minViewportSize.height || 768
                }
                
                Ext.fly(iframe).setStyle({
                    width       : Math.max(Ext.core.Element.getViewportWidth()  * 0.8 + 'px', minViewportWidth),
                    height      : Math.max(Ext.core.Element.getViewportHeight() * 0.8 + 'px', minViewportHeight),
                
                    'z-index'   : 100000,
                
                    border                      : '5px solid',
                    'border-color'              : '#d5e7f0',
                    '-moz-border-radius'        : '14px',
                    '-webkit-border-radius'     : '14px',
                    'border-radius'             : '14px'
                })
            
                Ext.fly(iframe).center()
            },
        
        
            hideForcedIFrame : function (iframe) {
                Ext.fly(iframe).setStyle({
                    left        : '-10000px',
                    top         : '-10000px',
                
                    'z-index'   : '',
                    border      : ''
                })
            }
        }
        
    }
    //eof my
})
//eof Siesta.Harness.Browser


;
Siesta.supports = {
    
    results : {},

    init : function() {
        var emptyFn = function() {},
            foo = Class({
                does    : Siesta.Test.Simulate.Event,
            
                has     : {
                    global      : null
                },
            
                methods : {
                    fireEvent           : emptyFn,
                    addResult           : emptyFn,
                    normalizeElement    : function(a) { return a[0]; },
                    findCenter          : function() { return [0,0]; }
                }
            });
        
        this.simulator = new foo({ global : window });

        for (var i = 0; i < this.tests.length; i++) {
            var test            = this.tests[i];
            var testId          = test.id;
            var detectorFn      = test.fn;
            
            // also save the results to "results" property - we'll use this in out own test suite
            // where we copy the feature testing results from the outer scope to inner
            this.results[ testId ] = this[ testId ] = detectorFn.call(this);
        }
    },

    tests : [
        {
            id : "mouseEnterLeave",
            fn : function() {
                var el = document.createElement("div");
                return 'onmouseenter' in el && 'onmouseleave' in el;
            }
        },

        {
            id : "enterOnAnchorTriggersClick",
            fn : function() {
                var sim     = this.simulator,
                    E       = Siesta.Test.Simulate.KeyCodes().keys.ENTER,
                    result  = false;
                    
                var anchor = $('<a href="foo">test me</a>');
                $('body').append(anchor);

                anchor.focus();
                anchor.click(function(e) {
                    result = true;
                    return false;
                });
        
                sim.simulateEvent(anchor, 'keypress', { keyCode : E, charCode : 0 }, true);
         
                anchor.remove();
                return result;
            }
        },

        {
            id : "canSimulateKeyCharacters",
            fn : function() {
        
                var sim = this.simulator;
                
                var input = $('<input type="text" />'),
                    A = Siesta.Test.Simulate.KeyCodes().keys.A;
                $('body').append(input);
                
                input.focus();
                sim.simulateEvent(input, 'keypress', { keyCode : A, charCode : A }, true);
                sim.simulateEvent(input, 'textInput', { text : "A" }, true);
        
                var result = input.val() === 'A';
                input.remove();
                return result;
            }
        },

        {
            id : "canSimulateBackspace",
            fn : function() {
                var sim = this.simulator;
                
                var input = $('<input type="text" />'),
                    BS = Siesta.Test.Simulate.KeyCodes().keys.BACKSPACE,
                    A = Siesta.Test.Simulate.KeyCodes().keys.A;
                $('body').append(input);
                
                input.focus();
                sim.simulateEvent(input, 'keypress', { keyCode : A, charCode : A }, true);
                sim.simulateEvent(input, 'keypress', { keyCode : A, charCode : A }, true);
                sim.simulateEvent(input, 'keypress', { keyCode : BS, charCode : BS }, true);
                var result = input.val() === 'A';
         
                input.remove();
                return result;
            }
        }
    ]
};
;
if (typeof Ext !== "undefined") {;
Ext.Component.override({

    slot            : null,
    __COLLECTOR__   : null,


    onRemoved : function() {
        if (this.__COLLECTOR__) {
            delete this.__COLLECTOR__.slots[this.slot]
            delete this.__COLLECTOR__
        }
    
        this.callOverridden(arguments)
    },


    beforeDestroy : function () {
        if (this.__COLLECTOR__) {
            delete this.__COLLECTOR__.slots[this.slot]
            delete this.__COLLECTOR__
        }
    
        this.callOverridden(arguments)
    }
})



Ext.Container.override({

    slots       : null,


    onAdd : function () {
    
        this.cascade(function (comp) {
            if (comp.slot && !comp.__COLLECTOR__) {
        
                var parentWithSlots = comp.__COLLECTOR__ = comp.up('{slots}')
            
                if (parentWithSlots) parentWithSlots.slots[ comp.slot ] = comp
            }
        })
    },


    initComponent : function () {
        if (this.slots) this.slots = {}
    
        this.callOverridden()
    }
})
;
Ext.define('Siesta.Harness.Browser.Model.TestFile', {

    extend      : 'Ext.data.Model',

    idProperty  : 'id',

    fields      : [
        'id',
        'url',
    
        'title',
    
        'passCount',
        'failCount',
        'todoPassCount',
        'todoFailCount',
    
        'time',
    
        {
            name            : 'checked',
            defaultValue    : false
        },
    
        {
            name            : 'folderStatus',
            defaultValue    : 'yellow'
        },
    
        // will be set to true for all tests, once the users clicks "run"
        'isStarting',
        // will be set to true, right before the scope preload begin
        'isStarted',
        // will be set to true, after preload ends and tests launch
        'isRunning',
        'isMissing',
        'isFailed',
    
        // composite objects
        'assertionsStore',
        'test',
        'descriptor'
    ],


    init : function () {
        this.internalId     = this.getId() || this.internalId
    },


    computeFolderStatus : function () {
        if (!this.childNodes.length) return 'yellow'
    
        var hasFailed       = false
        var allGreen        = true
    
        Joose.A.each(this.childNodes, function (childNode) {
        
            if (childNode.isLeaf()) {
                var test    = childNode.get('test')
            
                if (test && test.isFailed()) {
                    allGreen    = false
                    hasFailed   = true
                
                    // stop iteration
                    return false
                }
            
                if (test && !test.isPassed())   allGreen = false
                if (!test)                      allGreen = false
            
            } else {
                var status  = childNode.computeFolderStatus()
            
                if (status == 'red') {
                    allGreen    = false
                    hasFailed   = true
                
                    // stop iteration
                    return false
                }
            
                if (status == 'yellow')         allGreen = false
            }
        })
    
        if (hasFailed)  return 'red'
        if (allGreen)   return 'green'
    
        return 'yellow'
    },


    updateFolderStatus : function () {
        this.set('folderStatus', this.computeFolderStatus())
    
        var parentNode  = this.parentNode
    
        if (parentNode && !parentNode.isRoot()) parentNode.updateFolderStatus()
    }
})
;
Ext.define('Siesta.Harness.Browser.Model.Assertion', {

    extend      : 'Ext.data.Model',

    idProperty  : 'index',

    fields      : [
        'index', 
        'passed',
        'isTodo',
        'description',
        'annotation',
        'type',
    
        // For logging simulated events (will also have a type as for diagnostic messages)
        { name : 'isSimulatedEvent', type : 'boolean', defaultValue : false },
        'eventType'
    ]
})
;
Ext.define('Siesta.Harness.Browser.UI.VersionField', {
    
    extend  : 'Ext.form.field.Spinner',
    alias   : 'widget.versionfield',
    
    
    width   : 90,
    
    // versions : read from harness?
    versions : [
        '4.0.2a',
        '4.0.4',
        '4.0.5',
        '4.0.6',
        '4.0.7',
        '4.1.0-beta-2',
        '4.1.0-beta-3',
        '4.1.0'
    ],
    

    onSpinUp: function() {
        var me = this;
        var index = Ext.Array.indexOf(me.versions, me.getValue());
        if (index < me.versions.length - 1) {
            me.setValue(me.versions[index+1]);
        }
    },

    onSpinDown: function() {
        var val, me = this;
        var index = Ext.Array.indexOf(me.versions, me.getValue());
        if (index > 0) {
            me.setValue(me.versions[index-1]);
        }
    }
});
;
Ext.define('Siesta.Harness.Browser.UI.Center', {
    extend              : 'Ext.Panel',
    alias               : 'widget.centerpanel',
    border              : false,
    style : 'background:transparent',
    bodyStyle : 'background:transparent',
    layout              : {
        type : 'hbox',
        align : 'stretch'
    },

    initComponent : function() {

        Ext.apply(this, {
            
            items : [
                {
                    style : 'background:transparent',
                    bodyStyle : 'background:transparent',
                    border : false,
                    flex : 1,
                    layout              : {
                        type : 'vbox',
                        align : 'stretch'
                    },
                    items : [
                        {
                            margins     : '20 5 10 20',
                            flex : 1,
                            title : 'Tips and tricks',
               
                            html : 'Tips....'
                        },
                        {
                            margins     : '10 5 20 20',
                            flex : 1,
                            cls : 'videos',
                            title : 'Siesta videos',
                            autoScroll : true,
                            html : '<dl><dt>' + 
                                            '<a href="http://www.bryntum.com/products/siesta/docs/#!/video/32559451" target="_blank"  class="video"></a>' + 
                                        '</dt>' + 
                                        '<dd>In this video we introduce Siesta - the stress-free JavaScript unit testing tool</dd>' +
                                    '</dl>' +
                                    '<dl><dt>' + 
                                            '<a href="http://www.bryntum.com/products/siesta/docs/#!/video/32559505" target="_blank"  class="video"></a>' + 
                                        '</dt>' + 
                                        '<dd>In this video we will show how to do a basic ExtJS UI test with Siesta</dd>' +
                                    '</dl>' +
                                    '<dl><dt>' + 
                                            '<a href="http://www.bryntum.com/products/siesta/docs/#!/video/32559560" target="_blank" class="video"></a>' + 
                                        '</dt>' + 
                                        '<dd>In this video we will show how to automate your test suite using PhantomJS or Selenium</dd>' +
                                    '</dl>' + 
                                    '<dl><dt>' + 
                                            '<a href="http://www.bryntum.com/products/siesta/docs/#!/guide/siesta_getting_started" target="_blank" class="book"></a>' + 
                                        '</dt>' + 
                                        '<dd>Getting started with Siesta</dd>' +
                                    '</dl>' +
                                    '<dl><dt>' + 
                                            '<a href="http://www.bryntum.com/products/siesta/docs/#!/guide/siesta_automation" target="_blank" class="book"></a>' + 
                                        '</dt>' + 
                                        '<dd>Siesta automation and continuous integration</dd>' +
                                    '</dl>' +
                                    '<dl><dt>' + 
                                            '<a href="http://www.bryntum.com/products/siesta/docs/#!/guide/testing_mvc_app" target="_blank" class="book"></a>' + 
                                        '</dt>' + 
                                        '<dd>Testing Ext JS MVC</dd>' +
                                    '</dl>' + 
                                    '<dl><dt>' + 
                                            '<a href="http://www.bryntum.com/products/siesta/docs/#!/guide/extending_test_class" target="_blank" class="book"></a>' + 
                                        '</dt>' + 
                                        '<dd>Extending Siesta</dd>' +
                                    '</dl>'
                        }
                    ]
                },
                {
                    style : 'background:transparent',
                    bodyStyle : 'background:transparent',
                    flex : 1,
                    border : false,
                    layout              : {
                        type : 'vbox',
                        align : 'stretch'
                    },
                    items : [
                        {
                            cls : 'splash-links',
                            margins     : '20 20 10 20',
                            flex : 1,
                            title : 'Useful links',
                            autoScroll : true,
                            html : '<ul>' +
                                        '<li>' + 
                                            '<a class="docs" href="http://www.bryntum.com/products/siesta/docs" target="_blank">API Documentation</a>' +
                                        '</li>' + 
                                        '<li>' + 
                                            '<a class="report-bug" href="http://www.assembla.com/spaces/bryntum/support/tickets" target="_blank">Report a bug</a>' +
                                        '</li>' + 
                                        '<li>' + 
                                            '<a class="forum" href="http://www.bryntum.com/forum/viewforum.php?f=20&sid=b18136bc962e28494e2e7614382b27d5" target="_blank">Community Forums</a>' +
                                        '</li>' + 
                                        '<li>' + 
                                            '<a class="buy" href="http://www.bryntum.com/store/siesta" target="_blank">Buy a license</a>' +
                                        '</li>' + 
                                    '</ul>'
                        },
                        {
                            flex : 1,
                            margins     : '10 5 20 20',
                            title : 'Change log for v.'+ (Siesta.meta.VERSION || "1.0.0"),
                            html : 'TODO'
                        }
                    ]
                }

            ]
        });
        this.callParent();
    }
})
;
Ext.define('Siesta.Harness.Browser.UI.Header', {
    extend              : 'Ext.Panel',
    
    region              : 'north',
    slot                : 'title',
                
    cls                 : 'tr-title',
    html                : '<a href="http://bryntum.com"><div class="tr-logo"></div></a>',
    border              : false,
    height              : 115, 
    buttonAlign         : 'center',
    minButtonWidth      : 42,
    
    viewport            : null,
    stateConfig         : null,
    
    
    initComponent : function() {
        var state = this.stateConfig;

        Ext.apply(this, {
            buttons    : [
                {
                    xtype       : 'label',
                    cls         : "tr-version-indicator",
                    html        : 'v.' + (Siesta.meta.VERSION || "1.0.0")
                },
                {
                    iconCls     : 'tr-icon-run-checked',
                    tooltip     : 'Run checked',
                    scale       : 'large',
                    actionName  : 'run-checked',
                    scope       : this,
                    handler     : this.onBtnClicked
                },
                {
                    iconCls     : 'tr-icon-run-failed',
                    tooltip     : 'Run failed',
                    scale       : 'large',
                    actionName  : 'run-failed',
                    scope       : this,
                    handler     : this.onBtnClicked
                },
                {
                    iconCls     : 'tr-icon-run-all',
                    tooltip     : 'Run all',
                    scale       : 'large',
                    actionName  : 'run-all',
                    scope       : this,
                    handler     : this.onBtnClicked
                },
                {
                    iconCls     : 'tr-icon-stop',
                    tooltip     : 'Stop',
                    scale       : 'large',
                    actionName  : 'stop',
                    scope       : this,
                    handler     : this.onBtnClicked
                },
                {
                    tooltip     : 'Options...',
                    iconCls     : 'tr-icon-options',
                    scale       : 'large',
                                
                    menu     : new Ext.menu.Menu({
                        defaults : {
                            scope           : this,
                            checkHandler    : this.onOptionChange
                        },
                        listeners : {
                            beforeshow  : this.onSettingsMenuBeforeShow,
                            scope       : this
                        },
                        items : [
                            {
                                text        : 'View DOM',
                                option      : 'viewDOM',
                                checked     : state.viewDOM
                            },
                            {
                                text        : 'Transparent exceptions',
                                option      : 'transparentEx',
                                checked     : state.transparentEx
                            },
                            {
                                text        : 'Cache preloads',
                                option      : 'cachePreload',
                                checked     : state.cachePreload
                            },
                            {
                                text        : 'Auto launch',
                                option      : 'autoRun',
                                checked     : state.autoRun
                            },
                            {
                                text        : 'Keep results',
                                option      : 'keepResults',
                                checked     : state.keepResults
                            },
                            {
                                text        : 'Speed run',
                                option      : 'speedRun',
                                checked     : state.speedRun
                            },
                            {
                                text        : 'Break on fail',
                                option      : 'breakOnFail',
                                checked     : state.breakOnFail
                            }
                        ]
                    })
                }
            ].concat(this.buttons || [])
        });
        this.callParent();
    },
    
    
    afterRender : function () {
        this.callParent(arguments);
        
        var logoEl  = document.createElement('div')
        
        logoEl.className    = "tr-siesta-logo"
        logoEl.innerHTML    = '&nbsp;'
        
        this.el.appendChild(logoEl)
        
        Ext.get(logoEl).on('click', function () { this.fireEvent('logoclick', this); }, this)
    },
    

    onBtnClicked : function(btn) {
        if(btn.actionName) { 
            this.fireEvent('buttonclick', this, btn, btn.actionName); 
        }
    },
    
    onSettingsMenuBeforeShow : function(menu) {
        this.fireEvent('beforesettingsmenushow', this, menu); 
    },

    onOptionChange : function(button, state) {
        this.fireEvent('optionchange', this, button.option, state); 
    }
})
//eof Siesta.Harness.Browser.UI.Header
;
Ext.define('Siesta.Harness.Browser.UI.ExtHeader', {
    
    extend          : 'Siesta.Harness.Browser.UI.Header',
    
    versionField    : null,
    
    
    initComponent : function() {
        
        if (this.viewport.getOption('allowExtVersionChange')) {
            this.buttons = [
                {
                    xtype : 'label',
                    text  : 'Ext '
                },
                this.versionField = new Siesta.Harness.Browser.UI.VersionField({
                    // not so good line
                    value   : this.viewport.harness.findExtVersion(),
                    
                    listeners : {
                        change  : this.onVersionChange,
                        scope   : this
                    }
                })
            ];
        }
        
        this.callParent();
    },

    
    onVersionChange : function() {
        this.fireEvent('optionchange', this, 'extVersion', this.versionField.getValue());
    }
})
//eof Siesta.Harness.Browser.UI.ExtHeader
;
Ext.define('Siesta.Harness.Browser.UI.Viewport', {

    extend          : 'Ext.container.Viewport',


    title           : null,

    harness         : null,

    // need to set stateful properties before `initComponent`
    stateful        : false,

    // stateful
    selection       : null,
    selectedURL     : null,
    // eof stateful

    testsStore      : null,

    contextMenu     : null,

    verticalCenteredTpl     : new Ext.XTemplate(
        '<div class="tr-vertical-align-helper-content {cls}">{text}</div>',
        '<div class="tr-vertical-align-helper"></div>',
        { 
            compiled : true 
        }
    ),

//    statusIndicatorEl   : null,
    
    header          : null,
    headerClass     : 'Siesta.Harness.Browser.UI.Header',
    
    collapsedNodes  : null,


    initComponent : function () {
        Ext.state.Manager.setProvider(new Ext.state.CookieProvider())

        this.selection      = {}
    
        this.applyState(Ext.state.Manager.get(this.getStateId()))
    
    
        var testsStore      = this.testsStore = new Ext.data.TreeStore({
            model           : 'Siesta.Harness.Browser.Model.TestFile',
        
            sortOnLoad      : false,
        
            root            : { expanded : true, loaded : true },
        
            proxy           : {
                type        : 'memory',
            
                data        : this.buildTreeData({
                    id          : 'root',
                    group       : 'test suite' + this.title,
                    items       : this.harness.descriptors
                }).children,
            
                reader      : {
                    type    : 'json'
                }
            },
            
            listeners       : {
                collapse    : this.saveState,
                expand      : this.saveState,
                
                scope       : this
            }
        })
    
        testsStore.load()
        
        var header  = this.header = Ext.create(this.headerClass, {
            viewport        : this,
            stateConfig     : this.getState()
        });       
        
        header.on({
            optionchange            : this.onOptionChange,
            beforesettingsmenushow  : this.onSettingsMenuBeforeShow,
            buttonclick             : this.onHeaderButtonClick,
            logoclick               : this.onLogoClick,
            
            scope                   : this
        });
        
        
        Ext.apply(this, {
            plugins         : Ext.isIE ? undefined : new Siesta.Harness.Browser.UI.MouseVisualizer(this.harness),
            slots           : true,
        
            contextMenu     : this.buildContextMenu(),
        
            layout  : 'border',
            items   : [
                header,
                // main content area
                {
                    region  : 'center',
                
                    layout : 'border',
                
                    items : [
                        {
                            region      : 'west',
                        
                            xtype       : 'testgrid',
                            slot        : 'filesTree',
                        
                            iconCls     : 'tr-status-neutral-small',
                        
                            animate     : !Ext.isIE,    // TODO, comment in 4.1.0 :)
                            //split       : true,       // TODO, uncomment in 4.1.0
                        
                            viewConfig  : {
                                toggleOnDblClick : false,
                                listeners   : {
                                    refresh             : this.onViewReady,
                                
                                    scope               : this,
                                    single              : true
                                }
                            },
                        
                            listeners   : {
                                selectionchange     : this.onSelectionChange, 
                                checkchange         : this.onCheckChange,
                            
                                itemcontextmenu     : this.onFilesContextMenu,
                                itemdblclick        : this.onTestFileDoubleClick,
                            
                                scope               : this
                            },
                        
                            store       : testsStore
                        },
                        {
                            region      : 'center',
                            slot        : 'mainArea',
                            slots       : true,
                            border      : false,
                            layout      : 'card',
                            activeItem  : 0,
//                            style       : 'background:transparent',
//                            bodyStyle   : 'background:transparent',
                            cls         : 'mainarea-panel tr-main-area-centered',

                            html        : this.verticalCenteredTpl.apply({ cls : 'tr-rounded-box', text : 'Test suite has not been launched yet' })
                            
//                            items       : [{ xtype : 'centerpanel', slot : 'splashpanel' }]
                        }
                    ]
                }
                // eof main content area
            ]
        })
    
        this.callParent()
    
        var slots       = this.slots
    
        // delay is required to avoid recursive loop
        this.on('afterlayout', this.onAfterLayout, this, { single : true, delay : 1 })
    },

    
    buildTreeData : function (descriptor) {
        var data    = {
            id          : descriptor.id,
            title       : descriptor.group || descriptor.title || descriptor.name || descriptor.url,
            descriptor  : descriptor
        }
    
        var me              = this
        var prevId          = data.id
        var collapsedNodes  = this.collapsedNodes || {}
    
        if (descriptor.group) {
        
            var children    = []
        
            Ext.each(descriptor.items, function (desc) {
                children.push(me.buildTreeData(desc))
            })
        
            Ext.apply(data, {
                expanded        : collapsedNodes[ prevId ] != null ? false : true,
                // || false is required for TreeView - it checks that "checked" field contains Boolean
                checked         : me.selection[ prevId ] || false,
            
                folderStatus    : 'yellow',
            
                children        : children,
                leaf            : false
            })
        
        } else {
            Ext.apply(data, {
                url             : descriptor.url,
            
                leaf            : true,
                // || false is required for TreeView - it checks that "checked" field contains Boolean
                checked         : me.selection[ prevId ] || false,
            
                passCount       : 0,
                failCount       : 0,
            
                time            : 0,
            
                assertionsStore : new Ext.data.Store({
                    //autoDestroy : true,
                    model       : 'Siesta.Harness.Browser.Model.Assertion',

                    // Prevent datachanged event from being fired by an eventStore add action
                    insert : function() {
                        var O = Ext.util.Observable;
                        O.capture(this, function(name) {
                            return name !== 'datachanged';
                        });
                        this.self.prototype.insert.apply(this, arguments);
                        O.releaseCapture(this);
                    }
                })
            })
        }
    
        return data
    },


    onAfterLayout : function () {
//        this.statusIndicatorEl  = this.slots.title.el.down('.tr-status-indicator')
    
        if (this.getOption('autoRun')) {
            var checked     = this.getChecked()
        
            // either launch the suite for checked tests or for all
            this.harness.launch(checked.length && checked || this.harness.descriptors)
        }
    },


    onViewReady : function () {
        if (this.selectedURL) {
            var testFile    = this.testsStore.getNodeById(this.selectedURL)
        
            if (testFile) this.slots.filesTree.getSelectionModel().select(testFile)
        }
    },


    onSelectionChange : function (selModel, selectedRecords) {
    
        if (selectedRecords.length) {
            var testFile        = selectedRecords[ 0 ]
        
            if (testFile.get('test')) this.activateAssertionsGridFor(testFile)
        
            this.selectedURL = testFile.getId()
        
            this.saveState()
        }
    },


    onCheckChange : function (testFile, checked) {
        this.setNodeChecked(testFile, checked)
    },


    setNodeChecked : function (testFile, checked, doNotCascade, skipSave) {
        var me      = this
        var id      = testFile.getId()
    
        if (checked)
            this.selection[ id ] = true
        else
            delete this.selection[ id ]

        
        testFile.set('checked', checked)
        
        // when unchecking the node - uncheck the parent node (folder) as well 
        if (!checked && testFile.parentNode) me.setNodeChecked(testFile.parentNode, false, true, true)
    
        // only cascade for folders and when `doNotCascade` is false
        if (!testFile.isLeaf() && !doNotCascade) Ext.each(testFile.childNodes, function (childNode) {
            me.setNodeChecked(childNode, checked, false, true)
        })
    
        if (!skipSave) this.saveState()
    },


    onTestSuiteStart : function (descriptors) {
        var harness             = this.harness
        var filesTree           = this.slots.filesTree
        var selModel            = filesTree.getSelectionModel()
        var prevSelection       = selModel.getLastSelected()
        var testsStore          = this.testsStore
    
        this.resetDescriptors(descriptors);
    
        // restore the selection after data reload
        if (prevSelection) selModel.select(testsStore.getNodeById(prevSelection.getId()))
    
        var mainArea    = this.slots.mainArea
    
        Joose.A.each(harness.flattenDescriptors(descriptors), function (descriptor) {
        
            testsStore.getNodeById(descriptor.id).set({
                isMissing   : descriptor.isMissing,
                isStarting  : true
            })
        
            var prevAssertionGrid   = mainArea.slots[ 'assertGrid' + descriptor.url ]
        
            if (prevAssertionGrid) {
                
                // REMOVE_AFTER_SPLASH
                // the same for the case when there was no selected grid
                if (!prevSelection || descriptor.url == prevSelection.getId()) { 
                    mainArea.el.addCls('tr-main-area-centered')
                    mainArea.body.down('> .tr-rounded-box').addCls('launching');
                    mainArea.body.down('> .tr-rounded-box').update('Launching the test');
                }
                // REMOVE_AFTER_SPLASH
                
                prevAssertionGrid.destroy()
            }
        })
    
        filesTree.setIconCls('tr-status-running-small')
        filesTree.setTitle('Running...')
    },


    resetDescriptors : function(descriptors) {
        var testsStore          = this.testsStore;

        Joose.A.each(this.harness.flattenDescriptors(descriptors), function(descriptor){
            var testRecord = testsStore.getNodeById(descriptor.id);
        
            testRecord.get('assertionsStore').removeAll(true)
            testRecord.reject();
            // || false is required for TreeView - it checks that "checked" field contains Boolean
            testRecord.set('checked', this.selection[ descriptor.id ] || false)
        }, this);
    },

    
    onTestSuiteEnd : function (descriptors) {
        this.updateStatusIndicator()
    },
    
    
    // returns the NodeStore of the TreeStore - flattened presentation of the tree + its potentially filtered
    getNodeStore : function () {
        return this.slots.filesTree.getView().store
    },
    
    
    forEachTestFile : function (func, scope) {
        var nodeStore   = this.getNodeStore()
        
        if (nodeStore.isFiltered())
            nodeStore.each(func, scope)
        else
            Ext.Array.each(this.testsStore.tree.flatten(), func, scope)
    },


    getChecked : function () {
        var descriptors     = []
    
        this.forEachTestFile(function (testFileRecord) {
        
            if (testFileRecord.get('checked') && testFileRecord.isLeaf()) descriptors.push(testFileRecord.get('descriptor'))
        })
    
        return descriptors
    },

    runChecked : function () {
        this.harness.launch(this.getChecked())
    },


    runFailed : function () {
        var descriptors     = []
    
        this.forEachTestFile(function (testFileRecord) {
        
            var test    = testFileRecord.get('test')
        
            if (test && test.isFailed()) descriptors.push(testFileRecord.get('descriptor'))
        })
    
        this.harness.launch(descriptors)
    },


    runAll : function () {
        var allDesc     = []
        
        this.forEachTestFile(function (testFile) {
            if (testFile.isLeaf()) allDesc.push(testFile.get('descriptor')) 
        })
        
        this.harness.launch(allDesc)
    },


    stopSuite : function (button) {
        this.performStop();
        button.disable()
    
        setTimeout(function () {
        
            button.enable()
        
        }, 1000)
    },

    performStop : function() {
        this.harness.needToStop = true;
    
        Ext.each(this.testsStore.tree.flatten(), function (testFileRecord) {
            if (testFileRecord.get('isStarting') && !testFileRecord.get('isStarted')) {
                testFileRecord.set('isStarting', false);
            }
        });
    },


    activateAssertionsGridFor : function (testFile, config) {
        var url                 = testFile.get('url')
        var mainArea            = this.slots.mainArea
    
        var assertionGrid       = mainArea.slots[ 'assertGrid' + url ]
    
        if (assertionGrid && assertionGrid.isStale) {
            assertionGrid.destroy()
        
            assertionGrid = null
        }

        var me              = this;
        var harness         = this.harness
        var testDescriptor  = harness.getScriptDescriptor(testFile.getId())
        
        if (!assertionGrid) assertionGrid = mainArea.add(Ext.apply({
            xtype       : 'assertiongrid',
            slot        : 'assertGrid' + url,
        
            title       : testFile.get('title') || url,
        
            testRecord      : testFile,
            
            maintainMinViewportSize : harness.getDescriptorConfig(testDescriptor, 'maintainMinViewportSize'),
            minViewportSize         : harness.getDescriptorConfig(testDescriptor, 'minViewportSize'),
            
            halfWidth       : mainArea.getWidth() / 2,
            viewDOM         : this.getOption('viewDOM'),
            
            listeners       : {
                viewdomchange : function(g, value) {
                    me.setOption('viewDOM', value);
                    me.saveState();
                },
                
                rerun         : function () {
                    harness.launch([ testDescriptor ]);
                }
            }
        }, config))
        
        assertionGrid.setViewDOM(this.getOption('viewDOM'), true);
        mainArea.getLayout().setActiveItem(assertionGrid)
        
        // REMOVE_AFTER_SPLASH 
        mainArea.el.removeCls('tr-main-area-centered')
        // REMOVE_AFTER_SPLASH 
    },


    // looks less nice than setting it only after preload for some reason
    onBeforeScopePreload : function (scopeProvider, url) {
        var testRecord          = this.testsStore.getNodeById(url)
    
        // to avoid disturbing grid
        testRecord.data.isStarted = true
    
//        // will trigger an update in grid
//        testRecord.set('isRunning', true)
    },


    isTestRunningVisible : function (test) {
        // return false for test's running in popups (not iframes), since we can't show any visual accompaniment for them
        if (!(test.scopeProvider instanceof Scope.Provider.IFrame)) return false
    
        // if there is a "forced to be on top" test then we only need to compare the tests instances
        if (this.harness.testOfForcedIFrame) {
            return this.harness.testOfForcedIFrame == test
        }
    
        // otherwise the only possibly visible test is the one of the current assertion grid
        var currentAssertionGrid = this.getActiveAssertionGrid();
    
        // if no assertions grids in main area - no visible tests
        // same if the assertion grid is from other test
        if (!currentAssertionGrid || currentAssertionGrid.testRecord.get('test') != test) {
            return false;
        }
    
        // now we know that visible assertion grid is from our test and there is no "forced on top" test
        // we only need to check visibility (collapsed / expanded of the right panel 
        return currentAssertionGrid.isFrameVisible()
    },


    onTestStart : function (test) {
        var testRecord          = this.testsStore.getNodeById(test.url)
        var testDescriptor      = this.harness.getScriptDescriptor(test.url)
    
        // when starting the test with forcedIframe - do not allow the assertion grid to change the location of the iframe
        var assertionGridConfig = {
            canManageDOM    : !this.harness.testHasForcedIframe(test)
        }
    
        testRecord.beginEdit()
    
        // will trigger an update in grid
        testRecord.set({
            test        : test,
            isRunning   : true
        })
    
        testRecord.endEdit()
    
        var currentSelection    = this.slots.filesTree.getSelectionModel().getLastSelected()
    
        var mainArea            = this.slots.mainArea
    
        // activate the assertions grid for currently selected row, or, if the main area is empty
        if (
            currentSelection && currentSelection.getId() == test.url 
                || 
            !mainArea.items.getCount() 
                || 
            // `onTestSuiteStart` will remove all other assertion grid except active one (marked as stale)
            // when there's no selected row - need to update that stale grid
            mainArea.items.getCount() == 1 && mainArea.items.getAt(0).isStale
        ) 
            this.activateAssertionsGridFor(testRecord, assertionGridConfig)
    },


    onTestUpdate : function (test, result) {
        
        var testRecord  = this.testsStore.getNodeById(test.url),
            failCount = test.getFailCount();
    
        testRecord.beginEdit()

        testRecord.set({
            'passCount' : test.getPassCount(),
            'failCount' : failCount,
            'todoPassCount' : test.getTodoPassCount(),
            'todoFailCount' : test.getTodoFailCount()
        });
    
        testRecord.endEdit()
    
        testRecord.get('assertionsStore').add({
            index               : result.index,
            passed              : result.passed,
            isTodo              : result.isTodo,
            description         : result.description,
            annotation          : result.annotation,
            type                : result.meta.name,

            // For logging simulated events
            isSimulatedEvent    : result.isSimulatedEvent,
            eventType           : result.type
        })
    
        testRecord.parentNode && testRecord.parentNode.updateFolderStatus()

        if (failCount > 0 && this.getOption('breakOnFail')) {
            this.performStop();
            this.slots.filesTree.getSelectionModel().select(testRecord);
        }
    },



    onTestEnd : function (test) {
        var testRecord          = this.testsStore.getNodeById(test.url)
        var testDescriptor      = this.harness.getScriptDescriptor(test.url)
    
        testRecord.set('time', test.getDuration() + 'ms')
    
        testRecord.get('assertionsStore').add({
            passed      : true,
            description : test.getSummaryMessage('<br>'),
            type        : 'Siesta.Result.Diagnostic'
        })
    
        testRecord.parentNode && testRecord.parentNode.updateFolderStatus()
    
        if (this.harness.testHasForcedIframe(test)) {
            var assertionGrid = this.slots.mainArea.slots[ 'assertGrid' + test.url ]
        
            if (assertionGrid) assertionGrid.setCanManageDOM(true)
        }
    },


    onTestFail : function (test, exception, stack) {
        var testRecord  = this.testsStore.getNodeById(test.url)
    
        testRecord.set('isFailed', true)
    
        testRecord.parentNode && testRecord.parentNode.updateFolderStatus()
    
        // if the test failed already after its finish (some exception in the `setTimeout` for example)
        // need to add additional message to user
        // however, if the exception happened when test is still running, it will be included in the `getSummaryMessage`
        if (test.isFinished()) testRecord.get('assertionsStore').add({
            passed      : true,
            description : 'Test suite threw an exception: ' + exception + (stack ? '<br>' + stack.join('<br>') : ''),
            type        : 'Siesta.Result.Diagnostic'
        })
    },
    
    
    getOption : function (name) {
        switch (name) {
            case 'selection'    : return this.selection
            
            case 'selectedURL'  : return this.selectedURL
            
            default             : return this.harness[ name ]
        }
    },
    
    
    setOption : function (name, value) {
        switch (name) {
            case 'selection'    : return this.selection     = value
            
            case 'selectedURL'  : return this.selectedURL   = value
            
            case 'collapsedNodes': return this.collapsedNodes   = value
            
            default             : return this.harness[ name ] = value
        }
    },


    getState : function () {
        return {
            // harness configs
            autoRun         : this.getOption('autoRun'),
            speedRun        : this.getOption('speedRun'),
            viewDOM         : this.getOption('viewDOM'),
            keepResults     : this.getOption('keepResults'),
            cachePreload    : this.getOption('cachePreload'),
            transparentEx   : this.getOption('transparentEx'),
            breakOnFail     : this.getOption('breakOnFail'),
        
            // UI configs
            selection       : this.selection,
            selectedURL     : this.selectedURL,
            
            collapsedNodes  : this.getCollapsedFolders()
        }
    },
    
    
    getCollapsedFolders : function () {
        var collapsed        = {}
        
        Joose.A.each(this.testsStore.tree.flatten(), function (treeNode) {
            if (!treeNode.isLeaf() && !treeNode.isExpanded()) collapsed[ treeNode.getId() ] = ''
        })
        
        return collapsed
    },
    
    
    applyState : function (state) {
        var me  = this
        
        if (state) Joose.O.each(state, function (value, name) {
            me.setOption(name, value)
        })
    },


    getStateId : function () {
        return 'test-run-' + this.title
    },


    onOptionChange : function (component, optionName, optionValue) {
        this.setOption(optionName, optionValue)
    
        if (optionName == 'viewDOM') {
            var activeAssertionGrid = this.getActiveAssertionGrid()
            
            activeAssertionGrid && activeAssertionGrid.setViewDOM(optionValue);
        }

        this.saveState()
    },


    saveState : function () {
        Ext.state.Manager.set(this.getStateId(), this.getState())
    },


    uncheckAllExcept : function (testFile) {
        var me      = this
    
        Ext.each(this.testsStore.tree.flatten(), function (node) {
        
            if (node != testFile) me.setNodeChecked(node, false, true)
        })
    },
    
    
    getActiveAssertionGrid : function() {
        var item    = this.slots.mainArea.getLayout().getActiveItem()
        
        // if active item is a splash screen return "null" to indicate that no assertion grid is active
        return item instanceof Siesta.Harness.Browser.UI.Center ? null : item;
    },

    
    buildContextMenu : function () {
        return new Ext.menu.Menu({
        
            renderTo    : Ext.getBody(),
        
            defaults    : {
                scope   : this
            },
        
            items       : [
                {
                    text        : 'Uncheck others (and check this)',
                
                    handler     : this.uncheckOthersHandler
                },
                {
                    text        : 'Uncheck all',
                
                    handler     : this.uncheckAllHandler
                },
                {
                    text        : 'Check all',
                
                    handler     : this.checkAllHandler
                },
                {
                    text        : 'Run this',
                
                    handler     : this.runThisFileHandler
                }
            ]
        })
    },


    uncheckOthersHandler : function () {
        var currentFile     = this.currentFile
    
        this.uncheckAllExcept(currentFile)
    
        this.setNodeChecked(currentFile, true)
    },


    runThisFileHandler : function () {
        this.harness.launch([ this.currentFile.get('descriptor') ])
    },


    uncheckAllHandler : function () {
        this.uncheckAllExcept()
    },


    checkAllHandler : function () {
        var me      = this
    
        Ext.each(this.testsStore.tree.flatten(), function (node) {
        
            me.setNodeChecked(node, true, true)
        })
    },


    onFilesContextMenu : function (view, testFile, el, index, event) {
        this.currentFile    = testFile
    
        this.contextMenu.setPagePosition(event.getX(), event.getY())
    
        this.contextMenu.show()
    
        event.preventDefault()
    },


    onTestFileDoubleClick : function (view, testFile) {
        // don't launch groups when filtered - will be confusing for user
        if (this.getNodeStore().isFiltered() && !testFile.isLeaf()) return 
        
        this.harness.launch([ testFile.get('descriptor') ])
    },


    updateStatusIndicator : function () {
        // can remain neutral if all files are missing for example
        var isNeutral       = true
        var allGreen        = true
        var hasFailures     = false
    
        var totalPassed     = 0
        var totalFailed     = 0
    
        Ext.each(this.testsStore.tree.flatten(), function (testFileRecord) {
            var test        = testFileRecord.get('test')
        
            // if there's at least one test - state is not neutral
            if (test && test.isFinished()) {
                isNeutral = false
            
                allGreen        = allGreen      && test.isPassed()
                hasFailures     = hasFailures   || test.isFailed()
            
                totalPassed     += test.getPassCount()
                totalFailed     += test.getFailCount()
            }
        })
    
//        var statusIndicatorEl   = this.statusIndicatorEl
    
//        statusIndicatorEl.removeCls([ 'tr-status-running', 'tr-status-allgreen', 'tr-status-bugs' ])
    
        var filesTree       = this.slots.filesTree
    
        filesTree.setTitle('Totals: ' + totalPassed + ' / ' + totalFailed)
    
//        if (isNeutral) return
//        
//        if (allGreen)       statusIndicatorEl.addCls('tr-status-allgreen')
//        if (hasFailures)    statusIndicatorEl.addCls('tr-status-bugs')
    
        if (isNeutral)      filesTree.setIconCls('tr-status-neutral-small')
        if (allGreen)       filesTree.setIconCls('tr-status-allgreen-small')
        if (hasFailures)    filesTree.setIconCls('tr-status-bugs-small')
    
    },

    onSettingsMenuBeforeShow : function(hdr, menu) {
        menu.down('[option=viewDOM]').setChecked(this.getOption('viewDOM'));
    },

    onHeaderButtonClick : function(hdr, button, action) {
        switch(action) {
            case 'run-checked':
                this.runChecked();
            break;
            case 'run-failed':
                this.runFailed();
            break;
            case 'run-all':
                this.runAll();
            break;
            case 'stop':
                this.stopSuite(button);
            break;
        }
    },
    
    
    onLogoClick : function () {
        this.slots.mainArea.getLayout().setActiveItem(this.slots.splashpanel)
    }
})
//eof Siesta.Harness.Browser.UI.Viewport
;
Ext.define('Siesta.Harness.Browser.UI.ExtViewport', {

    extend          : 'Siesta.Harness.Browser.UI.Viewport',
    
    headerClass     : 'Siesta.Harness.Browser.UI.ExtHeader',
    
    
    setOption : function (name, value) {
        switch (name) {
            case 'extVersion'   : return this.harness.setExtVersion(value)
            
            default             : return this.callParent(arguments)
        }
    }
//    ,
//    
//    
//    getState : function () {
//        var state       = this.callParent()
//        
//        var extVersion  = this.getOption('extVersion')
//        
//        if (extVersion && this.getOption('allowExtVersionChange')) state.extVersion = extVersion
//        
//        return state
//    } 
})
//eof Siesta.Harness.Browser.UI.ExtViewport
;
Ext.define('Siesta.Harness.Browser.UI.MouseVisualizer', {

    displaySimulationTip : false,
    displayClickIndicator : true,

    ghostCursor             : null,
    viewport                : null,


    constructor : function(harness) {
        this.harness = harness;
        this.callParent([]);
    },

    init : function(viewport) { 
        this.viewport = viewport;

        if (!this.ghostCursor) {
            this.ghostCursor = Ext.getBody().createChild({
                tag : 'div',
                cls : 'ghost-cursor'
            });
        }
  
        this.harness.on('eventsimulated', this.onEventSimulated, this);
        this.harness.on('teststart', this.onTestStart, this);
        this.harness.on('testfinalize', this.onTestFinished, this);
    },

    onTestFinished : function(meta, test) {
        var me = this;
        clearTimeout(me.hideTimer);

        me.hideTimer = setTimeout(function() {
            me.ghostCursor.fadeOut({ duration : 1500 });
            if (me.viewport.isTestRunningVisible(test)) {
                Ext.select('.ghost-cursor-click-indicator').destroy();
            }
        }, 2000);
    },

    onTestStart : function(meta, test) {
        if (this.viewport.isTestRunningVisible(test)) {
            // Cancel any ongoing fadeOut operation
            this.ghostCursor.stopAnimation();
            this.ghostCursor.setOpacity(1);
        }
    },

    onEventSimulated : function(meta, test, el, type, evt) {
        // Make sure this test is visible in DOM right now
        if (test.scopeProvider.iframe && type.match('touch|mouse|click|contextmenu') && this.viewport.isTestRunningVisible(test) &&
            Ext.isNumber(evt.clientX) && Ext.isNumber(evt.clientY)) {
            var bd = Ext.getBody(),
                frameOffsets = Ext.fly(test.scopeProvider.iframe).getOffsetsTo(bd),
                x = evt.clientX + frameOffsets[0],
                y = evt.clientY + frameOffsets[1];
    
            this.updateGhostCursor(type, x, y);
             
            if (Ext.supports.Transitions) {
                if (this.displaySimulationTip &&
                   (type === 'mousedown' ||
                    type === 'mouseup' ||
                    type === 'click')) {
                    this.showSimulationTip(type, x, y);
                }

                 if (this.displayClickIndicator && (
                    type === 'click' || 
                    type === 'dblclick' || 
                    type === 'touchstart' || 
                    type === 'touchend' || 
                    type === 'mousedown' || 
                    type === 'mouseup' || 
                    type === 'contextmenu')) {
                    this.showClickIndicator(type, x, y);
                }
            }
        }
    },

    /*
    * This method shows a fading circle at the position of click/dblclick/mousedown/contextmenu
    * @param {String} type The name of the event
    * @param {Number} x The x coordinate of the event
    * @param {Number} y The y coordinate of the event
    */
    showClickIndicator : function(type, x, y) {
        var clickCircle = Ext.getBody().createChild({
            tag : 'div',
            cls : 'ghost-cursor-click-indicator',
            style : 'left:' + x + 'px;top:' + y + 'px'
        });
        Ext.Function.defer(clickCircle.addCls, 50, clickCircle, ['ghost-cursor-click-indicator-big']);
    },

    /*
    * This method shows a fading tooltip with the name of the event
    * @param {String} type The name of the event
    * @param {Number} x The x coordinate of the event
    * @param {Number} y The y coordinate of the event
    */
    showSimulationTip: function (type, x, y) {
        var note = Ext.getBody().createChild({
            tag : 'span',
            cls : 'ghost-cursor-message',
            style : 'left:' + x + 'px;top:' + y + 'px',
            html : type + ' at [' + x + ', ' + y + ']'
        });

        note.setStyle({ 
            opacity: 0.5,
            top : (y - 40) + 'px'
        });
        Ext.Function.defer(note.destroy, 2000, note);
    },

    /*
    * This method updates the ghost cursor position and appearance
    * @param {String} type The name of the event
    * @param {Number} x The x coordinate of the event
    * @param {Number} y The y coordinate of the event
    */
    updateGhostCursor: function (type, x, y) {
        
        this.ghostCursor.setXY([x-5, y], false);        // -5 to get index finger aligned correctly
        if (this.hideTimer) {
            clearTimeout(this.hideTimer);
            this.hideTimer = null;
        }
   
        switch(type) {
            case 'touchstart':
            case 'mousedown':
                this.ghostCursor.addCls('ghost-cursor-press');
            break;

            case 'dblclick':
                this.ghostCursor.addCls('ghost-cursor-press');
                Ext.Function.defer(this.ghostCursor.removeCls, 40, this.ghostCursor, ['ghost-cursor-press']);
            break;

            case 'touchend':
            case 'mouseup':
            case 'click':
                this.ghostCursor.removeCls('ghost-cursor-press');
            break;
        
            case 'contextmenu' :
            break;
        }
    }
});
;
Ext.define('Siesta.Harness.Browser.UI.TestGrid', {

    alias       : 'widget.testgrid',

    extend      : 'Ext.tree.Panel',
    stateful    : true,
    id          : 'testTree',

    initComponent : function () {
    
        Ext.apply(this, {
        
            width       : 400,
        
            cls         : 'tr-testgrid',
            
//            border      : false,
            
            forceFit    : true,
            rootVisible : false,
        
            title       : 'Double click a test to run it',
        
            columns     : [
                { 
                    xtype       : 'treecolumn',
                    header      : 'Name',
                    sortable    : false,
                
                    dataIndex   : 'title',
                
                    width       : 180, 
                    renderer    : this.treeColumnRenderer,
                    scope       : this
                },
                { header: 'Passed', width: 40, sortable: false, dataIndex: 'passCount', align : 'center', renderer : this.passedColumnRenderer, scope : this },
                { header: 'Failed', width: 40, sortable: false, dataIndex: 'failCount', align : 'center', renderer : this.failedColumnRenderer, scope : this },
                { header: 'Time', width: 50, sortable: false, dataIndex: 'time', align : 'center' }
            ],
            tools : [
                {
                    type : 'down',
                    tooltip        : 'Expand all',
                    tooltipType : 'title',
                    scope : this,                                    
                    handler     : this.onExpandAll
                },
                {
                    type : 'up',
                    tooltipType : 'title',
                    tooltip : 'Collapse all',
                    scope : this,                                    
                    handler     : this.onCollapseAll
                }
            ],
            
            dockedItems : [
                {
                    xtype       : 'triggerfield',
                    emptyText   : 'Filter tests',
                    
                    itemId      : 'trigger',
                    
                    style       : 'margin-top : 3px; margin-left : 1px; margin-right : 1px',
                    triggerCls  : 'x-form-clear-trigger',
                    
                    dock        : 'top',
                    
                    onTriggerClick  : Ext.Function.bind(this.onClearFilter, this),
                    
                    listeners   : {
                        change  : this.onFilterChange,
                        scope   : this
                    }
                }
            ]
        })
    
        this.callParent()
    },
    
    
    onClearFilter : function () {
        var treeView    = this.getView()
        
        // TODO 4.0.2 quirk probably not needed in 4.1
        treeView.blockRefresh = false
        
        this.getDockedComponent('trigger').setValue('')
        treeView.store.clearFilter()
        
        // TODO 4.0.2 quirk probably not needed in 4.1
        treeView.blockRefresh = true
    },
    
    
    onFilterChange : function (field, newValue, oldValue) {
        var treeView    = this.getView()
        var nodeStore   = treeView.store
        
        // TODO 4.0.2 quirk probably not needed in 4.1
        treeView.blockRefresh = false
        
        if (newValue) {
            var regexps         = Ext.Array.map(newValue.split(/\s+/), function (token) { return new RegExp(Ext.String.escapeRegex(token), 'i') })
            var length          = regexps.length
            
            var filteredById    = {}
            
            Ext.Array.each(this.store.tree.flatten(), function (testFile) {
                var title       = testFile.get('title')
                
                // blazing fast "for" loop! :)
                for (var i = 0; i < length; i++)
                    if (!regexps[ i ].test(title)) return
                    
                filteredById[ testFile.getId() ] = true

                // also include parent nodes for leafs for better user experience
                if (testFile.isLeaf()) {
                    var parent  = testFile.parentNode
                    
                    while (parent) {
                        filteredById[ parent.getId() ] = true
                        
                        parent = parent.parentNode
                    }
                }
            })
            
            nodeStore.filterBy(function (testFile) {
                return filteredById[ testFile.getId() ]
            })
            
        } else
            nodeStore.clearFilter()
            
        // TODO 4.0.2 quirk probably not needed in 4.1
        treeView.blockRefresh = true
    },
    

    onExpandAll : function () {
        this.expandAll()
    }, 


    onCollapseAll : function () {
        this.collapseAll()
    },

    treeColumnRenderer : function (value, metaData, testFile, rowIndex, colIndex, store) {
        metaData.tdCls = 'tr-test-status '
    
        if (testFile.isLeaf()) {
    
            var test = testFile.get('test')
        
            if (test) {
            
                if (testFile.get('isFailed'))
                    metaData.tdCls += 'tr-test-status-thrown'
                
                else if (testFile.get('isRunning') && !test.isFinished())
                    metaData.tdCls += 'tr-test-status-running'
                else
                    if (test.isFinished()) {
                    
                        if (test.isPassed())
                            metaData.tdCls += 'tr-test-status-passed'
                        else 
                            metaData.tdCls += 'tr-test-status-failed'
                    } else
                        metaData.tdCls += 'tr-test-status-working'
                
            } else {
            
                if (testFile.get('isMissing'))
                    metaData.tdCls += 'tr-test-status-missing'
                else
                    if (testFile.get('isStarting'))
                        metaData.tdCls += 'tr-test-status-working'
                    else
                        metaData.tdCls += 'tr-test-status-empty'
            }
        
            return value.replace(/(?:.*\/)?([^/]+)$/, '$1')
            
        } else {
            metaData.tdCls += 'tr-folder-' + testFile.get('folderStatus')
        
            return value
        }
    },

    passedColumnRenderer : function(v, m, r) {
        if (r.data.todoPassCount > 0) {
            v += ' <span title="' + r.data.todoPassCount + ' todo assertion(s) passed" class="tr-test-todo tr-test-todo-pass">+ ' + r.data.todoPassCount + '</span>';
        }
        return v;
    },

    failedColumnRenderer : function(v, m, r) {
        if (r.data.todoFailCount > 0) {
            v += ' <span title="' + r.data.todoFailCount + ' todo assertion(s) failed" class="tr-test-todo tr-test-todo-fail">+ ' + r.data.todoFailCount + '</span>';
        }
        return v;
    }
})
;
Ext.define('Siesta.Harness.Browser.UI.AssertionGrid', {

    extend          : 'Ext.Panel',

    alias           : 'widget.assertiongrid',


    slots           : true,


    testRecord      : null,

    halfWidth       : null,

    isStale         : false,

    maintainMinViewportSize : false,
    minViewportSize         : null,
    
    viewDOM                 : false,
    canManageDOM            : true,

    verticalCenteredTpl     : new Ext.XTemplate(
        '<div class="tr-vertical-align-helper-content {cls}">{text}</div>',
        '<div class="tr-vertical-align-helper"></div>',
        { 
            compiled : true 
        }
    ),


    initComponent : function() {
        this.addEvents('viewdomchange');

        Ext.apply(this, {
            tbar : [{
                text            : 'View source', 
                iconCls         : 'view-source',
                enableToggle    : true,
            
                pressed         : false,
            
                scope           : this,
                handler         : this.toggleSources
            },
            {
                text            : 'Toggle DOM visible', 
                iconCls         : 'show-dom',
            
                scope           : this,
                handler         : function(btn) {
                    this.setViewDOM(!this.viewDOM);
                }
            },
            {
                text            : 'Re-run test', 
                iconCls         : 'rerun',
            
                scope           : this,
                handler         : this.onRerun
            }],

            layout      : 'border',
        
            cls         : 'tr-container',
            border      : false,
        
            items       : [
                // a card container
                {
                    region      : 'center',
                    slot        : 'cardContainer',
                
                    border      : false,
                
                    layout      : 'card', 
                    activeItem  : 0,
                
                    items : [
                        // grid with assertion
                        {
                            cls : 'hide-simulated',
                            xtype       : 'grid',
                            slot        : 'grid',
                    
                            border      : false,
                    
                            store       : this.testRecord.get('assertionsStore'),
                    
                            columns     : [
                                {
                                    tdCls       : 'tr-td-vertical-align',
                                    header      : '#',
                                    width       : 30,
                                    dataIndex   : 'index',
                                    align       : 'center',
                                    renderer : function(value, metaData, record) {
                                        if (record.get('isSimulatedEvent')) {
                                            metaData.tdCls  =  'tr-diag-headercell';
                                            return Ext.String.format('<div class="simulated simulated-{0}">{1}</div>', record.get('eventType'), record.get('description'));
                                        }
                                        return value;
                                    }
                                },
                                {
                                    header      : 'Result',
                                    width       : 60,
                                    dataIndex   : 'passed',
                                    align       : 'center',
                                    fixed       : true,
                                    renderer    : this.resultRenderer
                                },
                                {
                                    header      : 'Assertion',
                                    flex        : 1,
                                    dataIndex   : 'description',
                            
                                    renderer    : this.descriptionRenderer
                                }
                            ],
                    
                            viewConfig      : {
                                forceFit        : true,
                                stripeRows : false,
                                getRowClass: function(record, rowIndex, rowParams, store){
                                    if (record.get('isSimulatedEvent')) {
                                        return 'tr-simulation-row';
                                    }

                                    return record.get("type") == 'Siesta.Result.Diagnostic' ? 
                                        'tr-diagnostic-row' : 
                                        !record.get('passed') && !record.get('isTodo') ? 'tr-row-failed-assertion' : '' 
                                }
                            }                    
                    
                        },
                        // eof grid with assertion
                        {
                            xtype       : 'container',
                            slot        : 'sources',
                            border      : false,
                            autoScroll  : true,
                            cls         : 'test-source-ct'
                        }
                    ]
                },
                {
                    xtype           : 'panel',
                    region          : 'east',
                    slot            : 'domContainer',
                
                    width           : this.halfWidth,
                
                    split           : true,
                    header          : false,
                
                    collapsible     : true,
                    animCollapse    : false,
                    animFloat       : false,
                
                    collapsed       : !this.viewDOM,
                
                    bodyStyle       : 'text-align : center',
                    html            : this.verticalCenteredTpl.apply({ 
                        cls     : 'tr-rounded-box', 
                        text    : '"Keep results" option is not enabled' 
                    })
                }
            ]
        })
    
        this.callParent()
    
        this.slots.domContainer.on({
            expand      : this.onDomContainerExpand,
            collapse    : this.onDomContainerCollapse,
        
            scope       : this
        })
    
        this.on({
            afterlayout : this.afterDOMContainerLayout,
            hide        : this.hideIFrame,
            show        : this.alignIFrame,
        
            scope       : this
        })
    },


    toggleSources : function(btn) {  
        var slots           = this.slots
        var cardContainer   = slots.cardContainer
        var sourcesCt       = slots.sources
    
        cardContainer.layout.setActiveItem(btn.pressed ? sourcesCt : slots.grid);
    
        if (btn.pressed && !sourcesCt.__filled__) {
            sourcesCt.__filled__ = true
        
            sourcesCt.update(Ext.String.format('<pre class="brush: javascript">{0}</pre>', this.testRecord.get('test').getSource()));
        
            if (SyntaxHighlighter && SyntaxHighlighter.highlight) {
                SyntaxHighlighter.highlight(sourcesCt.el);
            }
        } 
    },

    setViewDOM : function (value, suppressEvent) {
        var domContainer    = this.slots.domContainer
    
        if (value)
            domContainer.expand(Ext.Component.DIRECTION_RIGHT, false)
        else
            domContainer.collapse(Ext.Component.DIRECTION_RIGHT, false)
    },


    resultRenderer : function (value, metaData, record, rowIndex, colIndex, store) {
    
        if (record.get('isTodo')) {
            metaData.tdCls = value ? 'tr-assert-row-ok-todo-cell' : 'tr-assert-row-bug-todo-cell';
        } else {
            metaData.tdCls = value ? 'tr-assert-row-ok-cell' : 'tr-assert-row-bug-cell';
        }

        return ''
    },


    descriptionRenderer : function (value, metaData, record, rowIndex, colIndex, store) {
    
        if (record.get('isSimulatedEvent')) {
            return '';
        } else if (record.get('type') == 'Siesta.Result.Diagnostic') {
            metaData.tdCls  =  'tr-diag-headercell';
            return '<h2>' + record.get('description') + '</h2>';
        }
    
        var annotation      = record.get('annotation')
    
        if (annotation) value += '<pre class="tr-assert-row-annontation">' + Ext.String.htmlEncode(annotation) + '</pre>'
    
        return value
    },


    setCanManageDOM : function (value) {
        if (value != this.canManageDOM) {
            this.canManageDOM = value
        
            if (value && !this.hidden) this.alignIFrame()
        }
    },


    getIFrame : function () {
        var test = this.testRecord.get('test')
    
        return this.canManageDOM && test.scopeProvider && test.scopeProvider.iframe
    },


    afterDOMContainerLayout : function () {
        if (!this.slots.domContainer.collapsed) this.alignIFrame() 
    },


    alignIFrame : function () {
        var domContainer    = this.slots.domContainer
        var iframe          = this.getIFrame()
    
        if (this.hidden || domContainer.collapsed || !iframe) return
    
        Ext.fly(iframe).setXY(domContainer.el.getXY())
        
        var containerSize       = domContainer.el.getSize()
        
        if (this.maintainMinViewportSize) {
            var minViewportSize     = this.minViewportSize
            
            containerSize.width     = Math.max(containerSize.width, minViewportSize && minViewportSize.width || 1024)
            containerSize.height    = Math.max(containerSize.height, minViewportSize && minViewportSize.height || 768)
        }
        
        Ext.fly(iframe).setSize(containerSize)
    },

    onDomContainerCollapse : function() {
        this.hideIFrame();
        this.viewDOM = false;
        this.fireEvent('viewdomchange', this, false);
    },

    onDomContainerExpand : function() {
        this.alignIFrame();
        this.viewDOM = true;
        this.fireEvent('viewdomchange', this, true);
    },

    hideIFrame : function () {
        var iframe          = this.getIFrame()
    
        iframe && Ext.fly(iframe).setLeftTop(-10000, -10000)
    },


    isFrameVisible : function () {
        return !(this.hidden || this.slots.domContainer.collapsed)
    },

    onRerun : function() {
        this.fireEvent('rerun', this);
    }
})
;
};
/**
@class Siesta.Harness.Browser.ExtJS
@extends Siesta.Harness.Browser 

Class, representing the browser harness. This class provides a web-based UI and defines some additional configuration options.

The default value of the `testClass` configuration option in this class is {@link Siesta.Test.ExtJS}, which inherits from 
{@link Siesta.Test.Browser} and contains various ExtJS-specific assertions. So, use this harness class, when testing an ExtJS application.

This file is for reference only, for a getting start guide and manual, please refer to <a href="#!/guide/siesta_getting_started">Getting Started Guide</a>.

Synopsys
========

    var Harness = Siesta.Harness.Browser.ExtJS;
    
    Harness.configure({
        title     : 'Awesome ExtJS Application Test Suite',
        
        transparentEx       : true,
        
        autoCheckGlobals    : true,
        expectedGlobals     : [
            'Ext',
            'Sch'
        ],
        
        preload : [
            "http://cdn.sencha.io/ext-4.0.2a/ext-all-debug.js",
            "../awesome-project-all.js",
            {
                text    : "console.log('preload completed')"
            }
        ]
    })
    
    
    Harness.start(
        // simple string - url relative to harness file
        'sanity.t.js',
        
        // test file descriptor with own configuration options
        {
            url     : 'basic.t.js',
            
            // replace `preload` option of harness
            preload : [
                "http://cdn.sencha.io/ext-4.0.6/ext-all-debug.js",
                "../awesome-project-all.js"
            ]
        },
        
        // groups ("folders") of test files (possibly with own options)
        {
            group       : 'Sanity',
            
            autoCheckGlobals    : false,
            
            items       : [
                'data/crud.t.js',
                ...
            ]
        },
        ...
    )


*/

Class('Siesta.Harness.Browser.ExtJS', {
    
    isa     : Siesta.Harness.Browser,
    
    // pure static class, no need to instantiate it
    my : {
        
        has     : {
            /**
             * @cfg {Class} testClass The test class which will be used for creating test instances, defaults to {@link Siesta.Test.ExtJS}.
             * You can subclass {@link Siesta.Test.ExtJS} and provide a new class. 
             * 
             * This option can be also specified in the test file descriptor. 
             */
            testClass           : Siesta.Test.ExtJS,
            
            /**
             * @cfg {Class} viewportClass 
             * 
             */
            viewportClass       : "Siesta.Harness.Browser.UI.ExtViewport",
            
            /**
             * @cfg {Boolean} waitForExtReady
             * 
             * By default the `StartTest` function will be executed after `Ext.onReady`. Set to `false` to launch `StartTest` immediately.  
             * 
             * This option can be also specified in the test file descriptor. 
             */
            waitForExtReady     : true,
            
            /**
             * @cfg {Boolean} waitForAppReady
             * 
             * Setting this configuration option to "true" will cause Siesta to wait until the ExtJS MVC application on the test page will become ready,
             * before starting the test. More precisely it will wait till the first "launch" event from any instance of `Ext.app.Application` class on the page.
             *   
             * This option can (and probably should) be also specified in the test file descriptor. 
             */
            waitForAppReady     : false,
            

            /**
             * @cfg {Object} loaderPath
             * 
             * The path used to configure the Ext.Loader with, for dynamic loading of Ext JS classes.
             *
             * This option can be also specified in the test file descriptor. 
             */
            loaderPath          : null,
            
            extVersion              : null,

            /**
             * @cfg {Boolean} allowExtVersionChange
             * 
             * True to show a version picker to swiftly change which Ext JS version is used in the test suite.
             */
            allowExtVersionChange   : false,
            
            extVersionRegExp    : /ext(?:js)?-(\d\.\d+\.\d+.*?)\//
        },
        
        
        methods : {
            
            setup : function (callback) {
                if (this.allowExtVersionChange) this.extVersion     = this.findExtVersion()
                
                this.SUPER(callback)
            },
            
        
            getNewTestConfiguration : function (desc, scopeProvider, contentManager, options, runFunc) {
                var config          = this.SUPERARG(arguments)
                
                config.waitForExtReady  = this.getDescriptorConfig(desc, 'waitForExtReady')
                config.waitForAppReady  = this.getDescriptorConfig(desc, 'waitForAppReady')
                config.loaderPath       = this.getDescriptorConfig(desc, 'loaderPath')
                
                return config
            },
            
            
            setExtVersion : function (newVersion) {
                if (!this.allowExtVersionChange || newVersion == this.extVersion) return
                
                this.extVersion         = newVersion
                
                var me                  = this
                var allDescriptors      = this.flattenDescriptors(this.descriptors)
                var mainPreset          = this.mainPreset
                
                this.setExtVersionForPreset(mainPreset, newVersion)
                
                Joose.A.each(allDescriptors, function (desc) {
                    if (desc.preset != mainPreset) me.setExtVersionForPreset(desc.preset, newVersion)
                })
            },
            
            
            setExtVersionForPreset : function (preset, newVersion) {
                var me      = this
                
                preset.eachResource(function (resource) {
                    var url     = resource.url
                    
                    if (url && url.match(me.extVersionRegExp)) resource.url = url.replace(me.extVersionRegExp, 'ext-' + newVersion + '/')
                })
            },
            
            
            findExtVersion : function () {
                var me      = this
                
                var found
                
                this.mainPreset.eachResource(function (resource) {
                    var match   = me.extVersionRegExp.exec(resource.url)
                    
                    if (match) {
                        found   = match[ 1 ]
                        
                        return false
                    }
                })
                
                return found
            }
        }
    }
})


;
;
Class('Siesta', {
    /*PKGVERSION*/VERSION : '1.0.8',

    // "my" should been named "static"
    my : {
        
        has : {
            config      : null
        },
        
        methods : {
        
            getConfigForTestScript : function (text) {
                try {
                    eval(text)
                    
                    return this.config
                } catch (e) {
                    return null
                }
            },
            
            
            StartTest : function (arg1, arg2) {
                if (typeof arg1 == 'object') 
                    this.config = arg1
                else if (typeof arg2 == 'object')
                    this.config = arg2
                else
                    this.config = null
            }
        }
    }
})

// fake StartTest function to extract test configs
if (typeof StartTest == 'undefined') StartTest = Siesta.StartTest;
