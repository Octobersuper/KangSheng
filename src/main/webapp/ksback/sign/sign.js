layui.use(['form','layer','table','laydate'],function(){
    $ = layui.jquery;
    var layer = layui.layer ,table = layui.table,laydate = layui.laydate,form = layui.form;
    //第一个实例
    table.render({
        elem: '#demo'
        ,id:'tabuser'
        ,height: "auto"
        ,method:"get"
        ,url: baseurl+'gameSign' //数据接口
        ,page: false //开启分页
        ,cols: [[ //表头
            {field: 'signid', title: 'ID',align:'center',fixed: 'left',width:100,sort:true}
            ,{field: 'num', title: '连续天数',align:'center',width:100,sort:true}
            ,{field: 'value', title: '奖励个数',event: 'setMoney',width:100,align:'center',sort:true}
        ]]
    });
    //监听工具条
    table.on('tool(test)', function(obj){
        var data = obj.data;
        var layEvent = obj.event;
        var tr = obj.tr; //获得当前行 tr 的DOM对象
        if(layEvent === 'setMoney'){//修改金币
            layer.prompt({
                formType: 2
                ,shadeClose:true
                ,title: '修改第 ['+ data.num +'] 天的奖励数量'
                ,value: data.value
            }, function(value, index){
                layer.close(index);
                $.ajax({
                    type : "put",
                    url : baseurl + "gameSign",
                    data : {
                        signid: data.signid ,
                        value : value
                    },
                    success: function(res){
                        if(res.meta.code==200){
                            //加载层
                            var index = layer.load(0, {shade: false,time:500} ); //0代表加载的风格，支持0-2
                            setTimeout(function(){ layer.msg(''+res.meta.msg+'',{icon:1,time:1000});index.closed}, 500);
                            setTimeout(function(){
                                obj.update({
                                    value: value
                                });
                            }, 1000);
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


