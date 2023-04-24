/** 定义秒杀商品控制器 */
app.controller("seckillGoodsController", function($scope,$controller,$location,$timeout,baseService){

    /** 指定继承cartController */
    $controller("baseController", {$scope:$scope});

    // 查询正在秒杀的商品
    $scope.findSeckillGoodsList = function () {
        baseService.sendGet("/seckill/findSeckillGoodsList").then(function(response){
            // 获取响应数据
            $scope.seckillGoodsList = response.data;
        });
    };

    // 根据id查询秒杀商品
    $scope.findOne = function () {
        // 获取请求URL后面的参数 id
        var id = $location.search().id;
        baseService.sendGet("/seckill/findOne?id=" + id).then(function(response){
            // 获取响应数据
            $scope.entity = response.data;

            // 显示倒计时
            $scope.downCount($scope.entity.endTime);

        });
    };

    // 定义倒计时的方法
    $scope.downCount = function (endTime) {
        // 计算出结束时间与当前时间相差的秒数
        var seconds = Math.floor((endTime - new Date().getTime()) / 1000); // 向下取整

        // 通过秒数计算出 相差的天数、相差的小时数、相差的分钟、相差的秒数
        if (seconds >= 0) {
            // 计算出相差的分钟
            var minutes = Math.floor(seconds / 60);
            // 计算出相差的小时
            var hours = Math.floor(minutes / 60);
            // 计算出相差的天数
            var days = Math.floor(hours / 24);

            // 定义数组封装时间字符串
            var arr = [];

            if (days > 0) {
                arr.push(calc(days) + "天 ");
            }
            if (hours > 0) {
                arr.push(calc(hours - days * 24) + ":");
            }
            if (minutes > 0) {
                arr.push(calc(minutes - hours * 60) + ":");
            }
            // 添加秒
            arr.push(calc(seconds - minutes * 60));
            $scope.timeStr = arr.join("");

            if (seconds > 0){
                // 开启定时器
                $timeout(function () {
                    $scope.downCount(endTime)
                }, 1000);
            }
        }else{
            // 时间已到
            $scope.timeStr = "秒杀已结束！";
        }
    };

    // 定义一个计算的方法
    var calc = function (num) {
        return num > 9 ? num : "0" + num;
    };


    /** 提交订单 */
    $scope.submitOrder = function(){
        // 判断用户是否登录
        if ($scope.loginName){ // 已登录
            baseService.sendGet("/order/submitOrder?id="
                + $scope.entity.id).then(function(response){
                if (response.data){
                    location.href = "/order/pay.html";
                }else {
                    alert("下单失败！");
                }
            });
        }else{ // 未登录
            // 跳转到单点登录系统
            location.href = "http://sso.pinyougou.com?service="
                + $scope.redirectUrl;
        }
    };

});