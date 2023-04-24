/** 定义订单控制器 */
app.controller("orderController",function ($scope, $controller,$interval,$location,baseService) {
   /** 指定继承cartController */
   $controller("cartController",{$scope:$scope});
   /** 根据登录用户获取地址 */
   $scope.findAddressByUser = function () {
       baseService.sendGet("/order/findAddressByUser")
           .then(function (response) {
               $scope.addressList = response.data;

               /**
                * 获取用户默认的地址，设置默认地址
                * 在查询的时候进行了降序排序
                * 所以第一个就是默认的地址
                *   */
               $scope.address = $scope.addressList[0];
           });
   };

   /** 选择地址 */
   $scope.selectAddress = function (item) {
       $scope.address = item;
   };

   /** 判断是否是当前选中的地址 */
   $scope.isSelectedAddress = function (item) {
     return item == $scope.address;
   };


   /** 新增收获地址 */
   $scope.saveAddress = function () {

       var url = "saveAddress";
       if ($scope.address.id){
           url = "updateAddress";
       }

       /** 发送post请求 */
       baseService.sendPost("/order/" + url, $scope.address)
           .then(function(response){
               if (response.data){
                   /** 重新加载数据 */
                   window.location.reload();
               }else{
                   alert("操作失败！");
               }
           });
   };

    /** 显示地址修改 */
    $scope.show = function(item){
        /** 把json对象转化成一个新的json对象 */
        $scope.address = JSON.parse(JSON.stringify(item));
    };



   /**  定义order对象封装参数 */
   $scope.order = {paymentType : '1'};
   /** 选择支付方式 */
   $scope.selectPayType = function (paytype) {
       $scope.order.paymentType = paytype;
   };


   /** 保存订单 */
   $scope.saveOrder = function () {
       // 设置收件人地址
       $scope.order.receiverAreaName = $scope.address.adress;
       // 设置收件人手机号码
       $scope.order.receiverMobile = $scope.address.mobile;
       // 设置收件人
       $scope.order.receiver = $scope.address.contact;
       // 发送异步请求
       baseService.sendPost("/order/save",$scope.order)
           .then(function (response) {
               if (response.data){
                   //如果是微信支付，跳转到扫码支付页面
                   if ($scope.order.paymentType == 1){
                       location.href = "/order/pay.html";
                   }else {
                       //如果是货到付款，跳转到成功页面
                       location.href = "/order/paysuccess.html";
                   }
               }else {
                   alert("订单提交失败！");
               }
       });
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

});