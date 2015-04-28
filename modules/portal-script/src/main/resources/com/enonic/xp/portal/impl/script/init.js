(function (key, log, execute, require, resolve) {
    var exports = {};
    var module = {};
    module.id = key.toString();
    module.name = key.module.toString();

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
