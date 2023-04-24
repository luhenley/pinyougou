//定义购物车的控制器
app.controller('baseController',function ($scope, $http) {

    //获取登录用户名
    $scope.loadUsername = function () {
        // 定义重定向的URL
        $scope.redirectUrl = window.encodeURIComponent(location.href);

        //发送异步请求
        $http.get("/user/showName").then(function (response) {
            //获取响应数据
            $scope.loginName = response.data.loginName;
        });
    };
});