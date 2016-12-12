const isCI = require('is-ci');

module.exports = function ci() {
    const code = isCI ? 1 : 0;
    process.exit(code);
};
