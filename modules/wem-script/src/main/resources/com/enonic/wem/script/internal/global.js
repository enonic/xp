var exports = {};

function require(name) {
    return __helper.require(name);
}

function resolve(name) {
    return __helper.resolve(name);
}

function execute(name, params) {
    return executeCommand(name, params);
}

function executeCommand(name, params) {
    var command = __helper.newCommand(name);

    for (key in params) {
        command[key] = params[key];
    }

    __helper.invokeCommand(command);
    return command.result;
}
