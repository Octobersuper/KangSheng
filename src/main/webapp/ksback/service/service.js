
layui.use(['layer','table','laydate'],function(){
    $ = layui.jquery;
    var layer = layui.layer ,table = layui.table,laydate = layui.laydate;
    //第一个实例
    table.render({
        elem: '#demo'
        ,height: "auto"
        ,method:"post"
        ,url:baseurl+'service/getService' //数据接口
        ,request: {
            pageName: 'pageNum' //页码的参数名称，默认：page
            ,limitName: 'pageSize' //每页数据量的参数名，默认：limit
        }
        ,page: false //开启分页
        ,cols: [[ //表头
             {field: 'qq', title: 'QQ号',align:'center',event:"edit0",width:150}
            ,{field: 'wx', title: '微信号',align:'center',event:"edit2",width:150}
        ]]
    });
    //监听工具条
    table.on('tool(test)', function(obj){
        var data = obj.data;
        var layEvent = obj.event;
        var tr = obj.tr; //获得当前行 tr 的DOM对象
       if(layEvent === 'edit0') { //编辑
           layer.prompt({
               formType: 2
               ,shadeClose:true
               ,title: '修改客服QQ号'
               ,value: data.qq
           }, function(value, index){
               layer.close(index);
               var uinfo = { "qq" : value }
               //这里一般是发送修改的Ajax请求
               $.ajax({
                   type: 'post',
                   url: baseurl+"service/update",
                   data: uinfo,
                   async:false,
                   dataType: 'json',
                   success: function(res){
                       if(res.meta.code==200){
                           //加载层
                           var index = layer.load(0, {shade: false,time:500} ); //0代表加载的风格，支持0-2
                           setTimeout(function(){ layer.msg(''+res.meta.msg+'',{icon:1,time:1000});index.closed}, 500);
                           setTimeout(function(){
                               obj.update({
                                   qq: value
                               });}, 1000);
                       }else{
                           //加载层
                           var index = layer.load(0, {shade: false,time:1000} ); //0代表加载的风格，支持0-2
                           setTimeout(function(){ layer.msg(''+res.meta.msg+'',{icon:2,time:1000});index.closed}, 1000);
                       }
                   }
               });
           });
        }else if(layEvent === 'edit1') { //编辑
           layer.prompt({
               formType: 2
               ,shadeClose:true
               ,title: '修改客服QQ群'
               ,value: data.qqService
           }, function(value, index){
               layer.close(index);
               var uinfo = { "qqService" : value }
               //这里一般是发送修改的Ajax请求
               $.ajax({
                   type: 'post',
                   url: baseurl+"service/update",
                   data: uinfo,
                   async:false,
                   dataType: 'json',
                   success: function(res){
                       if(res.meta.code==200){
                           //加载层
                           var index = layer.load(0, {shade: false,time:500} ); //0代表加载的风格，支持0-2
                           setTimeout(function(){ layer.msg(''+res.meta.msg+'',{icon:1,time:1000});index.closed}, 500);
                           setTimeout(function(){
                               obj.update({
                                   qqService: value
                               });}, 1000);
                       }else{
                           //加载层
                           var index = layer.load(0, {shade: false,time:1000} ); //0代表加载的风格，支持0-2
                           setTimeout(function(){ layer.msg(''+res.meta.msg+'',{icon:2,time:1000});index.closed}, 1000);
                       }
                   }
               });
           });
       }else if(layEvent === 'edit2') { //编辑
           layer.prompt({
               formType: 2
               ,shadeClose:true
               ,title: '修改客服微信号'
               ,value: data.wx
           }, function(value, index){
               layer.close(index);
               var uinfo = { "wx" : value }
               //这里一般是发送修改的Ajax请求
               $.ajax({
                   type: 'post',
                   url: baseurl+"service/update",
                   data: uinfo,
                   async:false,
                   dataType: 'json',
                   success: function(res){
                       if(res.meta.code==200){
                           //加载层
                           var index = layer.load(0, {shade: false,time:500} ); //0代表加载的风格，支持0-2
                           setTimeout(function(){ layer.msg(''+res.meta.msg+'',{icon:1,time:1000});index.closed}, 500);
                           setTimeout(function(){
                               obj.update({
                                   wx: value
                               });}, 1000);
                       }else{
                           //加载层
                           var index = layer.load(0, {shade: false,time:1000} ); //0代表加载的风格，支持0-2
                           setTimeout(function(){ layer.msg(''+res.meta.msg+'',{icon:2,time:1000});index.closed}, 1000);
                       }
                   }
               });
           });
       }
    });
})
