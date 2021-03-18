%%--------------------------------------------------------------------
%% Copyright (c) 2020 DGIOT Technologies Co., Ltd. All Rights Reserved.
%%
%% Licensed under the Apache License, Version 2.0 (the "License");
%% you may not use this file except in compliance with the License.
%% You may obtain a copy of the License at
%%
%%     http://www.apache.org/licenses/LICENSE-2.0
%%
%% Unless required by applicable law or agreed to in writing, software
%% distributed under the License is distributed on an "AS IS" BASIS,
%% WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
%% See the License for the specific language governing permissions and
%% limitations under the License.
%%--------------------------------------------------------------------

-define(DCACHE, shuwadiskcache).

-define(SHUWA_WORKERS, shuwa_workers).
-define(SHUWA_WORKER, shuwa_worker).
-define(DEFAULT_TASK, default_task).
-define(TAKS_DB, shuwa_base_task).

-define(CHILD(I, Type, Args), {I, {I, start_link, Args}, permanent, 5000, Type, [I]}).

-define(CHILD2(I, Mod, Type, Args), {I, {Mod, start_link, Args}, permanent, 5000, Type, [Mod]}).

-define(TASK_NAME(Name), shuwa_utils:to_atom(lists:concat([shuwa_utils:to_atom(Name), "task"]))).
