/** 定义控制器层 */
app.controller('orderController', function($scope,$controller,$interval,$location,baseService){

    /** 指定继承baseController */
    $controller('indexController',{$scope:$scope});

    //显示用户信息
    $scope.showInfo = function () {
        baseService.sendPost("/user/showInfo")
            .then(function (response) {
                $scope.entity = response.data;
            });
    };


    /*//显示用户订单商品信息
    $scope.showOrders = function () {
        baseService.sendPost("/order/showOrders")
            .then(function (response) {
                //所有订单
                $scope.orderList = response.data;
                //创建数组存放每个订单下的商品
                $scope.arrayOrderItem = [];
                // 按顺序迭代发送异步请求,解决按顺序执行异步请求
                /!*
                * var promise = $q.when();
                * promise.then(function () {
                *       需要按顺序执行的异步请求
                * });
                * *!/
                var promise = $q.when();
                for (var i = 0; i < $scope.orderList.length; i++){
                    promise.then(function () {
                        baseService.sendPost("order/showOrdersItems?orderId="
                            + $scope.orderList[i].orderId).then(function (response) {
                            //追加商品到的商品数组
                            $scope.arrayOrderItem.push(response.data);
                        });
                    });
                }
            });
    };*/


    //跳转微信扫码支付页面
    $scope.OrderPay = function (order) {
        if (order.status == 1){
            baseService.sendGet("/order/findPayLogFromOrderId",
                "orderId=" + order.orderId).then(function (response) {
                if(response.data){
                    location.href = "http://user.pinyougou.com/order/pay.html";
                }else{
                    alert("订单处理失败");
                }
            });

        }
    };

    /** 生成微信支付二维码  */
    $scope.genPayCode = function () {
        baseService.sendGet("/order/genPayCode").then(function (response) {
            //获取响应数据 {outTradeNo : '',money : 10, codeUrl : ''}
            /** 获取金额（转化成元） */
            $scope.money = (response.data.totalFee /100).toFixed(2);
            /** 获取订单交易号 */
            $scope.outTradeNo = response.data.outTradeNo;
            // 获取微信支付的URL
            $scope.codeUrl = response.data.codeUrl;
            /** 生成二维码 */
            /*var qr = new QRious({
                element : document.getElementById('qrious'),
                size : 250,
                level : 'H',
                value : response.data.codeUrl
            });*/
            document.getElementById("ORcode").src = "/barcode?url=" + $scope.codeUrl;
            /**
             * 开启定时器
             * 第一个参数：调用的函数
             * 第二个参数：间隔的毫秒数 3秒
             * 第三个参数：调用的总次数 100 (5分钟)
             */
            var timer = $interval(function(){

                // 发送异步请求，检测支付状态
                baseService.sendGet("/order/queryPayStatus?outTradeNo="
                    + $scope.outTradeNo).then(function(response){
                    // 获取响应数据 {status : 1} 1:支付成功 2:未支付 3:支付失败
                    if (response.data.status == 1){
                        // 取消定时器
                        $interval.cancel(timer);
                        // 支付成功，需要跳转到支付成功的页面
                        location.href = "/order/paysuccess.html?money=" + $scope.money;
                    }
                    if (response.data.status == 3){
                        // 取消定时器
                        $interval.cancel(timer);
                        // 支付失败，需要跳转到支付失败的页面
                        location.href = "/order/payfail.html";
                    }
                });
            }, 3000, 100);

            /** 执行100次(3分钟)之后需要回调的函数 */
            timer.then(function () {
                // 关闭订单
                // 提示信息
                $scope.codeStr = "二维码已过期，刷新页面重新获取二维码。";
            });

        });
    };

    /** 获取支付金额 */
    $scope.getMoney = function () {
        return $location.search().money;
    };






    // 定义分页组件需要参数对象
    $scope.paginationConf = {
        currentPage : 1, // 当前页码
        perPageOptions : [10,15,20], // 页码下拉列表框
        totalItems : 0, // 总记录数
        itemsPerPage : 2, // 每页显示记录数
        onChange : function(){ // 当页码发生改变后需要调用的函数
            if ($scope.load){
                // 重新加载数据
                $scope.reload();
            }
            $scope.load = true;
        }
    };

    //定义是否查询标识符
    $scope.load = true;

    // 定义重新加载数据方法
    $scope.reload = function(){
        // 分页查询(带查询条件)
        $scope.search($scope.paginationConf.currentPage,//获取当前页码
            $scope.paginationConf.itemsPerPage);//获取页大小
        $scope.load = false;
    };

    /** 查询条件对象 */
    $scope.searchEntity = {};
    /** 分页查询(查询条件) */
    $scope.search = function(page, rows){
        baseService.findByPage("/order/findByPage", page,
            rows, $scope.searchEntity)
            .then(function(response){
                /** 获取分页查询结果 */
                $scope.dataList = response.data.rows;
                /** 更新分页总记录数 */
                $scope.paginationConf.totalItems = response.data.total;
                //创建数组存放每个订单下的商品
                $scope.arrayOrderItem = [];
                for (var i = 0; i < $scope.dataList.length; i++){
                    baseService.sendPost("order/showOrdersItems?orderId="
                        + $scope.dataList[i].orderId).then(function (response) {
                        //追加商品到的商品数组
                        $scope.arrayOrderItem.push(response.data);
                    });
                }
            });
    };



});