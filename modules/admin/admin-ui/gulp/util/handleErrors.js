import notify from "gulp-notify";

export default (errorObject) => {
    const context = this || global;
    notify.onError(errorObject.toString().replace(/:\s{1}/g, ':\n')).apply(context, arguments);
    // Keep gulp from hanging on this task
    if (context && typeof context.emit === 'function') {
        global.emit('end');
    }
};
