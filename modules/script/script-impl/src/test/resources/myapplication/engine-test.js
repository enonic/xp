exports.engine = function () {
    // the GraalJS builtin global is the only engine marker available to a script
    return typeof Graal !== 'undefined' ? 'graal' : 'nashorn';
};
