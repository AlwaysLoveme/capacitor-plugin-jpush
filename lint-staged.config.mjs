const formatStageFiles = (stageFiles) => {
  return stageFiles.join(' ');
};

export default {
  // eslint-disable-next-line @typescript-eslint/explicit-module-boundary-types
  '*.{js,jsx,tsx,ts,less,md,json}': (stageFiles) => {
    // 过滤掉 public 目录下的文件
    const filteredFiles = stageFiles.filter(
      (file) => !file.includes('/public/') && !file.includes('/dist/') && !file.includes('/libs/'),
    );
    if (filteredFiles.length === 0) return [];
    return [`prettier --write ${formatStageFiles(filteredFiles)}`, `git add ${formatStageFiles(stageFiles)}`];
  },
};
