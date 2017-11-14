var path = require('path');
var webpack=require('webpack');
const ExtractTextPlugin = require("extract-text-webpack-plugin");
var CopyWebpackPlugin = require('copy-webpack-plugin');

function resolve (dir) {
  return path.join(__dirname, '..', dir)
}

module.exports = {
  context: __dirname,
  entry: {
   app: "./src/App.js",
 },
 externals: {
  'AMap': 'window.AMap'
},
output: {
  path: __dirname + '/dist/assets',
  filename: '[name].bundle.js',
  publicPath: "/assets"
},
devServer: {
 contentBase: __dirname + '/src',
 port: 8085,
 host: 'localhost',
 inline: true
},
resolve: {
  extensions: ['.js', '.vue', '.json'],
  alias: {
    'vue$': 'vue/dist/vue.js',
    '@': resolve('src'),
  }
},
module: {
  rules: [
  {
    test: /\.js$/,
    exclude: /node_modules/,
    use: [{
      loader: "babel-loader",
      options: { presets: ["es2015"] }
    }],
  },
  {
    test: /\.jsx?$/,
    exclude: /node_modules/,
    use: [
    {
      loader: "babel-loader"
    }
    ]
  },
  {
   test: /\.vue$/,
   loader: 'vue-loader'
 },
 {
  test: /\.css$/,
  use: [
  "css-loader", "style-loader"
  ],
}
]
},
plugins: [
new ExtractTextPlugin({
  filename: "[name].bundle.css",
  allChunks: true,
}),
new CopyWebpackPlugin([
 { from: __dirname + '/node_modules/vue/dist/vue.js', to: __dirname + '/src/vendor/vue.js' },
 { from: __dirname + '/node_modules/vue-router/dist/vue-router.js', to: __dirname + '/src/vendor/vue-router.js' }
 ], {
  ignore: [
                // Doesn't copy any files with a txt extension    
                '*.txt',
                
                // Doesn't copy any file, even if they start with a dot
                '**/*',

                // Doesn't copy any file, except if they start with a dot
                { glob: '**/*', dot: false }
                ],

            // By default, we only copy modified files during
            // a watch or webpack-dev-server build. Setting this
            // to `true` copies all files.
            copyUnmodified: true
          }),
],
}