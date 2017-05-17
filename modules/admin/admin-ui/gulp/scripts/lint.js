const notifier = require('node-notifier');
const git = require('../util/git');
const lint = require('../util/linter').lint;

function log(fails) {
    fails.forEach(fail => fail.log());

    if (fails.length > 0) {
        const title = `Linter found ${fails.length} error.`;
        const message = 'Run "npm run lint:quick" to check again.';

        notifier.notify({title, message});
        console.log('\n' + title);
        console.log(message);

        process.exit(1);
    }
}

// `*.ts` files, except `*.d.ts`
const ext = ['.ts', /^(?!.*\.d\.ts$).*$/];

const diff = () => git.mergeBase().then((hash) => git.diff(hash, ...ext)).then(lint).then(log);
const status = () => git.status(...ext).then(lint).then(log);

module.exports = {
    status,
    diff,
};
