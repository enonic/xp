const glob = require('glob');
const cpFile = require('cp-file');
const path = require('path');

const SRC_RESOURCES = 'src/main/resources';

glob.sync(`lib-*/${SRC_RESOURCES}/lib/xp/*.d.ts`).forEach(async (srcResourcePath) => {
  const [libPath] = srcResourcePath.split('/');

  const indexPath = path.join(libPath, '', 'index.d.ts').replaceAll(path.sep, '/');

  await cpFile(srcResourcePath, indexPath);
});
