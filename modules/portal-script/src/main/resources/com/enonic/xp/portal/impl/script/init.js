(function ( log, execute, require, resolve, __) {
    var exports = {};
    var module = {};
    module.id = __.script.toString();
    module.name = __.module.toString();

    Object.defineProperty(module, 'exports', {
        get: function () {
            return exports;
        },
        set: function (value) {
            exports = value;
        }
    });

    __script__;

    return module.exports;
});
