var path = require('path');

module.exports = {
    watch: false,
    entry: './src/main/js/index.js',
    devtool: 'sourcemaps',
    cache: true,
    mode: 'development',
    output: {
        path: path.join(__dirname, "./src/main/resources/static"),
        filename: 'bundle-dashboard.js',
        publicPath: 'http://localhost:3000/'
    },
    devServer: {
        contentBase: path.join(__dirname, './src/main/resources/static'),
        compress: true,
        port: 3000
    },
    module: {
        rules: [
            {
                test: path.join(__dirname, '.'),
                exclude: /(node_modules)/,
                use: [{
                    loader: 'babel-loader',
                    options: {
                        presets: ["@babel/preset-env", "@babel/preset-react"],
                        plugins: ["@babel/plugin-proposal-class-properties", "@babel/plugin-transform-runtime"]
                    }
                }]
            },
            {
                test: /\.css$/,
                use: ['style-loader', 'css-loader']
            }
        ]
    }
};