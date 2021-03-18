%%%-------------------------------------------------------------------
%%% @author kenneth
%%% @copyright (C) 2019, <COMPANY>
%%% @doc
%%% 创建的mnesia
%%% @end
%%% Created : 29. 七月 2019 18:09
%%%-------------------------------------------------------------------
-author("kenneth").


%% ====  全局缓存表 ========
-define(MCACHE, shuwamnesiacache).
-record(shuwa_mcache, {
    key,
    value
}).

%% ====  设备路由表 ========
-define(SHUWA_ROUTE, shuwa_route).
-record(shuwa_route, {
    key,      % [ProductId, DevAddr], [产品ID, 设备地址]
    status,   % 设备状态
    node      % 节点
}).

%% ====  设备表 ========
-define(SMART_DEV, mnesia_smartdev).
-record(shuwa_device, {
    key,      % [ProductId, DevAddr], [产品ID, 设备地址]
    basedata  % 设备基本数据,map类型
}).

-define(SMART_PROD, mnesia_smartprod).
-record(shuwa_prod, {
    key,      % [ProductId], [产品ID]
    product   % 产品基本数据,map类型
}).

-define(SMART_HUB, mnesia_smarthub).
-record(shuwa_hub, {
    key,      % [vcaddr,pn], [网关设备地址,子设备子网地址]
    subdev    % [ProductId,DevAddr], [产品ID, 设备地址]
}).

-define(SMART_GATEWAY, mnesia_smartgateway).
-record(shuwa_gateway, {
    key,    % [ProductId, DevAddr], [产品ID, 设备地址]
    gatway  % [DtuProductId, vcaddr,pn], [网关产品ID, 网关设备地址,子设备子网地址]
}).


-define(TABLES, [
    {?MCACHE, [
        {ram_copies, [node()]},
        {record_name, shuwa_mcache},
        {attributes, record_info(fields, shuwa_mcache)},
        {storage_properties, [{ets, [{read_concurrency, true}, {write_concurrency, true}]}]}
    ]},

    {?SHUWA_ROUTE, [
        {ram_copies, [node()]},
        {record_name, shuwa_route},
        {type, set},
        {index, [node]},
        {attributes, record_info(fields, shuwa_route)},
        {storage_properties, [{ets, [{read_concurrency, true}, {write_concurrency, true}]}]}
    ]},

    {?SMART_DEV, [
        {disc_copies, [node()]},
        {local_content, true},
        {type, set},
        {record_name, shuwa_device},
        {attributes, record_info(fields, shuwa_device)},
        {storage_properties, [{ets, [{read_concurrency, true}, {write_concurrency, true}]}]}
    ]},

    {?SMART_PROD, [
        {ram_copies, [node()]},
        {record_name, shuwa_prod},
        {type, set},
        {attributes, record_info(fields, shuwa_prod)},
        {storage_properties, [{ets, [{read_concurrency, true}, {write_concurrency, true}]}]}
    ]},

    {?SMART_HUB, [
        {ram_copies, [node()]},
        {record_name, shuwa_hub},
        {type, set},
        {attributes, record_info(fields, shuwa_hub)},
        {storage_properties, [{ets, [{read_concurrency, true}, {write_concurrency, true}]}]}
    ]},

    {?SMART_GATEWAY, [
        {ram_copies, [node()]},
        {record_name, shuwa_gateway},
        {type, set},
        {attributes, record_info(fields, shuwa_gateway)},
        {storage_properties, [{ets, [{read_concurrency, true}, {write_concurrency, true}]}]}
    ]}
]).
