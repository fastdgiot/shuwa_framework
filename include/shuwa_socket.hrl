%%%-------------------------------------------------------------------
%%% @author kenneth
%%% @copyright (C) 2019, <COMPANY>
%%% @doc
%%%
%%% @end
%%% Created : 19. 九月 2019 20:22
%%%-------------------------------------------------------------------
-author("kenneth").

-record(tcp, {
    transport,
    socket,
    log = false, % 是否日志, log = file, log = {Mod, Fun}
    buff = <<>>,
    state % 自定义字段
}).
