Ext.define('Admin.lib.css.Transition', {

    singleton: true,

    transit: function (target, options) {
        var domEl = target instanceof Ext.Element ? target.dom : target;
        var me = this,
            fromProperties = options.from,
            toProperties = options.to,
            duration = options.duration,
            easing = options.easing || 'linear',
            delay = options.delay || '0ms',
            callback = options.callback;

        var f,
            properties = [], durtations = [], timingFunctions = [], delays = [],
            fromProp, toProp;

        for (f in fromProperties) {
            if (fromProperties.hasOwnProperty(f)) {
                fromProp = me._getPrefixedStyleProperty(f);
                domEl.style[fromProp] = fromProperties[f];
                properties.push(fromProp);
                durtations.push(duration + 'ms');
                timingFunctions.push(me._ease[easing]);
                delays.push(delay + 'ms');
            }
        }

        if (callback) {
            me._addPrefixedEventListener(domEl, 'TransitionEnd', callback);
        }

        domEl.style[me._getVendorPrefix(['webkitTransitionProperty', 'transitionProperty'])] = properties.join(',');
        domEl.style[me._getVendorPrefix(['webkitTransitionDuration', 'transitionDuration'])] = durtations.join(',');
        domEl.style[me._getVendorPrefix(['webkitTransitionTimingFunction', 'transitionTimingFunction'])] = timingFunctions.join(',');
        domEl.style[me._getVendorPrefix(['webkitTransitionDelay', 'transitionDelay'])] = delays.join(',');

        var t;
        for (t in toProperties) {
            if (toProperties.hasOwnProperty(t)) {
                toProp = me._getPrefixedStyleProperty(t);

                domEl.style[toProp] = toProperties[t];
            }
        }
    },

    _ease: {
        'ease-in':              'ease-in',
        'ease-out':             'ease-out',
        'ease-in-out':          'ease-in-out',
        'snap':                 'cubic-bezier(0,1,.5,1)',
        'linear':               'cubic-bezier(0.250, 0.250, 0.750, 0.750)',
        'ease-in-quad':         'cubic-bezier(0.550, 0.085, 0.680, 0.530)',
        'ease-in-cubic':        'cubic-bezier(0.550, 0.055, 0.675, 0.190)',
        'ease-in-quart':        'cubic-bezier(0.895, 0.030, 0.685, 0.220)',
        'ease-in-quint':        'cubic-bezier(0.755, 0.050, 0.855, 0.060)',
        'ease-in-sine':         'cubic-bezier(0.470, 0.000, 0.745, 0.715)',
        'ease-in-expo':         'cubic-bezier(0.950, 0.050, 0.795, 0.035)',
        'ease-in-circ':         'cubic-bezier(0.600, 0.040, 0.980, 0.335)',
        'ease-in-back':         'cubic-bezier(0.600, -0.280, 0.735, 0.045)',
        'ease-out-quad':        'cubic-bezier(0.250, 0.460, 0.450, 0.940)',
        'ease-out-cubic':       'cubic-bezier(0.215, 0.610, 0.355, 1.000)',
        'ease-out-quart':       'cubic-bezier(0.165, 0.840, 0.440, 1.000)',
        'ease-out-quint':       'cubic-bezier(0.230, 1.000, 0.320, 1.000)',
        'ease-out-sine':        'cubic-bezier(0.390, 0.575, 0.565, 1.000)',
        'ease-out-expo':        'cubic-bezier(0.190, 1.000, 0.220, 1.000)',
        'ease-out-circ':        'cubic-bezier(0.075, 0.820, 0.165, 1.000)',
        'ease-out-back':        'cubic-bezier(0.175, 0.885, 0.320, 1.275)',
        'ease-in-out-quart':    'cubic-bezier(0.770, 0.000, 0.175, 1.000)',
        'ease-in-out-quint':    'cubic-bezier(0.860, 0.000, 0.070, 1.000)',
        'ease-in-out-sine':     'cubic-bezier(0.445, 0.050, 0.550, 0.950)',
        'ease-in-out-expo':     'cubic-bezier(1.000, 0.000, 0.000, 1.000)',
        'ease-in-out-circ':     'cubic-bezier(0.785, 0.135, 0.150, 0.860)',
        'ease-in-out-back':     'cubic-bezier(0.680, -0.550, 0.265, 1.550)'
    },


    _getPrefixedStyleProperty: function (prop) {
        var me = this,
            p = prop;
        if (prop === 'transform') {
            p = me._getVendorPrefix(['transform', 'webkitTransform']);
        }
        return p;
    },


    _addPrefixedEventListener: function (element, type, callback) {
        var me = this,
            prefixes = ['webkit', 'moz', 'MS', 'o', ''],
            i;

        for (i = 0; i < prefixes.length; i++) {
            if (!prefixes[i]) {
                type = type.toLowerCase();
            }

            element.addEventListener(prefixes[i] + type, function(event) {
                event.stopPropagation();
                callback.call(me, event);
            }, false);
        }
    },


    _getVendorPrefix: function (arrayOfPrefixes) {
        var tmp = document.createElement("div"),
            result = '';

        for (var i = 0; i < arrayOfPrefixes.length; ++i) {

            if (typeof tmp.style[arrayOfPrefixes[i]] != 'undefined') {
                result = arrayOfPrefixes[i];
                break;
            }
            else {
                result = null;
            }

        }

        return result;
    }


});
