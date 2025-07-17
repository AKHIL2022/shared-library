def call(String dir ='.') {
    dir(dir) {
    sh 'npm run build'
   }
}
