(function (log, require, resolve, __) {
    var exports = {};
    var module = {};
    module.id = __.script.toString();

    Object.defineProperty(module, 'exports', {
        get() {
            return exports;
        },
        set(value) {
            exports = value;
        }
    });

    __script__;

    return module.exports;
});
