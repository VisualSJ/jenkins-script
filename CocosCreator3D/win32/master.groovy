properties([parameters([
  string(name: 'EDITOR_BUILD_BRANCH', defaultValue: 'v1.0.0', description: '构建的分支(对应 GitHub 上的 branch )'),
  booleanParam(name: 'EDITOR_CLEAR', defaultValue: true, description: '是否清空仓库'),
  booleanParam(name: 'EDITOR_UPDATE_ENGINE', defaultValue: true, description: '是否强制更新 engine 仓库'),
  booleanParam(name: 'EDITOR_CODESIGN', defaultValue: true, description: '是否需要签名'),
  booleanParam(name: 'EDITOR_UPLOAD_FTP', defaultValue: true, description: '是否上传 ftp'),
  booleanParam(name: 'EDITOR_DAILY', defaultValue: true, description: '是否放到 daily 文件夹下'),
])])

node('windows') {
    stage ('checkout code'){
        git branch: "${EDITOR_BUILD_BRANCH}", url: 'git@github.com:cocos-creator/editor-3d.git'
    }

    stage ('num run clear') {
        if (Boolean.parseBoolean(env.EDITOR_CLEAR)) {
            bat 'npm run clear'
        } else {
            echo 'skip num run clear stage'
        }
    }

    stage ('init editor') {
        bat 'npm install'
    }

    stage ('update engine') {
        if (Boolean.parseBoolean(env.EDITOR_UPDATE_ENGINE)) {
            bat 'npm run checkout'
        } else {
            echo 'skip update engine'
        }
    }

    stage ('generate') {
        bat 'npm run generate'
    }

    stage ('codesign') {
        if (Boolean.parseBoolean(env.EDITOR_CODESIGN)) {
            bat 'npm run pack -- -without package,ftp'
        } else {
            echo 'skip codesign'
        }
    }

    stage ('package') {
        bat 'npm run pack -- --without codesign,ftp'
    }

    stage ('upload ftp') {
        if (Boolean.parseBoolean(env.EDITOR_UPLOAD_FTP)) {

            if (Boolean.parseBoolean(env.EDITOR_DAILY)) {
                bat 'npm run pack -- --without codesign,package --daily'
            } else {
                bat 'npm run pack -- --without codesign,package'
            }
        } else {
            echo 'skip upload ftp'
        }
    }
}